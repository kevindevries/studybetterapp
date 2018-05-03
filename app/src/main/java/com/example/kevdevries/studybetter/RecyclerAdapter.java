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

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyHoder>{

    private List<FireModel> list;
    private Context context;

    public RecyclerAdapter(List<FireModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public MyHoder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.card,parent,false);
        MyHoder myHoder = new MyHoder(view);

        return myHoder;
    }

    @Override
    public void onBindViewHolder(MyHoder holder, int position) {
        FireModel mylist = list.get(position);
        holder.title.setText(mylist.getTitle());
        holder.date.setText(mylist.getDate());
        holder.time.setText(mylist.getTime());
        holder.recurring.setText(mylist.getRecurring());
        holder.location.setText(mylist.getLocation());
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

    class MyHoder extends RecyclerView.ViewHolder{
        TextView title,date,time,location,recurring;

        private MyHoder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.vtitle);
            time = (TextView) itemView.findViewById(R.id.vtime);
            date = (TextView) itemView.findViewById(R.id.vdate);
            location = (TextView) itemView.findViewById(R.id.vlocation);
            recurring = (TextView) itemView.findViewById(R.id.vrecurring);
        }
    }
}
