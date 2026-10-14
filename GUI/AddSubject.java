import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import javax.swing.*;

/**
 * ============================================================
 *  คลาส AddSubject  (ชั้น "หน้าจอเพิ่มงาน" หรือ View)
 * ============================================================
 *  หน้าที่: หน้าต่าง (JFrame) สำหรับกรอกข้อมูลงานใหม่ แล้วกดบันทึก
 *    ช่องที่ให้กรอก: ชื่อวิชา, รายละเอียด, วันกำหนดส่ง (เลือกจากปฏิทิน), สถานะ
 *
 *  ลำดับการทำงานโดยสรุป:
 *    1) หน้าอื่น (To-Do หรือ Calendar) เปิดหน้านี้ขึ้นมา
 *    2) ผู้ใช้กรอกข้อมูล + กดช่องวันที่เพื่อเปิดปฏิทินเลือกวัน
 *    3) กด "Save subject" -> ตรวจข้อมูล -> เรียก app.addTask(...) เพื่อบันทึก
 *    4) เรียก onSaved เพื่อบอกหน้าเดิมให้วาดหน้าจอใหม่
 *    5) ปิดหน้านี้ -> หน้าเดิมกลับมาแสดงอีกครั้งอัตโนมัติ
 *
 *  ภายในคลาสมีคลาสย่อย CalendarDialog (ปฏิทินป๊อปอัป) อยู่ด้านล่าง
 */
public class AddSubject extends JFrame {

    // ---------- สีและฟอนต์ ----------
    static final Color PINK = new Color(240, 143, 179);                        // สีชมพูของธีม
    static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");  // รูปแบบวันที่ที่แสดง
    static final Font THAI_FONT = new Font("Tahoma", Font.PLAIN, 14);   // ฟอนต์ที่แสดงภาษาไทยได้

    // ---------- ช่องกรอกข้อมูล ----------
    JTextField nameField = new JTextField();       // ช่องชื่อวิชา
    JTextField descField = new JTextField();       // ช่องรายละเอียด
    JTextField deadlineField = new JTextField();   // ช่องแสดงวันที่ (พิมพ์เองไม่ได้ ต้องเลือกจากปฏิทิน)
    JComboBox<String> statusBox = new JComboBox<>(new String[]{Task.TODO, Task.IN_PROGRESS, Task.COMPLETE}); // ตัวเลือกสถานะ
    LocalDate deadlineDate = null;                 // วันที่ที่เลือกจริง (null = ยังไม่ได้เลือก)

    // ---------- ตัวแปรอื่นๆ ----------
    JFrame parent;                // หน้าที่เปิดหน้านี้ขึ้นมา (To-Do หรือ Calendar)
    StudyPlannerApp app;          // ตัวที่ใช้บันทึกงาน
    Runnable onSaved;             // โค้ดที่จะสั่งให้หน้าเดิมวาดใหม่หลังบันทึกเสร็จ

    // ตัวสร้างแบบไม่มีพารามิเตอร์: ใช้ตอนรันหน้านี้เดี่ยวๆ เพื่อทดสอบ
    public AddSubject() {
        this(null, null, null);
    }

    // ตัวสร้างแบบไม่กำหนดวันเริ่มต้น
    public AddSubject(JFrame parent, StudyPlannerApp app, Runnable onSaved) {
        this(parent, app, onSaved, null);
    }

