package com.amit.groupsprojectmvc;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;

import java.util.regex.Pattern;


public class Model {

    private static Model model_instance = null;
    private String countryCode;
    private Activity activity;

    // static method to create instance of Singleton class
    public static Model getInstance() {
        if (model_instance == null)
            model_instance = new Model();

        return model_instance;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    /**
     * @return country dial code
     */
    public String getCountryDialCode() {

        String contryId = null;
        String contryDialCode = null;

        TelephonyManager telephonyMngr = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);

        contryId = telephonyMngr.getSimCountryIso().toUpperCase();
        String[] arrContryCode = activity.getResources().getStringArray(R.array.DialingCountryCode);
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
     * @param phoneNumber to verify
     * @param verificationListener
     */
    public void verifyPhoneNumber( String phoneNumber, RegistrationActivityPresenter.PhoneListener verificationListener) {
        MyFirebase myFirebase = MyFirebase.getInstance();
        myFirebase.initFirebaseApp(activity);


        //phone not valid
        if (!isValidMobile(phoneNumber)) {
            verificationListener.get(PhoneVeriStage.notValid);
            return;
        }

        //phone is valid
        myFirebase.phoneVerification(activity,phoneNumber, verificationListener);
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

    public void smsVerify(String codeTxt, RegistrationActivityPresenter.PhoneListener phoneListener) {
        MyFirebase instance = MyFirebase.getInstance();
        instance.codeVerify(activity,codeTxt,phoneListener);
    }

    public void initServerTime(int time, RegistrationActivityPresenter.BoolListener timeListener) {
        MyFirebase.getInstance().setTime(time,timeListener);
    }

    public User getUser() {
        return MyFirebase.getInstance().getFirebaseUser();
    }
}
