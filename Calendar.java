import java.time.LocalDate;
import java.time.YearMonth;
 
/**
 * Calendar logic (no GUI here).
 * It remembers which month is shown and which day is selected,
 * and it does all the date calculations for CalendarGUI.
 */
public class Calendar {
 
    private YearMonth month;                            // the month that is shown
    private LocalDate selectedDate = LocalDate.now();   // the day that was clicked
 
    // Start at the current month
    public Calendar() {
        this.month = YearMonth.now();
    }
 
    // ---------- Month ----------
    public void nextMonth() {
        month = month.plusMonths(1);
    }
 
    public void prevMonth() {
        month = month.minusMonths(1);
    }
 
    public void setMonth(int year, int monthNumber) {
        month = YearMonth.of(year, monthNumber);
    }
 
    public int getYear() {
        return month.getYear();
    }
 
    public int getMonthNumber() {
        return month.getMonthValue();       // 1 = January, 12 = December
    }
 
    // ---------- Days in the grid ----------
    public int getDaysInMonth() {
        return month.lengthOfMonth();
    }
 
    // How many blank cells come before day 1 (Sunday = 0)
    public int getBlankDays() {
        return month.atDay(1).getDayOfWeek().getValue() % 7;
    }
 
    // Total cells in the grid (always full weeks of 7)
    public int getTotalCells() {
        return (int) Math.ceil((getBlankDays() + getDaysInMonth()) / 7.0) * 7;
    }
 
    // The date of a day number in this month
    public LocalDate getDate(int dayNum) {
        return month.atDay(dayNum);
    }
 
    // ---------- Selected day ----------
    public LocalDate getSelectedDate() {
        return selectedDate;
    }
 
    public void setSelectedDate(LocalDate date) {
        selectedDate = date;
    }
}
 
