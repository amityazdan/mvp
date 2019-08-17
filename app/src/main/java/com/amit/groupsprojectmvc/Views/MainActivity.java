package com.amit.groupsprojectmvc.Views;

import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amit.groupsprojectmvc.Group;
import com.amit.groupsprojectmvc.MainActivityPresenter;
import com.amit.groupsprojectmvc.NearAdapter;
import com.amit.groupsprojectmvc.PersonalAdapter;
import com.amit.groupsprojectmvc.R;
import com.amit.groupsprojectmvc.User;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MainActivityPresenter.MainView {

    private RelativeLayout collLay;
    private AppBarLayout appBarLayout;
    private RecyclerView recyclerView, personalRecycler;
    private Button addNew;
    private MainActivityPresenter mainActivityPresenter;
    private PersonalAdapter personalAdapter;
    private NearAdapter nearAdapter;
    private Button scrlB;
    private ProgressBar mainProgressBar;
    private SwipeRefreshLayout pullToRefresh;
    private SeekBar seekBar;
    private TextView seekText;
    private boolean appBarIsExpanded = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();


        mainActivityPresenter = new MainActivityPresenter(this);
        mainActivityPresenter.updateRecyclerViews();

        AppBarViews();

        /**full recyclerView*/

        personalRecycler();
        nearRecycler();

        scrlB = (Button) findViewById(R.id.scrlB);
        mainProgressBar = (ProgressBar) findViewById(R.id.pb);
        pullToRefresh = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        scrlB = (Button) findViewById(R.id.scrlB);
        scrlB.setOnClickListener(this);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekText = (TextView) findViewById(R.id.seekText);


        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mainActivityPresenter.refreshList();
            }
        });


        mainActivityPresenter.updateSeekText(seekBar.getProgress());


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mainActivityPresenter.updateSeekText(seekBar.getProgress());

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mainActivityPresenter.seekChanged(seekBar.getProgress());


            }
        });

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                appBarIsExpanded = (verticalOffset == 0);

            }
        });
    }

    private void nearRecycler() {
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        nearAdapter = new NearAdapter();
        recyclerView.setAdapter(nearAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mainActivityPresenter.recyclerScrollUpdate(dx, dy, layoutManager);
            }
        });
    }

    private void personalRecycler() {
        personalRecycler = (RecyclerView) findViewById(R.id.personalRecycler);
        addNew = (Button) findViewById(R.id.addNewGroup);
        addNew.setOnClickListener(this);

        personalRecycler.setHasFixedSize(true);
        LinearLayoutManager personalLayoutManager = new LinearLayoutManager(this);
        personalRecycler.setLayoutManager(personalLayoutManager);
        personalAdapter = new PersonalAdapter();
        personalRecycler.setAdapter(personalAdapter);

    }

    //appbar
    private void AppBarViews() {
        collLay = (RelativeLayout) findViewById(R.id.collLay);
        collLay.setOnClickListener(this);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addNewGroup:

                break;
            case R.id.scrlB:
                mainActivityPresenter.scrollToPosition(recyclerView, false);
                break;

            case R.id.collLay:
                if (appBarIsExpanded) {
                    appBarLayout.setExpanded(false);
                } else {
                    appBarLayout.setExpanded(true);
                }
                break;
        }
    }

    @Override
    public void notifyUserUpdated(User me) {
        if (me != null) {
            Toast.makeText(this, me.getName(), Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, "no user", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void personalGroupsUpdated(ArrayList arrayList) {
        if (arrayList.isEmpty()) {
            Toast.makeText(this, "no personal groups", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void addPersonalGroupRV(Group group) {
        personalAdapter.add(group);
        personalAdapter.notifyDataSetChanged();
    }


    @Override
    public void clearNearGroupRV() {
        nearAdapter.clear();
        nearAdapter.notifyDataSetChanged();
    }

    @Override
    public void addNearGroupRV(Group group) {
        nearAdapter.add(group);
        nearAdapter.notifyDataSetChanged();
    }

    @Override
    public void makeToast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showScrlBtn(boolean show) {
        if (show) {
            scrlB.setVisibility(View.VISIBLE);
            scrlB.animate().scaleX(1).scaleY(1).start();
        } else {
            scrlB.animate().scaleX(0).scaleY(0).withEndAction(new Runnable() {
                @Override
                public void run() {
                    scrlB.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void showProgressBar(boolean show) {
        mainProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showRefresh(boolean show) {
        pullToRefresh.setRefreshing(show);

    }

    @Override
    public void updateListPositionView(boolean show) {
        if (show) {
            scrlB.setVisibility(View.VISIBLE);
            scrlB.animate().scaleX(1).scaleY(1).start();
        } else {
            scrlB.animate().scaleX(0).scaleY(0).withEndAction(new Runnable() {
                @Override
                public void run() {
                    scrlB.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void setSeekText(String str) {
        seekText.setText(str);
    }
}
