import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/**
 * Study Planner - To-Do List page (Java Swing)
 * Compile: javac TodoList.java
 * Run:     java TodoList
 */
public class TodoList extends JFrame {

    // ---------- Colors ----------
    static final Color BG        = new Color(0xFB, 0xF1, 0xF8);
    static final Color PINK_TEXT = new Color(0xE8, 0xA5, 0xBC);
    static final Color PINK_LINK = new Color(0xC7, 0x7D, 0xBE);
    static final Color PINK_AREA = new Color(0xF2, 0xD7, 0xE0);
    static final Color GRAY_TEXT = new Color(0x80, 0x80, 0x80);
    static final Color GREEN     = new Color(0x33, 0xB8, 0x33);
    static final Color RED       = new Color(0xD3, 0x2F, 0x2F);
    static final Color YELLOW    = new Color(0xE0, 0xB7, 0x2D);

    // ---------- Fonts ----------
    static final Font FONT_TITLE = new Font("Tahoma", Font.BOLD, 22);
    static final Font FONT_BIG   = new Font("Tahoma", Font.BOLD, 18);
    static final Font FONT_NORM  = new Font("Tahoma", Font.BOLD, 14);
    static final Font FONT_SMALL = new Font("Tahoma", Font.PLAIN, 12);

    // ---------- Variables ----------
    private static TodoList instance;          // keeps the one and only TodoList window
    private JFrame calendarFrame;              // the calendar page (hidden while we are open)
    private StudyPlannerApp app;               // all logic is in here
    private StudyPlannerApp.Filter filter = StudyPlannerApp.Filter.ALL;
    private JPanel listPanel = new JPanel();   // the panel that holds all task rows
    private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ---------- Open this page from the calendar ----------
    static void open(JFrame from, StudyPlannerApp app) {
        if (instance == null) {
            instance = new TodoList(app);
        }
        instance.calendarFrame = from;
        instance.setVisible(true);
        from.setVisible(false);
    }

    // ---------- Go back to the calendar ----------
    void goBack() {
        if (calendarFrame instanceof CalendarGUI) {
            ((CalendarGUI) calendarFrame).refreshSummary();   // update the numbers first
        }
        if (calendarFrame != null) {
            calendarFrame.setVisible(true);
        }
        setVisible(false);
    }

    static void reset() {
        if (instance != null) {
            instance.dispose();
            instance = null;
        }
    }

    // ---------- Constructor ----------
    public TodoList(StudyPlannerApp app) {
        this.app = app;
        setTitle("Study Planner");
        setSize(487, 802);                       // same size as calendar_gui
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createTopBar(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);

        refresh();
    }

