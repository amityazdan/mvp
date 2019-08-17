package com.amit.groupsprojectmvc.LocationPack;


import com.amit.groupsprojectmvc.Group;
import com.amit.groupsprojectmvc.MathFucnClass;
import com.amit.groupsprojectmvc.Me;

import java.util.Comparator;

/**
 * distance comparator
 */
public class DistanceComparator implements Comparator<Group> {


    @Override
    public int compare(Group g1, Group g2) {
        MyLocation l1 = g1.getLocation();
        MyLocation l2 = g2.getLocation();

        double curLat = Me.getMeInstance().getCurrentLocation().getLatitude();
        double curLon = Me.getMeInstance().getCurrentLocation().getLongitude();

        double d1 = Math.round(MathFucnClass.distance(curLat, curLon, l1.getLatitude(), l1.getLongitude(), 'm'));
        double d2 = Math.round(MathFucnClass.distance(curLat, curLon, l2.getLatitude(), l2.getLongitude(), 'm'));


        return Double.compare(d1, d2);
    }


}