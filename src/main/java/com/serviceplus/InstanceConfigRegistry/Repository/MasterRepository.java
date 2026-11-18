package com.serviceplus.InstanceConfigRegistry.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.serviceplus.InstanceConfigRegistry.Entity.Master;

@Repository
public interface MasterRepository   extends MongoRepository<Master, Integer> {

}
