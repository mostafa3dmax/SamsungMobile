package smartdevelopers.ir.hesabdari.samsungmobile;

import android.content.Intent;

import android.app.AlertDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;

import smartdevelopers.ir.hesabdari.samsungmobile.adapters.JobListAdapter;
import smartdevelopers.ir.hesabdari.samsungmobile.data.Const;
import smartdevelopers.ir.hesabdari.samsungmobile.model.JobModel;

public class WorkResultActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView workList;
    private ArrayList<JobModel> models = new ArrayList<>();
    private JobListAdapter adapter;
    private LinearLayout workResultPanel,workResultPanelLayout;
    private String[] workResultPanelJobList ;
    private ArrayList<String> checkedItems = new ArrayList<>();
    private ArrayList<JobModel> newModels = new ArrayList<>();
//    private RelativeLayout workResultPanelJobLayout;
    private ImageView workResultPanelClose;
    public boolean selectItemPanelIsOpen=false;
    private Button showAllDaryafti,showSood;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_result);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.setTitle(R.string.work_results);
        }
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        showAllDaryafti=findViewById(R.id.show_sum_all_result);
        showSood=findViewById(R.id.show_sod_of_result);
        workResultPanelLayout=findViewById(R.id.work_result_filter_panel_layout);
        workResultPanelClose=findViewById(R.id.work_result_filter_panel_close);
//        workResultPanelJobLayout=findViewById(R.id.work_result_filter_panel);
        workResultPanel = findViewById(R.id.work_result_filter_panel_jobs);
        Intent intent=getIntent();
        final String user=intent.getStringExtra(Const.EXTERA_INTENT_USERNAME);
        if (user.equals(Const.MOSTAFA)){
            workResultPanelJobList=Const.MOSTAFA_JOBS;
        }else if (user.equals(Const.HOSSEIN)){
            workResultPanelJobList=Const.HOSSEIN_JOBS;
        }else if (user.equals(Const.MEHDI)){
            workResultPanelJobList=Const.MEHDI_JOBS;
        }
        showAllDaryafti.setOnClickListener(this);
        showSood.setOnClickListener(this);
        workResultPanelClose.setOnClickListener(this);
        for (int i = 0; i < workResultPanelJobList.length; i++) {

            CheckBox workJobCheckBox = new CheckBox(this);
            RelativeLayout.LayoutParams params =
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_START);
            params.setMarginStart(25);
            workJobCheckBox.setLayoutParams(params);
//            workJobCheckBox.setGravity(Gravity.START);
            workJobCheckBox.setText(workResultPanelJobList[i]);
            workJobCheckBox.setTextColor(getResources().getColor(R.color.colorWhit));
            workJobCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        String text = compoundButton.getText().toString();
                        checkedItems.add(text);
                    } else {
                        String text = compoundButton.getText().toString();
                        checkedItems.remove(text);
                    }
                    newModels.clear();
                    if (!checkedItems.isEmpty()) {
                        int d = 1;
                        for (JobModel m : models) {
                            for (int j = 0; j < checkedItems.size(); j++) {
                                if (m.getJobName().equals(checkedItems.get(j))) {
                                    JobModel jobModel = new JobModel();
                                    jobModel.setForosh(m.getForosh());
                                    jobModel.setKharid(m.getKharid());
                                    jobModel.setDate(m.getDate());
                                    jobModel.setJobName(m.getJobName());
                                    jobModel.setTozihat(m.getTozihat());
                                    jobModel.setId(m.getId());
                                    jobModel.setRowNumber(d);
                                    newModels.add(jobModel);
                                    d++;
                                }
//                }
                            }
                            adapter = new JobListAdapter(WorkResultActivity.this, newModels,user);
                            workList.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                        }
                    }else {
                        adapter = new JobListAdapter(WorkResultActivity.this, models,user);
                        workList.setItemAnimator(new DefaultItemAnimator());
                        workList.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                }
            });
//            workJobCheckBox.setId(1000+i);

