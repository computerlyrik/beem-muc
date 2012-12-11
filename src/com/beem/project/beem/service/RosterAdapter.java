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
package com.beem.project.beem.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.beem.project.beem.R;
import com.beem.project.beem.service.aidl.IBeemRosterListener;
import com.beem.project.beem.smack.avatar.AvatarListener;
import com.beem.project.beem.smack.avatar.AvatarManager;
import com.beem.project.beem.smack.avatar.AvatarMetadataExtension.Info;
import com.beem.project.beem.utils.Status;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;


/**
 * This class implement a Roster adapter for BEEM.
 */
public class RosterAdapter extends com.beem.project.beem.service.aidl.IRoster.Stub {

    private static final String TAG = "RosterAdapter";
    private final Roster mAdaptee;
    private final RemoteCallbackList<IBeemRosterListener> mRemoteRosListeners =
	new RemoteCallbackList<IBeemRosterListener>();
    private final Map<Integer, String> mDefaultStatusMessages;
    private final RosterListenerAdapter mRosterListener = new RosterListenerAdapter();
    private Map<String, String> mAvatarIdmap = new HashMap<String, String>();
    private AvatarManager mAvatarManager;

    /**
     * Constructor.
     * @param roster The roster to adapt.
     * @param context The context of the RosterAdapter.
     */
    public RosterAdapter(final Roster roster, final Context context) {
	mAdaptee = roster;
	roster.addRosterListener(mRosterListener);
	mDefaultStatusMessages = createDefaultStatusMessagesMap(context);
    }

