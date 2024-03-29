package com.http_server.demo.Controller;
import com.http_server.demo.model.Todo;
import com.http_server.demo.model.TodoMongo;
import com.http_server.demo.model.TodoPostgres;
import org.json.JSONArray;
import org.json.JSONObject;
import com.http_server.demo.service.TodoMongoService;
import com.http_server.demo.service.TodoPostgresService;

import java.util.*;

public class ServerLogic {
    private int todosCounter = 0;
    private TodoPostgresService postgreService;
    private TodoMongoService mongoService;
    public void setTodosCounter(int count){
        todosCounter = count;
    }

    public int meow(){
        postgreService.getTodosCount();
        return mongoService.getTodosCount();
    }

    public void setPostgreService(TodoPostgresService postgreService) {
        this.postgreService = postgreService;
    }

    public void setMongoService(TodoMongoService mongoService) {
        this.mongoService = mongoService;
    }

    public int getTodosCount()
    {
        return postgreService.getTodosCount();
    }

    public void createTodo(String title, String content, long dueDate)
    {
        todosCounter++;
        TodoPostgres newPostgresTodo = new TodoPostgres(todosCounter, title, content, dueDate, "PENDING");
        TodoMongo newMongoTodo = new TodoMongo(todosCounter, title, content, dueDate, "PENDING");

        postgreService.addNewTodo(newPostgresTodo);
        mongoService.addNewTodo(newMongoTodo);

    }

    public TodoPostgres getLastElement()
    {
        return postgreService.getTodoById(todosCounter);
    }

    public int countTodos(String status, String dbName)
    {
        boolean flag = true;
        int res = 0;
        List<Todo> todos = new ArrayList<>();

        if(dbName.equals("POSTGRES")) {
            todos.addAll(postgreService.getAllTodos());
        } else {
            todos.addAll(mongoService.getAllTodos());
        }

        if(status.equals("ALL")) // return the vector size for all
        {
            return todos.size();
        }

        if(isGivenStatusValid(status)) // checking that the given staus is valid.
        {
            flag = false;
            for(Todo todo : todos)
            {
                if(todo.getState().equals(status))
                {
                    res++;
                }
            }
        }

        if(flag) // if the given status is invalid, return -1.
        {
            return -1;
        }

        return res;
    }

    public JSONArray createJsonArray(String sortBy, String status, String dbName)
    {
        ArrayList<Todo> res;
        List<Todo> todos = new ArrayList<>();

        if(dbName.equals("POSTGRES")) {
            todos.addAll(postgreService.getAllTodos());
        } else {
            todos.addAll(mongoService.getAllTodos());
        }

        if(sortBy == null) // if sort by is not given, sorting by ID.
        {
            res = createAndSortTodoArr(status,"ID", todos);
            sortBy = "ID";
        }
        else // in case sort by is given.
        {
            res = createAndSortTodoArr(status,sortBy, todos);
        }

        return toJsonArray(res);
    }

    public boolean isGivenStatusValid(String status)
    {
        if(!status.equals("PENDING")  && !status.equals("LATE") && !status.equals("DONE"))
        {
            return false;
        }
        return true;
    }

    public String updateStatus(int id, String status) {
        postgreService.updateTodoStatus(id, status);
        return mongoService.updateTodoStatus(id, status);
    }


    public int removeTodo(int index)
    {
        postgreService.deleteTodoById(index);
        mongoService.deleteTodoById(index);
        return postgreService.getTodosCount();
    }

    // returns the index of the to-do in the vector, -1 return value for not found.
    public int isIdExists(int id)
    {
        int index = 1;
        for(TodoPostgres todo: postgreService.getAllTodos())
        {
            if(todo.getRawid() == id)
            {
                return index;
            }

            index++;
        }
        return -1;
    }

    public boolean checkStatusAndSortBy(String status, String sortBy)
    {
        if(!status.equals("ALL")  && !status.equals("PENDING") && !status.equals("LATE") && !status.equals("DONE"))
        {

            return true;
        }
        if(sortBy != null && !sortBy.equals("ID") && !sortBy.equals("DUE_DATE") && !sortBy.equals("TITLE"))
        {
            return true;
        }
        return false;
    }

    public boolean isTitleExists(String title)
    {
        for(TodoPostgres todo: postgreService.getAllTodos())
        {
            if(todo.getTitle().equals(title))
            {
                return true;
            }
        }
        return false;
    }

    public boolean isDueDateNotValid(long dueDate)
    {
        long currentTimeMillis = System.currentTimeMillis();
        if(dueDate>currentTimeMillis)
        {
            return false;
        }
        else
        {
            return true;
        }

    }


    private ArrayList<Todo> createAndSortTodoArr(String status, String sortBy, List<Todo> todos)
    {
        ArrayList<Todo> res = new ArrayList<Todo>();
        for(Todo todo : todos)
        {
            if(todo.getState().equals(status) || status.equals("ALL"))
            {
                res.add(todo);
            }
        }

        switch (sortBy)
        {
            case "ID":
                Collections.sort(res, new Comparator<Todo>() {
                    @Override
                    public int compare(Todo o1, Todo o2) {
                        return Integer.compare(o1.getRawid(), o2.getRawid());
                    }
                });
                break;

            case "DUE_DATE":
                Collections.sort(res, new Comparator<Todo>() {
                    @Override
                    public int compare(Todo o1, Todo o2) {
                        return Long.compare(o1.getDuedate(), o2.getDuedate());
                    }
                });
                break;

            case "TITLE":
                Collections.sort(res, new Comparator<Todo>() {
                    @Override
                    public int compare(Todo o1, Todo o2) {
                        return o1.getTitle().compareTo(o2.getTitle());
                    }
                });
                break;
        }

        return res;
    }

    private JSONArray toJsonArray(ArrayList<Todo> todosList) {
        JSONArray jsonArray = new JSONArray();
        for (Todo todo : todosList) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", todo.getRawid());
            map.put("title", todo.getTitle());
            map.put("content", todo.getContent());
            map.put("status", todo.getState());
            map.put("dueDate", todo.getDuedate());

            // create the JSONObject using the Map object
            JSONObject jsonObject = new JSONObject(map);
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

}
