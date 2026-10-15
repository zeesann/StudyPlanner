
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.TextStyle;
import java.util.Locale;
 

public class CalendarGUI extends JFrame {
 
    // ใส่สี
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
 
    // ตัวแปร
    Calendar calendar = new Calendar();  //พวกแบบคำนวนปฏิทิน
 
    int allWork, done, notDone, dueSoon; 

    //หน้าจอ
    JComboBox<String> monthBox = new JComboBox<>();
    JComboBox<Integer> yearBox = new JComboBox<>();
    JPanel grid = new JPanel(new GridLayout(0, 7, 4, 6));//ตารางปฏิทิน 7 วัน
    boolean updating = false;//กันโปรแกรมทำงานวนซ้ำ
 
    //เชื่อมหลังบ้าน พวกเก็บข้อมูล
    private final StudyPlannerApp app;   
 
    public CalendarGUI(StudyPlannerApp app) {
        super("Study Planner");
        this.app = app;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(487, 802);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout()); //แบ่งหน้าต่างเป็นบนล่างซ้ายขวา
 
        add(buildTopBar(), BorderLayout.NORTH); //แถบชื่อโปรแกมกับล็อคเอ้าท์
        add(buildCenter(), BorderLayout.CENTER);//ปฏิทิน
        add(buildSummary(), BorderLayout.SOUTH);//ตารางสรุปงาน
 
