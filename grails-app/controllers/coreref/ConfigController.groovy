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

				// set parameters
				config.top = params.top as BigDecimal
				config.base = params.base ? params.base as BigDecimal : config.top + 1
				config.scale = 2000
				config.root = createLinkTo(dir: '/')[0..-2]

				// populate templates
				if (config?.url) { config.url = functionize(config.url, config) }
				if (config?.descriptions?.url) { config.descriptions.url = functionize(config.descriptions.url, config) }
				config?.tracks?.each { k, v ->
					if (v.url) { v.url = functionize(v.url, config) }
				}

				// clean up
				config.remove('out')

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

	private functionize(path, params) {
		return new groovy.text.SimpleTemplateEngine().createTemplate(path).make(params).toString()
	}
}
