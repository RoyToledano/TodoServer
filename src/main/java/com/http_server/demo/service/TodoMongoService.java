package com.http_server.demo.service;

import com.http_server.demo.model.TodoMongo;
import com.http_server.demo.repository.TodoMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        TodoMongo ret = todoRepository.save(todo);
        long meow = todoRepository.count();
        return ret;
    }

    public void deleteTodoById(Integer id) {
        long meow = todoRepository.count();
        for (TodoMongo todo : todoRepository.findAll()) {
            if(todo.getRawid() == id){
                todoRepository.delete(todo);
            }
        }
    }

    public int getTodosCount() {
        return (int) todoRepository.count();
    }

    public String updateTodoStatus(int id, String status){
        String oldStatus = null;

        for (TodoMongo todo : todoRepository.findAll()) {
            if(todo.getRawid() == id){
                oldStatus = todo.getState();
                todo.setState(status);
                todoRepository.save(todo);
            }
        }

        long meow = todoRepository.count();

        return oldStatus;
    }
}
