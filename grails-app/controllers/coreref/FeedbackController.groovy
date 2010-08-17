package coreref

class FeedbackController {
	def mailService
	def mongoService

	def contact = {
		sendMail {
			to 'info@coreref.org'
			subject 'Feedback from coreref.org contact form'
			body """
Name: ${params.name}
Email: ${params.email}
Message: ${params.message}
			"""
		}
	}
	
	def data = {
		sendMail {
			to 'info@coreref.org'
			subject 'Data Submission'
			body """
Name: ${params.name}
Email: ${params.email}
Project: ${params.project == 'new' ? 'New' : 'Existing (' + params.existing + ')' }
Message: ${params.message}
			"""
		}
	}

	def issue = {
		def doc = new LinkedHashMap(params)
		doc.remove('action')
		doc.remove('controller')
		doc.pending = true
		if (doc.project) {
			mongoService['_issues'].insert(doc)
			render "Issue successfully submitted!"
		} else {
			response.sendError(400, "Project required")
		}
	}
}
