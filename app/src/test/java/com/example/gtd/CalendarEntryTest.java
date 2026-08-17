package com.example.gtd;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CalendarEntryTest {
    @Test
    public void normalizedTimeConvertsToMinutes() {
        CalendarEntry entry = new CalendarEntry("Call", "13:05", new Date(17, 7, 2026));

        assertEquals(785, entry.getMinutesSinceMidnight());
    }

    @Test
    public void missingTimeSortsAfterTimedEntries() {
        CalendarEntry entry = new CalendarEntry("Note", "", new Date(17, 7, 2026));

        assertEquals(Integer.MAX_VALUE, entry.getMinutesSinceMidnight());
    }

    @Test
    public void invalidTimeIsRejectedForSorting() {
        CalendarEntry entry = new CalendarEntry("Call", "25:90", new Date(17, 7, 2026));

        assertEquals(Integer.MAX_VALUE, entry.getMinutesSinceMidnight());
    }
}
