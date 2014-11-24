package com.tenforwardconsulting.cordova.bgloc.data;

import java.util.Date;

public interface LocationDAO {
    public boolean persistLocation(Location l, String s, Integer i);
    public void deleteLocation(Location l, String s);
    public String dateToString(Date date);
}
