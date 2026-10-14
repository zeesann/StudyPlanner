import java.util.ArrayList;
import java.util.List;

public class ReminderService {

    public ArrayList<Task> checkDueSoon(List<Task> tasks) {
        ArrayList<Task> dueSoonTasks = new ArrayList<>();
        for (Task t : tasks) {
            if (t.isDueSoon(Dashboard.DUE_SOON_DAYS)) {
                dueSoonTasks.add(t);
            }
        }
        return dueSoonTasks;
    }

    public void sendEmailReminder(Task task) {
        System.out.println("=== ส่งอีเมลแจ้งเตือน ===");
        System.out.println("งาน: " + task.getTitle());
        System.out.println("วิชา: " + task.getSubject().getName());
        System.out.println("ครบกำหนด: " + task.getDeadline());
        System.out.println("========================");
    }

    public void checkAndNotify(List<Task> tasks) {
        ArrayList<Task> dueSoonTasks = checkDueSoon(tasks);

        if (dueSoonTasks.isEmpty()) {
            System.out.println("ไม่มีงานใกล้ครบกำหนด");
        } else {
            for (Task t : dueSoonTasks) {
                sendEmailReminder(t);
            }
        }
    }
}
