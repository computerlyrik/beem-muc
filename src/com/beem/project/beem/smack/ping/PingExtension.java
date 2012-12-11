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

*/

package com.beem.project.beem.smack.ping;

import org.jivesoftware.smack.packet.IQ;

/**
 * This extension represents a  iq ping.
 *
 */
public class PingExtension extends IQ {

    /**  Namespace of the Ping XEP. */
    public static final String NAMESPACE = "urn:xmpp:ping";

    /** Xml element name for the ping. */
    public static final String ELEMENT = "ping";


    /**
     * Create a ping iq packet.
     */
    public PingExtension() {
    }

    @Override
    public String getChildElementXML() {
	if (getType() == IQ.Type.RESULT)
	    return null;
        return "<" + ELEMENT + " xmlns=\"" + NAMESPACE + "\" />";
    }

}
