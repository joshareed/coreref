package coreref

import java.text.DecimalFormat

import grails.converters.JSON
import org.apache.lucene.store.*
import org.apache.lucene.search.spell.*

/**
 * A controller for project-related actions.
 */
class ProjectController extends SecureController {
	private static final DecimalFormat DEC = new DecimalFormat('0.##')

	private getMin(id) { mongoService[id]?.findByClass('Image')?.top ?: 0 }
	private getParts(depth) { [ 5 * Math.floor(depth / 5), ((depth as double) % 5) as BigDecimal] }

	def index = { redirect(action: overview, params: params) }

	/**
	 * Show the overview page for the requested project.
	 *
	 * Note: SECURE
	 */
	def overview = {
		withProject { project ->
			def related
			if (project.program && project.leg) {
				related = mongoService['_projects'].findAll(program: project.program, leg: project.leg, id: ['$ne': project.id]).sort(program: 1, leg: 1, site: 1, hole: 1).collect { SearchUtils.clean(it) }
			}
			return [ project: project, related: related ] 
		}
	}

	/**
	 * Show the viewer page for the requested project.
	 *
	 * Note: SECURE
	 */
	def viewer = {
		withProject { project ->
			// handle no depth
			if (params.depth == null) {
				def (base, offset) = getParts(getMin(params.project))
				redirect(url: createLink(controller: 'project', action: 'viewer', params: [project: params.project, depth: DEC.format(base)]) + "#${DEC.format(offset)}")
				return
			}

			// check to see if depth is multiple of 5
			def depth = params.depth as double
			if (depth % 5 > 0) {
				def (base, offset) = getParts(depth)
				redirect(url: createLink(controller: 'project', action: 'viewer', params: [project: params.project, depth: DEC.format(base)]) + "#${DEC.format(offset)}")
				return
			}

			// if we made it here, the depth is a multiple of 5 so just pass through
			return [ project: project, depth: params.depth ]
		}
	}

	/**
	 * Perform a keyword search within the requested project.
	 *
	 * Note: SECURE
	 */
	def search = {
		withProject { project -> 
			def results
			def didyoumean

			if (params.q) {
				// clean up the user's query
				def q = (params.q ?: '').replaceAll(' or ', '|').replaceAll(' and ', ' ')

				// if it is a number, redirect to that depth
				if (q.isNumber() || q.endsWith('m') || q.endsWith('mbsf') || q.endsWith('mblf')) {
					def clean = q.replace('mbsf', '').replace('mblf', '').replace('m', '').trim()
					if (clean.isNumber()) {
						mongoService['_searches'].insert([project: params.project, query: params.q])
						def depth = clean as double
						def (base, offset) = getParts(depth)
						redirect(url: createLink(controller: 'project', action: 'viewer', params: [project: params.project, depth: DEC.format(base)]) + "#${DEC.format(offset)}")
						return	
					}
				}

				// build a query map for mongo
				def tokens = SearchUtils.tokenize(q, [])
				def query = [:]
				if (q.contains('|')) {
					query['_keywords'] = ['$in': tokens]
				} else {
					query['_keywords'] = ['$all': tokens]
				}
				
				// build a did you mean query
				SpellChecker sp = new SpellChecker(FSDirectory.open(new File(servletContext.getRealPath('/lucene/' + project.id))))
				sp.accuracy = 0.75
				def alternate = didYouMean(sp, tokens)
				if (alternate && alternate != tokens) {
					didyoumean = params.q
					tokens.eachWithIndex { t, i -> didyoumean = didyoumean.replaceAll(t, alternate[i]) }
				}
				sp.close()

				// convert the results into documents for display
				results = mongoService[params.project].findAll(query).sort(top: 1).collect { doc ->
					def r = [:]
					r.top = doc.top as BigDecimal
					if (doc.top != doc.base) r.base = doc.base as BigDecimal
					r.title = doc.name ?: doc['class']
					def (base, offset) = getParts(r.top)
					r.link = createLink(controller:'project', action: 'viewer', params: [depth: DEC.format(base), project: project.id]) + "#${DEC.format(offset)}"
					r.text = doc.text ?: doc?.code?.replaceAll(',', ' ')
					r
				}
				if (results) { // only insert searches that returned results
					mongoService['_searches'].insert([project: params.project, query: params.q])
				}
			}
			return [ project: project, q: params.q , results: results, didyoumean: didyoumean]	
		}
	}
	
	private List didYouMean(SpellChecker sp, List query) {
		sp.accuracy = 0.75
		def didyoumean = []
		query.each { word ->
			if (sp.exist(word)) {
				didyoumean << word
			} else {
				def suggest = sp.suggestSimilar(word, 5)
				didyoumean << (suggest ? suggest[0] : word)
			}
		}
		return didyoumean
	}
	
	def list = {
		def results = mongoService['_projects'].findAll([:]).collect { SearchUtils.clean(it) }
		if (params.callback) {
			render(contentType: 'application/json', text: "${params.callback}(${results as JSON})")
		} else {
			render(contentType: 'application/json', text: (results as JSON))
		}
	}
}
