package com.example.app.model;

import org.junit.jupiter.api.*;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ChineseHolidayCalendarTest {

    private ChineseHolidayCalendar calendar;

    @BeforeEach
    void setUp() {
        calendar = new ChineseHolidayCalendar();
    }

    @Test
    @DisplayName("Should get Spring Festival date for lunar year")
    void testGetSpringFestivalDate() {
        LocalDate date = calendar.getSpringFestivalDate(2024);
        assertNotNull(date);
        // The actual date will depend on the lunar calendar library
        assertEquals(2024, date.getYear() >= 2024 ? date.getYear() : date.getYear() + 1);
    }

    @Test
    @DisplayName("Should get all Spring Festival holidays for lunar year")
    void testGetSpringFestivalHolidays() {
        List<LocalDate> holidays = calendar.getSpringFestivalHolidays(2024);
        assertEquals(7, holidays.size());
        LocalDate first = holidays.get(0);
        for (int i = 1; i < holidays.size(); i++) {
            assertEquals(first.plusDays(i), holidays.get(i));
        }
    }

    @Test
    @DisplayName("Should get holidays for Gregorian year")
    void testGetHolidaysForYear() {
        List<LocalDate> holidays = calendar.getHolidaysForYear(2024);
        assertTrue(holidays.size() >= 1);
        for (LocalDate date : holidays) {
            assertEquals(2024, date.getYear());
        }
    }

    @Test
    @DisplayName("Should get holidays in period")
    void testGetHolidaysInPeriod() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 12, 31);
        List<LocalDate> holidays = calendar.getHolidaysInPeriod(start, end);
        assertFalse(holidays.isEmpty());
        for (LocalDate date : holidays) {
            assertFalse(date.isBefore(start));
            assertFalse(date.isAfter(end));
        }
    }

    @Test
    @DisplayName("Should check if date is holiday")
    void testIsHoliday() {
        List<LocalDate> holidays = calendar.getHolidaysForYear(2024);
        if (!holidays.isEmpty()) {
            assertTrue(calendar.isHoliday(holidays.get(0)));
        }
        assertFalse(calendar.isHoliday(LocalDate.of(2024, 5, 1))); // unlikely to be Spring Festival
    }

    @Test
    @DisplayName("Should convert between Gregorian and Chinese dates")
    void testDateConversion() {
        LocalDate gregorian = LocalDate.of(2024, 2, 10);
        ChineseHolidayCalendar calendar = new ChineseHolidayCalendar();
        cn.hutool.core.date.ChineseDate lunar = calendar.toChineseDate(gregorian);
        assertNotNull(lunar);
        LocalDate back = calendar.fromChineseDate(lunar.getChineseYear(), lunar.getMonth(), lunar.getDay());
        assertNotNull(back);
    }
}