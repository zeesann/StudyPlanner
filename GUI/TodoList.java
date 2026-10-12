import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Study Planner - single file (Java Swing)
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

    static final Font FONT_TITLE = new Font("Tahoma", Font.BOLD, 22);
    static final Font FONT_BIG   = new Font("Tahoma", Font.BOLD, 18);
    static final Font FONT_NORM  = new Font("Tahoma", Font.BOLD, 14);
    static final Font FONT_SMALL = new Font("Tahoma", Font.PLAIN, 12);

    // ---------- Page switching ----------
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel pages = new JPanel(cardLayout);
    private TodoPage todoPage;
    private static TodoList instance;
    private JFrame calendarFrame;
    private final StudyPlannerApp app;      // all logic is in here

    static void open(JFrame from, StudyPlannerApp app) {
        if (instance == null) instance = new TodoList(app);
        instance.calendarFrame = from;
        instance.setVisible(true);
        from.setVisible(false);
    }

    // Go back to the Calendar page (the Calendar page was hidden when opened)
    void goBack() {
        if (calendarFrame instanceof CalendarGUI) {
            ((CalendarGUI) calendarFrame).refreshSummary(); // Update the numbers before showing it again
        }
        if (calendarFrame != null) calendarFrame.setVisible(true);
        setVisible(false);
    }

    static void reset() {
        if (instance != null) { instance.dispose(); instance = null; }
    }

    public TodoList(StudyPlannerApp app) {
        this.app = app;
        setTitle("Study Planner");
        setSize(487, 802);                       // same size as calendar_gui
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createTopBar(), BorderLayout.NORTH);

        todoPage = new TodoPage();
        pages.add(todoPage, "todo");
        add(pages, BorderLayout.CENTER);

        showPage("todo");
    }

    void showPage(String name) {
        cardLayout.show(pages, name);
    }

    // ---------- Top bar ----------
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
                if (calendarFrame != null) calendarFrame.dispose();
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

    // =====================================================
    //                    To-Do List page
    // =====================================================
    class TodoPage extends JPanel {
        private StudyPlannerApp.Filter filter = StudyPlannerApp.Filter.ALL;
        private final JPanel listPanel = new JPanel();
        private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        TodoPage() {
            setBackground(BG);
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(10, 10, 15, 10));

            // ---- Header: To-Do List ----
            JPanel header = new JPanel();
            header.setOpaque(false);
            header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

            JLabel title = new JLabel("To-Do List");
            title.setFont(FONT_TITLE);
            title.setForeground(new Color(0xF0, 0xB8, 0xCC));
            title.setBorder(new EmptyBorder(5, 8, 10, 0));
            title.setAlignmentX(Component.LEFT_ALIGNMENT);

            JButton back = linkButton("←  back to calendar", PINK_LINK);
            back.setAlignmentX(Component.LEFT_ALIGNMENT);
            back.addActionListener(e -> goBack());

            header.add(back);
            header.add(title);
            add(header, BorderLayout.NORTH);

            // ---- White card ----
            JPanel card = new JPanel(new BorderLayout(0, 15));
            card.setBackground(Color.WHITE);
            card.setBorder(new EmptyBorder(10, 10, 10, 10));
            add(card, BorderLayout.CENTER);

            // Filter buttons
            JPanel filters = new JPanel(new GridLayout(1, 4, 10, 0));
            filters.setOpaque(false);
            filters.setPreferredSize(new Dimension(0, 60));
            filters.add(filterButton("ALL", StudyPlannerApp.Filter.ALL, PINK_LINK));
            filters.add(filterButton("Done", StudyPlannerApp.Filter.DONE, GREEN));
            filters.add(filterButton("<html><center>Not<br>Done</center></html>", StudyPlannerApp.Filter.NOT_DONE, RED));
            filters.add(filterButton("Soon", StudyPlannerApp.Filter.SOON, YELLOW));
            card.add(filters, BorderLayout.NORTH);

            // Pink area + list
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
            JButton add = new JButton("Add subject");
            add.setFont(FONT_BIG.deriveFont(16f));
            add.setForeground(GRAY_TEXT);
            add.setBackground(Color.WHITE);
            add.setFocusPainted(false);
            add.setBorder(new EmptyBorder(10, 24, 10, 24));
            add.addActionListener(e -> openAddSubject());

            JPanel addWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            addWrap.setOpaque(false);
            addWrap.add(add);
            pinkArea.add(addWrap, BorderLayout.SOUTH);

            card.add(pinkArea, BorderLayout.CENTER);

            refresh();
        }

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

        // Redraw the entire list
        void refresh() {
            listPanel.removeAll();
            for (Task t : app.getTasks(filter)) {
                listPanel.add(createRow(t));
                listPanel.add(Box.createVerticalStrut(10));
            }
            listPanel.revalidate();
            listPanel.repaint();
        }

        private JPanel createRow(Task t) {
            JPanel row = new JPanel(new BorderLayout(10, 0));
            row.setBackground(Color.WHITE);
            row.setBorder(new EmptyBorder(10, 10, 10, 10));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

            // Circular checkbox
            JCheckBox cb = new JCheckBox();
            cb.setOpaque(false);
            cb.setIcon(new CircleIcon(false));
            cb.setSelectedIcon(new CircleIcon(true));
            cb.setSelected(t.isDone());
            cb.addActionListener(e -> {
                app.setDone(t, cb.isSelected());
                SwingUtilities.invokeLater(() -> refresh());   // วาดใหม่ ให้ช่อง Status ตรงกับวงกลม
            });

            JLabel subject = new JLabel(t.getTitle());
            subject.setFont(FONT_BIG.deriveFont(15f));
            subject.setForeground(GRAY_TEXT);
            subject.setToolTipText(t.getTitle());              // เผื่อชื่อยาวถูกตัด

            JLabel desc = new JLabel(t.getDescription());
            desc.setFont(FONT_SMALL.deriveFont(11f));
            desc.setForeground(new Color(0x9E, 0x9E, 0x9E));
            desc.setToolTipText(t.getDescription());

            JPanel text = new JPanel(new GridLayout(2, 1));
            text.setOpaque(false);
            text.add(subject);
            text.add(desc);

            JLabel deadline = new JLabel(t.getDeadline().format(fmt));
            deadline.setFont(FONT_SMALL.deriveFont(Font.BOLD));
            deadline.setForeground(GRAY_TEXT);

            // ช่องเลือกสถานะ (Todo / In Progress / Complete)
            JComboBox<String> statusBox = new JComboBox<>(
                    new String[]{Task.TODO, Task.IN_PROGRESS, Task.COMPLETE});
            statusBox.setFont(FONT_SMALL);
            statusBox.setPreferredSize(new Dimension(105, 26));
            statusBox.setSelectedItem(t.getStatus());
            statusBox.addActionListener(e -> {
                app.setStatus(t, (String) statusBox.getSelectedItem());
                SwingUtilities.invokeLater(() -> refresh());   // วาดรายการใหม่
            });

            // สถานะกับวันที่อยู่บรรทัดเดียวกัน ทางด้านขวา
            JPanel right = new JPanel(new GridBagLayout());
            right.setOpaque(false);
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(0, 3, 0, 3);
            right.add(statusBox, c);
            right.add(deadline, c);

            row.add(cb, BorderLayout.WEST);
            row.add(text, BorderLayout.CENTER);
            row.add(right, BorderLayout.EAST);
            return row;
        }

        // Open the Add subject page (our own) and hide this page
        private void openAddSubject() {
            AddSubject page = new AddSubject(TodoList.this, app, () -> refresh());
            page.setVisible(true);
            TodoList.this.setVisible(false);
        }
    }

    // ---------- Circle icon for the checkbox ----------
    static class CircleIcon implements Icon {
        private final boolean checked;
        CircleIcon(boolean checked) { this.checked = checked; }

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
        SwingUtilities.invokeLater(() -> new TodoList(new StudyPlannerApp(new User("test"))).setVisible(true));
    }
}