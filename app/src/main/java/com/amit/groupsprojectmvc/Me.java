package com.amit.groupsprojectmvc;

import android.app.Activity;

import com.amit.groupsprojectmvc.Database.MyFirebase;
import com.amit.groupsprojectmvc.ListenersPack.ArrayListener;
import com.amit.groupsprojectmvc.ListenersPack.BooleanListener;
import com.amit.groupsprojectmvc.ListenersPack.UserListener;
import com.amit.groupsprojectmvc.LocationPack.MyGPS;
import com.amit.groupsprojectmvc.LocationPack.MyLocation;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;


public class Me extends User {


    private static Me meInstance;

    //create me singleton
    public static Me getMeInstance() {
        if (meInstance == null) {
            meInstance = new Me();
        }
        return meInstance;
    }

    private MyLocation _currentLocation;
    private ArrayList<String> personalGroups;

    /**
     * initialize
     * <p>
     * set current location on 'me'
     * set my firebase auth id
     * set my phone
     *
     * @param activity
     * @param userListener
     */
    public void initial(final Activity activity, final UserListener userListener) {

        /**
         * succeed single time current location
         */

        MyGPS.SingleShotLocationProvider.requestSingleUpdate(activity,
                new MyGPS.SingleShotLocationProvider.LocationCallback() {
                    @Override
                    public void onNewLocationAvailable(MyGPS.SingleShotLocationProvider.GPSCoordinates location) {
                        MyLocation currLocation = new MyLocation(location.latitude, location.longitude);
                        setCurrentLocation(currLocation);
                    }
                });


        String name = MyFirebase.getInstance().getFirebaseUser().getName();
        String id = MyFirebase.getInstance().getFirebaseUser().getId();
        String phone = MyFirebase.getInstance().getFirebaseUser().getPhone();
        if (name.isEmpty()) {

        }
        setName(name);
        setId(id);
        setPhone(phone);
        setToken(FirebaseInstanceId.getInstance().getToken());


        /**succeed the groups im registered
         * after done update my status on firebase*/
        BooleanListener updateMeListener = new BooleanListener() {
            @Override
            public void succeed(boolean b) {
                if (userListener != null)
                    userListener.get(Me.getMeInstance());

            }
        };


        /**start update my status*/
        MyFirebase.getInstance().updateMe(Me.this, updateMeListener);
    }


    public MyLocation getCurrentLocation() {
        return _currentLocation;
    }

    public void setCurrentLocation(MyLocation _currentLocation) {
        this._currentLocation = _currentLocation;
    }


    public void getPersonalList(final ArrayListener personalReady) {
        if (personalGroups == null) {

            /**personal groups is ready, set arr
             * called after update me*/
            final ArrayListener personalGroupsListener = new ArrayListener() {
                @Override
                public void response(ArrayList arr) {

                    setPersonalGroups(arr);
                    if (personalReady != null)
                        personalReady.response(arr);
                }
            };
            MyFirebase.getInstance().personalGroups(Me.this, personalGroupsListener);
        } else {
            if (personalReady != null)
                personalReady.response(personalGroups);

        }


    }

    public void setPersonalGroups(ArrayList personalGroups) {
        this.personalGroups = personalGroups;
    }

}
