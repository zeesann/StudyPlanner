import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
 
public class TodoList extends JFrame {
 
    // ใส่สี
    static final Color BG        = new Color(0xFB, 0xF1, 0xF8);
    static final Color PINK_TEXT = new Color(0xE8, 0xA5, 0xBC);
    static final Color PINK_LINK = new Color(0xC7, 0x7D, 0xBE);
    static final Color PINK_AREA = new Color(0xF2, 0xD7, 0xE0);
    static final Color GRAY_TEXT = new Color(0x80, 0x80, 0x80);
    static final Color GREEN     = new Color(0x33, 0xB8, 0x33);
    static final Color RED       = new Color(0xD3, 0x2F, 0x2F);
    static final Color YELLOW    = new Color(0xE0, 0xB7, 0x2D);
 
    // ใส่ฟอนต์
    static final Font FONT_TITLE = new Font("Tahoma", Font.BOLD, 22);
    static final Font FONT_BIG   = new Font("Tahoma", Font.BOLD, 18);
    static final Font FONT_NORM  = new Font("Tahoma", Font.BOLD, 14);
    static final Font FONT_SMALL = new Font("Tahoma", Font.PLAIN, 12);
 
    // ตัวแปร
    private static TodoList instance;          // เก็บหน้านี้ไว้แค่หน้าเดียว ไม่ให้เปิดซ้ำ
    private JFrame calendarFrame;              // หน้าปฏิทิน (ซ่อนไว้ตอนเปิดหน้านี้)
    private StudyPlannerApp app;               // หลังบ้าน เก็บข้อมูลและคำนวณทั้งหมด
    private StudyPlannerApp.Filter filter = StudyPlannerApp.Filter.ALL;   // ตัวกรองที่เลือกอยู่ เริ่มที่ ALL
    private JPanel listPanel = new JPanel();   // แผ่นที่ใส่แถวงานทั้งหมด
    private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");   // รูปแบบวันที่ เช่น 15/10/2026
 
    // เปิดหน้านี้จากหน้าปฏิทิน
    static void open(JFrame from, StudyPlannerApp app) {
        if (instance == null) {
            instance = new TodoList(app);      // ยังไม่มีค่อยสร้าง ถ้ามีแล้วใช้อันเดิม
        }
        instance.calendarFrame = from;         // จำว่าเปิดมาจากหน้าไหน จะได้กลับถูก
        instance.setVisible(true);             // โชว์หน้านี้
        from.setVisible(false);                // ซ่อนหน้าปฏิทิน
    }
 
    // กลับไปหน้าปฏิทิน
    void goBack() {
        if (calendarFrame instanceof CalendarGUI) {
            ((CalendarGUI) calendarFrame).refreshSummary();   // อัพเดทตัวเลขสรุปก่อนกลับ
        }
        if (calendarFrame != null) {
            calendarFrame.setVisible(true);    // โชว์หน้าปฏิทินกลับมา
        }
        setVisible(false);                     // ซ่อนหน้านี้
    }
 
    // ล้างหน้านี้ทิ้ง (ใช้ตอนล็อคเอ้าท์ จะได้ไม่มีข้อมูลคนเก่าค้าง)
    static void reset() {
        if (instance != null) {
            instance.dispose();
            instance = null;
        }
    }
 
    // สร้างหน้าจอ
    public TodoList(StudyPlannerApp app) {
        this.app = app;
        setTitle("Study Planner");
        setSize(487, 802);                       // ขนาดเท่าหน้าปฏิทิน
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
 
        add(createTopBar(), BorderLayout.NORTH);       // แถบด้านบน
        add(createMainPanel(), BorderLayout.CENTER);   // ส่วนหลัก
 
        refresh();   // ใส่รายการงานครั้งแรก
    }
 
