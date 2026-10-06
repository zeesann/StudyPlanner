
    
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.TextStyle;
import java.util.*;

/**
 * Study Planner - Java Swing (ไฟล์เดียว ไม่ใช้ XML)
 * คอมไพล์: javac StudyPlanner.java
 * รัน:      java StudyPlanner
 */
public class  calendar_gui extends JFrame {

    // ---------- สี ----------
    static final Color BG      = new Color(0xFDF3FD);
    static final Color PINK    = new Color(0xF0A8C8);
    static final Color TITLE   = new Color(0xE58AC0);
    static final Color DARK    = new Color(0x2B2B2B);
    static final Color RANGE   = new Color(0xF2F2F2);
    static final Color GREY    = new Color(0xBBBBBB);
    static final Color LINE    = new Color(0xDDDDDD);
    static final Color GREEN   = new Color(0x00C800);
    static final Color RED     = new Color(0xE81B1B);
    static final Color YELLOW  = new Color(0xFFC800);

    static final Font BOLD = new Font("SansSerif", Font.BOLD, 16);

    // ---------- ข้อมูล ----------
    YearMonth current = YearMonth.of(2025, 9);
    LocalDate rangeStart = LocalDate.of(2025, 9, 9);
    LocalDate rangeEnd   = LocalDate.of(2025, 9, 13);
    final Set<LocalDate> taskDays = new HashSet<>(Arrays.asList(
            LocalDate.of(2025, 9, 10), LocalDate.of(2025, 9, 18),
            LocalDate.of(2025, 9, 25), LocalDate.of(2025, 9, 26),
            LocalDate.of(2025, 9, 30)));

    int allWork = 8, done = 1, notDone = 7, dueSoon = 4;

    // ---------- คอมโพเนนต์ ----------
    JComboBox<String> monthBox = new JComboBox<>();
    JComboBox<Integer> yearBox = new JComboBox<>();
    JPanel grid = new JPanel(new GridLayout(0, 7, 4, 6));
    boolean updating = false;

