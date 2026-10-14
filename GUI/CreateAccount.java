import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;



public class CreateAccount extends JFrame {

    
    private static final Color BG        = new Color(253, 244, 253); // สีพื้นหลังหน้าต่าง
    private static final Color DARK_PINK = new Color(155, 45, 115);  // ชมพูเข้ม ใช้กับหัวข้อและป้ายชื่อช่อง
    private static final Color PINK      = new Color(232, 110, 180); // ชมพูสว่าง ใช้กับคำบรรยายและตัวหนังสือบนปุ่ม
    private static final Color BTN_BG    = new Color(250, 225, 245); // สีพื้นปุ่ม
    private static final Font  BOLD      = new Font("Tahoma", Font.BOLD, 14);

    // เก็บช่องกรอกไว้เป็นตัวแปรของคลาส เพราะตอนกดปุ่มสมัคร ต้องย้อนมาอ่านค่าที่ผู้ใช้พิมพ์
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmField; // ช่องยืนยันรหัสผ่าน (ให้พิมพ์ซ้ำอีกรอบกันพิมพ์ผิด)

    // ตัวที่ทำหน้าที่สมัคร/เช็คบัญชี ตัวเดียวกับที่หน้า Login ใช้
    private final AuthService auth = new AuthService();

    //ตั้งค่าหน้าต่างตอนสร้างหน้านี้ขึ้นมา
    public CreateAccount() {
        setTitle("Create Account");
        setSize(420, 700);                      
        setDefaultCloseOperation(EXIT_ON_CLOSE); // กดกากบาทแล้วปิดโปรแกรมทั้งตัว
        setLocationRelativeTo(null);             // ให้หน้าต่างอยู่กลางจอ

        // พื้นหลังใช้ GridBagLayout เพื่อให้การ์ดที่ใส่เข้าไปอยู่ตรงกลางโดยอัตโนมัติ
        JPanel background = new JPanel(new GridBagLayout());
        background.setBackground(BG);
        background.add(createCard());
        setContentPane(background);
    }

