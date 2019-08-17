package com.amit.groupsprojectmvc;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.view.View;

import com.amit.groupsprojectmvc.Database.MyFirebase;
import com.amit.groupsprojectmvc.ListenersPack.ArrayListener;
import com.amit.groupsprojectmvc.ListenersPack.GroupListener;
import com.amit.groupsprojectmvc.ListenersPack.UserListener;

import java.util.ArrayList;
import java.util.regex.Pattern;

import static com.amit.groupsprojectmvc.VARIABLE.NUMBER_OF_CALL;


public class Model {

    public static boolean STOP_CALL_GROUPS = false;
    public static boolean first_group_ready = false;
    private static Model model_instance = null;
    private String countryCode;
    public static ArrayList<Group> personalFullGroup = new ArrayList();
    /**near by list is light groups list
     * contains:
     * location
     * date
     * */
    public static ArrayList<Group> nearbyIdsList = new ArrayList();
    public static ArrayList<Group> fullGroupList = new ArrayList();
    public int personalGroupThrowedException;
    public int lastPOSITION, lastItem;


    // static method to create instance of Singleton class
    public static Model getInstance() {
        if (model_instance == null)
            model_instance = new Model();

        return model_instance;
    }


    /**
     * @return country dial code
     */
    public String getCountryDialCode(Context context) {

        String contryId = null;
        String contryDialCode = null;

        TelephonyManager telephonyMngr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        contryId = telephonyMngr.getSimCountryIso().toUpperCase();
        String[] arrContryCode = context.getResources().getStringArray(R.array.DialingCountryCode);
        for (int i = 0; i < arrContryCode.length; i++) {
            String[] arrDial = arrContryCode[i].split(",");
            if (arrDial[1].trim().equals(contryId.trim())) {
                contryDialCode = arrDial[0];
                break;
            }
        }
        contryDialCode = ("+" + contryDialCode);
        countryCode = contryDialCode;
        return contryDialCode;

    }

    /**
     * @param phoneNumber          to verify
     * @param verificationListener
     */
    public void verifyPhoneNumber(Activity activity, String phoneNumber, RegistrationActivityPresenter.PhoneListener verificationListener) {
        MyFirebase myFirebase = MyFirebase.getInstance();


        //phone not valid
        if (!isValidMobile(phoneNumber)) {
            verificationListener.get(PhoneVeriStage.notValid);
            return;
        }

        //phone is valid
        myFirebase.phoneVerification(activity, phoneNumber, verificationListener);
    }

    /**
     * @param phone
     * @return if phone number is valid
     */
    public boolean isValidMobile(String phone) {
        boolean check = false;
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            if (phone.length() < 6 || phone.length() > 14) {
                // if(phone.length() != 10) {
                check = false;
            } else {
                check = true;
            }
        } else {
            check = false;
        }
        return check;
    }

    public void smsVerify(Activity activity, String codeTxt, RegistrationActivityPresenter.PhoneListener phoneListener) {
        MyFirebase firebaseInstance = MyFirebase.getInstance();
        firebaseInstance.codeVerify(activity, codeTxt, phoneListener);
    }

    public void initServerTime(int time, RegistrationActivityPresenter.BoolListener timeListener) {
        MyFirebase.getInstance().setTime(time, timeListener);
    }

    public User getUser() {
        return MyFirebase.getInstance().getFirebaseUser();
    }

    public void setMe(Activity act, UserListener userListener) {
        Me.getMeInstance().initial(act, userListener);
    }

    public Me getMe() {
        return Me.getMeInstance();
    }


    public void getPersonalList(ArrayListener personalReady) {
        Me.getMeInstance().getPersonalList(personalReady);
    }

    public void getPersGroup(Activity activity, ArrayList<String> arr, final GroupListener singlePerGrpready) {
        final MyFirebase firebaseInstance = MyFirebase.getInstance();

        GroupListener personalGroupListener = new GroupListener() {
            @Override
            public void response(Group group) {
                if (group == null) {
                    personalGroupThrowedException++;
                } else {
                    personalFullGroup.add(group);
                    singlePerGrpready.response(group);
                }
                if ((personalFullGroup.size() + personalGroupThrowedException) == personalFullGroup.size()) {
                    singlePerGrpready.response(null);
                }
            }
        };


        /**if no personal groups call all groups*/
        if (arr.size() == 0) {
            singlePerGrpready.response(null);

        } else
        /**else call my personal groups first*/
            for (int i = 0; i < arr.size(); i++) {
                firebaseInstance.callGroup(arr.get(i), activity, personalGroupListener);
            }
    }

    public void getNearList(final ArrayListener arrayListener) {

        ArrayListener getNearBy = new ArrayListener() {
            @Override
            public void response(ArrayList arr) {
                nearbyIdsList = arr;
                arrayListener.response(arr);
            }
        };
        MyFirebase.getInstance().getNearbyIds(getNearBy);
    }


    /**
     * pull some ids, and get the group from the firebase
     */
    public void makeGroups(final GroupListener nearGroupLister, Activity activity) {

        final GroupListener groupListener = new GroupListener() {
            @Override
            public void response(Group group) {
                fullGroupList.add(group);
                nearGroupLister.response(group);
            }
        };

        for (int i = 0; i < NUMBER_OF_CALL; i++, lastPOSITION++) {
            if (lastPOSITION == nearbyIdsList.size()) {
                //no more to load
                nearGroupLister.response(null);
                return;
            }
            String id = nearbyIdsList.get(lastPOSITION).getID();

            //todo check if i have already called this group in my personal
            if (!personalFullGroup.contains(id)) {
                MyFirebase.getInstance().callGroup(id, activity, groupListener);
            }
        }
    }


    public void initMainProcess(Activity activity, UserListener userListener) {
        setMe(activity, userListener);
    }
}
