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

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.beem.project.beem.R;
import com.beem.project.beem.service.Contact;

/**
 * Create the change chat dialog.
 */
public class ChatList extends AlertDialog.Builder {

    //private static final String TAG = "Dialogs.Builders > Chat list";

    /**
     * Constructor.
     * @param context context activity.
     * @param openedChats A list containing the JID of participants of the opened chats.
     */
    public ChatList(final Context context, final List<Contact> openedChats) {
	super(context);

	if (openedChats.size() > 0) {
	    CharSequence[] items = new CharSequence[openedChats.size()];

	    int i = 0;
	    for (Contact c : openedChats) {
		items[i++] = c.getName();
	    }
	    setTitle(R.string.chat_dialog_change_chat_title);
	    setItems(items, new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int item) {
		    Intent chatIntent = new Intent(context, com.beem.project.beem.ui.Chat.class);
		    chatIntent.setData((openedChats.get(item)).toUri());
		    context.startActivity(chatIntent);
		}
	    });
	} else {
	    setMessage(R.string.chat_no_more_chats);
	}
    }
}
