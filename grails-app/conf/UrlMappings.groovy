class UrlMappings {
	static mappings = {
		"/admin/$action/$collection/$opt?"(controller: 'admin')
		"/services/track/$action/$collection/$top/$base?/$scale?"(controller: 'track')
		"/services/search/$action/$collection/$query?/$top?/$base?"(controller: 'search')
		"/"(view:"/index")
		"500"(view:'/error')
	}
}
