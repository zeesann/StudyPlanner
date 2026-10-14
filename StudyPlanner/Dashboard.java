import java.util.List;
 
// คลาสคำนวณตัวเลขสรุปสำหรับหน้าปฏิทิน
public class Dashboard {
 
    
    public static final int DUE_SOON_DAYS = 3;
 
    private int total;     
    private int done;     
    private int notDone;   
    private int dueSoon;  
 
    // นับตัวเลขจากรายการงานที่ส่งเข้ามา
    public void computeStats(List<Task> tasks) {
        total = tasks.size();   // งานทั้งหมด = จำนวนงานในรายการ
        // รีเซ็ตเป็น 0 ก่อนนับใหม่ ไม่งั้นเลขจะบวกซ้ำจากรอบเก่า
        done = 0;
        notDone = 0;
        dueSoon = 0;
 
        // ไล่ดูงานทีละอัน
        for (Task t : tasks) {
            if (t.isDone()) {
                done++;                 // เสร็จ
            } else {
                notDone++;              // ยัง
                // ยังไม่เสร็จและใกล้ครบกำหนด ถึงจะนับเป็น "ใกล้ครบกำหนด"
                if (t.isDueSoon(DUE_SOON_DAYS)) dueSoon++;
            }
        }
    }
 
    // ฟังก์ชันขอค่าตัวเลข (ให้ CalendarGUI เรียกไปโชว์)
    public int getTotal()   { return total; }
    public int getDone()    { return done; }
    public int getNotDone() { return notDone; }
    public int getDueSoon() { return dueSoon; }
}
 