package coreref

class ProjectController {
	def mongoService

	private getMin(id) { return 3 * Math.round((mongoService[id]?.findByClass('Image')?.top ?: 0) / 3) }
	private getProject(id) { mongoService['_projects'].findById(id) }

	def index = { redirect(action: overview, params: params) }

	def overview = {
		def project = getProject(params.project)
		return [ project: project ]
	}

	def viewer = {
		// handle no depth
		if (params.depth == null) {
			redirect(action: viewer, params: [ project: params.project, depth: getMin(params.project) ])
			return
		}

		// check to see if depth is multiple of 3
		def depth = params.depth as double
		if (depth % 3 > 0) {
			def base = 3 * Math.floor(depth / 3)
			def offset = depth % 3
			redirect(url: createLink(controller: 'project', action: 'viewer', params: [project: params.project, depth: base]) + "#${offset}")
			return
		}

		// if we made it here, the depth is a multiple of 3 so just pass through
		def project = getProject(params.project)
		return [ project: project, depth: params.depth ]
	}

	def search = {
		def project = getProject(params.project)
		def results

		if (params.q) {
			// clean up the user's query
			def q = params.q ?: ''
			q.replaceAll(' or ', '|').replaceAll(' and ', ' ')

			// build a query map for mongo
			def tokens = SearchUtils.tokenize(q, [])
			def query = [:]
			if (q.contains('|')) {
				query['_keywords'] = ['$in': tokens]
			} else {
				query['_keywords'] = ['$all': tokens]
			}

			// convert the results into documents for display
			results = mongoService[params.project].findAll(query).sort(top: 1).collect { doc ->
				def r = [:]
				r.top = doc.top as BigDecimal
				if (doc.top != doc.base) r.base = doc.base as BigDecimal
				r.title = doc.type ?: doc['class']
				def depth = 3 * Math.floor(r.top / 3) as BigDecimal
				r.link = createLink(controller:'project', action: 'viewer', params: [depth: depth, project: project.id]) + "#${(r.top - depth)}"
				r.text = doc.text ?: doc?.code?.replaceAll(',', ' ')
				r
			}
		}

		return [ project: project, q: params.q , results: results]
	}
}
