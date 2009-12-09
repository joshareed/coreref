class UrlMappings {
	static mappings = {
		//"/services/data/flot/$collection/$series/$top/$base?"(controller: 'data', action: 'flot')
		"/services/track/$action/$collection/$top/$base?/$scale?"(controller: 'track')
		"/"(view:"/index")
		"500"(view:'/error')
	}
}
