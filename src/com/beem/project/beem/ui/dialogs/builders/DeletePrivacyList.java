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
package com.beem.project.beem.ui.dialogs.builders;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.RemoteException;
import android.util.Log;

import com.beem.project.beem.R;
import com.beem.project.beem.service.aidl.IPrivacyListManager;

/**
 * Use this builder to build a dialog which allows you to delete a privacy list.
 * @author Jean-Manuel Da Silva <dasilvj at beem-project dot com>
 */
public class DeletePrivacyList extends AlertDialog.Builder {

    private static final String TAG = "Dialogs.Builders > DeletePrivacyList";

    private final IPrivacyListManager mPrivacyListManager;
    private final String mPrivacyListName;

    /**
     * Constructor.
     * @param context context activity.
     * @param privacyListManager the privacy list manager managing the privacy list you want to delete.
     * @param privacyListName the name of the privacy list you want to delete.
     */
    public DeletePrivacyList(final Context context, final IPrivacyListManager privacyListManager,
	final String privacyListName) {
	super(context);

	mPrivacyListManager = privacyListManager;
	mPrivacyListName = privacyListName;

	setMessage(context.getString(R.string.privacy_list_delete_dialog_msg, privacyListName));
	DialogClickListener dl = new DialogClickListener();
	setPositiveButton(R.string.privacy_list_delete_dialog_yes, dl);
	setNegativeButton(R.string.privacy_list_delete_dialog_no, dl);
    }

    /**
     * Event click listener.
     */
    private class DialogClickListener implements DialogInterface.OnClickListener {

	/**
	 * Constructor.
	 */
	public DialogClickListener() {
	}

	@Override
	public void onClick(final DialogInterface dialog, final int which) {
	    if (which == DialogInterface.BUTTON_POSITIVE) {
		try {
		    mPrivacyListManager.removePrivacyList(mPrivacyListName);
		} catch (RemoteException e) {
		    Log.e(TAG, e.getMessage());
		}
	    }
	}
    }
}
