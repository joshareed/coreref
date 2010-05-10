class UrlMappings {
	static mappings = {
		// pages
		"/"(view: '/index.gsp')
		"/contact"(view: '/contact.gsp')
		"/help"(view: '/help.gsp')
		"500"(view: '/error.gsp')
		
		// admin area
		"/admin/issues"(controller: 'admin', action: 'issues')
		"/admin/$project/$action?/$opt?"(controller: 'admin')
		
		// projects area
		"/projects/$project/$action?/$depth?"(controller: 'project')
		
		// project-related services
		"/services/projects"(controller: 'project', action: 'list')
		"/services/$project/track/$action/$top/$base?/$scale?"(controller: 'track')
		"/services/$project/search/$action/$query?/$top?/$base?"(controller: 'search')
		"/services/$project/config/$action"(controller: 'config')
		"/services/$project/corelyzer/$action"(controller: 'corelyzer')
		"/services/$project/visualizer/$action?/$opt?"(controller: 'visualizer')
		
		// viewer-related services
		"/services/resources/$action/$opt?"(controller: 'resources')
		"/services/recent/$action/$project?"(controller: 'recent')
		"/services/feedback/$action"(controller: 'feedback')
	}
}
