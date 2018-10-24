package smartdevelopers.ir.hesabdari.samsungmobile.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.storage.StorageService;

import java.text.DecimalFormat;
import java.util.ArrayList;

import smartdevelopers.ir.hesabdari.samsungmobile.EditItemActivity;
import smartdevelopers.ir.hesabdari.samsungmobile.R;
import smartdevelopers.ir.hesabdari.samsungmobile.WorkResultActivity;
import smartdevelopers.ir.hesabdari.samsungmobile.data.Const;
import smartdevelopers.ir.hesabdari.samsungmobile.model.JobModel;
import smartdevelopers.ir.hesabdari.samsungmobile.model.ModelKEyValue;

/**
 * Created by mostafa on 03/07/2018.
 */

public class JobListAdapter extends RecyclerView.Adapter<JobListAdapter.ViewHolder> {
    Activity activity;
    ArrayList<JobModel> models = new ArrayList<>();
    Button edit, delete, cancel;
    ViewGroup editLayout;
    String id;
    String user;
    public int pos;
    ProgressDialog progressBar;
    public boolean editWindowIsOpen = false;

    public JobListAdapter(Activity activity, ArrayList<JobModel> models, String user) {
        this.activity = activity;
        this.models = models;
        this.user = user;
        edit = activity.findViewById(R.id.work_result_edit_btn);
        delete = activity.findViewById(R.id.work_result_delete_btn);
        cancel = activity.findViewById(R.id.work_result_cancel_btn);
        editLayout = activity.findViewById(R.id.work_result_long_click_layout);
        progressBar = new ProgressDialog(activity);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItem(id, pos);
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editItem(pos);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideEditButtons();
            }
        });
        Log.v("SSS","models size = "+models.size());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.work_result_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final JobModel model = models.get(position);

        holder.workRowNumber.setText(String.valueOf(model.getRowNumber()));
        holder.forosh.setText(format(model.getForosh()));
        holder.jobSubject.setText(model.getJobName());
        holder.tozihat.setText(model.getTozihat());
