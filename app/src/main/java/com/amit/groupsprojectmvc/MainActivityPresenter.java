package com.amit.groupsprojectmvc;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.amit.groupsprojectmvc.ListenersPack.ArrayListener;
import com.amit.groupsprojectmvc.ListenersPack.GroupListener;
import com.amit.groupsprojectmvc.ListenersPack.UserListener;

import java.util.ArrayList;

import static com.amit.groupsprojectmvc.Model.STOP_CALL_GROUPS;
import static com.amit.groupsprojectmvc.Model.first_group_ready;
import static com.amit.groupsprojectmvc.Model.fullGroupList;
import static com.amit.groupsprojectmvc.Model.nearbyIdsList;
import static com.amit.groupsprojectmvc.VARIABLE.LESS_THEN;
import static com.amit.groupsprojectmvc.VARIABLE.MAX_DISTANCE;

public class MainActivityPresenter {


    private Model model;
    private MainView mainView;

    public MainActivityPresenter(MainView mainView) {
        this.mainView = mainView;
        this.model = Model.getInstance();
    }


    GroupListener nearGroupLister;
    public static boolean ready;


    /**
     * refresh all groups list
     */
    public void refreshList() {
        mainView.clearNearGroupRV();
        mainView.showProgressBar(true);
        STOP_CALL_GROUPS = false;
        model.lastPOSITION = 0;
        model.lastItem = 0;

        /**near groups list is ready*/
        final ArrayListener nearListListener = new ArrayListener() {
            @Override
            public void response(ArrayList arr) {
                makeGroup();
                mainView.makeToast("we have " + arr.size() + " group near by");
            }
        };
        model.getNearList(nearListListener);
    }


    /**
     * init single near group callback listener
     * if after refresh or first time then clear list
     * add new group to list
     */
    public void makeGroup() {
        if (nearGroupLister == null) {
            nearGroupLister = new GroupListener() {
                @Override
                public void response(Group group) {
                    if (first_group_ready)
                        fullGroupList.clear();
                    first_group_ready = false;
                    mainView.showRefresh(false);
                    mainView.showProgressBar(false);
                    ready = true;
                    if (group == null) {
                        return;
                    }
                    mainView.addNearGroupRV(group);
                }
            };
        }
        model.makeGroups(nearGroupLister, (Activity) mainView);
    }


    /**
     * get groups list
     */
    public void updateRecyclerViews() {


        /**near groups list is ready*/
        final ArrayListener nearListListener = new ArrayListener() {
            @Override
            public void response(ArrayList arr) {
                makeGroup();
                mainView.makeToast("we have " + arr.size() + " group near by");
            }
        };

        /**personal single group ready*/
        final GroupListener singlePerGrpready = new GroupListener() {
            @Override
            public void response(Group group) {
                //array add group
                if (group != null)
                    mainView.addPersonalGroupRV(group);
                else {
                    //if group is null then start all group list
                    model.getNearList(nearListListener);
                }

            }
        };


        /**personal ids ready*/
        final ArrayListener personalReady = new ArrayListener() {
            @Override
            public void response(ArrayList arr) {
                mainView.personalGroupsUpdated(arr);
                model.getPersGroup((Activity) mainView, arr, singlePerGrpready);
            }
        };

        //get personal group after updated me
        UserListener userListener = new UserListener() {
            @Override
            public void get(User user) {
                mainView.notifyUserUpdated(user);
                //me ready
                //now notifyUserUpdated personal Groups from firebase
                model.getPersonalList(personalReady);
            }
        };
        //update me
//        updateMe(userListener);


        model.initMainProcess((Activity)mainView,userListener);


    }





    /**
     * update me in firebase
     */
    private void updateMe(UserListener userListener) {

        model.setMe((Activity) mainView, userListener);
    }


    public void recyclerScrollUpdate(int dx, int dy, LinearLayoutManager layoutManager) {
        if (!STOP_CALL_GROUPS) {
            if (dy > 0 && model.lastItem <= nearbyIdsList.size()) {
                model.lastItem = Math.max(layoutManager.findLastVisibleItemPosition(), model.lastItem);
                if (fullGroupList.size() - model.lastItem < LESS_THEN && ready) {
                    ready = false;
                    makeGroup();
                }
            }
        }

        if (layoutManager.findFirstVisibleItemPosition() == 3) {
            mainView.showScrlBtn(true);
        } else if (layoutManager.findFirstVisibleItemPosition() == 2) {
            mainView.showScrlBtn(false);
        }
    }

    public void scrollToPosition(RecyclerView recyclerView, boolean show) {
        recyclerView.scrollToPosition(0);
        mainView.updateListPositionView(show);
    }

    public void updateSeekText(int progress) {
        int prog = Integer.parseInt(String.valueOf(progress));
        String str=(100 + (int) Math.pow(prog, 2.35) + " Meter");
        mainView.setSeekText(str);
    }

    public void seekChanged(int progress) {
        STOP_CALL_GROUPS = false;
        int prog = Integer.parseInt(String.valueOf(progress));
        double meters = Math.pow(prog, 2.35);
        MAX_DISTANCE = 100 + (int) meters;
        refreshList();
        Log.d("SEEKBAR", MAX_DISTANCE + "");
    }


    public interface MainView {
        void notifyUserUpdated(User user);

        void personalGroupsUpdated(ArrayList arrayList);

        void addPersonalGroupRV(Group group);

        void addNearGroupRV(Group group);
        void clearNearGroupRV();

        void makeToast(String toast);

        void showScrlBtn(boolean show);

        void showProgressBar(boolean show);
        void showRefresh(boolean show);
        void updateListPositionView(boolean show);
        void setSeekText(String str);

    }
}