    // แถบด้านบน (ชื่อโปรแกรม + sign out)
    private JPanel createTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Color.WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xDD, 0xDD, 0xDD)),
                new EmptyBorder(15, 20, 15, 20)));
 
        JLabel title = new JLabel("study planner");
        title.setFont(FONT_TITLE);
        title.setForeground(PINK_TEXT);
 
        // กด sign out แล้วถามยืนยันก่อน
        JButton signOut = linkButton("sign out", PINK_TEXT);
        signOut.addActionListener(e -> {
            int ok = JOptionPane.showConfirmDialog(this, "Do you want to sign out?",
                    "Sign out", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                if (calendarFrame != null) {
                    calendarFrame.dispose();   // ปิดหน้าปฏิทินที่ซ่อนอยู่ด้วย
                }
                dispose();                     // ปิดหน้านี้
                instance = null;
                new Login().setVisible(true);  // เปิดหน้าล็อคอินใหม่
            }
        });
 
        bar.add(title, BorderLayout.WEST);
        bar.add(signOut, BorderLayout.EAST);
        return bar;
    }
 
    // ปุ่มที่เห็นแค่ตัวหนังสือ ไม่มีกรอบ ไม่มีพื้นหลัง
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
 
    // ส่วนหลัก (ปุ่มย้อนกลับ, หัวข้อ, ปุ่มกรอง, รายการงาน, ปุ่มเพิ่ม)
    private JPanel createMainPanel() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BG);
        main.setBorder(new EmptyBorder(10, 10, 15, 10));
 
        // ส่วนหัว: ปุ่มย้อนกลับ + ชื่อ "To-Do List"
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));   // เรียงจากบนลงล่าง
 
        JButton back = linkButton("←  back to calendar", PINK_LINK);
        back.setAlignmentX(Component.LEFT_ALIGNMENT);
        back.addActionListener(e -> goBack());   // กดแล้วกลับหน้าปฏิทิน
 
        JLabel title = new JLabel("To-Do List");
        title.setFont(FONT_TITLE);
        title.setForeground(new Color(0xF0, 0xB8, 0xCC));
        title.setBorder(new EmptyBorder(5, 8, 10, 0));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
 
        header.add(back);
        header.add(title);
        main.add(header, BorderLayout.NORTH);
 
        // การ์ดสีขาว
        JPanel card = new JPanel(new BorderLayout(0, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(10, 10, 10, 10));
        main.add(card, BorderLayout.CENTER);
 
        // ปุ่มกรอง 4 ปุ่ม เรียงเป็นแถวเดียว
        JPanel filters = new JPanel(new GridLayout(1, 4, 10, 0));
        filters.setOpaque(false);
        filters.setPreferredSize(new Dimension(0, 60));
        filters.add(filterButton("ALL", StudyPlannerApp.Filter.ALL, PINK_LINK));
        filters.add(filterButton("Done", StudyPlannerApp.Filter.DONE, GREEN));
        filters.add(filterButton("<html><center>Not<br>Done</center></html>", StudyPlannerApp.Filter.NOT_DONE, RED));   // ขึ้นบรรทัดใหม่ให้พอดีปุ่ม
        filters.add(filterButton("Soon", StudyPlannerApp.Filter.SOON, YELLOW));
        card.add(filters, BorderLayout.NORTH);
 
        // พื้นที่สีชมพู (ใส่รายการงานกับปุ่มเพิ่ม)
        JPanel pinkArea = new JPanel(new BorderLayout(0, 10));
        pinkArea.setBackground(PINK_AREA);
        pinkArea.setBorder(new EmptyBorder(10, 10, 10, 10));
 
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));   // แถวงานเรียงจากบนลงล่าง
        listPanel.setBackground(PINK_AREA);
 
        // ใส่แถบเลื่อน เผื่อมีงานเยอะ
        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(PINK_AREA);
        scroll.getVerticalScrollBar().setUnitIncrement(16);   // ความเร็วตอนเลื่อน
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);   // ไม่ให้เลื่อนซ้ายขวา
        pinkArea.add(scroll, BorderLayout.CENTER);
 
        // ปุ่มเพิ่มวิชา
        JButton addButton = new JButton("Add subject");
        addButton.setFont(FONT_BIG.deriveFont(16f));
        addButton.setForeground(GRAY_TEXT);
        addButton.setBackground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorder(new EmptyBorder(10, 24, 10, 24));
        addButton.addActionListener(e -> {
            AddSubject page = new AddSubject(this, app, () -> refresh());   // เพิ่มเสร็จให้วาดรายการใหม่
            page.setVisible(true);
            setVisible(false);   // ซ่อนหน้านี้ไว้ก่อน
        });
 
        // ให้ปุ่มชิดซ้าย ไม่ยืดเต็มความกว้าง
        JPanel addWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        addWrap.setOpaque(false);
        addWrap.add(addButton);
        pinkArea.add(addWrap, BorderLayout.SOUTH);
 
        card.add(pinkArea, BorderLayout.CENTER);
        return main;
    }
 
    // สร้างปุ่มกรอง 1 ปุ่ม
    private JButton filterButton(String text, StudyPlannerApp.Filter key, Color color) {
        JButton b = new JButton(text);
        b.setFont(FONT_BIG.deriveFont(16f));
        b.setForeground(color);
        b.setBackground(new Color(0xF5, 0xF5, 0xF5));
        b.setFocusPainted(false);
        b.addActionListener(e -> {
            filter = key;   // จำว่าเลือกตัวกรองอะไร
            refresh();      // วาดรายการใหม่ตามตัวกรอง
        });
        return b;
    }
 
    // วาดรายการงานใหม่ทั้งหมด
    void refresh() {
        listPanel.removeAll();   // ล้างของเก่า
        for (Task t : app.getTasks(filter)) {   // ขอรายการงานตามตัวกรองจากหลังบ้าน
            listPanel.add(createRow(t));
            listPanel.add(Box.createVerticalStrut(10));   // เว้นช่องว่างระหว่างแถว
        }
        listPanel.revalidate();
        listPanel.repaint();
    }
 
    // สร้างแถวงาน 1 แถว
    private JPanel createRow(Task t) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(Color.WHITE);
        row.setBorder(new EmptyBorder(10, 10, 10, 10));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));   // สูงสุด 70 กว้างได้เต็มที่
 
        // ช่องติ๊กวงกลม (ซ้าย)
        JCheckBox cb = new JCheckBox();
        cb.setOpaque(false);
        cb.setIcon(new CircleIcon(false));           // ยังไม่ติ๊ก = วงกลมเปล่า
        cb.setSelectedIcon(new CircleIcon(true));    // ติ๊กแล้ว = วงกลมมีจุดเขียว
        cb.setSelected(t.isDone());
        cb.addActionListener(e -> {
            app.setDone(t, cb.isSelected());   // บอกหลังบ้านว่างานนี้เสร็จหรือยัง
            refresh();
        });
 
        // ชื่อวิชา + รายละเอียด (กลาง)
        JLabel subject = new JLabel(t.getTitle());
        subject.setFont(FONT_BIG.deriveFont(15f));
        subject.setForeground(GRAY_TEXT);
        subject.setToolTipText(t.getTitle());          // เอาเมาส์ชี้แล้วโชว์ชื่อเต็ม (เผื่อยาวเกิน)
 
        JLabel desc = new JLabel(t.getDescription());
        desc.setFont(FONT_SMALL.deriveFont(11f));
        desc.setForeground(new Color(0x9E, 0x9E, 0x9E));
        desc.setToolTipText(t.getDescription());
 
        // ชื่อไว้บน รายละเอียดไว้ล่าง
        JPanel text = new JPanel(new GridLayout(2, 1));
        text.setOpaque(false);
        text.add(subject);
        text.add(desc);
 
        // กล่องเลือกสถานะ + วันครบกำหนด (ขวา)
        JComboBox<String> statusBox = new JComboBox<>(
                new String[]{Task.TODO, Task.IN_PROGRESS, Task.COMPLETE});
        statusBox.setFont(FONT_SMALL);
        statusBox.setPreferredSize(new Dimension(105, 26));
        statusBox.setSelectedItem(t.getStatus());
        statusBox.addActionListener(e -> {
            app.setStatus(t, (String) statusBox.getSelectedItem());   // เปลี่ยนสถานะงาน
            refresh();
        });
 
        JLabel deadline = new JLabel(t.getDeadline().format(fmt));   // วันครบกำหนดแบบ dd/MM/yyyy
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
 
    // ไอคอนวงกลมสำหรับช่องติ๊ก (วาดเอง)
    static class CircleIcon implements Icon {
        private boolean checked;   // true = ติ๊กแล้ว
 
        CircleIcon(boolean checked) {
            this.checked = checked;
        }
 
        // ขนาดไอคอน
        public int getIconWidth()  { return 22; }
        public int getIconHeight() { return 22; }
 
        // วาดไอคอน
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);   // ให้ขอบเรียบ
            g2.setColor(Color.DARK_GRAY);
            g2.setStroke(new BasicStroke(2f));   // ความหนาเส้น
            g2.drawOval(x + 2, y + 2, 17, 17);   // วาดวงกลมนอก
            if (checked) {
                g2.setColor(GREEN);
                g2.fillOval(x + 6, y + 6, 10, 10);   // ติ๊กแล้วถมจุดเขียวตรงกลาง
            }
            g2.dispose();
        }
    }
}
 