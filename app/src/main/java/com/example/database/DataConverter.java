package com.example.database;

import androidx.room.TypeConverter;

import java.util.Date;

public class DataConverter {
@TypeConverter
    public static Long toTime(Date date)
{
    return date==null? null: date.getTime();
}

@TypeConverter
    public static Date toDate(Long timestamp)
{
    return timestamp==null? null: new Date(timestamp);
}

}
