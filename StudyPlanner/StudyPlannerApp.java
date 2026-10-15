import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ============================================================
 *  คลาส StudyPlannerApp  (ชั้น "ตรรกะหลักของแอป")
 * ============================================================
 *  หน้าที่: เป็น "ศูนย์กลาง" ที่จัดการงานทั้งหมดของผู้ใช้หนึ่งคน
 *    - เพิ่ม / แก้ไข / ลบ งาน
 *    - เปลี่ยนสถานะงาน
 *    - ค้นหา / กรอง / ดึงงานตามวันที่
 *    - สรุปสถานะรายวันสำหรับปฏิทิน และสถิติสำหรับ Dashboard
 *    - สั่งบันทึกลงไฟล์ทุกครั้งที่ข้อมูลเปลี่ยน
 *
 *  จุดสำคัญ:
 *    - คลาสนี้ไม่มี Swing/AWT เลย (ไม่ยุ่งกับหน้าจอ)
 *      หน้าจอ (GUI) แค่เรียกเมธอดในนี้ แล้ววาดหน้าจอตัวเองใหม่
 *    - ไม่เขียนไฟล์เอง แต่ฝากให้ StorageService ทำ
 *
 *  ภาพรวมการทำงานร่วมกัน:
 *      หน้าจอ  ->  StudyPlannerApp  ->  Task (ข้อมูล)
 *                         |
 *                         +-> StorageService (เก็บลงไฟล์)
 *                         +-> Dashboard (คำนวณสถิติ)
 */
public class StudyPlannerApp {

    /** ตัวกรองรายการงาน: ทั้งหมด / เสร็จแล้ว / ยังไม่เสร็จ / ใกล้ถึงกำหนด */
    public enum Filter { ALL, DONE, NOT_DONE, SOON }

    /** สถานะของ "วันหนึ่งวัน" ที่ปฏิทินจะใช้แสดงสี/เครื่องหมาย */
    public enum DayStatus {
        NONE,      // วันนั้นไม่มีงาน
        PENDING,   // มีงานอย่างน้อย 1 งานที่ยังไม่เสร็จ
        ALL_DONE   // มีงาน และทุกงานเสร็จหมดแล้ว
    }

    // ---------- ตัวแปรของคลาส ----------
    private final User user;                // ผู้ใช้ที่ล็อกอินอยู่
    private final StorageService storage;   // ตัวอ่าน/เขียนไฟล์ของผู้ใช้คนนี้
    private final List<Task> tasks;         // รายการงานทั้งหมดในหน่วยความจำ
    private final Dashboard dashboard = new Dashboard();   // ตัวคำนวณสถิติ

    /**
     * ตัวสร้าง: สร้างแอปสำหรับผู้ใช้หนึ่งคน
     * ขั้นตอน: สร้าง StorageService ด้วยชื่อผู้ใช้ -> โหลดงานเก่าจากไฟล์เข้ามาในลิสต์ tasks
     * (หมายเหตุ: จริงๆ ตอนนี้ทุกคนใช้ไฟล์ tasks.txt เดียวกัน โดยแยกคนด้วยคอลัมน์ username)
     */
    public StudyPlannerApp(User user) {
        this.user = user;
        this.storage = new StorageService(user.getUsername());
        this.tasks = storage.loadTasks();
    }

    // ============================================================
    //  สร้าง / แก้ไข / ลบ
    // ============================================================

    /** เพิ่มงานใหม่ (เมธอดนี้ถูกเรียกจากหน้า AddSubject ตอนกดปุ่ม Save) */
    public Task addTask(String name, String description, LocalDate deadline, String status) {
        // ตรวจข้อมูลก่อน ถ้าผิดให้ throw error
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("name is empty");
        if (deadline == null) throw new IllegalArgumentException("deadline is null");

        // สร้าง Task: trim() ตัดช่องว่างหัวท้าย, ถ้า description เป็น null ให้เป็นข้อความว่าง
        // ชื่อวิชา (Subject) ตั้งเท่ากับชื่อที่กรอก
        Task t = new Task(name.trim(), description == null ? "" : description.trim(),
                new Subject(name.trim()), deadline);
        t.setStatus(status);   // ตั้งสถานะตามที่ผู้ใช้เลือก (ค่าเริ่มต้นของ Task คือ Todo)
        tasks.add(t);          // เพิ่มลงลิสต์
        save();                // บันทึกลงไฟล์ทันที
        return t;
    }

    /** แก้ไขชื่อ / รายละเอียด / วันกำหนดส่ง ของงานที่มีอยู่ */
    public void editTask(Task task, String newTitle, String newDescription, LocalDate newDeadline) {
        task.editTask(newTitle, newDescription, newDeadline);
        save();
    }

