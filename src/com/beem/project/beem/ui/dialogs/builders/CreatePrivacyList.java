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

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.beem.project.beem.R;
import com.beem.project.beem.service.PrivacyListItem;
import com.beem.project.beem.service.aidl.IPrivacyListManager;

/**
 * Use this builder to build a dialog which handles a privacy list creation.
 * @author Jean-Manuel Da Silva <dasilvj at beem-project dot com>
 */
public class CreatePrivacyList extends AlertDialog.Builder {

    private static final String TAG = "Dialogs.Builders > CreatePrivacyList";

    private final IPrivacyListManager mPrivacyListManager;
    private final View mTextEntryView;
    private EditText mListNameField;

    /**
     * Constructor.
     * @param context context activity.
     * @param privacyListManager the privacy list manager that will be use to create our list.
     */
    public CreatePrivacyList(final Context context, final IPrivacyListManager privacyListManager) {
	super(context);

	LayoutInflater factory = LayoutInflater.from(context);

	mTextEntryView = factory.inflate(R.layout.privacy_list_create_dialog, null);
	setView(mTextEntryView);

	mPrivacyListManager = privacyListManager;
	mListNameField = (EditText) mTextEntryView.findViewById(R.id.privacy_list_create_dialog_list_name);

	setTitle(R.string.privacy_list_create_dialog_title);
	setPositiveButton(R.string.privacy_list_create_dialog_create_button, new DialogClickListener());
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
		try {
		    Log.d(TAG, "mPrivacyListManager ## " + mPrivacyListManager);
		    Log.d(TAG, "listNameField ## " + mListNameField);
		    Log.d(TAG, "listNameField.getText().toString() ## " + mListNameField.getText().toString());
		    mPrivacyListManager.createPrivacyList(mListNameField.getText().toString(),
			new ArrayList<PrivacyListItem>());
		} catch (RemoteException e) {
		    Log.e(TAG, e.getMessage());
		}
	    }
	}
    }
}
