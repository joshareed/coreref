class UrlMappings {
	static mappings = {
		"/admin/$project/$action/$opt?"(controller: 'admin')
		"/projects/$project/$action?/$depth?"(controller: 'project')
		"/services/$project/track/$action/$top/$base?/$scale?"(controller: 'track')
		"/services/$project/search/$action/$query?/$top?/$base?"(controller: 'search')
		"/services/$project/config/$action"(controller: 'config')
		"/services/resources/$action/$opt?"(controller: 'resources')
		"/"(view:"/index")
		"500"(view:'/error')
	}
}
