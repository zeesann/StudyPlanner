import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * ============================================================
 *  คลาส Task  (ชั้น "ข้อมูล" หรือ Model)
 * ============================================================
 *  หน้าที่: แทน "งาน 1 งาน" เช่น "ส่งรายงานวิชาคณิตศาสตร์ ภายในวันที่ 20"
 *
 *  จุดสำคัญ:
 *    - เป็นคลาสเก็บข้อมูลล้วนๆ ไม่มีโค้ดหน้าจอ (no UI code)
 *    - แต่ละงานเก็บ 5 อย่าง: ชื่อ, รายละเอียด, วิชา, วันกำหนดส่ง, สถานะ
 *    - มีเมธอดช่วยคิด เช่น เหลืออีกกี่วัน, ใกล้ถึงกำหนดไหม, เสร็จหรือยัง
 */
public class Task {

    // ค่าคงที่ของสถานะ 3 แบบ (ใช้ค่าเหล่านี้แทนการพิมพ์ข้อความเอง จะได้ไม่พิมพ์ผิด)
    public static final String TODO        = "Todo";          // ยังไม่เริ่ม
    public static final String IN_PROGRESS = "In Progress";   // กำลังทำ
    public static final String COMPLETE    = "Complete";      // เสร็จแล้ว

    // ข้อมูลของงาน (private = เข้าถึงจากภายนอกได้ผ่าน getter/setter เท่านั้น)
    private String title;          // ชื่องาน
    private String description;    // รายละเอียด
    private Subject subject;       // วิชาที่เกี่ยวข้อง
    private LocalDate deadline;    // วันกำหนดส่ง
    private String status;         // สถานะปัจจุบัน

    /** ตัวสร้าง: สร้างงานใหม่ โดยสถานะเริ่มต้นเป็น Todo เสมอ */
    public Task(String title, String description, Subject subject, LocalDate deadline) {
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.deadline = deadline;
        this.status = TODO;
    }

    // ---------- Getter: อ่านค่า ----------
    public String getTitle()        { return title; }
    public String getDescription()  { return description; }
    public Subject getSubject()     { return subject; }
    public LocalDate getDeadline()  { return deadline; }
    public String getStatus()       { return status; }

    // ---------- Setter: ตั้งค่า ----------
    public void setTitle(String title)             { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setSubject(Subject subject)        { this.subject = subject; }
    public void setDeadline(LocalDate deadline)    { this.deadline = deadline; }
    public void setStatus(String status)           { this.status = status; }

    /** แก้ไขชื่อ รายละเอียด และวันกำหนดส่งในคราวเดียว */
    public void editTask(String newTitle, String newDescription, LocalDate newDeadline) {
        this.title = newTitle;
        this.description = newDescription;
        this.deadline = newDeadline;
    }

    /** สลับสถานะวนเป็นวงกลม: Todo -> In Progress -> Complete -> กลับไป Todo */
    public void toggleStatus() {
        if (status.equals(TODO)) {
            status = IN_PROGRESS;
        } else if (status.equals(IN_PROGRESS)) {
            status = COMPLETE;
        } else {
            status = TODO;
        }
    }

    /** งานนี้เสร็จแล้วหรือยัง (true เมื่อสถานะเป็น Complete) */
    public boolean isDone() {
        // เขียน COMPLETE.equals(status) เพื่อกัน error ในกรณี status เป็น null
        return COMPLETE.equals(status);
    }

    /** จำนวนวันจาก "วันนี้" ถึงวันกำหนดส่ง (ค่าติดลบ = เลยกำหนดแล้ว) */
    public long getDaysLeft() {
        return ChronoUnit.DAYS.between(LocalDate.now(), deadline);
    }

    /**
     * "ใกล้ถึงกำหนด" หรือไม่ คือต้องครบ 3 เงื่อนไข
     *   1) ยังไม่เสร็จ
     *   2) ยังไม่เลยกำหนด (เหลือ >= 0 วัน รวมวันนี้)
     *   3) เหลือไม่เกิน days วัน
     */
    public boolean isDueSoon(int days) {
        long left = getDaysLeft();
        return !isDone() && left >= 0 && left <= days;
    }

    /** แปลงงานเป็นข้อความสั้นๆ เช่น "[Todo] ทำรายงาน (ทำรายงาน) - due 2026-10-20" */
    @Override
    public String toString() {
        return "[" + status + "] " + title + " (" + subject.getName() + ") - due " + deadline;
    }
}