package coreref

import java.awt.Color
import java.awt.GradientPaint
import java.awt.geom.AffineTransform
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

class ResourcesController {

	// generate a properly sized overlay image
	def overlay = {
		// generate an overlay
		def height = params.opt as double
		BufferedImage overlay = new BufferedImage(5, height as int, BufferedImage.TYPE_INT_ARGB)
		def g2d = overlay.createGraphics()
		g2d.paint = new GradientPaint(0f, 0f, new Color(0, 0, 0, 210), 0f, (height / 2) as float, new Color(0, 0, 0, 0), true)
		g2d.fill(new Rectangle2D.Double(0, 0, 5, height as int))
		g2d.dispose()

		// setup caching headers
		response.contentType = 'image/png'
		def nowPlusDay = new Date().time + 86400000
		response.addHeader("Last-Modified", String.format('%ta, %<te %<tb %<tY %<tH:%<tM:%<tS %<tZ', new Date()))
		response.addHeader("Expires", String.format('%ta, %<te %<tb %<tY %<tH:%<tM:%<tS %<tZ', new Date(nowPlusDay)))
		response.addHeader("Cache-Control", "max-age=86400000, must-revalidate")

		// write it out
		ImageIO.write(overlay, 'png', response.outputStream)
	}
}