        rebuildCalendar(); //วาดปฏิทิน
    }
 
    //ด้านบน พวกแถบชื่อ
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

        JLabel repassword = new JLabel("REPASSWORD");
        signOut.setFont(BOLD);
        repassword.setForeground(RED);

        
        //ขึ้นถามยืนยันว่าจะล็อคเอ้าท์มั้ย
        signOut.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (JOptionPane.showConfirmDialog(CalendarGUI.this, "Sign out?",
                        "Sign out", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    dispose(); //ปิดหน้าต่าง
                    TodoList.reset();   // ล้างข้อมูลหน้า todo
                    new Login().setVisible(true); //เปิดหน้าล็อคอินใหม่
                }
            }
        });

         repassword.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (JOptionPane.showConfirmDialog(CalendarGUI.this, "repassword ?",
                        "Sign out", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    dispose(); //ปิดหน้าต่าง
                    TodoList.reset();   
                    new ForgotPassword().setVisible(true); 
                }
            }
        });
 

        bar.add(title, BorderLayout.WEST);
        bar.add(signOut, BorderLayout.EAST);
       bar.add(repassword, BorderLayout.NORTH); 
        return bar;
    }
 
    // ตรงกลาง พวกปฏิทิน
    JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(BG);
        center.setBorder(new EmptyBorder(24, 30, 20, 30)); //เว้นขอบ
 
        //หัวข้อที่อยู่ตรงกลาง
        JLabel heading = new JLabel("your study plan", SwingConstants.CENTER);
        heading.setFont(BOLD.deriveFont(24f));
        heading.setForeground(TITLE);
        heading.setBorder(new EmptyBorder(0, 0, 20, 0));
        center.add(heading, BorderLayout.NORTH);
 
        // ที่ใส่ปฏิทิน
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(14, 14, 14, 14));
 
        // เอาไว้เลือกเดือนปี ใส่เป็นชื่อย่อเดือน
        for (Month m : Month.values())
            monthBox.addItem(m.getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
        for (int y = 2020; y <= 2035; y++) yearBox.addItem(y);
 
        JButton prev = navButton("<");
        JButton next = navButton(">");
        prev.addActionListener(e -> { calendar.prevMonth(); rebuildCalendar(); });//เดือนก่อน 
        next.addActionListener(e -> { calendar.nextMonth(); rebuildCalendar(); });//เดือนหน้า

        //เลือกเดือนหรือปีจากแถบขึ้นลง
        ActionListener comboListener = e -> {
            if (updating) return; //กันวนซ้ำ
            //ให้เดือน +1 เพราะเดือนไม่มีเดือน 0
            calendar.setMonth((Integer) yearBox.getSelectedItem(), monthBox.getSelectedIndex() + 1);
            rebuildCalendar();
        };
        monthBox.addActionListener(comboListener);
        yearBox.addActionListener(comboListener);
 
        //แถบขึ้นลงเลือกเดือนปีอยู่ตรงกลาง
        JPanel combos = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        combos.setOpaque(false);
        combos.add(monthBox);
        combos.add(yearBox);
        //จัดว่าควรวางไว้ไหน
        JPanel nav = new JPanel(new BorderLayout());
        nav.setOpaque(false);
        nav.add(prev, BorderLayout.WEST);
        nav.add(combos, BorderLayout.CENTER);
        nav.add(next, BorderLayout.EAST);
        card.add(nav, BorderLayout.NORTH);
        //ส่วนปฏิทินไว้ด้านล่างแถบ
        grid.setOpaque(false);
        card.add(grid, BorderLayout.CENTER);
 
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(card, BorderLayout.NORTH); 
        center.add(wrap, BorderLayout.CENTER);
        return center;
    }
 
    //สร้างปุ่มซ้ายขวาแบบไม่มีพื้นหลัง ไม่มีกรอบ เอาเป็นฟังก์ชั่นจะได้ไม่ต้องเขียนซ้ำ
    JButton navButton(String text) {
        JButton b = new JButton(text);
        b.setFont(BOLD);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
 
    //วาดปฏิทิน
    void rebuildCalendar() {
        //ให้แถบขึ้นลงตรงกับเดือนปีปัจจุบัน
        updating = true; //เปลี่ยนค่าเองโดยไม่ต้องทำตามลิสเซนเนอร์
        monthBox.setSelectedIndex(calendar.getMonthNumber() - 1);
        yearBox.setSelectedItem(calendar.getYear());
        updating = false;
 
        grid.removeAll();
        //ใส่หัวตารางวันในสัปดาห์
        String[] heads = {"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"};
        for (String h : heads) {
            JLabel l = new JLabel(h, SwingConstants.CENTER);
            l.setFont(new Font("SansSerif", Font.PLAIN, 12));
            l.setForeground(new Color(0x888888));
            grid.add(l);
        }
        //ใส่วันที่
        int offset = calendar.getBlankDays();
        int daysInMonth = calendar.getDaysInMonth();
        //วนทีละช่องของตาราง
        for (int i = 0; i < calendar.getTotalCells(); i++) {
            int dayNum = i - offset + 1; //แปลงเลขช่องเป็นวันที่
            if (dayNum < 1) {
                grid.add(new JLabel());         //ช่องว่างก่อนวันที่ 1              
            } else if (dayNum > daysInMonth) {
                //วันของเดือนหน้าให้ใส่สีจาง
                JLabel l = new JLabel(String.valueOf(dayNum - daysInMonth), SwingConstants.CENTER);
                l.setFont(new Font("SansSerif", Font.PLAIN, 15));
                l.setForeground(GREY);
                grid.add(l);
            } else {
                grid.add(makeDayButton(calendar.getDate(dayNum)));
            }
        }
        //สั่งจัดวางและวาดหน้าจอใหม่
        grid.revalidate();
        grid.repaint();
    }
 
    // นับปุ่มแต่ละวัน รับวันที่ให้จำว่าตัวเองคือวันไหน
    JButton makeDayButton(LocalDate date) {
        JButton b = new JButton(String.valueOf(date.getDayOfMonth()));//แสดงเลขวัน
        b.setFont(new Font("SansSerif", Font.PLAIN, 15));
        b.setPreferredSize(new Dimension(44, 40));
        b.setBackground(Color.WHITE);
        b.setForeground(new Color(0x444444));
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setMargin(new Insets(0, 0, 0, 0));  
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
 
        if (date.equals(calendar.getSelectedDate())) {
            //วันที่ถูกเลือกทำพื้นดำ ตัวอักษรขาว
            b.setBackground(DARK);
            b.setForeground(Color.WHITE);
        } else {
            // วันแดงคือไม่เสร็จวันเขียวคือเสร็จ
            StudyPlannerApp.DayStatus st = app.getDayStatus(date); //ดูหลังบ้านว่าวันนี้มีงานมั้ย
            if (st == StudyPlannerApp.DayStatus.PENDING) {
                b.setForeground(RED);
                b.setFont(b.getFont().deriveFont(Font.BOLD));
            } else if (st != StudyPlannerApp.DayStatus.NONE) {
                b.setForeground(GREEN);
                b.setFont(b.getFont().deriveFont(Font.BOLD));
            }
        }
 
        //คลิก 1 ครั้งคือเลือกวัน
        b.addActionListener(e -> onDayClick(date));
 
        
        return b;
    }
 
    // คลิกวันไหนให้จำว่าวันนั้นโดนเลือก
    void onDayClick(LocalDate d) {
        calendar.setSelectedDate(d);
        rebuildCalendar();     
    }
 
 
    //ป้ายเลข 4 อันเก็บไว้เป็นตัวแปร
    JLabel lblAllWork, lblDone, lblNotDone, lblDueSoon;
 
    // สรุปตารางด้านล่าง
    JPanel buildSummary() {
        JPanel p = new JPanel(new GridLayout(4, 1));
        p.setBackground(Color.WHITE);
        p.setBorder(new MatteBorder(1, 0, 1, 0, LINE));
        p.setPreferredSize(new Dimension(0, 250));
 
        //สร้างป้ายเปล่าก่อน
        lblAllWork = new JLabel();
        lblDone = new JLabel();
        lblNotDone = new JLabel();
        lblDueSoon = new JLabel();
 
        //ทำให้ตรง all work คลิกได้
        JPanel allRow = summaryRow("All work", lblAllWork, PINK, PINK, false);
        allRow.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        allRow.addMouseListener(new MouseAdapter() {
            //เพื่อไปหน้า todo list
            public void mouseClicked(MouseEvent e) { TodoList.open(CalendarGUI.this, app); }
        });
        p.add(allRow);
        p.add(summaryRow("Done", lblDone, GREEN, GREEN, true));
        p.add(summaryRow("Not Done", lblNotDone, RED, RED, true));
        p.add(summaryRow("Due soon", lblDueSoon, YELLOW, YELLOW, true));
 
        refreshSummary(); //ใส่ตัวเลขทันทีที่เปิดหน้า
        return p;
    }
 
    //สร้าง 1 แถว ชื่ออยู่ซ้าย เลขอยู่ขวา พร้อมเส้นคั่นระหว่างแถว
    JPanel summaryRow(String label, JLabel valueLabel, Color c1, Color c2, boolean topLine) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.setBorder(new CompoundBorder(
                topLine ? new MatteBorder(1, 0, 0, 0, LINE) : new EmptyBorder(0, 0, 0, 0),
                new EmptyBorder(0, 40, 0, 60)));
 
        //ซ้าย
        JLabel l = new JLabel(label);
        l.setFont(BOLD.deriveFont(17f));
        l.setForeground(c1);
 
        //ขวา
        valueLabel.setFont(BOLD.deriveFont(17f));
        valueLabel.setForeground(c2);
 
        row.add(l, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.EAST);
        return row;
    }
 
    // ดึงเลขล่าสุดมาอัพเดท เรียกตอนสร้างหน้ากับตอนกลับมาจากหน้า todo
    void refreshSummary() {
        Dashboard stats = app.getStats();//ของานล่าสุดจากหลังบ้าน
        allWork = stats.getTotal();
        done = stats.getDone();
        notDone = stats.getNotDone();
        dueSoon = stats.getDueSoon();
 
        lblAllWork.setText(allWork + "  \u2192");
        lblDone.setText(String.valueOf(done));
        lblNotDone.setText(String.valueOf(notDone));
        lblDueSoon.setText(String.valueOf(dueSoon));
        rebuildCalendar();   // สีของวันอาจจะโดนเปลี่ยนเลยต้องสร้างปฏิทินใหม่
    }
 
}
 