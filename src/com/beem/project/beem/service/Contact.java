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
import java.util.List;

import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.beem.project.beem.utils.Status;

/**
 * This class contains informations on a jabber contact.
 * @author darisk
 */
public class Contact implements Parcelable {

    /** Parcelable.Creator needs by Android. */
    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {

	@Override
	public Contact createFromParcel(Parcel source) {
	    return new Contact(source);
	}

	@Override
	public Contact[] newArray(int size) {
	    return new Contact[size];
	}
    };

    private int mID;
    private int mStatus;
    private final String mJID;
    private String mSelectedRes;
    private String mMsgState;
    private List<String> mRes;
    private final List<String> mGroups = new ArrayList<String>();
    private String mName;
    private String mAvatarId;

    /**
     * Construct a contact from a parcel.
     * @param in parcel to use for construction
     */
    private Contact(final Parcel in) {
	mID = in.readInt();
	mStatus = in.readInt();
	mJID = in.readString();
	mSelectedRes = in.readString();
	mName = in.readString();
	mMsgState = in.readString();
	mAvatarId = in.readString();
	mRes = new ArrayList<String>();
	in.readStringList(mRes);
	in.readStringList(mGroups);
    }

    /**
     * Constructor.
     * @param jid JID of the contact
     */
    public Contact(final String jid) {
	mJID = StringUtils.parseBareAddress(jid);
	mName = mJID;
	mStatus = Status.CONTACT_STATUS_DISCONNECT;
	mMsgState = null;
	mRes = new ArrayList<String>();
	String res = StringUtils.parseResource(jid);
	mSelectedRes = res;
	if (!"".equals(res))
	    mRes.add(res);
    }

    /**
     * Create a contact from a Uri.
     * @param uri an uri for the contact
     * @throws IllegalArgumentException if it is not a xmpp uri
     */
    public Contact(final Uri uri) {
	if (!"xmpp".equals(uri.getScheme()))
	    throw new IllegalArgumentException();
	String enduri = uri.getEncodedSchemeSpecificPart();
	mJID = StringUtils.parseBareAddress(enduri);
	mName = mJID;
	mStatus = Status.CONTACT_STATUS_DISCONNECT;
	mMsgState = null;
	mRes = new ArrayList<String>();
	String res = StringUtils.parseResource(enduri);
	mSelectedRes = res;
	mRes.add(res);
    }

    /**
     * Make an xmpp uri for a spcific jid.
     *
     * @param jid the jid to represent as an uri
     * @return an uri representing this jid.
     */
    public static Uri makeXmppUri(String jid) {
	StringBuilder build = new StringBuilder("xmpp:");
	String name = StringUtils.parseName(jid);
	build.append(name);
	if (!"".equals(name))
	    build.append('@');
	build.append(StringUtils.parseServer(jid));
	String resource = StringUtils.parseResource(jid);
	if (!"".equals(resource)) {
	    build.append('/');
	    build.append(resource);
	}
	Uri u = Uri.parse(build.toString());
	return u;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
	dest.writeInt(mID);
	dest.writeInt(mStatus);
	dest.writeString(mJID);
	dest.writeString(mSelectedRes);
	dest.writeString(mName);
	dest.writeString(mMsgState);
	dest.writeString(mAvatarId);
	dest.writeStringList(getMRes());
	dest.writeStringList(getGroups());
    }

    /**
     * Add a group for the contact.
     * @param group the group
     */
    public void addGroup(String group) {
	if (!mGroups.contains(group))
	    mGroups.add(group);
    }

    /**
     * Remove the contact from a group.
     * @param group the group to delete the contact from.
     */
    public void delGroup(String group) {
	mGroups.remove(group);
    }

    /**
     * Add a resource for this contact.
     * @param res the resource to add
     */
    public void addRes(String res) {
	if (!mRes.contains(res))
	    mRes.add(res);
    }

