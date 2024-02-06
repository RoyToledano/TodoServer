package repository;

import model.TodoMongo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TodoMongoRepository extends MongoRepository<TodoMongo, Integer> {
}
