import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Calendar {

    private String currentMonth;   // format yyyy-MM

    public Calendar(String currentMonth) {
        this.currentMonth = currentMonth;
    }

    public ArrayList<Task> getEventsByDate(LocalDate date, List<Task> tasks) {
        ArrayList<Task> result = new ArrayList<>();
        for (Task t : tasks) {
            if (t.getDeadline().equals(date)) result.add(t);
        }
        return result;
    }

    public void showDeadlineMarker(Task task) {
        System.out.println("📍 " + task.getDeadline() + " has task: " + task.getTitle()
                + " (" + task.getSubject().getName() + ")");
    }

    public void nextMonth() {
        currentMonth = LocalDate.parse(currentMonth + "-01").plusMonths(1).toString().substring(0, 7);
        System.out.println("Next month: " + currentMonth);
    }

    public void prevMonth() {
        currentMonth = LocalDate.parse(currentMonth + "-01").minusMonths(1).toString().substring(0, 7);
        System.out.println("Previous month: " + currentMonth);
    }

    public String getCurrentMonth() {
        return currentMonth;
    }
}
