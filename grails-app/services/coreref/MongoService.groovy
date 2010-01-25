package coreref

import org.codehaus.groovy.grails.commons.ApplicationHolder
import com.mongodb.*

class MongoService {
	private Mongo mongo
	boolean transactional = false

	static {
		// some convenience methods
		DBCollection.metaClass {
			findAll << { LinkedHashMap query -> delegate.find(query as BasicDBObject) }
			findAll << { LinkedHashMap query, LinkedHashMap filter -> delegate.find(query as BasicDBObject, filter as BasicDBObject) }
			find << { LinkedHashMap query -> delegate.find(query as BasicDBObject).find {true} }
			find << { LinkedHashMap query, LinkedHashMap filter -> delegate.find(query as BasicDBObject, filter as BasicDBObject).find {true} }
			methodMissing { String name, args ->
				if (name.startsWith('findBy')) {
					def p =	 name - 'findBy'
					p = p[0].toLowerCase() + p[1..-1]
					def query = [:]
					query[p] = args[0]
					return delegate.find(query as BasicDBObject).find { true }
				} else if (name.startsWith('findAllBy')) {
					def p =	 name - 'findAllBy'
					p = p[0].toLowerCase() + p[1..-1]
					def query = [:]
					query[p] = args[0]
					return delegate.find(query as BasicDBObject)
				}
				throw new MissingMethodException(name, delegate, args)
			}
		}
		DBCursor.metaClass {
			sort << { LinkedHashMap keys -> delegate.sort(keys as BasicDBObject) }
			first << { -> delegate.find { true } }
		}
	}

	// allow accessing collections like properties.
	def propertyMissing(name) {
		def collection = getCollection(name)
		if (collection) {
			MongoService.metaClass."$name" = collection
		}
		return collection
	}

	// get a collection by name.  If the collection doesn't exist, this method return null.
	def getCollection(name) {
		if (!name) { return null }
		if (!mongo) {
			// create our Mongo instance if needed
			def host = ApplicationHolder.application.config?.mongo?.host ?: 'localhost'
			mongo = new Mongo(host)
		}

		// get our database and collection
		def db = mongo.getDB(ApplicationHolder.application.config?.mongo?.db ?: 'coreref')
		if (name in db.collectionNames) {
			return db.getCollection(name)
		} else {
			return null
		}
	}
}
