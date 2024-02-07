package repository;

import model.TodoMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoMongoRepository extends MongoRepository<TodoMongo, Integer> {
}
