class BootStrap {

	def init = { servletContext ->
		org.andrill.coretools.Platform.start()
	}
     
	def destroy = {
	}
} 