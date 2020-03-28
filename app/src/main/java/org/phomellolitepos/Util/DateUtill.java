package org.phomellolitepos.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


import android.annotation.SuppressLint;

@SuppressLint("SimpleDateFormat")
public class DateUtill {

    public static SimpleDateFormat simFormat = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", new Locale("en"));
    public static final String dateFormat = "\nex. 2012-12-31";


    public Date getDate(String date) throws ParseException {
        return simFormat.parse(date);
    }

    public String getDate(Date date) {
        return simFormat.format(date);
    }

    public Date getDate2(Date date) throws ParseException {
        String d = simFormat.format(date);
        return simFormat.parse(d);
    }

    public String currentDate() {
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("en"));
        return format.format(d);
    }

    public static String currentDatebackup() {
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy", new Locale("en"));
        return format.format(d);
    }

    public static String currentDatereport() {
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("ddMMyyyyHHmmss", new Locale("en"));
        return format.format(d);
    }

    public static String currentDateWithoutTime10() {
        Date d = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.DATE, 10);
        d = cal.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", new Locale("en"));
        return format.format(d);
    }

    public static String currentDateWithoutTime() {
        Date d = new Date();
        SimpleDateFormat simFormat = new SimpleDateFormat("yyyy-MM-dd", new Locale("en"));
        return simFormat.format(d);
    }

    public static String DateTimeAmPPM() {
        Date d = new Date();
        SimpleDateFormat simFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm a", new Locale("en"));
        return simFormat.format(d);
    }

    public static String DateAmPPM() {
        Date dt = new Date();
        SimpleDateFormat postFormater = new SimpleDateFormat("dd-MMM-yyyy", new Locale("en"));
        return postFormater.format(dt);
    }

    public static String Reportnamedate() {
        Date dt = new Date();
        SimpleDateFormat postFormater = new SimpleDateFormat("yyyyMMddHHmmss", new Locale("en"));
        return postFormater.format(dt);
    }

    public static String SelectedDate(String Date) {
        SimpleDateFormat curFormater = new SimpleDateFormat("dd-MM-yyyy", new Locale("en"));
        Date dateObj = null;
        String newDateStr = "";
        try {
            dateObj = curFormater.parse(Date);
            SimpleDateFormat postFormater = new SimpleDateFormat("dd-MMM-yyyy", new Locale("en"));
            newDateStr = postFormater.format(dateObj);
        } catch (ParseException e) {
        }
        return newDateStr;
    }

    public static String PaternDate1(String Date) {
        SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("en"));
        Date dateObj = null;
        String newDateStr = "";
        try {
            dateObj = curFormater.parse(Date);
            SimpleDateFormat postFormater = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", new Locale("en"));
            newDateStr = postFormater.format(dateObj);
        } catch (ParseException e) {
        }
        return newDateStr;
    }
    public static String PaternDatePrintDate(String Date) {
        SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("en"));
        Date dateObj = null;
        String newDateStr = "";
        try {
            dateObj = curFormater.parse(Date);
            SimpleDateFormat postFormater = new SimpleDateFormat("dd-MMM-yyyy", new Locale("en"));
            newDateStr = postFormater.format(dateObj);
        } catch (ParseException e) {
        }
        return newDateStr;
    }

    public static String PaternDatePrintTime(String Date) {
        SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("en"));
        Date dateObj = null;
        String newDateStr = "";
        try {
            dateObj = curFormater.parse(Date);
            SimpleDateFormat postFormater = new SimpleDateFormat("HH:mm", new Locale("en"));
            newDateStr = postFormater.format(dateObj);
        } catch (ParseException e) {
        }
        return newDateStr;
    }

    public static String PaternDate2(String Date) {
        SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd", new Locale("en"));
        Date dateObj = null;
        String newDateStr = "";
        try {
            dateObj = curFormater.parse(Date);
            SimpleDateFormat postFormater = new SimpleDateFormat("dd-MMM-yyyy", new Locale("en"));
            newDateStr = postFormater.format(dateObj);
        } catch (ParseException e) {
        }
        return newDateStr;
    }


    public static String PaternDateSend(String Date) {
        SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd", new Locale("en"));
        Date dateObj = null;
        String newDateStr = "";
        try {
            dateObj = curFormater.parse(Date);
            SimpleDateFormat postFormater = new SimpleDateFormat("yyyy-MM-dd", new Locale("en"));
            newDateStr = postFormater.format(dateObj);
        } catch (Exception e) {
            newDateStr = "1990-01-01 00:00:00";
        }
        return newDateStr;
    }

    public static String PaternDateTime(String Date) {
        SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("en"));
        Date dateObj = null;
        String newDateStr = "";
        try {
            dateObj = curFormater.parse(Date);
            SimpleDateFormat postFormater = new SimpleDateFormat("dd-MMM-yyyy HH:mm a", new Locale("en"));
            newDateStr = postFormater.format(dateObj);
        } catch (ParseException e) {
        }
        return newDateStr;
    }

    public static String CheckQueryDate(String Date) {
        SimpleDateFormat curFormater = new SimpleDateFormat("dd-MMM-yyyy", new Locale("en"));
        Date dateObj = null;
        String chkfrom = "1990-01-01";
        try {
            dateObj = curFormater.parse(Date);
            SimpleDateFormat postFormater = new SimpleDateFormat("yyyy-MM-dd", new Locale("en"));
            chkfrom = postFormater.format(dateObj);
        } catch (ParseException e) {
            chkfrom = "1990-01-01";
        }
        return chkfrom;

    }

