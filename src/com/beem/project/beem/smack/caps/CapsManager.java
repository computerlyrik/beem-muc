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

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.PacketInterceptor;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.packet.DiscoverInfo;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smack.util.collections.ReferenceMap;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.filter.PacketExtensionFilter;

import java.util.Map;
import java.util.Iterator;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;

import org.jivesoftware.smack.util.StringUtils;

/**
 * Capabilities manager to implements XEP-0115.
 * The DiscoverInfo are cached in memory.
 *
 */
public class CapsManager {
    // the verCache should be stored on disk
    private Map<String, DiscoverInfo> mVerCache = new ReferenceMap<String, DiscoverInfo>();
    private Map<String, DiscoverInfo> mJidCache = new ReferenceMap<String, DiscoverInfo>();

    private ServiceDiscoveryManager mSdm;
    private Connection mConnection;
    private String mNode;
    private List<String> mSupportedAlgorithm = new ArrayList<String>();

    /**
     * Create a CapsManager.
     *
     * @param sdm The service discovery manager to use.
     * @param conn The connection to manage.
     */
    public CapsManager(final ServiceDiscoveryManager sdm, final Connection conn) {
	mSdm = sdm;
	mConnection = conn;
	init();
    }

    /**
     * Get the discover info associated with a ver attribute.
     *
     * @param ver the ver attribute.
     * @return the discover info or null if it was not cached.
     */
    public DiscoverInfo getDiscoverInfo(String ver) {
	return mVerCache.get(ver);
    }

    /**
     * Get the discover info of a contact.
     *
     * @param jid the jid of the contact.
     * @param ver the ver attribute of the contact capability.
     * @return The info of the client null if the info was not cached.
     */
    public DiscoverInfo getDiscoverInfo(String jid, String ver) {
	DiscoverInfo info = mVerCache.get(ver);
	if (info == null) {
	    info = load(ver);
	    if (info == null)
		info = mJidCache.get(jid);
	}
	return info;
    }

    /**
     * Set the node attribute to send in your capability.
     * This is usually an uri to identify the client.
     *
     * @param node the node attribute to set.
     */
    public void setNode(String node) {
	mNode = node;
    }

    /**
     * Load a persistent DiscoverInfo.
     * The default implementation does nothing and always return null.
     *
     * @param ver the ver hash of the discoverInfo.
     * @return The discover info or null if not present.
     */
    protected DiscoverInfo load(String ver) {
	return null;
    }

    /**
     * Store a DiscoverInfo for persistence.
     * The default implementation does nothing.
     *
     * @param ver the ver hash of the DiscoverInfo
     * @param info the DiscoverInfo to store
     */
    protected void store(String ver, DiscoverInfo info) {
    }

    /**
     * Check if the discover info correspondig to the ver hash is in cache.
     * This implementation checks the memory cache.
     * If the info is not in cache it is necessary to request it from the network.
     *
     * @param ver the ver hash
     * @return true if it is in cache false otherwise
     */
    protected boolean isInCache(String ver) {
	return mVerCache.containsKey(ver);
    }

