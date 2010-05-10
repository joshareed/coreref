package coreref

/**
 * An abstract controller class that has checks for projects.
 */
abstract class SecureController {
	def mongoService
	
	def checkProject = {
		// check for a '_projects' collection
		def projects = mongoService['_projects']
		if (!projects) {
			response.sendError(500, "Unable to access projects database")
			return false
		}

		// ensure our project is defined in '_projects'
		def project = projects.findById(params.project)
		if (!project) {
			response.sendError(404, "Invalid project: '${params.project}'")
			return false
		}
		
		// ensure proper auth if secure project
		if (project['_secure']) {
			def auth = request.getHeader('Authorization')
			if (!auth) {
				response.addHeader("WWW-Authenticate", "Basic realm=\"" + params.project + "\"")
				response.sendError(401, "Authorization required")
				return false
			}
			
			def credentials =  new String(new sun.misc.BASE64Decoder().decodeBuffer(auth - 'Basic ')).split(':')
			if (credentials[0] != project['_secure'].user || credentials[1] != project['_secure'].pass) {
				response.addHeader("WWW-Authenticate", "Basic realm=\"" + project.name + "\"")
				response.sendError(401, "Authorization required")
				return false
			}
		}

		return true
	}
	
	def withProject = { closure ->
		// check for a '_projects' collection
		def projects = mongoService['_projects']
		if (!projects) {
			response.sendError(500, "Unable to access projects database")
			return false
		}

		// ensure our project is defined in '_projects'
		def project = projects.findById(params.project)
		if (!project) {
			response.sendError(404, "Invalid project: '${params.project}'")
			return false
		}
		
		// ensure proper auth if secure project
		if (project['_secure']) {
			def auth = request.getHeader('Authorization')
			if (!auth) {
				response.addHeader("WWW-Authenticate", "Basic realm=\"" + params.project + "\"")
				response.sendError(401, "Authorization required")
				return false
			}
			
			def credentials =  new String(new sun.misc.BASE64Decoder().decodeBuffer(auth - 'Basic ')).split(':')
			if (credentials[0] != project['_secure'].user || credentials[1] != project['_secure'].pass) {
				response.addHeader("WWW-Authenticate", "Basic realm=\"" + project.name + "\"")
				response.sendError(401, "Authorization required")
				return false
			}
		}

		return closure(project)
	}
}