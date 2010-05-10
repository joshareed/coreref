package coreref

import grails.converters.JSON

/**
 * A controller for performing various types of searches within a project.
 */
class SearchController extends SecureController {
	def mongoService

	/**
	 * Searches by document class/type.
	 * Note: SECURE
	 */
	def type = {
		withProject {
			renderQuery(['class': (params.query ?: '')])	
		}
	}

	/**
	 * Searches for a key word or phrase.
	 * Note: SECURE
	 */
	def text = {
		withProject {
			def query = [:]
			def q = params.query ?: ''
			if (q.contains('|')) {
				query['_keywords'] = ['$in': SearchUtils.tokenize(q, [])]
			} else {
				query['_keywords'] = ['$all': SearchUtils.tokenize(q, [])]
			}
			renderQuery(query)
		}
	}

	/**
	 * Searches for data by type and depth range.
	 * Note: SECURE
	 */
	def data = {
		withProject {
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

			results.each { k, v ->
				if (!v) {
					v[k] = [label: k, data: []]
				}
			}
			renderResults(results)
			
		}
	}

	// renders the results as JSON or JSONP
	def renderResults = { results ->
		if (params.callback) {
			render(contentType: 'application/json', text: "${params.callback}(${results as JSON})")
		} else {
			render(contentType: 'application/json', text: (results as JSON))
		}
	}

	// performs a query and renders the results
	def renderQuery = { query, filter = [:], sort = [top: 1] ->
		renderResults(mongoService[params.project].findAll(
			QueryUtils.withDepths(params, query),
			QueryUtils.buildFilter(params, filter)).sort(sort).collect() { SearchUtils.clean(it) })
	}
}