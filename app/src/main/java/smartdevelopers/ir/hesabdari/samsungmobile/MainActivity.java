package smartdevelopers.ir.hesabdari.samsungmobile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.b4a.manamsoftware.PersianDate.ManamPersianDate;

import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CacheManager;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.storage.StorageService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import ir.hamsaa.persiandatepicker.Listener;
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.util.PersianCalendar;
import smartdevelopers.ir.hesabdari.samsungmobile.adapters.MySpinnerAdapter;
import smartdevelopers.ir.hesabdari.samsungmobile.data.Const;
import smartdevelopers.ir.hesabdari.samsungmobile.data.MyHtmlTagHandler;
import smartdevelopers.ir.hesabdari.samsungmobile.database.DbConst;
import smartdevelopers.ir.hesabdari.samsungmobile.model.JobModel;

public class MainActivity extends AppCompatActivity  {
    private Spinner userSelector;
    private Spinner userJobSelector;
    private String[] users;

//    ArrayAdapter<String> adapter;
//    ArrayAdapter<String> jobAdapter;
    private MySpinnerAdapter adapter;
    private MySpinnerAdapter jobAdapter;
    private Button saveJob;
    private EditText tozihat;
    private EditText kharid;
    private EditText forosh;
    private Storage storage;
    private StorageService storageService;
    private float thisAppVersion;
    private String versionName;
    private PersianDatePickerDialog datePickerDialog;

    private TextView dateView;
    private long dateInMilis;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.setTitle(R.string.add_job);

        }
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        try {
            PackageInfo info=this.getPackageManager().getPackageInfo(getPackageName(),0);

            versionName=info.versionName;
            thisAppVersion=Float.valueOf(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        datePickerDialog=new PersianDatePickerDialog(this);

        dateView=findViewById(R.id.date_view);
        userSelector=findViewById(R.id.user_selector);
        userJobSelector=findViewById(R.id.user_job_selector);
        users=new String[]{"انتخاب کنید","mostafa","hossein","mehdi"};
        saveJob=findViewById(R.id.save_button);
        tozihat=findViewById(R.id.editText_tozihat);
        kharid=findViewById(R.id.editText_kharid);
        forosh=findViewById(R.id.editText_forosh);
        App42API.initialize(getApplicationContext(),Const.API_KEY,Const.SECRET_KEY);
        App42API.setOfflineStorage(true);
        App42CacheManager.setPolicy(App42CacheManager.Policy.NETWORK_FIRST);

        storageService=App42API.buildStorageService();
        getVersionFromNet();
        // تنظیم تاریخ بر روی تکست تاریخ
        initializeDatePicker();
        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });
//        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,users);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter=new MySpinnerAdapter(getApplicationContext(),R.layout.spinner_custom_row,users);
        userSelector.setAdapter(adapter);
        userSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getSelectedItemPosition()==0){
                    jobAdapter=new MySpinnerAdapter(getApplicationContext(),R.layout.spinner_custom_row,new String[]{""});
                    userJobSelector.setAdapter(jobAdapter);
                    jobAdapter.notifyDataSetChanged();
                }
                if (adapterView.getSelectedItem().toString().equals(Const.MOSTAFA)){
//                    jobAdapter=new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_dropdown_item,Const.MOSTAFA_JOBS);
//                    jobAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    jobAdapter=new MySpinnerAdapter(getApplicationContext(),R.layout.spinner_custom_row,Const.MOSTAFA_JOBS);

                    userJobSelector.setAdapter(jobAdapter);
                    jobAdapter.notifyDataSetChanged();
                }
                if (adapterView.getSelectedItem().toString().equals(Const.HOSSEIN)){
//                    jobAdapter=new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_dropdown_item,Const.HOSSEIN_JOBS);
//                    jobAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    jobAdapter=new MySpinnerAdapter(getApplicationContext(),R.layout.spinner_custom_row,Const.HOSSEIN_JOBS);

                    userJobSelector.setAdapter(jobAdapter);
                    jobAdapter.notifyDataSetChanged();
                }
                if (adapterView.getSelectedItem().toString().equals(Const.MEHDI)){
//                    jobAdapter=new ArrayAdapter<String>
//                            (MainActivity.this,android.R.layout.simple_spinner_dropdown_item,Const.MEHDI_JOBS);
//                    jobAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    jobAdapter=new MySpinnerAdapter(getApplicationContext(),R.layout.spinner_custom_row,Const.MEHDI_JOBS);

                    userJobSelector.setAdapter(jobAdapter);
                    jobAdapter.notifyDataSetChanged();
                }
            }



            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        saveJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               clickSave();
