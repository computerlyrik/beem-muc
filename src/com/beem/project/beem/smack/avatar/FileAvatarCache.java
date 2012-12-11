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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * An implementation of an AvatarCache which store the data of the filesystem.
 */
public class FileAvatarCache implements AvatarCache {

    private File mStoreDir;

    /**
     * Create a FileAvatarCache.
     *
     * @param storedir The directory used to store the data.
     */
    public FileAvatarCache(final File storedir) {
	if (storedir.exists() && !storedir.isDirectory())
	    throw new IllegalArgumentException("The store directory must be a directory");
	mStoreDir = storedir;
	mStoreDir.mkdirs();
    }

    @Override
    public void put(String key, byte[] data) throws IOException {
	File f = new File(mStoreDir, key);
	OutputStream os = new BufferedOutputStream(new FileOutputStream(f));
	try {
	    os.write(data);
	} finally {
	    os.close();
	}
    }

    @Override
    public void put(String key, InputStream in) throws IOException {
	File f = new File(mStoreDir, key);
	OutputStream os = new BufferedOutputStream(new FileOutputStream(f));
	try {
	    byte[] data = new byte[1024];
	    int nbread;
	    while ((nbread = in.read(data)) != -1)
		    os.write(data, 0, nbread);
	} finally {
	    in.close();
	    os.close();
	}
    }

    @Override
    public byte[] get(String key) throws IOException {
	File f = new File(mStoreDir, key);
	InputStream is = new BufferedInputStream(new FileInputStream(f));
	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	try {
	    byte[] data = new byte[1024];
	    is.read(data);
	    bos.write(data);
	} finally {
	    is.close();
	}
	return bos.toByteArray();
    }

    @Override
    public boolean contains(String key) {
	File f = new File(mStoreDir, key);
	return f.exists();
    }
}
