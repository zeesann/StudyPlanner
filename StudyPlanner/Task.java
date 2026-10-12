import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** Model: one task (no UI code). */
public class Task {

    public static final String TODO        = "Todo";
    public static final String IN_PROGRESS = "In Progress";
    public static final String COMPLETE    = "Complete";

    private String title;
    private String description;
    private Subject subject;
    private LocalDate deadline;
    private String status;

    public Task(String title, String description, Subject subject, LocalDate deadline) {
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.deadline = deadline;
        this.status = TODO;
    }

    public String getTitle()        { return title; }
    public String getDescription()  { return description; }
    public Subject getSubject()     { return subject; }
    public LocalDate getDeadline()  { return deadline; }
    public String getStatus()       { return status; }

    public void setTitle(String title)             { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setSubject(Subject subject)        { this.subject = subject; }
    public void setDeadline(LocalDate deadline)    { this.deadline = deadline; }
    public void setStatus(String status)           { this.status = status; }

    public void editTask(String newTitle, String newDescription, LocalDate newDeadline) {
        this.title = newTitle;
        this.description = newDescription;
        this.deadline = newDeadline;
    }

    public void toggleStatus() {
        if (status.equals(TODO)) {
            status = IN_PROGRESS;
        } else if (status.equals(IN_PROGRESS)) {
            status = COMPLETE;
        } else {
            status = TODO;
        }
    }

    public boolean isDone() {
        return COMPLETE.equals(status);
    }

    /** Days from today until the deadline (negative = overdue). */
    public long getDaysLeft() {
        return ChronoUnit.DAYS.between(LocalDate.now(), deadline);
    }

    /** Not done and the deadline is within the next `days` days (today included). */
    public boolean isDueSoon(int days) {
        long left = getDaysLeft();
        return !isDone() && left >= 0 && left <= days;
    }

    @Override
    public String toString() {
        return "[" + status + "] " + title + " (" + subject.getName() + ") - due " + deadline;
    }
}
