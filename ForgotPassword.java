import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
 
public class ForgotPassword extends JFrame {
 
    // ---------- Colors and font ----------
    static final Color BG        = new Color(253, 244, 253);
    static final Color DARK_PINK = new Color(155, 45, 115);
    static final Color PINK      = new Color(232, 110, 180);
    static final Color BTN_BG    = new Color(250, 225, 245);
    static final Font  BOLD      = new Font("Tahoma", Font.BOLD, 14);
 
    // ---------- Variables ----------
    private JTextField usernameField = new JTextField();
    private JPasswordField newPasswordField = new JPasswordField();
    private JPasswordField confirmField = new JPasswordField();
    private AuthService auth = new AuthService();
 
    public ForgotPassword() {
        setTitle("Forgot Password");
        setSize(420, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
 
        // ---- White card in the center ----
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(30, 40, 30, 40)));
        card.setPreferredSize(new Dimension(340, 500));
 
        // ---- Title ----
        card.add(row(makeLabel("RESET PASSWORD", new Font("Tahoma", Font.BOLD, 16), DARK_PINK), FlowLayout.CENTER));
        card.add(Box.createVerticalStrut(6));
        card.add(row(makeLabel("enter your username to reset", BOLD, PINK), FlowLayout.CENTER));
        card.add(Box.createVerticalStrut(30));
 
        // ---- Username ----
        card.add(row(makeLabel("USERNAME", BOLD, DARK_PINK), FlowLayout.LEFT));
        card.add(Box.createVerticalStrut(6));
        card.add(field(usernameField));
        card.add(Box.createVerticalStrut(20));
 
        // ---- New password ----
        card.add(row(makeLabel("NEW PASSWORD", BOLD, DARK_PINK), FlowLayout.LEFT));
        card.add(Box.createVerticalStrut(6));
        card.add(field(newPasswordField));
        card.add(Box.createVerticalStrut(20));
 
        // ---- Confirm new password ----
        card.add(row(makeLabel("CONFIRM NEW PASSWORD", BOLD, DARK_PINK), FlowLayout.LEFT));
        card.add(Box.createVerticalStrut(6));
        card.add(field(confirmField));
        card.add(Box.createVerticalStrut(25));
 
        // ---- Reset button ----
        JButton resetBtn = new JButton("Reset password");
        resetBtn.setFont(BOLD);
        resetBtn.setForeground(PINK);
        resetBtn.setBackground(BTN_BG);
        resetBtn.setOpaque(true);
        resetBtn.setFocusPainted(false);
        resetBtn.setBorder(new LineBorder(new Color(235, 200, 230), 1));
        resetBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        resetBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        resetBtn.addActionListener(e -> handleReset());
        card.add(resetBtn);
        card.add(Box.createVerticalStrut(20));
 
        // ---- Divider line ----
        JSeparator line = new JSeparator();
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        card.add(line);
        card.add(Box.createVerticalStrut(20));
 
        // ---- Back to sign in ----
        JButton back = linkButton("back to sign in", BOLD, PINK);
        back.addActionListener(e -> {
            new Login().setVisible(true);
            dispose();
        });
        card.add(row(back, FlowLayout.CENTER));
 
        getRootPane().setDefaultButton(resetBtn);     // press Enter = click Reset
 
        // ---- Pink background (card sits in the middle) ----
        JPanel background = new JPanel(new GridBagLayout());
        background.setBackground(BG);
        background.add(card);
        setContentPane(background);
    }
 
    // ---------- Reset password ----------
    private void handleReset() {
        String username = usernameField.getText().trim();
        String newPassword = new String(newPasswordField.getPassword());
        String confirm = new String(confirmField.getPassword());
 
        switch (auth.resetPassword(username, newPassword, confirm)) {
            case EMPTY:
                JOptionPane.showMessageDialog(this, "Please fill in all fields");
                return;
            case MISMATCH:
                JOptionPane.showMessageDialog(this, "The new password fields do not match");
                return;
            case INVALID_CHARS:
                JOptionPane.showMessageDialog(this, "Password cannot contain the | character");
                return;
            case NOT_FOUND:
                JOptionPane.showMessageDialog(this, "Username not found");
                return;
            case ERROR:
                JOptionPane.showMessageDialog(this, "Could not save the new password. Please try again");
                return;
            default:
                break;
        }
 
        JOptionPane.showMessageDialog(this, "Password reset successfully! Please sign in with your new password");
        new Login().setVisible(true);
        dispose();
    }
 
    // ---------- Helpers ----------
    // Make a label with font and color
    private JLabel makeLabel(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }
 
    // Make a button that looks like plain text (no border)
    private JButton linkButton(String text, Font font, Color color) {
        JButton b = new JButton(text);
        b.setFont(font);
        b.setForeground(color);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setMargin(new Insets(0, 0, 0, 0));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
 
    // Put a text field inside a panel with a border
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
 
    // Put a component in a row (align = FlowLayout.LEFT / CENTER / RIGHT)
    private JPanel row(JComponent c, int align) {
        JPanel p = new JPanel(new FlowLayout(align, 0, 0));
        p.setOpaque(false);
        p.add(c);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, p.getPreferredSize().height));
        return p;
    }
 
    public static void main(String[] args) {
        new ForgotPassword().setVisible(true);
    }
}
 