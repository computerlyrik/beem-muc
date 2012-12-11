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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.beem.project.beem.R;
import com.beem.project.beem.service.Contact;
import com.beem.project.beem.service.aidl.IRoster;

/**
 * Create dialog alias.
 */
public class Alias extends AlertDialog.Builder {

    private static final String TAG = "Dialogs.Builders > Alias";

    private IRoster mRoster;
    private Contact mContact;
    private EditText mEditTextAlias;

    /**
     * Constructor.
     * @param context context activity.
     * @param roster Beem roster.
     * @param contact the contact to modify.
     */
    public Alias(final Context context, final IRoster roster, final Contact contact) {
	super(context);

	mRoster = roster;
	mContact = contact;

	LayoutInflater factory = LayoutInflater.from(context);
	final View textEntryView = factory.inflate(
	    R.layout.contactdialogaliasdialog, null);
	setTitle(mContact.getJID());
	setView(textEntryView);
	mEditTextAlias = (EditText) textEntryView.findViewById(
	    R.id.CDAliasDialogName);
	mEditTextAlias.setText(mContact.getName());
	setPositiveButton(R.string.OkButton, new DialogClickListener());
	setNegativeButton(R.string.CancelButton, new DialogClickListener());
    }

    /**
     * Event click listener.
     */
    class DialogClickListener implements DialogInterface.OnClickListener {

	/**
	 * Constructor.
	 */
	public DialogClickListener() {
	}


	@Override
	public void onClick(final DialogInterface dialog, final int which) {
	    if (which == DialogInterface.BUTTON_POSITIVE) {
		String name = mEditTextAlias.getText().toString();
		if (name.length() == 0) {
		    name = mContact.getJID();
		}
		try {
		    mRoster.setContactName(mContact.getJID(), name);
		} catch (RemoteException e) {
		    Log.e(TAG, e.getMessage());
		}
	    }
	}
    }
}
