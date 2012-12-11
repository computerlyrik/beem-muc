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
package com.beem.project.beem.service.aidl;

import com.beem.project.beem.service.aidl.IXmppConnection;
import com.beem.project.beem.service.aidl.IRoster;
import com.beem.project.beem.service.aidl.IChatManager;
import com.beem.project.beem.service.aidl.IPrivacyListManager;
import com.beem.project.beem.service.PresenceAdapter;
import com.beem.project.beem.service.UserInfo;

import android.net.Uri;

interface IXmppFacade {

    /**
     * Get the XmppConnection of the facade.
     */
    IXmppConnection createConnection();

    /**
     * Get the roster of the user
     */
    IRoster getRoster();

    /**
     * Connect and login synchronously on the server.
     */
    void connectSync();

    /**
     * Connect and login asynchronously on the server.
     */
    void connectAsync();

    /**
     * Disconnect from the server
     */
    void disconnect();

    /**
     * Get the chat manager.
     */
    IChatManager getChatManager();

    /**
     * Change the status of the user.
     * @param status the status to set
     * @param msg the message state to set
     */
    void changeStatus(in int status, in String msg);

    void sendPresencePacket(in PresenceAdapter presence);

    /**
     * make a jingle audio call
     * @param jid the receiver id
     */
     void call(in String jid);

    boolean publishAvatar(in Uri avatarUri);

    void disableAvatarPublishing();

    /**
     * Get the user informations.
     * @return null if not connected
     */
    UserInfo getUserInfo();

     IPrivacyListManager getPrivacyListManager();
}