//    public static String CheckQuerybeforeonedayDate(String Date) {
//        SimpleDateFormat curFormater = new SimpleDateFormat("dd-MMM-yyyy", new Locale("en"));
//        Date dateObj = null;
//        String chkfrom = "";
//        try {
//            dateObj = curFormater.parse(Date);
//            dateObj = new DateTime(dateObj).minusDays(1).toDate();
//            SimpleDateFormat postFormater = new SimpleDateFormat("yyyy-MM-dd", new Locale("en"));
//            chkfrom = postFormater.format(dateObj);
//        } catch (ParseException e) {
//        }
//        return chkfrom;
//
//    }

    public static String PaternDate(String Date) {
        SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd", new Locale("en"));
        Date dateObj = null;
        String newDateStr = " ";
        try {
            dateObj = curFormater.parse(Date);
            SimpleDateFormat postFormater = new SimpleDateFormat("dd-MMM-yyyy", new Locale("en"));
            newDateStr = postFormater.format(dateObj);
        } catch (ParseException e) {
        }
        return newDateStr;
    }

    public static String DateWithoutTime(Date d) {
        SimpleDateFormat simFormat = new SimpleDateFormat("yyyy-MM-dd", new Locale("en"));
        return simFormat.format(d);
    }

    public static boolean isThisDateValid(String dateToValidate) {

        if (dateToValidate == null) {
            return false;
        }
        if (dateToValidate.length() == 0) {
            return true;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", new Locale("en"));
        sdf.setLenient(false);

        try {
            @SuppressWarnings("unused")
            Date date = sdf.parse(dateToValidate);

        } catch (ParseException e) {

            return false;
        }

        return true;
    }

    public Date parseDate(String paString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", new Locale("en"));

        try {
            return sdf.parse(paString);
        } catch (ParseException e) {
            return null;
        }
    }

//    public static int getDays(String date1, String date2) {
//        DateTime startDate = new DateTime(date1);
//        DateTime endDate = new DateTime(date2);
//        Days diff = Days.daysBetween(startDate, endDate);
//        return diff.getDays();
//    }
//
//    public static int getDefaultDays(String date1, String date2) {
//        DateTimeFormatter format = DateTimeFormat
//                .forPattern("yyyy-MM-dd HH:mm:ss");
//        DateTime startDate = format.parseDateTime(date1);
//        DateTime endDate = format.parseDateTime(date2);
//        Days diff = Days.daysBetween(startDate, endDate);
//        return diff.getDays();
//    }
//
//    public static int getDefaultDaysWithoutTime(String date1, String date2) {
//        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd");
//        DateTime startDate = format.parseDateTime(date1);
//        DateTime endDate = format.parseDateTime(date2);
//        Days diff = Days.daysBetween(startDate, endDate);
//        return diff.getDays();
//    }
//
//    public static int getTime(String date1, String date2) {
//        DateTimeFormatter format = DateTimeFormat
//                .forPattern("yyyy-MM-dd HH:mm:ss");
//        DateTime startDate = format.parseDateTime(date1);
//        DateTime endDate = format.parseDateTime(date2);
//        Seconds seconds = Seconds.secondsBetween(startDate, endDate);
//        return seconds.getSeconds();
//    }

    public static String getTime() {
        Calendar cal = Calendar.getInstance();
        cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss", new Locale("en"));
        return sdf.format(cal.getTime());
    }

    public static String getTime1() {
        Calendar cal = Calendar.getInstance();
        String dt = null;
        //add minutes to current date using Calendar.add method
        cal.add(Calendar.MINUTE, -30);
        int Hr24 = cal.get(Calendar.HOUR_OF_DAY);
        int Min = cal.get(Calendar.MINUTE);
        int Sec = cal.get(Calendar.SECOND);

        if (Hr24 > 0) {
            dt = Hr24 + ":" + Min + ":" + Sec;
        }
        return dt;
    }

    public static String getTime2() {
        long currentTime = System.currentTimeMillis();
        long halfAnHourLater = currentTime + 1800000;
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa", new Locale("en"));
        Date resultdate = new Date(halfAnHourLater);
        String newtime = sdf.format(resultdate);

        return newtime;
    }

    public static String getTime3() {
        long currentTime = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa", new Locale("en"));
        Date resultdate = new Date(currentTime);
        String newtime = sdf.format(resultdate);
        return newtime;
    }

//    public static String get30minBeforeTime(String compareString) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("en"));
//
//        try {
//            Date date = sdf.parse(compareString);
//            long millisec = date.getTime();
//            long newcalculted = CompanyParameter.ReservationTime * 60000;
//            long halfAnHourLater = millisec - newcalculted;
//            Date resultdate = new Date(halfAnHourLater);
//            String beforedate = sdf.format(resultdate);
//            return beforedate;
//        } catch (ParseException e) {
//            return null;
//        }


//    public static String get30minAfterTime(String compareString) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("en"));
//
//        try {
//            Date date = sdf.parse(compareString);
//            long millisec = date.getTime();
//            long newcalculted = CompanyParameter.ReservationTime * 60000;
//            long halfAnHourLater = millisec + newcalculted;
//            Date resultdate = new Date(halfAnHourLater);
//            String beforedate = sdf.format(resultdate);
//            return beforedate;
//        } catch (ParseException e) {
//            return null;
//        }
//    }

    public static boolean isDateAfter(String startDate, String endDate) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", new Locale("en"));
            Date date1 = df.parse(endDate);
            Date startingDate = df.parse(startDate);

            if (date1.after(startingDate))
                return true;
            else if (date1.equals(startingDate))
                return true;
            else
                return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static String AccDatePrint(String startDate) {
        SimpleDateFormat curFormater1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("en"));
        Date dateObj = null;
        String newDateStr1 = "";

        try {
            dateObj = curFormater1.parse(startDate);
            SimpleDateFormat postFormater = new SimpleDateFormat("dd-MM-yy", new Locale("en"));
            newDateStr1 = postFormater.format(dateObj);

        } catch (ParseException e) {

        }

        return newDateStr1;
    }

    public static String DatePrint(String startDate) {
        SimpleDateFormat curFormater1 = new SimpleDateFormat("dd-MMM-yyyy", new Locale("en"));
        Date dateObj = null;
        String newDateStr1 = "";

        try {
            dateObj = curFormater1.parse(startDate);
            SimpleDateFormat postFormater = new SimpleDateFormat("dd-MM-yy", new Locale("en"));
            newDateStr1 = postFormater.format(dateObj);

        } catch (ParseException e) {

        }

        return newDateStr1;
    }

    public static String DateTime(String startDate, int i) {
        SimpleDateFormat curFormater1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("en"));
        SimpleDateFormat postFormater = null;
        Date dateObj = null;
        String newDateStr1 = "";

        try {

            if (i == 0) {
                dateObj = curFormater1.parse(startDate);
                postFormater = new SimpleDateFormat("dd-MMM-yyyy", new Locale("en"));
                newDateStr1 = postFormater.format(dateObj);
            } else {
                dateObj = curFormater1.parse(startDate);
                postFormater = new SimpleDateFormat("HH:mm:ss a", new Locale("en"));
                newDateStr1 = postFormater.format(dateObj);
            }

        } catch (ParseException e) {
        }
        return newDateStr1;
    }

    public static String DatePrintReport(String startDate) {
        SimpleDateFormat curFormater1 = new SimpleDateFormat("dd-MMM-yyyy HH:mm a", new Locale("en"));
        Date dateObj = null;
        String newDateStr1 = "";

        try {
            dateObj = curFormater1.parse(startDate);
            SimpleDateFormat postFormater = new SimpleDateFormat("dd-MM-yy", new Locale("en"));
            newDateStr1 = postFormater.format(dateObj);

        } catch (ParseException e) {

        }

        return newDateStr1;
    }

    public static String getTime5() {
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm", new Locale("en"));
        Date resultdate = new Date(currentTime);
        String newtime = sdf.format(resultdate);
        return newtime;
    }

    public static String GetShTimehnm(String newtime, int i) {
        SimpleDateFormat curFormater1 = new SimpleDateFormat("HH:mm", new Locale("en"));
        SimpleDateFormat sdfh = null;
        String newhr = "";
        if (i == 0) {
            sdfh = new SimpleDateFormat("HH", new Locale("en"));
        } else {
            sdfh = new SimpleDateFormat("mm", new Locale("en"));
        }
        try {
            Date dateObj = curFormater1.parse(newtime);
            newhr = sdfh.format(dateObj);
        } catch (ParseException e) {
        }

        return newhr;
    }

    public static String getTime6(String newtime) {
        SimpleDateFormat curFormater1 = new SimpleDateFormat("HH:mm:ss", new Locale("en"));
        Date dateObj = null;
        String newDateStr1 = "";

        try {
            dateObj = curFormater1.parse(newtime);
            SimpleDateFormat postFormater = new SimpleDateFormat("HH:mm", new Locale("en"));
            newDateStr1 = postFormater.format(dateObj);

        } catch (ParseException e) {

        }

        return newDateStr1;
    }

    public static String getTime7(String newtime) {
        SimpleDateFormat curFormater1 = new SimpleDateFormat("HH:mm", new Locale("en"));
        Date dateObj = null;
        String newDateStr1 = "";
        try {
            dateObj = curFormater1.parse(newtime);
            SimpleDateFormat postFormater = new SimpleDateFormat("hh:mm a", new Locale("en"));
            newDateStr1 = postFormater.format(dateObj);
        } catch (ParseException e) {
        }
        return newDateStr1;
    }

    public static boolean isTimeAfter(String startDate, String endDate) {

        try {
            SimpleDateFormat df = new SimpleDateFormat("HH:mm", new Locale("en"));
            Date date1 = df.parse(endDate);
            Date startingDate = df.parse(startDate);

            if (date1.after(startingDate))
                return true;
            else if (date1.equals(startingDate))
                return true;
            else
                return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isTimeAfterTwodate(String startDate, String endDate) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("HH:mm", new Locale("en"));
            Date date1 = df.parse(endDate);
            Date startingDate = df.parse(startDate);

            if (date1.after(startingDate))
                return true;
            else if (date1.equals(startingDate))
                return true;
            else
                return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isdatebetween(String from, String to) {

        Date d = new Date();
        SimpleDateFormat simFormat = new SimpleDateFormat("yyyy-MM-dd", new Locale("en"));
        String currentdate = simFormat.format(d);
        try {
            Date curDate = simFormat.parse(currentdate);
            Date fromDate = simFormat.parse(from);
            Date ToDate = simFormat.parse(to);
            if ((curDate.after(fromDate)) && (curDate.before(ToDate))) {
                return true;
            } else if (curDate.equals(fromDate) || curDate.equals(ToDate)) {
                return true;
            } else if (curDate.before(fromDate)) {
                return false;
            } else {
                return false;
            }
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean CheckTimeBetween(String start, String end, String comtime) {
        boolean flag = false;

        try {
            Date time11 = new SimpleDateFormat("HH:mm:ss").parse(start + ":00");
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(time11);

            Date time22 = new SimpleDateFormat("HH:mm:ss").parse(end + ":00");
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(time22);

            String someRandomTime = comtime;
            Date d = new SimpleDateFormat("HH:mm:ss").parse(someRandomTime);
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(d);

            Date x = calendar3.getTime();

            if (x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                System.out.println("Time is in between..");
                flag = false;
            } else {
                System.out.println("Time is not in between..");
                flag = true;
            }

        } catch (ParseException e) {
        }
        return flag;
    }

    public static boolean CheckTimeBetween1(String start, String end, String comtime) {
        boolean flag = false;

        try {
            Date time11 = new SimpleDateFormat("HH:mm:ss").parse(start);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(time11);

            Date time22 = new SimpleDateFormat("HH:mm:ss").parse(end);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(time22);

            String someRandomTime = comtime + ":00";
            Date d = new SimpleDateFormat("HH:mm:ss").parse(someRandomTime);
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(d);

            Date x = calendar3.getTime();

            if (x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                System.out.println("Time is in between..");
                flag = false;
            } else {
                System.out.println("Time is not in between..");
                flag = true;
            }

        } catch (ParseException e) {
        }
        return flag;
    }

    public static String ValidationDateTime(String Date) {
        //2/12/2015 6:40:02 AM
        SimpleDateFormat curFormater = new SimpleDateFormat("dd-MMM-yyyy", new Locale("en"));
        Date dateObj = null;
        String newDateStr = "";
        try {
            dateObj = curFormater.parse(Date);
            SimpleDateFormat postFormater = new SimpleDateFormat("MM/dd/yyyy", new Locale("en"));
            newDateStr = postFormater.format(dateObj);
        } catch (ParseException e) {
        }
        return newDateStr;
    }

    public static String PaternServerDateTime(String Date) {
        //2/12/2015 6:40:02 AM
        SimpleDateFormat curFormater = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", new Locale("en"));
        Date dateObj = null;
        String newDateStr = "";
        try {
            dateObj = curFormater.parse(Date);
            SimpleDateFormat postFormater = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", new Locale("en"));
            newDateStr = postFormater.format(dateObj);
        } catch (ParseException e) {
        }
        return newDateStr;
    }

    public static String PaternServerDate(String Date) {
        //2/12/2015 6:40:02 AM
        SimpleDateFormat curFormater = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", new Locale("en"));
        Date dateObj = null;
        String newDateStr = "";
        try {
            dateObj = curFormater.parse(Date);
            SimpleDateFormat postFormater = new SimpleDateFormat("yyyy-MM-dd", new Locale("en"));
            newDateStr = postFormater.format(dateObj);
        } catch (ParseException e) {
        }
        return newDateStr;
    }

    public static boolean isdatebetween(String expdt) {
        SimpleDateFormat simFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("en"));
        String currentdate = new DateUtill().currentDate();
        try {
            Date curDate = simFormat.parse(currentdate);
            Date ExpDate = simFormat.parse(expdt);

            if ((curDate.before(ExpDate))) {
                return true;
            } else if (curDate.equals(ExpDate)) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            return false;
        }
    }


//    public static String FindDateDiff(String dt1, String dt2) {
//        int difference = 0;
//        try {
//            DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", new Locale("en"));
//            Date date1 = dateFormat.parse(dt1);
//            Date date2 = dateFormat.parse(dt2);
//            DateTime start = new DateTime(date1);
//            DateTime end = new DateTime(date2);
//            difference = Days.daysBetween(start, end).getDays();
//            difference = difference + Months.monthsBetween(start, end).getMonths();
//            difference = difference + Years.yearsBetween(start, end).getYears();
//
//        } catch (Exception e) {
//            difference = 0;
//        }
//        return String.valueOf(difference);
//    }
}
