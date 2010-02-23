package coreref

class AdminController {
	def mongoService

	def updateKeywords = {
		long start = System.currentTimeMillis()
		def collection = mongoService[params.project]
		collection.find().each { doc ->
			def tokens = []
			['name', 'code', 'text'].each { field ->
				if (doc.containsKey(field)) {
					SearchUtils.tokenize(doc[field] as String, tokens)
				}
			}
			if (tokens) {
				collection.update(doc, ['$set': ['_keywords': tokens]])
			}
		}
		render "Updated keyword index of ${params.project} in ${System.currentTimeMillis() - start} ms"
	}

	def updateSeries = {
		long start = System.currentTimeMillis()
		def collection = mongoService[params.project]

		// use map/reduce to determine the max/min values for each series
		def mapreduce = collection.mapReduce(SERIES_MAP, SERIES_REDUCE, null, ['class': 'Datum'] as com.mongodb.BasicDBObject)
		def results = mapreduce.results().inject([:]) { map, doc ->
			map[doc['_id']] = doc['value']
			return map
		}

		// update our config for the project
		def config = mongoService['_configs']
		def doc = config.find(['id': params.project])
		if (doc) {
			config.update(doc, ['$set': ['data': results]])
		}

		// clean up
		mapreduce.drop()

		render "Updated data series index of ${params.project} in ${System.currentTimeMillis() - start}ms"
	}

	def issues = {
		return [issues: mongoService['_issues'].findAll(pending: true, project: params.project)]
	}

	def closeIssue = {
		def issues = mongoService['_issues']
		def doc = issues.find('_id': mongoService.idFor(params.opt))
		if (doc) {
			issues.update(doc, ['$set': ['pending': false]])
			render "Closed issue ${params.opt}"
		} else {
			render "No issue for ${params.opt}"
		}
	}

	private static final SERIES_MAP = """
		function() {
			for (var k in this) {
				if (k != '_id' && k != 'class' && k != 'type') {
					emit(k, { max: this[k], min: this[k] });
				}
			}
		}
"""

	private static final SERIES_REDUCE = """
		function(key, values) {
			var result = { max: -9999, min: 9999 };
			for (var i = 0; i < values.length; i++ ) {
				if (values[i].max > result.max && values[i].max != '') {
					result.max = values[i].max;
				}
				if (values[i].min < result.min && values[i].min != '') {
					result.min = values[i].min;
				}
			}
			return result;
		}
"""
}
