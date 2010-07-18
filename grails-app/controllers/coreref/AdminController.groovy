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
		withProject { project ->
			// query our images and figure out our depth range
			def images = mongoService[project.id].findAll('class': 'Image').sort(top: 1).collect { it }
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
			scene.scalingFactor = 1000
			scene.validate()
			
			// our overview image
			def overview = new BufferedImage((int) (15 * Math.floor(base / 10)), (int) 500, BufferedImage.TYPE_INT_ARGB)
			
			// render each meter
			for (int i = 0; i < base; i++) {
				int row = (int) Math.floor(i % 10)
				int col = (int) Math.floor(i / 10)
				RasterGraphics graphics = new RasterGraphics((int) scene.contentSize.width, 1000, true, Color.blue)
				scene.renderContents(graphics, new Rectangle2D.Double(0, i * 1000, scene.contentSize.width, 1000))
				def image = graphics.image
				int w = image.width / 2
				for (int p = 0; p < 50; p++) {
					int px = image.getRGB(w, p*20)
					for (int j = 0; j < 13; j++) {
						overview.setRGB(col * 15 + j, (row * 50) + p, px)
					}
					overview.setRGB(col * 15 + 13, (row * 50) + p, (int) (col % 5 == 4 ? 0xFF00FF00 : 0xFFFFFF00))
					overview.setRGB(col * 15 + 14, (row * 50) + p, (int) (col % 5 == 4 ? 0xFF00FF00 : 0xFFFFFF00))
				}
				graphics.dispose()
			}
			
			// write out
			response.contentType = 'image/jpeg'
			ImageIO.write(overview, 'jpeg', response.outputStream)
			response.outputStream.close()
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
