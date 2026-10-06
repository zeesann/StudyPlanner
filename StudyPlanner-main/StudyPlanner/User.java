public class User {
 
    private String id;
    private String email;
    private String password;
 
    public User(String id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }
 
    
    public boolean signIn(String email, String password) {
        if (this.email.equals(email) && this.password.equals(password)) {
            System.out.println("เข้าสู่ระบบสำเร็จ: " + email);
            return true;
        } else {
            System.out.println("email หรือ password ไม่ถูกต้อง");
            return false;
        }
    }
 
    
    public void signUp(String email, String password) {
        this.email = email;
        this.password = password;
        System.out.println("สมัครสมาชิกสำเร็จ: " + email);
    }
 
    
    public void forgotPassword(String email) {
        if (this.email.equals(email)) {
            System.out.println("ส่งลิงก์รีเซ็ตรหัสผ่านไปที่: " + email);
        } else {
            System.out.println("ไม่พบ email นี้ในระบบ");
        }
    }
 
    // ---------- ออกจากระบบ ----------
    public void signOut() {
        System.out.println("ออกจากระบบแล้ว: " + email);
    }
 
    // ---------- Getter ----------
    public String getId() {
        return id;
    }
 
    public String getEmail() {
        return email;
    }
}
 