//            jobTitle.setLabelFor(workJobCheckBox.getId());
//            jobRowLayout.addView(jobTitle);
//            jobRowLayout.addView(workJobCheckBox);

            workResultPanel.addView(workJobCheckBox);

        }
        workList = findViewById(R.id.work_result_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        workList.setLayoutManager(layoutManager);
//        DatabaseHandler handler = new DatabaseHandler(this, user);

        //noinspection unchecked
//        models = (ArrayList<JobModel>) intent.getSerializableExtra(Const.EXTERA_INTENT_NAME);
        models=getModels();
        adapter = new JobListAdapter(this, models,user);
        workList.setAdapter(adapter);
        // adapter.notifyDataSetChanged();

    }
    @SuppressWarnings("unchecked")
    private ArrayList<JobModel> getModels(){
        ArrayList<JobModel> models1=new ArrayList<>();
        try {
            InputStream inputStream=openFileInput("models");
            ObjectInputStream objectInputStream=new ObjectInputStream(inputStream);
            models1=(ArrayList<JobModel>) objectInputStream.readObject();

        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return models1;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.work_result_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch (id){
            case R.id.work_filter_menu:
                if (!selectItemPanelIsOpen){
                    showJobSelectorWindow();

                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showJobSelectorWindow() {
        Animation animationUtils=AnimationUtils.loadAnimation(this,R.anim.work_reslt_item_selector_anim_show);
        Animation btnClose=AnimationUtils.loadAnimation(this,R.anim.work_reslt_item_close_btn_anim_show);
        workResultPanelLayout.startAnimation(animationUtils);
        workResultPanelLayout.setVisibility(View.VISIBLE);
        workResultPanelClose.startAnimation(btnClose);
        workResultPanelClose.setVisibility(View.VISIBLE);
        if (adapter.editWindowIsOpen){
            adapter.closeEditeButtons();
        }
        selectItemPanelIsOpen=true;

    }

    private void showSoodMethod(){
        int daryafti=0;
        int hazine=0;
        if (!newModels.isEmpty()){
            for (JobModel m:newModels){
                daryafti=m.getForosh()+daryafti;
                hazine=m.getKharid()+hazine;
            }

        }else {
            for (JobModel m:models){
                daryafti=m.getForosh()+daryafti;
                hazine=m.getKharid()+hazine;
            }
        }
        DecimalFormat decimalFormat=new DecimalFormat("##,###");
        String daryaftiSt=decimalFormat.format(daryafti);
        String hazineSt=decimalFormat.format(hazine);
        String soodSt=decimalFormat.format((daryafti-hazine));
        String s="کل دریافتی شما : " +daryaftiSt +" تومان " +"\n"+
                "-------------- \n"+
                "کل هزینه شما : "+hazineSt+" تومان " +"\n" +
                "-------------- \n"+
                "مجموع کل سود شما : "+soodSt+" تومان ";
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle(R.string.sood_title);
        builder.setMessage(s);
        builder.setPositiveButton("OK",null);
        AlertDialog temp=builder.show();
        TextView titleView=temp.findViewById(getResources().getIdentifier("alertTitle", "id", "android"));
        if (titleView!=null)
            titleView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        TextView message=temp.findViewById(android.R.id.message);
        message.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        Button pButton=temp.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
        ViewGroup linearLayout=(ViewGroup) pButton.getParent();
        linearLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
    }
    private void showAllDaryaftiMethod(){
        int daryafti=0;

        if (!newModels.isEmpty()){
            for (JobModel m:newModels){
                daryafti=m.getForosh()+daryafti;
            }

        }else {
            for (JobModel m:models){
                daryafti=m.getForosh()+daryafti;
            }
        }
        DecimalFormat format=new DecimalFormat("##,###");
        String daryaftiSt=format.format(daryafti);
        String s="کل دریافتی شما : " +daryaftiSt +" تومان " +"\n";

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle(R.string.all_daryafti_title);
        builder.setMessage(s);
        builder.setPositiveButton("OK",null);

        AlertDialog temp=builder.show();
        TextView titleView=temp.findViewById(getResources().getIdentifier("alertTitle", "id", "android"));

            titleView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            titleView.setGravity(Gravity.RIGHT);
            ViewGroup group=(ViewGroup) titleView.getParent();
            group.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        TextView message=temp.findViewById(android.R.id.message);
        message.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        Button pButton=temp.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
        ViewGroup linearLayout=(ViewGroup) pButton.getParent();
        linearLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
    }
    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch (id){
            case R.id.work_result_filter_panel_close:
                closeJobSelectorPanel();
                break;
            case R.id.show_sum_all_result:
                showAllDaryaftiMethod();
                break;
            case R.id.show_sod_of_result:
                showSoodMethod();
                break;
        }
    }

    public void closeJobSelectorPanel() {
        Animation animationUtils=AnimationUtils.loadAnimation(this,R.anim.work_reslt_item_selector_anim_close);
        Animation btnClose=AnimationUtils.loadAnimation(this,R.anim.work_reslt_item_close_btn_anim_close);

        workResultPanelLayout.startAnimation(animationUtils);
        workResultPanelLayout.setVisibility(View.GONE);
        workResultPanelClose.startAnimation(btnClose);
        workResultPanelClose.setVisibility(View.GONE);

        selectItemPanelIsOpen=false;

    }

    @Override
    public void onBackPressed() {
        if (adapter.editWindowIsOpen){
            adapter.hideEditButtons();
        }else if(selectItemPanelIsOpen){
            closeJobSelectorPanel();
            selectItemPanelIsOpen=false;

        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==Const.UPDATE_REQUEST_CODE ){
            if (resultCode==RESULT_OK){
                JobModel model=(JobModel) data.getSerializableExtra(Const.EXTERA_INTENT_MODEL);
                JobModel oldModel=models.get(adapter.pos);
                oldModel.setForosh(model.getForosh());
                oldModel.setKharid(model.getKharid());
                oldModel.setTozihat(model.getTozihat());
                oldModel.setJobName(model.getJobName());
                oldModel.setDate(model.getDate());
                sort(models);
//                adapter.notifyItemChanged(adapter.pos);
                adapter.notifyDataSetChanged();
            }
        }
    }
    private void sort(ArrayList<JobModel> models){
        Collections.sort(models);
        for (int i=0;i<models.size();i++){
            models.get(i).setRowNumber(i+1);
        }
    }
}
