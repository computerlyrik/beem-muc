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

import com.beem.project.beem.smack.avatar.AvatarMetadataExtension.Info;
import org.jivesoftware.smack.Connection;
// API level 8
//import android.net.http.AndroidHttpClient;
//import org.apache.http.client.HttpClient;

/**
 * A factory for AvatarRetriever.
 */
public final class AvatarRetrieverFactory {

    /**
     * Private constructor.
     */
    private AvatarRetrieverFactory() {
    }

    /**
     * Get a AvatarRetriever to retrieve this avatar.
     *
     * @param con the connection
     * @param from the user which own the avatar
     * @param info the metadata information of the avatar to retrieve
     * @return an AvatarRetriever null if none can retrieve this avatar
     */
    public static AvatarRetriever getRetriever(Connection con, String from, Info info) {
	String url = info.getUrl();
	if (url != null) {
	    // return new HttpAvatarRetriever(url);
	    // HttpClient client = AndroidHttpClient.newInstance("Beem");
	    return new HttpClientAvatarRetriever(url);
	}
	return new XmppAvatarRetriever(con, from, info.getId());
    }
}
