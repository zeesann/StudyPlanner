import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.border.*;


public class Login extends JFrame {

    // สีกับฟอนต์ที่ใช้ในหน้านี้
    private static final Color BG        = new Color(253, 244, 253); 
    private static final Color DARK_PINK = new Color(155, 45, 115);  
    private static final Color PINK      = new Color(232, 110, 180); 
    private static final Color BTN_BG    = new Color(250, 225, 245);
    private static final Color LAVENDER  = new Color(212, 193, 247);
    private static final Font  BOLD      = new Font("Tahoma", Font.BOLD, 14);

    // เก็บช่องกรอกไว้เป็นตัวแปรของคลาส เพราะตอนกดปุ่ม Sign in
    // เราต้องย้อนกลับมาอ่านว่าผู้ใช้พิมพ์อะไรไว้
    private JTextField usernameField;
    private JPasswordField passwordField;

    // ตัวที่เอาไว้เช็คว่า username/password ถูกไหม 
    private final AuthService auth = new AuthService();

    //  ทำงานตอนสร้างหน้า Login ขึ้นมา มีหน้าที่ตั้งค่าหน้าต่าง
    public Login() {
        setTitle("Login");                       
        setSize(420, 720);                       
        setDefaultCloseOperation(EXIT_ON_CLOSE); 
        setLocationRelativeTo(null);             

        // พื้นหลังใช้ GridBagLayout 
        // วางไว้ตรงกลาง ให้อัตโนมัติ 
        JPanel background = new JPanel(new GridBagLayout());
        background.setBackground(BG);
        background.add(createCard());            // เอาการ์ดสีขาวใส่เข้าไป
        setContentPane(background);              // ตั้งให้พื้นหลังนี้เป็นเนื้อหาของหน้าต่าง
    }

