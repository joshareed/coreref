package coreref

import grails.converters.JSON

class RecentController {
	def mongoService

	def searches = {
		def query = [:]
		if (params?.project) { query.project = params.project }
		def results = mongoService['_searches'].findAll(query).sort('$natural': -1).limit(10).collect { SearchUtils.clean(it) }.unique { it.query + ":" + it.project }
		if (params.callback) {
			render(contentType: 'application/json', text: "${params.callback}(${results as JSON})")
		} else {
			render(contentType: 'application/json', text: (results as JSON))
		}
	}
}