    // ตัวสร้างหลัก
    // initialDate = วันที่จะใส่ให้อัตโนมัติตอนเปิด (เช่น วันที่ที่เลือกไว้บนปฏิทิน) หรือ null
    public AddSubject(JFrame parent, StudyPlannerApp app, Runnable onSaved, LocalDate initialDate) {
        this.parent = parent;
        this.app = app;
        this.onSaved = onSaved;

        // ตั้งค่าหน้าต่าง: ชื่อ ขนาด และให้อยู่กลางจอ
        setTitle("Add subject");
        setSize(340, 500);
        setLocationRelativeTo(null);

        if (parent == null) {
            // รันหน้านี้เดี่ยวๆ: กดปิดแล้วให้โปรแกรมจบเลย
            setDefaultCloseOperation(EXIT_ON_CLOSE);
        } else {
            // มีหน้าแม่: ปิดแค่หน้านี้ (DISPOSE) ไม่ปิดทั้งโปรแกรม
            // และเมื่อหน้านี้ปิด (ไม่ว่าจะกดกากบาทหรือหลัง Save) ให้หน้าแม่แสดงขึ้นมาอีกครั้ง
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            addWindowListener(new WindowAdapter() {
                public void windowClosed(WindowEvent e) {
                    parent.setVisible(true);
                }
            });
        }

        // ตั้งฟอนต์ที่แสดงภาษาไทยได้ให้ทุกช่องกรอก
        nameField.setFont(THAI_FONT);
        descField.setFont(THAI_FONT);
        deadlineField.setFont(THAI_FONT);
        statusBox.setFont(THAI_FONT);

        // ถ้ามีวันเริ่มต้นส่งมา ให้ใส่ในช่องวันที่ทันที
        if (initialDate != null) {
            deadlineDate = initialDate;
            deadlineField.setText(initialDate.format(FMT));
        }

        // แผงหลัก: เรียงของจากบนลงล่าง (Y_AXIS) พื้นหลังขาว มีขอบ 20px
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // หัวข้อของหน้า
        JLabel title = new JLabel("add subject");
        title.setFont(new Font("Tahoma", Font.BOLD, 22));
        title.setForeground(new Color(247, 182, 205));
        panel.add(title);

        // ช่องวันที่ = ช่องข้อความ (พิมพ์ไม่ได้) + ปุ่มปฏิทิน วางเคียงกัน
        deadlineField.setEditable(false);
        deadlineField.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));   // เมาส์เป็นรูปมือ
        JButton calBtn = new JButton("📅");
        JPanel deadlinePanel = new JPanel(new BorderLayout(5, 0));
        deadlinePanel.setOpaque(false);
        deadlinePanel.add(deadlineField, BorderLayout.CENTER);   // ช่องข้อความอยู่ตรงกลาง (ขยายเต็ม)
        deadlinePanel.add(calBtn, BorderLayout.EAST);            // ปุ่มอยู่ด้านขวา

        // กดที่ช่องวันที่ หรือกดปุ่มปฏิทิน -> เปิดปฏิทินให้เลือกวัน
        calBtn.addActionListener(e -> openCalendar());
        deadlineField.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                openCalendar();
            }
        });

        // เพิ่มแต่ละช่อง (ป้ายชื่อ + ช่องกรอก) ลงฟอร์ม
        addField(panel, "Subject Name", nameField);
        addField(panel, "Description", descField);
        addField(panel, "Deadline", deadlinePanel);
        addField(panel, "Status", statusBox);

        // ปุ่มบันทึก: สีชมพู ตัวอักษรขาว กว้างเต็มแถว
        JButton saveBtn = new JButton("Save subject");
        saveBtn.setBackground(PINK);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);                                      // ไม่วาดกรอบโฟกัส
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        saveBtn.addActionListener(e -> save());                              // กดแล้วเรียก save()

        panel.add(Box.createVerticalStrut(25));   // เว้นช่องว่างแนวตั้ง 25px
        panel.add(saveBtn);
        add(panel);                               // ใส่แผงลงหน้าต่าง
    }

    // ============================================================
    //  บันทึกงาน (ทำงานเมื่อกดปุ่ม Save subject)
    // ============================================================
    void save() {
        // ตรวจ 1: ต้องกรอกชื่อวิชา
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a subject name");
            return;   // หยุด ไม่บันทึก
        }
        // ตรวจ 2: ต้องเลือกวันกำหนดส่ง
        if (deadlineDate == null) {
            JOptionPane.showMessageDialog(this, "Please select a deadline");
            return;
        }
        // ส่งข้อมูลให้ชั้น App บันทึก (app เป็น null ได้ตอนรันทดสอบเดี่ยวๆ)
        if (app != null) {
            app.addTask(name, descField.getText().trim(), deadlineDate, (String) statusBox.getSelectedItem());
        }
        // บอกหน้าเดิมให้วาดรายการใหม่
        if (onSaved != null) {
            onSaved.run();
        }
        dispose();      // ปิดหน้านี้ -> windowClosed ทำงาน -> หน้าเดิมแสดงกลับมา
    }

    // ============================================================
    //  เปิดปฏิทินป๊อปอัป
    // ============================================================
    void openCalendar() {
        CalendarDialog dialog = new CalendarDialog(this, deadlineDate);
        dialog.setVisible(true);                     // โค้ดจะรอตรงนี้จนกว่าป๊อปอัปจะถูกปิด
        LocalDate picked = dialog.getSelected();     // วันที่ที่ผู้ใช้เลือก (null ถ้าปิดโดยไม่เลือก)
        if (picked != null) {
            deadlineDate = picked;
            deadlineField.setText(picked.format(FMT));   // แสดงในช่องรูปแบบ dd/MM/yyyy
        }
    }

    // ============================================================
    //  เมธอดช่วย: เพิ่ม "ป้ายชื่อ + ช่องกรอก" ลงฟอร์ม
    // ============================================================
    void addField(JPanel panel, String text, JComponent field) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Tahoma", Font.BOLD, 15));
        label.setForeground(Color.GRAY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);      // ชิดซ้าย
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));   // กว้างเต็ม สูงสุด 35px
        panel.add(Box.createVerticalStrut(15));   // เว้นระยะก่อนป้าย
        panel.add(label);
        panel.add(Box.createVerticalStrut(5));    // เว้นระยะระหว่างป้ายกับช่อง
        panel.add(field);
    }

    // =====================================================
    //                  ปฏิทินป๊อปอัป
    // =====================================================
    // คลาสย่อย (static nested class) ที่เป็นหน้าต่างเล็กๆ ให้เลือกวันที่
    static class CalendarDialog extends JDialog {
        YearMonth month;                  // เดือน/ปี ที่กำลังแสดงอยู่
        LocalDate selected = null;        // วันที่ที่ผู้ใช้กดเลือก (null = ยังไม่เลือก)
        JLabel monthLabel = new JLabel("", SwingConstants.CENTER);   // ป้ายชื่อเดือน เช่น "October 2026"
        JPanel grid = new JPanel(new GridLayout(0, 7, 3, 3));        // ตาราง 7 คอลัมน์ (วันอาทิตย์-เสาร์)

        CalendarDialog(Frame owner, LocalDate initial) {
            super(owner, "Select deadline", true);       // true = ต้องปิดป๊อปอัปนี้ก่อนถึงจะใช้หน้าอื่นได้ (modal)

            // เริ่มแสดงที่เดือนของวันที่เดิม ถ้าไม่มีให้แสดงเดือนปัจจุบัน
            if (initial != null) {
                month = YearMonth.from(initial);
            } else {
                month = YearMonth.now();
            }

            // ปุ่มเลื่อนเดือนก่อนหน้า (<) และถัดไป (>) แล้ววาดตารางใหม่
            JButton prev = new JButton("<");
            JButton next = new JButton(">");
            prev.addActionListener(e -> {
                month = month.minusMonths(1);
                draw();
            });
            next.addActionListener(e -> {
                month = month.plusMonths(1);
                draw();
            });

            // ส่วนหัว: ปุ่ม < | ชื่อเดือน | ปุ่ม >
            monthLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
            JPanel header = new JPanel(new BorderLayout());
            header.add(prev, BorderLayout.WEST);
            header.add(monthLabel, BorderLayout.CENTER);
            header.add(next, BorderLayout.EAST);

            grid.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

            // ประกอบหน้าต่าง: หัวอยู่บน ตารางวันอยู่ตรงกลาง
            JPanel content = new JPanel(new BorderLayout(5, 5));
            content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            content.add(header, BorderLayout.NORTH);
            content.add(grid, BorderLayout.CENTER);
            add(content);

            draw();                          // วาดวันที่ของเดือนแรก
            setSize(320, 320);
            setLocationRelativeTo(owner);    // แสดงตรงกลางหน้าที่เปิดมัน
        }

        // วาดวันทั้งหมดของเดือนปัจจุบันลงตาราง (ถูกเรียกทุกครั้งที่เปลี่ยนเดือน)
        void draw() {
            grid.removeAll();   // ล้างของเก่าก่อน

            // แสดงชื่อเดือนภาษาอังกฤษ + ปี เช่น "October 2026"
            monthLabel.setText(month.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                    + " " + month.getYear());

            // แถวหัวตาราง: ชื่อวันย่อ
            String[] days = {"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"};
            for (String d : days) {
                JLabel l = new JLabel(d, SwingConstants.CENTER);
                l.setForeground(Color.GRAY);
                grid.add(l);
            }

            // ช่องว่างก่อนวันที่ 1 เพื่อให้วันที่ 1 ตรงกับวันในสัปดาห์ที่ถูกต้อง
            // getValue() ของ Java: จันทร์=1 ... อาทิตย์=7 ; หาร 7 เอาเศษ ทำให้อาทิตย์=0
            int blanks = month.atDay(1).getDayOfWeek().getValue() % 7;   // Sunday = 0
            for (int i = 0; i < blanks; i++) {
                grid.add(new JLabel(""));
            }

            // สร้างปุ่มวันที่ 1 ถึงวันสุดท้ายของเดือน
            for (int day = 1; day <= month.lengthOfMonth(); day++) {
                LocalDate date = month.atDay(day);
                JButton b = new JButton(String.valueOf(day));
                b.setMargin(new Insets(2, 2, 2, 2));
                b.setFocusPainted(false);
                if (date.equals(LocalDate.now())) {
                    b.setBackground(PINK);        // วันนี้ = ไฮไลต์สีชมพู
                    b.setForeground(Color.WHITE);
                }
                // กดวันไหน -> จำวันนั้นไว้ แล้วปิดป๊อปอัป
                b.addActionListener(e -> {
                    selected = date;
                    dispose();
                });
                grid.add(b);
            }
            grid.revalidate();   // คำนวณเลย์เอาต์ใหม่
            grid.repaint();      // วาดหน้าจอใหม่
        }

        // คืนวันที่ที่ถูกเลือก (null ถ้าผู้ใช้ปิดป๊อปอัปโดยไม่เลือก)
        LocalDate getSelected() {
            return selected;
        }
    }

    // จุดเริ่มต้นสำหรับรันหน้านี้เดี่ยวๆ เพื่อทดสอบ
    public static void main(String[] args) {
        new AddSubject().setVisible(true);
    }
}