    //สร้างการ์ดสีขาวตรงกลาง
    private JPanel createCard() {

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create(); 
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // ทำให้ขอบเนียน ไม่หยัก

                // วาดเงา วาดสี่เหลี่ยมมุมมนสีดำจางๆ ซ้อนกัน 8 ชั้น แต่ละชั้นใหญ่กว่าชั้นก่อนนิดนึง
                for (int i = 8; i > 0; i--) {
                    g2.setColor(new Color(0, 0, 0, 6)); 
                    g2.fillRoundRect(8 - i, 10 - i, getWidth() - 16 + i * 2, getHeight() - 16 + i * 2, 16, 16);
                }

                // วาดตัวการ์ดสีขาวทับบนเงา
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(8, 4, getWidth() - 16, getHeight() - 16, 12, 12);
                g2.dispose(); 
            }
        };
        card.setOpaque(false); // บอกว่าไม่ต้องทาพื้นให้ ไม่งั้นมุมมนจะมีสี่เหลี่ยมโผล่
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS)); // เรียงของในการ์ดจากบนลงล่าง
        card.setBorder(new EmptyBorder(30, 40, 30, 40));       // เว้นที่ว่างขอบใน ไม่ให้ของชิดขอบการ์ด
        card.setPreferredSize(new Dimension(340, 560));        // ขนาดการ์ด

        // ต่อจากนี้คือเอาของใส่การ์ดทีละชิ้นตามลำดับบนลงล่าง
        // Box.createVerticalStrut(n) คือช่องว่างแนวตั้ง n พิกเซลไว้ขั้นระหว่างชิ้น

        // รูปคนวงกลม
        card.add(centered(new AvatarIcon()));
        card.add(Box.createVerticalStrut(15));

        // ข้อความ WELCOME กับบรรทัดเล็ก
        JLabel welcome = new JLabel("WELCOME !");
        welcome.setFont(new Font("Tahoma", Font.BOLD, 16));
        welcome.setForeground(DARK_PINK); // setForeground = ตั้งสีตัวหนังสือ
        card.add(centered(welcome));
        card.add(Box.createVerticalStrut(6));

        JLabel subtitle = new JLabel("sign in to your study planner");
        subtitle.setFont(BOLD);
        subtitle.setForeground(PINK);
        card.add(centered(subtitle));
        card.add(Box.createVerticalStrut(30));

        // ช่องกรอก Username
        card.add(left(makeLabel("USERNAME"))); // ป้ายข้อความชิดซ้าย
        card.add(Box.createVerticalStrut(6));
        usernameField = new JTextField();
        card.add(field(usernameField));        // field() คือเมธอดช่วยแต่งช่องกรอก 
        card.add(Box.createVerticalStrut(20));

        // ช่องกรอก Password
        // JPasswordField พิมพ์แล้วขึ้นเป็นจุด ไม่โชว์ตัวอักษรจริง
        card.add(left(makeLabel("PASSWORD")));
        card.add(Box.createVerticalStrut(6));
        passwordField = new JPasswordField();
        card.add(field(passwordField));
        card.add(Box.createVerticalStrut(8));

        // ลิงก์ forgot password ข้อความธรรมดา JLabel ทำให้คลิกได้
        JLabel forgot = new JLabel("forgot password?");
        forgot.setFont(new Font("Tahoma", Font.BOLD, 10));
        forgot.setForeground(DARK_PINK);
        forgot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // เอาเมาส์ชี้แล้วเปลี่ยนเป็นรูปมือ
        forgot.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { // ทำงานตอนมีคนคลิก
                new ForgotPassword().setVisible(true); // เปิดหน้าลืมรหัสผ่าน
                dispose();                             // ปิดหน้า
            }
        });
        card.add(right(forgot)); // ชิดขวา
        card.add(Box.createVerticalStrut(20));

        //ปุ่ม Sign in 
        JButton signIn = new JButton("Sign in");
        signIn.setFont(BOLD);
        signIn.setForeground(PINK);
        signIn.setBackground(BTN_BG);
        signIn.setOpaque(true);        // ต้องสั่งอันนี้ สีพื้นปุ่มถึงจะขึ้นจริง
        signIn.setFocusPainted(false); // ปิดกรอบเส้นประที่ขึ้นรอบตัวหนังสือบนปุ่มตอนถูกเลือก เพราะไม่สวย
        signIn.setBorder(new LineBorder(new Color(235, 200, 230), 1)); 
        signIn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signIn.addActionListener(e -> handleSignIn()); // กดปุ่มแล้วไปเรียก handleSignIn() ข้างล่าง

        // เอาปุ่มใส่กล่องก่อนแล้วจำกัดความสูงไว้ 38 ถ้าไม่ทำ BoxLayout จะยืดปุ่มให้สูงผิดสัดส่วน
        JPanel btnWrap = new JPanel(new BorderLayout());
        btnWrap.setOpaque(false);
        btnWrap.add(signIn);
        btnWrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        card.add(btnWrap);
        card.add(Box.createVerticalStrut(25));

        // เส้นคั่นบางๆ
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(225, 225, 225));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        card.add(sep);
        card.add(Box.createVerticalStrut(20));

        //create an account 
        JLabel create = new JLabel("create an account");
        create.setFont(BOLD);
        create.setForeground(PINK);
        create.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        create.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new CreateAccount().setVisible(true);
                dispose();
            }
        });
        card.add(centered(create));

        // ทำให้กด Enter บนคีย์บอร์ดแล้วเท่ากับกดปุ่ม Sign in 
        getRootPane().setDefaultButton(signIn);
        return card;
    }

    // ทำงานตอนกด Sign in
    private void handleSignIn() {
        String username = usernameField.getText().trim(); // ตัดช่องว่างหน้าหลังทิ้ง เผื่อเผลอเคาะสเปซ
        String password = new String(passwordField.getPassword()); // ช่องรหัสผ่านให้ค่ามาเป็น char[] เลยแปลงเป็นข้อความ

        // ถ้ายังไม่กรอกอะไรสักช่อง ก็เด้งเตือนแล้วจบเลย
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password");
            return; // return = ออกจากเมธอดทันที โค้ดข้างล่างจะไม่ถูกรัน
        }

        // ส่งให้ AuthService ช่วยเช็ค ถ้าถูกจะได้ข้อมูลผู้ใช้กลับมา ถ้าผิดจะได้ null
        User user = auth.signIn(username, password);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Incorrect username or password");
            return;
        }

        // ล็อกอินผ่านแล้ว
        new CalendarGUI(new StudyPlannerApp(user)).setVisible(true);
        dispose();
    }

    // เมธอดช่วยไว้ไม่ให้เขียนซ้ำ

    // สร้างป้ายข้อความหน้าตาเหมือนกัน 
    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Tahoma", Font.BOLD, 14));
        l.setForeground(DARK_PINK);
        return l;
    }

    // แต่งช่องกรอก ใส่ขอบสีเทา + เว้นระยะข้างในให้ตัวหนังสือไม่ชิดขอบ แล้วจำกัดความสูงไว้ 36
    private JPanel field(JTextField f) {
        f.setBorder(new CompoundBorder(                  // CompoundBorder = เอาขอบสองอันมาซ้อนกัน
                new LineBorder(new Color(170, 170, 170), 1), // ชั้นนอก: เส้นสีเทา
                new EmptyBorder(4, 8, 4, 8)));               // ชั้นใน: ช่องว่างเปล่า
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.add(f);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        return p;
    }

    // เอาของชิ้นเดียวใส่กล่อง เพื่อสั่งว่าจะให้ชิดซ้าย กลาง ขวา
    private JPanel row(JComponent c, int align) {
        JPanel p = new JPanel(new FlowLayout(align, 0, 0));
        p.setOpaque(false);
        p.add(c);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, p.getPreferredSize().height)); // กว้างได้เต็ม แต่สูงเท่าตัวของ
        return p;
    }
    // left(...) แทน row(..., FlowLayout.LEFT)
    private JPanel centered(JComponent c) { return row(c, FlowLayout.CENTER); }
    private JPanel left(JComponent c)     { return row(c, FlowLayout.LEFT); }
    private JPanel right(JComponent c)    { return row(c, FlowLayout.RIGHT); }

    // รูปคนในวงกลม
    private static class AvatarIcon extends JComponent {
        AvatarIcon() { setPreferredSize(new Dimension(70, 70)); } // ขนาด 70x70

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            
            g2.setClip(new Ellipse2D.Float(0, 0, 70, 70));
            g2.setColor(LAVENDER);
            g2.fillRect(0, 0, 70, 70); // ทาสีม่วงอ่อนเต็มสี่เหลี่ยม แต่เพราะโดนตัดไว้ เลยเหลือแค่วงกลม

            g2.setColor(new Color(190, 165, 240));
            g2.fill(new Ellipse2D.Float(22, 14, 26, 26));   // หัว
            g2.fill(new Ellipse2D.Float(8, 46, 54, 40));    // ไหล่ 

            // วาดเส้นขอบสีขาวรอบหัวกับไหล่ ให้ดูแยกจากกันชัดขึ้น
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2.5f)); // ความหนาเส้น
            g2.draw(new Ellipse2D.Float(22, 14, 26, 26));
            g2.draw(new Ellipse2D.Float(8, 46, 54, 40));
            g2.dispose();
        }
    }

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}
