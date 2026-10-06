public class Task {
  
    private String title;
    private String description;
    private Subject subject;
    private String deadline;
    private String status;
 
    public Task(String title, String description, Subject subject, String deadline) {
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.deadline = deadline;
        this.status = "Todo"; 
    }
 
    public String getTitle() {
        return title;
    }
 
    public String getDescription() {
        return description;
    }
 
    public Subject getSubject() {
        return subject;
    }
 
    public String getDeadline() {
        return deadline;
    }
 
    public String getStatus() {
        return status;
    }

    public void setTitle(String title) {
        this.title = title;
    }
 
    public void setDescription(String description) {
        this.description = description;
    }
 
    public void setSubject(Subject subject) {
        this.subject = subject;
    }
 
    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
 
    public void setStatus(String status) {
        this.status = status;
    }
 
    public void editTask(String newTitle, String newDescription, String newDeadline) {
        this.title = newTitle;
        this.description = newDescription;
        this.deadline = newDeadline;
    }
 
    public void toggleStatus() {
        if (status.equals("Todo")) {
            status = "In Progress";
        } else if (status.equals("In Progress")) {
            status = "Complete";
        } else {
            status = "Todo";
        }
    }
 
    @Override
    public String toString() {
        return "[" + status + "] " + title + " (" + subject.getName() + ") - due " + deadline;
    }
}
    

