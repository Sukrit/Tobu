package com.journal.Tobu;
import android.provider.BaseColumns;

public final class FeedReaderContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public FeedReaderContract() {}

    /* Inner class that defines the table contents */
    public static abstract class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_FEED = "feed";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_LOCATION = "location";
    }
}