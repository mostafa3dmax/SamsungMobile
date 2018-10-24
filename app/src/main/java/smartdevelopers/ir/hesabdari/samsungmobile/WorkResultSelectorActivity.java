package smartdevelopers.ir.hesabdari.samsungmobile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.b4a.manamsoftware.PersianDate.ManamPersianDate;
import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CacheManager;
import com.shephertz.app42.paas.sdk.android.App42CachedRequest;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Response;
import com.shephertz.app42.paas.sdk.android.storage.Query;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.storage.StorageService;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

import smartdevelopers.ir.hesabdari.samsungmobile.adapters.CalendarSelectorSpinnerAdapter;
import smartdevelopers.ir.hesabdari.samsungmobile.adapters.MySpinnerAdapter;
import smartdevelopers.ir.hesabdari.samsungmobile.data.Const;
import smartdevelopers.ir.hesabdari.samsungmobile.database.DatabaseHandler;
import smartdevelopers.ir.hesabdari.samsungmobile.database.DbConst;
import smartdevelopers.ir.hesabdari.samsungmobile.model.JobModel;

public class WorkResultSelectorActivity extends AppCompatActivity implements View.OnClickListener {
    private Spinner selectUser, toDay, fromDay, fromYear, fromMounth, toYear, toMounth;
    private Button btnFromTo, btnThisMounth, btnAllWork;
    private Dialog dialog;
    private CalendarSelectorSpinnerAdapter dayAdapter;
    int pYear, pMonth, pDay;
    private Integer[] days31, days30, days29, days;
    private ManamPersianDate persianDate;
    private String[] users = Const.USERS;
    private ArrayList<JobModel> models=new ArrayList<>();
    private TextView fromDateString,toDateString;
    private StorageService storageService;
    private ProgressDialog progressBar;
    private MySpinnerAdapter userAdapter;
    public Semaphore semaphore=new Semaphore(0);
    public Semaphore semaphore2=new Semaphore(0);
    public CountDownLatch latch;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_result_selector);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.setTitle(R.string.work_results_filter);
        }
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        selectUser = findViewById(R.id.work_result_selector_username);
        btnFromTo = findViewById(R.id.work_result_selector_dated);
        btnThisMounth = findViewById(R.id.work_result_selector_thisMounth);
        btnAllWork = findViewById(R.id.work_result_selector_show_all);
        progressBar=new ProgressDialog(this);
        handler=new Handler();
        storageService=App42API.buildStorageService();
        btnFromTo.setOnClickListener(this);
        btnThisMounth.setOnClickListener(this);
        btnAllWork.setOnClickListener(this);
        days29 = getDays29();
        days30 = getDays30();
        days31 = getDays31();

//        ArrayAdapter<String> userAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, users);
        userAdapter=new MySpinnerAdapter(getApplicationContext(),R.layout.spinner_custom_row,users);
        selectUser.setAdapter(userAdapter);

    }

    private void showProgressDialog(){
        progressBar.setMessage(getString(R.string.pleas_wait));
        progressBar.setCancelable(false);
        progressBar.show();
    }
    private void stopProgressBar(){
        if (progressBar.isShowing()){
            progressBar.cancel();
        }
    }
    private void openSelectDateDialog() {
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getRealSize(point);
        int windowWidth = point.x;

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.work_date_choser_layout);
        Button btnCancel = dialog.findViewById(R.id.work_date_selector_cancel_button);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        Button btnViewResult = dialog.findViewById(R.id.work_date_selector_confirm_button);

        fromDateString=dialog.findViewById(R.id.from_date_text);
        toDateString=dialog.findViewById(R.id.to_date_text);
        fromYear = dialog.findViewById(R.id.from_date_year);
        fromMounth = dialog.findViewById(R.id.from_date_mount);
        fromDay = dialog.findViewById(R.id.from_date_day);
        toYear = dialog.findViewById(R.id.to_date_year);
        toMounth = dialog.findViewById(R.id.to_date_mount);
        toDay = dialog.findViewById(R.id.to_date_day);
        Calendar calendar = Calendar.getInstance();
        int gYear = calendar.get(Calendar.YEAR);
        int gMounth = calendar.get(Calendar.MONTH);
        int gDay = calendar.get(Calendar.DAY_OF_MONTH);
        persianDate = new ManamPersianDate();
        pYear = persianDate.getPersianYear();
        pMonth = persianDate.getPersianMonth();
        pDay = persianDate.getPersianDay();
        Integer[] years = {pYear - 1, pYear, pYear + 1, pYear + 2, pYear + 3, pYear + 4};
        Integer[] months = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};

        CalendarSelectorSpinnerAdapter yearAdapter =
                new CalendarSelectorSpinnerAdapter(WorkResultSelectorActivity.this, R.layout.calendar_spinner_custom_row, years);
