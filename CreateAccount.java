import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class CreateAccount extends JFrame {

    private static final Color BG        = new Color(253, 244, 253);
    private static final Color DARK_PINK = new Color(155, 45, 115);
    private static final Color PINK      = new Color(232, 110, 180);
    private static final Color BTN_BG    = new Color(250, 225, 245);
    private static final Font  BOLD      = new Font("Tahoma", Font.BOLD, 14);

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmField;
    private final AuthService auth = new AuthService();

    public CreateAccount() {
        setTitle("Create Account");
        setSize(420, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel background = new JPanel(new GridBagLayout());
        background.setBackground(BG);
        background.add(createCard());
        setContentPane(background);
    }

    private JPanel createCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                for (int i = 8; i > 0; i--) {
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
        card.setPreferredSize(new Dimension(340, 540));

        JLabel welcome = new JLabel("CREATE ACCOUNT");
        welcome.setFont(new Font("Tahoma", Font.BOLD, 16));
        welcome.setForeground(DARK_PINK);
        card.add(centered(welcome));
        card.add(Box.createVerticalStrut(6));

        JLabel subtitle = new JLabel("sign up for study planner");
        subtitle.setFont(BOLD);
        subtitle.setForeground(PINK);
        card.add(centered(subtitle));
        card.add(Box.createVerticalStrut(30));

        card.add(left(makeLabel("USERNAME")));
        card.add(Box.createVerticalStrut(6));
        usernameField = new JTextField();
        card.add(field(usernameField));
        card.add(Box.createVerticalStrut(20));

        card.add(left(makeLabel("PASSWORD")));
        card.add(Box.createVerticalStrut(6));
        passwordField = new JPasswordField();
        card.add(field(passwordField));
        card.add(Box.createVerticalStrut(20));

        card.add(left(makeLabel("CONFIRM PASSWORD")));
        card.add(Box.createVerticalStrut(6));
        confirmField = new JPasswordField();
        card.add(field(confirmField));
        card.add(Box.createVerticalStrut(25));

        JButton createBtn = new JButton("Create account");
        createBtn.setFont(BOLD);
        createBtn.setForeground(PINK);
        createBtn.setBackground(BTN_BG);
        createBtn.setOpaque(true);
        createBtn.setFocusPainted(false);
        createBtn.setBorder(new LineBorder(new Color(235, 200, 230), 1));
        createBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        createBtn.addActionListener(e -> handleCreateAccount());
        JPanel btnWrap = new JPanel(new BorderLayout());
        btnWrap.setOpaque(false);
        btnWrap.add(createBtn);
        btnWrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        card.add(btnWrap);
        card.add(Box.createVerticalStrut(20));

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(225, 225, 225));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        card.add(sep);
        card.add(Box.createVerticalStrut(20));

        JLabel back = new JLabel("back to sign in");
        back.setFont(BOLD);
        back.setForeground(PINK);
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        back.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new Login().setVisible(true);
                dispose();
            }
        });
        card.add(centered(back));

        getRootPane().setDefaultButton(createBtn);
        return card;
    }

    private void handleCreateAccount() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm  = new String(confirmField.getPassword());

        switch (auth.signUp(username, password, confirm)) {
            case EMPTY:
                JOptionPane.showMessageDialog(this, "Please fill in all fields"); return;
            case MISMATCH:
                JOptionPane.showMessageDialog(this, "Password and confirm password do not match"); return;
            case INVALID_CHARS:
                JOptionPane.showMessageDialog(this, "Username and password cannot contain the | character"); return;
            case TAKEN:
                JOptionPane.showMessageDialog(this, "This username is already taken. Please choose another username"); return;
            case ERROR:
                JOptionPane.showMessageDialog(this, "Could not save the account. Please try again"); return;
            default:
                break;
        }

        JOptionPane.showMessageDialog(this, "Account created successfully! Please sign in");
        new Login().setVisible(true);
        dispose();
    }

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CreateAccount().setVisible(true));
    }
}