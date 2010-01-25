package coreref

import java.awt.Color
import java.awt.geom.AffineTransform
import java.awt.geom.Rectangle2D

import org.andrill.coretools.Platform
import org.andrill.coretools.graphics.RasterGraphics
import org.andrill.coretools.model.ModelManager
import org.andrill.coretools.model.DefaultContainer
import org.andrill.coretools.scene.DefaultScene
import org.andrill.coretools.geology.ui.*

class TrackController {
	private static final int DEFAULT_SCALE = 2000
	def mongoService
	def cacheDir

	def split = {
		def scene = new DefaultScene()
		scene.setRenderHint('borders', 'false')
		scene.addTrack(Platform.getService(org.andrill.coretools.geology.ui.ImageTrack), null)

		def track = renderScene(params, ['class': 'Image', type: 'split'], scene, Color.black, Color.black)
		addHeaders(response, 'image/png')
		track.withInputStream { response.outputStream << it }
	}

	def whole = {
		def scene = new DefaultScene()
		scene.setRenderHint('borders', 'false')
		scene.addTrack(Platform.getService(org.andrill.coretools.geology.ui.ImageTrack), null)

		def track = renderScene(params, ['class': 'Image', type: 'whole'], scene, Color.black, Color.black)
		addHeaders(response, 'image/png')
		track.withInputStream { response.outputStream << it }
	}

	def lith = {
		def scene = new DefaultScene()
		scene.setRenderHint('borders', 'false')
		scene.addTrack(Platform.getService(org.andrill.coretools.geology.ui.LithologyTrack), null)

		def track = renderScene(params, ['class': 'Interval'], scene)
		addHeaders(response, 'image/png')
		track.withInputStream { response.outputStream << it }
	}

	def ruler = {
		def scene = new DefaultScene()
		scene.setRenderHint('borders', 'false')
		scene.addTrack(Platform.getService(org.andrill.coretools.geology.ui.RulerTrack), null)

		def track = renderScene(params, ['class': 'Interval'], scene)
		addHeaders(response, 'image/png')
		track.withInputStream { response.outputStream << it }
	}

	private def addHeaders(response, contentType) {
		response.contentType = 'image/png'
		def nowPlusHour = new Date().time + 3600000
		response.addHeader("Last-Modified", String.format('%ta, %<te %<tb %<tY %<tH:%<tM:%<tS %<tZ', new Date()))
		response.addHeader("Expires", String.format('%ta, %<te %<tb %<tY %<tH:%<tM:%<tS %<tZ', new Date(nowPlusHour)))
		response.addHeader("Cache-Control", "max-age=3600000, must-revalidate")
	}

	private def renderScene(params, query, scene, foreground = Color.black, background = Color.white) {
		// get our depth range
		def top = params.top as double
		def base = params.base ? params.base as double : top + 1
		def scale = params.scale ? params.scale as int: DEFAULT_SCALE
		boolean horizontal = true

		// check the local cache
		if (!cacheDir) {
			cacheDir = File.createTempFile("cache", "").parentFile
		}
		def cached = new File(cacheDir, "${params.action}-${top}-${base}-${scale}-${horizontal}.png")
		if (cached.exists()) {
			if (new Date().time - cached.lastModified() > 24*60*60*1000) {
				// if older than a day, re-generate
				cached.delete()
			} else {
				return cached
			}
		}

		// get the container
		def container = new DefaultContainer()

		// get the collection to query
		def collection = mongoService[params.project]

		// build the query
		query.putAll(top: ['$lte': base], base: ['$gte': top])

		// get our results and convert them into models
		ModelManager models = Platform.getService(ModelManager.class)
		collection.findAll(query).each { doc ->
			def model
			switch(doc['class']) {
				case 'Image':		model = models.build("Image", [top: doc.top, base: doc.base, group: doc.type, path: doc['_local'] ?: doc.url]); break
				case 'Interval':	model = models.build("Interval", [top: doc.top, base: doc.base, lithology: "org.psicat.resources.lithologies:${doc.lithology}"]); break
			}
			if (model) { container.add(model) }
		}

		// build our scene
		scene.models = container
		scene.scalingFactor = scale
		scene.validate()

		// render our scene
		int width, height
		if (horizontal) {
			width = (int) ((base - top) * scale)
			height = (int) scene.contentSize.width
		} else {
			height = (int) ((base - top) * scale)
			width = (int) scene.contentSize.width
		}

		RasterGraphics graphics = new RasterGraphics(width, height, true, background)
		graphics.setLineColor(foreground)
		if (horizontal) {
			AffineTransform tx = AffineTransform.getRotateInstance(-Math.PI / 2)
			tx.translate(-scene.contentSize.width, 0)
			graphics.pushTransform(tx)
		}
		scene.renderContents(graphics, new Rectangle2D.Double(0, top * scale, scene.contentSize.width, (base - top) * scale))
		graphics.write(cached)
		graphics.dispose()
		return cached
	}
}
