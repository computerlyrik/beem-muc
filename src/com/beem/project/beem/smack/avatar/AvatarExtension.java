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
package com.beem.project.beem.smack.avatar;

import org.jivesoftware.smack.util.Base64;
import org.jivesoftware.smack.packet.PacketExtension;

/**
 * PacketExtension to represent the Avatar data.
 * XML namespace urn:xmpp:avatar:data
 *
 */
public class AvatarExtension implements PacketExtension {

    private String mData;

    /**
     * Create an AvatarExtension.
     * @param base64 the data of the avatar as a base64 string
     */
    public AvatarExtension(final String base64) {
	mData = base64;
    }

    /**
     * Create an AvatarExtension.
     * @param data the data of the avatar
     */
    public AvatarExtension(final byte[] data) {
	mData = Base64.encodeBytes(data);
    }

    /**
     * Get the avatar data as a Base64 string.
     *
     * @return a base64 string.
     */
    public String getBase64() {
	return mData;
    }

    /**
     * Get the avatar data.
     *
     * @return the decoded data
     */
    public byte[] getData() {
	return Base64.decode(mData);
    }

    @Override
    public String getElementName() {
	return "data";
    }

    @Override
    public String getNamespace() {
	return "urn:xmpp:avatar:data";
    }

    @Override
    public String toXML() {
	StringBuilder builder = new StringBuilder("<data xmlns=\"");
	builder.append(getNamespace()).append("\">");
	builder.append(mData);
	builder.append("</data>");
	return builder.toString();
    }

}
