package com.example.app.model;

import cn.hutool.core.date.ChineseDate;
import cn.hutool.core.date.DateUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A utility class for handling Chinese lunar calendar holiday calculations.
 * This class provides functionality to convert between Gregorian and Chinese lunar calendar dates,
 * and to identify traditional Chinese holidays in both calendar systems.
 */
public class ChineseHolidayCalendar {
    /** 
     * For demo purposes, only Spring Festival is considered a holiday.
     * In lunar calendar, Spring Festival is always on the first day of the first lunar month 
     */
    private static final int SPRING_FESTIVAL_MONTH = 1;
    private static final int SPRING_FESTIVAL_DAY = 1;
    
    /** Number of official holiday days for Spring Festival */
    private static final int SPRING_FESTIVAL_DAYS = 7;
    
    /**
     * Constructs a new ChineseHolidayCalendar instance.
     * No initialization is required for this class.
     */
    public ChineseHolidayCalendar() {
        // No initialization needed
    }
    
    /**
     * Gets the Spring Festival date (first day) for a specific lunar year in the Gregorian calendar.
     *
     * @param lunarYear the Chinese lunar year
     * @return the first day of Spring Festival as a LocalDate in the Gregorian calendar
     */
    public LocalDate getSpringFestivalDate(int lunarYear) {
        // Create lunar date for Spring Festival
        ChineseDate lunarDate = new ChineseDate(lunarYear, SPRING_FESTIVAL_MONTH, SPRING_FESTIVAL_DAY);
        // Convert to Gregorian date
        Date gregorianDate = lunarDate.getGregorianDate();
        // Convert to LocalDate
        return DateUtil.toLocalDateTime(gregorianDate).toLocalDate();
    }
    
    /**
     * Gets all Spring Festival holidays for a specific lunar year in the Gregorian calendar.
     *
     * @param lunarYear the Chinese lunar year
     * @return a list of LocalDates representing all days of the Spring Festival holiday period
     */
    public List<LocalDate> getSpringFestivalHolidays(int lunarYear) {
        List<LocalDate> holidays = new ArrayList<>();
        LocalDate startDate = getSpringFestivalDate(lunarYear);
        
        // Add all days of the Spring Festival holiday period
        for (int i = 0; i < SPRING_FESTIVAL_DAYS; i++) {
            holidays.add(startDate.plusDays(i));
        }
        
        return holidays;
    }
    
    /**
     * Gets all holidays for a given Gregorian calendar year.
     *
     * @param gregorianYear the Gregorian calendar year
     * @return a list of LocalDates representing all holiday dates in the specified year
     */
    public List<LocalDate> getHolidaysForYear(int gregorianYear) {
        List<LocalDate> allHolidays = new ArrayList<>();
        
        // Get lunar years that might have Spring Festival in this Gregorian year
        
        // First check: typically most of year will match with lunar year
        ChineseDate midYearLunar = new ChineseDate(DateUtil.parse(gregorianYear + "-06-15"));
        int primaryLunarYear = midYearLunar.getChineseYear();
        
        // Also check previous lunar year (for January/February)
        int previousLunarYear = primaryLunarYear - 1;
        
        // Get Spring Festival dates for both lunar years
        List<LocalDate> previousLunarYearHolidays = getSpringFestivalHolidays(previousLunarYear);
        List<LocalDate> primaryLunarYearHolidays = getSpringFestivalHolidays(primaryLunarYear);
        
        // Add holidays that fall in the requested Gregorian year
        for (LocalDate holiday : previousLunarYearHolidays) {
            if (holiday.getYear() == gregorianYear) {
                allHolidays.add(holiday);
            }
        }
        
        for (LocalDate holiday : primaryLunarYearHolidays) {
            if (holiday.getYear() == gregorianYear) {
                allHolidays.add(holiday);
            }
        }
        
        return allHolidays;
    }
    
    /**
     * Gets all holidays within a date range in the Gregorian calendar.
     *
     * @param startDate the start date of the range (inclusive)
     * @param endDate the end date of the range (inclusive)
     * @return a list of LocalDates representing all holiday dates within the specified range
     */
    public List<LocalDate> getHolidaysInPeriod(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> holidays = new ArrayList<>();
        
        // Get all possible holiday years in the range
        int startYear = startDate.getYear();
        int endYear = endDate.getYear();
        
        for (int year = startYear; year <= endYear; year++) {
            List<LocalDate> yearHolidays = getHolidaysForYear(year);
            for (LocalDate holiday : yearHolidays) {
                if (!holiday.isBefore(startDate) && !holiday.isAfter(endDate)) {
                    holidays.add(holiday);
                }
            }
        }
        
        return holidays;
    }
    
    /**
     * Gets all holidays within a date range in Chinese lunar calendar format.
     *
     * @param startDate the start date of the range in Gregorian calendar (inclusive)
     * @param endDate the end date of the range in Gregorian calendar (inclusive)
     * @return a list of ChineseDate objects representing all holiday dates in lunar calendar format
     */
    public List<ChineseDate> getHolidaysInPeriodLunar(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> gregorianHolidays = getHolidaysInPeriod(startDate, endDate);
        List<ChineseDate> lunarHolidays = new ArrayList<>();
        
        for (LocalDate date : gregorianHolidays) {
            ChineseDate lunarDate = toChineseDate(date);
            lunarHolidays.add(lunarDate);
        }
        
        return lunarHolidays;
    }
    
    /**
     * Checks if a given date is a holiday.
     *
     * @param date the date to check
     * @return true if the date is a holiday, false otherwise
     */
    public boolean isHoliday(LocalDate date) {
        List<LocalDate> yearHolidays = getHolidaysForYear(date.getYear());
        return yearHolidays.stream().anyMatch(holiday -> holiday.equals(date));
    }
    
    /**
     * Converts a Gregorian date to Chinese lunar date.
     *
     * @param date the Gregorian date to convert
     * @return the equivalent date in the Chinese lunar calendar
     */
    public ChineseDate toChineseDate(LocalDate date) {
        Date utilDate = DateUtil.date(date);
        return new ChineseDate(utilDate);
    }
    
    /**
     * Converts a Chinese lunar date to a Gregorian date.
     *
     * @param lunarYear the lunar year
     * @param lunarMonth the lunar month
     * @param lunarDay the lunar day
     * @return the equivalent date in the Gregorian calendar
     */
    public LocalDate fromChineseDate(int lunarYear, int lunarMonth, int lunarDay) {
        ChineseDate lunarDate = new ChineseDate(lunarYear, lunarMonth, lunarDay);
        Date gregorianDate = lunarDate.getGregorianDate();
        return DateUtil.toLocalDateTime(gregorianDate).toLocalDate();
    }
    
    /**
     * Formats a Gregorian date in Chinese lunar calendar representation.
     *
     * @param date the Gregorian date to format
     * @return a string representation of the date in Chinese lunar calendar format
     */
    public String formatChineseDate(LocalDate date) {
        ChineseDate chineseDate = toChineseDate(date);
        return chineseDate.toString();
    }
    
    /**
     * Gets the lunar year for a Gregorian date.
     *
     * @param date the Gregorian date
     * @return the corresponding Chinese lunar year
     */
    public int getLunarYear(LocalDate date) {
        ChineseDate chineseDate = toChineseDate(date);
        return chineseDate.getChineseYear();
    }
}