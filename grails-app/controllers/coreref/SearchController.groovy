package coreref

import grails.converters.JSON

class SearchController {
	def mongoService

	def type = {
		def collection = mongoService.getCollection(params.collection)
		if (collection) {
			def query = QueryUtils.withDepths(params, ['class': (params.query ?: '')])
			def results = collection.find(query, QueryUtils.buildFilter(params)).sort([top: 1]).collect() { SearchUtils.clean(it) }
			if (params.callback) {
				render(contentType: 'application/json', text: "${params.callback}(${results as JSON})")
			} else {
				render(contentType: 'application/json', text: (results as JSON))
			}
		} else {
			response.sendError(404, "Invalid collection: '${params.collection}'")
		}
	}

	def text = {
		def collection = mongoService.getCollection(params.collection)
		if (collection) {
			def q = params.query ?: ''
			def query = QueryUtils.withDepths(params)
			if (q.contains('|')) {
				query['_keywords'] = ['$in': SearchUtils.tokenize(q, [])]
			} else {
				query['_keywords'] = ['$all': SearchUtils.tokenize(q, [])]
			}
			def results = collection.find(query, QueryUtils.buildFilter(params)).sort([top: 1]).collect() { SearchUtils.clean(it) }
			if (params.callback) {
				render(contentType: 'application/json', text: "${params.callback}(${results as JSON})")
			} else {
				render(contentType: 'application/json', text: (results as JSON))
			}
		} else {
			response.sendError(404, "Invalid collection: '${params.collection}'")
		}
	}

	def data = {
		def collection = mongoService.getCollection(params.collection)
		if (collection) {
			def filter = [top: true, base: true]
			def query = QueryUtils.withDepths(params, ['class': 'Datum'])
			(params.query ?: '').split(',').each { query[it] = ['$exists': true]; filter[it] = true }
			def results = [:]
			collection.find(query, filter).sort([top: 1]).collect() { SearchUtils.clean(it) }.each { doc ->
				doc.keySet().findAll{ it != 'top' && it != 'base' && it[0] != '_' }.each { k ->
					if (!results[k]) results[k] = [label: k[0].toUpperCase() + k[1..-1], data: []]
					results[k].data << [doc.top, doc[k]]
				}
			}
			if (params.callback) {
				render(contentType: 'application/json', text: "${params.callback}(${results as JSON})")
			} else {
				render(contentType: 'application/json', text: (results as JSON))
			}
		} else {
			response.sendError(404, "Invalid collection: '${params.collection}'")
		}
	}
}