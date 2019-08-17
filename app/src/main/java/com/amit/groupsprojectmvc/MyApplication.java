package com.amit.groupsprojectmvc;

import android.app.Application;
import android.content.Context;

import com.amit.groupsprojectmvc.Database.MyFirebase;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MyApplication extends Application {

    /**
     * font method
     *
     * @param newBase
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate() {
        super.onCreate();



        init();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/rubikregular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    private void init() {
        MyFirebase.init(MyApplication.this);
    }
}
