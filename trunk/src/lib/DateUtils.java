/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lib;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author minhdbh
 */
public class DateUtils {

    public static final String FILED15FORMAT = "yyyyMMdd HH:mm:ss";
    public static final String FILED7FORMAT = "yyyyMMddHHmmss";
    public static final String DATETIMENOW = "yyyy/MM/dd HH:mm:ss";
    public static final String LOCALDATETIMEIST = "yyyyMMdd HHmmss";
    public static final String DATEFORMATIST = "MMdd";
    public static final String DATEFORMAT = "yyyyMMdd";
    public static final String DATEYEARFORMAT = "yyMMdd";
    public static final String DATETIMEFORMATIST = "MMddHHmmss";
    public static final String YEARFORMAT = "yyyy";
    public static final String TIMEFORMAT = "HHmmss";

    /**
     * Get current date time
     * @return datetime
     */
    public static Date getDate() {
        Date nowDate = new Date();
        return nowDate;
    }

    /**
     *
     * @return Date value formatted by IST
     */
    public static String getCurrentDateIST() {
        SimpleDateFormat setDateFormat = new SimpleDateFormat(DATEFORMATIST);
        return setDateFormat.format(getDate());
    }

    /**
     *
     * @return Date value formatted by normal using
     */
    public static String getCurrentDate() {
        SimpleDateFormat setDateFormat = new SimpleDateFormat(DATEFORMAT);
        return setDateFormat.format(getDate());
    }

    public static String getF7GMT() {
        Calendar caldate = new GregorianCalendar();
        caldate.add(Calendar.HOUR, -7);
        SimpleDateFormat setDateFormat = new SimpleDateFormat("MMddHHmmss");
        return setDateFormat.format(caldate.getTime());
    }

    /**
     *
     * @return Date value formatted by normal using formatedString
     */
    public static String getCurrentDate(String formatedString) {
        SimpleDateFormat setDateFormat = new SimpleDateFormat(formatedString);
        return setDateFormat.format(getDate());
    }

    public static String getDateInFormat(Date pDate, String formatedString) {
        SimpleDateFormat setDateFormat = new SimpleDateFormat(formatedString);
        return setDateFormat.format(pDate);
    }

    /**
     *
     * @return Date value formatted by normal using formatedString
     */
    public static Date getDate(String formatedString, String dateValue) {
        SimpleDateFormat setDateFormat = new SimpleDateFormat(formatedString);
        try {
            return setDateFormat.parse(dateValue);
        } catch (Exception ex) {
            return getDate();
        }
    }

    public static String getCurrentYear() {
        SimpleDateFormat setDateFormat = new SimpleDateFormat(YEARFORMAT);
        return setDateFormat.format(getDate());
    }

    public static String getCurrentDateYear() {
        SimpleDateFormat setDateFormat = new SimpleDateFormat(DATEYEARFORMAT);
        return setDateFormat.format(getDate());
    }

    /**
     *
     * @return DateTime value formatted by IST
     */
    public static String getCurrentDateTime() {
        SimpleDateFormat setDateFormat = new SimpleDateFormat(DATETIMEFORMATIST);
        return setDateFormat.format(getDate());
    }

    /**
     * check the date between two date value, return true if datecheck is greater than startDate
     * and less than enddate
     * @param datecheck
     * @param startDate
     * @param endDate
     * @return
     */
    public static boolean CheckDateBetweenDate(Date datecheck, Date startDate, Date endDate) {
        if ((datecheck.getTime() <= endDate.getTime()) && (datecheck.getTime() >= startDate.getTime())) {
            return true;
        } else {
            return false;
        }
    }

    public static long DateDiff(DateTimeEnum dateDiffType, Date date1, Date date2) {
        long diffvalue = Integer.MAX_VALUE;
        //Calendar caldate1 = new GregorianCalendar();
        //Calendar caldate2 = new GregorianCalendar();
        //caldate1.setTime(date1);
        //caldate2.setTime(date2);
        switch (dateDiffType) {


            case MINUTE:
                diffvalue = Math.abs(date1.getTime() - date2.getTime()) / (1000 * 60);
                //diffvalue=date2.get
                break;
            case SECOND:
                diffvalue = Math.abs(date1.getTime() - date2.getTime()) / 1000;
                break;
            case MILISECOND:
                diffvalue = Math.abs(date1.getTime() - date2.getTime()) ;
                break;
            default:
                break;
        }
        return diffvalue;
    }

