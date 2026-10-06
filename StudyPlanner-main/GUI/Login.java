import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.border.*;

public class Login extends JFrame {

    // ===== สีตามดีไซน์ =====
    private static final Color BG        = new Color(253, 244, 253);
    private static final Color DARK_PINK = new Color(155, 45, 115);
    private static final Color PINK      = new Color(232, 110, 180);
    private static final Color BTN_BG    = new Color(250, 225, 245);
    private static final Color LAVENDER  = new Color(212, 193, 247);
    private static final Font  BOLD      = new Font("Tahoma", Font.BOLD, 14);

    private JTextField emailField;
    private JPasswordField passwordField;

    public Login() {
        setTitle("Login");
        setSize(420, 720);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel background = new JPanel(new GridBagLayout());
        background.setBackground(BG);
        background.add(createCard());
        setContentPane(background);
    }

    // ===== การ์ดสีขาวตรงกลาง (มีเงา) =====
    private JPanel createCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                for (int i = 8; i > 0; i--) { // เงา
                    g2.setColor(new Color(0, 0, 0, 6));
                    g2.fillRoundRect(8 - i, 10 - i, getWidth() - 16 + i * 2, getHeight() - 16 + i * 2, 16, 16);
                }
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(8, 4, getWidth() - 16, getHeight() - 16, 12, 12);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(30, 40, 30, 40));
        card.setPreferredSize(new Dimension(340, 560));

        // Avatar
        card.add(centered(new AvatarIcon()));
        card.add(Box.createVerticalStrut(15));

        // ข้อความต้อนรับ
        JLabel welcome = new JLabel("WELCOME !");
        welcome.setFont(new Font("Tahoma", Font.BOLD, 16));
        welcome.setForeground(DARK_PINK);
        card.add(centered(welcome));
        card.add(Box.createVerticalStrut(6));

        JLabel subtitle = new JLabel("sign in to your study planner");
        subtitle.setFont(BOLD);
        subtitle.setForeground(PINK);
        card.add(centered(subtitle));
        card.add(Box.createVerticalStrut(30));

        // Email
        card.add(left(makeLabel("EMAIL")));
        card.add(Box.createVerticalStrut(6));
        emailField = new JTextField();
        card.add(field(emailField));
        card.add(Box.createVerticalStrut(20));

        // Password
        card.add(left(makeLabel("PASSWORD")));
        card.add(Box.createVerticalStrut(6));
        passwordField = new JPasswordField();
        card.add(field(passwordField));
        card.add(Box.createVerticalStrut(8));

        // Forgot password
        JLabel forgot = new JLabel("forgot password?");
        forgot.setFont(new Font("Tahoma", Font.BOLD, 10));
        forgot.setForeground(DARK_PINK);
        forgot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // TODO: เปิดหน้ารีเซ็ตรหัสผ่าน
        card.add(right(forgot));
        card.add(Box.createVerticalStrut(20));

        // ปุ่ม Sign in
        JButton signIn = new JButton("Sign in");
        signIn.setFont(BOLD);
        signIn.setForeground(PINK);
        signIn.setBackground(BTN_BG);
        signIn.setOpaque(true);
        signIn.setFocusPainted(false);
        signIn.setBorder(new LineBorder(new Color(235, 200, 230), 1));
        signIn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signIn.addActionListener(e -> handleSignIn());
        JPanel btnWrap = new JPanel(new BorderLayout());
        btnWrap.setOpaque(false);
        btnWrap.add(signIn);
        btnWrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        card.add(btnWrap);
        card.add(Box.createVerticalStrut(25));

        // เส้นคั่น
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(225, 225, 225));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        card.add(sep);
        card.add(Box.createVerticalStrut(20));

        // Create an account
        JLabel create = new JLabel("create an account");
        create.setFont(BOLD);
        create.setForeground(PINK);
        create.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // TODO: เปิดหน้าสมัครสมาชิก
        card.add(centered(create));

        getRootPane().setDefaultButton(signIn);
        return card;
    }

    private void handleSignIn() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "กรุณากรอกอีเมลและรหัสผ่าน");
            return;
        }
        // TODO: ตรวจสอบล็อกอินจริง แล้วเปิดหน้าหลัก
        JOptionPane.showMessageDialog(this, "Signed in as " + email);
    }

    // ===== Helper ต่างๆ =====
    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Tahoma", Font.BOLD, 14));
        l.setForeground(DARK_PINK);
        return l;
    }

    private JPanel field(JTextField f) {
        f.setBorder(new CompoundBorder(
                new LineBorder(new Color(170, 170, 170), 1),
                new EmptyBorder(4, 8, 4, 8)));
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.add(f);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        return p;
    }

    private JPanel row(JComponent c, int align) {
        JPanel p = new JPanel(new FlowLayout(align, 0, 0));
        p.setOpaque(false);
        p.add(c);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, p.getPreferredSize().height));
        return p;
    }
    private JPanel centered(JComponent c) { return row(c, FlowLayout.CENTER); }
    private JPanel left(JComponent c)     { return row(c, FlowLayout.LEFT); }
    private JPanel right(JComponent c)    { return row(c, FlowLayout.RIGHT); }

    // ===== ไอคอนรูปคนวงกลม =====
    private static class AvatarIcon extends JComponent {
        AvatarIcon() { setPreferredSize(new Dimension(70, 70)); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setClip(new Ellipse2D.Float(0, 0, 70, 70));
            g2.setColor(LAVENDER);
            g2.fillRect(0, 0, 70, 70);

            g2.setColor(new Color(190, 165, 240));
            g2.fill(new Ellipse2D.Float(22, 14, 26, 26));   // หัว
            g2.fill(new Ellipse2D.Float(8, 46, 54, 40));    // ไหล่
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2.5f));
            g2.draw(new Ellipse2D.Float(22, 14, 26, 26));
            g2.draw(new Ellipse2D.Float(8, 46, 54, 40));
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}