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
package com.beem.project.beem.ui.wizard;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.LoginFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import org.jivesoftware.smack.util.StringUtils;

import com.beem.project.beem.ui.Login;
import com.beem.project.beem.ui.Settings;
import com.beem.project.beem.BeemApplication;
import com.beem.project.beem.R;

/**
 * Activity to enter the information required in order to configure a XMPP account.
 *
 * @author Da Risk <darisk972@gmail.com>
 */
public class AccountConfigure extends Activity implements OnClickListener {

    private static final int MANUAL_CONFIGURATION = 1;
    private Button mNextButton;
    private Button mManualConfigButton;
    private EditText mAccountJID;
    private EditText mAccountPassword;
    private final JidTextWatcher mJidTextWatcher = new JidTextWatcher();
    private final PasswordTextWatcher mPasswordTextWatcher = new PasswordTextWatcher();
    private boolean mValidJid;
    private boolean mValidPassword;

    /**
     * Constructor.
     */
    public AccountConfigure() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.wizard_account_configure);
	mManualConfigButton = (Button) findViewById(R.id.manual_setup);
	mManualConfigButton.setOnClickListener(this);
	mNextButton = (Button) findViewById(R.id.next);
	mNextButton.setOnClickListener(this);
	mAccountJID = (EditText) findViewById(R.id.account_username);
	mAccountPassword = (EditText) findViewById(R.id.account_password);


	InputFilter[] orgFilters = mAccountJID.getFilters();
	InputFilter[] newFilters = new InputFilter[orgFilters.length + 1];
	int i;
	for (i = 0; i < orgFilters.length; i++)
	    newFilters[i] = orgFilters[i];
	newFilters[i] = new LoginFilter.UsernameFilterGeneric();
	mAccountJID.setFilters(newFilters);
	mAccountJID.addTextChangedListener(mJidTextWatcher);
	mAccountPassword.addTextChangedListener(mPasswordTextWatcher);
    }

    @Override
    public void onClick(View v) {
	Intent i = null;
	if (v == mNextButton) {
	    configureAccount();
	    i = new Intent(this, Login.class);
	    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    startActivity(i);
	    finish();
	} else if (v == mManualConfigButton) {
	    i = new Intent(this, Settings.class);
	    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    startActivityForResult(i, MANUAL_CONFIGURATION);
	}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	if (requestCode == MANUAL_CONFIGURATION) {
	    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
	    String login = settings.getString(BeemApplication.ACCOUNT_USERNAME_KEY, "");
	    String password = settings.getString(BeemApplication.ACCOUNT_PASSWORD_KEY, "");
	    mAccountJID.setText(login);
	    mAccountPassword.setText(password);
	    checkUsername(login);
	    checkPassword(password);
	    mNextButton.setEnabled(mValidJid && mValidPassword);
	}
    }

    /**
     * Store the account in the settings.
     */
    private void configureAccount() {
	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
	SharedPreferences.Editor edit = settings.edit();
	edit.putString(BeemApplication.ACCOUNT_USERNAME_KEY, mAccountJID.getText().toString());
	edit.putString(BeemApplication.ACCOUNT_PASSWORD_KEY, mAccountPassword.getText().toString());
	edit.commit();
    }

    /**
     * Check that the username is really a JID.
     * @param username the username to check.
     */
    private void checkUsername(String username) {
	String name = StringUtils.parseName(username);
	String server = StringUtils.parseServer(username);
	if (TextUtils.isEmpty(name) || TextUtils.isEmpty(server)) {
	    mValidJid = false;
	} else {
	    mValidJid = true;
	}
    }

    /**
     * Check password.
     * @param password the password to check.
     */
    private void checkPassword(String password) {
	if (password.length() > 0)
	    mValidPassword = true;
	else
	    mValidPassword = false;
    }

    /**
     * Text watcher to test the existence of a password.
     */
    private class PasswordTextWatcher implements TextWatcher {

	/**
	 * Constructor.
	 */
	public PasswordTextWatcher() {
	}

	@Override
	public void afterTextChanged(Editable s) {
	    checkPassword(s.toString());
	    mNextButton.setEnabled(mValidJid && mValidPassword);
	}

	@Override
	public void beforeTextChanged(CharSequence  s, int start, int count, int after) {
	}

	@Override
	public void onTextChanged(CharSequence  s, int start, int before, int count) {
	}
    }

    /**
     * TextWatcher to check the validity of a JID.
     */
    private class JidTextWatcher implements TextWatcher {

	/**
	 * Constructor.
	 */
	public JidTextWatcher() {
	}

	@Override
	public void afterTextChanged(Editable s) {
	    checkUsername(s.toString());
	    mNextButton.setEnabled(mValidJid && mValidPassword);
	}

	@Override
	public void beforeTextChanged(CharSequence  s, int start, int count, int after) {
	}

	@Override
	public void onTextChanged(CharSequence  s, int start, int before, int count) {
	}
    }
}
