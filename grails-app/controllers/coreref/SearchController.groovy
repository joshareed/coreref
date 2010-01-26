package coreref

import grails.converters.JSON

class SearchController {
	def mongoService

	def type = {
		renderQuery(['class': (params.query ?: '')])
	}

	def text = {
		def query = [:]
		def q = params.query ?: ''
		if (q.contains('|')) {
			query['_keywords'] = ['$in': SearchUtils.tokenize(q, [])]
		} else {
			query['_keywords'] = ['$all': SearchUtils.tokenize(q, [])]
		}
		renderQuery(query)
	}

	def data = {
		def filter = [top: true, base: true]
		def query = ['class': 'Datum']
		(params.query ?: '').split(',').each { query[it] = ['$exists': true]; filter[it] = true }
		def results = [:]
		mongoService[params.project].findAll(query, filter).sort([top: 1]).collect() { SearchUtils.clean(it) }.each { doc ->
			doc.keySet().findAll{ it != 'top' && it != 'base' && it[0] != '_' }.each { k ->
				if (!results[k]) results[k] = [label: k[0].toUpperCase() + k[1..-1], data: []]
				results[k].data << [doc.top, doc[k]]
			}
		}
		renderResults(results)
	}

	def renderResults = { results ->
		if (params.callback) {
			render(contentType: 'application/json', text: "${params.callback}(${results as JSON})")
		} else {
			render(contentType: 'application/json', text: (results as JSON))
		}
	}

	def renderQuery = { query, filter = [:], sort = [top: 1] ->
		renderResults(mongoService[params.project].findAll(
			QueryUtils.withDepths(params, query),
			QueryUtils.buildFilter(params, filter)).sort(sort).collect() { SearchUtils.clean(it) })
	}
}