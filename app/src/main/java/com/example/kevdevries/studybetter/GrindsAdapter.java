package com.example.kevdevries.studybetter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

/**
 * Created by KevdeVries on 09/04/2018.
 *
 * Adapter to create grind cards
 */

public class GrindsAdapter extends RecyclerView.Adapter<GrindsAdapter.MyHoder>{

    private List<GrindModel> list;
    private Context context;
    //get current user
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String email = user.getEmail();

    public GrindsAdapter(List<GrindModel> list, Context context) {
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
    public void onBindViewHolder(final MyHoder holder, int position) {
        GrindModel mylist = list.get(position);
        holder.title.setText(mylist.getTitle());
        holder.date.setText(mylist.getDate());
        holder.time.setText(mylist.getTime());
        holder.recurring.setText(mylist.getRecurring());
        holder.location.setText(mylist.getLocation());

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
        inflater.inflate(R.menu.grindmenu, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    protected void sendEmail() {
        Log.i("Send email", "");

        String[] TO = {email};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("message/rfc822");

        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Study Better");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "You have signed up to a new Grind Session!");

        try {
            context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            Log.i("Finished sending email", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.join:
                    Toast.makeText(context, "You have signed up to a new Grind session!", Toast.LENGTH_SHORT).show();
                    sendEmail();
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
        TextView title,date,time,location,recurring;
        ImageView overflow;

        private MyHoder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.vtitle);
            time = (TextView) itemView.findViewById(R.id.vtime);
            date = (TextView) itemView.findViewById(R.id.vdate);
            location = (TextView) itemView.findViewById(R.id.vlocation);
            recurring = (TextView) itemView.findViewById(R.id.vrecurring);
            overflow = (ImageView) itemView.findViewById(R.id.overflow);
        }
    }
}