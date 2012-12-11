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

import com.beem.project.beem.service.aidl.IBeemRosterListener;
import com.beem.project.beem.service.Contact;
import com.beem.project.beem.service.PresenceAdapter;

interface IRoster {

    boolean addContact(in String user, in String name, in String[] groups);

    void deleteContact(in Contact contact);

    Contact getContact(in String jid);
    void setContactName(in String jid, in String name);

    void createGroup(in String groupname);

    void addContactToGroup(in String groupName, in String jid);

    void removeContactFromGroup(in String groupName, in String jid);

    List<Contact> getContactList();

    List<String> getGroupsNames();

    PresenceAdapter getPresence(in String jid);

    void addRosterListener(in IBeemRosterListener listen);
    void removeRosterListener(in IBeemRosterListener listen);

}
