package com.example.gtd;

import java.util.ArrayList;
import java.util.Objects;

public class Date {
    private int num;
    private int month;
    private int year;

    public Date(int num, int month, int year) {
        this.num = num;
        this.month = month;
        this.year = year;
    }

    public int getNum() {
        return num;
    }

    public String getMonth() {
        return (new ArrayList<String>() {{
            add("January");
            add("February");
            add("March");
            add("April");
            add("May");
            add("June");
            add("July");
            add("August");
            add("September");
            add("October");
            add("November");
            add("December");
        }}).get(month);
    }

    public int getMonthIndex() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public Date monday() {
        Date res = this;
        while (res.getWeekday() != 1) {
            res = res.prevDay();
        }
        return res.nextDay();
    }

    public Date prevDay() {
        int prevNum = num - 1;
        int prevMonth = month;
        int prevYear = year;

        if (prevNum < 1) {
            prevMonth--;
            if (prevMonth < 0) {
                prevMonth = 11;
                prevYear--;
            }
            prevNum = getMonthLength(prevMonth, prevYear);
        }

        return new Date(prevNum, prevMonth, prevYear);
    }

    public int getWeekday() {
        // Zeller's Congruence algorithm to calculate the day of the week
        int m = month + 1; // Adjust month for Zeller's formula (March=3, ..., February=14)
        int y = year;
        if (m < 3) {
            m += 12;
            y--;
        }
        int k = y % 100;
        int j = y / 100;
        int f = num + (13 * (m + 1)) / 5 + k + (k / 4) + (j / 4) - (2 * j);
        return ((f % 7) + 7) % 7; // Return value in range [0,6] where 0=Saturday, ..., 6=Friday
    }

    public int getMonthLength(int month, int year) {
        switch (month) {
            case 0: // January
            case 2: // March
            case 4: // May
            case 6: // July
            case 7: // August
            case 9: // October
            case 11: // December
                return 31;
            case 3: // April
            case 5: // June
            case 8: // September
            case 10: // November
                return 30;
            case 1: // February
                if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
                    return 29; // Leap year
                } else {
                    return 28; // Non-leap year
                }
            default:
                throw new IllegalArgumentException("Invalid month: " + month);
        }
    }

    public Date nextDay() {
        int nextNum = num + 1;
        int nextMonth = month;
        int nextYear = year;

        if (nextNum > getMonthLength(month, year)) {
            nextNum = 1;
            nextMonth++;
            if (nextMonth > 11) {
                nextMonth = 0;
                nextYear++;
            }
        }

        return new Date(nextNum, nextMonth, nextYear);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Date)) return false;
        Date date = (Date) other;
        return num == date.num && month == date.month && year == date.year;
    }

    @Override
    public int hashCode() {
        return Objects.hash(num, month, year);
    }

}
