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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jivesoftware.smackx.muc.MultiUserChat; 
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.ChatStateListener;
import org.jivesoftware.smackx.ChatState;

import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;

import com.beem.project.beem.BeemService;
import com.beem.project.beem.service.aidl.IChatMUC;
import com.beem.project.beem.service.aidl.IMessageListener;

/**
 * An adapter for smack's Chat class.
 * @author darisk
 */
public class ChatMUCAdapter extends IChatMUC.Stub {
    private static final int HISTORY_MAX_SIZE = 50;
    private static final String TAG = "ChatMUCAdapter";
    private final  MultiUserChat mAdaptee;
    private final Contact mParticipant;
    private String mState;
    private boolean mIsOpen;
    private String mNick ;
    private final BeemService mService;
    private final List<Message> mMessages;
    private final RemoteCallbackList<IMessageListener> mRemoteListeners = new RemoteCallbackList<IMessageListener>();
    private final MsgMUCListener mMsgListener = new MsgMUCListener();
    
   /**
     * Constructor.
     * @param chat The chat to adapt
     */
    public ChatMUCAdapter(final MultiUserChat chat, final BeemService service, String nick) {
    	mAdaptee = chat;
    	mParticipant = new Contact(chat.getRoom(),true);
    	mMessages = new LinkedList<Message>();
    	mAdaptee.addMessageListener(mMsgListener);
    	mNick = nick ;
    	mService = service;
    	// Join the MUC
		try {
			chat.join(nick) ;
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Contact getRoom() throws RemoteException {
	return mParticipant;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMessage(com.beem.project.beem.service.Message message) throws RemoteException {
	org.jivesoftware.smack.packet.Message send = new org.jivesoftware.smack.packet.Message();
	send.setTo(message.getTo());
	Log.w(TAG, "message to " + message.getTo());
	send.setBody(message.getBody());
	send.setType(org.jivesoftware.smack.packet.Message.Type.groupchat);
	// TODO gerer les messages contenant des XMPPError
	// send.set
	try {
	    mAdaptee.sendMessage(send);
	} catch (XMPPException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addMessageListener(IMessageListener listen) {
    	Log.d(TAG, "Un ecouteur de plus");
	if (listen != null)
	    mRemoteListeners.register(listen);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeMessageListener(IMessageListener listen) {
    	Log.d(TAG, "Un ecouteur de moins");
	if (listen != null) {
	    mRemoteListeners.unregister(listen);
	}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getState() throws RemoteException {
	return mState;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setState(String state) throws RemoteException {
	mState = state;
    }

    /**
     * Get the adaptee for the Chat.
     * @return The real chat object
     */
    public MultiUserChat getAdaptee() {
	return mAdaptee;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOpen(boolean isOpen) {
	this.mIsOpen = isOpen;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isOpen() {
	return mIsOpen;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Message> getMessages() throws RemoteException {
	return Collections.unmodifiableList(mMessages);
    }

    /**
     * Add a message in the chat history.
     * @param msg the message to add
     */
    void addMessage(Message msg) {
	if (mMessages.size() == HISTORY_MAX_SIZE)
	    mMessages.remove(0);
	mMessages.add(msg);
    }

    /**
     * Listener.
     */
    private class MsgMUCListener implements PacketListener {
	/**
	 * Constructor.
	 */
	public MsgMUCListener() { }

	public void processPacket(Packet p) {
		org.jivesoftware.smack.packet.Message message = (org.jivesoftware.smack.packet.Message) p ;
	    Message  msg = new Message(message);
	    //TODO add que les message pas de type errors
	    ChatMUCAdapter.this.addMessage(msg);
	    final int n = mRemoteListeners.beginBroadcast();
	    for (int i = 0; i < n; i++) {
		IMessageListener listener = mRemoteListeners.getBroadcastItem(i);
		try {
		    if (listener != null) {
		    	//HL detection
		    	//If the pseudo
		    	String bodylow = msg.getBody().toLowerCase() ;
		    	if (bodylow.contains(mNick.toLowerCase())) {
		    		msg.setHL(true) ; 
		    	}
		    	//If in the hl list
		    	String hls = PreferenceManager.getDefaultSharedPreferences(mService).getString("settings_key_hls", "") ;
		    	if (! hls.equalsIgnoreCase("")) {
			    	for (String hl : hls.split(",")) {
			    		if (bodylow.contains(hl.toLowerCase())) {
				    		msg.setHL(true) ; 
				    	}
			    	}
		    	}
		    	listener.processMUCMessage(ChatMUCAdapter.this, msg);
		    }
		} catch (RemoteException e) {
		    Log.w(TAG, "Error while diffusing message to listener", e);
		}
	    }
	    mRemoteListeners.finishBroadcast();
	}

    }

	@Override
	public List<Contact> getMembers() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

}