    /**
     * Initialize this CapsManageer.
     */
    private void init() {
	initSupportedAlgorithm();
	PacketFilter filter = new PacketExtensionFilter("c", "http://jabber.org/protocol/caps");
	mConnection.addPacketListener(new PacketListener() {
	    public void processPacket(Packet packet) {
		if (packet.getFrom().equals(mConnection.getUser()))
		    return;
		PacketExtension p = packet.getExtension("c", "http://jabber.org/protocol/caps");
		CapsExtension caps = (CapsExtension) p;
		if (!isInCache(caps.getVer())) {
		    validate(packet.getFrom(), caps.getNode(), caps.getVer(), caps.getHash());
		}
	    }
	}, filter);
	mConnection.addPacketInterceptor(new PacketInterceptor() {

	    public void interceptPacket(Packet packet) {
		DiscoverInfo info = getOwnInformation();
		if (mSupportedAlgorithm.size() > 0) {
		    try {
			String algo = mSupportedAlgorithm.get(0);
			String ver = calculateVer(info, algo);
			CapsExtension caps = new CapsExtension(algo, mNode, ver);
			packet.addExtension(caps);
		    } catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		    }
		}
	    }
	}, new PacketTypeFilter(Presence.class));
    }

    /**
     * Validate the ver attribute of a received capability.
     *
     * @param jid the jid of the sender of the capability.
     * @param node the node attribute of the capability.
     * @param ver the ver attribute of the capability.
     * @param hashMethod the hash algorithm to use to calculate ver
     * @return true if the ver attribute is valid false otherwise.
     */
    private boolean validate(String jid, String node, String ver, String hashMethod) {
	try {
	    DiscoverInfo info = mSdm.discoverInfo(jid, node + "#" + ver);
	    if (!mSupportedAlgorithm.contains(hashMethod)) {
		mJidCache.put(jid, info);
		return false;
	    }
	    String v = calculateVer(info, hashMethod);
	    boolean res = v.equals(ver);
	    if (res) {
		mVerCache.put(ver, info);
		store(ver, info);
	    }
	    return res;
	} catch (XMPPException e) {
	    e.printStackTrace();
	} catch (NoSuchAlgorithmException e) {
	    e.printStackTrace();
	}
	return false;
    }

    /**
     * Calculate the ver attribute.
     *
     * @param info The discover info to calculate the ver.
     * @param hashMethod the hash algorithm to use.
     * @return the value of the ver attribute
     * @throws NoSuchAlgorithmException if the hash algorithm is not supported.
     */
    private String calculateVer(DiscoverInfo info, String hashMethod) throws NoSuchAlgorithmException {
	StringBuilder s = new StringBuilder();
	for (DiscoverInfo.Identity identity : getSortedIdentity(info)) {
	    String c = identity.getCategory();
	    if (c != null)
		s.append(c);
	    s.append('/');
	    c = identity.getType();
	    if (c != null)
		s.append(c);
	    s.append('/');
	    // Should add lang but it is not available
//             c = identity.getType();
//             if (c != null)
//                 S.append(c);
	    s.append('/');
	    c = identity.getName();
	    if (c != null)
		s.append(c);
	    s.append('<');
	}
	for (String f : getSortedFeature(info)) {
	    s.append(f);
	    s.append('<');
	}
	// Should add data form (XEP 0128) but it is not available
	byte[] hash = getHash(hashMethod, s.toString().getBytes());
	return StringUtils.encodeBase64(hash);
    }

    /**
     * Get the identities sorted correctly to calculate the ver attribute.
     *
     * @param info the DiscoverInfo containing the identities
     * @return the sorted list of identities.
     */
    private List<DiscoverInfo.Identity> getSortedIdentity(DiscoverInfo info) {
	List<DiscoverInfo.Identity> result = new ArrayList<DiscoverInfo.Identity>();
	Iterator<DiscoverInfo.Identity> it = info.getIdentities();
	while (it.hasNext()) {
	    DiscoverInfo.Identity id = it.next();
	    result.add(id);
	}
	Collections.sort(result, new Comparator<DiscoverInfo.Identity>() {
	    public int compare(DiscoverInfo.Identity o1, DiscoverInfo.Identity o2) {

		String cat1 = o1.getCategory();
		if (cat1 == null) cat1 = "";
		String cat2 = o2.getCategory();
		if (cat2 == null) cat2 = "";
		int res = cat1.compareTo(cat2);
		if (res != 0)
		    return res;
		String type1 = o1.getType();
		if (type1 == null) type1 = "";
		String type2 = o2.getCategory();
		if (type2 == null) type2 = "";
		res = type1.compareTo(type2);
		if (res != 0)
		    return res;
		// should compare lang but not avalaible
		return 0;
	    }
	});
	return result;
    }

    /**
     * Get the features sorted correctly to calculate the ver attribute.
     *
     * @param info the DiscoverInfo containing the features
     * @return the sorted list of features.
     */
    private List<String> getSortedFeature(DiscoverInfo info) {
	List<String> result = new ArrayList<String>();
	Iterator<DiscoverInfo.Feature> it = info.getFeatures();
	while (it.hasNext()) {
	    DiscoverInfo.Feature feat = it.next();
	    result.add(feat.getVar());
	}
	Collections.sort(result);
	return result;
    }

    /**
     * Get the Discover Information send by your own connection.
     *
     * @return your own DiscoverInfo
     */
    private DiscoverInfo getOwnInformation() {
	DiscoverInfo result = new DiscoverInfo();
	DiscoverInfo.Identity id = new DiscoverInfo.Identity("client", ServiceDiscoveryManager.getIdentityName());
	id.setType(ServiceDiscoveryManager.getIdentityType());
	result.addIdentity(id);
	Iterator<String> it = mSdm.getFeatures();
	while (it.hasNext()) {
	    result.addFeature(it.next());
	}
	return result;
    }

    /**
     * Calculate a Hash (digest).
     *
     * @param algo the algorithm to use
     * @param data the data to compute
     * @return the resulting hash
     * @throws NoSuchAlgorithmException if the algorithm is not supported
     */
    private byte[] getHash(String algo, byte[] data) throws NoSuchAlgorithmException {
	MessageDigest md = MessageDigest.getInstance(algo);
	return md.digest(data);
    }

    /**
     * Initialize a list of supported Hash algorithm.
     */
    private void initSupportedAlgorithm() {
	// sort by ""preference"
	String[] algo = new String[] {"sha-1", "md2", "md5", "sha-224", "sha-256", "sha-384", "sha-512" };
	for (String a : algo) {
	    try {
		MessageDigest md = MessageDigest.getInstance(a);
		mSupportedAlgorithm.add(a);
	    } catch (NoSuchAlgorithmException e) {
		System.err.println("Hash algorithm " + a + " not supported");
	    }
	}
    }

}
