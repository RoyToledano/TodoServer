package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "todos")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class TodoPostgre implements Serializable, Todo {
    @Id
    @Column(name = "rawid", nullable = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    @Column(name = "title", nullable = false)
    private String title = null;
    @Column(name = "content", nullable = false)
    private String content = null;
    @Column(name = "dueDate", nullable = false)
    private long dueDate;
    @Column(name = "status", nullable = false)
    private String status = null;


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public long getDueDate() {
        return dueDate;
    }
    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
