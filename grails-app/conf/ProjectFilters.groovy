class ProjectFilters {
	def mongoService

	def filters = {
		// check that the project is defined in the '_projects' collection
		projectCheck(controller:'*', action:'*') {
			before = {
				if (params.controller in ['resources', 'recent', 'feedback']) return true

				// check for a '_projects' collection
				def projects = mongoService['_projects']
				if (!projects) {
					response.sendError(500, "Unable to access projects database")
					return false
				}

				// ensure our project is defined in '_projects'
				//def project = projects.find(id: params.project)
				def project = projects.findById(params.project)
				if (!project) {
					response.sendError(404, "Invalid project: '${params.project}'")
					return false
				}

				return true
			}
		}
	}
}