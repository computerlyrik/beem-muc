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

import java.util.LinkedList;
import java.util.List;

import org.jivesoftware.smack.packet.PacketExtension;

/**
 * PacketExtension to represent the Avatar metadata.
 * XML namespace urn:xmpp:avatar:metadata
 *
 */
public class AvatarMetadataExtension implements PacketExtension {
    private List<Info> mInfos = new LinkedList<Info>();

    /**
     * Create an AvatarMetadataExtension.
     */
    public AvatarMetadataExtension() {
    }

    /**
     * Get the metadata informations.
     *
     * @return a list of informations
     */
    public List<Info> getInfos() {
	return mInfos;
    }

    /**
     * Add a metadate information.
     *
     * @param info the metadata information to add
     */
    public void addInfo(Info info) {
	mInfos.add(info);
    }

    @Override
    public String getElementName() {
	return "metadata";
    }

    @Override
    public String getNamespace() {
	return "urn:xmpp:avatar:metadata";
    }

    @Override
    public String toXML() {
	StringBuilder builder = new StringBuilder("<metadata xmlns=\"");
	builder.append(getNamespace()).append("\">");
	for (Info info : mInfos) {
	    builder.append(info.toXML());
	}
	builder.append("</metadata>");
	return builder.toString();
    }

    /**
     * A metadata information element.
     */
    public static class Info {
	private int mBytes;
	private int mHeight;
	private int mWidth;
	private String mId;
	private String mType;
	private String mUrl;

	/**
	 * Create an Info.
	 *
	 * @param id the id of the info
	 * @param type the MIME type of the avatar
	 * @param bytes the size of the avatar in bytes
	 */
	public Info(final String id, final String type, final int bytes) {
	    mId = id;
	    mType = type;
	    mBytes = bytes;
	}

	/**
	 * Set the size of the avatar in bytes.
	 *
	 * @param bytes the size
	 */
	public void setBytes(int bytes) {
	    this.mBytes = bytes;
	}

	/**
	 * Set the size of the avatar in bytes.
	 *
	 * @return the size
	 */
	public int getBytes() {
	    return mBytes;
	}

	/**
	 * Set the height.
	 *
	 * @param height the height
	 */
	public void setHeight(int height) {
	    this.mHeight = height;
	}

	/**
	 * Get the height.
	 *
	 * @return the height
	 */
	public int getHeight() {
	    return mHeight;
	}

	/**
	 * Set the width.
	 *
	 * @param width the width
	 */
	public void setWidth(int width) {
	    this.mWidth = width;
	}

	/**
	 * Get the width.
	 *
	 * @return the width
	 */
	public int getWidth() {
	    return mWidth;
	}

	/**
	 * Set the url.
	 *
	 * @param url the url
	 */
	public void setUrl(String url) {
	    this.mUrl = url;
	}

	/**
	 * Get the url.
	 *
	 * @return the url, null if no url is present
	 */
	public String getUrl() {
	    return mUrl;
	}

	/**
	 * Get the id.
	 *
	 * @return the id
	 */
	public String getId() {
	    return mId;
	}

	/**
	 * Set the id.
	 *
	 * @param id the id
	 */
	public void setId(String id) {
	    this.mId = id;
	}

	/**
	 * Set the MIME type of the avatar.
	 *
	 * @param type the type
	 */
	public void setType(String type) {
	    this.mType = type;
	}

	/**
	 * Get the MIME type of the avatar.
	 *
	 * @return the type, null if no type is present
	 */
	public String getType() {
	    return mType;
	}

	/**
	 * Return this information as an xml element.
	 *
	 * @return an xml element representing this information
	 */
	public String toXML() {
	    StringBuilder builder = new StringBuilder("<info ");
	    builder.append("id=\"" + mId + "\"");
	    builder.append(" type=\"" + mType + "\"");
	    builder.append(" bytes=\"" + mBytes + "\"");

	    if (mHeight > 0)
		builder.append(" height=\"" + mHeight + "\"");
	    if (mWidth > 0)
		builder.append(" width=\"" + mWidth + "\"");
	    if (mUrl != null)
		builder.append(" url=\"" + mUrl + "\"");
	    builder.append(" />");
	    return builder.toString();
	}
    }
}
