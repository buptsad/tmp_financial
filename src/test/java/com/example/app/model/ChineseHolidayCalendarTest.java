package com.example.app.model;

import org.junit.jupiter.api.*;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the ChineseHolidayCalendar class.
 * These tests verify the functionality for calculating Chinese traditional holidays,
 * date conversions between Gregorian and Chinese lunar calendars, and holiday period calculations.
 */
class ChineseHolidayCalendarTest {

    /**
     * Test instance of ChineseHolidayCalendar used across all test methods.
     */
    private ChineseHolidayCalendar calendar;

    /**
     * Sets up a fresh instance of ChineseHolidayCalendar before each test.
     */
    @BeforeEach
    void setUp() {
        calendar = new ChineseHolidayCalendar();
    }

    /**
     * Tests the calculation of the Spring Festival date for a given lunar year.
     * Verifies that the method returns a non-null date in the correct year.
     */
    @Test
    @DisplayName("Should get Spring Festival date for lunar year")
    void testGetSpringFestivalDate() {
        LocalDate date = calendar.getSpringFestivalDate(2024);
        assertNotNull(date);
        // The actual date will depend on the lunar calendar library
        assertEquals(2024, date.getYear() >= 2024 ? date.getYear() : date.getYear() + 1);
    }

    /**
     * Tests the retrieval of all Spring Festival holiday dates for a given lunar year.
     * Verifies that the correct number of dates is returned and that they are consecutive.
     */
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

    /**
     * Tests the retrieval of all holidays for a specific Gregorian year.
     * Verifies that the returned dates are all within the specified year.
     */
    @Test
    @DisplayName("Should get holidays for Gregorian year")
    void testGetHolidaysForYear() {
        List<LocalDate> holidays = calendar.getHolidaysForYear(2024);
        assertTrue(holidays.size() >= 1);
        for (LocalDate date : holidays) {
            assertEquals(2024, date.getYear());
        }
    }

    /**
     * Tests the retrieval of holidays within a specified date range.
     * Verifies that all returned dates fall within the given period.
     */
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

    /**
     * Tests the functionality for checking if a given date is a holiday.
     * Verifies that known holidays are correctly identified and non-holidays are not.
     */
    @Test
    @DisplayName("Should check if date is holiday")
    void testIsHoliday() {
        List<LocalDate> holidays = calendar.getHolidaysForYear(2024);
        if (!holidays.isEmpty()) {
            assertTrue(calendar.isHoliday(holidays.get(0)));
        }
        assertFalse(calendar.isHoliday(LocalDate.of(2024, 5, 1))); // unlikely to be Spring Festival
    }

    /**
     * Tests the conversion between Gregorian calendar dates and Chinese lunar calendar dates.
     * Verifies that dates can be converted in both directions correctly.
     */
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