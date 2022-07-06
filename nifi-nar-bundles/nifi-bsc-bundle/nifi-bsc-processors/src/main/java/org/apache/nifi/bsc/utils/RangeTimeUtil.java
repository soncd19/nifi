package org.apache.nifi.bsc.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RangeTimeUtil {

    public static Map<String, String> getProcessSessionAttrs(String rangeType, Calendar calendar) {

        Map<String, String> attributes = new HashMap<>();
        LocalDate localDate = calendar.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        SimpleDateFormat format;
        String dateString = "";

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date cal = Calendar.getInstance().getTime();
        String synDate = dateFormat.format(cal);

        switch (rangeType) {
            case "Day": {
                format = new SimpleDateFormat("yyyyMMdd");
                dateString = format.format(calendar.getTime());

                attributes.put("datekey", dateString);
                attributes.put("day", String.valueOf(localDate.getDayOfMonth()));
                attributes.put("year", String.valueOf(localDate.getYear()));
                attributes.put("month", String.format("%02d", localDate.getMonthValue()));
            }

            case "Month": {
                format = new SimpleDateFormat("yyyyMM");
                dateString = format.format(calendar.getTime());

                attributes.put("monthkey", dateString);
                attributes.put("year", String.valueOf(localDate.getYear()));
                attributes.put("month", String.format("%02d", localDate.getMonthValue()));
            }

            case "Year": {
                format = new SimpleDateFormat("yyyy");
                dateString = format.format(calendar.getTime());

                attributes.put("yearkey", dateString);
                attributes.put("year", String.valueOf(localDate.getYear()));
            }
        }

        attributes.put("SynDate", synDate);
        return attributes;
    }
}
