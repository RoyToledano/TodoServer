package com.http_server.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.http_server.demo.model.TodoPostgres;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoPostgresRepository extends JpaRepository<TodoPostgres, Integer>{
}
