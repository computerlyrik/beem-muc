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
import  com.beem.project.beem.service.aidl.IChat;
import  com.beem.project.beem.service.aidl.IMessageListener;
import  com.beem.project.beem.service.aidl.IChatManagerListener;

/**
 * Aidl interface for a chat manager.
 * The chat manager will manage all the chat sessions.
 */
interface IChatManager {

    	/**
    	 * Create a chat session with a contact.
    	 * @param contact	the contact to chat with
    	 * @param listener	the callback to call when a new message comes from this chat session
    	 * @return 		the chat session
    	 */
	IChat createChat(in Contact contact, in IMessageListener listener);

	/**
	 * Get an existing Chat session with a contact.
	 * @return null if the chat session does not exist.
	 */
	IChat getChat(in Contact contact);

	/**
    	 * Destroy a chat session with a contact.
    	 * @param chat	the chat session
    	 */
	void destroyChat(in IChat chat);

        /**
	 * @param chat the chat.
         */
	void deleteChatNotification(in IChat chat);

	/**
	 * Register a callback to call when a new chat session is created.
	 * @param listener	the callback to add
	 */
	void addChatCreationListener(in IChatManagerListener listener);

	/**
	 * Remove a callback for the creation of new chat session.
	 * @param listener	the callback to remove.
	 */
	void removeChatCreationListener(in IChatManagerListener listener);

	/**
	 * Get a list of contact which we are currently chatting.
	 * @return list of contact.
	 */
	List<Contact> getOpenedChatList();
}