    public static Date DateDiff(DateTimeEnum dateDiffType, int value, Date sourcedate) {
        Calendar caldate = new GregorianCalendar();

        caldate.setTime(sourcedate);

        switch (dateDiffType) {
            case DAY:
                caldate.add(Calendar.DAY_OF_YEAR, value);
                break;
            case HOUR24:
                caldate.add(Calendar.HOUR_OF_DAY, value);
                break;
            case HOUR12:
                caldate.add(Calendar.HOUR, value);
                break;
            case MONTH:
                caldate.add(Calendar.MONTH, value);
                break;
            case MINUTE:
                caldate.add(Calendar.MINUTE, value);
                break;
            case SECOND:
                caldate.add(Calendar.SECOND, value);
                break;
            case MILISECOND:
                caldate.add(Calendar.MILLISECOND, value);
                break;
            default:
                break;
        }
        return caldate.getTime();
    }

    public static int DatePart(DateTimeEnum partType, Date value) {
        int result = 0;
        Calendar caldate1 = new GregorianCalendar();
        switch (partType) {
            case DAY:
                result = caldate1.get(Calendar.DAY_OF_MONTH);
                break;
            case HOUR24:
                result = caldate1.get(Calendar.HOUR_OF_DAY);
                break;
            case HOUR12:
                result = caldate1.get(Calendar.HOUR);
                break;
            case MONTH:
                result = caldate1.get(Calendar.MONTH);
                break;
            case MINUTE:
                result = caldate1.get(Calendar.MINUTE);
                break;
            case SECOND:
                result = caldate1.get(Calendar.SECOND);
                break;
            case MILISECOND:
                result = caldate1.get(Calendar.MILLISECOND);
                break;
            case YEAR:
                result = caldate1.get(Calendar.YEAR);
                break;

        }
        return result;
    }

    public static String getSettDate(String pSettTime) {

        String pResult = "";
        Date settdate = DateUtils.getDate("yyyyMMdd HH:mm:ss", DateUtils.getCurrentDate() + " " + pSettTime);
        Date endday = DateUtils.getDate("yyyyMMdd HH:mm:ss", DateUtils.getCurrentDate() + " 23:59:59");
        if (DateUtils.CheckDateBetweenDate(DateUtils.getDate(), settdate, endday)) {
            pResult = getDateInFormat(DateDiff(DateTimeEnum.DAY, 1, DateUtils.getDate()), "MMdd");

        } else {
            pResult = getCurrentDateIST();
        }

        return pResult;

    }

    public static String getSettDateForMEPS(String pSettValue) {

        String pResult = "";
        Date currSettdate = DateUtils.getDate("yyyyMMdd", DateUtils.DatePart(DateTimeEnum.YEAR, getDate()) + pSettValue);
        Date trueSettdate = DateDiff(DateTimeEnum.DAY, 1, currSettdate);

        pResult = getDateInFormat(trueSettdate, "MMdd");

        return pResult;

    }

    public static String getDateTimeZone(Date pDatesource, int pHour) {
        SimpleDateFormat setDateFormat = new SimpleDateFormat(DATETIMEFORMATIST);
        return setDateFormat.format(DateDiff(DateTimeEnum.HOUR24, pHour, getDate()));

    }
    public static String getTime() {
        SimpleDateFormat setDateFormat = new SimpleDateFormat(TIMEFORMAT);
        return setDateFormat.format(getDate());

    }
    public static String getDateTimeZone(String pDateSource, int pHour) {

        Date currDate = DateUtils.getDate("yyyyMMddHH24mmss", DateUtils.getCurrentDateYear() + pDateSource);
        return getDateTimeZone(currDate, pHour);

    }
}
