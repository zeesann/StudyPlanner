import java.util.List;

/** Computes summary numbers for the calendar page (no UI code). */
public class Dashboard {

    /** "Due soon" = within this many days. Single place to change it. */
    public static final int DUE_SOON_DAYS = 3;

    private int total;
    private int done;
    private int notDone;
    private int dueSoon;

    public void computeStats(List<Task> tasks) {
        total = tasks.size();
        done = 0;
        notDone = 0;
        dueSoon = 0;

        for (Task t : tasks) {
            if (t.isDone()) {
                done++;
            } else {
                notDone++;
                if (t.isDueSoon(DUE_SOON_DAYS)) dueSoon++;
            }
        }
    }

    public int getTotal()   { return total; }
    public int getDone()    { return done; }
    public int getNotDone() { return notDone; }
    public int getDueSoon() { return dueSoon; }
}
