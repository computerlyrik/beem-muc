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
package com.beem.project.beem.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.beem.project.beem.BeemService;
import com.beem.project.beem.R;
import com.beem.project.beem.service.aidl.IRoster;
import com.beem.project.beem.service.aidl.IXmppFacade;
import com.beem.project.beem.utils.BeemBroadcastReceiver;

/**
 * This activity is used to add a contact.
 * @author nikita
 */
public class AddContact extends Activity {

    private static final Intent SERVICE_INTENT = new Intent();
    private static final String TAG = "AddContact";
    private final List<String> mGroup = new ArrayList<String>();
    private IXmppFacade mXmppFacade;
    private final ServiceConnection mServConn = new BeemServiceConnection();
    private final BeemBroadcastReceiver mReceiver = new BeemBroadcastReceiver();
    private final OkListener mOkListener = new OkListener();

    static {
	SERVICE_INTENT.setComponent(new ComponentName("com.beem.project.beem", "com.beem.project.beem.BeemService"));
    }

    /**
     * Constructor.
     */
    public AddContact() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.addcontact);
	Button ok = (Button) findViewById(R.id.addc_ok);
	ok.setOnClickListener(mOkListener);
	this.registerReceiver(mReceiver, new IntentFilter(BeemBroadcastReceiver.BEEM_CONNECTION_CLOSED));
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onStart()
     */
    @Override
    protected void onStart() {
	super.onStart();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResume() {
	super.onResume();
	bindService(new Intent(this, BeemService.class), mServConn, BIND_AUTO_CREATE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPause() {
	super.onPause();
	unbindService(mServConn);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
	super.onDestroy();
	this.unregisterReceiver(mReceiver);
    }

    /**
     * Get the text of a widget.
     * @param id the id of the widget.
     * @return the text of the widget.
     */
    private String getWidgetText(int id) {
	EditText widget = (EditText) this.findViewById(id);
	return widget.getText().toString();
    }

    /**
     * The ServiceConnection used to connect to the Beem service.
     */
    private class BeemServiceConnection implements ServiceConnection {

	/**
	 * Constructor.
	 */
	public BeemServiceConnection() {
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
	    mXmppFacade = IXmppFacade.Stub.asInterface(service);
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
	    mXmppFacade = null;
	}
    }

    /**
     * Listener.
     */
    private class OkListener implements OnClickListener {

	/**
	 * Constructor.
	 */
	public OkListener() { }

	@Override
	public void onClick(View v) {
	    String login;
	    login = getWidgetText(R.id.addc_login);
	    boolean isEmail = Pattern.matches("[a-zA-Z0-9._%+-]+@(?:[a-zA-Z0-9-]+.)+[a-zA-Z]{2,4}", login);
	    if (!isEmail) {
		Toast.makeText(AddContact.this, getString(R.string.AddCContactAddedLoginError), Toast.LENGTH_SHORT)
		    .show();
		return;
	    }
	    String alias;
	    alias = getWidgetText(R.id.addc_alias);
	    if (getWidgetText(R.id.addc_group).length() != 0)
		mGroup.add(getWidgetText(R.id.addc_group));
	    try {
		if (mXmppFacade != null) {
		    IRoster roster = mXmppFacade.getRoster();
		    if (roster.getContact(login) != null) {
			mGroup.addAll(roster.getContact(login).getGroups());
			Toast.makeText(AddContact.this, getString(R.string.AddCContactAlready), Toast.LENGTH_SHORT)
			    .show();
			return;
		    }
		    if (!roster.addContact(login, alias, mGroup.toArray(new String[mGroup.size()]))) {
			Toast.makeText(AddContact.this, getString(R.string.AddCContactAddedError), Toast.LENGTH_SHORT)
			    .show();
			return;
		    } else {
			Toast.makeText(AddContact.this, getString(R.string.AddCContactAdded), Toast.LENGTH_SHORT)
			    .show();
			finish();
		    }
		}
	    } catch (RemoteException e) {
		Toast.makeText(AddContact.this, e.getMessage(), Toast.LENGTH_SHORT).show();
		Log.e(TAG, "Problem adding contact", e);
	    }

	}
    };
}
