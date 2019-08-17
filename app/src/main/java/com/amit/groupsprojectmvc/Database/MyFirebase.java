package com.amit.groupsprojectmvc.Database;


import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.amit.groupsprojectmvc.Group;
import com.amit.groupsprojectmvc.ListenersPack.ArrayListener;
import com.amit.groupsprojectmvc.ListenersPack.BooleanListener;
import com.amit.groupsprojectmvc.ListenersPack.GroupListener;
import com.amit.groupsprojectmvc.LocationPack.DistanceComparator;
import com.amit.groupsprojectmvc.LocationPack.MyLocation;
import com.amit.groupsprojectmvc.Me;
import com.amit.groupsprojectmvc.Model;
import com.amit.groupsprojectmvc.MyApplication;
import com.amit.groupsprojectmvc.PhoneVeriStage;
import com.amit.groupsprojectmvc.RegistrationActivityPresenter;
import com.amit.groupsprojectmvc.User;
import com.amit.groupsprojectmvc.Views.MainActivity;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.amit.groupsprojectmvc.Model.nearbyIdsList;
import static com.amit.groupsprojectmvc.VARIABLE.MAX_DISTANCE;

public class MyFirebase {


    private static MyFirebase firebase_instance;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private static long serverTime;


    public static MyFirebase getInstance() {
        if (firebase_instance == null)
            firebase_instance = new MyFirebase();
        return firebase_instance;
    }

    public static void init(Context context) {
        FirebaseApp.initializeApp(context);
    }

    public void phoneVerification(final Activity activity, String phoneNumber, final RegistrationActivityPresenter.PhoneListener verificationListener) {
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                signInWithPhoneAuthCredential(activity, credential, verificationListener);
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
        startVerification(activity, phoneNumber, mCallbacks);
    }

    private void startVerification(Activity activity, String phoneNum, PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNum,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                activity,       // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private void signInWithPhoneAuthCredential(Activity activity, PhoneAuthCredential credential, final RegistrationActivityPresenter.PhoneListener verificationListener) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
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

    public void codeVerify(Activity activity, String code, RegistrationActivityPresenter.PhoneListener phoneListener) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(activity, credential, phoneListener);

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

                        setTime((long) dataSnapshot.getValue(), timeListener);
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
        Me meInstance = Me.getMeInstance();
        meInstance.setId(currentUser.getUid());
        meInstance.setPhone(currentUser.getPhoneNumber());
        meInstance.setToken(FirebaseInstanceId.getInstance().getToken());
        meInstance.setName(currentUser.getDisplayName());


        return meInstance;
    }

    public void personalGroups(Me me, final ArrayListener personalGroupsListener) {
        mDatabase.child("users/fullProfileList/" + me.getId() + "/mygroups").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> Children = dataSnapshot.getChildren();

                ArrayList<String> arr = new ArrayList<>();
                for (DataSnapshot id : Children) {
                    try {
                        String key = id.getKey();
                        arr.add(key);
                    } catch (Exception e) {
                        //todo response exception
                    }
                }
                if (personalGroupsListener != null)
                    personalGroupsListener.response(arr);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (personalGroupsListener != null)
                    personalGroupsListener.response(null);
            }
        });
    }

    /**
     * @param me update id
     *           update phone
     *           update token
     *           update name
     *           <p>
     *           listener for success and canceled
     */
    public void updateMe(Me me, final BooleanListener updateMeListener) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("users/fullProfileList/" + me.getId() + "/id", me.getId());
        childUpdates.put("users/fullProfileList/" + me.getId() + "/phone", me.getPhone());
        childUpdates.put("users/fullProfileList/" + me.getId() + "/token", me.getToken());
        childUpdates.put("users/fullProfileList/" + me.getId() + "/name", me.getName());
        childUpdates.put("users/phoneList/" + me.getPhone(), me.getId());

        mDatabase.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (updateMeListener != null) {
                        updateMeListener.succeed(true);
                    }
                } else if (task.isCanceled()) {
                    if (updateMeListener != null) {
                        updateMeListener.succeed(true);
                    }
                }
            }
        });
    }

    public void callGroup(String groupID, final Activity activity, final GroupListener groupReadyListener) {
        mDatabase.child("groups/" + groupID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    Group group = new Group(dataSnapshot, activity, Me.getMeInstance());
                    if (group.getDistance() < MAX_DISTANCE || Model.getInstance().fullGroupList.size() < 3)

                        if (groupReadyListener != null) {
                            groupReadyListener.response(group);
                        }
                } catch (Exception e) {
                    Model.getInstance().STOP_CALL_GROUPS = false;
                    if (groupReadyListener != null) {
                        groupReadyListener.response(null);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    /**
     * get a list from of all groups location
     * return a sorted list of all group location by distance
     *
     * @param getNearBy
     */
    public void getNearbyIds(final ArrayListener getNearBy) {
//        start = System.nanoTime();

        //clear and start with empty nearby ids
        nearbyIdsList.clear();
        mDatabase.child("location").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> Children = dataSnapshot.getChildren();

                long hour = 1000 * 60 * 60;
                long day = hour * 24;

                int total = 0;
                for (DataSnapshot group : Children) {
                    try {

                        MyLocation location = new MyLocation((double) group.child("latitude").getValue(),
                                (double) group.child("longitude").getValue());
                        long groupTime = (long) group.child("date").getValue();
                        total++;

                        if ((0 * day) + groupTime > getTime()) {
                            Group g = new Group(group.getKey(), null,
                                    location, null, groupTime);
                            nearbyIdsList.add(g);
                        }
                    } catch (Exception e) {
                        //todo response exception
                    }
                }

                try {
                    DistanceComparator distanceComparator = new DistanceComparator();
                    Collections.sort(nearbyIdsList, distanceComparator);
                } catch (Exception e) {
//                    ("getNearbyIds - sort exception: " + e);
                }


                //ready to initialize group list
                Model.getInstance().first_group_ready = true;

                if (getNearBy != null) {
                    getNearBy.response(nearbyIdsList);
//                    myUtils.AmitLog("Total groups: " + total);
//                    myUtils.AmitLog("nearbyIdsList size: " + nearbyIdsList.size());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (getNearBy != null) {
                    getNearBy.response(null);
//                    myUtils.AmitLog("nearbyIdsList: Cancelled" + databaseError);
                }
            }
        });


    }
}
