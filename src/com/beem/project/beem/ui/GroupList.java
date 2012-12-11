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

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.beem.project.beem.BeemService;
import com.beem.project.beem.R;
import com.beem.project.beem.service.Contact;
import com.beem.project.beem.service.aidl.IRoster;
import com.beem.project.beem.service.aidl.IXmppFacade;
import com.beem.project.beem.utils.BeemBroadcastReceiver;

/**
 * That activity permit to manage user groups.
 * @author nikita
 */
public class GroupList extends ListActivity {

    private static final Intent SERVICE_INTENT = new Intent();

    private final ServiceConnection mServConn = new BeemServiceConnection();
    private final BeemBroadcastReceiver mReceiver =  new BeemBroadcastReceiver();
    private IXmppFacade mXmppFacade;
    private IRoster mRoster;
    private String mJID;
    private ArrayAdapter<String> mGroups;
    private Contact mContact;
    private TextView mText;
    private final List<String> mStrings = new ArrayList<String>();

    static {
	SERVICE_INTENT.setComponent(new ComponentName("com.beem.project.beem", "com.beem.project.beem.BeemService"));
    }

    /**
     * Constructor.
     */
    public GroupList() {
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.group_list);
	mContact = getIntent().getParcelableExtra("contact");
	mJID = mContact.getJID();
	final ListView listView = getListView();

	listView.setItemsCanFocus(false);
	listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	listView.setOnItemClickListener(new GroupOnItemClickListener());

	mText = (TextView) findViewById(R.id.GroupListText);
	mText.setOnKeyListener(new GroupListOnKeyListener());
	this.registerReceiver(mReceiver, new IntentFilter(BeemBroadcastReceiver.BEEM_CONNECTION_CLOSED));
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
     * init activity list adapter.
     */
    private void setAdapter() {
	try {
	    for (String group : mRoster.getGroupsNames()) {
		mStrings.add(group);
	    }
	    mGroups = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, mStrings);
	    setListAdapter(mGroups);
	    mContact = mRoster.getContact(mJID);
	    for (String group : mContact.getGroups()) {
		getListView().setItemChecked(mGroups.getPosition(group), true);
	    }
	} catch (RemoteException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Event when group is added.
     */
    private class GroupListOnKeyListener implements OnKeyListener {

	/**
	 * Constructor.
	 */
	public GroupListOnKeyListener() {
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
	    boolean result = false;
	    if (event.getAction() == KeyEvent.ACTION_DOWN) {
		switch (keyCode) {
		    case KeyEvent.KEYCODE_DPAD_CENTER:
		    case KeyEvent.KEYCODE_ENTER:
			if (mText.getText().length() == 0)
			    return false;
			String groupname = mText.getText().toString();
			mGroups.add(groupname);
			mText.setText(null);
			result = true;
			break;
		    default:
			result = false;
		}
	    }
	    return result;
	}

    }

    /**
     * Event click on list group contact.
     */
    private class GroupOnItemClickListener implements OnItemClickListener {

	/**
	 * Constructor.
	 */
	public GroupOnItemClickListener() {
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
	    CheckedTextView textView = (CheckedTextView) v;
	    if (!textView.isChecked()) {
		try {
		    mRoster.addContactToGroup(textView.getText().toString(), mJID);
		} catch (RemoteException e) {
		    e.printStackTrace();
		}
	    } else {
		try {
		    mRoster.removeContactFromGroup(textView.getText().toString(), mJID);
		} catch (RemoteException e) {
		    e.printStackTrace();
		}
	    }

	}

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
	    try {
		mRoster = mXmppFacade.getRoster();
		setAdapter();
	    } catch (RemoteException e) {
		e.printStackTrace();
	    }
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
	    mXmppFacade = null;
	    mRoster = null;
	}
    }

}
