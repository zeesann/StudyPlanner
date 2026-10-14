// เก็บแค่ username อย่างเดียว 
public class User {
    // ตั้งค่าได้ครั้งเดียวตอนสร้าง object จากนั้นเปลี่ยนไม่ได้
    private final String username;

    //  new User("ชืื่อ") จะเอาชื่อที่ส่งมาเก็บใส่ตัวแปร username
    public User(String username) {
        this.username = username;  
    }

    // get เอาไว้ให้ไฟล์อื่นขอดูชื่อผู้ใช้ ตัวแปรเป็น private เข้าถึงตรงไม่ได้
    public String getUsername() {
        return username;
    }
}