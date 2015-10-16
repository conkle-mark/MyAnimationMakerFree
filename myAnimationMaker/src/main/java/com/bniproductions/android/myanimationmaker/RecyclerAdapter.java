package com.bniproductions.android.myanimationmaker;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Mark on 9/18/2015.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>{

    private static final String TAG = "RecyclerAdapter";
    private ClickListener clickListener;
    private LayoutInflater inflater;
    List<AboutNavigationDrawerInfo> drawerInfoList;

    RecyclerAdapter(Context context, List<AboutNavigationDrawerInfo> list){

        inflater = LayoutInflater.from(context);
        drawerInfoList = list;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = inflater.inflate(R.layout.recycler_view_row, viewGroup, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int i) {
        AboutNavigationDrawerInfo current = drawerInfoList.get(i);
        viewHolder.title.setText(current.title);
        viewHolder.icon.setImageResource(current.iconId);
    }

    @Override
    public int getItemCount() {
        return drawerInfoList.size();
    }

    public void setClickListener(ClickListener clickListener){
        this.clickListener = clickListener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView icon;
        TextView title;

        public MyViewHolder(View itemView){
            super(itemView);

            icon = (ImageView) itemView.findViewById(R.id.icon_view);
            title = (TextView) itemView.findViewById(R.id.title_view);
            title.setTypeface(Typeface.DEFAULT_BOLD);
            title.setTextSize(1, 20.0f);

            icon.setOnClickListener(this);
            title.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(clickListener != null){
                clickListener.itemClicked(v, getAdapterPosition());
            }
        }
    }
    public interface ClickListener{
        public void itemClicked(View view, int position);
    }
}
