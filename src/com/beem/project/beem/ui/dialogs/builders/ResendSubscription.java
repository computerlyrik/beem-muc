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

import org.jivesoftware.smack.packet.Presence;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.beem.project.beem.R;
import com.beem.project.beem.service.Contact;
import com.beem.project.beem.service.PresenceAdapter;
import com.beem.project.beem.service.aidl.IXmppFacade;

/**
 * Use this builder to build a dialog which allows you resend a subscription query to a contact.
 * @author Jean-Manuel Da Silva <dasilvj at beem-project dot com>
 */
public class ResendSubscription extends AlertDialog.Builder {

    private static final String TAG = "Dialogs.Builders > ResendSubscription";

    private Context mContext;
    private IXmppFacade mXmppFacade;
    private Contact mContact;

    /**
     * Constructor.
     * @param context context activity.
     * @param xmppFacade the XMPP Facade used to send the query.
     * @param contact the receiver of the query.
     */
    public ResendSubscription(final Context context, final IXmppFacade xmppFacade, final Contact contact) {
	super(context);

	mContext = context;
	mXmppFacade = xmppFacade;
	mContact = contact;

	setMessage(R.string.userinfo_sureresend);
	DialogClickListener dl = new DialogClickListener();
	setPositiveButton(R.string.userinfo_yes, dl);
	setNegativeButton(R.string.userinfo_no, dl);
    }

    /**
     * Event click listener.
     */
    class DialogClickListener implements DialogInterface.OnClickListener {

	/**
	 * Constructor.
	 */
	DialogClickListener() {
	}

	@Override
	public void onClick(final DialogInterface dialog, final int which) {
	    if (which == DialogInterface.BUTTON_POSITIVE) {
		Presence presencePacket = new Presence(Presence.Type.subscribe);
		presencePacket.setTo(mContact.getJID());
		try {
		    mXmppFacade.sendPresencePacket(new PresenceAdapter(presencePacket));
		    Toast.makeText(mContext, mContext.getString(R.string.userinfo_resend), Toast.LENGTH_SHORT).show();
		} catch (RemoteException e) {
		    Log.e(TAG, e.getMessage());
		}
	    }
	}

    }
}
