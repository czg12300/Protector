
package cn.protector.ui.helper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author 陈志刚
 * @Description 处理日期
 * @date 2014-7-17 下午3:43:30
 */
public class DateUtil {

    public static List<DayInfo> getDayListOfMonth(int year, int month) {
        int nextMontCount = -1;
        List<DayInfo> dayList = new ArrayList<DayInfo>();
        final int length = 43;
        boolean isLeapYear = isLeapYear(year);
        int positionMonthCount = getDaysOfMonth(isLeapYear, month);
        int lastMonthCount = getDaysOfMonth(isLeapYear, month - 1);
        int indexOfWeek = getWeekdayOfMonth(year, month);
        int indexDay = -1;
        for (int i = lastMonthCount - indexOfWeek + 1; i < lastMonthCount + 1; i++) {
            ++indexDay;
            dayList.add(new DayInfo(DayInfo.TYPE_LAST_MONTH, i));
        }
        for (int i = 1; i < positionMonthCount; i++) {
            ++indexDay;
            dayList.add(new DayInfo(DayInfo.TYPE_POSITION_MONTH, i));
        }
        nextMontCount = length - indexDay - 1;
        for (int i = 1; i < nextMontCount; i++) {
            ++indexDay;
            dayList.add(new DayInfo(DayInfo.TYPE_NEXT_MONTH, i));
        }
        return dayList;
    }

    public static int getDaysOfMonth(boolean isLeapYear, int month) {
        int daysOfMonth = 31;
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                daysOfMonth = 31;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                daysOfMonth = 30;
                break;
            case 2:
                if (isLeapYear) {
                    daysOfMonth = 29;
                } else {
                    daysOfMonth = 28;
                }

        }
        return daysOfMonth;
    }

    // 指定某年中的某月的第一天是星期几
    public static int getWeekdayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, 1);
        return cal.get(Calendar.DAY_OF_WEEK) - 1;
    }

    // 判断是否是闰年
    public static boolean isLeapYear(int year) {
        if (year % 100 == 0 && year % 400 == 0) {
            return true;
        } else if (year % 100 != 0 && year % 4 == 0) {
            return true;
        }
        return false;
    }

    // 获取当前日期
    public static int[] getDateInt() {
        int[] data = new int[3];
        Calendar c = Calendar.getInstance();
        data[0] = c.get(Calendar.YEAR);
        data[1] = c.get(Calendar.MONTH) + 1;
        data[2] = c.get(Calendar.DAY_OF_MONTH);
        return data;
    }

    public static class DayInfo {
        public static final int TYPE_LAST_MONTH = 0;

        public static final int TYPE_POSITION_MONTH = 1;

        public static final int TYPE_NEXT_MONTH = 2;

        public int day;

        public int type;

        public DayInfo(int type, int day) {
            this.type = type;
            this.day = day;
        }
    }
}
