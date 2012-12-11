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

package com.beem.project.beem;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * This class contains informations that needs to be global in the application.
 * Theses informations must be necessary for the activities and the service.
 * @author Da Risk <darisk972@gmail.com>
 */
public class BeemApplication extends Application {

    /* Constants for PREFERENCE_KEY
     * The format of the Preference key is :
     * $name_KEY = "$name"
     */
    /** Preference key for account username. */
    public static final String ACCOUNT_USERNAME_KEY = "account_username";
    /** Preference key for account password. */
    public static final String ACCOUNT_PASSWORD_KEY = "account_password";
    /** Preference key for status (available, busy, away, ...). */
    public static final String STATUS_KEY = "status";
    /** Preference key for status message. */
    public static final String STATUS_TEXT_KEY = "status_text";
    /** Preference key for connection resource . */
    public static final String CONNECTION_RESOURCE_KEY = "connection_resource";
    /** Preference key for connection priority. */
    public static final String CONNECTION_PRIORITY_KEY = "connection_priority";
    /** Preference key for the use of a proxy. */
    public static final String PROXY_USE_KEY = "proxy_use";
    /** Preference key for the type of proxy. */
    public static final String PROXY_TYPE_KEY = "proxy_type";
    /** Preference key for the proxy server. */
    public static final String PROXY_SERVER_KEY = "proxy_server";
    /** Preference key for the proxy port. */
    public static final String PROXY_PORT_KEY = "proxy_port";
    /** Preference key for the proxy username. */
    public static final String PROXY_USERNAME_KEY = "proxy_username";
    /** Preference key for the proxy password. */
    public static final String PROXY_PASSWORD_KEY = "proxy_password";
    /** Preference key for vibrate on notification. */
    public static final String NOTIFICATION_VIBRATE_KEY = "notification_vibrate";
    /** Preference key for notification sound. */
    public static final String NOTIFICATION_SOUND_KEY = "notification_sound";
    /** Preference key for smack debugging. */
    public static final String SMACK_DEBUG_KEY = "smack_debug";
    /** Preference key for full Jid for login. */
    public static final String FULL_JID_LOGIN_KEY = "full_jid_login";
    /** Preference key for display offline contact. */
    public static final String SHOW_OFFLINE_CONTACTS_KEY = "show_offline_contacts";
    /** Preference key for hide the groups. */
    public static final String HIDE_GROUPS_KEY = "hide_groups";
    /** Preference key for auto away enable. */
    public static final String USE_AUTO_AWAY_KEY = "use_auto_away";
    /** Preference key for auto away message. */
    public static final String AUTO_AWAY_MSG_KEY = "auto_away_msg";
    /** Preference key for compact chat ui. */
    public static final String USE_COMPACT_CHAT_UI_KEY = "use_compact_chat_ui";
    /** Preference key for history path on the SDCard. */
    public static final String CHAT_HISTORY_KEY = "settings_chat_history_path";

    //TODO add the other one

    private boolean mIsConnected;
    private boolean mIsAccountConfigured;
    private boolean mPepEnabled;
    private SharedPreferences mSettings;
    private final PreferenceListener mPreferenceListener = new PreferenceListener();

    /**
     * Constructor.
     */
    public BeemApplication() {
    }

    @Override
    public void onCreate() {
	super.onCreate();
	mSettings = PreferenceManager.getDefaultSharedPreferences(this);
	String login = mSettings.getString(BeemApplication.ACCOUNT_USERNAME_KEY, "");
	String password = mSettings.getString(BeemApplication.ACCOUNT_PASSWORD_KEY, "");
	mIsAccountConfigured = !("".equals(login) || "".equals(password));
	mSettings.registerOnSharedPreferenceChangeListener(mPreferenceListener);
    }

    @Override
    public void onTerminate() {
	super.onTerminate();
	mSettings.unregisterOnSharedPreferenceChangeListener(mPreferenceListener);
    }

    /**
     * Tell if Beem is connected to a XMPP server.
     * @return false if not connected.
     */
    public boolean isConnected() {
	return mIsConnected;
    }

    /**
     * Set the status of the connection to a XMPP server of BEEM.
     * @param isConnected set for the state of the connection.
     */
    public void setConnected(boolean isConnected) {
	mIsConnected = isConnected;
    }

    /**
     * Tell if a XMPP account is configured.
     * @return false if there is no account configured.
     */
    public boolean isAccountConfigured() {
	return mIsAccountConfigured;
    }

    /**
     * Enable Pep in the application context.
     *
     * @param enabled true to enable pep
     */
    public void setPepEnabled(boolean enabled) {
	mPepEnabled = enabled;
    }

    /**
     * Check if Pep is enabled.
     *
     * @return true if enabled
     */
    public boolean isPepEnabled() {
	return mPepEnabled;
    }

    /**
     * A listener for all the change in the preference file. It is used to maintain the global state of the application.
     */
    private class PreferenceListener implements SharedPreferences.OnSharedPreferenceChangeListener {

	/**
	 * Constructor.
	 */
	public PreferenceListener() {
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences  sharedPreferences, String key) {
	    if (BeemApplication.ACCOUNT_USERNAME_KEY.equals(key) || BeemApplication.ACCOUNT_PASSWORD_KEY.equals(key)) {
		String login = mSettings.getString(BeemApplication.ACCOUNT_USERNAME_KEY, "");
		String password = mSettings.getString(BeemApplication.ACCOUNT_PASSWORD_KEY, "");
		mIsAccountConfigured = !("".equals(login) || "".equals(password));
	    }
	}
    }
}
