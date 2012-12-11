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

import org.jivesoftware.smack.packet.PacketExtension;

/**
 * This extension represents a capability of XEP-0115.
 *
 */
public class CapsExtension implements PacketExtension {

    private String mVer;
    private String mHash;
    private String mNode;
    private String mExt;


    /**
     * Create a CapsExtension.
     *
     * @param hash The value of the hash attribute.
     * @param node the value of the node attribute
     * @param ver the value of the ver attribute.
     */
    public CapsExtension(final String hash, final String node, final String ver) {
	mHash = hash;
	mNode = node;
	mVer = ver;
    }

    /**
     * Get the ver attribute value.
     *
     * @return the value of the ver attribute.
     */
    public String getVer() {
	return mVer;
    }

    /**
     * Get the hash attribute value.
     *
     * @return the value of the hash attribute.
     */
    public String getHash() {
	return mHash;
    }

    /**
     * Get the node attribute value.
     *
     * @return the value of the node attribute.
     */
    public String getNode() {
	return mNode;
    }

    /**
     * Get the ext attribute value.
     *
     * @return the value of the ext attribute.
     */
    public String getExt() {
	return mExt;
    }

    /**
     * Set the hash attribute.
     *
     * @param hash the value of hash
     */
    public void setHash(String hash) {
	mHash = hash;
    }

    /**
     * Set the ver attribute.
     *
     * @param ver the value of ver
     */
    public void setVer(String ver) {
	mVer = ver;
    }

    /**
     * Set the node attribute.
     *
     * @param node the value of node
     */
    public void setNode(String node) {
	mNode = node;
    }

    /**
     * Set the ext attribute.
     *
     * @param ext the value of ext
     */
    public void setExt(String ext) {
	mExt = ext;
    }

    @Override
    public String getElementName() {
	return "c";
    }

    @Override
    public String getNamespace() {
	return "http://jabber.org/protocol/caps";
    }

    @Override
    public String toXML() {
	StringBuilder b = new StringBuilder("<");
	b.append(getElementName());
	b.append(" xmlns=\"").append(getNamespace()).append("\" ");
	if (mHash != null) {
	    b.append("hash=\"").append(mHash).append("\" ");
	}
	if (mNode != null)
	    b.append("node=\"").append(mNode).append("\" ");
	if (mVer != null)
	    b.append("ver=\"").append(mVer).append("\" ");
	if (mExt != null)
	    b.append("ext=\"").append(mExt).append("\" ");
	b.append("/>");
	return b.toString();
    }

}
