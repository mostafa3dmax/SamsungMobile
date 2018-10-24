package smartdevelopers.ir.hesabdari.samsungmobile.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.b4a.manamsoftware.PersianDate.ManamPersianDate;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Comparator;

/**
 * Created by mostafa on 02/07/2018.
 */

public class JobModel implements Serializable,Comparable<JobModel>{
    private String id;
    private int rowNumber;
    private int forosh;
    private int kharid;
    private String jobName;
    private String tozihat;
    private long date;

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getForosh() {
        return forosh;
    }

    public void setForosh(int forosh) {
        this.forosh = forosh;
    }

    public int getKharid() {
        return kharid;
    }

    public void setKharid(int kharid) {
        this.kharid = kharid;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getTozihat() {
        return tozihat;
    }

    public void setTozihat(String tozihat) {
        this.tozihat = tozihat;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getDateAsString(){
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(date);
        int year=calendar.get(Calendar.YEAR);
        int mount=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);
//        Log.v("SSS","model year ="+year);
//        Log.v("SSS","model month ="+mount);
//        Log.v("SSS","model day ="+day);

        ManamPersianDate persianDate=new ManamPersianDate();
//        Log.v("SSS","model date ="+persianDate.getPersianMonth());
        String d=persianDate.GregorianToPersian(year,mount+1,day);
//        Log.v("SSS","g to p ="+d);
        return d;
    }

    @Override
    public int compareTo(@NonNull JobModel model) {
        return date>model.getDate()?1:(date==model.getDate()?0:-1);
    }
}
