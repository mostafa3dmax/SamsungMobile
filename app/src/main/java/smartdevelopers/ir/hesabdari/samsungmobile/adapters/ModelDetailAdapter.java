package smartdevelopers.ir.hesabdari.samsungmobile.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import smartdevelopers.ir.hesabdari.samsungmobile.R;
import smartdevelopers.ir.hesabdari.samsungmobile.model.ModelKEyValue;

/**
 * Created by mostafa on 18/07/2018.
 */

public class ModelDetailAdapter extends RecyclerView.Adapter<ModelDetailAdapter.ViewHolder> {
    private Activity activity;
    private ArrayList<ModelKEyValue> models=new ArrayList<>();

    public ModelDetailAdapter(Activity activity, ArrayList<ModelKEyValue> models) {
        this.activity = activity;
        this.models = models;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_detail_row,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ModelKEyValue model=models.get(position);
        holder.text.setText(model.getText());
        holder.title.setText(model.getTitle());

    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView title,text;

        public ViewHolder(View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.model_detail_title);
            text=itemView.findViewById(R.id.model_detail_content);
        }
    }
}
