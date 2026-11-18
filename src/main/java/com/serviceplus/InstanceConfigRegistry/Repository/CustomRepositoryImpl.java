package com.serviceplus.InstanceConfigRegistry.Repository;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class CustomRepositoryImpl<T> implements CustomRepository<T> {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void deleteByTenantId(String value, ClientSession session,Class<T> clazz,String key) {
        MongoCollection<Document> collection = mongoTemplate.getCollection(mongoTemplate.getCollectionName(clazz));
        if(key.equals("_id")){
            collection.deleteMany(session, Filters.eq(key, new ObjectId(value)));
        }
        else {
            collection.deleteMany(session, Filters.eq(key, value));
        }
    }
}
