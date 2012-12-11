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

import com.beem.project.beem.R;
import com.beem.project.beem.service.aidl.IChat;

/**
 * Use this builder to build a dialog which allows you to display otr fingerprints.
 * @author nikita
 */
public class DisplayOtrFingerprint extends AlertDialog.Builder {

    private static final String TAG = "DisplayOtrFingerprint";
    private IChat mChat;

    /**
     * Constructor.
     * @param context context activity.
     * @param chat the current chat.
     */
    public DisplayOtrFingerprint(final Context context, final IChat chat) {
	super(context);

	mChat = chat;
	try {
	    setMessage(context.getString(R.string.chat_otr_verify_key, chat.getLocalOtrFingerprint(),
		chat.getRemoteOtrFingerprint()));
	} catch (RemoteException e) {
	    e.printStackTrace();
	}
	DialogClickListener dl = new DialogClickListener();
	setPositiveButton(R.string.userinfo_yes, dl);
	setNegativeButton(R.string.userinfo_no, dl);
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
		    mChat.verifyRemoteFingerprint(true);
		} catch (RemoteException e) {
		    e.printStackTrace();
		}
	    } else if (which == DialogInterface.BUTTON_NEGATIVE) {
		try {
		    mChat.verifyRemoteFingerprint(false);
		} catch (RemoteException e) {
		    e.printStackTrace();
		}
	    }
	}
    }
}
