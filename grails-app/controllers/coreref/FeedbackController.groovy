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

	def issue = {
		def doc = new LinkedHashMap(params)
		doc.remove('action')
		doc.remove('controller')
		doc.pending = true
		mongoService['_issues'].insert(doc)

		render "Issue successfully submitted!"
	}
}
