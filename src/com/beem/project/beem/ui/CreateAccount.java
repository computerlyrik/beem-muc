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

import java.util.regex.Pattern;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.proxy.ProxyInfo;
import org.jivesoftware.smack.util.StringUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.beem.project.beem.BeemApplication;
import com.beem.project.beem.R;

/**
 * This class represents an activity which allows the user to create an account on the XMPP server saved in settings.
 * @author Jean-Manuel Da Silva <dasilvj at beem-project dot com>
 */
public class CreateAccount extends Activity {

    private static final boolean DEFAULT_BOOLEAN_VALUE = false;
    private static final String DEFAULT_STRING_VALUE = "";
    private static final int DEFAULT_XMPP_PORT = 5222;

    private static final int NOTIFICATION_DURATION = Toast.LENGTH_SHORT;

    private SharedPreferences mSettings;
    private Button mCreateAccountButton;

    /**
     * Constructor.
     */
    public CreateAccount() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.create_account);
	initCreateAccountButton();
	mSettings = PreferenceManager.getDefaultSharedPreferences(this);
    }

    /**
     * Create an account on the XMPP server specified in settings.
     * @param username the username of the account.
     * @param password the password of the account.
     * @return true if the account was created successfully.
     */
    private boolean createAccount(String username, String password) {
	XMPPConnection xmppConnection = null;
	ConnectionConfiguration connectionConfiguration = null;
	ProxyInfo pi = getRegisteredProxy();
	if (pi != null) {
	    connectionConfiguration = new ConnectionConfiguration(getXMPPServer(), getXMPPPort(), pi);
	} else {
	    connectionConfiguration = new ConnectionConfiguration(getXMPPServer(), getXMPPPort());
	}
	if (getRegisteredXMPPTLSUse())
	    connectionConfiguration.setSecurityMode(ConnectionConfiguration.SecurityMode.required);

	xmppConnection = new XMPPConnection(connectionConfiguration);
	try {
	    xmppConnection.connect();
	    AccountManager accountManager = new AccountManager(xmppConnection);
	    accountManager.createAccount(username, password);
	    Toast toast = Toast.makeText(getApplicationContext(), String.format(
		getString(R.string.create_account_successfull_after), username), NOTIFICATION_DURATION);
	    toast.show();
	} catch (XMPPException e) {
	    createErrorDialog(e.getMessage());
	    return false;
	}
	xmppConnection.disconnect();
	return true;
    }

    /**
     * Create a dialog containing an error message.
     * @param errMsg the error message
     */
    private void createErrorDialog(String errMsg) {
	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	builder.setTitle(R.string.create_account_err_dialog_title)
	    .setMessage(errMsg)
	    .setCancelable(false)
	    .setIcon(android.R.drawable.ic_dialog_alert);
	builder.setNeutralButton(R.string.create_account_close_dialog_button, new DialogInterface.OnClickListener() {

	    @Override
	    public void onClick(DialogInterface dialog, int which) {
		dialog.cancel();
	    }
	});
	AlertDialog settingsErrDialog = builder.create();
	settingsErrDialog.show();
    }

    /**
     * Retrive proxy informations from the preferences.
     * @return Registered proxy informations
     */
    private ProxyInfo getRegisteredProxy() {
	if (getRegisteredProxyUse()) {
	    ProxyInfo proxyInfo = new ProxyInfo(getRegisteredProxyType(), getRegisteredProxyServer(),
		getRegisteredProxyPort(), getRegisteredProxyUsername(), getRegisteredProxyPassword());
	    return proxyInfo;
	}
	return null;
    }

    /**
     * Retrieve proxy password from the preferences.
     * @return Registered proxy password
     */
    private String getRegisteredProxyPassword() {
	return mSettings.getString(BeemApplication.PROXY_PASSWORD_KEY, DEFAULT_STRING_VALUE);
    }

    /**
     * Retrieve proxy port from the preferences.
     * @return Registered proxy port
     */
    private int getRegisteredProxyPort() {
	return Integer.parseInt(mSettings.getString(BeemApplication.PROXY_PORT_KEY, DEFAULT_STRING_VALUE));
    }

    /**
     * Retrieve proxy server from the preferences.
     * @return Registered proxy server
     */
    private String getRegisteredProxyServer() {
	return mSettings.getString(BeemApplication.PROXY_SERVER_KEY, DEFAULT_STRING_VALUE);
    }

    /**
     * Retrieve proxy type from the preferences.
     * @return Registered proxy type
     */
    private ProxyInfo.ProxyType getRegisteredProxyType() {
	ProxyInfo.ProxyType result = ProxyInfo.ProxyType.NONE;
	if (mSettings.getBoolean(BeemApplication.PROXY_USE_KEY, false)) {
	    String type = mSettings.getString(BeemApplication.PROXY_TYPE_KEY, "none");
	    if ("HTTP".equals(type))
		result = ProxyInfo.ProxyType.HTTP;
	    else if ("SOCKS4".equals(type))
		result = ProxyInfo.ProxyType.SOCKS4;
	    else if ("SOCKS5".equals(type))
		result = ProxyInfo.ProxyType.SOCKS5;
	    else
		result = ProxyInfo.ProxyType.NONE;
	}
	return result;
    }

    /**
     * Retrieve proxy use from the preferences.
     * @return Registered proxy use
     */
    private boolean getRegisteredProxyUse() {
	return mSettings.getBoolean(BeemApplication.PROXY_USE_KEY, DEFAULT_BOOLEAN_VALUE);
    }

    /**
     * Retrieve proxy username from the preferences.
     * @return Registered proxy username
     */
    private String getRegisteredProxyUsername() {
	return mSettings.getString(BeemApplication.PROXY_USERNAME_KEY, DEFAULT_STRING_VALUE);
    }

    /**
     * Retrieve xmpp port from the preferences.
     * @return Registered xmpp port
     */
    private int getXMPPPort() {
	int port = DEFAULT_XMPP_PORT;
	if (mSettings.getBoolean("settings_key_specific_server", false))
	    port = Integer.parseInt(mSettings.getString("settings_key_xmpp_port", "5222"));
	return port;
    }

    /**
     * Retrieve xmpp server from the preferences.
     * @return Registered xmpp server
     */
    private String getXMPPServer() {
	TextView xmppServerTextView = (TextView) findViewById(R.id.create_account_username);
	String xmppServer = "";
	if (mSettings.getBoolean("settings_key_specific_server", false))
	    xmppServer = mSettings.getString("settings_key_xmpp_server", "");
	else
	    xmppServer = StringUtils.parseServer(xmppServerTextView.getText().toString());
	return xmppServer;
    }

    /**
     * Retrieve TLS use from the preferences.
     * @return Registered TLS use
     */
    private boolean getRegisteredXMPPTLSUse() {
	return mSettings.getBoolean("settings_key_xmpp_tls_use", DEFAULT_BOOLEAN_VALUE);
    }

    /**
     * Check if the fields password and confirm password match.
     * @return return true if password & confirm password fields match, else false
     */
    private boolean checkPasswords() {
	final String passwordFieldValue = ((EditText) findViewById(R.id.create_account_password)).getText().toString();
	final String passwordConfirmFielddValue = ((EditText) findViewById(R.id.create_account_confirm_password))
	    .getText().toString();

	return passwordFieldValue.equals(passwordConfirmFielddValue) && !"".equals(passwordConfirmFielddValue);
    }

    /**
     * Check the format of the email.
     * @return true if the email is valid.
     */
    private boolean checkEmail() {
	String email = ((TextView) findViewById(R.id.create_account_username)).getText().toString();
	return Pattern.matches("[a-zA-Z0-9._%+-]+@(?:[a-zA-Z0-9-]+.)+[a-zA-Z]{2,4}", email);
    }

    /**
     * Initialize the "Create this account" button which allows the user to create an account.
     */
    private void initCreateAccountButton() {
	mCreateAccountButton = (Button) findViewById(R.id.create_account_button);
	mCreateAccountButton.setOnClickListener(new View.OnClickListener() {
	    public void onClick(View v) {
		String usernameFieldValue = ((EditText) findViewById(R.id.create_account_username)).getText()
		    .toString();
		String passwordFieldValue = ((EditText) findViewById(R.id.create_account_password)).getText()
		    .toString();
		String username = StringUtils.parseName(usernameFieldValue);
		if (!checkEmail())
		    createErrorDialog(getString(R.string.create_account_err_username));
		else if (!checkPasswords())
		    createErrorDialog(getString(R.string.create_account_err_passwords));
		else {
		    if (createAccount(username, passwordFieldValue))
			finish();
		}

	    }
	});
	Button createAccountLoginButton = (Button) findViewById(R.id.create_account_login_button);
	createAccountLoginButton.setOnClickListener(new View.OnClickListener() {
	    public void onClick(View v) {
		String usernameFieldValue = ((EditText) findViewById(R.id.create_account_username)).getText()
		    .toString();
		String username = StringUtils.parseName(usernameFieldValue);
		String passwordFieldValue = ((EditText) findViewById(R.id.create_account_password)).getText()
		    .toString();
		if (!checkEmail())
		    createErrorDialog(getString(R.string.create_account_err_username));
		else if (!checkPasswords())
		    createErrorDialog(getString(R.string.create_account_err_passwords));
		else {
		    if (createAccount(username, passwordFieldValue)) {
			SharedPreferences.Editor settingsEditor = mSettings.edit();
			settingsEditor.putString(BeemApplication.ACCOUNT_USERNAME_KEY, usernameFieldValue);
			settingsEditor.putString(BeemApplication.ACCOUNT_PASSWORD_KEY, passwordFieldValue);
			settingsEditor.putBoolean("settings_key_gmail", false);
			settingsEditor.commit();
			finish();
		    }
		}
	    }
	});
    }
}
