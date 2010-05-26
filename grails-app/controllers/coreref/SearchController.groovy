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
			def q = (params.query ?: '').replaceAll(' or ', '|').replaceAll(' and ', ' ')
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
		withFormat {
			js {
				if (params.callback) {
					render(contentType: 'application/json', text: "${params.callback}(${results as JSON})")
				} else {
					render(contentType: 'application/json', text: (results as JSON))
				}
			}
			csv {
				render(contentType: 'text/csv', text: asCSV(results))
			}
		}
	}
	
	private def asCSV(results) {
		// get our set of fields
		def fields = ['top', 'base', 'class'] as LinkedHashSet
		results.each { doc -> doc.each { k,v -> fields << k } }
		
		// generate the CSV
		def buffer = new StringBuilder()
		fields.eachWithIndex { f, i ->
			buffer.append('"' + f + '"')
			if (i < fields.size() - 1)  buffer.append(',')
		}
		buffer.append("\n")
		results.each { doc ->
			fields.eachWithIndex { key, i ->
				def value = doc[key]
				if (value) {
					if (value instanceof Number) {
						buffer.append(value)
					} else {
						buffer.append('"' + value.replaceAll('"', '""') + '"')
					}
				}
				if (i < fields.size() - 1)  buffer.append(',')
			}
			buffer.append("\n")
		}
		buffer
	}

	// performs a query and renders the results
	def renderQuery = { query, filter = [:], sort = [top: 1] ->
		renderResults(mongoService[params.project].findAll(
			QueryUtils.withDepths(params, query),
			QueryUtils.buildFilter(params, filter)).sort(sort).collect() { SearchUtils.clean(it) })
	}
}