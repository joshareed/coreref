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
			findByClass { String type -> delegate.find(['_class': type] as BasicDBObject) }
			findByDepth { Number top, Number base -> delegate.find([top: ['$lte': base], base: ['$gte', top]] as BasicDBObject) }
			findByClassAndDepth { String type, Number top, Number base -> delegate.find(['_class': type, top: ['$lte': base], base: ['$gte', top]] as BasicDBObject) }
		}
		DBCursor.metaClass {
			sort << { LinkedHashMap keys -> delegate.sort(keys as BasicDBObject) }
		}
	}

	def getCollection(name) {
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
