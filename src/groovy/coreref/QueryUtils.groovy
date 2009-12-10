package coreref

class QueryUtils {
	
	static Map withDepths(params, query = [:]) {
		if (params.top && params.base) {
			query.putAll(top: ['$lte': params.base as double], base: ['$gte': params.top as double])
		} else if (params.top) {
			query.putAll(top: ['$gte': params.top as double])
		} else if (params.base) {
			query.putAll(base: ['$lte': params.base as double])
		}
		return query
	}
	
	static Map buildFilter(params, filter = [:]) {
		if (params.filter) {
			params.filter.split(',').each { filter[it] = true }
		}
		return filter
	}
}