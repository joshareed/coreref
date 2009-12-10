package coreref

import grails.converters.JSON

class SearchController {
	def mongoService

	def type = {
		def collection = mongoService.getCollection(params.collection)
		if (collection) {
			def query = QueryUtils.withDepths(params, ['_class': params.query])
			def results = collection.find(query, QueryUtils.buildFilter(params)).collect() { it }
			render "${results as JSON}"
		}
	}

	def text = {
		def collection = mongoService.getCollection(params.collection)
		if (collection) {
			def query = QueryUtils.withDepths(params)
			if (params.query.contains('|')) {
				query['_keywords'] = ['$in': SearchUtils.tokenize(params.query, [])]
			} else {
				query['_keywords'] = ['$all': SearchUtils.tokenize(params.query, [])]
			}
			def results = collection.find(query, QueryUtils.buildFilter(params)).collect() { it }
			render "${results as JSON}"
		}
	}
}