    // ===== สร้างการ์ดสีขาวตรงกลาง ตัวหลักของหน้าจอ =====
    private JPanel createCard() {

        // วาดการ์ดเ
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // ขอบเนียน ไม่หยัก

                // เงา: สี่เหลี่ยมมุมมนสีดำจางๆ ซ้อนกัน 8 ชั้น แต่ละชั้นใหญ่ขึ้นนิดนึง 
                for (int i = 8; i > 0; i--) {
                    g2.setColor(new Color(0, 0, 0, 6)); // เลข 6 คือความโปร่งใส 
                    g2.fillRoundRect(8 - i, 10 - i, getWidth() - 16 + i * 2, getHeight() - 16 + i * 2, 16, 16);
                }

                // ตัวการ์ดสีขาววาดทับบนเงา
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(8, 4, getWidth() - 16, getHeight() - 16, 12, 12);
                g2.dispose();
            }
        };
        card.setOpaque(false); // ไม่ให้ Swing ทาพื้นให้ 
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS)); // เรียงของในการ์ดจากบนลงล่าง
        card.setBorder(new EmptyBorder(30, 40, 30, 40));       // เว้นที่ว่างขอบใน
        card.setPreferredSize(new Dimension(340, 540));

       
        // --- หัวข้อกับคำบรรยาย ---
        JLabel welcome = new JLabel("CREATE ACCOUNT");
        welcome.setFont(new Font("Tahoma", Font.BOLD, 16));
        welcome.setForeground(DARK_PINK); // setForeground = ตั้งสีตัวหนังสือ
        card.add(centered(welcome));
        card.add(Box.createVerticalStrut(6));

        JLabel subtitle = new JLabel("sign up for study planner");
        subtitle.setFont(BOLD);
        subtitle.setForeground(PINK);
        card.add(centered(subtitle));
        card.add(Box.createVerticalStrut(30));

        // --- ช่อง Username ---
        card.add(left(makeLabel("USERNAME"))); // ป้ายชื่อช่อง ชิดซ้าย
        card.add(Box.createVerticalStrut(6));
        usernameField = new JTextField();
        card.add(field(usernameField));        // field() คือเมธอดช่วยแต่งช่องกรอก 
        card.add(Box.createVerticalStrut(20));

        // --- ช่อง Password --- (JPasswordField พิมพ์แล้วขึ้นเป็นจุดๆ ไม่โชว์ตัวอักษรจริง)
        card.add(left(makeLabel("PASSWORD")));
        card.add(Box.createVerticalStrut(6));
        passwordField = new JPasswordField();
        card.add(field(passwordField));
        card.add(Box.createVerticalStrut(20));

        // --- ช่อง Confirm Password --- 
        card.add(left(makeLabel("CONFIRM PASSWORD")));
        card.add(Box.createVerticalStrut(6));
        confirmField = new JPasswordField();
        card.add(field(confirmField));
        card.add(Box.createVerticalStrut(25));

        // --- ปุ่ม Create account ---
        JButton createBtn = new JButton("Create account");
        createBtn.setFont(BOLD);
        createBtn.setForeground(PINK);
        createBtn.setBackground(BTN_BG);
        createBtn.setOpaque(true);        
        createBtn.setFocusPainted(false); // ปิดกรอบเส้นประที่ขึ้นรอบตัวหนังสือบนปุ่มตอนถูกเลือก เพราะไม่สวย
        createBtn.setBorder(new LineBorder(new Color(235, 200, 230), 1)); // ขอบบางๆ สีชมพูอ่อน
        createBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // เอาเมาส์ชี้แล้วเป็นรูปมือ
        createBtn.addActionListener(e -> handleCreateAccount()); // กดปุ่มแล้วไปเรียก handleCreateAccount() ข้างล่าง

       
        JPanel btnWrap = new JPanel(new BorderLayout());
        btnWrap.setOpaque(false);
        btnWrap.add(createBtn);
        btnWrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        card.add(btnWrap);
        card.add(Box.createVerticalStrut(20));

        // --- เส้นคั่นบางๆ ---
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(225, 225, 225));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        card.add(sep);
        card.add(Box.createVerticalStrut(20));

        // --- ลิงก์ back to sign in --- ข้อความธรรมดา (JLabel) ที่ทำให้คลิกได้
        JLabel back = new JLabel("back to sign in");
        back.setFont(BOLD);
        back.setForeground(PINK);
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        back.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { // ทำงานตอนมีคนคลิก
                new Login().setVisible(true);        // เปิดหน้า Login กลับมา
                dispose();                           // ปิดหน้าสมัครสมาชิกนี้ทิ้ง
            }
        });
        card.add(centered(back));

        // ทำให้กด Enter บนคีย์บอร์ดแล้วเท่ากับกดปุ่ม Create account
        getRootPane().setDefaultButton(createBtn);
        return card;
    }

    // ===== ทำงานตอนกดปุ่ม Create account =====
    // ส่วนนี้คือตัวที่ต่างจากหน้า Login ที่สุด
    private void handleCreateAccount() {
        String username = usernameField.getText().trim();             // trim() ตัดช่องว่างหน้าหลังทิ้ง
        String password = new String(passwordField.getPassword());   // ช่องรหัสผ่านให้ค่ามาเป็น char[] เลยแปลงเป็นข้อความ
        String confirm  = new String(confirmField.getPassword());

        // ส่งทั้งสามค่าให้ AuthService ตรวจและบันทึกบัญชี มันจะตอบกลับมาเป็น "ผลลัพธ์" อย่างใดอย่างหนึ่ง
        // แล้วเราใช้ switch ดูว่าได้ผลอะไร จะได้แจ้งผู้ใช้ให้ตรงกับสาเหตุ
        switch (auth.signUp(username, password, confirm)) {
            case EMPTY:         // มีช่องไหนว่าง
                JOptionPane.showMessageDialog(this, "Please fill in all fields"); return;
            case MISMATCH:      // รหัสผ่านกับช่องยืนยันไม่ตรงกัน
                JOptionPane.showMessageDialog(this, "Password and confirm password do not match"); return;
            case INVALID_CHARS: // ใช้ตัวอักษรต้องห้าม คือ | 
                JOptionPane.showMessageDialog(this, "Username and password cannot contain the | character"); return;
            case TAKEN:         // มีคนใช้ username นี้ไปแล้ว
                JOptionPane.showMessageDialog(this, "This username is already taken. Please choose another username"); return;
            case ERROR:         // บันทึกลงไฟล์/ระบบไม่สำเร็จ
                JOptionPane.showMessageDialog(this, "Could not save the account. Please try again"); return;
            default:            // กรณีอื่น = สมัครสำเร็จ ไม่ต้องทำอะไรในนี้ แค่หลุดออกจาก switch ไปทำต่อข้างล่าง
                break;
        }
        // สังเกต: ทุก case ที่เป็นปัญหามี return ปิดท้าย
        // แปลว่าถ้าเกิดปัญหา มันจะหยุดแค่ตรงนั้น และผู้ใช้อยู่หน้าเดิมเพื่อแก้ไขแล้วลองใหม่
        // ส่วนโค้ดข้างล่างนี้จะรันก็ต่อเมื่อสมัครสำเร็จเท่านั้น

        JOptionPane.showMessageDialog(this, "Account created successfully! Please sign in");
        new Login().setVisible(true); // พากลับไปหน้า Login ให้ล็อกอินด้วยบัญชีที่เพิ่งสร้าง
        dispose();                    // ปิดหน้าสมัครสมาชิก
    }

    // ===== เมธอดช่วยเล็กๆ น้อยๆ ไว้ไม่ให้เขียนซ้ำ  =====

    // สร้างป้ายชื่อช่อง (USERNAME / PASSWORD / CONFIRM PASSWORD) หน้าตาเหมือนกัน
    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Tahoma", Font.BOLD, 14));
        l.setForeground(DARK_PINK);
        return l;
    }

    // แต่งช่องกรอก: ใส่ขอบสีเทา + เว้นระยะข้างในไม่ให้ตัวหนังสือชิดขอบ
    private JPanel field(JTextField f) {
        f.setBorder(new CompoundBorder(                      // CompoundBorder = เอาขอบสองอันมาซ้อนกัน
                new LineBorder(new Color(170, 170, 170), 1),     // ชั้นนอก: เส้นสีเทา
                new EmptyBorder(4, 8, 4, 8)));                   // ชั้นใน: ช่องว่างเปล่า
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.add(f);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        return p;
    }

    // เอาของชิ้นเดียวใส่กล่อง เพื่อสั่งว่าจะให้ชิดซ้าย / กลาง
    // (BoxLayout ชอบยืดของให้เต็มความกว้าง เลยต้องใช้กล่องช่วยจัดตำแหน่ง)
    private JPanel row(JComponent c, int align) {
        JPanel p = new JPanel(new FlowLayout(align, 0, 0));
        p.setOpaque(false);
        p.add(c);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, p.getPreferredSize().height)); // กว้างได้เต็ม แต่สูงเท่าตัวของ
        return p;
    }
    // ทางลัด ไว้เรียกให้อ่านง่าย เช่น left(...) แทน row(..., FlowLayout.LEFT)
    
    private JPanel centered(JComponent c) { return row(c, FlowLayout.CENTER); }
    private JPanel left(JComponent c)     { return row(c, FlowLayout.LEFT); }

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CreateAccount().setVisible(true));
    }
}