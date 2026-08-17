package com.example.gtd;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DateTest {
    @Test
    public void mondayReturnsSameDayWhenAlreadyMonday() {
        Date monday = new Date(17, 7, 2026);

        assertEquals(new Date(17, 7, 2026), monday.monday());
    }

    @Test
    public void mondayReturnsPreviousMondayWhenCalledOnSunday() {
        Date sunday = new Date(16, 7, 2026);

        assertEquals(new Date(10, 7, 2026), sunday.monday());
    }

    @Test
    public void mondayCrossesMonthAndYearBoundary() {
        Date thursday = new Date(1, 0, 2026);

        assertEquals(new Date(29, 11, 2025), thursday.monday());
    }

    @Test
    public void nextDayHandlesLeapDayAndNewYear() {
        assertEquals(new Date(29, 1, 2028), new Date(28, 1, 2028).nextDay());
        assertEquals(new Date(1, 0, 2027), new Date(31, 11, 2026).nextDay());
    }
}
