package service;

import model.TodoMongo;
import model.TodoPostgre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.TodoMongoRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TodoMongoService {
    @Autowired
    private TodoMongoRepository todoRepository;

    public List<TodoMongo> getAllTodos() {
        return todoRepository.findAll();
    }

    public Optional<TodoMongo> getTodoById(Integer id){
        return todoRepository.findById(id);
    }

    public TodoMongo addNewTodo(TodoMongo todo) {
        return todoRepository.save(todo);
    }

    public void deleteTodoById(Integer id) {
        todoRepository.deleteById(id);
    }

    public int getTodosCount() {
        return (int) todoRepository.count();
    }

    public String updateTodoStatus(int id, String status){
        Optional<TodoMongo> todo = todoRepository.findById(id);
        String oldStatus = null;
        if(todo.isPresent()){
            TodoMongo existingTodo = todo.get();
            oldStatus = existingTodo.getStatus();
            existingTodo.setStatus(status);
        }

        return oldStatus;
    }
}
