class UrlMappings {
	static mappings = {
		"/admin/$collection/$action/$opt?"(controller: 'admin')
		"/projects/$collection/$action?/$depth?"(controller: 'project')
		"/services/$collection/track/$action/$top/$base?/$scale?"(controller: 'track')
		"/services/$collection/search/$action/$query?/$top?/$base?"(controller: 'search')
		"/services/$collection/config/$action/$top/$base?"(controller: 'config')
		"/services/resources/$action/$opt?"(controller: 'resources')
		"/"(view:"/index")
		"500"(view:'/error')
	}
}
