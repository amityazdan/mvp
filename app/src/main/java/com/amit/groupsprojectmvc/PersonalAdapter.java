package com.amit.groupsprojectmvc;


import android.app.Activity;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;

public class PersonalAdapter extends RecyclerView.Adapter<PersonalAdapter.MyViewHolder> {
    private ArrayList<Group> groupList=new ArrayList<>();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView month;
        public TextView day;
        public TextView personalHour;

        public MyViewHolder(View v) {
            super(v);
            month = v.findViewById(R.id.personalGroupIdTV);
            day = v.findViewById(R.id.personalDay);
            personalHour = v.findViewById(R.id.personalHour);
        }
    }

    public PersonalAdapter() {
    }

    public void add(Group group){
        groupList.add(group);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PersonalAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.personal_group_item, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element



        final Group group = groupList.get(position);


        Calendar c = Calendar.getInstance();
        //Set time in milliseconds
        c.setTimeInMillis(group.getTimeInMilis());
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        int hr = c.get(Calendar.HOUR);
        int min = c.get(Calendar.MINUTE);
        int sec = c.get(Calendar.SECOND);

        String monthString = new DateFormatSymbols().getMonths()[mMonth];

        holder.day.setText((mDay)+"");
        holder.month.setText(monthString.substring(0,3));

        holder.personalHour.setText(MathFucnClass.fixTime(hr-1,min));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                GroupDialog groupDialog = new GroupDialog(act,group,position,myFirebase,myUtils,me,generalListener);
//                groupDialog.show();
            }
        });


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return groupList.size();
    }

}