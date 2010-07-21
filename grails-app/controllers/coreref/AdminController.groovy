package coreref

import java.awt.Color
import java.awt.geom.AffineTransform
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

import org.apache.lucene.store.*
import org.apache.lucene.search.spell.*

import org.andrill.coretools.Platform
import org.andrill.coretools.graphics.RasterGraphics
import org.andrill.coretools.model.ModelManager
import org.andrill.coretools.model.DefaultContainer
import org.andrill.coretools.scene.DefaultScene
import org.andrill.coretools.geology.ui.*
import org.andrill.util.image.ImageMagick

class AdminController extends SecureController {
	static def cacheDir

	def index = {
		withProject { project ->
			return [
				project: project,
				issues: mongoService['_issues'].findAll(pending: true, project: params.project)
			]
		}
	}

	def updateKeywords = {
		withProject { project ->
			long start = System.currentTimeMillis()
			def dictionary = [] as Set
			def collection = mongoService[project.id]
			collection.find().each { doc ->
				def tokens = []
				['name', 'core', 'section', 'code', 'text'].each { field ->
					if (doc.containsKey(field)) {
						SearchUtils.tokenize(doc[field] as String, tokens)
					}
				}
				if (tokens) {
					collection.update(doc, ['$set': ['_keywords': tokens]])
					dictionary.addAll(tokens)
				}
			}
			if (dictionary) {
				SpellChecker sp = new SpellChecker(FSDirectory.open(new File(servletContext.getRealPath('/lucene/' + project.id))))
				sp.clearIndex()
				sp.indexDictionary([ getWordsIterator: { -> return dictionary.iterator() } ] as Dictionary)
				sp.close()
			}
			render "Updated keyword index of ${params.project} in ${System.currentTimeMillis() - start} ms"
		}
	}

	def updateCoverage = {
		withProject { project ->
			def ranges = []
			def r
			mongoService[project.id].findAll('class': (params.opt ?: 'Image')).sort(top: 1).each { i ->
				if (!r || Math.floor(i.top) > r[1]) {
					r = [Math.floor(i.top), Math.ceil(i.base)]
					ranges << r
				} else {
					r[1] = Math.max(r[1], Math.ceil(i.base))
				}
			}
			render ranges
		}
	}

	def updateHoleView = {
		int COLUMN_WIDTH = 15
		int COLUMN_HEIGHT = 500
		double SCALING_FACTOR = 500
		withProject { project ->
			// make sure we have a cache directory
			if (!cacheDir || !cacheDir.exists()) {
				def temp = File.createTempFile("cache", "")
				cacheDir = temp.parentFile
				temp.delete()
			}

			// check the cache
			def png = new File(cacheDir, "${params.project}-overview.png")
			if (png.exists()) { return writeFile(png) }

			// query our images and figure out our depth range
			def images = mongoService[project.id].findAll('class': 'Image', 'type': 'split').sort(top: 1).collect { it }
			int base = (int) (Math.ceil(images.last().base / 10) * 10)

			// build our models
			def container = new DefaultContainer()
			ModelManager models = Platform.getService(ModelManager.class)
			images.each { doc ->
				def model = models.build("Image", [top: doc.top, base: doc.base, group: doc.type, path: doc.url.replace('r0', 'r2')]);
				if (model) { container.add(model) }
			}

			// build our scene
			def scene = new DefaultScene()
			scene.setRenderHint('borders', 'false')
			scene.addTrack(Platform.getService(org.andrill.coretools.geology.ui.ImageTrack), null)
			scene.models = container
			scene.scalingFactor = SCALING_FACTOR
			scene.validate()

			// our overview image
			def overview = new BufferedImage((int) (COLUMN_WIDTH * Math.floor(base / 10)), COLUMN_HEIGHT, BufferedImage.TYPE_INT_ARGB)

			// render each meter
			for (int i = 0; i < base; i++) {
				int row = (int) Math.floor(i % 10)
				int col = (int) Math.floor(i / 10)
				RasterGraphics graphics = new RasterGraphics((int) scene.contentSize.width, (int) Math.ceil(SCALING_FACTOR), true, Color.blue)
				scene.renderContents(graphics, new Rectangle2D.Double(0, i * SCALING_FACTOR, scene.contentSize.width, SCALING_FACTOR))
				def image = graphics.image
				int steps = COLUMN_HEIGHT / 10
				int perStep = 10 * SCALING_FACTOR / COLUMN_HEIGHT
				for (int p = 0; p < steps; p++) {
					for (int j = 0; j < COLUMN_WIDTH - 1; j++) {
						int px = image.getRGB((int) (image.width * ((double) j / (double) COLUMN_WIDTH)), p*perStep)
						overview.setRGB(col * COLUMN_WIDTH + j, (row * steps) + p, px)
					}
					overview.setRGB(col * COLUMN_WIDTH + COLUMN_WIDTH - 1, (row * steps) + p, (int) 0xFFFFFFFF)
				}
				graphics.dispose()
			}

			// write
			ImageIO.write(overview, 'png', png)
			return writeFile(png)
		}
	}

