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
package com.beem.project.beem.utils;

import org.jivesoftware.smack.packet.Presence;

/**
 * Utility class to deal with Presence type.
 * @author nikita
 */
public final class PresenceType {

    /** The user is available to receive messages (default). */
    public static final int AVAILABLE = 100;

    /** The user is unavailable to receive messages. */
    public static final int UNAVAILABLE = 200;

    /** Request subscription to recipient's presence. */

    public static final int SUBSCRIBE = 300;

    /** Grant subscription to sender's presence. */
    public static final int SUBSCRIBED = 400;

    /** Request removal of subscription to sender's presence. */
    public static final int UNSUBSCRIBE = 500;

    /** Grant removal of subscription to sender's presence. */
    public static final int UNSUBSCRIBED = 600;

    /** The presence packet contains an error message. */
    public static final int ERROR = 701;

    /**
     * Private default constructor.
     */
    private PresenceType() {
    }

    /**
     * Get the presence type from a presence packet.
     * @param presence the presence type
     * @return an int representing the presence type
     */
    public static int getPresenceType(final Presence presence) {
	int res = PresenceType.ERROR;
	switch (presence.getType()) {
	    case available:
		res = PresenceType.AVAILABLE;
		break;
	    case unavailable:
		res = PresenceType.UNAVAILABLE;
		break;
	    case subscribe:
		res = PresenceType.SUBSCRIBE;
		break;
	    case subscribed:
		res = PresenceType.SUBSCRIBED;
		break;
	    case unsubscribe:
		res = PresenceType.UNSUBSCRIBE;
		break;
	    case unsubscribed:
		res = PresenceType.UNSUBSCRIBED;
		break;
	    case error:
	    default:
		res = PresenceType.ERROR;
	}
	return res;
    }

    /**
     * Get the smack presence mode for a status.
     * @param type the status type in beem
     * @return the presence mode to use in presence packet or null if there is no mode to use
     */
    public static Presence.Type getPresenceTypeFrom(final int type) {
	Presence.Type res;
	switch (type) {
	    case AVAILABLE:
		res = Presence.Type.available;
		break;
	    case UNAVAILABLE:
		res = Presence.Type.unavailable;
		break;
	    case SUBSCRIBE:
		res = Presence.Type.subscribe;
		break;
	    case SUBSCRIBED:
		res = Presence.Type.subscribed;
		break;
	    case UNSUBSCRIBE:
		res = Presence.Type.unsubscribe;
		break;
	    case UNSUBSCRIBED:
		res = Presence.Type.unsubscribed;
		break;
	    case ERROR:
		res = Presence.Type.error;
		break;
	    default:
		return null;
	}
	return res;
    }
}
