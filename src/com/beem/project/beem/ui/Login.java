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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.beem.project.beem.BeemApplication;
import com.beem.project.beem.R;
import com.beem.project.beem.ui.wizard.Account;
import com.beem.project.beem.utils.BeemConnectivity;

/**
 * This class is the main Activity for the Beem project.
 * @author Da Risk <darisk@beem-project.com>
 */
public class Login extends Activity {

    private static final int LOGIN_REQUEST_CODE = 1;
    private TextView mTextView;
    private boolean mIsResult;
    private BeemApplication mBeemApplication;

    /**
     * Constructor.
     */
    public Login() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	Application app = getApplication();
	if (app instanceof BeemApplication) {
	    mBeemApplication = (BeemApplication) app;
	    if (mBeemApplication.isConnected()) {
		startActivity(new Intent(this, ContactList.class));
		finish();
	    } else if (!mBeemApplication.isAccountConfigured()) {
		startActivity(new Intent(this, Account.class));
		finish();
	    }
	}
	setContentView(R.layout.login);
	mTextView = (TextView) findViewById(R.id.log_as_msg);
    }

    @Override
    protected void onStart() {
	super.onStart();
	if (mBeemApplication.isAccountConfigured() && !mIsResult
	    && BeemConnectivity.isConnected(getApplicationContext())) {
	    mTextView.setText("");
	    Intent i = new Intent(this, LoginAnim.class);
	    startActivityForResult(i, LOGIN_REQUEST_CODE);
	    mIsResult = false;
	} else {
	    mTextView.setText(R.string.login_start_msg);
	}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	if (requestCode == LOGIN_REQUEST_CODE) {
	    mIsResult = true;
	    if (resultCode == Activity.RESULT_OK) {
		startActivity(new Intent(this, ContactList.class));
		finish();
	    } else if (resultCode == Activity.RESULT_CANCELED) {
		if (data != null) {
		    String tmp = data.getExtras().getString("message");
		    Toast.makeText(Login.this, tmp, Toast.LENGTH_SHORT).show();
		    mTextView.setText(tmp);
		}
	    }
	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	super.onCreateOptionsMenu(menu);
	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.login, menu);
	return true;
    }

    @Override
    public final boolean onOptionsItemSelected(MenuItem item) {
	boolean result;
	switch (item.getItemId()) {
	    case R.id.login_menu_settings:
		mTextView.setText("");
		startActivity(new Intent(Login.this, Settings.class));
		result = true;
		break;
	    case R.id.login_menu_about:
		createAboutDialog();
		result = true;
		break;
	    case R.id.login_menu_login:
		if (mBeemApplication.isAccountConfigured()) {
		    Intent i = new Intent(this, LoginAnim.class);
		    startActivityForResult(i, LOGIN_REQUEST_CODE);
		}
		result = true;
		break;
	    default:
		result = false;
		break;
	}
	return result;
    }

    /**
     * Create an about "BEEM" dialog.
     */
    private void createAboutDialog() {
	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	String versionname;
	try {
	    PackageManager pm = getPackageManager();
	    PackageInfo pi = pm.getPackageInfo("com.beem.project.beem", 0);
	    versionname = pi.versionName;
	} catch (PackageManager.NameNotFoundException e) {
	    versionname = "";
	}
	String title = getString(R.string.login_about_title, versionname);
	builder.setTitle(title).setMessage(R.string.login_about_msg).setCancelable(false);
	builder.setNeutralButton(R.string.login_about_button, new DialogInterface.OnClickListener() {

	    public void onClick(DialogInterface dialog, int whichButton) {
		dialog.cancel();
	    }
	});
	AlertDialog aboutDialog = builder.create();
	aboutDialog.show();
    }
}
