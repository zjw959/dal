package utils.lang;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class FormatEx {
    public static Date parseDate(String d, String fmt) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(fmt);
        Date eDate = sdf.parse(d);
        return eDate;
    }

    public static final String formatString(Date d, String fmt) {
        SimpleDateFormat sdf = new SimpleDateFormat(fmt);
        String eStr = sdf.format(d);
        return eStr;
    }
}
