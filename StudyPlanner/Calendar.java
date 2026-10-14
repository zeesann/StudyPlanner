import java.time.LocalDate;
import java.time.YearMonth;
 

public class Calendar {
 
    private YearMonth month;                            // เดือนที่กำลังโชว์ (เก็บทั้งปีและเดือน)
    private LocalDate selectedDate = LocalDate.now();   // วันที่ถูกคลิกเลือก เริ่มที่วันนี้
 
    // เริ่มต้นที่เดือนปัจจุบัน
    public Calendar() {
        this.month = YearMonth.now();
    }
 
    // เดินหน้า 1 เดือน
    public void nextMonth() {
        month = month.plusMonths(1);
    }
 
    // ถอยหลัง 1 เดือน
    public void prevMonth() {
        month = month.minusMonths(1);
    }
 
    // กระโดดไปเดือนและปีที่ระบุ
    public void setMonth(int year, int monthNumber) {
        month = YearMonth.of(year, monthNumber);
    }
 
    // ขอปีที่โชว์อยู่
    public int getYear() {
        return month.getYear();
    }
 
    // ขอเลขเดือนที่โชว์อยู่
    public int getMonthNumber() {
        return month.getMonthValue();       // 1 = มกราคม, 12 = ธันวาคม
    }
 
    // เดือนนี้มีกี่วัน
    public int getDaysInMonth() {
        return month.lengthOfMonth();
    }
 
    // มีช่องว่างกี่ช่องก่อนวันที่ 1 (อาทิตย์ = 0)
    // จันทร์ = 1 ... อาทิตย์ = 7 แล้ว % 7 ทำให้อาทิตย์เป็น 0
    public int getBlankDays() {
        return month.atDay(1).getDayOfWeek().getValue() % 7;
    }
 
    // จำนวนช่องทั้งหมดในตาราง (ปัดให้เต็มสัปดาห์ละ 7 ช่องเสมอ)
    public int getTotalCells() {
        return (int) Math.ceil((getBlankDays() + getDaysInMonth()) / 7.0) * 7;
    }
 
    // ขอวันที่เต็มจากเลขวัน เช่น วันที่ 5 ของเดือนนี้
    public LocalDate getDate(int dayNum) {
        return month.atDay(dayNum);
    }
 
    
    // ขอวันที่ถูกเลือกอยู่
    public LocalDate getSelectedDate() {
        return selectedDate;
    }
 
    // จำว่าวันนี้ถูกเลือก
    public void setSelectedDate(LocalDate date) {
        selectedDate = date;
    }
}
 