//        ArrayAdapter yearAdapter=new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,years);
        fromYear.setAdapter(yearAdapter);
        toYear.setAdapter(yearAdapter);
        CalendarSelectorSpinnerAdapter monthAdapter =
                new CalendarSelectorSpinnerAdapter(WorkResultSelectorActivity.this, R.layout.calendar_spinner_custom_row, months);
//        ArrayAdapter monthAdapter=new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,months);
        fromMounth.setAdapter(monthAdapter);
        toMounth.setAdapter(monthAdapter);
        setDayList(toDay, pMonth, pYear);
        setDayList(fromDay, pMonth, pYear);
        toDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int month = (Integer) toMounth.getSelectedItem();
                int year = (Integer) toYear.getSelectedItem();
                int day=(Integer)adapterView.getSelectedItem();
                changeToDateText(year,month,day);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        fromDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int month = (Integer) fromMounth.getSelectedItem();
                int year = (Integer) fromYear.getSelectedItem();
                int day=(Integer)adapterView.getSelectedItem();
                changeFromDateText(year,month,day);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        toMounth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int month = (Integer) adapterView.getSelectedItem();
                int year = (Integer) toYear.getSelectedItem();
                int day=(Integer)toDay.getSelectedItem();
                changeToDateText(year,month,day);
                setDayList(toDay, month, year);
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        fromMounth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int month = (Integer) adapterView.getSelectedItem();
                int year = (Integer) fromYear.getSelectedItem();
                int day=(Integer)fromDay.getSelectedItem();
                changeFromDateText(year,month,day);
                setDayList(fromDay, month, year);
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        fromYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int month = (Integer) fromMounth.getSelectedItem();
                int year = (Integer) adapterView.getSelectedItem();
                int day=(Integer)fromDay.getSelectedItem();
                changeFromDateText(year,month,day);
                setDayListFromYearChanged(fromDay, month, year);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        toYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int month = (Integer) toMounth.getSelectedItem();
                int year = (Integer) adapterView.getSelectedItem();
                int day=(Integer)toDay.getSelectedItem();
                changeToDateText(year,month,day);
                setDayListFromYearChanged(toDay, month, year);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        fromYear.setSelection(yearAdapter.getPosition(pYear));
        toYear.setSelection(yearAdapter.getPosition(pYear));
        fromMounth.setSelection(monthAdapter.getPosition(pMonth));
        toMounth.setSelection(monthAdapter.getPosition(pMonth));

        fromDay.setSelection(dayAdapter.getPosition(pDay));

        toDay.setSelection(dayAdapter.getPosition(pDay));
