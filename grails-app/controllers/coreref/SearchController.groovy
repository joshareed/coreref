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
		def filter = QueryUtils.buildFilter(params, [top: true, base: true])
		def query = QueryUtils.withDepths(params, ['class': 'Datum', 'type': ['$in': []]])

		def results = [:]
		def series = (params.query ?: '').split(',') as List
		series.each {
			query.type.'$in' << it
			results[it] = [:]
		}

		mongoService[params.project].findAll(query).sort([top: 1]).collect() { SearchUtils.clean(it) }.each { doc ->
			doc.keySet().findAll{ !(it in ['top', 'base', 'class', 'type', '_id', '_ns'])}.each { k ->
				if (!results[doc.type][k]) {
					results[doc.type][k] = [label: k[0].toUpperCase() + k[1..-1], data: [], yaxis: series.indexOf(doc.type) + 1]
				}
				results[doc.type][k].data << [doc.top, doc[k] ?: null]
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