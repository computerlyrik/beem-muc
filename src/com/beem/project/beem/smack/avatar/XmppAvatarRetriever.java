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

import java.util.List;
import java.util.Arrays;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.PayloadItem;

import com.beem.project.beem.smack.pep.PepSubManager;

/**
 * An AvatarRetriever which retrieve the avatar over the XMPP connection.
 */
public class XmppAvatarRetriever implements AvatarRetriever {

    private static String AVATARDATANODE = "urn:xmpp:avatar:data";
    private PepSubManager mPubsub;
    private String mFrom;
    private String mId;

    /**
     * Create an XmppAvatarRetriever.
     *
     * @param con the xmpp connection
     * @param from the contact from which we retrieve the avatar
     * @param id the id of the avatar to retrieve
     */
    public XmppAvatarRetriever(final Connection con, final String from, final String id) {
	mPubsub = new PepSubManager(con, from);
	mFrom = from;
	mId = id;
    }

    @Override
    public byte[] getAvatar() {
	try {
	    LeafNode node = mPubsub.getPEPNode(AVATARDATANODE);
	    List<Item> items = node.getItems(Arrays.asList(mId));
	    PayloadItem<AvatarExtension> item = (PayloadItem<AvatarExtension>) items.get(0);
	    AvatarExtension avatar = item.getPayload();
	    return avatar.getData();
	} catch (XMPPException e) {
	    return null;
	}
    }

}
