package com.example.kevdevries.studybetter;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by KevdeVries on 12/03/2018.
 *
 * Adapter to create Study Group cards (send)
 */

public class SendStudyRecyclerAdapter extends RecyclerView.Adapter<SendStudyRecyclerAdapter.MyHoder>{

    private List<StudyModel> list;
    private Context context;

    public SendStudyRecyclerAdapter(List<StudyModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public MyHoder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.studycard,parent,false);
        MyHoder myHoder = new MyHoder(view);

        return myHoder;
    }

    @Override
    public void onBindViewHolder(final MyHoder holder, int position) {
        StudyModel mylist = list.get(position);
        holder.title.setText(mylist.getTitle());
        holder.date.setText(mylist.getDate());
        holder.time.setText(mylist.getTime());
        holder.recurring.setText(mylist.getRecurring());
        holder.location.setText(mylist.getLocation());
        holder.members.setText(mylist.getMembers());

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow);
            }
        });
    }

    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.studyadd, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }


    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.add:
                    Integer id = menuItem.getItemId();
                    Toast.makeText(context, "You have added this member to the group! " + id, Toast.LENGTH_SHORT).show();

                    return true;
                default:
            }
            return false;
        }
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
        TextView title,date,time,location,members,recurring;
        ImageView overflow;

        private MyHoder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.vtitle);
            time = (TextView) itemView.findViewById(R.id.vtime);
            date = (TextView) itemView.findViewById(R.id.vdate);
            location = (TextView) itemView.findViewById(R.id.vlocation);
            members = (TextView) itemView.findViewById(R.id.vmembers);
            recurring = (TextView) itemView.findViewById(R.id.vrecurring);
            overflow = (ImageView) itemView.findViewById(R.id.overflow);
        }
    }
}
