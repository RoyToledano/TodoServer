package model;

public interface Todo {
    public int getId();
    public void setId(int id);
    public String getTitle();
    public void setTitle(String title);
    public String getContent();
    public void setContent(String content);
    public long getDueDate();
    public void setDueDate(long dueDate);
    public String getStatus();
    public void setStatus(String status);
}
