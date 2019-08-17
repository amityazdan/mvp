package com.amit.groupsprojectmvc;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class RegistrationActivityPresenter {

    private final RegistrationView view;
    private final Model model;
    private static final int TAG_PERMISSION_CODE = 0;
    private long SPLASH_TIME = 500;


    public RegistrationActivityPresenter(RegistrationView view) {
        model = Model.getInstance();
        this.view = view;
    }

    public void updateCountryDialCode() {
        view.updatePhoneText(model.getCountryDialCode((Activity)view));
    }

    public void phoneVerification(String phoneNumber) {
        PhoneListener phoneListener = new PhoneListener() {
            @Override
            public void get(PhoneVeriStage stage) {
                view.updatePhoneVerificationUI(stage);
            }
        };
        model.verifyPhoneNumber((Activity)view,phoneNumber, phoneListener);
    }

    public void verifySms(String codeTxt) {
        PhoneListener phoneListener = new PhoneListener() {
            @Override
            public void get(PhoneVeriStage stage) {
                view.updatePhoneVerificationUI(stage);
            }
        };
        model.smsVerify((Activity)view,codeTxt, phoneListener);
    }

    boolean timeReady = false, userReady = false;

    /**update main ui for replace Activities*/
    public void updateMainUI() {

        if (model.getUser() != null) {

            view.replaceViewToSplash();


            BoolListener timeListener = new BoolListener() {
                @Override
                public void get(boolean b) {
                    timeReady = true;
                    view.changeActivity(timeReady, userReady);
                }
            };

            //server time with firebase timeStemp
            model.initServerTime(0, timeListener);
            if (!checkPermission((Activity) view, Manifest.permission.ACCESS_FINE_LOCATION)) {
                requestPermission((Activity) view, TAG_PERMISSION_CODE, Manifest.permission.ACCESS_FINE_LOCATION);
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        userReady = true;
                        view.changeActivity(timeReady, userReady);
                    }
                }, SPLASH_TIME);
            }
        }

    }

    public interface RegistrationView {

        void updatePhoneText(String str);

        void updatePhoneVerificationUI(PhoneVeriStage stage);

        void replaceViewToSplash();

        void changeActivity(boolean timeReady, boolean userReady);

    }

    public interface PhoneListener {
        void get(PhoneVeriStage stage);
    }

    public interface BoolListener {
        void get(boolean b);
    }


    public boolean checkPermission(Activity activity, String permission) {
        int result = ContextCompat.checkSelfPermission(activity, permission);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    //REQUEST FOR PERMISSSION
    public void requestPermission(Activity activity, final int code, String permission) {

        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{permission},
                    code);
        } else {
            // Permission has already been granted
        }

    }
}
