package smartdevelopers.ir.hesabdari.samsungmobile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.b4a.manamsoftware.PersianDate.ManamPersianDate;

import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.storage.StorageService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Objects;

import ir.hamsaa.persiandatepicker.Listener;
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.util.PersianCalendar;
import smartdevelopers.ir.hesabdari.samsungmobile.adapters.CalendarSelectorSpinnerAdapter;
import smartdevelopers.ir.hesabdari.samsungmobile.adapters.MySpinnerAdapter;
import smartdevelopers.ir.hesabdari.samsungmobile.data.Const;
import smartdevelopers.ir.hesabdari.samsungmobile.database.DbConst;
import smartdevelopers.ir.hesabdari.samsungmobile.model.JobModel;

public class EditItemActivity extends AppCompatActivity {
    Spinner userSelector;
    Spinner userJobSelector;
    String user;
    private JobModel model;

//    ArrayAdapter<String> jobAdapter;
MySpinnerAdapter jobAdapter;
    private Button saveJob;
    private EditText tozihat;
    private EditText kharid;
    private EditText forosh;
    private ProgressDialog progressBar;
    StorageService storageService;
    Storage storage;
    private PersianDatePickerDialog datePickerDialog;

    private TextView dateView;
    private long dateInMilis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.setTitle(R.string.edit_job);
        }
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        dateView=findViewById(R.id.date_view);
        datePickerDialog=new PersianDatePickerDialog(this);
        userSelector=findViewById(R.id.user_selector);
        userJobSelector=findViewById(R.id.user_job_selector);
        Intent intent=getIntent();
        user=intent.getStringExtra(Const.EXTERA_INTENT_USERNAME);
        model=(JobModel) intent.getSerializableExtra(Const.EXTERA_INTENT_MODEL);
        saveJob=findViewById(R.id.save_button);
        tozihat=findViewById(R.id.editText_tozihat);
        kharid=findViewById(R.id.editText_kharid);
        forosh=findViewById(R.id.editText_forosh);
        userSelector.setVisibility(View.GONE);
        storageService= App42API.buildStorageService();
//        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//
//        userJobSelector.setLayoutParams(params);
        progressBar=new ProgressDialog(this);
        // تنظیم تاریخ بر روی تکست تاریخ
        initializeDatePicker();
        setAdapterView();
        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });
        tozihat.setText(model.getTozihat());
        forosh.setText(String.valueOf(model.getForosh()));
        kharid.setText(String.valueOf(model.getKharid()));
//        userJobSelector.setSelection(jobAdapter.getPosition(model.getJobName()));
        userJobSelector.setSelection(jobAdapter.getPosition(model.getJobName()));

        saveJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSave();
            }
        });



    }

    private void setAdapterView(){
        if (user.equals(Const.MOSTAFA)){
//            jobAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,Const.MOSTAFA_JOBS);
//            jobAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
             jobAdapter=new MySpinnerAdapter(getApplicationContext(),R.layout.spinner_custom_row,Const.MOSTAFA_JOBS);
            userJobSelector.setAdapter(jobAdapter);
            jobAdapter.notifyDataSetChanged();
        }
        if (user.equals(Const.HOSSEIN)){
//            jobAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,Const.HOSSEIN_JOBS);
//            jobAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
             jobAdapter=new MySpinnerAdapter(getApplicationContext(),R.layout.spinner_custom_row,Const.HOSSEIN_JOBS);

            userJobSelector.setAdapter(jobAdapter);
            jobAdapter.notifyDataSetChanged();
        }
        if (user.equals(Const.MEHDI)){
//            jobAdapter=new ArrayAdapter<String>
//                    (this,android.R.layout.simple_spinner_dropdown_item,Const.MEHDI_JOBS);
//            jobAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
             jobAdapter=new MySpinnerAdapter(getApplicationContext(),R.layout.spinner_custom_row,Const.MEHDI_JOBS);
            userJobSelector.setAdapter(jobAdapter);
            jobAdapter.notifyDataSetChanged();
        }
    }

    private void initializeDatePicker(){

        dateInMilis=model.getDate();
        dateView.setText(model.getDateAsString());
        datePickerDialog.setPositiveButtonResource(R.string.ok);
        datePickerDialog.setNegativeButtonResource(R.string.cancel);
        datePickerDialog.setListener(new Listener() {
            @Override
            public void onDateSelected(PersianCalendar persianCalendar) {
                dateInMilis=persianCalendar.getTimeInMillis();
                dateView.setText(persianCalendar.getPersianShortDate());
            }

            @Override
            public void onDismissed() {

            }
        });
    }
    private void clickSave(){
        final JobModel model=new JobModel();
        model.setJobName(userJobSelector.getSelectedItem().toString());
        try {
            model.setForosh(Integer.valueOf(forosh.getText().toString()));
        }catch (NumberFormatException e){
            Toast.makeText(this, "مقدار عددی برای فروش وارد کنید", Toast.LENGTH_SHORT).show();
            forosh.setText("");
            forosh.requestFocus();
        }
        if (Objects.equals(kharid.getText().toString(), "")){
            model.setKharid(0);

        }else {
            try {
                model.setKharid(Integer.valueOf(kharid.getText().toString()));
            } catch (NumberFormatException e) {
                Toast.makeText(this, "مقدار عددی   وارد کنید", Toast.LENGTH_SHORT).show();
                kharid.setText("");
                kharid.requestFocus();
            }
        }
        model.setTozihat(tozihat.getText().toString());

        if (forosh.getText()!=null && !forosh.getText().toString().equals("")) {
            model.setDate(dateInMilis);
            model.setId(this.model.getId());
            model.setRowNumber(this.model.getRowNumber());
            JSONObject object=getJsonObject(model);
            showProgressDialog();

            storageService.updateDocumentByDocId(Const.dbName, user,
                    model.getId(), object, new App42CallBack() {
                        @Override
                        public void onSuccess(Object o) {
                             storage=(Storage)o;


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    stopProgressBar();
                                    if (storage.isOfflineSync()){
                                        Toast.makeText(EditItemActivity.this, getString(R.string.oflineAsyns_message), Toast.LENGTH_LONG).show();
                                    }
                                    Intent resultIntent=new Intent();
                                    resultIntent.putExtra(Const.EXTERA_INTENT_MODEL,model);
                                    setResult(Activity.RESULT_OK,resultIntent);
                                    finish();
                                }
                            });

                        }

                        @Override
                        public void onException(Exception e) {
                            Log.v("SSS","error updating "+e.getMessage());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(EditItemActivity.this, "مشکلی در ذخیره داده ها رخ داده است", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });


        }
    }



    private JSONObject getJsonObject(JobModel model){
        JSONObject object=new JSONObject();
        try {
            object.put(DbConst.FOROSH,model.getForosh());
            object.put(DbConst.JOB,model.getJobName());
            object.put(DbConst.TOZIHAT,model.getTozihat());
            object.put(DbConst.DB_DATE,model.getDate());
            object.put(DbConst.KHARID,model.getKharid());


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
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
}