//        Log.v("SSS"," date as string"+model.getDateAsString());
//        Log.v("SSS"," date"+model.getDate());
        holder.date.setText(model.getDateAsString());
        holder.kharid.setText(format(model.getKharid()));
        if (position % 2 == 0) {
            holder.work_row.setBackgroundResource(R.drawable.result_row_zoj_bg_selector);
        } else {
            holder.work_row.setBackgroundResource(R.drawable.result_row_fard_bg_selector);
        }

        holder.work_row.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showEditButtons();
                JobModel model1 = models.get(holder.getAdapterPosition());
                pos = holder.getAdapterPosition();
                id = model1.getId();
                return true;
            }
        });
        holder.work_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JobModel model1 = models.get(holder.getAdapterPosition());
                ArrayList<ModelKEyValue> detailModel=getModelDetail(model1);
                AlertDialog.Builder builder=new AlertDialog.Builder(activity);
                builder.setView(R.layout.model_detail_layout);
                final AlertDialog dialog=builder.show();
                Button ok=dialog.findViewById(R.id.detail_btn_ok);
                RecyclerView list=dialog.findViewById(R.id.model_detail_list);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });
                RecyclerView.LayoutManager manager=new LinearLayoutManager(activity);
                list.setLayoutManager(manager);
                ModelDetailAdapter adapter=new ModelDetailAdapter(activity,detailModel);
                list.setAdapter(adapter);
                adapter.notifyDataSetChanged();



            }
        });

    }

    public ArrayList<ModelKEyValue> getModelDetail(JobModel model) {
        ArrayList<ModelKEyValue> modelKEyValues = new ArrayList<>();
        ModelKEyValue value1 = new ModelKEyValue(activity.getString(R.string.detail_work_title), model.getJobName());
        ModelKEyValue value2 = new ModelKEyValue(activity.getString(R.string.detail_tozihat), model.getTozihat());
        ModelKEyValue value3 = new ModelKEyValue(activity.getString(R.string.foros_title), format(model.getForosh()));
        ModelKEyValue value4 = new ModelKEyValue(activity.getString(R.string.detail_work_kharid), format(model.getKharid()));
        ModelKEyValue value5 = new ModelKEyValue(activity.getString(R.string.detail_work_date), model.getDateAsString());
        modelKEyValues.add(value1);
        modelKEyValues.add(value2);
        modelKEyValues.add(value3);
        modelKEyValues.add(value4);
        modelKEyValues.add(value5);
        return modelKEyValues;
    }

    public void showEditButtons() {
        editWindowIsOpen = true;
        WorkResultActivity a = (WorkResultActivity) activity;
        if (a.selectItemPanelIsOpen) {
            a.closeJobSelectorPanel();
        }
        editLayout.setVisibility(View.VISIBLE);
        Animation hide = new AlphaAnimation((float) 0.0, (float) 1.0);
        hide.setDuration(250);
        editLayout.startAnimation(hide);
        Animation btnEditAnim = AnimationUtils.loadAnimation(activity, R.anim.work_reslt_btn_edit_visable);
        Animation btnDeleteAnim = AnimationUtils.loadAnimation(activity, R.anim.work_reslt_btn_delete_visable);
        Animation btnCancelAnim = AnimationUtils.loadAnimation(activity, R.anim.work_reslt_btn_cancel_visable);
        edit.startAnimation(btnEditAnim);
        delete.startAnimation(btnDeleteAnim);
        cancel.startAnimation(btnCancelAnim);
    }

    public void hideEditButtons() {
        Animation btnEditAnim = AnimationUtils.loadAnimation(activity, R.anim.work_reslt_btn_edit_gone);
        Animation btnDeleteAnim = AnimationUtils.loadAnimation(activity, R.anim.work_reslt_btn_delete_gone);
        Animation btnCancelAnim = AnimationUtils.loadAnimation(activity, R.anim.work_reslt_btn_cancel_gone);
        edit.startAnimation(btnEditAnim);
        delete.startAnimation(btnDeleteAnim);
        cancel.startAnimation(btnCancelAnim);
        Animation hide = new AlphaAnimation((float) 1.0, (float) 0.0);
        hide.setDuration(250);
        editLayout.startAnimation(hide);
        editLayout.setVisibility(View.GONE);
        editWindowIsOpen = false;
    }

    public void closeEditeButtons() {
        editLayout.setVisibility(View.GONE);
        editWindowIsOpen = false;
    }

    private void deleteItem(final String id, final int position) {
        closeEditeButtons();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle(activity.getString(R.string.delete));
        builder.setMessage(activity.getString(R.string.delete_item_message));
        builder.setNegativeButton(activity.getString(R.string.cancel), null);
        builder.setPositiveButton(activity.getString(R.string.Yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showProgressDialog();
                StorageService storageService = App42API.buildStorageService();
                storageService.deleteDocumentById(Const.dbName, user, id, new App42CallBack() {
                    @Override
                    public void onSuccess(Object o) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity, "با موفقیت حذف شد", Toast.LENGTH_SHORT).show();
                                models.remove(position);
                                notifyItemRemoved(position);
                                stopProgressBar();
                                notifyItemRangeChanged(position - 1, models.size());
                            }
                        });

                    }

                    @Override
                    public void onException(Exception e) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity, "حذف با خطا رو به رو شد اتصال اینترنت خود را چک کنید", Toast.LENGTH_SHORT).show();
                                stopProgressBar();
                            }
                        });
                    }
                });
            }
        });
        AlertDialog temp = builder.show();
        TextView titleView = temp.findViewById(activity.getResources().getIdentifier("alertTitle", "id", "android"));
        if (titleView != null)
            titleView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        Button pButton = temp.getButton(AlertDialog.BUTTON_POSITIVE);
        ViewGroup linearLayout = (ViewGroup) pButton.getParent();
        linearLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

    }

    public void editItem(int position) {
        JobModel model = models.get(position);
        Intent intent = new Intent(activity, EditItemActivity.class);
        intent.putExtra(Const.EXTERA_INTENT_MODEL, model);
        intent.putExtra(Const.EXTERA_INTENT_USERNAME, user);
        closeEditeButtons();
        activity.startActivityForResult(intent, Const.UPDATE_REQUEST_CODE);


    }

    private void showProgressDialog() {
        progressBar.setMessage(activity.getString(R.string.pleas_wait));
        progressBar.setCancelable(false);
        progressBar.show();
    }

    private void stopProgressBar() {
        if (progressBar.isShowing()) {
            progressBar.cancel();
        }
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView workRowNumber, forosh, jobSubject, tozihat, date, kharid;
        LinearLayout work_row;


        public ViewHolder(View itemView) {
            super(itemView);
            workRowNumber = itemView.findViewById(R.id.work_row_number);
            forosh = itemView.findViewById(R.id.work_forosh);
            jobSubject = itemView.findViewById(R.id.work_title);
            tozihat = itemView.findViewById(R.id.work_tozihat);
            date = itemView.findViewById(R.id.work_date);
            kharid = itemView.findViewById(R.id.work_kharid);
            work_row = itemView.findViewById(R.id.work_header_layout);
        }
    }

    public String format(int number) {
        DecimalFormat formatter = new DecimalFormat("##,###");
        return formatter.format(number);
    }

}
