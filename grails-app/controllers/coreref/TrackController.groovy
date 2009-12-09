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

	def split = {
		// build our scene
		def scene = new DefaultScene()
		scene.setRenderHint('borders', 'false')
		scene.addTrack(Platform.getService(org.andrill.coretools.geology.ui.ImageTrack), null)

		// output our graphics
		def graphics = renderScene(params, [_class: 'Image', type: 'split'], scene)
		response.contentType = 'image/png'
		graphics.write(response.outputStream, 'png')
		graphics.dispose()
	}

	def whole = {
		// build our scene
		def scene = new DefaultScene()
		scene.setRenderHint('borders', 'false')
		scene.addTrack(Platform.getService(org.andrill.coretools.geology.ui.ImageTrack), null)

		// output our graphics
		def graphics = renderScene(params, [_class: 'Image', type: 'whole'], scene)
		response.contentType = 'image/png'
		graphics.write(response.outputStream, 'png')
		graphics.dispose()
	}

	def lith = {
		// build our scene
		def scene = new DefaultScene()
		scene.setRenderHint('borders', 'false')
		scene.addTrack(Platform.getService(org.andrill.coretools.geology.ui.LithologyTrack), null)

		// output our graphics
		def graphics = renderScene(params, [_class: 'Interval'], scene)
		response.contentType = 'image/png'
		graphics.write(response.outputStream, 'png')
		graphics.dispose()
	}

	def ruler = {
		// build our scene
		def scene = new DefaultScene()
		scene.setRenderHint('borders', 'false')
		scene.addTrack(Platform.getService(org.andrill.coretools.geology.ui.RulerTrack), null)

		// output our graphics
		def graphics = renderScene(params, [_class: 'Interval'], scene, Color.white)
		response.contentType = 'image/png'
		graphics.write(response.outputStream, 'png')
		graphics.dispose()
	}

	private def renderScene(params, query, scene, foreground = Color.black, background = Color.black) {
		// get our depth range
		def top = params.top as double
		def base = params.base ? params.base as double : top + 1
		def scale = params.scale ? params.scale as int: DEFAULT_SCALE
		boolean horizontal = true

		// get the container
		def container = new DefaultContainer()

		// get the collection to query
		def collection = mongoService.getCollection(params.collection)
		if (!collection) {
			return container
		}

		// build the query
		query.putAll(top: ['$lte': base], base: ['$gte': top])

		// get our results and convert them into models
		ModelManager models = Platform.getService(ModelManager.class)
		collection.find(query).each { doc ->
			def model
			switch(doc['_class']) {
				case 'Image':		model = models.build("Image", [top: doc.top, base: doc.base, group: doc.type, path: doc.url]); break
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
		return graphics
	}
}
