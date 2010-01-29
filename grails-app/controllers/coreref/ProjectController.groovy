package coreref

class ProjectController {
	def mongoService

	private getMin(id) { return 5 * Math.round((mongoService[id]?.findByClass('Image')?.top ?: 0) / 5) }
	private getProject(id) { mongoService['_projects'].findById(id) }

	def index = { redirect(action: overview, params: params) }

	def overview = {
		def project = getProject(params.project)
		return [ project: project, depth: params.depth ?: getMin(params.project) ]
	}

	def viewer = {
		def project = getProject(params.project)
		return [ project: project, depth: params.depth ?: getMin(params.project) ]
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
				def depth = 5 * Math.floor(r.top / 5) as BigDecimal
				r.link = createLink(controller:'project', action: 'viewer', params: [depth: depth, project: project.id]) + "#${(r.top - depth)}"
				r.text = doc.text ?: doc?.code?.replaceAll(',', ' ')
				r
			}
		}

		return [ project: project, q: params.q , results: results]
	}
}
