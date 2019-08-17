package com.amit.groupsprojectmvc.LocationPack;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.amit.groupsprojectmvc.MyApplication;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MyLocation {



    private double longitude;
    private double latitude;
    private String address;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public MyLocation(MyLocation location, Activity activity) {
        setLongitude(location.getLongitude());
        setLatitude(location.getLatitude());
        setAddress(location.getAddress(activity));
    }

    public MyLocation(double latitude, double longitude) {
        setLongitude(longitude);
        setLatitude(latitude);
    }

    public MyLocation(double latitude, double longitude, String address) {
        setLongitude(longitude);
        setLatitude(latitude);
        setAddress(address);
    }


    /**
     * must have empty succeed for the firebase class compress on write data
     */
    public String getAddress() {
        return address;
    }

    public String getAddress(Activity activity) {
        if (address == null) {
            String newAddress = getAddressFromLatLand(activity);
            setAddress(newAddress);
            return newAddress;
        }
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    private String getAddressFromLatLand(Context context) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5


        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (addresses.size() != 0) {
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

                return address + ", " + city;
            } else {
                return "didn't find address from geocoder";
            }
        }catch (Exception e){
            return "didn't find address from geocoder";
        }
    }



}
