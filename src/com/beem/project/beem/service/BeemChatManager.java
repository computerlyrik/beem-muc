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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.java.otr4j.OtrException;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;

import com.beem.project.beem.BeemApplication;
import com.beem.project.beem.BeemService;
import com.beem.project.beem.service.aidl.IChat;
import com.beem.project.beem.service.aidl.IChatManager;
import com.beem.project.beem.service.aidl.IChatManagerListener;
import com.beem.project.beem.service.aidl.IMessageListener;
import com.beem.project.beem.service.aidl.IRoster;
import com.beem.project.beem.utils.Status;

/**
 * An adapter for smack's ChatManager. This class provides functionnality to handle chats.
 * @author darisk
 */
public class BeemChatManager extends IChatManager.Stub {

    private static final String TAG = "BeemChatManager";
    private final ChatManager mAdaptee;
    private final Map<String, ChatAdapter> mChats = new HashMap<String, ChatAdapter>();
    private final ChatListener mChatListener = new ChatListener();
    private final RemoteCallbackList<IChatManagerListener> mRemoteChatCreationListeners =
	new RemoteCallbackList<IChatManagerListener>();
    private final BeemService mService;
    private final ChatRosterListener mChatRosterListn = new ChatRosterListener();

    /**
     * Constructor.
     * @param chatManager the smack ChatManager to adapt
     * @param service the service which runs the chat manager
     * @param roster roster used to get presences changes
     */
    public BeemChatManager(final ChatManager chatManager, final BeemService service, final Roster roster) {
	mService = service;
	mAdaptee = chatManager;
	roster.addRosterListener(mChatRosterListn);
	mAdaptee.addChatListener(mChatListener);
    }

    @Override
    public void addChatCreationListener(IChatManagerListener listener) throws RemoteException {
	if (listener != null)
	    mRemoteChatCreationListeners.register(listener);
    }

    /**
     * Create a chat session.
     * @param contact the contact you want to chat with
     * @param listener listener to use for chat events on this chat session
     * @return the chat session
     */
    @Override
    public IChat createChat(Contact contact, IMessageListener listener) {
	String jid = contact.getJIDWithRes();
	return createChat(jid, listener);
    }

