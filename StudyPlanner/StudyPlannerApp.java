import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The "App" layer: all task logic lives here, with NO Swing/AWT.
 * The GUI pages only call these methods and then redraw themselves.
 */
public class StudyPlannerApp {

    public enum Filter { ALL, DONE, NOT_DONE, SOON }

    /** What the calendar should show for one day. */
    public enum DayStatus { NONE, PENDING, ALL_DONE }

    private final User user;
    private final StorageService storage;
    private final List<Task> tasks;
    private final Dashboard dashboard = new Dashboard();

    /** Each user gets their own task file (tasks_<username>.txt). */
    public StudyPlannerApp(User user) {
        this.user = user;
        this.storage = new StorageService(user.getUsername());
        this.tasks = storage.loadTasks();
    }

    // ---------- Create / update / delete ----------
    public Task addTask(String name, String description, LocalDate deadline, String status) {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("name is empty");
        if (deadline == null) throw new IllegalArgumentException("deadline is null");

        Task t = new Task(name.trim(), description == null ? "" : description.trim(),
                new Subject(name.trim()), deadline);
        t.setStatus(status);
        tasks.add(t);
        save();
        return t;
    }

    public void editTask(Task task, String newTitle, String newDescription, LocalDate newDeadline) {
        task.editTask(newTitle, newDescription, newDeadline);
        save();
    }

    public void setDone(Task task, boolean done) {
        task.setStatus(done ? Task.COMPLETE : Task.TODO);
        save();
    }

    public void toggleStatus(Task task) {
        task.toggleStatus();
        save();
    }

    public boolean deleteTask(Task task) {
        boolean removed = tasks.remove(task);
        if (removed) save();
        return removed;
    }

    // ---------- Query ----------
    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    public List<Task> getTasks(Filter filter) {
        List<Task> result = new ArrayList<>();
        for (Task t : tasks) {
            boolean ok;
            switch (filter) {
                case DONE:     ok = t.isDone(); break;
                case NOT_DONE: ok = !t.isDone(); break;
                case SOON:     ok = t.isDueSoon(Dashboard.DUE_SOON_DAYS); break;
                default:       ok = true;
            }
            if (ok) result.add(t);
        }
        return result;
    }

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

    public List<Task> getTasksByDate(LocalDate date) {
        List<Task> result = new ArrayList<>();
        for (Task t : tasks) {
            if (t.getDeadline().equals(date)) result.add(t);
        }
        return result;
    }

    /** NONE = no task due that day, PENDING = at least one unfinished task, ALL_DONE = every task that day is complete. */
    public DayStatus getDayStatus(LocalDate date) {
        boolean any = false;
        for (Task t : tasks) {
            if (t.getDeadline().equals(date)) {
                if (!t.isDone()) return DayStatus.PENDING;
                any = true;
            }
        }
        return any ? DayStatus.ALL_DONE : DayStatus.NONE;
    }

    /** Fresh numbers for the calendar summary. */
    public Dashboard getStats() {
        dashboard.computeStats(tasks);
        return dashboard;
    }

    public User getCurrentUser() { return user; }

    private void save() {
        storage.saveTasks(tasks);
    }
}
