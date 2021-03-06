package coreref

class CorelyzerController extends SecureController {

    def cml = {
		withProject { project ->
			response.setHeader "Content-disposition", "attachment; filename=${project.id}.cml"
			def images = mongoService[project.id].findAll('class': 'Image').collect { it }.groupBy { it.type }
			render(contentType: 'text/xml') {
				comment << "Generated by CoreRef (http://coreref.org)"
				scene(name: "${project.id}", version: "1.5") {
					session(name: "${project.id} via CoreRef") {
						images.each { type, list ->
							visual(type: 'track', name: type, x: 0, y: 0, z: 0, length: 0, mcd_depth: 0, top_depth: 0) {
								list.each { img ->
									String name = img.url.split('/')[-1]
									name = name.substring(0, name.lastIndexOf('.') - 1)
									visual(
										type: 'core_section', 
										depth: img.top as BigDecimal, 
										length: (img.base as BigDecimal) - (img.top as BigDecimal), 
										rot: -90,
										orientation: 'LANDSCAPE',
										urn: img.url.replace('and1-1b', 'and/and1-1b'),
										name: name
									)
									if (type == 'split') {
										//visual(type: 'graph')
									}
								}
							}
						}
					}
				}
			}	
		}
	}
	
	def data = {
		render "Data"
	}
}