//        windowWidth= (int) (windowWidth*(double)6/5);
        btnViewResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatedResult();
                dialog.cancel();
            }
        });
        changeFromDateText(pYear,pMonth,pDay);
        changeToDateText(pYear,pMonth,pDay);
        Window window = dialog.getWindow();
        if (window != null)
            window.setLayout(windowWidth, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.show();

    }

    private void changeFromDateText(int pYear,int pMonth,int pDay){
        String fromDateSt=String.format("%s/%s/%s",pYear,pMonth>9?pMonth:"0"+pMonth,pDay>9?pDay:"0"+pDay);
        fromDateString.setText(fromDateSt);
    }
    private void changeToDateText(int pYear,int pMonth,int pDay){
        String toDateSt=String.format("%s/%s/%s",pYear,pMonth>9?pMonth:"0"+pMonth,pDay>9?pDay:"0"+pDay);
        toDateString.setText(toDateSt);
    }
    private void setDayListFromYearChanged(Spinner daySpinner, int month, int year) {
        int current = daySpinner.getSelectedItemPosition();
        if (month == 12) {
            if (persianDate.IsLeap(year)) {
                days = getDays30();
                dayAdapter = new CalendarSelectorSpinnerAdapter(WorkResultSelectorActivity.this
                        , R.layout.calendar_spinner_custom_row, days);
                daySpinner.setAdapter(dayAdapter);
            } else {
                days = getDays29();
                dayAdapter = new CalendarSelectorSpinnerAdapter(WorkResultSelectorActivity.this
                        , R.layout.calendar_spinner_custom_row, days);
                daySpinner.setAdapter(dayAdapter);
            }
        }
        daySpinner.setSelection(current);
    }

    private void setDayList(Spinner daySpinner, int month, int year) {
        int current = daySpinner.getSelectedItemPosition();
        if (month <= 6) {

            days = days31;
            dayAdapter = new CalendarSelectorSpinnerAdapter(WorkResultSelectorActivity.this
                    , R.layout.calendar_spinner_custom_row, days);
//                    ArrayAdapter dayAdapter=new ArrayAdapter(WorkResultSelectorActivity.this,android.R.layout.simple_spinner_dropdown_item,days);
            daySpinner.setAdapter(dayAdapter);
            dayAdapter.notifyDataSetChanged();

        } else if (month > 6 && month < 12) {

            days = days30;
            dayAdapter = new CalendarSelectorSpinnerAdapter(WorkResultSelectorActivity.this
                    , R.layout.calendar_spinner_custom_row, days);
//                    ArrayAdapter dayAdapter=new ArrayAdapter(WorkResultSelectorActivity.this,android.R.layout.simple_spinner_dropdown_item,days);

            daySpinner.setAdapter(dayAdapter);
            dayAdapter.notifyDataSetChanged();

        } else if (month == 12) {
            if (persianDate.IsLeap(year)) {

                days = days30;
                dayAdapter = new CalendarSelectorSpinnerAdapter(WorkResultSelectorActivity.this
                        , R.layout.calendar_spinner_custom_row, days);
//                        ArrayAdapter dayAdapter=new ArrayAdapter(WorkResultSelectorActivity.this,android.R.layout.simple_spinner_dropdown_item,days);

                daySpinner.setAdapter(dayAdapter);
                dayAdapter.notifyDataSetChanged();

            } else {
                days = days29;
                dayAdapter = new CalendarSelectorSpinnerAdapter(WorkResultSelectorActivity.this
                        , R.layout.calendar_spinner_custom_row, days);
//                        ArrayAdapter dayAdapter=new ArrayAdapter(WorkResultSelectorActivity.this,android.R.layout.simple_spinner_dropdown_item,days);

                daySpinner.setAdapter(dayAdapter);
                dayAdapter.notifyDataSetChanged();

            }

        }
        daySpinner.setSelection(current);
    }

    private Integer[] getDays31() {
        Integer[] days = new Integer[31];
        for (int i = 0; i < 31; i++) {
            days[i] = i + 1;
        }
        return days;
    }

    private Integer[] getDays30() {
        Integer[] days = new Integer[30];
        for (int i = 0; i < days.length; i++) {
            days[i] = i + 1;
        }
        return days;
    }

    private Integer[] getDays29() {
        Integer[] days = new Integer[29];
        for (int i = 0; i < days.length; i++) {
            days[i] = i + 1;
        }
        return days;
    }

    private void showDatedResult() {
        int fYear = (Integer) this.fromYear.getSelectedItem();
        int fMonth = (Integer) fromMounth.getSelectedItem();
        int fDay = (Integer) fromDay.getSelectedItem();
        int tYear = (Integer) toYear.getSelectedItem();
        int tMonth = (Integer) toMounth.getSelectedItem();
        int tDay = (Integer) toDay.getSelectedItem();
        getResultFromDateToDate(fYear, fMonth, fDay, tYear, tMonth, tDay);

    }
    private void showThisMonthResult(){
//        Calendar calendar=Calendar.getInstance();
//        int year=calendar.get(Calendar.YEAR);
//        int month=calendar.get(Calendar.MONTH);
//        int day=calendar.get(Calendar.DAY_OF_MONTH);
        ManamPersianDate persianDate=new ManamPersianDate();
        int pYear=persianDate.getPersianYear();
        int pMonth=persianDate.getPersianMonth();
        int toDay=30;
        if (pMonth<=6){
            toDay=31;
        }else if (pMonth>6 && pMonth<12){
            toDay=30;
        }else if (pMonth==12){
            if (persianDate.IsLeap(pYear)){
                toDay=30;
            }else {
                toDay=29;
            }
        }
        getResultFromDateToDate(pYear,pMonth,1,pYear,pMonth,toDay);



    }
    private void startResultActivity(ArrayList<JobModel> models){
        try {
            OutputStream OutputStream=openFileOutput("models",MODE_PRIVATE);
            ObjectOutputStream objectOutputStream=new ObjectOutputStream(OutputStream);
            objectOutputStream.writeObject(models);
            objectOutputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent datedIntent = new Intent(this, WorkResultActivity.class);
//        datedIntent.putExtra(Const.EXTERA_INTENT_NAME, models);
        datedIntent.putExtra(Const.EXTERA_INTENT_USERNAME,selectUser.getSelectedItem().toString());

        startActivity(datedIntent);
    }


    private void showAllResult(){
//        DatabaseHandler handler=new DatabaseHandler(this,selectUser.getSelectedItem().toString());
        models.clear();
        showProgressDialog();
        final String user=selectUser.getSelectedItem().toString();
        storageService.findAllDocumentsCount(Const.dbName, user, new App42CallBack() {
            @Override
            public void onSuccess(Object o) {
                App42Response response=(App42Response)o;

                int totallRecord=response.getTotalRecords();

                int handered=getHanderedResponse(totallRecord);
                latch=new CountDownLatch(handered);

                for (int i=0;i<handered;i++){
                    if (i==handered-1){
                        findAllDocs(user,100,i*100,i);
                    }else {
                        findAllDocs(user,100,i*100,i);
                    }

                }

                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sort(models);
                stopProgressBar();
                startResultActivity(models);



            }

            @Override
            public void onException(Exception e) {
                showError(e);
            }
        });




    }
    private void sort(ArrayList<JobModel> models){
        Collections.sort(models);
        for (int i=0;i<models.size();i++){
            models.get(i).setRowNumber(i+1);
        }
    }
    private int getHanderedResponse(long number){

        if (number%100==0){
            return (int)(number/100);
        }else {
            return (int) ((number/100)+1);
        }

    }
    private void findAllDocs(String user, int max, int offset, final int start){

        storageService.findAllDocuments(Const.dbName, user,max,offset, new App42CallBack() {
            @Override
            public  void onSuccess(Object o) {
                if (start!=0){
                    try {
                        semaphore.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Storage storage=(Storage)o;



                ArrayList<Storage.JSONDocument> docs=storage.getJsonDocList();

                int d=models.size()+1;
                for (int i=0;i<docs.size();i++){
                    Storage.JSONDocument doc=docs.get(i);
                    String id=doc.getDocId();
                    JobModel model=new JobModel();
                    model.setId(id);
                    try {
                        JSONObject jsonObject=new JSONObject(doc.getJsonDoc());
                        model.setForosh(jsonObject.getInt(DbConst.FOROSH));
                        model.setJobName(jsonObject.getString(DbConst.JOB));
                        model.setTozihat(jsonObject.getString(DbConst.TOZIHAT));
                        model.setDate(jsonObject.getLong(DbConst.DB_DATE));

                        model.setKharid(jsonObject.getInt(DbConst.KHARID));
                        model.setRowNumber(d);
                        d++;

                    } catch (JSONException e) {
                        Log.v("SSS","json error");
                    }
                    synchronized (models) {
                        models.add(model);
                    }
                }

               semaphore.release();
                latch.countDown();


            }

            @Override
            public void onException(Exception e) {
                showError(e);
            }
        });
    }
    private void getResultFromDateToDate(int fromYear, int fromMonth, int fromDay, int toYear, int toMonth, int toDay) {

        ManamPersianDate fromPersian = new ManamPersianDate();
        String s = fromPersian.PersianToGregorian(fromYear, fromMonth, fromDay);
        String[] dates = s.split("/");
        int fY = Integer.valueOf(dates[0]);
        int fM = Integer.valueOf(dates[1]);
        int fD = Integer.valueOf(dates[2]);
        Calendar calendarFrom = Calendar.getInstance();
        calendarFrom.set(fY, fM-1, fD,0,0,0);

        String sTo = fromPersian.PersianToGregorian(toYear, toMonth, toDay);
        String[] datesTo = sTo.split("/");
        int tY = Integer.valueOf(datesTo[0]);
        int tM = Integer.valueOf(datesTo[1]);
        int tD = Integer.valueOf(datesTo[2]);
        Calendar calendarTo = Calendar.getInstance();
        calendarTo.set(tY, tM-1, tD,23,59,59);

        long fromDateInMilis =calendarFrom.getTimeInMillis();
        long toDateInMilis = calendarTo.getTimeInMillis();
        final String tabelName = selectUser.getSelectedItem().toString();

        final long fromDateInMilis2=fromDateInMilis;
        final long toDateInMilis2=toDateInMilis;

        String passSaved=Const.getPrefs(this,selectUser.getSelectedItem().toString(),"");

        if (passSaved.equals("")) {
            AfterGettingPass afterGettingPass = new AfterGettingPass() {
                @Override
                public void passIsTrue() {
                    models.clear();
                    showProgressDialog();
                    selectJobsFromDateToDateFromApp42(tabelName, fromDateInMilis2, toDateInMilis2,0,0);
                    waitForGetModelAndStartResult();

                }
            };
            showGetPassDialog(afterGettingPass);
        }else {
            showProgressDialog();
            models.clear();


            selectJobsFromDateToDateFromApp42(tabelName, fromDateInMilis2, toDateInMilis2,0,0);
            waitForGetModelAndStartResult();



        }


    }
    private void waitForGetModelAndStartResult(){
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                try {//برای بستن پروگرس و استارت ریکورد ها صبر کند تا تمام رکورد ها از نت دریافت شود
                    semaphore2.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sort(models);
                stopProgressBar();
                startResultActivity(models);
            }
        };

        ExecutorService executor=Executors.newSingleThreadExecutor();
        executor.execute(runnable);
    }

    public void showGetPassDialog(final AfterGettingPass afterGettingPass){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setView(R.layout.get_password_dialog_layout)
                .setCancelable(false);
        final AlertDialog dialog=builder.show();
        Button ok,cancel;
        final CheckBox savePass=dialog.findViewById(R.id.get_pass_checkbox);
        final EditText pass;
        ok=dialog.findViewById(R.id.get_pass_dialog_ok_btn);
        cancel=dialog.findViewById(R.id.get_pass_dialog_cancel_btn);
        pass=dialog.findViewById(R.id.get_pass_edit_text);
        pass.requestFocus();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isChecked=savePass.isChecked();
                String pass2=pass.getText().toString().trim();
                getPassword(selectUser.getSelectedItem().toString(),pass2,afterGettingPass,isChecked);
                dialog.cancel();
            }
        });

    }

    public static int numberOfdatedThread=0;
    private void selectJobsFromDateToDateFromApp42
            (final String tabelName, final long fromDateInMilis, final long toDateInMilis, final int offset, final int start){
        numberOfdatedThread++;
        Query q1= QueryBuilder.build(DbConst.DB_DATE,fromDateInMilis, QueryBuilder.Operator.GREATER_THAN_EQUALTO);
        Query q2=QueryBuilder.build(DbConst.DB_DATE,toDateInMilis, QueryBuilder.Operator.LESS_THAN_EQUALTO);
        Query q3=QueryBuilder.compoundOperator(q1, QueryBuilder.Operator.AND,q2);


        storageService.findDocumentsByQueryWithPaging(Const.dbName, tabelName, q3,200,offset,new App42CallBack() {
            int count;
//        storageService.findDocumentsByQuery(Const.dbName, tabelName, q3, new App42CallBack() {
            @Override
            public void onSuccess(Object o) {



                Storage storage=(Storage)o;
                ArrayList<Storage.JSONDocument> jsonDocuments = storage.getJsonDocList();

                count=jsonDocuments.size();

                if (count==100){

//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
                            selectJobsFromDateToDateFromApp42(tabelName,fromDateInMilis,toDateInMilis,numberOfdatedThread*100,1);
//                        }
//                    });

                }

// اگر اولین بار است که تابع فراخوانی میشود سمافون اکوایر نشود
                if (start!=0){
                    try {
                        semaphore.acquire() ;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


                    int d = models.size();
                    for (int i = 0; i < jsonDocuments.size(); i++) {
                        Storage.JSONDocument doc = jsonDocuments.get(i);
                        JobModel model = new JobModel();
                        String id = doc.getDocId();
                        model.setId(id);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(doc.getJsonDoc());
                            model.setForosh(jsonObject.getInt(DbConst.FOROSH));
                            model.setJobName(jsonObject.getString(DbConst.JOB));
                            model.setTozihat(jsonObject.getString(DbConst.TOZIHAT));
                            model.setDate(jsonObject.getLong(DbConst.DB_DATE));
                            model.setKharid(jsonObject.getInt(DbConst.KHARID));
                            model.setRowNumber(d);
                            d++;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        synchronized (models) {
                            models.add(model);
                        }
                    }

//                Callable<ArrayList<JobModel>> callable=new ExecuteModel(storage);
//                Future<ArrayList<JobModel>> future=pool.submit(callable);
//                set.add(future);

                    if (count<100){
                        semaphore2.release();
                        numberOfdatedThread=0;
                    }
                    semaphore.release();

                }

            @Override
            public void onException(Exception e) {
                semaphore.release();
                semaphore2.release();
                if (count != 0 || numberOfdatedThread <= 0) {
                    showError(e);
                }

            }
        });

    }


    public void showError(Exception e){
        e.printStackTrace();
        Log.v("SSS","get from net error"+e.getMessage());
        String message=e.getMessage();
        if (message.equals("Neither Network is avalable nor data is available in cache")){

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    stopProgressBar();
                    showMessageDialog(getString(R.string.fetching_model_error_title),getString(R.string.fetching_model_error_message));
                }
            });
        }else {


            JSONObject jsonObject=null;
            try {
                jsonObject=new JSONObject(message);
                JSONObject error=jsonObject.getJSONObject("app42Fault");
                int errorCode=error.getInt("appErrorCode");


                switch (errorCode){
                    case 2608:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                stopProgressBar();
                                showMessageDialog(getString(R.string.fetching_model_error_title),getString(R.string.no_document_found_for_query_message));
                            }
                        });
                        break;
                    case 2602:

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                stopProgressBar();
                                showMessageDialog(getString(R.string.fetching_model_error_title),getString(R.string.no_document_found_for_query_message));
                            }
                        });
                        break;
                        default:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    stopProgressBar();
                                    showMessageDialog(getString(R.string.fetching_model_error_title),getString(R.string.no_document_found_for_query_message));
                                }
                            });
                            break;
                }

            } catch (JSONException e1) {
                e1.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        stopProgressBar();
                        showMessageDialog(getString(R.string.fetching_model_error_title),getString(R.string.fetching_model_error_message));
                    }
                });
                Log.v("SSS","json erro"+e1.getMessage());
            }
        }
    }
    private void showMessageDialog(String title,String message){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK",null);
        builder.show();
    }

    public void getPassword(final String user, final String pass, final AfterGettingPass afterGettingPass, final boolean isChecked){
        showProgressDialog();
        Query q1=QueryBuilder.build(user,pass, QueryBuilder.Operator.EQUALS);

        storageService.findDocumentsByQuery(Const.dbName, Const.PASSWORD, q1, new App42CallBack() {

            @Override
            public void onSuccess(Object o) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        stopProgressBar();
                        if (isChecked){

                            Const.savePrefs(WorkResultSelectorActivity.this,user,pass);
                        }
                        afterGettingPass.passIsTrue();
                    }
                });

            }

            @Override
            public void onException(Exception e) {
                String message=e.getMessage();
                if (message.equals("Neither Network is avalable nor data is available in cache")){

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            stopProgressBar();
                            showMessageDialog(getString(R.string.fetching_model_error_title),getString(R.string.fetching_model_error_message));
                        }
                    });
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            stopProgressBar();
                            Toast.makeText(WorkResultSelectorActivity.this, getString(R.string.pass_is_incurrect), Toast.LENGTH_SHORT).show();
                            Const.savePrefs(WorkResultSelectorActivity.this, selectUser.getSelectedItem().toString(), "");
                            showGetPassDialog(afterGettingPass);
                        }
                    });
                }

            }

        });

    }
