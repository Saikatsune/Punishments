package net.saikatsune.punishments.manager;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class DateManager {

    public String translateMillisToDate(long timeInMillis) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy (HH:mm:ss)");
        Date resultDate = new Date(timeInMillis);

        return simpleDateFormat.format(resultDate);
    }

}
