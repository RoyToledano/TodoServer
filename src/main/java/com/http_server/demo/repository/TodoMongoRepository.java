package com.http_server.demo.repository;

import com.http_server.demo.model.TodoMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoMongoRepository extends MongoRepository<TodoMongo, Integer> {
}
