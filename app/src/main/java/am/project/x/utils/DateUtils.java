package am.project.x.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * 日期工具
 *
 * @author Alex
 */
@SuppressWarnings("unused")
public class DateUtils {
    public static final String PATTERN_FULL_0 = "yyyyMMddHHmmss";
    public static final String PATTERN_FULL_1 = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_FULL_2 = "yyyy/MM/dd HH:mm:ss";
    public static final String PATTERN_FULL_NS_1 = "yyyy-MM-dd HH:mm";
    public static final String PATTERN_FULL_NS_2 = "yyyy/MM/dd HH:mm";
    public static final String PATTERN_DATE_0 = "yyyyMMdd";
    public static final String PATTERN_DATE_1 = "yyyy-MM-dd";
    public static final String PATTERN_DATE_2 = "yyyy/MM/dd";
    public static final String PATTERN_TIME_1 = "HH:mm:ss";
    public static final String PATTERN_TIME_NS_1 = "HH:mm";
    public static final String PATTERN_YEAR = "yyyy";
    public static final String PATTERN_HH = "HH";
    public static final String PATTERN_MM = "mm";
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat(PATTERN_FULL_1,
            Locale.getDefault());


    /**
     * 获取日期格式化字符串
     *
     * @param pattern 格式模板
     * @return 输出字符串
     */
    public static String getCalendarStr(String pattern) {
        return getCalendarStr(pattern, new GregorianCalendar().getTime());
    }

    /**
     * 获取日期格式化字符串
     *
     * @param pattern 格式模板
     * @return 输出字符串
     */
    public static String getCalendarStr(String pattern, long date) {
        return getCalendarStr(pattern, new Date(date));
    }

    /**
     * 获取日期格式化字符串
     *
     * @param pattern 格式模板
     * @return 输出字符串
     */
    public static String getCalendarStr(String pattern, Date date) {
        FORMAT.applyLocalizedPattern(pattern);
        return FORMAT.format(date);
    }

    /**
     * 获取指定格式日期的毫秒数
     *
     * @return 毫秒
     */
    public static long getMillis(String TargetTime, String pattern) {
        Calendar calendar = Calendar.getInstance();
        FORMAT.applyLocalizedPattern(pattern);
        try {
            // 特定格式的时间
            calendar.setTime(FORMAT.parse(TargetTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar.getTimeInMillis();
    }

    /**
     * 根据年份和年的第周(从年第一天开始)，获得指定周第最后天的MM.dd格式的日期。
     *
     * @param year       年
     * @param weekOfYear 周数
     * @return 输出字符串
     */
    public static String getLastMonthAndDate(int year, int weekOfYear) {

        long oneDate = 24 * 60 * 60 * 1000;
        long oneWeekMillis = 7 * 24 * 60 * 60 * 1000;
        long sixDate = 6 * 24 * 60 * 60 * 1000;

        StringBuilder monthAndDate = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",
                Locale.getDefault());
        Calendar cl = Calendar.getInstance();
        try {
            // 特定格式的时间
            cl.setTime(sdf.parse(year + "-01-01"));
            cl.setTimeInMillis(cl.getTimeInMillis() - oneDate * (cl.get(Calendar.DAY_OF_WEEK) - 1));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cl2 = Calendar.getInstance();
        long weekLasttDateMillis = cl.getTimeInMillis() + (weekOfYear - 1)
                * oneWeekMillis + sixDate;
        cl2.setTimeInMillis(weekLasttDateMillis);
        int month = cl2.get(Calendar.MONTH) + 1;
        if (month < 10) {
            monthAndDate.append("0");
        }
        monthAndDate.append(month);
        monthAndDate.append(".");

        int day = cl2.get(Calendar.DAY_OF_MONTH);
        if (day < 10) {
            monthAndDate.append("0");
        }
        monthAndDate.append(day);
        return monthAndDate.toString();
    }

    /**
     * 根据年份和年的第几周(从年第一天开始)，获得指定周最-天的MM.dd格式的日期。
     *
     * @param year       年
     * @param weekOfYear 周数
     * @return 输出字符串
     */
    public static String getFirstMonthAndDate(int year, int weekOfYear) {

        long oneDate = 24 * 60 * 60 * 1000;
        long oneWeekMillis = 7 * 24 * 60 * 60 * 1000;

        StringBuilder monthAndDate = new StringBuilder();
        FORMAT.applyLocalizedPattern(PATTERN_DATE_1);
        Calendar cl = Calendar.getInstance();
        try {
            // 特定格式的时间
            cl.setTime(FORMAT.parse(year + "-01-01"));
            cl.setTimeInMillis(cl.getTimeInMillis() - oneDate * (cl.get(Calendar.DAY_OF_WEEK) - 1));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cl2 = Calendar.getInstance();
        long weekFirstDateMillis = cl.getTimeInMillis() + (weekOfYear - 1)
                * oneWeekMillis;
        cl2.setTimeInMillis(weekFirstDateMillis);
        int month = cl2.get(Calendar.MONTH) + 1;
        if (month < 10) {
            monthAndDate.append("0");
        }
        monthAndDate.append(month);
        monthAndDate.append(".");

        int day = cl2.get(Calendar.DAY_OF_MONTH);
        if (day < 10) {
            monthAndDate.append("0");
        }
        monthAndDate.append(day);
        return monthAndDate.toString();
    }

    /**
     * 根据绝对毫秒数计算当前日期所处周数
     *
     * @param currentTimeMillis 当前的毫秒数
     * @return 周数
     */
    public static int getWeekByMillis(long currentTimeMillis) {
        FORMAT.applyLocalizedPattern(PATTERN_YEAR);
        String yearStr = FORMAT.format(currentTimeMillis) + "-01-01";
        long yearBeginMillis = getMillis(yearStr, PATTERN_DATE_1);
        long oneWeekMillis = 7 * 24 * 60 * 60 * 1000;
        return (int) ((currentTimeMillis - yearBeginMillis) / oneWeekMillis + 1);
    }

    /**
     * 得到当前日期是星期几。
     *
     * @param timeMillis 毫秒数
     * @return 当为周日时，返回0，当为周一至周六时，则返回对应的1-6。
     */
    public static int getCurrentDayOfWeek(long timeMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }

    /**
     * 得到当前日期是星期几。
     *
     * @param date 日期
     * @return 当为周日时，返回0，当为周一至周六时，则返回对应的1-6。
     */
    public static int getCurrentDayOfWeek(String date) {
        Calendar calendar = Calendar.getInstance();
        FORMAT.applyLocalizedPattern(PATTERN_DATE_0);
        try {
            calendar.setTime(FORMAT.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }
}
