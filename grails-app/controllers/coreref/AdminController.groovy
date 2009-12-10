package coreref

class AdminController {
    def mongoService

    def updateIndex = {
        long start = System.currentTimeMillis()
        def collection = mongoService.getCollection(params.collection)
        if (collection) {
            collection.find().each { doc ->
                def tokens = []
                ['name', 'code', 'text'].each { field ->
                    if (doc.containsKey(field)) {
                        SearchUtils.tokenize(doc[field] as String, tokens)
                    }
                }
                if (tokens) {
                    collection.update(doc, ['$set': ['_keywords': tokens]] as com.mongodb.BasicDBObject)
                }
            }
        }
        render "Text indexing of ${params.collection} took ${System.currentTimeMillis() - start} ms"
    }
}