    /**
     * Constructor.
     * @param roster The roster to adapt.
     * @param context The context of the RosterAdapter.
     * @param avatarMgr The AvatarManager of the connection
     */
    public RosterAdapter(final Roster roster, final Context context, final AvatarManager avatarMgr) {
	mAdaptee = roster;
	roster.addRosterListener(mRosterListener);
	mDefaultStatusMessages = createDefaultStatusMessagesMap(context);
	mAvatarManager = avatarMgr;
	if (mAvatarManager != null)
	    mAvatarManager.addAvatarListener(new AvatarEventListener());
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void addRosterListener(IBeemRosterListener listen) throws RemoteException {
	if (listen != null)
	    mRemoteRosListeners.register(listen);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addContact(String user, String name, String[] groups) throws RemoteException {
	RosterEntry contact = mAdaptee.getEntry(user);
	try {
	    mAdaptee.createEntry(user, name, groups);
	    contact = mAdaptee.getEntry(user);
	} catch (XMPPException e) {
	    Log.e(TAG, "Error while adding new contact", e);
	    return false;
	}
	return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteContact(Contact contact) throws RemoteException {
	try {
	    RosterEntry entry = mAdaptee.getEntry(contact.getJID());
	    mAdaptee.removeEntry(entry);
	} catch (XMPPException e) {
	    e.printStackTrace();
	}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createGroup(String groupname) throws RemoteException {
	if (mAdaptee.getGroup(groupname) == null)
	    mAdaptee.createGroup(groupname);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Contact getContact(String jid) throws RemoteException {
	if (mAdaptee.contains(jid))
	    return getContactFromRosterEntry(mAdaptee.getEntry(jid));
	return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Contact> getContactList() throws RemoteException {
	boolean add = true;
	Collection<RosterEntry> list = mAdaptee.getEntries();
	List<Contact> coList = new ArrayList<Contact>(list.size());
	for (RosterEntry entry : list) {
	    coList.add(getContactFromRosterEntry(entry));
	}
	return coList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getGroupsNames() throws RemoteException {
	Collection<RosterGroup> groups = mAdaptee.getGroups();
	List<String> result = new ArrayList<String>(groups.size());
	for (RosterGroup rosterGroup : groups) {
	    result.add(rosterGroup.getName());
	}
	return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeRosterListener(IBeemRosterListener listen) throws RemoteException {
	if (listen != null)
	    mRemoteRosListeners.unregister(listen);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setContactName(String jid, String name) throws RemoteException {
	mAdaptee.getEntry(jid).setName(name);
    }

    /* (non-Javadoc)
     * @see com.beem.project.beem.service.aidl.IRoster#getPresence(java.lang.String)
     */
    @Override
    public PresenceAdapter getPresence(String jid) throws RemoteException {
	return new PresenceAdapter(mAdaptee.getPresence(jid));
    }

    /* (non-Javadoc)
     * @see com.beem.project.beem.service.aidl.IRoster#addContactToGroup(java.lang.String, java.lang.String)
     */
    @Override
    public void addContactToGroup(String groupName, String jid) throws RemoteException {
	createGroup(groupName);
	RosterGroup group = mAdaptee.getGroup(groupName);
	try {
	    group.addEntry(mAdaptee.getEntry(jid));
	} catch (XMPPException e) {
	    e.printStackTrace();
	}
    }

    /* (non-Javadoc)
     * @see com.beem.project.beem.service.aidl.IRoster#removeContactFromGroup(java.lang.String, java.lang.String)
     */
    @Override
    public void removeContactFromGroup(String groupName, String jid) throws RemoteException {
	RosterGroup group = mAdaptee.getGroup(groupName);
	try {
	    group.removeEntry(mAdaptee.getEntry(jid));
	} catch (XMPPException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Get a contact from a RosterEntry.
     * @param entry a roster entry containing information for the contact.
     * @return a contact for this entry.
     */
    private Contact getContactFromRosterEntry(RosterEntry entry) {
	String user = entry.getUser();
	Contact c = new Contact(user);
	Presence p = mAdaptee.getPresence(user);

	if (p.getStatus() == null || "".equals(p.getStatus()))
	    p.setStatus(mDefaultStatusMessages.get(Status.getStatusFromPresence(p)));
	c.setStatus(p);
	try {
	    c.setGroups(entry.getGroups());
	} catch (NullPointerException e) {
	    Log.d(TAG, "Group list not ready", e);
	}
	Iterator<Presence> iPres = mAdaptee.getPresences(user);
	while (iPres.hasNext()) {
	    p = iPres.next();
	    if (!p.getType().equals(Presence.Type.unavailable))
		c.addRes(StringUtils.parseResource(p.getFrom()));
	}
	c.setName(entry.getName());
	c.setAvatarId(mAvatarIdmap.get(user));
	return c;
    }

    /**
     * Create a map which contains default status messages.
     * @param context The context of the roster adapter.
     * @return A Map<Integer, String> which assigns a status to a message.
     */
    private Map<Integer, String> createDefaultStatusMessagesMap(Context context) {
	Map<Integer, String> defaultStatusMessages = new HashMap<Integer, String>();
	defaultStatusMessages.put(Status.CONTACT_STATUS_AVAILABLE, context
	    .getString(R.string.contact_status_msg_available));
	defaultStatusMessages.put(Status.CONTACT_STATUS_AVAILABLE_FOR_CHAT, context
	    .getString(R.string.contact_status_msg_available_chat));
	defaultStatusMessages.put(Status.CONTACT_STATUS_AWAY, context.getString(R.string.contact_status_msg_away));
	defaultStatusMessages.put(Status.CONTACT_STATUS_BUSY, context.getString(R.string.contact_status_msg_dnd));
	defaultStatusMessages.put(Status.CONTACT_STATUS_DISCONNECT, context
	    .getString(R.string.contact_status_msg_offline));
	defaultStatusMessages.put(Status.CONTACT_STATUS_UNAVAILABLE, context.getString(R.string.contact_status_msg_xa));

	return defaultStatusMessages;
    }

    /**
     * Listener for the roster events. It will call the remote listeners registered.
     * @author darisk
     */
    private class RosterListenerAdapter implements RosterListener {

	/**
	 * Constructor.
	 */
	public RosterListenerAdapter() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void entriesAdded(Collection<String> addresses) {
	    final int n = mRemoteRosListeners.beginBroadcast();

	    List<String> tab = new ArrayList<String>();
	    tab.addAll(addresses);
	    for (int i = 0; i < n; i++) {
		IBeemRosterListener listener = mRemoteRosListeners.getBroadcastItem(i);
		try {
		    listener.onEntriesAdded(tab);
		} catch (RemoteException e) {
		    Log.w(TAG, "Error while adding roster entries", e);
		}
	    }
	    mRemoteRosListeners.finishBroadcast();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void entriesDeleted(Collection<String> addresses) {
	    final int n = mRemoteRosListeners.beginBroadcast();

	    List<String> tab = new ArrayList<String>();
	    tab.addAll(addresses);
	    for (int i = 0; i < n; i++) {
		IBeemRosterListener listener = mRemoteRosListeners.getBroadcastItem(i);
		try {
		    listener.onEntriesDeleted(tab);
		} catch (RemoteException e) {
		    Log.w(TAG, "Error while deleting roster entries", e);
		}
	    }
	    mRemoteRosListeners.finishBroadcast();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void entriesUpdated(Collection<String> addresses) {
	    final int n = mRemoteRosListeners.beginBroadcast();

	    List<String> tab = new ArrayList<String>();
	    tab.addAll(addresses);
	    for (int i = 0; i < n; i++) {
		IBeemRosterListener listener = mRemoteRosListeners.getBroadcastItem(i);
		try {
		    listener.onEntriesUpdated(tab);
		} catch (RemoteException e) {
		    Log.w(TAG, "Error while updating roster entries", e);
		}
	    }
	    mRemoteRosListeners.finishBroadcast();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void presenceChanged(Presence presence) {
	    final int n = mRemoteRosListeners.beginBroadcast();
	    Log.v(TAG, ">>> Presence changed for " + presence.getFrom());

	    for (int i = 0; i < n; i++) {
		IBeemRosterListener listener = mRemoteRosListeners.getBroadcastItem(i);
		try {
		    if (presence.getStatus() == null || "".equals(presence.getStatus())) {
			presence.setStatus(mDefaultStatusMessages.get(Status.getStatusFromPresence(presence)));
		    }
		    listener.onPresenceChanged(new PresenceAdapter(presence));
		} catch (RemoteException e) {
		    Log.w(TAG, "Error while updating roster presence entries", e);
		}
	    }
	    mRemoteRosListeners.finishBroadcast();
	}
    }

    /**
     * Listener on avatar metadata event.
     *
     */
    private class AvatarEventListener implements AvatarListener {

	/**
	 * Constructor.
	 */
	public AvatarEventListener() { }

	@Override
	public void onAvatarChange(String from, String avatarId, List<Info> avatarInfos) {
	    String bare = StringUtils.parseBareAddress(from);
	    if (avatarId == null)
		mAvatarIdmap.remove(bare);
	    else if (avatarInfos.size() > 0) {
		mAvatarIdmap.put(bare, avatarId);
	    }
	}
    }
}
