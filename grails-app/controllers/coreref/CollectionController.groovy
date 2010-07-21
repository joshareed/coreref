package coreref

class CollectionController {
	def mongoService

    def index = {
		def collections = mongoService['_collections']
		if (!collections) {
			response.sendError(500, "_collections not initialized")
			return false
		}
		
		def matching
		def collection = collections.find('_id': params.collection)
		if (collection) {
			def query = [:]
			transform(collection.query, query)
			matching = mongoService['_projects'].findAll(query).sort(program: 1, expedition: 1, site: 1, hole: 1).collect { SearchUtils.clean(it) }
		} else {
			if (params.collection == 'all') {
				matching = mongoService['_projects'].findAll([:]).sort(program: 1, expedition: 1, site: 1, hole: 1).collect { SearchUtils.clean(it) }
				collection = [name: 'All Projects', description: 'This collection contains all project available at CoreRef']
			} else {
				matching = mongoService['_projects'].findAll(program: params.collection).sort(program: 1, expedition: 1, site: 1, hole: 1).collect { SearchUtils.clean(it) }
				collection = [name: "${params.project} Expeditions", description: "All ${params.project} Expeditions"]
			}
		}

		if (!matching) {
			response.sendError(404, "Invalid collection: '${params.collection}'")
			return false
		}

		// render our projects
		def projects = [:]
		matching.groupBy { it.program }.each { program, list ->
			projects[program] = list.groupBy { it.expedition ?: it.leg }
		} 
		collection.projects = projects
		return [collection: collection]
	}
	
	private def transform(from, to) {
	    from.each { k,v ->
	        def key = k.replace('_$_', '$')
	        if (v instanceof Map) {
	            to[key] = [:]
	            transform(v, to[key])
	        } else {
	            to[key] = v
	        }
	    }
	}
}
