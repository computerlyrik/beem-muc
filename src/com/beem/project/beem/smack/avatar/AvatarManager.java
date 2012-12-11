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

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

import com.beem.project.beem.smack.avatar.AvatarMetadataExtension.Info;
import com.beem.project.beem.smack.pep.PEPListener;
import com.beem.project.beem.smack.pep.PepSubManager;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PayloadItem;

/**
 * This class deals with the avatar data.
 * It can be configured to auto retrieve the avatar and put it in cache.
 *
 */
public class AvatarManager {

    /**  The pubsub node for avatar data. */
    public static final String AVATARDATA_NODE = "urn:xmpp:avatar:data";
    /**  The pubsub node for avatar metadata. */
    public static final String AVATARMETADATA_NODE = "urn:xmpp:avatar:metadata";

    private PepSubManager mPep;
    private Connection mCon;
    private boolean mAutoDownload;
    private AvatarCache mCache;
    private final List<AvatarListener> mListeners = new LinkedList<AvatarListener>();

    /**
     * Create an AvatarManager.
     *
     * @param con the connection
     * @param pepMgr the PepSubManager of the Connection
     * @param cache the cache which will store the avatars
     * @param autoDownload true to enable auto download of avatars
     */
    public AvatarManager(final Connection con, final PepSubManager pepMgr,
	    final AvatarCache cache, final boolean autoDownload) {
	mCon = con;
	mPep = pepMgr;
	mAutoDownload = autoDownload;
	mCache = cache;
	mPep.addPEPListener(new Listener());
    }

    /**
     * Create an AvatarManager.
     *
     * @param con the connection
     * @param pepMgr the PepSubManager of the Connection
     * @param autoDownload true to enable auto download of avatars
     */
    protected AvatarManager(final Connection con, final PepSubManager pepMgr, final boolean autoDownload) {
	mCon = con;
	mPep = pepMgr;
	mAutoDownload = autoDownload;
	mPep.addPEPListener(new Listener());
	mCache = new MemoryAvatarCache(100, 1800000);
    }

    /**
     * Get an avatar from the cache.
     *
     * @param avatarId the id of the avatar
     * @return the avatar or null if it cannot be retrieved from the cache
     */
    public byte[] getAvatar(String avatarId) {
	if (avatarId == null)
	    return null;
	try {
	    return mCache.get(avatarId);
	} catch (IOException e) {
	    return null;
	}
    }

    /**
     * Add an AvatarListener.
     *
     * @param listener the AvatarListener to add
     */
    public void addAvatarListener(AvatarListener listener) {
	if (!mListeners.contains(listener))
	    mListeners.add(listener);
    }

    /**
     * Remove an AvatarListener.
     *
     * @param listener the AvatarListener to remove
     */
    public void removeAvatarListener(AvatarListener listener) {
	mListeners.remove(listener);
    }

    /**
     * Download an avatar.
     *
     * @param from The jid of the user
     * @param avatarId the id of the avatar
     * @param info the metadata information of the avatar to download
     * @return true if the download was successfull
     */
    public boolean downloadAvatar(String from, String avatarId, Info info) {
	try {
	    AvatarRetriever retriever = AvatarRetrieverFactory.getRetriever(mCon, from, info);
	    byte[] avatar = retriever.getAvatar();
	    mCache.put(avatarId, avatar);
	    return true;
	} catch (IOException e) {
	    System.err.println("Error while downloading avatar");
	    e.printStackTrace();
	    return false;
	}
    }

    /**
     * Disable the diffusion of your avatar.
     */
    public void disableAvatarPublishing() {
	AvatarMetadataExtension metadata = new AvatarMetadataExtension();
	publishAvatarMetaData(null, metadata);
    }

    /**
     * Send an avatar image to the pep server.
     *
     * @param data the image data.
     * @return true if the image where successfully sent. false otherwise
     */
    public boolean publishAvatarData(byte[] data) {
	try {
	    String id = getAvatarId(data);
	    publishAvatarData(id, data);
	    return true;
	} catch (NoSuchAlgorithmException e) {
	    System.err.println("Security error while publishing avatar data : " + e.getMessage());
	    return false;
	}
    }

    /**
     * Send the metadata of the avatar you want to publish.
     * By sending this metadata, you publish an avatar.
     *
     * @param id the id of the metadata item
     * @param metadata the metadata to publish
     */
    public void publishAvatarMetaData(String id, AvatarMetadataExtension metadata) {
	PayloadItem<AvatarMetadataExtension> item = new PayloadItem<AvatarMetadataExtension>(id, metadata);
	LeafNode node = mPep.getPEPNode(AVATARMETADATA_NODE);
	node.publish(item);
    }

    /**
     * Select the avatar to download.
     * Subclass should override this method to take control over the selection process.
     * This implementation select the first element.
     *
     * @param available list of the avatar metadata information
     * @return the metadata of the avatar to download
     */
    protected Info selectAvatar(List<Info> available) {
	return available.get(0);
    }


    /**
     * Get the id corresponding to this avatar data.
     *
     * @param data the avatar data
     * @return the id
     * @throws NoSuchAlgorithmException if the sha-1 algorithm is unavailable
     */
    protected String getAvatarId(byte[] data) throws NoSuchAlgorithmException {
	MessageDigest md = MessageDigest.getInstance("sha-1");
	byte[] hash = md.digest(data);
	return StringUtils.encodeHex(hash);
    }

    /**
     * Publish an avatar data.
     *
     * @param id the id of the avatar data
     * @param data the data of the avatar
     */
    private void publishAvatarData(String id, byte[] data) {
	AvatarExtension avatar = new AvatarExtension(data);
	PayloadItem<AvatarExtension> item = new PayloadItem<AvatarExtension>(id, avatar);
	LeafNode node = mPep.getPEPNode(AVATARDATA_NODE);
	node.publish(item);
    }

    /**
     * Fire the listeners for avatar change.
     *
     * @param from the jid of the contact
     * @param avatarId the new avatar id
     * @param avatarInfos the metadata infos of the avatar
     */
    private void fireListeners(String from, String avatarId, List<Info> avatarInfos) {
	for (AvatarListener l : mListeners)
	    l.onAvatarChange(from, avatarId, avatarInfos);
    }


    /**
     * A listener to PEPEevent.
     */
    private class Listener implements PEPListener {

	/**
	 * Create a listener.
	 */
	public Listener() {
	}

	@Override
	public void eventReceived(String from, String node, List<Item> items) {
	    if (!"urn:xmpp:avatar:metadata".equals(node))
		return;
	    Item i = items.get(0);
	    if (i instanceof PayloadItem) {
		PayloadItem<PacketExtension> pi = (PayloadItem<PacketExtension>) i;
		PacketExtension ex = pi.getPayload();
		if (ex instanceof AvatarMetadataExtension) {
		    AvatarMetadataExtension ext = (AvatarMetadataExtension) ex;
		    String id = i.getId();
		    List<Info> infos = ext.getInfos();
		    if (infos.size() > 0 && mAutoDownload) {
			Info info = selectAvatar(infos);
			if (!mCache.contains(id))
			    downloadAvatar(from, id, info);
		    }
		    fireListeners(from, id, infos);
		}
	    }
	}
    }
}
