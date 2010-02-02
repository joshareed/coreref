package coreref

import grails.converters.JSON

class ConfigController {
	def mongoService

	def viewer = {
		// get our configuration
		def config = mongoService['_configs']?.findById(params.project)
		if (config) {
			// cleanup config
			config.keySet().findAll { it.startsWith('_') }.each { config.remove(it) }
			config.root = createLinkTo(dir: '/')
			if (config.root.endsWith('/')) {
				config.root = config.root.substring(0, config.root.length() - 1)
			}

			// render it
			if (params.callback) {
				render(contentType: 'application/json', text: "${params.callback}(${config as JSON})")
			} else {
				render(contentType: 'application/json', text: (config as JSON))
			}
		} else {
			sendError(500, "Invalid configuration: '${params.project}'")
		}
	}
}
