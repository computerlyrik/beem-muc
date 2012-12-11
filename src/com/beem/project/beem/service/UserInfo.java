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

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class contains information about the user of the connection.
 * These informations are sent by the connection.
 *
 */
public class UserInfo implements Parcelable {

    /** Parcelable.Creator needs by Android. */
    public static final Parcelable.Creator<UserInfo> CREATOR = new Parcelable.Creator<UserInfo>() {

	@Override
	public UserInfo createFromParcel(Parcel source) {
	    return new UserInfo(source);
	}

	@Override
	public UserInfo[] newArray(int size) {
	    return new UserInfo[size];
	}
    };

    private final String mFullJid;
    private String mAvatarId;

    /**
     * Construct a UserInfo from a parcel.
     * @param in parcel to use for construction
     */
    private UserInfo(final Parcel in) {
	mFullJid = in.readString();
	mAvatarId = in.readString();
    }

    /**
     * Constructor.
     * @param jid jid of the user
     */
    public UserInfo(final String jid) {
	// the jid is case insensitive
	mFullJid = jid;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
	dest.writeString(mFullJid);
	dest.writeString(mAvatarId);
    }

    @Override
    public int describeContents() {
	return 0;
    }

    /**
     * Get the avatar id of the user.
     *
     * @return the avatar id
     */
    public String getAvatarId() {
	return mAvatarId;
    }

    /**
     * Set the avater id of the user.
     *
     * @param avatarId the avatar id
     */
    public void setAvatarId(String avatarId) {
	mAvatarId = avatarId;
    }

    /**
     * Get the full jid of the user.
     *
     * @return the jid
     */
    public String getJid() {
	return mFullJid;
    }
}
