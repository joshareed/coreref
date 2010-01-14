package coreref

class ProjectController {
	def mongoService

	private firstimg(project) {
		def collection = mongoService.getCollection(project)
		if (collection) {
			return collection.find('class': 'Image').find { true }?.top ?: 0
		} else {
			return 0
		}
	}

	def index = { redirect(action: overview, params: params) }

	def overview = {
		def collection = mongoService.getCollection('_projects')
		if (collection) {
			def project = collection.find(project: params.collection).find { true }
			if (project) {
				return [project: project, depth: params.depth ?: firstimg(params.collection) ]
			} else {
				sendError(404, "Invalid project: '${params.collection}'")
			}
		} else {
			sendError(500, "'_projects' collection not defined")
		}
	}

	def viewer = {
		def collection = mongoService.getCollection('_projects')
		if (collection) {
			def project = collection.find(project: params.collection).find { true }
			if (project) {
				return [project: project, depth: params.depth ?: firstimg(params.collection)]
			} else {
				sendError(404, "Invalid project: '${params.collection}'")
			}
		} else {
			sendError(500, "'_projects' collection not defined")
		}
	}

	def downloads = {
		def collection = mongoService.getCollection('_projects')
		if (collection) {
			def project = collection.find(project: params.collection).find { true }
			if (project) {
				return [project: project, depth: params.depth ?: firstimg(params.collection)]
			} else {
				sendError(404, "Invalid project: '${params.collection}'")
			}
		} else {
			sendError(500, "'_projects' collection not defined")
		}
	}

	def timeline = {
		def collection = mongoService.getCollection('_projects')
		if (collection) {
			def project = collection.find(project: params.collection).find { true }
			if (project) {
				return [project: project, depth: params.depth ?: firstimg(params.collection)]
			} else {
				sendError(404, "Invalid project: '${params.collection}'")
			}
		} else {
			sendError(500, "'_projects' collection not defined")
		}
	}
}
