package com.amit.groupsprojectmvc;


import android.app.Activity;

import com.amit.groupsprojectmvc.LocationPack.MyLocation;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class Group {

    private double _distance;
    private String _ID;
    private String _type;

    private long timeInMilis;
    private MyLocation _location;
    private String _creatorId;
    private int _minimum;
    private int _maximum;
    private ArrayList<String> _registersIds;


    /**
     * @param ID
     * @param type
//     * @param date
     */
    public Group(String ID, String type, /*Date date,*/ MyLocation location/*, Time time*/, String creatorId, long timeInMilis) {
        _ID = ID;
//        _date = date;
        _type = type;
        _location = location;
        _creatorId = creatorId;
//        _time = time;

    }

    /**
     * create group from snapshot
     *
     * @param dataSnapshot
     */
    public Group(DataSnapshot dataSnapshot, Activity activity, Me me) {
        double lat = Double.parseDouble(dataSnapshot.child("location/latitude").getValue().toString());
        double lon = (double) dataSnapshot.child("location/longitude").getValue();
        String address = dataSnapshot.child("location/address").getValue() + "";
        String id=dataSnapshot.getKey();
        int max=Integer.parseInt(dataSnapshot.child("max").getValue().toString());
        int min=Integer.parseInt(dataSnapshot.child("min").getValue().toString());
        String creatorId= (String) dataSnapshot.child("creator").getValue();
        String type= (String) dataSnapshot.child("type").getValue();


        long mili=((long)dataSnapshot.child("date").getValue());


        DataSnapshot registers = dataSnapshot.child("registers");
        Iterable<DataSnapshot> Children = registers.getChildren();
        ArrayList<String > ids=new ArrayList<>();
        for (DataSnapshot register : Children) {
            try {
                ids.add(register.getKey());
            } catch (Exception e) {
                //todo response exception
            }
        }

        setRegistersIds(ids);


        setTimeInMilis(mili);
        setDistance(MathFucnClass.distance(me.getCurrentLocation().getLatitude(), me.getCurrentLocation().getLongitude(), lat, lon, 'm'));
        setID(id);
        setMinimum(min);
        setMaximum(max);
        setCreatorId(creatorId);
        setLocation(new MyLocation(lat, lon, address),activity);
        setType(type);



//        if (dataSnapshot.child("date").getValue() != null)
//            setDate((Date) dataSnapshot.child("date").getValue());
//        if (dataSnapshot.child("time").getValue() != null)
//            setTime((Time) dataSnapshot.child("time").getValue());


    }

    public Group() {

    }


    public int getDistance() {
        return (int)_distance ;
    }

    public void setDistance(double _distance) {
        this._distance = _distance;
    }

    public String getID() {
        return _ID;
    }

    public void setID(String ID) {
        this._ID = ID;
    }

    public String getType() {
        return _type;
    }

    public void setType(String type) {
        if (type != null)
            this._type = type;
    }

    public MyLocation getLocation() {
        return _location;
    }

    public void setLocation(MyLocation location, Activity activity) {
        this._location = new MyLocation(location,activity);

    }

    public String getCreatorId() {
        return _creatorId;
    }

    public void setCreatorId(String creatorId) {
        this._creatorId = creatorId;
    }

    public int getMinimum() {
        return _minimum;
    }

    public void setMinimum(int _minimum) {
        this._minimum = _minimum;
    }

    public int getMaximum() {
        return _maximum;
    }

    public void setMaximum(int _maximum) {
        this._maximum = _maximum;
    }

    public ArrayList<String> getRegistersIds() {
        return _registersIds;
    }

    public void setRegistersIds(ArrayList<String> _registersIds) {
        this._registersIds = _registersIds;
    }


    public long getTimeInMilis() {
        return timeInMilis;
    }

    public void setTimeInMilis(long timeInMilis) {
        this.timeInMilis = timeInMilis;
    }

}
