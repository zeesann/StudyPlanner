/**
 * ============================================================
 *  คลาส AuthService  (ชั้น "ระบบบัญชีผู้ใช้")
 * ============================================================
 *  หน้าที่: จัดการเรื่องบัญชีผู้ใช้ 3 อย่าง
 *    1) signIn        = เข้าสู่ระบบ
 *    2) signUp        = สมัครสมาชิกใหม่
 *    3) resetPassword = ตั้งรหัสผ่านใหม่
 *
 *  จุดสำคัญ:
 *    - คลาสนี้ "ไม่มี Swing/AWT" เลย คือไม่ยุ่งกับหน้าจอ มีแต่ตรรกะ (logic)
 *      หน้าจอ (เช่นหน้า Login) จะเรียกใช้เมธอดในคลาสนี้ แล้วเอาผลลัพธ์ไปแสดงข้อความเอง
 *    - คลาสนี้ไม่ได้อ่าน/เขียนไฟล์เอง แต่ส่งต่อให้ UserStorage (เก็บใน users.txt)
 *      ดังนั้นสมัครบัญชีครั้งเดียว ปิดโปรแกรมแล้วเปิดใหม่ก็ยังล็อกอินได้
 *
 *  แนวคิด: AuthService = "ผู้ตรวจเช็กเงื่อนไข" / UserStorage = "ผู้จัดเก็บข้อมูล"
 */
public class AuthService {

    // enum = ชุดค่าผลลัพธ์ที่เป็นไปได้ ใช้แทนการคืนค่าเป็นข้อความหรือตัวเลขเลขๆ
    // ทำให้หน้าจอรู้ว่าเกิดอะไรขึ้น แล้วเลือกข้อความแจ้งผู้ใช้ได้ถูกต้อง

    /** ผลลัพธ์ของการสมัครสมาชิก */
    public enum SignUpResult {
        OK,             // สมัครสำเร็จ
        EMPTY,          // มีช่องที่ยังไม่ได้กรอก
        MISMATCH,       // รหัสผ่านกับช่องยืนยันรหัสผ่านไม่ตรงกัน
        INVALID_CHARS,  // ใช้อักขระต้องห้าม (| หรือขึ้นบรรทัดใหม่)
        TAKEN,          // ชื่อผู้ใช้นี้มีคนใช้แล้ว
        ERROR           // เขียนไฟล์ไม่สำเร็จ หรือเกิดข้อผิดพลาดอื่น
    }

    /** ผลลัพธ์ของการรีเซ็ตรหัสผ่าน */
    public enum ResetResult {
        OK,             // เปลี่ยนรหัสผ่านสำเร็จ
        EMPTY,          // มีช่องที่ยังไม่ได้กรอก
        MISMATCH,       // รหัสผ่านใหม่กับช่องยืนยันไม่ตรงกัน
        INVALID_CHARS,  // รหัสผ่านใหม่มีอักขระต้องห้าม
        NOT_FOUND,      // ไม่พบชื่อผู้ใช้นี้ในระบบ
        ERROR           // เขียนไฟล์ไม่สำเร็จ หรือเกิดข้อผิดพลาดอื่น
    }

    // ------------------------------------------------------------
    //  1) เข้าสู่ระบบ
    // ------------------------------------------------------------
    /** คืนค่า User ถ้า username/password ถูกต้อง ถ้าไม่ถูกต้องคืน null */
    public User signIn(String username, String password) {
        // ถ้าเป็น null หรือว่างเปล่า ถือว่าล็อกอินไม่ผ่านทันที
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) return null;

        // ให้ UserStorage ตรวจกับข้อมูลใน users.txt
        // ถ้าตรง -> สร้างอ็อบเจกต์ User ส่งกลับ (หน้าจออื่นจะใช้ User นี้ต่อ)
        // ถ้าไม่ตรง -> null
        return UserStorage.validateLogin(username, password) ? new User(username) : null;
    }

    // ------------------------------------------------------------
    //  2) สมัครสมาชิก  (ตรวจทีละเงื่อนไข เจอเงื่อนไขแรกที่ไม่ผ่านก็คืนผลทันที)
    // ------------------------------------------------------------
    public SignUpResult signUp(String username, String password, String confirm) {
        // ขั้นที่ 1: ทุกช่องต้องไม่ว่าง
        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) return SignUpResult.EMPTY;

        // ขั้นที่ 2: รหัสผ่านกับช่องยืนยันต้องเหมือนกัน
        if (!password.equals(confirm))                                     return SignUpResult.MISMATCH;

        // ขั้นที่ 3: ห้ามมีอักขระที่ทำให้รูปแบบไฟล์พัง
        if (hasBadChars(username) || hasBadChars(password))                return SignUpResult.INVALID_CHARS;

        // ขั้นที่ 4: ชื่อผู้ใช้ต้องยังไม่ซ้ำกับคนอื่น
        if (UserStorage.usernameExists(username))                          return SignUpResult.TAKEN;

        // ขั้นที่ 5: ผ่านหมดแล้ว -> บันทึกลงไฟล์ ถ้าบันทึกได้ = OK ไม่ได้ = ERROR
        return UserStorage.registerUser(username, password) ? SignUpResult.OK : SignUpResult.ERROR;
    }

    // ------------------------------------------------------------
    //  3) รีเซ็ตรหัสผ่าน
    // ------------------------------------------------------------
    public ResetResult resetPassword(String username, String newPassword, String confirm) {
        // ทุกช่องต้องไม่ว่าง
        if (username.isEmpty() || newPassword.isEmpty() || confirm.isEmpty()) return ResetResult.EMPTY;

        // รหัสใหม่กับช่องยืนยันต้องตรงกัน
        if (!newPassword.equals(confirm))                                     return ResetResult.MISMATCH;

        // รหัสใหม่ห้ามมีอักขระต้องห้าม (ไม่ต้องเช็ก username เพราะต้องมีอยู่ในระบบอยู่แล้ว)
        if (hasBadChars(newPassword))                                         return ResetResult.INVALID_CHARS;

        // ต้องมีชื่อผู้ใช้นี้อยู่จริงถึงจะเปลี่ยนรหัสได้
        if (!UserStorage.usernameExists(username))                            return ResetResult.NOT_FOUND;

        // สั่งเปลี่ยนรหัสในไฟล์ ถ้าสำเร็จ = OK ไม่สำเร็จ = ERROR
        return UserStorage.resetPassword(username, newPassword) ? ResetResult.OK : ResetResult.ERROR;
    }

    // ------------------------------------------------------------
    //  เมธอดช่วยตรวจอักขระต้องห้าม
    // ------------------------------------------------------------
    // ไฟล์ users.txt ใช้ '|' คั่นข้อมูลแต่ละช่อง และใช้ขึ้นบรรทัดใหม่คั่นแต่ละคน
    // ถ้าผู้ใช้พิมพ์อักขระเหล่านี้ลงในชื่อหรือรหัสผ่าน รูปแบบไฟล์จะเพี้ยนและอ่านกลับไม่ได้
    // จึงห้ามใช้ (private = ใช้ได้เฉพาะในคลาสนี้)
    private boolean hasBadChars(String s) {
        return s.contains("|") || s.contains("\n") || s.contains("\r");
    }
}