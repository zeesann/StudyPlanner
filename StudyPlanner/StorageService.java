import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class StorageService {

    // ชื่อไฟล์ที่ใช้เก็บข้อมูล (ค่าคงที่ ใช้ร่วมกันทุกอ็อบเจกต์)
    private static final String FILE_NAME = "tasks.txt";

    // ชื่อผู้ใช้เจ้าของอ็อบเจกต์นี้ ใช้แยกว่าบรรทัดไหนเป็นของใคร
    private final String username;

    /** สร้างตัวจัดเก็บสำหรับผู้ใช้หนึ่งคน */
    public StorageService(String username) {
        this.username = username;
    }

    // ------------------------------------------------------------
    //  ทำความสะอาดข้อความก่อนเขียนลงไฟล์
    // ------------------------------------------------------------
    // แทนที่ '|' และการขึ้นบรรทัดใหม่ด้วยช่องว่าง
    // เพราะอักขระเหล่านี้เป็นตัวคั่นของไฟล์ ถ้าหลุดเข้าไปจะทำให้อ่านกลับมาผิดช่อง
    // ถ้าค่าเป็น null ให้ถือเป็นข้อความว่าง
    private static String clean(String s) {
        return s == null ? "" : s.replace("|", " ").replace("\r", " ").replace("\n", " ");
    }

    // เปิดไฟล์สำหรับ "อ่าน" แบบ UTF-8
    private static BufferedReader openReader(File file) throws IOException {
        return new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
    }

    // เปิดไฟล์สำหรับ "เขียน" แบบ UTF-8 (false = เขียนทับของเดิมทั้งไฟล์ ไม่ต่อท้าย)
    private static Writer openWriter() throws IOException {
        return new OutputStreamWriter(new FileOutputStream(FILE_NAME, false), StandardCharsets.UTF_8);
    }

    // ------------------------------------------------------------
    //  บันทึกงานทั้งหมดของผู้ใช้คนนี้ลงไฟล์
    // ------------------------------------------------------------
    public void saveTasks(List<Task> tasks) {
        // ขั้นที่ 1: อ่านไฟล์เดิม แล้วเก็บ "บรรทัดของผู้ใช้คนอื่น" ไว้ก่อน
        //           (เพราะเดี๋ยวจะเขียนทับทั้งไฟล์ ถ้าไม่เก็บไว้ งานของคนอื่นจะหาย)
        List<String> others = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (file.exists()) {
            try (BufferedReader reader = openReader(file)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // split("\\|", -1) แยกบรรทัดตาม '|' ; -1 = เก็บช่องว่างท้ายบรรทัดไว้ด้วย
                    String[] p = line.split("\\|", -1);
                    // p[0] คือ username ถ้าไม่ใช่ของเรา ให้เก็บทั้งบรรทัดไว้
                    if (p.length >= 1 && !p[0].equals(username)) others.add(line);
                }
            } catch (IOException e) {
                System.out.println("Failed to read: " + e.getMessage());
                return;   // อ่านไม่ได้ก็ไม่เขียนทับ เพื่อป้องกันข้อมูลคนอื่นหาย
            }
        }

        // ขั้นที่ 2: เขียนไฟล์ใหม่ = บรรทัดของคนอื่น + งานของผู้ใช้คนนี้
        try (Writer writer = openWriter()) {
            // เขียนของคนอื่นกลับลงไปตามเดิม
            for (String line : others) writer.write(line + "\n");

            // เขียนงานของเราทีละงาน เรียงตามรูปแบบ username|title|description|subject|deadline|status
            for (Task t : tasks) {
                writer.write(clean(username) + "|"
                        + clean(t.getTitle()) + "|"
                        + clean(t.getDescription()) + "|"
                        + clean(t.getSubject().getName()) + "|"
                        + t.getDeadline() + "|"      // LocalDate แปลงเป็น yyyy-MM-dd อัตโนมัติ
                        + t.getStatus() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Failed to save: " + e.getMessage());
        }
        // try-with-resources จะปิดไฟล์ให้เองอัตโนมัติ
    }

    // ------------------------------------------------------------
    //  โหลดงานของผู้ใช้คนนี้จากไฟล์
    // ------------------------------------------------------------
    public ArrayList<Task> loadTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        File file = new File(FILE_NAME);

        // ยังไม่มีไฟล์ (เปิดโปรแกรมครั้งแรก) -> คืนลิสต์ว่าง
        if (!file.exists()) return tasks;

        try (BufferedReader reader = openReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] p = line.split("\\|", -1);

                // ข้ามบรรทัดที่ข้อมูลไม่ครบ 6 ช่อง หรือไม่ใช่ของผู้ใช้คนนี้
                if (p.length < 6 || !p[0].equals(username)) continue;

                try {
                    // p[1]=ชื่องาน p[2]=รายละเอียด p[3]=ชื่อวิชา p[4]=วันที่ p[5]=สถานะ
                    Task task = new Task(p[1], p[2], new Subject(p[3]), LocalDate.parse(p[4]));
                    task.setStatus(p[5]);
                    tasks.add(task);
                } catch (Exception e) {
                    // เช่น วันที่รูปแบบผิด -> ข้ามบรรทัดนั้น แต่โปรแกรมไม่ล่ม
                    System.out.println("Skipped unreadable line: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to load: " + e.getMessage());
        }
        return tasks;
    }
}
