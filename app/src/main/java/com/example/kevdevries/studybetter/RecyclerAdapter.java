package com.example.kevdevries.studybetter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by KevdeVries on 09/04/2018.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyHolder>{

    List<FireModel> list;
    Context context;

    public RecyclerAdapter(List<FireModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.card,parent,false);
        MyHolder myHolder = new MyHolder(view);

        return myHolder;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        FireModel mylist = list.get(position);
        holder.title.setText(mylist.getTitle());
        holder.date.setText(mylist.getDate());
        holder.time.setText(mylist.getTime());
        holder.recurring.setText(mylist.getRecurring());
    }

    @Override
    public int getItemCount() {

        int arr = 0;

        try{
            if(list.size()==0){

                arr = 0;

            }
            else{

                arr=list.size();
            }

        }catch (Exception e){


        }

        return arr;

    }

    class MyHolder extends RecyclerView.ViewHolder{
        TextView title,date,time,recurring;

        public MyHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            date = (TextView) itemView.findViewById(R.id.date);
            time = (TextView) itemView.findViewById(R.id.time);
            recurring = (TextView) itemView.findViewById(R.id.recurring);
        }
    }
}
