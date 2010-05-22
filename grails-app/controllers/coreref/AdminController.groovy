package coreref

import org.apache.lucene.store.*
import org.apache.lucene.search.spell.*

class AdminController extends SecureController {
	def index = {
		withProject { project ->
			return [
				project: project,
				issues: mongoService['_issues'].findAll(pending: true, project: params.project)
			]
		}
	}

	def updateKeywords = {
		withProject { project ->
			long start = System.currentTimeMillis()
			def dictionary = [] as Set
			def collection = mongoService[project.id]
			collection.find().each { doc ->
				def tokens = []
				['name', 'core', 'section', 'code', 'text'].each { field ->
					if (doc.containsKey(field)) {
						SearchUtils.tokenize(doc[field] as String, tokens)
					}
				}
				if (tokens) {
					collection.update(doc, ['$set': ['_keywords': tokens]])
					dictionary.addAll(tokens)
				}
			}
			if (dictionary) {
				SpellChecker sp = new SpellChecker(FSDirectory.open(new File(servletContext.getRealPath('/lucene/' + project.id))))
				sp.clearIndex()
				sp.indexDictionary([ getWordsIterator: { -> return dictionary.iterator() } ] as Dictionary)
				sp.close()
			}
			render "Updated keyword index of ${params.project} in ${System.currentTimeMillis() - start} ms"	
		}
	}

	def updateSeries = {
		long start = System.currentTimeMillis()
		def collection = mongoService[params.project]

		// calculate count, total, and mean
		def stats = [:]
		collection.findAll('class': 'Datum').each { doc ->
			if (!stats[doc.type]) stats[doc.type] = [max: Double.MIN_VALUE, min: Double.MAX_VALUE, count: 0, total: 0]
			def local = stats[doc.type]
			doc.findAll { !(it.key in ['_id', '_ns', 'class', 'type', 'top', 'base']) }.each { k,v ->
				if (v) {
					local.max = Math.max(local.max, v)
					local.min = Math.min(local.min, v)
					local.count++
					local.total += v
				}
			}
		}
		stats.each { k,v -> v.mean = v.total / v.count }

		// calculate standard deviation
		collection.findAll('class': 'Datum').each { doc ->
			def local = stats[doc.type]
			if (local.devtot == null) local.devtot = 0
			doc.findAll { !(it.key in ['_id', '_ns', 'class', 'type', 'top', 'base']) }.each { k,v ->
				if (v) {
					local.devtot += (v - local.mean) * (v - local.mean)
				}
			}
		}
		stats.each { k,v ->
			v.stddev = Math.sqrt(v.devtot / (v.count - 1))
			v.remove('total')
			v.remove('devtot')
		}

		// update our config for the project
		def config = mongoService['_configs']
		def doc = config.find(['id': params.project])
		if (doc) {
			config.update(doc, ['$set': ['stats': stats]])
		}

		render "Updated data series stats of ${params.project} in ${System.currentTimeMillis() - start}ms"
	}

	def issues = {
		return [issues: mongoService['_issues'].findAll(pending: true)]
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
}
