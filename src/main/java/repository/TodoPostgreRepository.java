package repository;
import org.springframework.data.jpa.repository.JpaRepository;
import model.TodoPostgre;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoPostgreRepository extends JpaRepository<TodoPostgre, Integer>{
}
