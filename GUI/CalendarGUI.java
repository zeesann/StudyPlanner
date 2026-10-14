
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.TextStyle;
import java.util.Locale;
 
/**
 * Calendar - Java Swing (single file, no XML)
 * Compile: javac *.java
 * Run:     java Login
 */
public class CalendarGUI extends JFrame {
 
    // ---------- Colors ----------
    static final Color BG      = new Color(0xFDF3FD);
    static final Color PINK    = new Color(0xF0A8C8);
    static final Color TITLE   = new Color(0xE58AC0);
    static final Color DARK    = new Color(0x2B2B2B);
    static final Color GREY    = new Color(0xBBBBBB);
    static final Color LINE    = new Color(0xDDDDDD);
    static final Color GREEN   = new Color(0x00C800);
    static final Color RED     = new Color(0xE81B1B);
    static final Color YELLOW  = new Color(0xFFC800);
 
    static final Font BOLD = new Font("SansSerif", Font.BOLD, 16);
 
    // ---------- Data ----------
    Calendar calendar = new Calendar();   // all the date logic is in the Calendar class
 
    int allWork, done, notDone, dueSoon; // actual values are pulled from TodoList.getStats() in refreshSummary()
 
    // ---------- Components ----------
    JComboBox<String> monthBox = new JComboBox<>();
    JComboBox<Integer> yearBox = new JComboBox<>();
    JPanel grid = new JPanel(new GridLayout(0, 7, 4, 6));
    boolean updating = false;
 
    private final StudyPlannerApp app;   // logic layer
 
    public CalendarGUI(StudyPlannerApp app) {
        super("Study Planner");
        this.app = app;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(487, 802);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());
 
        add(buildTopBar(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildSummary(), BorderLayout.SOUTH);
 
        rebuildCalendar();
    }
 
