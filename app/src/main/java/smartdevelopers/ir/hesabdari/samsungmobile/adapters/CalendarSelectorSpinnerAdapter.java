package smartdevelopers.ir.hesabdari.samsungmobile.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import smartdevelopers.ir.hesabdari.samsungmobile.R;

/**
 * Created by mostafa on 06/07/2018.
 */

public class CalendarSelectorSpinnerAdapter extends ArrayAdapter<Integer> {
  private Integer[] values;

    public CalendarSelectorSpinnerAdapter(@NonNull Context context, int resource, @NonNull Integer[] objects) {
        super(context, resource, objects);
        this.values=objects;
    }
    public View getCustomView(int position, View convertView, ViewGroup parent){
        View view=convertView;
        Holder holder;
        if (view==null){
            LayoutInflater inflater=LayoutInflater.from(parent.getContext());
             view=inflater.inflate(R.layout.calendar_spinner_custom_row,parent,false);
              holder=new Holder();
             holder.textView=view.findViewById(R.id.calendar_selector_spinner_text);
             view.setTag(holder);

        }else {
            holder=(Holder) view.getTag();
        }


        holder.textView.setText(String.valueOf(values[position]));
        return view;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position,convertView,parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position,convertView,parent);
    }
    class Holder{
        TextView textView;
    }
}
