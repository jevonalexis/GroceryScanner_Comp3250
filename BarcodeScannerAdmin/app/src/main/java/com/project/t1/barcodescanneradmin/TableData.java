package com.project.t1.barcodescanneradmin;

import android.provider.BaseColumns;

/**
 * Created by Jevon on 03/03/2015.
 */
public class TableData {
    public TableData(){

    }

    public static abstract class TableInfo implements BaseColumns {
        public static final String Database_Name="itemDB";
        public static final String TableName="itemInfo";
        public static final String ITEM_NAME="item_name";
        public static final String PRICE="price";
        public static final String CODE="code";
        public static final String UNIT="unit";
        public static final String QUANTITY="quantity";

    }
}