    // ---------- Top bar ----------
    JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Color.WHITE);
        bar.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 1, 0, LINE), new EmptyBorder(14, 18, 14, 18)));
 
        JLabel title = new JLabel("study planner");
        title.setFont(BOLD.deriveFont(18f));
        title.setForeground(PINK);
 
        JLabel signOut = new JLabel("sign out");
        signOut.setFont(BOLD);
        signOut.setForeground(PINK);
        signOut.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signOut.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (JOptionPane.showConfirmDialog(CalendarGUI.this, "Sign out?",
                        "Sign out", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    dispose();
                    TodoList.reset();   // Clear the old To-Do page
                    new Login().setVisible(true);
                }
            }
        });
 
        bar.add(title, BorderLayout.WEST);
        bar.add(signOut, BorderLayout.EAST);
        return bar;
    }
 
    // ---------- Center section (heading + calendar) ----------
    JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(BG);
        center.setBorder(new EmptyBorder(24, 30, 20, 30));
 
        JLabel heading = new JLabel("your study plan", SwingConstants.CENTER);
        heading.setFont(BOLD.deriveFont(24f));
        heading.setForeground(TITLE);
        heading.setBorder(new EmptyBorder(0, 0, 20, 0));
        center.add(heading, BorderLayout.NORTH);
 
        // White card for the calendar
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(14, 14, 14, 14));
 
        // Month/year selector row
        for (Month m : Month.values())
            monthBox.addItem(m.getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
        for (int y = 2020; y <= 2035; y++) yearBox.addItem(y);
 
        JButton prev = navButton("<");
        JButton next = navButton(">");
        prev.addActionListener(e -> { calendar.prevMonth(); rebuildCalendar(); });
        next.addActionListener(e -> { calendar.nextMonth(); rebuildCalendar(); });
 
        ActionListener comboListener = e -> {
            if (updating) return;
            calendar.setMonth((Integer) yearBox.getSelectedItem(), monthBox.getSelectedIndex() + 1);
            rebuildCalendar();
        };
        monthBox.addActionListener(comboListener);
        yearBox.addActionListener(comboListener);
 
        JPanel combos = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        combos.setOpaque(false);
        combos.add(monthBox);
        combos.add(yearBox);
 
        JPanel nav = new JPanel(new BorderLayout());
        nav.setOpaque(false);
        nav.add(prev, BorderLayout.WEST);
        nav.add(combos, BorderLayout.CENTER);
        nav.add(next, BorderLayout.EAST);
        card.add(nav, BorderLayout.NORTH);
 
        grid.setOpaque(false);
        card.add(grid, BorderLayout.CENTER);
 
        // The "+ add task" button and legend were removed.
        // Tasks are now added by double-clicking a day (see makeDayButton).
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(card, BorderLayout.NORTH);
        center.add(wrap, BorderLayout.CENTER);
        return center;
    }
 
    JButton navButton(String text) {
        JButton b = new JButton(text);
        b.setFont(BOLD);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
 
    // ---------- Rebuild the calendar ----------
    void rebuildCalendar() {
        updating = true;
        monthBox.setSelectedIndex(calendar.getMonthNumber() - 1);
        yearBox.setSelectedItem(calendar.getYear());
        updating = false;
 
        grid.removeAll();
 
        String[] heads = {"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"};
        for (String h : heads) {
            JLabel l = new JLabel(h, SwingConstants.CENTER);
            l.setFont(new Font("SansSerif", Font.PLAIN, 12));
            l.setForeground(new Color(0x888888));
            grid.add(l);
        }
 
        int offset = calendar.getBlankDays();
        int daysInMonth = calendar.getDaysInMonth();
 
        for (int i = 0; i < calendar.getTotalCells(); i++) {
            int dayNum = i - offset + 1;
            if (dayNum < 1) {
                grid.add(new JLabel());                       // Blank cells before day 1
            } else if (dayNum > daysInMonth) {
                // Days from next month (faded, cannot click)
                JLabel l = new JLabel(String.valueOf(dayNum - daysInMonth), SwingConstants.CENTER);
                l.setFont(new Font("SansSerif", Font.PLAIN, 15));
                l.setForeground(GREY);
                grid.add(l);
            } else {
                grid.add(makeDayButton(calendar.getDate(dayNum)));
            }
        }
        grid.revalidate();
        grid.repaint();
    }
 
    // ---------- One day button ----------
    JButton makeDayButton(LocalDate date) {
        JButton b = new JButton(String.valueOf(date.getDayOfMonth()));
        b.setFont(new Font("SansSerif", Font.PLAIN, 15));
        b.setPreferredSize(new Dimension(44, 40));
        b.setBackground(Color.WHITE);
        b.setForeground(new Color(0x444444));
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setMargin(new Insets(0, 0, 0, 0));    // no empty space inside the button, so 10-31 are not cut to "..."
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
 
        if (date.equals(calendar.getSelectedDate())) {
            // The clicked day gets a dark background
            b.setBackground(DARK);
            b.setForeground(Color.WHITE);
        } else {
            // Day color: red = has unfinished task, green = every task that day is done
            StudyPlannerApp.DayStatus st = app.getDayStatus(date);
            if (st == StudyPlannerApp.DayStatus.PENDING) {
                b.setForeground(RED);
                b.setFont(b.getFont().deriveFont(Font.BOLD));
            } else if (st != StudyPlannerApp.DayStatus.NONE) {
                b.setForeground(GREEN);
                b.setFont(b.getFont().deriveFont(Font.BOLD));
            }
        }
 
        // Single click = select the day
        b.addActionListener(e -> onDayClick(date));
 
        // Double click = open the Add task page for that day
        b.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    calendar.setSelectedDate(date);
                    openAddTask();
                }
            }
        });
        return b;
    }
 
    // Click a day = that day is selected
    void onDayClick(LocalDate d) {
        calendar.setSelectedDate(d);
        rebuildCalendar();      // redraw so the new day turns dark
    }
 
    // Open the Add subject page with the selected day as the deadline
    void openAddTask() {
        AddSubject page = new AddSubject(this, app, () -> {
            rebuildCalendar();      // redraw the status dots
            refreshSummary();       // update the 4 numbers
        }, calendar.getSelectedDate());
        page.setVisible(true);
        setVisible(false);          // AddSubject shows this page again when it closes
    }
 
    // The 4 number labels, stored as fields so refreshSummary() can update their text later
    JLabel lblAllWork, lblDone, lblNotDone, lblDueSoon;
 
    // ---------- Bottom summary table ----------
    JPanel buildSummary() {
        JPanel p = new JPanel(new GridLayout(4, 1));
        p.setBackground(Color.WHITE);
        p.setBorder(new MatteBorder(1, 0, 1, 0, LINE));
        p.setPreferredSize(new Dimension(0, 250));
 
        lblAllWork = new JLabel();
        lblDone = new JLabel();
        lblNotDone = new JLabel();
        lblDueSoon = new JLabel();
 
        JPanel allRow = summaryRow("All work", lblAllWork, PINK, PINK, false);
        allRow.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        allRow.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { TodoList.open(CalendarGUI.this, app); }
        });
        p.add(allRow);
        p.add(summaryRow("Done", lblDone, GREEN, GREEN, true));
        p.add(summaryRow("Not Done", lblNotDone, RED, RED, true));
        p.add(summaryRow("Due soon", lblDueSoon, YELLOW, YELLOW, true));
 
        refreshSummary(); // Fill in the real numbers as soon as the page opens
        return p;
    }
 
    JPanel summaryRow(String label, JLabel valueLabel, Color c1, Color c2, boolean topLine) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.setBorder(new CompoundBorder(
                topLine ? new MatteBorder(1, 0, 0, 0, LINE) : new EmptyBorder(0, 0, 0, 0),
                new EmptyBorder(0, 40, 0, 60)));
 
        JLabel l = new JLabel(label);
        l.setFont(BOLD.deriveFont(17f));
        l.setForeground(c1);
 
        valueLabel.setFont(BOLD.deriveFont(17f));
        valueLabel.setForeground(c2);
 
        row.add(l, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.EAST);
        return row;
    }
 
    // ---------- Pull the latest numbers from TodoList to update all 4 labels ----------
    // Called when the page is first created, and again every time TodoList.goBack() returns here
    void refreshSummary() {
        Dashboard stats = app.getStats();
        allWork = stats.getTotal();
        done = stats.getDone();
        notDone = stats.getNotDone();
        dueSoon = stats.getDueSoon();
 
        lblAllWork.setText(allWork + "  \u2192");
        lblDone.setText(String.valueOf(done));
        lblNotDone.setText(String.valueOf(notDone));
        lblDueSoon.setText(String.valueOf(dueSoon));
        rebuildCalendar();   // day colors may have changed
    }
 
    // ---------- main ----------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CalendarGUI(new StudyPlannerApp(new User("test"))).setVisible(true));
    }
}
 