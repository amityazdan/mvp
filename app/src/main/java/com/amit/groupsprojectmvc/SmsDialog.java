package com.amit.groupsprojectmvc;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SmsDialog extends Dialog {
    private final RegistrationActivityPresenter registrationActivityPresenter;
    private EditText codeET;
    private Button codeBtn;

    public SmsDialog(Context context, RegistrationActivityPresenter registrationActivityPresenter) {
        super(context);
        this.registrationActivityPresenter = registrationActivityPresenter;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_dialog);


        views();

        codeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrationActivityPresenter.verifySms(codeET.getText().toString());
            }
        });
    }
    private void views() {

        codeET = (EditText) findViewById(R.id.codeET);
        codeBtn = (Button) findViewById(R.id.codeBtn);
    }

}