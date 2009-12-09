package coreref

import org.codehaus.groovy.grails.commons.ApplicationHolder
import com.mongodb.*

class MongoService {
	private Mongo mongo
	boolean transactional = false
	static {
		// some convenience methods
		DBCollection.metaClass {
			find << { LinkedHashMap query -> delegate.find(query as BasicDBObject) }
			find << { LinkedHashMap query, LinkedHashMap filter -> delegate.find(query as BasicDBObject, filter as BasicDBObject) }
		}
		DBCursor.metaClass {
			sort << { LinkedHashMap keys -> delegate.sort(keys as BasicDBObject) }
		}
	}

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