    // ---------- Top bar (title + sign out) ----------
    private JPanel createTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Color.WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xDD, 0xDD, 0xDD)),
                new EmptyBorder(15, 20, 15, 20)));

        JLabel title = new JLabel("study planner");
        title.setFont(FONT_TITLE);
        title.setForeground(PINK_TEXT);

        JButton signOut = linkButton("sign out", PINK_TEXT);
        signOut.addActionListener(e -> {
            int ok = JOptionPane.showConfirmDialog(this, "Do you want to sign out?",
                    "Sign out", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                if (calendarFrame != null) {
                    calendarFrame.dispose();
                }
                dispose();
                instance = null;
                new Login().setVisible(true);
            }
        });

        bar.add(title, BorderLayout.WEST);
        bar.add(signOut, BorderLayout.EAST);
        return bar;
    }

    // Plain text button (no border)
    static JButton linkButton(String text, Color color) {
        JButton b = new JButton(text);
        b.setFont(FONT_NORM);
        b.setForeground(color);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ---------- Main panel (back button, title, filters, list, add button) ----------
    private JPanel createMainPanel() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BG);
        main.setBorder(new EmptyBorder(10, 10, 15, 10));

        // ---- Header: back button + "To-Do List" ----
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JButton back = linkButton("←  back to calendar", PINK_LINK);
        back.setAlignmentX(Component.LEFT_ALIGNMENT);
        back.addActionListener(e -> goBack());

        JLabel title = new JLabel("To-Do List");
        title.setFont(FONT_TITLE);
        title.setForeground(new Color(0xF0, 0xB8, 0xCC));
        title.setBorder(new EmptyBorder(5, 8, 10, 0));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(back);
        header.add(title);
        main.add(header, BorderLayout.NORTH);

        // ---- White card ----
        JPanel card = new JPanel(new BorderLayout(0, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(10, 10, 10, 10));
        main.add(card, BorderLayout.CENTER);

        // Filter buttons
        JPanel filters = new JPanel(new GridLayout(1, 4, 10, 0));
        filters.setOpaque(false);
        filters.setPreferredSize(new Dimension(0, 60));
        filters.add(filterButton("ALL", StudyPlannerApp.Filter.ALL, PINK_LINK));
        filters.add(filterButton("Done", StudyPlannerApp.Filter.DONE, GREEN));
        filters.add(filterButton("<html><center>Not<br>Done</center></html>", StudyPlannerApp.Filter.NOT_DONE, RED));
        filters.add(filterButton("Soon", StudyPlannerApp.Filter.SOON, YELLOW));
        card.add(filters, BorderLayout.NORTH);

        // Pink area (holds the list and the add button)
        JPanel pinkArea = new JPanel(new BorderLayout(0, 10));
        pinkArea.setBackground(PINK_AREA);
        pinkArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(PINK_AREA);

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(PINK_AREA);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        pinkArea.add(scroll, BorderLayout.CENTER);

        // Add subject button
        JButton addButton = new JButton("Add subject");
        addButton.setFont(FONT_BIG.deriveFont(16f));
        addButton.setForeground(GRAY_TEXT);
        addButton.setBackground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorder(new EmptyBorder(10, 24, 10, 24));
        addButton.addActionListener(e -> {
            AddSubject page = new AddSubject(this, app, () -> refresh());
            page.setVisible(true);
            setVisible(false);
        });

        JPanel addWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        addWrap.setOpaque(false);
        addWrap.add(addButton);
        pinkArea.add(addWrap, BorderLayout.SOUTH);

        card.add(pinkArea, BorderLayout.CENTER);
        return main;
    }

    // ---------- One filter button ----------
    private JButton filterButton(String text, StudyPlannerApp.Filter key, Color color) {
        JButton b = new JButton(text);
        b.setFont(FONT_BIG.deriveFont(16f));
        b.setForeground(color);
        b.setBackground(new Color(0xF5, 0xF5, 0xF5));
        b.setFocusPainted(false);
        b.addActionListener(e -> {
            filter = key;
            refresh();
        });
        return b;
    }

    // ---------- Redraw the whole list ----------
    void refresh() {
        listPanel.removeAll();
        for (Task t : app.getTasks(filter)) {
            listPanel.add(createRow(t));
            listPanel.add(Box.createVerticalStrut(10));
        }
        listPanel.revalidate();
        listPanel.repaint();
    }

    // ---------- One task row ----------
    private JPanel createRow(Task t) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(Color.WHITE);
        row.setBorder(new EmptyBorder(10, 10, 10, 10));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        // Circle checkbox (left)
        JCheckBox cb = new JCheckBox();
        cb.setOpaque(false);
        cb.setIcon(new CircleIcon(false));
        cb.setSelectedIcon(new CircleIcon(true));
        cb.setSelected(t.isDone());
        cb.addActionListener(e -> {
            app.setDone(t, cb.isSelected());
            refresh();
        });

        // Subject name + description (center)
        JLabel subject = new JLabel(t.getTitle());
        subject.setFont(FONT_BIG.deriveFont(15f));
        subject.setForeground(GRAY_TEXT);
        subject.setToolTipText(t.getTitle());          // show full name when it is too long

        JLabel desc = new JLabel(t.getDescription());
        desc.setFont(FONT_SMALL.deriveFont(11f));
        desc.setForeground(new Color(0x9E, 0x9E, 0x9E));
        desc.setToolTipText(t.getDescription());

        JPanel text = new JPanel(new GridLayout(2, 1));
        text.setOpaque(false);
        text.add(subject);
        text.add(desc);

        // Status box + deadline (right)
        JComboBox<String> statusBox = new JComboBox<>(
                new String[]{Task.TODO, Task.IN_PROGRESS, Task.COMPLETE});
        statusBox.setFont(FONT_SMALL);
        statusBox.setPreferredSize(new Dimension(105, 26));
        statusBox.setSelectedItem(t.getStatus());
        statusBox.addActionListener(e -> {
            app.setStatus(t, (String) statusBox.getSelectedItem());
            refresh();
        });

        JLabel deadline = new JLabel(t.getDeadline().format(fmt));
        deadline.setFont(FONT_SMALL.deriveFont(Font.BOLD));
        deadline.setForeground(GRAY_TEXT);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        right.setOpaque(false);
        right.add(statusBox);
        right.add(deadline);

        row.add(cb, BorderLayout.WEST);
        row.add(text, BorderLayout.CENTER);
        row.add(right, BorderLayout.EAST);
        return row;
    }

    // ---------- Circle icon for the checkbox ----------
    static class CircleIcon implements Icon {
        private boolean checked;

        CircleIcon(boolean checked) {
            this.checked = checked;
        }

        public int getIconWidth()  { return 22; }
        public int getIconHeight() { return 22; }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.DARK_GRAY);
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(x + 2, y + 2, 17, 17);
            if (checked) {
                g2.setColor(GREEN);
                g2.fillOval(x + 6, y + 6, 10, 10);
            }
            g2.dispose();
        }
    }

    // ---------- main ----------
    public static void main(String[] args) {
        new TodoList(new StudyPlannerApp(new User("test"))).setVisible(true);
    }
}