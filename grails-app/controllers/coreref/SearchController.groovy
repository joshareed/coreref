package coreref

import grails.converters.JSON

class SearchController {
	def mongoService

	def all = {
		def collection = mongoService.getCollection(params.collection)
		if (collection) {
			def results = collection.find([:], QueryUtils.buildFilter(params)).collect() { SearchUtils.clean(it) }
			render "${results as JSON}"
		} else {
			response.sendError(404, "Invalid collection: '${params.collection}'")
		}
	}

	def type = {
		def collection = mongoService.getCollection(params.collection)
		if (collection) {
			def query = QueryUtils.withDepths(params, ['class': (params.query ?: '')])
			def results = collection.find(query, QueryUtils.buildFilter(params)).collect() { SearchUtils.clean(it) }
			render "${results as JSON}"
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
			def results = collection.find(query, QueryUtils.buildFilter(params)).collect() { SearchUtils.clean(it) }
			render "${results as JSON}"
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
			def results = collection.find(query, filter).collect() { SearchUtils.clean(it) }
			render "${results as JSON}"
		} else {
			response.sendError(404, "Invalid collection: '${params.collection}'")
		}
	}
}