package coreref

import grails.converters.JSON

class ConfigController extends SecureController {
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
			response.sendError(500, "Invalid configuration: '${params.project}'")
		}
	}
	
	def canvas = {
		withProject { project ->
			def config = mongoService['_configs']?.findById(params.project)
			def json = [
				id: project.id,
				name: project.name,
				images: [items:[]],
				intervals: [
					scheme: (config.scheme ?: createLinkTo(dir:'schemes/lith', file: 'andrill.json')),
					items:[]
				]
			]

			// add our images and intervals
			mongoService[project.id].findAll('class': 'Image').each { json.images.items << SearchUtils.clean(it) }
			mongoService[project.id].findAll('class': 'Interval').each { json.intervals.items << SearchUtils.clean(it) }

			if (params.callback) {
				render(contentType: 'application/json', text: "${params.callback}(${json as JSON})")
			} else {
				render(contentType: 'application/json', text: (json as JSON))
			}
		}
	}
}
