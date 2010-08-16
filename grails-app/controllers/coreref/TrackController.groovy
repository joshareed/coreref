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
import org.andrill.util.image.ImageMagick

import org.codehaus.groovy.grails.commons.ApplicationHolder

/**
 * A controller for rendering the various viewer tracks as images.
 */
class TrackController extends SecureController {
	private static final int DEFAULT_SCALE = 2000
	static def cacheDir

	/**
	 * Renders the split core image track.
	 * Note: SECURE
	 */
	def split = {
		def scene = new DefaultScene()
		scene.setRenderHint('borders', 'false')
		scene.addTrack(Platform.getService(org.andrill.coretools.geology.ui.ImageTrack), null)
		renderScene(scene, ['class': 'Image', type: 'split'], true, Color.black, Color.black)
	}

	/**
	 * Renders the whole core image track.
	 * Note: SECURE
	 */
	def whole = {
		def scene = new DefaultScene()
		scene.setRenderHint('borders', 'false')
		scene.addTrack(Platform.getService(org.andrill.coretools.geology.ui.ImageTrack), null)
		renderScene(scene, ['class': 'Image', type: 'whole'], true, Color.black, Color.black)
	}

	/**
	 * Renders the xrd core image track.
	 * Note: SECURE
	 */
	def xrd = {
		def scene = new DefaultScene()
		scene.setRenderHint('borders', 'false')
		scene.addTrack(Platform.getService(org.andrill.coretools.geology.ui.ImageTrack), null)
		renderScene(scene, ['class': 'Image', type: 'xrd'], true, Color.black, Color.black)
	}

	/**
	 * Renders the lithology track.
	 * Note: SECURE
	 */
	def lith = {
		def scene = new DefaultScene()
		scene.setRenderHint('borders', 'false')
		scene.addTrack(Platform.getService(org.andrill.coretools.geology.ui.LithologyTrack), null)
		renderScene(scene, ['class': 'Interval'], false, Color.black, Color.white)
	}

	/**
	 * Renders the ruler track.
	 * Note: SECURE
	 */
	def ruler = {
		def scene = new DefaultScene()
		scene.setRenderHint('borders', 'false')
		scene.addTrack(Platform.getService(org.andrill.coretools.geology.ui.RulerTrack), null)
		renderScene(scene, ['class': 'Interval'], false, Color.black, Color.white)
	}

	// does the heavy lifting for rendering a scene
	def renderScene = { scene, query, usejpeg, foreground, background ->
		def rewriteFrom = ApplicationHolder.application.config?.coreref?.local?.url
		def rewriteTo = ApplicationHolder.application.config?.coreref?.local?.path
		
		withProject { project ->
			// get our depth range
			def top = params.top as double
			def base = params.base ? params.base as double : top + 1
			def scale = params.scale ? params.scale as int: DEFAULT_SCALE
			boolean horizontal = true

			// make sure we have a cache directory
			if (!cacheDir || !cacheDir.exists()) {
				def temp = File.createTempFile("cache", "")
				cacheDir = temp.parentFile
				temp.delete()
			}

			// try finding the cached file
			def jpeg = new File(cacheDir, "${params.project}-${params.action}-${top}-${base}-${scale}-${horizontal}.jpeg")
			def png = new File(cacheDir, "${params.project}-${params.action}-${top}-${base}-${scale}-${horizontal}.png")
			if (jpeg.exists())	{ return writeFile(jpeg) }
			if (png.exists())	{ return writeFile(png) }

			// if we got here, then we need to render the scene
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
					case 'Image':
						def url = doc.url
						if (rewriteFrom && rewriteTo) {
							url = url.replace(rewriteFrom, rewriteTo)
						}
						model = models.build("Image", [top: doc.top, base: doc.base, group: doc.type, path: url]) 
						break
					case 'Interval':
						model = models.build("Interval", [top: doc.top, base: doc.base, lithology: "org.psicat.resources.lithologies:${doc.lithology}"])
						break
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
			graphics.write(png)
			graphics.dispose()

			if (usejpeg) {
				try {
					// convert the png version to jpeg
					ImageMagick convert = new ImageMagick()
					if (convert.run(png, jpeg)) {
						png.delete()
						return writeFile(jpeg)
					}
				} catch (e) {
					e.printStackTrace()
				}
			}

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
}
