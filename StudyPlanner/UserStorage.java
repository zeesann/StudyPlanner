import java.io.*;    
import java.util.*;  



public class UserStorage {
    // "ที่เก็บบัญชีผู้ใช้" ใช้ไฟล์ users.txt แทนฐานข้อมูล ในไฟล์ 1 บรรทัด = 1 บัญชี ชื่อ|รหัสผ่าน 
    // ชื่อไฟล์ที่ใช้เก็บข้อมูลเป็นค่าคงที่ไว้ที่เดียว ไม่ต้องพิมพ์ซ้ำ
    private static final String FILE_NAME = "users.txt";

    // เช็คตอนล็อกอิน ว่ามีบัญชีที่ชื่อกับรหัสตรงกันทั้งคู่ไหม
    public static boolean validateLogin(String username, String password) {
        // loadUsers() จะอ่านไฟล์แล้วคืนรายการบัญชีทั้งหมดมา แต่ละบัญชีเป็น array 2 ช่อง
        // user[0] = ชื่อผู้ใช้, user[1] = รหัสผ่าน
        for (String[] user : loadUsers()) {
            if (user[0].equals(username) && user[1].equals(password)) {
                return true;   // เจอบัญชีที่ตรงทั้งชื่อและรหัส ล็อกอินผ่าน
            }
        }
        return false;          // ชื่อหรือรหัสผิด
    }

    // เช็คว่าชื่อนี้มีคนใช้ไปแล้วหรือยัง กันสมัครซ้ำ
    public static boolean usernameExists(String username) {
        for (String[] user : loadUsers()) {
            if (user[0].equals(username)) return true;   // เจอชื่อซ้ำ
        }
        return false;   // ไม่เจอ 
    }

    // สมัครบัญชีใหม่ สำเร็จคืน true, ถ้าชื่อซ้ำหรือบันทึกพลาดคืน false
    public static boolean registerUser(String username, String password) {
        if (usernameExists(username)) {
            return false;   // ชื่อซ้ำ ไม่ให้สมัคร
        }
        
        try (FileWriter writer = new FileWriter(FILE_NAME, true)) { 
            // true ตัวนี้คือโหมด "ต่อท้ายไฟล์" บัญชีเก่าจะไม่หาย
            writer.write(username + "|" + password + "\n");   // เขียนหนึ่งบรรทัด ชื่อ|รหัส แล้วขึ้นบรรทัดใหม่
            return true;
        } catch (IOException e) {
            // ถ้าเขียนไฟล์ไม่ได้ ไม่มีสิทธิ์เขียน ดิสก์เต็ม จะมาตรงนี้
            System.out.println("Failed to save account: " + e.getMessage());
            return false;
        }
    }

    // เปลี่ยนรหัสผ่านของชื่อที่ระบุ ไฟล์ข้อความแก้ทีละบรรทัดไม่ได้ 
    // โหลดทุกบัญชีเข้าหน่วยความจำ แก้รหัส เขียนทับไฟล์ใหม่ทั้งไฟล์
    public static boolean resetPassword(String username, String newPassword) {
        List<String[]> users = loadUsers();   // โหลดบัญชีทั้งหมดมาก่อน
        boolean found = false;                // ตัวจำว่าเจอชื่อที่ต้องการแก้หรือยัง

        for (String[] u : users) {
            if (u[0].equals(username)) {
                u[1] = newPassword;   // เจอแล้ว เปลี่ยนรหัสในหน่วยความจำ ไม่ได้แตะไฟล์
                found = true;
                break;                // เจอแล้วไม่ต้องหาต่อ ออกจากลูปเลย
            }
        }
        if (!found) return false;     // ไม่มีชื่อนี้ในระบบ ก็ไม่ต้องเขียนไฟล์อะไร

        try (FileWriter writer = new FileWriter(FILE_NAME, false)) { 
            // false = เขียนทับเนื้อหาเก่าหายหมด แล้วเขียนใหม่ทั้งชุด
            // ปลอดภัยเพราะเรามีข้อมูลทุกบัญชีอยู่ใน users แล้ว (รวมรหัสใหม่ด้วย)
            for (String[] u : users) {
                writer.write(u[0] + "|" + u[1] + "\n");   // เขียนทุกบัญชีกลับลงไปทีละบรรทัด
            }
            return true;
        } catch (IOException e) {
            System.out.println("Failed to save: " + e.getMessage());
            return false;
        }
    }


    // อ่านไฟล์ users.txt แล้วแปลงเป็นรายการบัญชี 
    private static List<String[]> loadUsers() {
        List<String[]> list = new ArrayList<>();   // ลิสต์ว่างๆ เตรียมเก็บผลลัพธ์
        File file = new File(FILE_NAME);
        if (!file.exists()) return list;  // ถ้ายังไม่มีไฟล์ แปลว่ายังไม่มีใครสมัคร คืนลิสต์ว่างกลับไปเลย ไม่ error

        // BufferedReader ช่วยให้อ่านไฟล์ทีละบรรทัดได้สะดวก ปิดไฟล์ให้อัตโนมัติ
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            // อ่านทีละบรรทัดไปเรื่อยๆ จนได้ null ซึ่งแปลว่าหมดไฟล์
            while ((line = reader.readLine()) != null) {
                // split("\\|") ตัดข้อความด้วยตัว | เช่น "somchai|1234" ได้ ["somchai", "1234"]
                // ต้องใส่ \\ นำหน้า เพราะใน regex ตัว | มีความหมายพิเศษ ถ้าไม่ใส่จะตัดผิด
                String[] parts = line.split("\\|");
                // รับเฉพาะบรรทัดที่ได้ 2 ส่วนพอดี (ชื่อ + รหัส) บรรทัดเสียหรือว่างจะถูกข้าม
                if (parts.length == 2) list.add(parts);
            }
        } catch (IOException e) {
            System.out.println("Failed to load accounts: " + e.getMessage());
        }
        return list;   // คืนรายการบัญชีทั้งหมดที่อ่านได้
    }
}