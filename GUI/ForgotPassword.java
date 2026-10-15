import java.awt.*;                 
import javax.swing.*;              
import javax.swing.border.*;      

public class ForgotPassword extends JFrame {

    // Colors and font 
    static final Color BG        = new Color(253, 244, 253);   // สีพื้นหลังด้านนอก ชมพูอ่อนมากๆ
    static final Color DARK_PINK = new Color(155, 45, 115);    // ชมพูเข้ม ใช้กับหัวข้อและ label
    static final Color PINK      = new Color(232, 110, 180);   // ชมพูสด ใช้กับตัวหนังสือรอง/ปุ่ม
    static final Color BTN_BG    = new Color(250, 225, 245);   // สีพื้นของปุ่ม Reset
    static final Font  BOLD      = new Font("Tahoma", Font.BOLD, 14); // ฟอนต์ตัวหนา ขนาด 14 ใช้ทั่วทั้งหน้า

    
    // ช่องกรอกต่างๆ ประกาศเป็นตัวแปรของคลาส เพราะต้องดึงค่าออกมาใช้ตอนกดปุ่ม เมธอด handleReset
    private JTextField usernameField = new JTextField();            // ช่องกรอกชื่อผู้ใช้ เห็นตัวอักษร
    private JPasswordField newPasswordField = new JPasswordField(); // ช่องรหัสผ่านใหม่ ขึ้นเป็นจุด
    private JPasswordField confirmField = new JPasswordField();     // ช่องยืนยันรหัสผ่านใหม่
    private AuthService auth = new AuthService();                   // ตัวจัดการเรื่องล็อกอิน/รีเซ็ตรหัส เรียกใช้

