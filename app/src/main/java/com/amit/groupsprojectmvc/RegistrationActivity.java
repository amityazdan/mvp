package com.amit.groupsprojectmvc;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

public class RegistrationActivity extends AppCompatActivity implements RegistrationActivityPresenter.View {

    private FrameLayout loginContainer;
    private View phoneView, splashView;
    private EditText phoneET;
    private Button verifyBtn;
    private ImageView splashImg;
    private LayoutInflater inflater;
    private RegistrationActivityPresenter presenter;
    private SmsDialog smsDialog;


    /**
     * check if user already connected
     */
    @Override
    public void onStart() {
        super.onStart();
        presenter.updateMainUI();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        getSupportActionBar().hide();

        //inflate create views
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        phoneViews();
        splashViews();

        presenter = new RegistrationActivityPresenter(this);
        presenter.updateCountryDialCode();

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.phoneVerification(phoneET.getText().toString());
            }
        });
    }

    //splash view
    private void splashViews() {
        splashView = inflater.inflate(R.layout.splash_layout, null);
        splashImg = (ImageView) splashView.findViewById(R.id.splashImg);
    }

    //phone view
    private void phoneViews() {
        loginContainer = (FrameLayout) findViewById(R.id.loginContainer);
        loginContainer.startLayoutAnimation();
        phoneView = inflater.inflate(R.layout.phone_layout, null);
        phoneET = (EditText) phoneView.findViewById(R.id.phoneET);
        verifyBtn = (Button) phoneView.findViewById(R.id.verifyBtn);
        loginContainer.addView(phoneView);
    }

    @Override
    public void updatePhoneText(String countryDialCode) {
        phoneET.setText(countryDialCode);
        phoneET.setSelection(phoneET.getText().length());
    }

    @Override
    public void updatePhoneVerificationUI(PhoneVeriStage stage) {
        switch (stage) {
            case succeed:
                // Sign in success, update UI with the signed-in user's information
                if (smsDialog != null)
                    smsDialog.dismiss();
                presenter.updateMainUI();
                break;
            case codeSent:
                smsDialog = new SmsDialog(RegistrationActivity.this, presenter);
                smsDialog.show();
                break;
            case notValid:
                Toast.makeText(this, "Not Valid Number", Toast.LENGTH_SHORT).show();
                break;
            case signInFailed:
//                updateUI(null);
                Toast.makeText(this, "Sign in Failed", Toast.LENGTH_SHORT).show();
                break;

            case veriFailed:
                Toast.makeText(this, "Verification Failed", Toast.LENGTH_SHORT).show();
                break;
            case error:
                Toast.makeText(this, "Verification Error", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void replaceViewToSplash() {
        loginContainer.removeAllViews();
        loginContainer.addView(splashView);
        splashImg.animate().scaleY(1).scaleX(1).setDuration(700).start();
    }

    @Override
    public void changeActivity(boolean timeReady, boolean userReady) {
        if (timeReady & userReady) {
            Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    /**
     * check if permissions granted and update ui if does
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0
                        // permission was granted, yay! Do the
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    presenter.updateMainUI();
                } else {
//                    "Permission denied"
                    presenter.updateMainUI();

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

        }
    }
}

