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

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface for an AvatarCache.
 * This can be improved to a generic cache.
 *
 */
public interface AvatarCache {

    /**
     * Put some datas in cache.
     *
     * @param id the key id of the data
     * @param data the data to cache
     * @throws IOException if an IO error occurs while caching the data
     */
    void put(String id, byte[] data) throws IOException;

    /**
     * Put some datas in cache.
     *
     * @param id the key id of the data
     * @param data an InputStream to the data to cache
     * @throws IOException if an IO error occurs while caching the data
     */
    void put(String id, InputStream data) throws IOException;

    /**
     * Get some data from the cache.
     *
     * @param id the id of the data to get
     * @return the cached data
     * @throws IOException  if an IO error occurs while geting the data
     */
    byte[] get(String id) throws IOException;

    /**
     * Test if a data is in cache.
     *
     * @param id the id of the data
     * @return true if data is in cache false otherwise
     */
    boolean contains(String id);
}