    public calendar_gui() {
        super("Study Planner");
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

    // ---------- แถบบน ----------
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
                if (JOptionPane.showConfirmDialog(calendar_gui.this, "Sign out?",
                        "Sign out", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    dispose();
                }
            }
        });

        bar.add(title, BorderLayout.WEST);
        bar.add(signOut, BorderLayout.EAST);
        return bar;
    }

    // ---------- ส่วนกลาง (หัวข้อ + ปฏิทิน) ----------
    JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(BG);
        center.setBorder(new EmptyBorder(24, 30, 20, 30));

        JLabel heading = new JLabel("your study plan", SwingConstants.CENTER);
        heading.setFont(BOLD.deriveFont(24f));
        heading.setForeground(TITLE);
        heading.setBorder(new EmptyBorder(0, 0, 20, 0));
        center.add(heading, BorderLayout.NORTH);

        // การ์ดสีขาวของปฏิทิน
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(14, 14, 14, 14));

        // แถวเลือกเดือน/ปี
        for (Month m : Month.values())
            monthBox.addItem(m.getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
        for (int y = 2020; y <= 2035; y++) yearBox.addItem(y);

        JButton prev = navButton("<");
        JButton next = navButton(">");
        prev.addActionListener(e -> { current = current.minusMonths(1); rebuildCalendar(); });
        next.addActionListener(e -> { current = current.plusMonths(1);  rebuildCalendar(); });

        ActionListener comboListener = e -> {
            if (updating) return;
            current = YearMonth.of((Integer) yearBox.getSelectedItem(),
                    monthBox.getSelectedIndex() + 1);
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

    // ---------- สร้างปฏิทินใหม่ ----------
    void rebuildCalendar() {
        updating = true;
        monthBox.setSelectedIndex(current.getMonthValue() - 1);
        yearBox.setSelectedItem(current.getYear());
        updating = false;

        grid.removeAll();

        String[] heads = {"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"};
        for (String h : heads) {
            JLabel l = new JLabel(h, SwingConstants.CENTER);
            l.setFont(new Font("SansSerif", Font.PLAIN, 12));
            l.setForeground(new Color(0x888888));
            grid.add(l);
        }

        int offset = current.atDay(1).getDayOfWeek().getValue() % 7; // อาทิตย์ = 0
        int total = (int) Math.ceil((offset + current.lengthOfMonth()) / 7.0) * 7;

        for (int i = 0; i < total; i++) {
            int dayNum = i - offset + 1;
            if (dayNum < 1) {
                grid.add(new JLabel());                       // ช่องว่างก่อนวันที่ 1
            } else if (dayNum > current.lengthOfMonth()) {
                LocalDate d = current.plusMonths(1).atDay(dayNum - current.lengthOfMonth());
                grid.add(new DayCell(d, false));              // วันของเดือนถัดไป (สีจาง)
            } else {
                grid.add(new DayCell(current.atDay(dayNum), true));
            }
        }
        grid.revalidate();
        grid.repaint();
    }

    // ---------- ช่องวันที่ ----------
    class DayCell extends JPanel {
        final LocalDate date;
        final boolean inMonth;

        DayCell(LocalDate date, boolean inMonth) {
            this.date = date;
            this.inMonth = inMonth;
            setOpaque(false);
            setPreferredSize(new Dimension(44, 40));
            if (inMonth) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) { onDayClick(DayCell.this.date); }
                });
            }
        }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            boolean isEdge = date.equals(rangeStart) || date.equals(rangeEnd);
            boolean inRange = !date.isBefore(rangeStart) && !date.isAfter(rangeEnd);

            if (inMonth && inRange) {
                g.setColor(isEdge ? DARK : RANGE);
                g.fillRoundRect(0, 0, getWidth(), getHeight() - 2, 10, 10);
            }

            g.setFont(new Font("SansSerif", Font.PLAIN, 15));
            g.setColor(!inMonth ? GREY : (isEdge ? Color.WHITE : new Color(0x444444)));
            String s = String.valueOf(date.getDayOfMonth());
            FontMetrics fm = g.getFontMetrics();
            g.drawString(s, (getWidth() - fm.stringWidth(s)) / 2,
                    (getHeight() - 2 - fm.getHeight()) / 2 + fm.getAscent());

            if (inMonth && taskDays.contains(date)) {       // จุดแดง = มีงาน
                g.setColor(Color.RED);
                g.fillOval(getWidth() / 2 - 2, getHeight() - 9, 4, 4);
            }
            g.dispose();
        }
    }

    // คลิกครั้งแรก = วันเริ่ม, คลิกครั้งที่สอง = วันจบ
    boolean pickingEnd = false;
    void onDayClick(LocalDate d) {
        if (!pickingEnd) {
            rangeStart = d;
            rangeEnd = d;
            pickingEnd = true;
        } else {
            if (d.isBefore(rangeStart)) { rangeEnd = rangeStart; rangeStart = d; }
            else rangeEnd = d;
            pickingEnd = false;
        }
        grid.repaint();
    }

    // ---------- ตารางสรุปด้านล่าง ----------
    JPanel buildSummary() {
        JPanel p = new JPanel(new GridLayout(4, 1));
        p.setBackground(Color.WHITE);
        p.setBorder(new MatteBorder(1, 0, 1, 0, LINE));
        p.setPreferredSize(new Dimension(0, 250));

        p.add(summaryRow("All work", allWork + "  \u2192", PINK, PINK, false));
        p.add(summaryRow("Done", String.valueOf(done), GREEN, GREEN, true));
        p.add(summaryRow("Not Done", String.valueOf(notDone), RED, RED, true));
        p.add(summaryRow("Due soon", String.valueOf(dueSoon), YELLOW, YELLOW, true));
        return p;
    }

    JPanel summaryRow(String label, String value, Color c1, Color c2, boolean topLine) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.setBorder(new CompoundBorder(
                topLine ? new MatteBorder(1, 0, 0, 0, LINE) : new EmptyBorder(0, 0, 0, 0),
                new EmptyBorder(0, 40, 0, 60)));

        JLabel l = new JLabel(label);
        l.setFont(BOLD.deriveFont(17f));
        l.setForeground(c1);

        JLabel v = new JLabel(value);
        v.setFont(BOLD.deriveFont(17f));
        v.setForeground(c2);

        row.add(l, BorderLayout.WEST);
        row.add(v, BorderLayout.EAST);
        return row;
    }

    // ---------- main ----------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new calendar_gui().setVisible(true));
    }
}



