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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.Toast;

import com.beem.project.beem.BeemService;
import com.beem.project.beem.R;

/**
 * Manage broadcast disconnect intent.
 * @author nikita
 */
public class BeemBroadcastReceiver extends BroadcastReceiver {

    /** Broadcast intent type. */
    public static final String BEEM_CONNECTION_CLOSED = "BeemConnectionClosed";

    /**
     * constructor.
     */
    public BeemBroadcastReceiver() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(final Context context, final Intent intent) {
	String intentAction = intent.getAction();
	if (intentAction.equals(BEEM_CONNECTION_CLOSED)) {
	    CharSequence message = intent.getCharSequenceExtra("message");
	    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	    if (context instanceof Activity) {
		Activity act = (Activity) context;
		act.finish();
		// The service will be unbinded in the destroy of the activity.
	    }
	} else if (intentAction.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
	    if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {
		Toast.makeText(context, context.getString(R.string.BeemBroadcastReceiverDisconnect),
		    Toast.LENGTH_SHORT).show();
		context.stopService(new Intent(context, BeemService.class));
	    }
	}
    }
}
