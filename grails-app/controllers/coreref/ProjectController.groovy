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
		return [ project: project, q: params.q ]
	}
}
