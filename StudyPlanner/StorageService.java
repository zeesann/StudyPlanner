import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** All users' tasks live in ONE file (tasks.txt).
 *  One line = username|title|description|subject|yyyy-MM-dd|status
 *  ไฟล์นี้อ่าน/เขียนแบบ UTF-8 เพื่อให้เก็บภาษาไทยได้ไม่เพี้ยน */
public class StorageService {

    private static final String FILE_NAME = "tasks.txt";
    private final String username;

    public StorageService(String username) {
        this.username = username;
    }

    private static String clean(String s) {
        return s == null ? "" : s.replace("|", " ").replace("\r", " ").replace("\n", " ");
    }

    // เปิดไฟล์สำหรับอ่านแบบ UTF-8
    private static BufferedReader openReader(File file) throws IOException {
        return new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
    }

    // เปิดไฟล์สำหรับเขียนแบบ UTF-8 (เขียนทับของเดิมทั้งไฟล์)
    private static Writer openWriter() throws IOException {
        return new OutputStreamWriter(new FileOutputStream(FILE_NAME, false), StandardCharsets.UTF_8);
    }

    public void saveTasks(List<Task> tasks) {
        // 1) keep lines that belong to other users
        List<String> others = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (file.exists()) {
            try (BufferedReader reader = openReader(file)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] p = line.split("\\|", -1);
                    if (p.length >= 1 && !p[0].equals(username)) others.add(line);
                }
            } catch (IOException e) {
                System.out.println("Failed to read: " + e.getMessage());
                return;   // don't overwrite if we couldn't read
            }
        }

        // 2) write others + this user's tasks
        try (Writer writer = openWriter()) {
            for (String line : others) writer.write(line + "\n");
            for (Task t : tasks) {
                writer.write(clean(username) + "|"
                        + clean(t.getTitle()) + "|"
                        + clean(t.getDescription()) + "|"
                        + clean(t.getSubject().getName()) + "|"
                        + t.getDeadline() + "|"
                        + t.getStatus() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Failed to save: " + e.getMessage());
        }
    }

    public ArrayList<Task> loadTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) return tasks;

        try (BufferedReader reader = openReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] p = line.split("\\|", -1);
                if (p.length < 6 || !p[0].equals(username)) continue;
                try {
                    Task task = new Task(p[1], p[2], new Subject(p[3]), LocalDate.parse(p[4]));
                    task.setStatus(p[5]);
                    tasks.add(task);
                } catch (Exception e) {
                    System.out.println("Skipped unreadable line: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to load: " + e.getMessage());
        }
        return tasks;
    }
}