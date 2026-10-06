import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
 
public class ReminderService {
 
   
    public ArrayList<Task> checkDueSoon(ArrayList<Task> tasks) {
        ArrayList<Task> dueSoonTasks = new ArrayList<>();
        LocalDate today = LocalDate.now();
 
        for (Task t : tasks) {
            if (t.getStatus().equals("Complete")) {
                continue; // continue = ข้ามรอบนี้ ไปดูตัวถัดไปใน loop
            }
 
            try {
                LocalDate deadlineDate = LocalDate.parse(t.getDeadline());
                long daysLeft = ChronoUnit.DAYS.between(today, deadlineDate);
 
                if (daysLeft >= 0 && daysLeft <= 1) {
                    dueSoonTasks.add(t);
                }
            } catch (Exception e) {
                System.out.println("รูปแบบวันที่ไม่ถูกต้องสำหรับงาน: " + t.getTitle());
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
 
    
    public void checkAndNotify(ArrayList<Task> tasks) {
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