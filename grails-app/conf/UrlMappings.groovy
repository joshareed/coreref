class UrlMappings {
	static mappings = {
		"/admin/$project/$action/$opt?"(controller: 'admin')
		"/projects/$project/$action?/$depth?"(controller: 'project')
		"/services/$project/track/$action/$top/$base?/$scale?"(controller: 'track')
		"/services/$project/search/$action/$query?/$top?/$base?"(controller: 'search')
		"/services/$project/config/$action"(controller: 'config')
		"/services/resources/$action/$opt?"(controller: 'resources')
		"/services/recent/$action/$project?"(controller: 'recent')
		"/services/feedback/$action"(controller: 'feedback')
		"/"(view: '/index.gsp')
		"/contact"(view: '/contact.gsp')
		"/help"(view: '/help.gsp')
		"500"(view: '/error.gsp')
	}
}