    // constructor ทำงานทันทีที่ new ForgotPassword ใช้สร้างหน้าตาทั้งหมดของหน้าต่าง
    public ForgotPassword() {
        setTitle("Forgot Password");                 // ข้อความบนแถบหัวหน้าต่าง
        setSize(420, 650);                           // ขนาดหน้าต่าง กว้าง 420 สูง 650 พิกเซล
        setDefaultCloseOperation(EXIT_ON_CLOSE);     // กดกากบาทแล้วปิดโปรแกรมทั้งหมดเลย
        setLocationRelativeTo(null);                 // ให้หน้าต่างโผล่กลางจอ

        
        // "การ์ด" สีขาวที่อยู่ตรงกลาง ข้างในจะใส่ช่องกรอกกับปุ่มทั้งหมด
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS)); // เรียงของข้างในจากบนลงล่างเป็นแนวตั้ง
        card.setBackground(Color.WHITE);                       // พื้นการ์ดสีขาว
        card.setBorder(new CompoundBorder(                     // CompoundBorder = เอาขอบสองอันมาซ้อนกัน
                new LineBorder(new Color(230, 230, 230), 1),   // ขอบนอก: เส้นสีเทาอ่อนหนา 1 พิกเซล
                new EmptyBorder(30, 40, 30, 40)));             // ขอบใน: เว้นช่องว่าง บน30 ซ้าย40 ล่าง30 ขวา40 (ให้เนื้อหาไม่ชิดขอบ)
        card.setPreferredSize(new Dimension(340, 500));        // ขนาดที่การ์ดอยากได้ กว้าง 340 สูง 500

        // title 
        // row เอา component ไปใส่แถวแล้วกำหนดว่าจะชิดซ้าย/กลาง/ขวา
        card.add(row(makeLabel("RESET PASSWORD", new Font("Tahoma", Font.BOLD, 16), DARK_PINK), FlowLayout.CENTER)); // หัวข้อใหญ่ อยู่กลาง
        card.add(Box.createVerticalStrut(6));   // เว้นช่องว่างแนวตั้ง 6 พิกเซล (เหมือนกดเว้นบรรทัดนิดนึง)
        card.add(row(makeLabel("enter your username to reset", BOLD, PINK), FlowLayout.CENTER)); // คำอธิบายใต้หัวข้อ
        card.add(Box.createVerticalStrut(30));  // เว้นห่างหน่อย ก่อนเข้าส่วนกรอกข้อมูล

        //  Username 
        card.add(row(makeLabel("USERNAME", BOLD, DARK_PINK), FlowLayout.LEFT)); // ป้ายบอกว่าช่องนี้คือชื่อผู้ใช้ ชิดซ้าย
        card.add(Box.createVerticalStrut(6));                                   // เว้นระหว่างป้ายกับช่องกรอก
        card.add(field(usernameField));                                         // ช่องกรอกชื่อผู้ใช้ 
        card.add(Box.createVerticalStrut(20));                                  // เว้นก่อนไปช่องถัดไป

        // New password 
        card.add(row(makeLabel("NEW PASSWORD", BOLD, DARK_PINK), FlowLayout.LEFT)); // ป้ายรหัสผ่านใหม่
        card.add(Box.createVerticalStrut(6));
        card.add(field(newPasswordField));                                          // ช่องกรอกรหัสผ่านใหม่
        card.add(Box.createVerticalStrut(20));

        //  Confirm new password 
        card.add(row(makeLabel("CONFIRM NEW PASSWORD", BOLD, DARK_PINK), FlowLayout.LEFT)); // ป้ายยืนยันรหัสผ่าน
        card.add(Box.createVerticalStrut(6));
        card.add(field(confirmField));                                                       // ช่องกรอกยืนยันรหัสผ่าน (ต้องพิมพ์ให้ตรงกับช่องบน)
        card.add(Box.createVerticalStrut(25));                                               // เว้นห่างหน่อยก่อนถึงปุ่ม

        //  Reset button 
        JButton resetBtn = new JButton("Reset password");  // สร้างปุ่ม ข้อความบนปุ่มคือ "Reset password"
        resetBtn.setFont(BOLD);                            // ตั้งฟอนต์ตัวหนา
        resetBtn.setForeground(PINK);                      // สีตัวหนังสือบนปุ่ม
        resetBtn.setBackground(BTN_BG);                    // สีพื้นปุ่ม
        resetBtn.setOpaque(true);                          // บังคับให้ทาสีพื้นจริงๆ (บาง look and feel จะไม่ยอมทาสีถ้าไม่ตั้งอันนี้)
        resetBtn.setFocusPainted(false);                   // ปิดกรอบประเส้นๆ ที่โผล่ตอนปุ่มถูกเลือก ให้ดูสะอาดขึ้น
        resetBtn.setBorder(new LineBorder(new Color(235, 200, 230), 1)); // ขอบปุ่มเส้นบางๆ สีชมพูอ่อน
        resetBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // เอาเมาส์ไปชี้แล้วเปลี่ยนเป็นรูปมือ
        resetBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));      // ให้ปุ่มกว้างเต็มการ์ด แต่สูงแค่ 38
        resetBtn.addActionListener(e -> handleReset());    // พอกดปุ่มแล้วให้ไปเรียกเมธอด handleReset 
        card.add(resetBtn);                                // เอาปุ่มใส่การ์ด
        card.add(Box.createVerticalStrut(20));

        // Divider line 
        JSeparator line = new JSeparator();                               // เส้นคั่นแนวนอน
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));         // ให้ยาวเต็มการ์ด สูงแค่ 2 พิกเซล
        card.add(line);
        card.add(Box.createVerticalStrut(20));

        // Back to sign in 
        JButton back = linkButton("back to sign in", BOLD, PINK);   // ปุ่มหน้าตาเหมือนลิงก์ข้อความธรรมดา
        back.addActionListener(e -> {                               // พอกดลิงก์นี้...
            new Login().setVisible(true);                           // เปิดหน้า Login ขึ้นมา
            dispose();                                              // แล้วปิดหน้านี้ทิ้ง ไม่ได้ปิดโปรแกรม ปิดแค่หน้าต่างนี้
        });
        card.add(row(back, FlowLayout.CENTER));                     // เอาลิงก์ใส่การ์ด อยู่กลาง

        getRootPane().setDefaultButton(resetBtn);     // ตั้งให้ resetBtn เป็นปุ่มเริ่มต้น แปลว่ากด Enter = เหมือนกดปุ่ม Reset

        
        JPanel background = new JPanel(new GridBagLayout());  // GridBagLayout จะจัดของที่ใส่เข้าไปให้อยู่กลางพื้นที่เอง
        background.setBackground(BG);                         // พื้นหลังสีชมพูอ่อน
        background.add(card);                                 // เอาการ์ดขาวไปวางตรงกลาง
        setContentPane(background);                           // ตั้งให้แผ่นนี้เป็นเนื้อหาหลักของหน้าต่าง
    }

    // Reset password 
    private void handleReset() {
        String username = usernameField.getText().trim();                // ดึงชื่อผู้ใช้จากช่องกรอก trim() = ตัดช่องว่างหน้าหลังทิ้ง
        String newPassword = new String(newPasswordField.getPassword()); // getPassword() ได้เป็น char[] เลยต้องแปลงเป็น String ก่อนใช้
        String confirm = new String(confirmField.getPassword());         // ค่าจากช่องยืนยันรหัสผ่านเหมือนกัน

        // ส่งข้อมูลไปให้ AuthService ตรวจและเปลี่ยนรหัสให้ แล้วมันจะตอบกลับมาเป็นผลลัพธ์ 
        // เราเอาผลลัพธ์มาเช็คด้วย switch ว่าเกิดอะไรขึ้น แล้วแจ้งผู้ใช้ตามนั้น
        switch (auth.resetPassword(username, newPassword, confirm)) {
            case EMPTY:    // มีช่องไหนช่องหนึ่งว่าง
                JOptionPane.showMessageDialog(this, "Please fill in all fields");
                return;    // จบเมธอดเลย ไม่ทำต่อ
            case MISMATCH: // รหัสผ่านใหม่กับช่องยืนยันไม่ตรงกัน
                JOptionPane.showMessageDialog(this, "The new password fields do not match");
                return;
            case INVALID_CHARS: // รหัสผ่านมีตัว | ซึ่งห้ามใช้ 
                JOptionPane.showMessageDialog(this, "Password cannot contain the | character");
                return;
            case NOT_FOUND: // ไม่เจอชื่อผู้ใช้นี้ในระบบ
                JOptionPane.showMessageDialog(this, "Username not found");
                return;
            case ERROR:    // บันทึกรหัสใหม่ไม่สำเร็จ 
                JOptionPane.showMessageDialog(this, "Could not save the new password. Please try again");
                return;
            default:       // กรณีอื่นๆ คือสำเร็จ ไม่ต้องทำอะไรในนี้ ปล่อยให้ไหลลงไปข้างล่าง
                break;
        }

        // มาตรงนี้ได้แปลว่าไม่มี error อะไรเลย = เปลี่ยนรหัสสำเร็จ
        JOptionPane.showMessageDialog(this, "Password reset successfully! Please sign in with your new password");
        new Login().setVisible(true);   // เด้งไปหน้า Login ให้ล็อกอินด้วยรหัสใหม่
        dispose();                      // ปิดหน้าลืมรหัสผ่านทิ้ง
    }

    //  Helpers เอาไว้ลดโค้ดซ้ำๆ ตอนสร้างหน้าตา
    // สร้างข้อความ (JLabel) พร้อมตั้งฟอนต์กับสีให้เสร็จในบรรทัดเดียว
    private JLabel makeLabel(String text, Font font, Color color) {
        JLabel l = new JLabel(text);   // สร้าง label จากข้อความที่ส่งมา
        l.setFont(font);               // ตั้งฟอนต์
        l.setForeground(color);        // ตั้งสีตัวหนังสือ
        return l;                      // ส่ง label ที่ตกแต่งเสร็จแล้วกลับไป
    }

    
    // สร้างปุ่มที่หน้าตาเหมือนข้อความธรรมดา ไม่มีกรอบ ไม่มีพื้นหลัง 
    private JButton linkButton(String text, Font font, Color color) {
        JButton b = new JButton(text);
        b.setFont(font);
        b.setForeground(color);
        b.setContentAreaFilled(false);          // ไม่ทาสีพื้นปุ่ม (โปร่งใส)
        b.setBorderPainted(false);              // ไม่วาดขอบปุ่ม
        b.setFocusPainted(false);               // ไม่วาดกรอบตอนถูกเลือก
        b.setMargin(new Insets(0, 0, 0, 0));    // ตัดระยะห่างภายในปุ่มออกให้หมด จะได้พอดีกับตัวหนังสือ
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // เมาส์ชี้แล้วเป็นรูปมือ
        return b;
    }

    // ห่อช่องกรอกด้วย panel พร้อมใส่ขอบ เพื่อให้ช่องกรอกทุกช่องหน้าตาเหมือนกัน
    // รับ JTextField ได้ และ JPasswordField ก็ส่งเข้ามาได้ด้วย เพราะเป็นลูกของ JTextField
    private JPanel field(JTextField f) {
        f.setBorder(new CompoundBorder(
                new LineBorder(new Color(170, 170, 170), 1),   // ขอบนอก: เส้นสีเทา
                new EmptyBorder(4, 8, 4, 8)));                 // ขอบใน: เว้นที่ว่างให้ตัวอักษรไม่ชิดขอบ
        JPanel p = new JPanel(new BorderLayout());  // BorderLayout ทำให้ช่องกรอกยืดเต็ม panel
        p.setOpaque(false);                         // ให้ panel โปร่งใส จะเห็นพื้นขาวของการ์ดข้างหลัง
        p.add(f);                                   // เอาช่องกรอกใส่ panel
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36)); // กว้างเต็มการ์ด สูงแค่ 36
        return p;
    }

    // เอา component อะไรก็ได้ไปใส่ในแถว แล้วเลือกได้ว่าจะชิดซ้าย กลาง หรือขวา
    private JPanel row(JComponent c, int align) {
        JPanel p = new JPanel(new FlowLayout(align, 0, 0));  // FlowLayout จัดของตามที่เราบอก (ซ้าย/กลาง/ขวา) ช่องว่าง 0
        p.setOpaque(false);                                  // โปร่งใส
        p.add(c);                                            // ใส่ component ลงไปในแถว
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, p.getPreferredSize().height)); // กว้างเต็มที่ แต่สูงเท่าตัว component พอดี ไม่ให้ยืดเกินจำเป็น
        return p;
    }

   
    public static void main(String[] args) {
        new ForgotPassword().setVisible(true);  
    }
}
 