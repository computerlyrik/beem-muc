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
package com.beem.project.beem.service.aidl;

import  com.beem.project.beem.service.Contact;
import  com.beem.project.beem.service.Message;
import  com.beem.project.beem.service.aidl.IMessageListener;

/**
 * An aidl interface for Chat session.
 */
interface IChat {

    	/**
    	 * Send a message.
    	 * @param message	the message to send
    	 */
	void sendMessage(in Message message);

	/**
	 * Get the participant of the chat
	 * @return the participant
	 */
	Contact getParticipant();

	/**
	 * Add a message listener.
	 * @param listener the listener to add.
	 */
	void addMessageListener(in IMessageListener listener);

	/**
	 * Remove a message listener.
	 * @param listener the listener to remove.
	 */
	void removeMessageListener(in IMessageListener listener);

	String getState();

	void setOpen(in boolean isOpen);

	boolean isOpen();

	void setState(in String state);

	List<Message> getMessages();
	
	/**
	 * Try to start an OTR session.
	 */
	void startOtrSession();
	
	/**
	 * Stop the OTR session.
	 */
	void endOtrSession();
	
	/**
	 * get local OTR key fingerprints.
	 */
	String getLocalOtrFingerprint();
	
	
	/**
	 * get remote OTR key fingerprints.
	 */
	String getRemoteOtrFingerprint();
	
	void verifyRemoteFingerprint(in boolean ok);
	
	
	/**
	 * get current OTR status.
	 */
	String getOtrStatus();
	

}
