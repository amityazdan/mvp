package com.amit.groupsprojectmvc;


import android.app.Activity;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.concurrent.TimeUnit;

public class MyFirebase {


    private static MyFirebase firebase_instance;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseUser currentUser=FirebaseAuth.getInstance().getCurrentUser();
    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private static long serverTime;
    User me =new User();


    public static MyFirebase getInstance() {
        if (firebase_instance == null)
            firebase_instance = new MyFirebase();
        return firebase_instance;
    }

    public void initFirebaseApp(Activity activity) {
        FirebaseApp.initializeApp(activity);

    }

    public void phoneVerification(final Activity act, String phoneNumber, final RegistrationActivityPresenter.PhoneListener verificationListener) {
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                signInWithPhoneAuthCredential(act, credential, verificationListener);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    verificationListener.get(PhoneVeriStage.veriFailed);
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    verificationListener.get(PhoneVeriStage.veriFailed);
                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                verificationListener.get(PhoneVeriStage.codeSent);
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };
        startVerification(act, phoneNumber, mCallbacks);
    }

    private void startVerification(Activity act, String phoneNum, PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNum,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                act,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private void signInWithPhoneAuthCredential(Activity act, PhoneAuthCredential credential, final RegistrationActivityPresenter.PhoneListener verificationListener) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(act, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            currentUser = user;
                            verificationListener.get(PhoneVeriStage.succeed);


                        } else {
                            verificationListener.get(PhoneVeriStage.signInFailed);
                        }
                    }
                });
    }

    public void codeVerify(Activity act, String code, RegistrationActivityPresenter.PhoneListener phoneListener) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(act, credential, phoneListener);

    }


    public long getTime() {
        return serverTime;
    }

    public void setTime(long time, RegistrationActivityPresenter.BoolListener timeListener) {
        if (time == 0)
            getServerTime(timeListener);
        else {
            serverTime = time;
            if (timeListener != null)
                timeListener.get(true);
        }
    }

    private void getServerTime(final RegistrationActivityPresenter.BoolListener timeListener) {
        final String key = mDatabase.child("serverTime").push().getKey();
        mDatabase.child("serverTime/" + key).setValue(ServerValue.TIMESTAMP).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mDatabase.child("serverTime/" + key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        setTime((long) dataSnapshot.getValue(),timeListener);
                        mDatabase.child("serverTime/" + key).setValue(null);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }


    public User getFirebaseUser() {

        me.setId(currentUser.getUid());
        me.setPhone(currentUser.getPhoneNumber());
        me.setToken(FirebaseInstanceId.getInstance().getToken());
        me.setName(currentUser.getDisplayName());


        return me;
    }
}
