import java.io.*;
import java.util.ArrayList;
 
public class StorageService {
 
    private String fileName = "tasks.txt";
 
    public void saveTasks(ArrayList<Task> tasks) {
        try {
            
            FileWriter writer = new FileWriter(fileName, false);
 
            
            for (Task t : tasks) {
                String line = t.getTitle() + "|"
                            + t.getDescription() + "|"
                            + t.getSubject().getName() + "|"
                            + t.getDeadline() + "|"
                            + t.getStatus();
                writer.write(line + "\n"); 
            }
 
            writer.close(); 
            System.out.println("บันทึกข้อมูลสำเร็จ: " + tasks.size() + " งาน");
 
        } catch (IOException e) {
            System.out.println("บันทึกข้อมูลไม่สำเร็จ: " + e.getMessage());
        }
    }
 
    public ArrayList<Task> loadTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        File file = new File(fileName);
 
        if (!file.exists()) {
            System.out.println("ยังไม่มีไฟล์ข้อมูล เริ่มต้นด้วย list ว่าง");
            return tasks;
        }
 
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
 
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|"); // แยก string ด้วยเครื่องหมาย |
 
                // parts[0]=title, parts[1]=description, parts[2]=subjectName,
                // parts[3]=deadline, parts[4]=status
                Subject subject = new Subject(parts[2]);
                Task task = new Task(parts[0], parts[1], subject, parts[3]);
                task.setStatus(parts[4]); 
                tasks.add(task);
            }
 
            reader.close();
            System.out.println("โหลดข้อมูลสำเร็จ: " + tasks.size() + " งาน");
 
        } catch (IOException e) {
            System.out.println("โหลดข้อมูลไม่สำเร็จ: " + e.getMessage());
        }
 
        return tasks;
    }
 
    public void clearAll() {
        File file = new File(fileName);
        if (file.delete()) {
            System.out.println("ลบข้อมูลทั้งหมดแล้ว");
        } else {
            System.out.println("ไม่มีข้อมูลให้ลบ");
        }
    }
}