    /** ติ๊กว่าเสร็จ/ไม่เสร็จ: true = Complete, false = กลับไป Todo */
    public void setDone(Task task, boolean done) {
        task.setStatus(done ? Task.COMPLETE : Task.TODO);
        save();
    }

    /** เปลี่ยน Status เป็นค่าที่เลือก (Todo / In Progress / Complete) แล้วบันทึกลงไฟล์ */
    public void setStatus(Task task, String status) {
        task.setStatus(status);
        save();
    }

    /** กดสลับสถานะวนไปเรื่อยๆ: Todo -> In Progress -> Complete -> Todo */
    public void toggleStatus(Task task) {
        task.toggleStatus();
        save();
    }

    /** ลบงาน คืน true ถ้าลบสำเร็จ (เจองานนั้นในลิสต์) */
    public boolean deleteTask(Task task) {
        boolean removed = tasks.remove(task);
        if (removed) save();   // บันทึกเฉพาะเมื่อมีการลบจริง
        return removed;
    }

    // ============================================================
    //  ดึงข้อมูล (Query)
    // ============================================================

    /** คืนงานทั้งหมดแบบ "อ่านอย่างเดียว" หน้าจอจะเพิ่ม/ลบผ่านลิสต์นี้ตรงๆ ไม่ได้ */
    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    /** คืนงานตามตัวกรองที่เลือก */
    public List<Task> getTasks(Filter filter) {
        List<Task> result = new ArrayList<>();
        for (Task t : tasks) {
            boolean ok;
            switch (filter) {
                case DONE:     ok = t.isDone(); break;                                  // เสร็จแล้ว
                case NOT_DONE: ok = !t.isDone(); break;                                 // ยังไม่เสร็จ
                case SOON:     ok = t.isDueSoon(Dashboard.DUE_SOON_DAYS); break;        // ใกล้ถึงกำหนด
                default:       ok = true;                                               // ALL = เอาทั้งหมด
            }
            if (ok) result.add(t);
        }
        return result;
    }

    /** ค้นหางานจากคำที่พิมพ์ (หาในชื่อหรือรายละเอียด ไม่สนตัวพิมพ์เล็ก/ใหญ่) */
    public List<Task> searchTasks(String keyword) {
        String k = keyword.toLowerCase();
        List<Task> result = new ArrayList<>();
        for (Task t : tasks) {
            if (t.getTitle().toLowerCase().contains(k) || t.getDescription().toLowerCase().contains(k)) {
                result.add(t);
            }
        }
        return result;
    }

    /** ดึงงานทั้งหมดที่กำหนดส่งตรงกับวันที่ระบุ (ใช้ตอนกดวันบนปฏิทิน) */
    public List<Task> getTasksByDate(LocalDate date) {
        List<Task> result = new ArrayList<>();
        for (Task t : tasks) {
            if (t.getDeadline().equals(date)) result.add(t);
        }
        return result;
    }

    /**
     * บอกสถานะของวันหนึ่งๆ สำหรับปฏิทิน
     *  NONE    = วันนั้นไม่มีงาน
     *  PENDING = มีงานที่ยังไม่เสร็จอย่างน้อย 1 งาน
     *  ALL_DONE = มีงาน และเสร็จครบทุกงาน
     */
    public DayStatus getDayStatus(LocalDate date) {
        boolean any = false;   // จำไว้ว่าวันนี้เจองานหรือยัง
        for (Task t : tasks) {
            if (t.getDeadline().equals(date)) {
                if (!t.isDone()) return DayStatus.PENDING;   // เจองานที่ยังไม่เสร็จ ตอบได้เลย
                any = true;
            }
        }
        // วนจนจบแล้วไม่มีงานค้าง: ถ้าเคยเจองาน = ALL_DONE, ไม่เคยเจอ = NONE
        return any ? DayStatus.ALL_DONE : DayStatus.NONE;
    }

    /** คำนวณตัวเลขสถิติล่าสุดทุกครั้งที่เรียก (แสดงสรุปบนปฏิทิน) */
    public Dashboard getStats() {
        dashboard.computeStats(tasks);
        return dashboard;
    }

    /** คืนผู้ใช้ที่ล็อกอินอยู่ */
    public User getCurrentUser() { return user; }

    // เมธอดภายใน: สั่งให้ StorageService เขียนงานทั้งหมดลงไฟล์
    // ถูกเรียกทุกครั้งที่มีการเพิ่ม/แก้/ลบ/เปลี่ยนสถานะ
    private void save() {
        storage.saveTasks(tasks);
    }
}