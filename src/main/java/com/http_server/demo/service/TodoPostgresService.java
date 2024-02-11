package com.http_server.demo.service;
import com.http_server.demo.model.TodoPostgres;
import com.http_server.demo.repository.TodoPostgresRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TodoPostgresService {
    @Autowired
    private TodoPostgresRepository todoRepository;

    public List<TodoPostgres> getAllTodos() {
        return todoRepository.findAll();
    }

    public TodoPostgres getTodoById(Integer id){
        return todoRepository.getById(id);
    }

    public TodoPostgres addNewTodo(TodoPostgres todo) {
        return todoRepository.save(todo);
    }

    public void deleteTodoById(Integer id) {
        todoRepository.deleteById(id);
    }

    public int getTodosCount() {
        return (int) todoRepository.count();
    }

    public String updateTodoStatus(int id, String status){
        Optional<TodoPostgres> todo = todoRepository.findById(id);
        String oldStatus = null;
        if(todo.isPresent()){
            TodoPostgres existingTodo = todo.get();
            oldStatus = existingTodo.getState();
            existingTodo.setState(status);
            todoRepository.save(existingTodo);
        }

        return oldStatus;
    }


}
