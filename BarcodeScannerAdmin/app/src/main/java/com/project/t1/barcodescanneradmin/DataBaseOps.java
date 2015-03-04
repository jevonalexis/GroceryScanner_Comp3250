package com.project.t1.barcodescanneradmin;

/**
 * Created by Jevon on 03/03/2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Created by Jevon on 01/03/2015.
 */
public class DataBaseOps extends SQLiteOpenHelper {

    public static final int VERSION=8;

    public String Create_Query= "CREATE TABLE "+TableData.TableInfo.TableName+"("+TableData.TableInfo.CODE+" TEXT,"+
            TableData.TableInfo.ITEM_NAME+" TEXT,"+TableData.TableInfo.PRICE+" REAL,"+TableData.TableInfo.UNIT+" TEXT,"+TableData.TableInfo.QUANTITY+" REAL);";

    public DataBaseOps(Context context) {
        super(context, TableData.TableInfo.Database_Name, null, VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Create_Query);
        Log.d("DATABASE OP", "DB CREATED");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertRow(DataBaseOps DBop,String code,String item_name,double price,String unit, double quantity){
        SQLiteDatabase db=DBop.getWritableDatabase();
        ContentValues cv= new ContentValues();
        cv.put(TableData.TableInfo.CODE,code);
        cv.put(TableData.TableInfo.ITEM_NAME,item_name);
        cv.put(TableData.TableInfo.PRICE,price);
        cv.put(TableData.TableInfo.UNIT,unit);
        cv.put(TableData.TableInfo.QUANTITY,quantity);

        long l=db.insert(TableData.TableInfo.TableName,null,cv);
        Log.d("DATABASE OP","Row inserted");


    }

    public Cursor getData(DataBaseOps DBop){
        SQLiteDatabase db = DBop.getReadableDatabase();
        String[] columns= {TableData.TableInfo.CODE,TableData.TableInfo.ITEM_NAME,TableData.TableInfo.PRICE,
                TableData.TableInfo.UNIT,TableData.TableInfo.QUANTITY};
        Cursor cursor=db.query(TableData.TableInfo.TableName,columns,null,null,null,null,null);
        return cursor;
    }
}
