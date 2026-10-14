public class Subject {

      // ชื่อวิชา เป็น private คือไฟล์อื่นแก้ตรงๆ ไม่ได้ ต้องผ่าน get/set 
      private String name;

    // construc สร้างวิชาใหม่ต้องบอกชื่อ เอาชื่อที่ส่งมาเก็บใส่ตัวแปรของ object
    public Subject(String name) {
        this.name = name;   
    }
 
    // ขอดูชื่อวิชา
    public String getName() {
        return name;
    }
 
    // เปลี่ยนชื่อวิชา 
    public void setName(String name) {
        this.name = name;
    }
 
    // toString: กำหนดว่าตอนเอา object นี้ไป "แสดงเป็นข้อความ" จะออกมาเป็นอะไร
    // เอา Subject ไปใส่ JList,JComboBox จะเรียกเมธอดนี้เพื่อเอาข้อความมาโชว์
    @Override
    public String toString() {
        return  " " + name;   
    }
    
}
