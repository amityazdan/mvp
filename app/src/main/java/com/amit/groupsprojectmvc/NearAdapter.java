package com.amit.groupsprojectmvc;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;

//import static com.YazdanAmit.Game.Activities.MainScreen.MainActivity.me;

public class NearAdapter extends RecyclerView.Adapter<NearAdapter.MyViewHolder> {
    private Activity activity;
    private ArrayList<Group> groupList=new ArrayList<>();

    public void add(Group group) {
        groupList.add(group);
    }

    public void clear() {
        groupList.clear();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView registers;
        public TextView id;
        public TextView type;
        public TextView creator;
        public TextView date;
        public TextView location;
        public TextView distance;
        public ImageView mineImg;
//        public TextView textView;

        public MyViewHolder(View v) {
            super(v);
            id = v.findViewById(R.id.group_id);
            type = v.findViewById(R.id.group_type);
            creator = v.findViewById(R.id.group_creator);
            date = v.findViewById(R.id.group_date);
            location = v.findViewById(R.id.group_location);
            distance = v.findViewById(R.id.group_distance);
            registers = v.findViewById(R.id.registers);


        }
    }

    @Override
    public NearAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_group_item, parent, false);
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
        holder.id.setText(group.getID());

        long distance = group.getDistance();

        //in kilometer
        if (distance >= 1000) {
            float distanceInKilometer = (distance / 100);
            distanceInKilometer = (float) (distanceInKilometer / 10.0);
            holder.distance.setText(distanceInKilometer + " Kilometers");
        }
        //in meter
        else {
            holder.distance.setText(distance + " Meters");
        }
        holder.type.setText(group.getType());
        holder.creator.setText(group.getCreatorId());
        holder.location.setText(group.getLocation().getAddress(activity));
        holder.registers.setText(group.getRegistersIds().size() + "/" + group.getMinimum());




        holder.date.setText(MathFucnClass.dateCoolFormat(group.getTimeInMilis()));


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                GroupDialog groupDialog = new GroupDialog(activity, group, position,myFirebase,myUtils,me,generalListener);
//                groupDialog.show();
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return groupList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}