class UrlMappings {
	static mappings = {
		"/admin/$collection/$action/$opt?"(controller: 'admin')
		"/projects/$collection/$action?/$depth?"(controller: 'project')
		"/services/track/$collection/$action/$top/$base?/$scale?"(controller: 'track')
		"/services/search/$collection/$action/$query?/$top?/$base?"(controller: 'search')
		"/services/resources/$action/$opt?"(controller: 'resources')
		"/"(view:"/index")
		"500"(view:'/error')
	}
}
