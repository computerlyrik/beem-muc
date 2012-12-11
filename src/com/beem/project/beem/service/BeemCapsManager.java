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

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smackx.packet.DiscoverInfo;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParser;

import android.util.Log;
import java.io.FileReader;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;

import java.io.File;
import java.io.IOException;

import android.content.Context;

import com.beem.project.beem.smack.caps.CapsManager;

/**
 * An implementation of CapsManager which keeps DiscoverInfo on the Cache directory of the android application.
 */
public class BeemCapsManager extends CapsManager {

    private static final String TAG = "BeemCapsManager";

    private Context mContext;
    private File mCacheDir;
    private XmlPullParser mParser;

    /**
     * Create a BeemCapsManager.
     *
     * @param sdm the ServiceDiscoveryManager to use
     * @param conn the connection to use
     * @param context the Android context to use to store data
     */
    public BeemCapsManager(final ServiceDiscoveryManager sdm, final Connection conn, final Context context) {
	super(sdm, conn);
	mContext = context;
	initCacheDirectory();
    }


    @Override
    protected DiscoverInfo load(String ver) {
	File fver = new File(mCacheDir, sanitizeName(ver));
	try {
	    Reader fr = new BufferedReader(new FileReader(fver));
	    try {
		if (mParser == null)
		    mParser = makeParser();
		mParser.setInput(fr);
		return (DiscoverInfo) PacketParserUtils.parsePacketExtension("query",
			"http://jabber.org/protocol/disco#info", mParser);

	    } finally {
		fr.close();
	    }
	} catch (Exception e) {
	    // The parsePacketExtension throw Exception on error
	    Log.d(TAG, "Error while loading Capabilities " + ver, e);
	}
	return null;
    }

    @Override
    protected void store(String ver, DiscoverInfo info) {

	File fver = new File(mCacheDir, sanitizeName(ver));
	try {
	    Writer fw = new BufferedWriter(new FileWriter(fver));
	    try {
		String data  = info.toXML();
		fw.write(data, 0, data.length());
	    } finally {
		fw.close();
	    }
	} catch (IOException e) {
	    Log.d(TAG, "Error while saving Capabilities " + ver, e);
	}
    }

    @Override
    protected boolean isInCache(String ver) {
	boolean result = super.isInCache(ver);
	if (!result) {
	    File fver = new File(mCacheDir, sanitizeName(ver));
	    result = fver.exists();
	}
	return result;
    }

    /**
     * Init the cache directory.
     */
    private void initCacheDirectory() {
	File dir = mContext.getCacheDir();
	mCacheDir = new File(dir, "capabilities");
	mCacheDir.mkdir();
    }

    /**
     * Make an Xml parser.
     *
     * @return the created xml parser.
     * @throws XmlPullParserException if an error occurs while creating the parser.
     */
    private XmlPullParser makeParser() throws XmlPullParserException {
	XmlPullParserFactory fact = XmlPullParserFactory.newInstance();
	fact.setNamespaceAware(true);
	return fact.newPullParser();
    }

    /**
     * Sanitize the base64 ver attribute in order to use it as a filename.
     * @param ver the base64 ver attribute
     * @return a sanitize filename for the ver attribute
     */
    private String sanitizeName(String ver) {
	return ver.replaceAll("/", ".");
    }
}
