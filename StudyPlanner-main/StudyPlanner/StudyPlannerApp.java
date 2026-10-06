import java.util.ArrayList;
import java.util.Iterator;
 
public class StudyPlannerApp {
 
    private ArrayList<Task> tasks;
    private ArrayList<Subject> subjects;
    private User currentUser;
 
    
    public StudyPlannerApp(User currentUser) {
        this.currentUser = currentUser;
        this.tasks = new ArrayList<>();
        this.subjects = new ArrayList<>();
    }
 
   
    public void addTask(Task task) {
        tasks.add(task);
        System.out.println("เพิ่มงานแล้ว: " + task.getTitle());
    }
 
    
    public void editTask(String title, String newTitle, String newDescription, String newDeadline) {
        for (Task t : tasks) {
            if (t.getTitle().equals(title)) {
                t.editTask(newTitle, newDescription, newDeadline);
                System.out.println("แก้ไขงานแล้ว: " + title + " -> " + newTitle);
                return; 
            }
        }
        System.out.println("ไม่พบงานชื่อ: " + title);
    }
 
   
    public void deleteTask(String title) {
        Iterator<Task> it = tasks.iterator();
        while (it.hasNext()) {
            Task t = it.next();
            if (t.getTitle().equals(title)) {
                it.remove(); 
                System.out.println("ลบงานแล้ว: " + title);
                return;
            }
        }
        System.out.println("ไม่พบงานชื่อ: " + title);
    }
 
   
    public ArrayList<Task> searchTasks(String keyword) {
        ArrayList<Task> result = new ArrayList<>();
        for (Task t : tasks) {
            boolean inTitle = t.getTitle().toLowerCase().contains(keyword.toLowerCase());
            boolean inDescription = t.getDescription().toLowerCase().contains(keyword.toLowerCase());
            if (inTitle || inDescription) {
                result.add(t);
            }
        }
        return result;
    }
 
    public ArrayList<Task> filterByStatus(String status) {
        ArrayList<Task> result = new ArrayList<>();
        for (Task t : tasks) {
            if (t.getStatus().equals(status)) {
                result.add(t);
            }
        }
        return result;
    }
 
    
    public ArrayList<Task> getTasks() {
        return tasks;
    }
 
    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks; 
    }
 
    public ArrayList<Subject> getSubjects() {
        return subjects;
    }
 
    public User getCurrentUser() {
        return currentUser;
    }
}