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
package com.beem.project.beem.smack.caps;

import org.xmlpull.v1.XmlPullParser;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.jivesoftware.smack.packet.PacketExtension;

/**
 * PacketExtensionProvider for XEP-0115.
 * This provider parse c element of namespace
 * http://jabber.org/protocol/caps which represents a capability of XEP-0115
 *
 */
public class CapsProvider implements PacketExtensionProvider {

    /**
     * Constructor.
     */
    public CapsProvider() { }

    @Override
    public PacketExtension parseExtension(XmlPullParser parser) {
	String ver = parser.getAttributeValue("", "ver");
	String hash = parser.getAttributeValue("", "hash");
	String node = parser.getAttributeValue("", "node");
	String ext = parser.getAttributeValue("", "ext");
	CapsExtension e = new CapsExtension(hash, node, ver);
	e.setExt(ext);
	return e;
    }

}
