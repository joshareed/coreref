package coreref

class FeedbackController {
	def mailService

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
		// TODO
	}
}