private void checkPassAndSaveToPrefs(){
        final int count=selectUser.getCount();
        final String[] users=new String[count];
        for (int i=0;i<count;i++){
            users[i]=selectUser.getItemAtPosition(i).toString();
        }
    final Map<String,String> userPass=new HashMap<>();
    for (int i=0;i<count;i++){
        String pass=Const.getPrefs(this,users[i],"");
        userPass.put(users[i],pass);
    }
        storageService.findAllDocuments(Const.dbName,Const.PASSWORD, new App42CallBack() {
            @Override
            public void onSuccess(Object o) {
                Storage storage=(Storage)o;
                ArrayList<Storage.JSONDocument> docs=storage.getJsonDocList();
                Storage.JSONDocument document=docs.get(0);
                try {
                    JSONObject object=new JSONObject(document.getJsonDoc());
                    for (int i=0;i<count;i++){
                        String user=users[i];
                        String passFromNet=object.getString(user);
                        String pass=userPass.get(user);
                        if (!pass.equals(passFromNet)){
                            Const.savePrefs(WorkResultSelectorActivity.this,user,"");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onException(Exception e) {

            }
        });

}
    public void showAllResul2(){
        String passSaved=Const.getPrefs(this,selectUser.getSelectedItem().toString(),"");
        if (passSaved.equals("")) {
            AfterGettingPass afterGettingPass=new AfterGettingPass() {
                @Override
                public void passIsTrue() {
                    showAllResult();
                }
            };
            showGetPassDialog(afterGettingPass);
        }else {
//            AfterGettingPass afterGettingPass=new AfterGettingPass() {
//                @Override
//                public void passIsTrue() {
                    showAllResult();
//                }
//            };
//            getPassword(selectUser.getSelectedItem().toString(),passSaved,afterGettingPass,false);


        }
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.work_result_selector_dated:
                openSelectDateDialog();
                break;
            case R.id.work_result_selector_thisMounth:
                showThisMonthResult();
                break;
            case R.id.work_result_selector_show_all:
                showAllResul2();

                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPassAndSaveToPrefs();
    }
}
