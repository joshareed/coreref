package coreref

import grails.converters.JSON

class ConfigController {
	def mongoService

	def viewer = {
		def collection = mongoService.getCollection('_configs')
		if (collection) {
			def config = collection.find(project: params.collection).find { true }
			if (config) {
				// cleanup config
				config.keySet().findAll { it.startsWith('_') }.each { config.remove(it) }
				config.root = createLinkTo(dir: '/')[0..-2]

				// render it
				if (params.callback) {
					render(contentType: 'application/json', text: "${params.callback}(${config as JSON})")
				} else {
					render(contentType: 'application/json', text: (config as JSON))
				}
			} else {
				sendError(404, "Invalid project: '${params.collection}'")
			}
		} else {
			sendError(500, "'_configs' collection not defined")
		}
	}
}