    /**
     * Create a chat session.
     * @param jid the jid of the contact you want to chat with
     * @param listener listener to use for chat events on this chat session
     * @return the chat session
     */
    public IChat createChat(String jid, IMessageListener listener) {
	String key = jid;
	ChatAdapter result;
	if (mChats.containsKey(key)) {
	    result = mChats.get(key);
	    result.addMessageListener(listener);
	    return result;
	}
	Chat c = mAdaptee.createChat(key, null);
	// maybe a little probleme of thread synchronization
	// if so use an HashTable instead of a HashMap for mChats
	result = getChat(c);
	result.addMessageListener(listener);
	return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroyChat(IChat chat) throws RemoteException {
	// Can't remove it. otherwise we will lose all futur message in this chat
	// chat.removeMessageListener(mChatListener);
	if (chat == null)
	    return;
	deleteChatNotification(chat);
	mChats.remove(chat.getParticipant().getJID());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteChatNotification(IChat chat) {
	try {
	    mService.deleteNotification(chat.getParticipant().getJID().hashCode());
	} catch (RemoteException e) {
	    Log.v(TAG, "Remote exception ", e);
	}
    }

    /**
     * Get an existing ChatAdapter or create it if necessary.
     * @param chat The real instance of smack chat
     * @return a chat adapter register in the manager
     */
    private ChatAdapter getChat(Chat chat) {
	String key = chat.getParticipant();
	if (mChats.containsKey(key)) {
	    return mChats.get(key);
	}
	ChatAdapter res = new ChatAdapter(chat);
	boolean history = PreferenceManager.getDefaultSharedPreferences(mService.getBaseContext()).getBoolean(
	    "settings_key_history", false);
	String accountUser = PreferenceManager.getDefaultSharedPreferences(mService.getBaseContext()).getString(
	    BeemApplication.ACCOUNT_USERNAME_KEY, "");
	String historyPath = PreferenceManager.getDefaultSharedPreferences(mService.getBaseContext()).getString(
	    BeemApplication.CHAT_HISTORY_KEY, "");
	if ("".equals(historyPath)) historyPath = "/Android/data/com.beem.project.beem/chat/";
	res.setHistory(history);
	res.setAccountUser(accountUser);
	res.listenOtrSession();
	res.setHistoryPath(new File(Environment.getExternalStorageDirectory(), historyPath));
	Log.d(TAG, "getChat put " + key);
	mChats.put(key, res);
	return res;
    }

    @Override
    public ChatAdapter getChat(Contact contact) {
	String key = contact.getJIDWithRes();
	return mChats.get(key);
    }

    /**
     * This methods permits to retrieve the list of contacts who have an opened chat session with us.
     * @return An List containing Contact instances.
     * @throws RemoteException If a Binder remote-invocation error occurred.
     */
    public List<Contact> getOpenedChatList() throws RemoteException {
	List<Contact> openedChats = new ArrayList<Contact>();
	IRoster mRoster = mService.getBind().getRoster();

	for (ChatAdapter chat : mChats.values()) {
	    if (chat.getMessages().size() > 0) {
		Contact t = mRoster.getContact(chat.getParticipant().getJID());
		if (t == null)
		    t = new Contact(chat.getParticipant().getJID());
		openedChats.add(t);
	    }
	}
	return openedChats;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeChatCreationListener(IChatManagerListener listener) throws RemoteException {
	if (listener != null)
	    mRemoteChatCreationListeners.unregister(listener);
    }

    /**
     * A listener for all the chat creation event that happens on the connection.
     * @author darisk
     */
    private class ChatListener extends IMessageListener.Stub implements ChatManagerListener {

	/**
	 * Constructor.
	 */
	public ChatListener() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void chatCreated(Chat chat, boolean locally) {
	    IChat newchat = getChat(chat);
	    Log.d(TAG, "Chat" + chat.toString() + " created locally " + locally + " with " + chat.getParticipant());
	    try {
		newchat.addMessageListener(mChatListener);
		final int n = mRemoteChatCreationListeners.beginBroadcast();

		for (int i = 0; i < n; i++) {
		    IChatManagerListener listener = mRemoteChatCreationListeners.getBroadcastItem(i);
		    listener.chatCreated(newchat, locally);
		}
		mRemoteChatCreationListeners.finishBroadcast();
	    } catch (RemoteException e) {
		// The RemoteCallbackList will take care of removing the
		// dead listeners.
		Log.w(TAG, " Error while triggering remote connection listeners in chat creation", e);
	    }
	}

	/**
	 * Create the PendingIntent to launch our activity if the user select this chat notification.
	 * @param chat A ChatAdapter instance
	 * @return A Chat activity PendingIntent
	 */
	private PendingIntent makeChatIntent(IChat chat) {
	    Intent chatIntent = new Intent(mService, com.beem.project.beem.ui.Chat.class);
	    chatIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP
		| Intent.FLAG_ACTIVITY_NEW_TASK);
	    try {
		chatIntent.setData(chat.getParticipant().toUri());
	    } catch (RemoteException e) {
		Log.e(TAG, e.getMessage());
	    }
	    PendingIntent contentIntent = PendingIntent.getActivity(mService, 0, chatIntent,
		PendingIntent.FLAG_UPDATE_CURRENT);
	    return contentIntent;
	}

	/**
	 * Set a notification of a new chat.
	 * @param chat The chat to access by the notification
	 * @param msgBody the body of the new message
	 */
	private void notifyNewChat(IChat chat, String msgBody) {
	    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mService);
	    try {
		CharSequence tickerText = mService.getBind().getRoster().getContact(chat.getParticipant().getJID())
		    .getName();
		Notification notification = new Notification(android.R.drawable.stat_notify_chat, tickerText, System
		    .currentTimeMillis());
		notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS;
		notification.setLatestEventInfo(mService, tickerText, msgBody, makeChatIntent(chat));
		mService.sendNotification(chat.getParticipant().getJID().hashCode(), notification);
	    } catch (RemoteException e) {
		Log.e(TAG, e.getMessage());
	    }
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processMessage(final IChat chat, Message message) {
	    try {
		String body = message.getBody();
		if (!chat.isOpen() && body != null) {
		    if (chat instanceof ChatAdapter) {
			mChats.put(chat.getParticipant().getJID(), (ChatAdapter) chat);
		    }
		    notifyNewChat(chat, body);
		}
	    } catch (RemoteException e) {
		Log.e(TAG, e.getMessage());
	    }
	}

	@Override
	public void stateChanged(final IChat chat) {
	}

	@Override
	public void otrStateChanged(String otrState) throws RemoteException {
	    // TODO Auto-generated method stub

	}
    }

    /**
     * implement a roster listener, is used to detect and close otr chats.
     * @author nikita
     *
     */
    private class ChatRosterListener implements RosterListener {

	@Override
	public void entriesAdded(Collection<String> arg0) {
	}

	@Override
	public void entriesDeleted(Collection<String> arg0) {
	}

	@Override
	public void entriesUpdated(Collection<String> arg0) {
	}

	@Override
	public void presenceChanged(Presence presence) {
	    String key = StringUtils.parseBareAddress(presence.getFrom());
	    if (!mChats.containsKey(key)) {
		return;
	    }

	    if (Status.getStatusFromPresence(presence) >= Status.CONTACT_STATUS_DISCONNECT) {
		try {
		    mChats.get(key).localEndOtrSession();
		} catch (OtrException e) {
		    e.printStackTrace();
		}
	    }
	}

    }
}