	// caches the rendered image for later re-use
	def writeFile = { file ->
		// set up our content type
		if (file.name.endsWith('.jpeg')) {
			response.contentType = 'image/jpeg'
		} else if (file.name.endsWith('.png')) {
			response.contentType = 'image/png'
		} else {
			println "Unknown content type for ${file.name}"
		}

		// setup some headers
		def nowPlusDay = new Date().time + 86400000
		response.addHeader("Last-Modified", String.format('%ta, %<te %<tb %<tY %<tH:%<tM:%<tS %<tZ', new Date(file.lastModified())))
		response.addHeader("Expires", String.format('%ta, %<te %<tb %<tY %<tH:%<tM:%<tS %<tZ', new Date(nowPlusDay)))
		response.addHeader("Cache-Control", "max-age=86400000, must-revalidate")

		// write the file
		try {
			file.withInputStream { response.outputStream << it }
		} catch (e) {
			// ignore connection reset errors
		}
	}

	def updateSeries = {
		long start = System.currentTimeMillis()
		def collection = mongoService[params.project]

		// calculate count, total, and mean
		def stats = [:]
		collection.findAll('class': 'Datum').each { doc ->
			if (!stats[doc.type]) stats[doc.type] = [max: Double.MIN_VALUE, min: Double.MAX_VALUE, count: 0, total: 0]
			def local = stats[doc.type]
			doc.findAll { !(it.key in ['_id', '_ns', 'class', 'type', 'top', 'base']) }.each { k,v ->
				if (v) {
					local.max = Math.max(local.max, v)
					local.min = Math.min(local.min, v)
					local.count++
					local.total += v
				}
			}
		}
		stats.each { k,v -> v.mean = v.total / v.count }

		// calculate standard deviation
		collection.findAll('class': 'Datum').each { doc ->
			def local = stats[doc.type]
			if (local.devtot == null) local.devtot = 0
			doc.findAll { !(it.key in ['_id', '_ns', 'class', 'type', 'top', 'base']) }.each { k,v ->
				if (v) {
					local.devtot += (v - local.mean) * (v - local.mean)
				}
			}
		}
		stats.each { k,v ->
			v.stddev = Math.sqrt(v.devtot / (v.count - 1))
			v.remove('total')
			v.remove('devtot')
		}

		// update our config for the project
		def config = mongoService['_configs']
		def doc = config.find(['id': params.project])
		if (doc) {
			config.update(doc, ['$set': ['stats': stats]])
		}

		render "Updated data series stats of ${params.project} in ${System.currentTimeMillis() - start}ms"
	}

	def issues = {
		return [issues: mongoService['_issues'].findAll(pending: true)]
	}

	def closeIssue = {
		def issues = mongoService['_issues']
		def doc = issues.find('_id': mongoService.idFor(params.opt))
		if (doc) {
			issues.update(doc, ['$set': ['pending': false]])
			render "Closed issue ${params.opt}"
		} else {
			render "No issue for ${params.opt}"
		}
	}
}
