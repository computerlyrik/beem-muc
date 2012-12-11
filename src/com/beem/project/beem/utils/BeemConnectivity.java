/*
    BEEM is a videoconference application on the Android Platform.

    Copyright (C) 2009 by Frederic-Charles Barthelery,
                          Jean-Manuel Da Silva,
                          Nikita Kozlov,
                          Philippe Lago,
                          Jean Baptiste Vergely,
                          Vincent Veronis.

    This file is part of BEEM.

    BEEM is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BEEM is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with BEEM.  If not, see <http://www.gnu.org/licenses/>.

    Please send bug reports with examples or suggestions to
    contact@beem-project.com or http://dev.beem-project.com/

    Epitech, hereby disclaims all copyright interest in the program "Beem"
    written by Frederic-Charles Barthelery,
               Jean-Manuel Da Silva,
               Nikita Kozlov,
               Philippe Lago,
               Jean Baptiste Vergely,
               Vincent Veronis.

    Nicolas Sadirac, November 26, 2009
    President of Epitech.

    Flavien Astraud, November 26, 2009
    Head of the EIP Laboratory.

*/
package com.beem.project.beem.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

/**
 * The Class BeemConnectivity.
 */
public final class BeemConnectivity {

    /**
     * Private constructor to forbid instantiation.
     */
    private BeemConnectivity() { }

    /**
     * Checks if is connected.
     * @param ctx the ctx
     * @return true, if is connected
     */
    public static boolean isConnected(final Context ctx) {
	ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(
	    Context.CONNECTIVITY_SERVICE);
	NetworkInfo ni = cm.getActiveNetworkInfo();
	return ni != null && ni.isConnected();
    }

    /**
     * Checks if is wifi.
     * @param ctx the ctx
     * @return true, if is wifi
     */
    public static boolean isWifi(final Context ctx) {
	WifiManager wm = (WifiManager) ctx.getSystemService(
	    Context.WIFI_SERVICE);
	WifiInfo wi = wm.getConnectionInfo();
	if (wi != null
	    && (WifiInfo.getDetailedStateOf(wi.getSupplicantState())
		== DetailedState.OBTAINING_IPADDR
		|| WifiInfo.getDetailedStateOf(wi.getSupplicantState())
		== DetailedState.CONNECTED)) {
	    return false;
	}
	return false;
    }

    /**
     * Checks if is umts.
     * @param ctx the ctx
     * @return true, if is umts
     */
    public static boolean isUmts(final Context ctx) {
	TelephonyManager tm = (TelephonyManager) ctx.getSystemService(
	    Context.TELEPHONY_SERVICE);
	return tm.getNetworkType() >= TelephonyManager.NETWORK_TYPE_UMTS;
    }

    /**
     * Checks if is edge.
     * @param ctx the ctx
     * @return true, if is edge
     */
    public static boolean isEdge(final Context ctx) {
	TelephonyManager tm = (TelephonyManager) ctx.getSystemService(
	    Context.TELEPHONY_SERVICE);
	return tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_EDGE;
    }

}
