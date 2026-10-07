import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Study Planner - ไฟล์เดียวจบ (Java Swing)
 * คอมไพล์:  javac TodoList.java
 * รัน:      java TodoList
 */
public class TodoList extends JFrame {

    // ---------- สี ----------
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

    // ---------- สลับหน้า ----------
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel pages = new JPanel(cardLayout);
    private TodoPage todoPage;
    private static TodoList instance;
    private JFrame calendarFrame;

    static void open(JFrame from) {
    if (instance == null) instance = new TodoList();
    instance.calendarFrame = from;
    instance.setVisible(true);
    from.setVisible(false);
}

    // กลับไปหน้า Calendar (หน้า Calendar ถูกซ่อนไว้ตอน open)
    void goBack() {
        if (calendarFrame != null) calendarFrame.setVisible(true);
        setVisible(false);
    }

    static void reset() {
    if (instance != null) { instance.dispose(); instance = null; }
}

    public TodoList() {
        setTitle("Study Planner");
        setSize(600, 800);
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

    // ---------- แถบด้านบน ----------
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
            int ok = JOptionPane.showConfirmDialog(this, "ต้องการออกจากระบบใช่ไหม?",
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

    // ปุ่มแบบข้อความล้วน (ไม่มีกรอบ)
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
    //                    หน้า To-Do List
    // =====================================================
    static class Task {
        String subject, description;
        LocalDate deadline;
        boolean done;

        Task(String subject, String description, LocalDate deadline, boolean done) {
            this.subject = subject;
            this.description = description;
            this.deadline = deadline;
            this.done = done;
        }
    }

    class TodoPage extends JPanel {
        private final List<Task> tasks = new ArrayList<>();
        private String filter = "ALL";
        private final JPanel listPanel = new JPanel();
        private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        TodoPage() {
            setBackground(BG);
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(10, 20, 20, 20));

            // ข้อมูลตัวอย่าง
            tasks.add(new Task("subject1", "Description", LocalDate.now().plusDays(1), false));
            tasks.add(new Task("subject2", "Description", LocalDate.now().plusDays(5), false));
            tasks.add(new Task("subject3", "Description", LocalDate.now().minusDays(1), true));

            // ---- ส่วนหัว: To-Do List ----
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

            // ---- การ์ดสีขาว ----
            JPanel card = new JPanel(new BorderLayout(0, 15));
            card.setBackground(Color.WHITE);
            card.setBorder(new EmptyBorder(15, 15, 15, 15));
            add(card, BorderLayout.CENTER);

            // ปุ่มกรอง
            JPanel filters = new JPanel(new GridLayout(1, 4, 10, 0));
            filters.setOpaque(false);
            filters.setPreferredSize(new Dimension(0, 70));
            filters.add(filterButton("ALL", "ALL", PINK_LINK));
            filters.add(filterButton("Done", "DONE", GREEN));
            filters.add(filterButton("<html><center>Not<br>Done</center></html>", "NOT_DONE", RED));
            filters.add(filterButton("Soon", "SOON", YELLOW));
            card.add(filters, BorderLayout.NORTH);

            // พื้นที่สีชมพู + รายการ
            JPanel pinkArea = new JPanel(new BorderLayout(0, 10));
            pinkArea.setBackground(PINK_AREA);
            pinkArea.setBorder(new EmptyBorder(15, 15, 15, 15));

            listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
            listPanel.setBackground(PINK_AREA);

            JScrollPane scroll = new JScrollPane(listPanel);
            scroll.setBorder(null);
            scroll.getViewport().setBackground(PINK_AREA);
            scroll.getVerticalScrollBar().setUnitIncrement(16);
            pinkArea.add(scroll, BorderLayout.CENTER);

            // ปุ่ม Add subject
            JButton add = new JButton("Add subject");
            add.setFont(FONT_BIG);
            add.setForeground(GRAY_TEXT);
            add.setBackground(Color.WHITE);
            add.setFocusPainted(false);
            add.setBorder(new EmptyBorder(10, 30, 10, 30));
            add.addActionListener(e -> openAddSubject());

            JPanel addWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            addWrap.setOpaque(false);
            addWrap.add(add);
            pinkArea.add(addWrap, BorderLayout.SOUTH);

            card.add(pinkArea, BorderLayout.CENTER);

            refresh();
        }

        private JButton filterButton(String text, String key, Color color) {
            JButton b = new JButton(text);
            b.setFont(FONT_BIG);
            b.setForeground(color);
            b.setBackground(new Color(0xF5, 0xF5, 0xF5));
            b.setFocusPainted(false);
            b.addActionListener(e -> {
                filter = key;
                refresh();
            });
            return b;
        }

        private boolean matchFilter(Task t) {
            long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), t.deadline);
            switch (filter) {
                case "DONE":     return t.done;
                case "NOT_DONE": return !t.done;
                case "SOON":     return !t.done && daysLeft >= 0 && daysLeft <= 3; // ภายใน 3 วัน
                default:         return true;
            }
        }

        // วาดรายการใหม่ทั้งหมด
        void refresh() {
            listPanel.removeAll();
            for (Task t : tasks) {
                if (matchFilter(t)) {
                    listPanel.add(createRow(t));
                    listPanel.add(Box.createVerticalStrut(10));
                }
            }
            listPanel.revalidate();
            listPanel.repaint();
        }

        private JPanel createRow(Task t) {
            JPanel row = new JPanel(new BorderLayout(15, 0));
            row.setBackground(Color.WHITE);
            row.setBorder(new EmptyBorder(12, 15, 12, 15));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

            // checkbox วงกลม
            JCheckBox cb = new JCheckBox();
            cb.setOpaque(false);
            cb.setIcon(new CircleIcon(false));
            cb.setSelectedIcon(new CircleIcon(true));
            cb.setSelected(t.done);
            cb.addActionListener(e -> {
                t.done = cb.isSelected();
                if (!filter.equals("ALL")) refresh();
            });

            JLabel subject = new JLabel(t.subject);
            subject.setFont(FONT_BIG);
            subject.setForeground(GRAY_TEXT);

            JLabel desc = new JLabel(t.description);
            desc.setFont(FONT_SMALL);
            desc.setForeground(new Color(0x9E, 0x9E, 0x9E));

            JPanel text = new JPanel(new GridLayout(2, 1));
            text.setOpaque(false);
            text.add(subject);
            text.add(desc);

            JLabel deadline = new JLabel(t.deadline.format(fmt));
            deadline.setFont(FONT_NORM);
            deadline.setForeground(GRAY_TEXT);

            row.add(cb, BorderLayout.WEST);
            row.add(text, BorderLayout.CENTER);
            row.add(deadline, BorderLayout.EAST);
            return row;
        }

        // เปิดหน้า Add subject (ของเราเอง) แล้วซ่อนหน้านี้ไว้
        private void openAddSubject() {
            AddSubject page = new AddSubject(TodoList.this, task -> {
                tasks.add(task);   // ได้งานใหม่จากหน้า Add subject
                refresh();
            });
            page.setVisible(true);
            TodoList.this.setVisible(false);
        }
    }

    // ---------- ไอคอนวงกลมสำหรับ checkbox ----------
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
        SwingUtilities.invokeLater(() -> new TodoList().setVisible(true));
    }
}