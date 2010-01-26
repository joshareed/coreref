package coreref

class ProjectController {
	def mongoService

	private getMin(id) { return mongoService[id]?.findByClass('Image')?.top ?: 0 }
	private getProject(id) { mongoService['_projects'].findById(id) }

	def index = { redirect(action: overview, params: params) }

	def overview = {
		def project = getProject(params.project)
		return [ project: project, depth: params.depth ?: getMin(params.project) ]
	}

	def viewer = {
		def project = getProject(params.project)
		return [ project: project ]
	}

	def search = {
		def project = getProject(params.project)

		def query = [:]
		def q = params.q.replaceAll(' or ', '|').replaceAll(' and ', ' ') ?: ''
		if (q.contains('|')) {
			query['_keywords'] = ['$in': SearchUtils.tokenize(q, [])]
		} else {
			query['_keywords'] = ['$all': SearchUtils.tokenize(q, [])]
		}

		def results = mongoService[params.project].findAll(query).sort(top: 1).collect { doc ->
			def r = [:]
			r.top = doc.top as BigDecimal
			if (doc.top != doc.base) r.base = doc.base as BigDecimal
			r.title = doc.type ?: doc['class']
			r.text = doc.text ?: doc?.code?.replaceAll(',', ' ')
			def depth = 5 * Math.floor(r.top / 5) as BigDecimal
			r.link = createLink(controller:'project', action: 'viewer', params: [depth: depth, project: project.id]) + "#${(r.top - depth)}"
			r
		}

		return [ project: project, q: params.q , results: results]
	}
}
