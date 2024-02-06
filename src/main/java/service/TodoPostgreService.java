package service;
import model.TodoPostgre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.TodoPostgreRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TodoPostgreService {
    @Autowired
    private TodoPostgreRepository todoRepository;

    public List<TodoPostgre> getAllTodos() {
        return todoRepository.findAll();
    }

    public TodoPostgre getTodoById(Integer id){
        return todoRepository.getById(id);
    }

    public TodoPostgre addNewTodo(TodoPostgre todo) {
        return todoRepository.save(todo);
    }

    public void deleteTodoById(Integer id) {
        todoRepository.deleteById(id);
    }

    public int getTodosCount() {
        return (int) todoRepository.count();
    }

    public String updateTodoStatus(int id, String status){
        Optional<TodoPostgre> todo = todoRepository.findById(id);
        String oldStatus = null;
        if(todo.isPresent()){
            TodoPostgre existingTodo = todo.get();
            oldStatus = existingTodo.getStatus();
            existingTodo.setStatus(status);
        }

        return oldStatus;
    }


}
