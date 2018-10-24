package smartdevelopers.ir.hesabdari.samsungmobile.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import smartdevelopers.ir.hesabdari.samsungmobile.model.JobModel;

/**
 * Created by Samsung Mobile on 02/07/2018.
 */

public class DatabaseHandler extends SQLiteOpenHelper{
    private String tableName;
    ArrayList<JobModel> jobModels=new ArrayList<>();

    public DatabaseHandler(Context context,String tabelName) {
        super(context, DbConst.DB_NAME, null, DbConst.DB_VERSION);
        this.tableName = tabelName;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql="CREATE TABLE IF NOT EXISTS "+tableName+"(" +
                DbConst.ID +" INTEGER ,"+DbConst.FOROSH+" INTEGER,"+DbConst.JOB+" TEXT, "+
                DbConst.TOZIHAT+ " TEXT,"+DbConst.DB_DATE+" LONG,"+DbConst.KHARID+" INTEGER,"+
                "PRIMARY KEY("+DbConst.ID+"));";

        sqLiteDatabase.execSQL(sql);

    }
    public void createTable(String tableName){
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        String sql="CREATE TABLE IF NOT EXISTS "+tableName+"(" +
                DbConst.ID +" INTEGER ,"+DbConst.FOROSH+" INTEGER,"+DbConst.JOB+" TEXT, "+
                DbConst.TOZIHAT+ " TEXT,"+DbConst.DB_DATE+" LONG,"+DbConst.KHARID+" INTEGER,"+
                "PRIMARY KEY("+DbConst.ID+"));";

        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    if (i1>i){
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+tableName);
    }
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void insertJobToDb(JobModel model){
        SQLiteDatabase db=getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(DbConst.FOROSH,model.getForosh());
        values.put(DbConst.JOB,model.getJobName());
        values.put(DbConst.TOZIHAT,model.getTozihat());
        Calendar calendar=Calendar.getInstance();
        values.put(DbConst.DB_DATE,calendar.getTimeInMillis());
        values.put(DbConst.KHARID,model.getKharid());

        db.insert(tableName,null,values);
        db.close();
    }
    public ArrayList<JobModel> getJobModels(){
        jobModels.clear();
        SQLiteDatabase db=getReadableDatabase();
        try{
        Cursor cursor=db.query(tableName,null,null,null,null,null,DbConst.DB_DATE+" ASC");
        if (cursor.moveToFirst()) {
            int rowNumber = 1;
            do  {
                JobModel model = new JobModel();
//                model.setId(cursor.getInt(cursor.getColumnIndex(DbConst.ID)));
                model.setForosh(cursor.getInt(cursor.getColumnIndex(DbConst.FOROSH)));
                model.setJobName(cursor.getString(cursor.getColumnIndex(DbConst.JOB)));
                model.setTozihat(cursor.getString(cursor.getColumnIndex(DbConst.TOZIHAT)));
                model.setDate(cursor.getLong(cursor.getColumnIndex(DbConst.DB_DATE)));
                model.setKharid(cursor.getInt(cursor.getColumnIndex(DbConst.KHARID)));
               model.setRowNumber(rowNumber);
               jobModels.add(model);
               rowNumber++;
            }while (cursor.moveToNext());
        }
        cursor.close();
        }catch (Exception e){
            Log.v("SSS","ERROR in sqlite queri ="+e.getMessage());
        }
        db.close();
        return jobModels;
    }
    public ArrayList<JobModel> selectJobFromDateToDate(String tableName,long from,long to){
        jobModels.clear();
        SQLiteDatabase db=getReadableDatabase();
        try {
            Cursor cursor = db.query(tableName, null, DbConst.DB_DATE + " BETWEEN ? AND ?",
                    new String[]{String.valueOf(from), String.valueOf(to)}, null, null, DbConst.DB_DATE + " ASC");
            if (cursor.moveToFirst()) {
                int rowNumber = 1;
                do {
                    JobModel model = new JobModel();
//                    model.setId(cursor.getInt(cursor.getColumnIndex(DbConst.ID)));
                    model.setTozihat(cursor.getString(cursor.getColumnIndex(DbConst.TOZIHAT)));
                    model.setJobName(cursor.getString(cursor.getColumnIndex(DbConst.JOB)));
                    model.setDate(cursor.getLong(cursor.getColumnIndex(DbConst.DB_DATE)));
                    model.setKharid(cursor.getInt(cursor.getColumnIndex(DbConst.KHARID)));
                    model.setForosh(cursor.getInt(cursor.getColumnIndex(DbConst.FOROSH)));
                    model.setRowNumber(rowNumber);
                    jobModels.add(model);
                    rowNumber++;
                } while (cursor.moveToNext());
                cursor.close();

            }
        }catch (Exception e){
            Log.v("SSS","ERROR in sqlite queri ="+e.getMessage());
        }
        db.close();
        return jobModels;
    }

}
