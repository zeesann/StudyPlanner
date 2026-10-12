import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import javax.swing.*;

public class AddSubject extends JFrame {

    static final Color PINK = new Color(240, 143, 179);
    static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ฟอนต์ที่แสดงภาษาไทยได้ (ใช้กับช่องกรอกทุกช่อง)
    static final Font THAI_FONT = new Font("Tahoma", Font.PLAIN, 14);

    JTextField nameField = new JTextField();
    JTextField descField = new JTextField();
    JTextField deadlineField = new JTextField();
    JComboBox<String> statusBox = new JComboBox<>(new String[]{Task.TODO, Task.IN_PROGRESS, Task.COMPLETE});
    LocalDate deadlineDate = null;

    JFrame parent;                         // The page (To-Do or Calendar) that opened this page
    StudyPlannerApp app;                   // Logic layer: the task is saved through this
    Runnable onSaved;                      // Tells the To-Do page to redraw its list

    // Used when running this page standalone for testing
    public AddSubject() {
        this(null, null, null);
    }

    public AddSubject(JFrame parent, StudyPlannerApp app, Runnable onSaved) {
        this(parent, app, onSaved, null);
    }

    // initialDate = deadline to pre-fill (e.g. the day selected on the calendar), or null
    public AddSubject(JFrame parent, StudyPlannerApp app, Runnable onSaved, LocalDate initialDate) {
        this.parent = parent;
        this.app = app;
        this.onSaved = onSaved;

        setTitle("Add subject");
        setSize(340, 500);
        setLocationRelativeTo(null);

        if (parent == null) {
            setDefaultCloseOperation(EXIT_ON_CLOSE);
        } else {
            // Close this page (X button or after Save) then show the To-Do page again
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    AddSubject.this.parent.setVisible(true);
                }
            });
        }

        // ตั้งฟอนต์ไทยให้ช่องกรอก เพื่อให้พิมพ์และแสดงภาษาไทยได้
        nameField.setFont(THAI_FONT);
        descField.setFont(THAI_FONT);
        deadlineField.setFont(THAI_FONT);
        statusBox.setFont(THAI_FONT);

        if (initialDate != null) {
            deadlineDate = initialDate;
            deadlineField.setText(initialDate.format(FMT));
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("add subject");
        title.setFont(new Font("Tahoma", Font.BOLD, 22));
        title.setForeground(new Color(247, 182, 205));
        panel.add(title);

        // Deadline = text field (read only) + calendar button
        deadlineField.setEditable(false);
        deadlineField.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        JButton calBtn = new JButton("📅");
        JPanel deadlinePanel = new JPanel(new BorderLayout(5, 0));
        deadlinePanel.setOpaque(false);
        deadlinePanel.add(deadlineField, BorderLayout.CENTER);
        deadlinePanel.add(calBtn, BorderLayout.EAST);

        // Open the calendar when clicking the field or the button
        calBtn.addActionListener(e -> openCalendar());
        deadlineField.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { openCalendar(); }
        });

        addField(panel, "Subject Name", nameField);
        addField(panel, "Description", descField);
        addField(panel, "Deadline", deadlinePanel);
        addField(panel, "Status", statusBox);

        JButton saveBtn = new JButton("Save subject");
        saveBtn.setBackground(PINK);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        saveBtn.addActionListener(e -> save());

        panel.add(Box.createVerticalStrut(25));
        panel.add(saveBtn);
        add(panel);
    }

    void save() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a subject name");
            return;
        }
        if (deadlineDate == null) {
            JOptionPane.showMessageDialog(this, "Please select a deadline");
            return;
        }
        if (app != null) {
            app.addTask(name, descField.getText().trim(), deadlineDate, (String) statusBox.getSelectedItem());
        }
        if (onSaved != null) onSaved.run();
        dispose();   // Close this page -> windowClosed will reopen the To-Do page
    }

    void openCalendar() {
        CalendarDialog dialog = new CalendarDialog(this, deadlineDate);
        dialog.setVisible(true);                       // wait until closed
        LocalDate picked = dialog.getSelected();
        if (picked != null) {
            deadlineDate = picked;
            deadlineField.setText(picked.format(FMT));
        }
    }

    void addField(JPanel panel, String text, JComponent field) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Tahoma", Font.BOLD, 15));
        label.setForeground(Color.GRAY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        panel.add(Box.createVerticalStrut(15));
        panel.add(label);
        panel.add(Box.createVerticalStrut(5));
        panel.add(field);
    }

    // ---------- Calendar popup ----------
    static class CalendarDialog extends JDialog {
        YearMonth month;
        LocalDate selected = null;
        JLabel monthLabel = new JLabel("", SwingConstants.CENTER);
        JPanel grid = new JPanel(new GridLayout(0, 7, 3, 3));

        CalendarDialog(Frame owner, LocalDate initial) {
            super(owner, "Select deadline", true);       // true = modal
            month = YearMonth.from(initial != null ? initial : LocalDate.now());

            JButton prev = new JButton("<");
            JButton next = new JButton(">");
            prev.addActionListener(e -> { month = month.minusMonths(1); draw(); });
            next.addActionListener(e -> { month = month.plusMonths(1); draw(); });

            monthLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
            JPanel header = new JPanel(new BorderLayout());
            header.add(prev, BorderLayout.WEST);
            header.add(monthLabel, BorderLayout.CENTER);
            header.add(next, BorderLayout.EAST);

            grid.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
            setLayout(new BorderLayout(5, 5));
            ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            add(header, BorderLayout.NORTH);
            add(grid, BorderLayout.CENTER);

            draw();
            setSize(320, 320);
            setLocationRelativeTo(owner);
        }

        void draw() {
            grid.removeAll();
            monthLabel.setText(month.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                    + " " + month.getYear());

            String[] days = {"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"};
            for (String d : days) {
                JLabel l = new JLabel(d, SwingConstants.CENTER);
                l.setForeground(Color.GRAY);
                grid.add(l);
            }

            int blanks = month.atDay(1).getDayOfWeek().getValue() % 7;   // Sunday = 0
            for (int i = 0; i < blanks; i++) grid.add(new JLabel(""));

            for (int day = 1; day <= month.lengthOfMonth(); day++) {
                LocalDate date = month.atDay(day);
                JButton b = new JButton(String.valueOf(day));
                b.setMargin(new Insets(2, 2, 2, 2));
                b.setFocusPainted(false);
                if (date.equals(LocalDate.now())) {
                    b.setBackground(PINK);
                    b.setForeground(Color.WHITE);
                }
                b.addActionListener(e -> { selected = date; dispose(); });
                grid.add(b);
            }
            grid.revalidate();
            grid.repaint();
        }

        LocalDate getSelected() { return selected; }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AddSubject().setVisible(true));
    }
}