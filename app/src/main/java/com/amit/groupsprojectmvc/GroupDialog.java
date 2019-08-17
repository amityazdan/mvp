//package com.amit.groupsprojectmvc;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.content.DialogInterface;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.view.View;
//import android.view.Window;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.YazdanAmit.Game.Activities.MainScreen.MainActivity;
//import com.YazdanAmit.Game.Firebase.MyFirebase;
//import com.YazdanAmit.Game.MyListeners.BooleanListener;
//import com.YazdanAmit.Game.MyListeners.GeneralListener;
//import com.YazdanAmit.Game.MyUtils;
//import com.YazdanAmit.Game.simpleClass.MyMessage;
//import com.google.firebase.database.ChildEventListener;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//import java.util.ArrayList;
//
////import static com.YazdanAmit.Game.Activities.MainScreen.MainActivity.addPersonalGroup;
////import static com.YazdanAmit.Game.Activities.MainScreen.MainActivity.removeFullGroup;
////import static com.YazdanAmit.Game.Activities.MainScreen.MainActivity.removePersonalGroup;
//
//
//public class GroupDialog extends Dialog implements View.OnClickListener {
//
//    private final Group group;
//    private final int position;
//    private final Activity activity;
//    private final Me me;
//    private boolean imIn = false;
//    private Button join;
//    private LinearLayout dltTV;
//    private TextView date, address, type, creator, distance, registersTV;
//    private boolean imTheCreator;
//    private boolean ready = true;
//    private ProgressBar pb;
//    private ArrayList<String> registersIds;
//    private EditText chatEt;
//    private ListView listView;
//    private ChatAdapter adapter;
//    private LinearLayout chatBtn;
//    private ChildEventListener childEventListener;
//    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
//    private ImageView exit;
//    private BooleanListener joinGroupListener;
//    private BooleanListener deleteGroupListener;
//    private BooleanListener leaveGroupListener;
//
//    public GroupDialog(Activity activity, Group group, int position) {
//        super(activity);
//        this.group = group;
//        this.activity = activity;
//        this.position = position;
//
//
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.group_dialog);
////        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//
//
//        initializeViews();
//
//
//        registersIds = group.getRegistersIds();
//        //if im the creator
//        if (group.getCreatorId().equals(me.getId())) {
//            imTheCreator = true;
//            dltTV.setVisibility(View.VISIBLE);
//        }
//        //not the creator but check if im in
//        else {
//            join.setClickable(true);
//            join.setVisibility(View.VISIBLE);
//            for (int i = 0; i < registersIds.size(); i++) {
//                if (me.getId().equals(registersIds.get(i))) {
//                    join.setText("leave group");
//                    imIn = true;
//                    break;
//                }
//            }
//        }
//
//
//        updateText();
//        //set join leave listeners
//        Listeners();
//
//
//        adapter = new ChatAdapter(activity, R.id.list,me);
//        listView.setAdapter(adapter);
//
//
//        childEventListener = new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                try {
//
//                    String msgStr = dataSnapshot.child("message").getValue().toString();
//                    long timeLong = (long) dataSnapshot.child("time").getValue();
//                    String name = (String) dataSnapshot.child("userName").getValue();
//                    String userId = (String) dataSnapshot.child("userID").getValue();
//
//                    if (userId == null) {
//                        userId = "000";
//                    }
//                    MyMessage msg = new MyMessage(name, msgStr, timeLong, userId);
//
//                    //todo make group dialog listener
//                    if (chatListener != null) {
//                        chatListener.chatMessage(msg);
//                    }
//                } catch (Exception e) {
//                    myUtils.AmitLog("Chat response exception: " + e);
//                }
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };
//
//
//        mDatabase.child("chat/" + group.getID()).addChildEventListener(childEventListener);
//
//
//    }
//
//    @Override
//    public void onDetachedFromWindow() {
//        mDatabase.child("chat/" + group.getID()).removeEventListener(childEventListener);
//        super.onDetachedFromWindow();
//    }
//
//    private void sendNoti(String msg) {
////        myFirebase.sendNotificationToID2(me.getToken());
//
//
//        for (int i = 0; i < group.getRegistersIds().size(); i++) {
//            myFirebase.getUserTokenSendMsg(group.getRegistersIds().get(i), msg,activity,me);
//        }
//    }
//
//    private void scrollToBottom() {
//        listView.post(new Runnable() {
//            @Override
//            public void run() {
//                listView.setSelection(adapter.getCount() - 1);
//            }
//        });
//    }
//
//    private void updateText() {
//        date.setText(myUtils.dateCoolFormat(group.getTimeInMilis()));
//        creator.setText(group.getCreatorId());
//
//        if (group.getType().isEmpty())
//            type.setText("Most Awesome group");
//        else
//            type.setText(group.getType());
//        address.setText(group.getLocation().getAddress(activity));
//        distance.setText(group.getDistance() + " Meters from you");
//        registersTV.setText(registersIds.size() + "/" + group.getMinimum());
//    }
//
//    private void Listeners() {
//        //chat message listener
//        chatListener=new MyFirebase.ChatListener() {
//            @Override
//            public void chatMessage(MyMessage msg) {
//                adapter.add(msg);
//                scrollToBottom();
//            }
//        };
//
////        chatListener = new MyFirebase.ChatListener() {
////            @Override
////            public void chatMessage(MyMessage msg) {
////                adapter.add(msg);
////                scrollToBottom();
////            }
////        };
//
//        //join group success listener
////        myFirebase.joinGroupListener = new MyFirebase.BooleanListener() {
//        joinGroupListener = new BooleanListener() {
//            @Override
//            public void success(boolean f) {
//
//                if (f) {
//                    //add my id to local list and update list ui
//                    registersIds.add(me.getId());
//                    group.setRegistersIds(registersIds);
//                    MainActivity.updateItem(position);
//                    myUtils.AmitLog("join group: success, id: " + group.getID());
//                    imIn = true;
//                    join.setText("leave group");
//                    sendNoti("Joined the group");
//
//                    //update list views
//                    generalListener.addPersonalGroup(group);
//                    generalListener.removeFullGroup(group);
//
//                } else {
//                    myUtils.AmitLog("join group: fail, id: " + group.getID());
//                    Toast.makeText(getContext(), "please try again", Toast.LENGTH_SHORT).show();
//                }
//
//                updateText();
//                pb.setVisibility(View.GONE);
//                ready = true;
//            }
//        };
//
//        //leave group success listener
//        leaveGroupListener = new BooleanListener() {
//            @Override
//            public void success(boolean f) {
//
//                if (f) {
//                    //remove my id from local list and update list ui
//                    registersIds.remove(me.getId());
//                    MainActivity.updateItem(position);
//                    myUtils.AmitLog("join: success");
//                    imIn = false;
//                    join.setText("join");
//                    sendNoti("Left the group");
//
//
//                    //update list views
//                    generalListener.removePersonalGroup(group);
//                    generalListener.addFullGroup(group);
//
//                } else {
//                    myUtils.AmitLog("join: fail");
//                    Toast.makeText(getContext(), "please try again", Toast.LENGTH_SHORT).show();
//                }
//                updateText();
//
//                pb.setVisibility(View.GONE);
//                ready = true;
//            }
//        };
//
//
//
//        //delete
//        deleteGroupListener = new BooleanListener() {
//            @Override
//            public void success(boolean f) {
//                if (f) {
////                    MainActivity.refreshList();
////                    MainActivity.removeItem(position);
//                    myUtils.AmitLog("Group deleted id: " + group.getID());
//                    Toast.makeText(getContext(), "Group deleted", Toast.LENGTH_SHORT).show();
//                    //todo refresh list
////                    removePersonalGroup(position);
////                    removeFullGroup(position);
//
//                    generalListener.removePersonalGroup(group);
//                    generalListener.removeFullGroup(group);
//                    dismiss();
//                } else {
//                    pb.setVisibility(View.GONE);
//                    myUtils.AmitLog("Group Not deleted id: " + group.getID());
//                    Toast.makeText(getContext(), "Something went wrong please try again", Toast.LENGTH_SHORT).show();
//                }
//            }
//        };
//    }
//
//    /**
//     * initialize all views
//     */
//    private void initializeViews() {
//        join = (Button) findViewById(R.id.join);
//        join.setOnClickListener(this);
//        dltTV = (LinearLayout) findViewById(R.id.dltTV);
//        dltTV.setOnClickListener(this);
//
//        pb = (ProgressBar) findViewById(R.id.pb);
//        date = (TextView) findViewById(R.id.date);
//        address = (TextView) findViewById(R.id.address);
//        type = (TextView) findViewById(R.id.type);
//        creator = (TextView) findViewById(R.id.creator);
//        registersTV = (TextView) findViewById(R.id.regis);
//        distance = (TextView) findViewById(R.id.distance);
//        exit = (ImageView) findViewById(R.id.exit);
//        exit.setOnClickListener(this);
//
//        chatBtn = (LinearLayout) findViewById(R.id.chatBtn);
//        chatBtn.setOnClickListener(this);
//
//        chatEt = (EditText) findViewById(R.id.chatEt);
//
//        listView = (ListView) findViewById(R.id.list);
//
//    }
//
//    /**
//     * @param v the view clicked
//     */
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//
//            case R.id.chatBtn:
//                String chatMsgStr = chatEt.getText().toString().trim();
//                if (chatMsgStr.isEmpty()) {
//                    Toast.makeText(activity, "No message..", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                MyMessage msg = new MyMessage(me.getName(), chatEt.getText().toString().trim(), 0, me.getId());
//                myFirebase.setChatMessage(group.getID(), msg);
//                chatEt.setText("");
//                scrollToBottom();
//
//                sendNoti(msg.getMessage());
//                break;
//            case R.id.join:
//                joinLeaveGroup();
//                break;
//            case R.id.dltTV:
//                myAlertDialog();
//                break;
//            case R.id.exit:
//                dismiss();
//                break;
//        }
//    }
//
//    /**
//     * join or leave group
//     */
//    private void joinLeaveGroup() {
//        pb.setVisibility(View.VISIBLE);
//        //leave group
//        if (imIn && ready) {
//            myUtils.AmitLog("onClick: asked to leave the group id: " + group.getID());
//            ready = false;
//            myFirebase.leaveGroup(group.getID(),me,leaveGroupListener);
//            //todo remove me from list
//        }
//        //join group
//        else if (imIn == false && ready) {
//            myUtils.AmitLog("onClick: ask to join the group id: " + group.getID());
//            ready = false;
//            myFirebase.joinGroup(group.getID(),me,joinGroupListener);
//        }
//    }
//
//    /**
//     * show dialog to delete
//     * buttons delete/cancel
//     */
//    private void myAlertDialog() {
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        builder.setMessage("Are you sure you want to delete this group")
//                .setPositiveButton("delete", new OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        myFirebase.deleteGroup(group.getID(),me,deleteGroupListener);
//                        pb.setVisibility(View.VISIBLE);
//                        dialog.cancel();
//                    }
//                });
//        builder.setNegativeButton("cancel", new OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                dialog.cancel();
//                // User cancelled the dialog
//            }
//        });
//
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }
//}
