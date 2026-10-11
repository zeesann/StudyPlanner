import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class ForgotPassword extends JFrame {

    private static final Color BG        = new Color(253, 244, 253);
    private static final Color DARK_PINK = new Color(155, 45, 115);
    private static final Color PINK      = new Color(232, 110, 180);
    private static final Color BTN_BG    = new Color(250, 225, 245);
    private static final Font  BOLD      = new Font("Tahoma", Font.BOLD, 14);

    private JTextField usernameField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmField;

    public ForgotPassword() {
        setTitle("Forgot Password");
        setSize(420, 650);
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
        card.setPreferredSize(new Dimension(340, 500));

        JLabel welcome = new JLabel("RESET PASSWORD");
        welcome.setFont(new Font("Tahoma", Font.BOLD, 16));
        welcome.setForeground(DARK_PINK);
        card.add(centered(welcome));
        card.add(Box.createVerticalStrut(6));

        JLabel subtitle = new JLabel("enter your username to reset");
        subtitle.setFont(BOLD);
        subtitle.setForeground(PINK);
        card.add(centered(subtitle));
        card.add(Box.createVerticalStrut(30));

        card.add(left(makeLabel("USERNAME")));
        card.add(Box.createVerticalStrut(6));
        usernameField = new JTextField();
        card.add(field(usernameField));
        card.add(Box.createVerticalStrut(20));

        card.add(left(makeLabel("NEW PASSWORD")));
        card.add(Box.createVerticalStrut(6));
        newPasswordField = new JPasswordField();
        card.add(field(newPasswordField));
        card.add(Box.createVerticalStrut(20));

        card.add(left(makeLabel("CONFIRM NEW PASSWORD")));
        card.add(Box.createVerticalStrut(6));
        confirmField = new JPasswordField();
        card.add(field(confirmField));
        card.add(Box.createVerticalStrut(25));

        JButton resetBtn = new JButton("Reset password");
        resetBtn.setFont(BOLD);
        resetBtn.setForeground(PINK);
        resetBtn.setBackground(BTN_BG);
        resetBtn.setOpaque(true);
        resetBtn.setFocusPainted(false);
        resetBtn.setBorder(new LineBorder(new Color(235, 200, 230), 1));
        resetBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        resetBtn.addActionListener(e -> handleReset());
        JPanel btnWrap = new JPanel(new BorderLayout());
        btnWrap.setOpaque(false);
        btnWrap.add(resetBtn);
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

        getRootPane().setDefaultButton(resetBtn);
        return card;
    }

    private void handleReset() {
        String username = usernameField.getText().trim();
        String newPassword = new String(newPasswordField.getPassword());
        String confirm = new String(confirmField.getPassword());

        if (username.isEmpty() || newPassword.isEmpty() || confirm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields");
            return;
        }

        if (!newPassword.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "The new password fields do not match");
            return;
        }

        boolean success = UserStorage.resetPassword(username, newPassword);
        if (!success) {
            JOptionPane.showMessageDialog(this, "Username not found");
            return;
        }

        JOptionPane.showMessageDialog(this, "Password reset successfully! Please sign in with your new password");
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
        SwingUtilities.invokeLater(() -> new ForgotPassword().setVisible(true));
    }
}