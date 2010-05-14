package coreref

class CollectionController {
	def mongoService

    def index = {
		def collection = mongoService['_collections'].find('_id': params.collection)
		if (collection) {
			def query = [:]
			transform(collection.query, query)
			def matching = mongoService['_projects'].findAll(query).sort(program: 1, leg: 1, site: 1, hole: 1).collect { SearchUtils.clean(it) }
			def projects = [:]
			matching.groupBy { it.program }.each { program, list ->
				projects[program] = list.groupBy { it.leg }
			} 
			collection.projects = projects
			return [collection: collection]
		} else {
			response.sendError(404, "Invalid collection: '${params.collection}'")
		}
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
