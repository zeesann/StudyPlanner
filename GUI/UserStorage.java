import java.io.*;
import java.util.*;

/**
 * เก็บบัญชีผู้ใช้ (username, password) ลงไฟล์ users.txt
 * รูปแบบไฟล์: 1 บรรทัดต่อ 1 บัญชี คั่นด้วย | เช่น  somchai|1234
 */
public class UserStorage {

    private static final String FILE_NAME = "users.txt";

    // ---------- เช็คว่า username/password ตรงกับที่เคยสมัครไว้ไหม ----------
    public static boolean validateLogin(String username, String password) {
        for (String[] user : loadUsers()) {
            if (user[0].equals(username) && user[1].equals(password)) {
                return true;
            }
        }
        return false;
    }

    // ---------- เช็คว่ามีคนใช้ username นี้ไปแล้วหรือยัง ----------
    public static boolean usernameExists(String username) {
        for (String[] user : loadUsers()) {
            if (user[0].equals(username)) return true;
        }
        return false;
    }

    // ---------- สมัครสมาชิกใหม่ คืน true ถ้าสำเร็จ, false ถ้า username ซ้ำ ----------
    public static boolean registerUser(String username, String password) {
        if (usernameExists(username)) {
            return false;
        }
        try (FileWriter writer = new FileWriter(FILE_NAME, true)) { // true = ต่อท้ายไฟล์เดิม ไม่ลบของเก่า
            writer.write(username + "|" + password + "\n");
            return true;
        } catch (IOException e) {
            System.out.println("บันทึกบัญชีไม่สำเร็จ: " + e.getMessage());
            return false;
        }
    }

    // ---------- เปลี่ยนรหัสผ่านของ username ที่ระบุ คืน true ถ้าเจอและเปลี่ยนสำเร็จ ----------
    public static boolean resetPassword(String username, String newPassword) {
        List<String[]> users = loadUsers();
        boolean found = false;

        for (String[] u : users) {
            if (u[0].equals(username)) {
                u[1] = newPassword;
                found = true;
                break;
            }
        }
        if (!found) return false;

        try (FileWriter writer = new FileWriter(FILE_NAME, false)) { // false = เขียนทับไฟล์เดิมทั้งหมด
            for (String[] u : users) {
                writer.write(u[0] + "|" + u[1] + "\n");
            }
            return true;
        } catch (IOException e) {
            System.out.println("บันทึกไม่สำเร็จ: " + e.getMessage());
            return false;
        }
    }

    // ---------- โหลดบัญชีทั้งหมดจากไฟล์ ----------
    private static List<String[]> loadUsers() {
        List<String[]> list = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) return list; // ยังไม่เคยมีใครสมัครเลย คืน list ว่าง

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) list.add(parts);
            }
        } catch (IOException e) {
            System.out.println("โหลดบัญชีไม่สำเร็จ: " + e.getMessage());
        }
        return list;
    }
}