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
package com.beem.project.beem.utils;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Utility class to get a free port.
 * @author nikita
 */
public final class FreePort {

    private static final int MAGIC_10 = 10;
    private static final int MAGIC_10000 = 10000;

    /**
     * Private default constructor.
     */
    private FreePort() {
    }

    /**
     * return a free port.
     * @return free socket port.
     */
    public static int getFreePort() {
	ServerSocket ss;
	int freePort = 0;

	for (int i = 0; i < MAGIC_10; i++) {
	    freePort = (int) (MAGIC_10000 + Math.round(Math.random() * MAGIC_10000));
	    try {
		ss = new ServerSocket(freePort);
		freePort = ss.getLocalPort();
		ss.close();
		return freePort;
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
	try {
	    ss = new ServerSocket(0);
	    freePort = ss.getLocalPort();
	    ss.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return freePort;
    }
}