//                saveToFirebase();
//                Intent intent=new Intent(MainActivity.this,WorkResultActivity.class);

//                startActivity(intent);

            }
        });

        forosh.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                formatEditText(forosh,this);

            }
        });
        kharid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                formatEditText(kharid,this);
            }
        });
        deleteTempFile();
    }
    private void initializeDatePicker(){
        ManamPersianDate persianDate=new ManamPersianDate();
        dateInMilis=Calendar.getInstance().getTimeInMillis();
        dateView.setText(persianDate.getPersianShortDate());
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
    private void deleteTempFile(){
        File external=getExternalFilesDir(null);
        File temp=new File(external,"temp");
        File app=new File(temp,"temp.apk");

        if (app.exists()){
            Log.v("SSS","app exist");
            app.delete();
        }
    }
    public void formatEditText(EditText editText,TextWatcher watcher){
        editText.removeTextChangedListener(watcher);
        String text=editText.getText().toString();
        String text2=text.replaceAll(",","");
//                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
        DecimalFormat format=new DecimalFormat("##,###");
        Integer number = null;
        String s="";
        if (!text2.equals("")){
            try {
                number = Integer.valueOf(text2);
            }catch (NumberFormatException e){
                Toast.makeText(this, "عدد وارد نکردید", Toast.LENGTH_SHORT).show();
            }
            if (number!=null) {
                s = format.format(number);
            }else {
                s="";
            }
        }else {
            s="";
        }

        editText.setText(s);
        editText.setSelection(s.length());
        editText.addTextChangedListener(watcher);
    }

private void clickSave(){
    String user=userSelector.getSelectedItem().toString();
    if (user!=null && !user.equals("انتخاب کنید")) {
        JobModel model = new JobModel();
        model.setJobName(userJobSelector.getSelectedItem().toString());
        try {
            String foroshText = forosh.getText().toString();
            String foroshNum = foroshText.replaceAll(",", "");
            model.setForosh(Integer.valueOf(foroshNum));
        } catch (NumberFormatException e) {
            Toast.makeText(MainActivity.this, "مقدار عددی برای فروش وارد کنید", Toast.LENGTH_SHORT).show();
            forosh.setText("");
            forosh.requestFocus();
        }
        if (Objects.equals(kharid.getText().toString(), "")) {
            model.setKharid(0);

        } else {
            try {
                String kharidText = kharid.getText().toString();
                String kharidNum = kharidText.replaceAll(",", "");
                model.setKharid(Integer.valueOf(kharidNum));
            } catch (NumberFormatException e) {
                Toast.makeText(MainActivity.this, "مقدار عددی   وارد کنید", Toast.LENGTH_SHORT).show();
                kharid.setText("");
                kharid.requestFocus();
            }
        }
        model.setTozihat(tozihat.getText().toString());
        if (forosh.getText() != null && !forosh.getText().toString().equals("")) {
            String userSelected = userSelector.getSelectedItem().toString();
//        DatabaseHandler databaseHandler = new DatabaseHandler(getBaseContext(), userSelected);
//        databaseHandler.createTable(userSelected);
//        databaseHandler.insertJobToDb(model);

            JSONObject object = getJsonObject(model);
            storageService.insertJSONDocument(Const.dbName, userSelected, object, new App42CallBack() {
                @Override
                public void onSuccess(Object o) {
                    storage = (Storage) o;
                    if (storage.isOfflineSync()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Toast.makeText(MainActivity.this, getString(R.string.oflineAsyns_message), Toast.LENGTH_LONG).show();
                            }

                        });
                    }

                }

                @Override
                public void onException(Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "مشکلی در ذخیره داده ها رخ داده است", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            tozihat.setText("");

            forosh.setText("");
            kharid.setText("");
            tozihat.requestFocus();

        }
    }else {
        Toast.makeText(this, getString(R.string.choose_user), Toast.LENGTH_SHORT).show();
    }
}
    public void getVersionFromNet(){
        storageService.findAllDocuments(Const.dbName, Const.UPDATE, new App42CallBack() {
            @Override
            public void onSuccess(Object o) {
                Storage storage=(Storage)o;
                ArrayList<Storage.JSONDocument> doclist=storage.jsonDocList;
                Storage.JSONDocument document=doclist.get(0);
                try {
                    JSONObject jsonObject=new JSONObject(document.getJsonDoc());
                    float version=Float.valueOf(jsonObject.getString(Const.APP_VERSION)) ;
                    final String appUrl=jsonObject.getString(Const.APP_URL);
                    final String[] newVersionTip=new String[]{jsonObject.getString(Const.APP_NEW_VERSION_TIPS)};
                    if (newVersionTip[0]==null || newVersionTip[0].equals("")){
                        newVersionTip[0]=getString(R.string.update_message);
                    }

                    if (version>thisAppVersion){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showUpdateDialog(appUrl,newVersionTip[0]);
                            }
                        });

                    }

                } catch (JSONException e) {
                    Log.v("SSS","get version from net error ="+e.getMessage());
                }

            }

            @Override
            public void onException(Exception e) {

            }
        });
    }

    @SuppressWarnings("deprecation")
    public Spanned fromHtml(String s){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            return Html.fromHtml(s,Html.FROM_HTML_MODE_LEGACY,null,new MyHtmlTagHandler());
        }else {
            return Html.fromHtml(s,null,new MyHtmlTagHandler());
        }
    }

    private void showUpdateDialog(final String url,String message) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.update_dialog_layout);
        final AlertDialog dialog=builder.show();
        Button ok,cancel;
        TextView messageText=dialog.findViewById(R.id.update_dialog_message);
        messageText.setText(message);
        ok=dialog.findViewById(R.id.update_dialog_ok_btn);
        cancel=dialog.findViewById(R.id.update_dialog_cancel_btn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                MainActivity.this.finish();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File extFile=getExternalFilesDir(null);
                File tempDir=new File(extFile,"temp");
                File tempApp=new File(tempDir,"temp.apk");
                if (tempApp.exists()){
                    dialog.cancel();
                    APKLauncher launcher=new APKLauncher(MainActivity.this,tempApp);
                    launcher.lunchAPK();
                }else {
                    dialog.cancel();
                    update(url);
                }
            }
        });
    }

    private void update(String url) {
        ProgressDialog dialog=new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("در حال دانلود ...");
        dialog.setIndeterminate(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setMax(100);

        UpdateApp updateApp=new UpdateApp(this,dialog);
        updateApp.execute(url);

    }

    private JSONObject getJsonObject(JobModel model){
    JSONObject object=new JSONObject();
        try {
            object.put(DbConst.FOROSH,model.getForosh());
            object.put(DbConst.JOB,model.getJobName());
            object.put(DbConst.TOZIHAT,model.getTozihat());
            object.put(DbConst.DB_DATE,dateInMilis);
            object.put(DbConst.KHARID,model.getKharid());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch (id){
            case R.id.show_working_result:
                Intent intent=new Intent(MainActivity.this,WorkResultSelectorActivity.class);
                startActivity(intent);
                return true;
            case R.id.password_setting:
                Intent passWordSettin=new Intent(MainActivity.this,PasswordActivity.class);
                startActivity(passWordSettin);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==Const.UPDATE_REQUEST_CODE){
            if (resultCode==RESULT_CANCELED){
                getVersionFromNet();
            }
        }
    }
}
