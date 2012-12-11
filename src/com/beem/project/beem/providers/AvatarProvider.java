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
package com.beem.project.beem.providers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;


/**
 * A simple content provider we expose the differents avatar downloaded.
 *
 */
public class AvatarProvider extends ContentProvider {

    /** The content uri of this provider. */
    public static final Uri CONTENT_URI =
	Uri.parse("content://com.beem.project.beem.providers.avatarprovider");

    /** The MIME type of a CONTENT_URI directory of Beem avatars.  */
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.beem.project.beem.avatar";

    /** The MIME type of a CONTENT_URI subdirectory of a single Beem avatar.  */
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.beem.project.beem.avatar";


    private static final String TAG = AvatarProvider.class.getSimpleName();
    private static final String AUTHORITY = "com.beem.project.beem.providers.avatarprovider";

    private static String[] columnNames = new String[] {Columns.ID, Columns.DATA};

    private static final int AVATAR = 1;
    private static final int AVATAR_ID = 2;
    private static final UriMatcher URIMATCHER = new UriMatcher(AVATAR);

    static
    {
        URIMATCHER.addURI(AUTHORITY, "*", AVATAR_ID);
	// should not be needed if we pass AVATAR on the constructor but it does not work
        URIMATCHER.addURI(AUTHORITY, null, AVATAR);
    }

    private String mDataPath;

    /**
     * Create an AvatarProvider.
     */
    public AvatarProvider() {
    }

    @Override
    public boolean onCreate() {
	File cacheDir = Environment.getExternalStorageDirectory();
	File dataPath = new File(cacheDir, "/Android/data/com.beem.project.beem/cache/avatar");
	dataPath.mkdirs();
	mDataPath = dataPath.getAbsolutePath();
	return true;
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode)
	throws FileNotFoundException {
    	return openFileHelper(uri, mode);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
	MatrixCursor c = new MatrixCursor(columnNames);
	int match = URIMATCHER.match(uri);
	switch (match) {
	    case AVATAR:
		File[] files = new File(mDataPath).listFiles();
		if (files != null) {
		    for (File f : files) {
			c.newRow().add(f.getName()).add(f.getAbsolutePath());
		    }
		}
		break;
	    case AVATAR_ID:
		String id = uri.getPathSegments().get(0);
		File f = new File(mDataPath, id);
		if (f.exists())
			c.newRow().add(f.getName()).add(f.getAbsolutePath());
		break;
	    default:
		Log.w(TAG, "Unsupported uri for query match = " + match);
	}
	if (c != null)
		c.setNotificationUri(getContext().getContentResolver(), uri);
	return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
	int match = URIMATCHER.match(uri);
	String id = null;
	switch (match) {
	    case AVATAR_ID:
		id = uri.getPathSegments().get(0);
		break;
	    default:
		Log.w(TAG, "Unsupported uri for query match = " + match);
	}

	if (id == null)
	    return 0;

	File f = new File(mDataPath, id);
	try {
	    f.createNewFile();
	    getContext().getContentResolver().notifyChange(uri, null);
	    return 1;
	} catch (IOException e) {
	    Log.e(TAG, "Error while creating file", e);
	}
	return 0;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
	int res = 0;
	boolean all = false;
	String id = null;
	int match = URIMATCHER.match(uri);
	switch (match) {
	    case AVATAR_ID:
		id = uri.getPathSegments().get(0);
		break;
	    case AVATAR:
		all = true;
		break;
	    default:
		Log.w(TAG, "Unsupported uri for query match = " + match);
	}
	File[] list = null;
	if (id != null) {
	    list = new File[] {new File(mDataPath, id) };
	} else if (all) {
	    list = new File(mDataPath).listFiles();
	}

	if (list == null)
	    return res;
	for (File data : list) {
	    if (data.exists() && data.delete())
		res++;
	}
	if (res > 0)
	    getContext().getContentResolver().notifyChange(uri, null);
	return res;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
	int match = URIMATCHER.match(uri);
	String id = null;
	Uri result = null;
	switch (match) {
	    case AVATAR:
		id = values.getAsString(Columns.ID);
		result = Uri.withAppendedPath(uri, id);
		break;
	    case AVATAR_ID:
		id = uri.getPathSegments().get(0);
		result = uri;
		break;
	    default:
		Log.w(TAG, "Unsupported uri for query match = " + match);
	}
	if (id == null)
	    return null;

	File f = new File(mDataPath, id);
	try {
	    f.createNewFile();
	    if (result != null)
		getContext().getContentResolver().notifyChange(result, null);
	    return result;
	} catch (IOException e) {
	    Log.e(TAG, "Error while creating file", e);
	}
	return null;
    }

    @Override
    public String getType(Uri uri) {
    	int match = URIMATCHER.match(uri);
    	switch (match) {
    	    case AVATAR:
    		return CONTENT_TYPE;
    	    case AVATAR_ID:
    		return CONTENT_ITEM_TYPE;
    	    default:
    		Log.w(TAG, "Unsupported uri for query match = " + match);
    	}
	return null;
    }

    /**
     * The differents columns available in the AvatarProvider.
     */
    public interface Columns {

	/** The id of the avatar.
	 * type: string */
	String ID = "_id";

	/** The path of the avatar file.
	 * type: string
	 * This field is readonly */
	String DATA = "_data";
    }

}
