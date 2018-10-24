package smartdevelopers.ir.hesabdari.samsungmobile;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.storage.Query;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.storage.StorageService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import smartdevelopers.ir.hesabdari.samsungmobile.adapters.MySpinnerAdapter;
import smartdevelopers.ir.hesabdari.samsungmobile.data.Const;

public class PasswordActivity extends AppCompatActivity {

    private EditText currentPass,newPass,confirmNewPass;
    private Button save;
    private StorageService storageService;
    private Spinner userSelector;
    private MySpinnerAdapter adapter;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.setTitle(R.string.chanfe_password);

        }
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        currentPass=findViewById(R.id.change_pass_current);
        newPass=findViewById(R.id.change_pass_new_pass_editText);
        confirmNewPass=findViewById(R.id.change_pass_confirm_new_pass_editText);
        save=findViewById(R.id.change_pass_save_button);
        userSelector=findViewById(R.id.change_pass_userSelector);
        adapter=new MySpinnerAdapter(this,R.layout.spinner_custom_row,Const.USERS);
        userSelector.setAdapter(adapter);
        storageService= App42API.buildStorageService();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPass();
            }
        });


    }
    private void checkPass(){
        String user=userSelector.getSelectedItem().toString();
        String curenntPassText=currentPass.getText().toString();
        String newPassText=newPass.getText().toString();
        String confirmNewPassText=confirmNewPass.getText().toString();

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.pleas_wait));
        progressDialog.setCancelable(false);

        if (curenntPassText.equals("")){

            currentPass.requestFocus();
            Toast.makeText(this, getString(R.string.insert_current_pass), Toast.LENGTH_SHORT).show();
        }else if (newPassText.equals("")){
                newPass.requestFocus();
                Toast.makeText(this, getString(R.string.insert_new_pass), Toast.LENGTH_SHORT).show();
        }else if (confirmNewPassText.equals("")){

                confirmNewPass.requestFocus();
            Toast.makeText(this, getString(R.string.insert_confrim_new_pass), Toast.LENGTH_SHORT).show();
        }else {
            //اگه ادیت تکس ها خالی نبودند یکسان بودن دو پسورد جدید را چک کن
            if (!newPassText.equals(confirmNewPassText)){
                confirmNewPass.requestFocus();
                confirmNewPass.selectAll();
                Toast.makeText(this, getString(R.string.passes_not_equals), Toast.LENGTH_SHORT).show();
            }else{// در صورت وجود هیچ گونه مشکل در پسورد ها پسورد را از سرور چک کن

                savePassToServer(user,curenntPassText,newPassText);
            }
        }

    }
    private void savePassToServer(final String user, final String currentPass, final String newPass){
        showProgress();
        Query validateCurrentPass= QueryBuilder.build(user,currentPass, QueryBuilder.Operator.EQUALS);

        storageService.findDocumentsByQuery(Const.dbName, Const.PASSWORD, validateCurrentPass, new App42CallBack() {
            @Override
            public void onSuccess(Object o) {
                Storage storage=(Storage)o;
                ArrayList<Storage.JSONDocument> docs=storage.getJsonDocList();
                Storage.JSONDocument document=docs.get(0);
                String id=document.getDocId();
                try {
                    JSONObject jsonObject=new JSONObject(document.getJsonDoc());
                    jsonObject.put(user,newPass);
                    // آپدیت پسورد جدید
                    storageService.updateDocumentByDocId(Const.dbName, Const.PASSWORD, id, jsonObject, new App42CallBack() {
                        @Override
                        public void onSuccess(Object o) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    stopProgress();
                                    clearEditText();
                                    Toast.makeText(PasswordActivity.this, getString(R.string.password_saved), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onException(Exception e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    stopProgress();
                                    Toast.makeText(PasswordActivity.this, getString(R.string.problem_to_save_pass), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                } catch (JSONException e) {
                    stopProgress();
                    e.printStackTrace();
                }

            }

            @Override
            public void onException(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        stopProgress();
                        Toast.makeText(PasswordActivity.this, getString(R.string.current_pass_is_incurrect), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
private void showProgress(){
        progressDialog.show();
}
private void stopProgress(){
    progressDialog.cancel();
}
private void clearEditText(){
    currentPass.setText("");
    newPass.setText("");
    confirmNewPass.setText("");
    currentPass.requestFocus();
}
}
