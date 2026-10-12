import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** All users' tasks live in ONE file (tasks.txt).
 *  One line = username|title|description|subject|yyyy-MM-dd|status */
public class StorageService {

    private static final String FILE_NAME = "tasks.txt";
    private final String username;

    public StorageService(String username) {
        this.username = username;
    }

    private static String clean(String s) {
        return s == null ? "" : s.replace("|", " ").replace("\r", " ").replace("\n", " ");
    }

    public void saveTasks(List<Task> tasks) {
        // 1) keep lines that belong to other users
        List<String> others = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
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
        try (FileWriter writer = new FileWriter(FILE_NAME, false)) {
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

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
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