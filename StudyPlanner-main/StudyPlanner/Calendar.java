import java.time.LocalDate;
import java.util.ArrayList;
 
public class Calendar {
 
    private String currentMonth; 
 
    public Calendar(String currentMonth) {
        this.currentMonth = currentMonth;
    }
 
    
    public ArrayList<Task> getEventsByDate(String date, ArrayList<Task> tasks) {
        ArrayList<Task> result = new ArrayList<>();
 
        for (Task t : tasks) {
            if (t.getDeadline().equals(date)) {
                result.add(t);
            }
        }
 
        return result;
    }
 
    
    public void showDeadlineMarker(Task task) {
        System.out.println("📍 " + task.getDeadline() + " มีงาน: " + task.getTitle()
                + " (" + task.getSubject().getName() + ")");
    }
 
    
    public void nextMonth() {
        LocalDate temp = LocalDate.parse(currentMonth + "-01"); 
        LocalDate next = temp.plusMonths(1);
        currentMonth = next.toString().substring(0, 7); 
        System.out.println("เปลี่ยนไปเดือน: " + currentMonth);
    }
 
   
    public void prevMonth() {
        LocalDate temp = LocalDate.parse(currentMonth + "-01");
        LocalDate prev = temp.minusMonths(1);
        currentMonth = prev.toString().substring(0, 7);
        System.out.println("เปลี่ยนไปเดือน: " + currentMonth);
    }
 
    public String getCurrentMonth() {
        return currentMonth;
    }
}
 