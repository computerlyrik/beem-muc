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
 * A simplified version of the Smack PrivacyItem class.
 * @author Jean-Manuel Da Silva <dasilvj at beem-project dot com>
 */
public class PrivacyListItem implements Parcelable {

    /**
     * Constructor. Needed to implements the Parcelable.Creator interface. Generates instances of PrivacyListItem from a
     * Parcel.
     */
    public static final Parcelable.Creator<PrivacyListItem> CREATOR = new Parcelable.Creator<PrivacyListItem>() {
	public PrivacyListItem createFromParcel(Parcel in) {
	    return new PrivacyListItem(in);
	}

	public PrivacyListItem[] newArray(int size) {
	    return new PrivacyListItem[size];
	}
    };

    private int mType;
    private String mValue;

    /**
     * Constructor.
     */
    public PrivacyListItem() {
    }

    /**
     * Constructor. Generates instances of PrivacyListItem from a Parcel.
     * @param in The Parcel used to initialize object's attributes.
     */
    public PrivacyListItem(final Parcel in) {
	readFromParcel(in);
    }

    /**
     * Constructor.
     * @param type The type of the item.
     * @param value The value of the item.
     */
    public PrivacyListItem(final int type, final String value) {
	mType = type;
	mValue = value;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int describeContents() {
	return 0;
    }

    /**
     * Initialize object's attributes from a Parcel.
     * @param in The Parcel used to initialize object's attributes.
     */
    public void readFromParcel(Parcel in) {
	mType = in.readInt();
	mValue = in.readString();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
	dest.writeInt(mType);
	dest.writeString(mValue);
    }

    /**
     * PrivacyListItem type accessor.
     * @return The type of the PrivacyListItem.
     */
    public int getType() {
	return mType;
    }

    /**
     * PrivacyListItem value accessor.
     * @return The value of the PrivacyListItem.
     */
    public String getValue() {
	return mValue;
    }

    /**
     * PrivacyListItem type mutator.
     * @param type The type of the PrivacyListItem.
     */
    public void setType(final int type) {
	mType = type;
    }

    /**
     * PrivacyListItem value mutator.
     * @param value The value of the PrivacyListItem.
     */
    public void setValue(final String value) {
	mValue = value;
    }
}
