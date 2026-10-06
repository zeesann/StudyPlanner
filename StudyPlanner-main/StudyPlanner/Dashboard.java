import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
 
public class Dashboard {
 
    private int total;
    private int done;
    private int notDone;
    private int dueSoon;
 
    
    public void computeStats(ArrayList<Task> tasks) {
        total = tasks.size();
        done = 0;
        notDone = 0;
        dueSoon = 0;
 
        LocalDate today = LocalDate.now();
 
        for (Task t : tasks) {
            if (t.getStatus().equals("Complete")) {
                done++;
            } else {
                notDone++;
 
                
                try {
                    LocalDate deadlineDate = LocalDate.parse(t.getDeadline());
                    long daysLeft = ChronoUnit.DAYS.between(today, deadlineDate);
 
                    
                    if (daysLeft >= 0 && daysLeft <= 1) {
                        dueSoon++;
                    }
                } catch (Exception e) {
                    System.out.println("รูปแบบวันที่ไม่ถูกต้องสำหรับงาน: " + t.getTitle());
                }
            }
        }
 
        System.out.println("คำนวณสถิติเสร็จแล้ว: ทั้งหมด " + total
                + " เสร็จแล้ว " + done
                + " ยังไม่เสร็จ " + notDone
                + " ใกล้ครบกำหนด " + dueSoon);
    }
 
    
    public int getTotal() {
        return total;
    }
 
    public int getDone() {
        return done;
    }
 
    public int getNotDone() {
        return notDone;
    }
 
    public int getDueSoon() {
        return dueSoon;
    }
}