    /**
     * Delete a resource for this contact.
     * @param res the resource de delete
     */
    public void delRes(String res) {
	mRes.remove(res);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int describeContents() {
	return 0;
    }

    /**
     * Get the groups the contact is in.
     * @return the mGroups
     */
    public List<String> getGroups() {
	return mGroups;
    }

    /**
     * Get the id of the contact on the phone contact list.
     * @return the mID
     */
    public int getID() {
	return mID;
    }

    /**
     * Get the Jabber ID of the contact.
     * @return the Jabber ID
     */
    public String getJID() {
	return mJID;
    }

    /**
     * Get selected resource.
     * @return the selected resource.
     */
    public String getSelectedRes() {
	return mSelectedRes;
    }

    /**
     * Get the list of resource for the contact.
     * @return the mRes
     */
    public List<String> getMRes() {
	return mRes;
    }

    /**
     * Get the message status of the contact.
     * @return the message status of the contact.
     */
    public String getMsgState() {
	return mMsgState;
    }

    /**
     * Get the name of the contact.
     * @return the mName
     */
    public String getName() {
	return mName;
    }

    /**
     * Get the status of the contact.
     * @return the mStatus
     */
    public int getStatus() {
	return mStatus;
    }

    /**
     * Get the avatar id of the contact.
     *
     * @return the avatar id or null if there is not
     */
    public String getAvatarId() {
	return mAvatarId;
    }

    /**
     * Set the groups the contact is in.
     * @param groups list of groups
     */
    public void setGroups(Collection<RosterGroup> groups) {
	this.mGroups.clear();
	for (RosterGroup rosterGroup : groups) {
	    mGroups.add(rosterGroup.getName());
	}
    }

    /**
     * Set the groups the contact is in.
     * @param groups the mGroups to set
     */
    public void setGroups(List<String> groups) {
	mGroups.clear();
	mGroups.addAll(groups);
    }

    /**
     * set the id of te contact on the phone contact list.
     * @param mid the mID to set
     */
    public void setID(int mid) {
	mID = mid;
    }

    /**
     * Set the avatar id of the contact.
     *
     * @param avatarId the avatar id
     */
    public void setAvatarId(String avatarId) {
	mAvatarId = avatarId;
    }

    /**
     * Set the resource of the contact.
     * @param resource to set.
     */
    public void setSelectedRes(String resource) {
	mSelectedRes = resource;
    }

    /**
     * Set a list of resource for the contact.
     * @param mRes the mRes to set
     */
    public void setMRes(List<String> mRes) {
	this.mRes = mRes;
    }

    /**
     * Set the message status of the contact.
     * @param msgState the message status of the contact to set
     */
    public void setMsgState(String msgState) {
	mMsgState = msgState;
    }

    /**
     * Set the name of the contact.
     * @param name the mName to set
     */
    public void setName(String name) {
	if (name == null || "".equals(name)) {
	    this.mName = this.mJID;
	    this.mName = StringUtils.parseName(this.mName);
	    if (this.mName == null || "".equals(this.mName))
		this.mName = this.mJID;
	} else {
	    this.mName = name;
	}
    }

    /**
     * Set the status of the contact.
     * @param status the mStatus to set
     */
    public void setStatus(int status) {
	mStatus = status;
    }

    /**
     * Set the status of the contact using a presence packet.
     * @param presence the presence containing status
     */
    public void setStatus(Presence presence) {
	mStatus = Status.getStatusFromPresence(presence);
	mMsgState = presence.getStatus();
    }

    /**
     * Set status for the contact.
     * @param presence The presence packet which contains the status
     */
    public void setStatus(PresenceAdapter presence) {
	mStatus = presence.getStatus();
	mMsgState = presence.getStatusText();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
	if (mJID != null)
	    return mJID + "/[" + mRes + "]";
	return super.toString();
    }

    /**
     * Get a URI to access the contact.
     * @return the URI
     */
    public Uri toUri() {
	return makeXmppUri(mJID);
    }

    /**
     * Get a URI to access the specific contact on this resource.
     * @param resource the resource of the contact
     * @return the URI
     */
    public Uri toUri(String resource) {
	StringBuilder build = new StringBuilder("xmpp:");
	String name = StringUtils.parseName(mJID);
	build.append(name);
	if (!"".equals(name))
	    build.append('@');
	build.append(StringUtils.parseServer(mJID));
	if (!"".equals(resource)) {
	    build.append('/');
	    build.append(resource);
	}
	Uri u = Uri.parse(build.toString());
	return u;
    }

    /**
     * Get a JID to access the specific contact on this resource.
     * @return the JID.
     */
    public String getJIDWithRes() {
	StringBuilder build = new StringBuilder(mJID);
	if (!"".equals(mSelectedRes))
	    build.append('/').append(mSelectedRes);
	return build.toString();
    }

    @Override
    public boolean equals(Object other) {
	if (!(other instanceof Contact))
	    return false;
	if (other == this)
	    return true;
	Contact c = (Contact) other;
	return c.getJID().equals(getJID());
    }

    @Override
    public int hashCode() {
	return mJID.hashCode();
    }

}
