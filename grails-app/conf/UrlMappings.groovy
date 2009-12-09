class UrlMappings {
	static mappings = {
		"/admin/$action/$collection/$opt?"(controller: 'admin')
		"/services/track/$action/$collection/$top/$base?/$scale?"(controller: 'track')
		"/"(view:"/index")
		"500"(view:'/error')
	}
}
