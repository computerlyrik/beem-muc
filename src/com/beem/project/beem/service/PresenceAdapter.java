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

import org.jivesoftware.smack.packet.Presence;

import android.os.Parcel;
import android.os.Parcelable;

import com.beem.project.beem.utils.PresenceType;
import com.beem.project.beem.utils.Status;

/**
 * this class contain contact presence informations.
 * @author nikita
 */
public class PresenceAdapter implements Parcelable {

    /** Parcelable.Creator needs by Android. */
    public static final Parcelable.Creator<PresenceAdapter> CREATOR = new Parcelable.Creator<PresenceAdapter>() {

	@Override
	public PresenceAdapter createFromParcel(Parcel source) {
	    return new PresenceAdapter(source);
	}

	@Override
	public PresenceAdapter[] newArray(int size) {
	    return new PresenceAdapter[size];
	}
    };

    private int mType;
    private int mStatus;
    private String mTo;
    private String mFrom;
    private String mStatusText;

    /**
     * constructor from Parcel.
     * @param source parcelable presence.
     */
    public PresenceAdapter(final Parcel source) {
	mType = source.readInt();
	mStatus = source.readInt();
	mTo = source.readString();
	mFrom = source.readString();
	mStatusText = source.readString();
    }

    /**
     * constructor from smack Presence.
     * @param presence smack presence.
     */
    public PresenceAdapter(final Presence presence) {
	mType = PresenceType.getPresenceType(presence);
	mStatus = Status.getStatusFromPresence(presence);
	mTo = presence.getTo();
	mFrom = presence.getFrom();
	mStatusText = presence.getStatus();
    }

    /* (non-Javadoc)
     * @see android.os.Parcelable#describeContents()
     */
    @Override
    public int describeContents() {
	// TODO Auto-generated method stub
	return 0;
    }

    /**
     * mFrom getter.
     * @return the mFrom
     */
    public String getFrom() {
	return mFrom;
    }

    /**
     * mStatus getter.
     * @return the mStatus
     */
    public int getStatus() {
	return mStatus;
    }

    /**
     * mStatusText getter.
     * @return the mStatusText
     */
    public String getStatusText() {
	return mStatusText;
    }

    /**
     * mTo getter.
     * @return the mTo
     */
    public String getTo() {
	return mTo;
    }

    /**
     * mType getter.
     * @return the mType
     */
    public int getType() {
	return mType;
    }

    /**
     * mFrom setter.
     * @param from the mFrom to set
     */
    public void setFrom(final String from) {
	this.mFrom = from;
    }

    /**
     * mStatus setter.
     * @param status the mStatus to set
     */
    public void setStatus(final int status) {
	this.mStatus = status;
    }

    /**
     * mStatusText setter.
     * @param statusText the mStatusText to set
     */
    public void setStatusText(final String statusText) {
	this.mStatusText = statusText;
    }

    /**
     * mTo setter.
     * @param to the mTo to set
     */
    public void setTo(final String to) {
	this.mTo = to;
    }

    /**
     * mType setter.
     * @param type the type to set
     */
    public void setType(int type) {
	this.mType = type;
    }

    /* (non-Javadoc)
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
	dest.writeInt(mType);
	dest.writeInt(mStatus);
	dest.writeString(mTo);
	dest.writeString(mFrom);
	dest.writeString(mStatusText);
    }
}
