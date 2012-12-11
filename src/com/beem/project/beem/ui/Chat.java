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
package com.beem.project.beem.ui;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.text.util.Linkify;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.beem.project.beem.BeemApplication;
import com.beem.project.beem.R;
import com.beem.project.beem.providers.AvatarProvider;
import com.beem.project.beem.service.Contact;
import com.beem.project.beem.service.Message;
import com.beem.project.beem.service.PresenceAdapter;
import com.beem.project.beem.service.aidl.IBeemRosterListener;
import com.beem.project.beem.service.aidl.IChat;
import com.beem.project.beem.service.aidl.IChatManager;
import com.beem.project.beem.service.aidl.IChatManagerListener;
import com.beem.project.beem.service.aidl.IMessageListener;
import com.beem.project.beem.service.aidl.IRoster;
import com.beem.project.beem.service.aidl.IXmppFacade;
import com.beem.project.beem.ui.dialogs.builders.ChatList;
import com.beem.project.beem.ui.dialogs.builders.DisplayOtrFingerprint;
import com.beem.project.beem.utils.BeemBroadcastReceiver;
import com.beem.project.beem.utils.Status;

import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.util.StringUtils;


/**
 * This class represents an activity which allows the user to chat with his/her contacts.
 * @author Jean-Manuel Da Silva <dasilvj at beem-project dot com>
 */
public class Chat extends Activity implements TextView.OnEditorActionListener {

    private static final String TAG = "Chat";
    private static final Intent SERVICE_INTENT = new Intent();
    static {
	SERVICE_INTENT.setComponent(new ComponentName("com.beem.project.beem", "com.beem.project.beem.BeemService"));
    }
    private Handler mHandler = new Handler();

    private IRoster mRoster;
    private Contact mContact;

    private TextView mContactNameTextView;
    private TextView mContactStatusMsgTextView;
    private TextView mContactChatState;
    private TextView mContactOtrState;
    private ImageView mContactStatusIcon;
    private LayerDrawable mAvatarStatusDrawable;
    private ListView mMessagesListView;
    private EditText mInputField;
    private Button mSendButton;
    private final Map<Integer, Bitmap> mStatusIconsMap = new HashMap<Integer, Bitmap>();

    private final List<MessageText> mListMessages = new ArrayList<MessageText>();

    private IChat mChat;
    private IChatManager mChatManager;
    private final IMessageListener mMessageListener = new OnMessageListener();
    private final IChatManagerListener mChatManagerListener = new ChatManagerListener();
    private MessagesListAdapter mMessagesListAdapter = new MessagesListAdapter();

    private final ServiceConnection mConn = new BeemServiceConnection();
    private final BeemBroadcastReceiver mBroadcastReceiver = new BeemBroadcastReceiver();
    private final BeemRosterListener mBeemRosterListener = new BeemRosterListener();
    private IXmppFacade mXmppFacade;
    private String mCurrentAvatarId;
    private boolean mBinded;
    private boolean mCompact;

    /**
     * Constructor.
     */
    public Chat() {
	super();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    protected void onCreate(Bundle savedBundle) {
	super.onCreate(savedBundle);
	this.registerReceiver(mBroadcastReceiver, new IntentFilter(BeemBroadcastReceiver.BEEM_CONNECTION_CLOSED));
	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

	if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
	    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
	}

	mCompact = settings.getBoolean(BeemApplication.USE_COMPACT_CHAT_UI_KEY, false);
	// UI
	if (!mCompact) {
	    setContentView(R.layout.chat);
	    mContactNameTextView = (TextView) findViewById(R.id.chat_contact_name);
	    mContactStatusMsgTextView = (TextView) findViewById(R.id.chat_contact_status_msg);
	    mContactChatState = (TextView) findViewById(R.id.chat_contact_chat_state);
	    mContactStatusIcon = (ImageView) findViewById(R.id.chat_contact_status_icon);
	    mAvatarStatusDrawable = (LayerDrawable) mContactStatusIcon.getDrawable();
	    mAvatarStatusDrawable.setLayerInset(1, 36, 36, 0, 0);
	} else {
	    setContentView(R.layout.chat_compact);
	}
	mContactOtrState = (TextView) findViewById(R.id.chat_contact_otr_state);
	mMessagesListView = (ListView) findViewById(R.id.chat_messages);
	mMessagesListView.setAdapter(mMessagesListAdapter);
	mInputField = (EditText) findViewById(R.id.chat_input);
	mInputField.setOnEditorActionListener(this);
	mInputField.requestFocus();
	mSendButton = (Button) findViewById(R.id.chat_send_message);
	mSendButton.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		sendMessage();
	    }
	});

	prepareIconsStatus();
    }

    @Override
    protected void onResume() {
	super.onResume();
	mContact = new Contact(getIntent().getData());
	if (!mBinded) {
	    bindService(SERVICE_INTENT, mConn, BIND_AUTO_CREATE);
	    mBinded = true;
	}
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    protected void onDestroy() {
	super.onDestroy();
	this.unregisterReceiver(mBroadcastReceiver);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    protected void onPause() {
	super.onPause();
	try {
	    if (mChat != null) {
		mChat.setOpen(false);
		mChat.removeMessageListener(mMessageListener);
	    }
	    if (mRoster != null)
		mRoster.removeRosterListener(mBeemRosterListener);
	    if (mChatManager != null)
		mChatManager.removeChatCreationListener(mChatManagerListener);
	} catch (RemoteException e) {
	    Log.e(TAG, e.getMessage());
	}
	if (mBinded) {
	    unbindService(mConn);
	    mBinded = false;
	}
	mXmppFacade = null;
	mRoster = null;
	mChat = null;
	mChatManager = null;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    protected void onNewIntent(Intent intent) {
	super.onNewIntent(intent);
	setIntent(intent);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
	super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
	super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public final boolean onCreateOptionsMenu(Menu menu) {
	super.onCreateOptionsMenu(menu);
	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.chat, menu);
	return true;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public final boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	    case R.id.chat_menu_contacts_list:
		Intent contactListIntent = new Intent(this, ContactList.class);
		contactListIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(contactListIntent);
		break;
	    case R.id.chat_menu_change_chat:
		try {
		    final List<Contact> openedChats = mChatManager.getOpenedChatList();
		    Dialog chatList = new ChatList(Chat.this, openedChats).create();
		    chatList.show();
		} catch (RemoteException e) {
		    Log.e(TAG, e.getMessage());
		}
		break;
	    case R.id.chat_menu_close_chat:
		try {
		    mChatManager.destroyChat(mChat);
		} catch (RemoteException e) {
		    Log.e(TAG, e.getMessage());
		}
		this.finish();
		break;
	    case R.id.chat_menu_start_otr_session:
		try {
		    if (mChat == null) {
			mChat = mChatManager.createChat(mContact, mMessageListener);
			if (mChat != null) {
			    mChat.setOpen(true);
			}
		    }
		    mChat.startOtrSession();
		} catch (RemoteException e) {
		    Log.e(TAG, "start otr chats failed " + mChat, e);
		}
		break;
	    case R.id.chat_menu_stop_otr_session:
		try {
		    if (mChat == null) {
			mChat = mChatManager.createChat(mContact, mMessageListener);
			if (mChat != null) {
			    mChat.setOpen(true);
			}
		    }
		    mChat.endOtrSession();
		} catch (RemoteException e) {
		    Log.e(TAG, "close otr chats failed " + mChat, e);
		}
		break;
	    case R.id.chat_menu_otr_verify_key:
		try {
		    if (mChat == null) {
			mChat = mChatManager.createChat(mContact, mMessageListener);
			if (mChat != null) {
			    mChat.setOpen(true);
			}
		    }
		    Dialog otrDialog = new DisplayOtrFingerprint(this, mChat).create();
		    otrDialog.show();
		} catch (RemoteException e) {
		    Log.e(TAG, "getting local otr key failed " + mChat, e);
		}
		break;
	    default:
		return false;
	}
	return true;
    }

    /**
     * Change the displayed chat.
     * @param contact the targeted contact of the new chat
     * @throws RemoteException If a Binder remote-invocation error occurred.
     */
    private void changeCurrentChat(Contact contact) throws RemoteException {
	if (mChat != null) {
	    mChat.setOpen(false);
	    mChat.removeMessageListener(mMessageListener);
	}
	mChat = mChatManager.getChat(contact);
	if (mChat != null) {
	    mChat.setOpen(true);
	    mChat.addMessageListener(mMessageListener);
	    mChatManager.deleteChatNotification(mChat);
	    updateOtrInformations(mChat.getOtrStatus());
	}
	mContact = mRoster.getContact(contact.getJID());
	String res = contact.getSelectedRes();
	if (mContact == null)
	    mContact = contact;
	if (!"".equals(res)) {
	    mContact.setSelectedRes(res);
	}
	updateContactInformations();
	updateContactStatusIcon();

	playRegisteredTranscript();
    }

    /**
     * Get all messages from the current chat and refresh the activity with them.
     * @throws RemoteException If a Binder remote-invocation error occurred.
     */
    private void playRegisteredTranscript() throws RemoteException {
	mListMessages.clear();
	if (mChat != null) {
	    List<MessageText> msgList = convertMessagesList(mChat.getMessages());
	    mListMessages.addAll(msgList);
	    mMessagesListAdapter.notifyDataSetChanged();
	}
    }

    /**
     * Convert a list of Message coming from the service to a list of MessageText that can be displayed in UI.
     * @param chatMessages the list of Message
     * @return a list of message that can be displayed.
     */
    private List<MessageText> convertMessagesList(List<Message> chatMessages) {
	List<MessageText> result = new ArrayList<MessageText>(chatMessages.size());
	String remoteName = mContact.getName();
	String localName = getString(R.string.chat_self);
	MessageText lastMessage = null;

	for (Message m : chatMessages) {
	    String name = remoteName;
	    String fromBareJid = StringUtils.parseBareAddress(m.getFrom());
	    if (m.getType() == Message.MSG_TYPE_ERROR) {
		lastMessage = null;
		result.add(new MessageText(fromBareJid, name, m.getBody(), true, m.getTimestamp()));
	    } else if  (m.getType() == Message.MSG_TYPE_INFO) {
		lastMessage = new MessageText("", "", m.getBody(), false);
		result.add(lastMessage);

	    } else if (m.getType() == Message.MSG_TYPE_CHAT) {
		if (fromBareJid == null) { //nofrom or from == yours
		    name = localName;
		    fromBareJid = "";
		}

		if (m.getBody() != null) {
		    if (lastMessage == null || !fromBareJid.equals(lastMessage.getBareJid())) {
			lastMessage = new MessageText(fromBareJid, name, m.getBody(), false, m.getTimestamp());
			result.add(lastMessage);
		    } else {
			lastMessage.setMessage(lastMessage.getMessage().concat("\n" + m.getBody()));
		    }
		}
	    }
	}
	return result;
    }


    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	if (v == mInputField && actionId == EditorInfo.IME_ACTION_SEND) {
	    sendMessage();
	    return true;
	}
	return false;
    }

    /**
     * Send an XMPP message.
     */
    private void sendMessage() {
	final String inputContent = mInputField.getText().toString();

	if (!"".equals(inputContent)) {
	    Message msgToSend = new Message(mContact.getJIDWithRes(), Message.MSG_TYPE_CHAT);
	    msgToSend.setBody(inputContent);

	    try {
		if (mChat == null) {
		    mChat = mChatManager.createChat(mContact, mMessageListener);
		    mChat.setOpen(true);
		}
		mChat.sendMessage(msgToSend);
	    } catch (RemoteException e) {
		Log.e(TAG, e.getMessage());
	    }

	    final String self = getString(R.string.chat_self);
	    MessageText lastMessage = null;
	    if (mListMessages.size() != 0)
		lastMessage = mListMessages.get(mListMessages.size() - 1);

	    if (lastMessage != null && lastMessage.getName().equals(self)) {
		lastMessage.setMessage(lastMessage.getMessage().concat("\n" + inputContent));
		lastMessage.setTimestamp(new Date());
	    } else
		mListMessages.add(new MessageText(self, self, inputContent, false, new Date()));
	    mMessagesListAdapter.notifyDataSetChanged();
	    mInputField.setText(null);
	}
    }


    /**
     * Update the contact informations.
     */
    private void updateContactInformations() {
	// Check for a contact name update
	String name = mContact.getName();
	String res = mContact.getSelectedRes();
	if (!"".equals(res))
	    name += "(" + res + ")";
	if (!mCompact) {
	    if (!(mContactNameTextView.getText().toString().equals(name)))
		mContactNameTextView.setText(name);
	    //Check for a contact status message update
	    if (!(mContactStatusMsgTextView.getText().toString().equals(mContact.getMsgState()))) {
		mContactStatusMsgTextView.setText(mContact.getMsgState());
		Linkify.addLinks(mContactStatusMsgTextView, Linkify.WEB_URLS);
	    }
	} else {
	    Mode m = Status.getPresenceModeFromStatus(mContact.getStatus());
	    if (m == null)
		setTitle(getString(R.string.chat_name) + " " + name + " ("
			+ getString(R.string.contact_status_msg_offline) + ")");
	    else
		setTitle(getString(R.string.chat_name) + " " + name + " (" + m.name() + ")");
	}
    }

    /**
     * Update the OTR informations.
     * @param otrState the otr state
     */
    private void updateOtrInformations(final String otrState) {
	String text = null;
	if ("ENCRYPTED".equals(otrState)) {
	    text = Chat.this.getString(R.string.chat_otrstate_encrypted);
	} else if ("FINISHED".equals(otrState)) {
	    text = Chat.this.getString(R.string.chat_otrstate_finished);
	} else if ("AUTHENTICATED".equals(otrState)) {
	    text = Chat.this.getString(R.string.chat_otrstate_authenticated);
	} else {
	    text = Chat.this.getString(R.string.chat_otrstate_plaintext);
	}
	if (mContactOtrState != null)
	    mContactOtrState.setText(text);
    }

    /**
     * Update the contact status icon.
     */
    private void updateContactStatusIcon() {
	if (mCompact)
	    return;
	String id = mContact.getAvatarId();
	if (id == null)
	    id = "";
	Log.d(TAG, "update contact icon  : " + id);
	if (!id.equals(mCurrentAvatarId)) {
	    Drawable avatar = getAvatarDrawable(mContact.getAvatarId());
	    mAvatarStatusDrawable.setDrawableByLayerId(R.id.avatar, avatar);
	    mCurrentAvatarId = id;
	}
	mContactStatusIcon.setImageLevel(mContact.getStatus());
    }

    /**
     * Get a Drawable containing the avatar icon.
     * @param avatarId the avatar id to retrieve or null to get default
     * @return a Drawable
     */
    private Drawable getAvatarDrawable(String avatarId) {
	Drawable avatarDrawable = null;
	if (avatarId != null) {
	    Uri uri = AvatarProvider.CONTENT_URI.buildUpon().appendPath(avatarId).build();
	    InputStream in = null;
	    try {
		try {
		    in = getContentResolver().openInputStream(uri);
		    avatarDrawable = Drawable.createFromStream(in, avatarId);
		} finally {
		    if (in != null)
			in.close();
		}
	    } catch (IOException e) {
		Log.w(TAG, "Error while setting the avatar", e);
	    }
	}
	if (avatarDrawable == null)
	    avatarDrawable = getResources().getDrawable(R.drawable.beem_launcher_icon_silver);
	return avatarDrawable;
    }

    /**
     * Prepare the status icons map.
     */
    private void prepareIconsStatus() {
	mStatusIconsMap.put(Status.CONTACT_STATUS_AVAILABLE,
		BitmapFactory.decodeResource(getResources(), android.R.drawable.presence_online));
	mStatusIconsMap.put(Status.CONTACT_STATUS_AVAILABLE_FOR_CHAT,
		BitmapFactory.decodeResource(getResources(), android.R.drawable.presence_online));
	mStatusIconsMap.put(Status.CONTACT_STATUS_AWAY,
		BitmapFactory.decodeResource(getResources(), android.R.drawable.presence_away));
	mStatusIconsMap.put(Status.CONTACT_STATUS_BUSY,
		BitmapFactory.decodeResource(getResources(), android.R.drawable.presence_busy));
	mStatusIconsMap.put(Status.CONTACT_STATUS_DISCONNECT,
		BitmapFactory.decodeResource(getResources(), android.R.drawable.presence_offline));
	mStatusIconsMap.put(Status.CONTACT_STATUS_UNAVAILABLE,
		BitmapFactory.decodeResource(getResources(), R.drawable.status_requested));
    }


    /**
     * {@inheritDoc}.
     */
    private final class BeemServiceConnection implements ServiceConnection {

	/**
	 * Constructor.
	 */
	public BeemServiceConnection() {
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
	    mXmppFacade = IXmppFacade.Stub.asInterface(service);
	    try {
		mRoster = mXmppFacade.getRoster();
		if (mRoster != null)
		    mRoster.addRosterListener(mBeemRosterListener);
		mChatManager = mXmppFacade.getChatManager();
		if (mChatManager != null) {
		    mChatManager.addChatCreationListener(mChatManagerListener);
		    changeCurrentChat(mContact);
		}
	    } catch (RemoteException e) {
		Log.e(TAG, e.getMessage());
	    }
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public void onServiceDisconnected(ComponentName name) {
	    mXmppFacade = null;
	    try {
		mRoster.removeRosterListener(mBeemRosterListener);
		mChatManager.removeChatCreationListener(mChatManagerListener);
	    } catch (RemoteException e) {
		Log.e(TAG, e.getMessage());
	    }
	}
    }

    /**
     * {@inheritDoc}.
     */
    private class BeemRosterListener extends IBeemRosterListener.Stub {

	/**
	 * Constructor.
	 */
	public BeemRosterListener() {
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public void onEntriesAdded(List<String> addresses) throws RemoteException {
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public void onEntriesDeleted(List<String> addresses) throws RemoteException {
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public void onEntriesUpdated(List<String> addresses) throws RemoteException {
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public void onPresenceChanged(final PresenceAdapter presence) throws RemoteException {
	    if (mContact.getJID().equals(StringUtils.parseBareAddress(presence.getFrom()))) {
		mHandler.post(new Runnable() {
		    @Override
		    public void run() {
			mContact.setStatus(presence.getStatus());
			mContact.setMsgState(presence.getStatusText());
			updateContactInformations();
			updateContactStatusIcon();
		    }
		});
	    }
	}
    }

    /**
     * {@inheritDoc}.
     */
    private class OnMessageListener extends IMessageListener.Stub {

	/**
	 * Constructor.
	 */
	public OnMessageListener() {
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public void processMessage(IChat chat, final Message msg) throws RemoteException {
	    final String fromBareJid = StringUtils.parseBareAddress(msg.getFrom());

	    if (mContact.getJID().equals(fromBareJid)) {
		mHandler.post(new Runnable() {

		    @Override
		    public void run() {
			if (msg.getType() == Message.MSG_TYPE_ERROR) {
			    mListMessages.add(new MessageText(fromBareJid, mContact.getName(), msg.getBody(), true, msg
				.getTimestamp()));
			    mMessagesListAdapter.notifyDataSetChanged();
			} else if (msg.getBody() != null) {
			    MessageText lastMessage = null;
			    if (mListMessages.size() != 0)
				lastMessage = mListMessages.get(mListMessages.size() - 1);

			    if (lastMessage != null && lastMessage.getBareJid().equals(fromBareJid)) {
				lastMessage.setMessage(lastMessage.getMessage().concat("\n" + msg.getBody()));
				lastMessage.setTimestamp(msg.getTimestamp());
				mListMessages.set(mListMessages.size() - 1, lastMessage);
			    } else if (msg.getBody() != null)
				mListMessages.add(new MessageText(fromBareJid, mContact.getName(), msg.getBody(),
				    false, msg.getTimestamp()));
			    mMessagesListAdapter.notifyDataSetChanged();
			}
		    }
		});
	    }
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public void stateChanged(IChat chat) throws RemoteException {
	    final String state = chat.getState();
	    mHandler.post(new Runnable() {
		@Override
		public void run() {
		    String text = null;
		    if ("active".equals(state)) {
			text = Chat.this.getString(R.string.chat_state_active);
		    } else if ("composing".equals(state)) {
			text = Chat.this.getString(R.string.chat_state_composing);
		    } else if ("gone".equals(state)) {
			text = Chat.this.getString(R.string.chat_state_gone);
		    } else if ("inactive".equals(state)) {
			text = Chat.this.getString(R.string.chat_state_inactive);
		    } else if ("paused".equals(state)) {
			text = Chat.this.getString(R.string.chat_state_active);
		    }
		    if (!mCompact)
			mContactChatState.setText(text);
		}
	    });

	}

	@Override
	public void otrStateChanged(final String otrState) throws RemoteException {
	    mHandler.post(new Runnable() {
		@Override
		public void run() {
		    updateOtrInformations(otrState);
		    mListMessages.add(new MessageText("", "", otrState, false));
		    mMessagesListAdapter.notifyDataSetChanged();
		}
	    });

	}
    }

    /**
     * {@inheritDoc}.
     */
    private class MessagesListAdapter extends BaseAdapter {

	/**
	 * Constructor.
	 */
	public MessagesListAdapter() {
	}

	/**
	 * Returns the number of messages contained in the messages list.
	 * @return The number of messages contained in the messages list.
	 */
	@Override
	public int getCount() {
	    return mListMessages.size();
	}

	/**
	 * Return an item from the messages list that is positioned at the position passed by parameter.
	 * @param position The position of the requested item.
	 * @return The item from the messages list at the requested position.
	 */
	@Override
	public Object getItem(int position) {
	    return mListMessages.get(position);
	}

	/**
	 * Return the id of an item from the messages list that is positioned at the position passed by parameter.
	 * @param position The position of the requested item.
	 * @return The id of an item from the messages list at the requested position.
	 */
	@Override
	public long getItemId(int position) {
	    return position;
	}

	/**
	 * Return the view of an item from the messages list.
	 * @param position The position of the requested item.
	 * @param convertView The old view to reuse if possible.
	 * @param parent The parent that this view will eventually be attached to.
	 * @return A View corresponding to the data at the specified position.
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
	    View sv;
	    if (convertView == null) {
		LayoutInflater inflater = Chat.this.getLayoutInflater();
		sv = inflater.inflate(R.layout.chat_msg_row, null);
	    } else {
		sv = convertView;
	    }
	    DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
	    MessageText msg = mListMessages.get(position);
	    TextView msgName = (TextView) sv.findViewById(R.id.chatmessagename);
	    msgName.setText(msg.getName());
	    msgName.setTextColor(Color.WHITE);
	    msgName.setError(null);
	    TextView msgText = (TextView) sv.findViewById(R.id.chatmessagetext);
	    msgText.setText(msg.getMessage());
	    registerForContextMenu(msgText);
	    TextView msgDate = (TextView) sv.findViewById(R.id.chatmessagedate);
	    if (msg.getTimestamp() != null) {
		String date = df.format(msg.getTimestamp());
		msgDate.setText(date);
	    }
	    if (msg.isError()) {
		String err = getString(R.string.chat_error);
		msgName.setText(err);
		msgName.setTextColor(Color.RED);
		msgName.setError(err);
	    }
	    return sv;
	}
    }

    /**
     * Class which simplify an Xmpp text message.
     * @author Jean-Manuel Da Silva <dasilvj at beem-project dot com>
     */
    private class MessageText {
	private String mBareJid;
	private String mName;
	private String mMessage;
	private boolean mIsError;
	private Date mTimestamp;

	/**
	 * Constructor.
	 * @param bareJid A String containing the bare JID of the message's author.
	 * @param name A String containing the name of the message's author.
	 * @param message A String containing the message.
	 */
	public MessageText(final String bareJid, final String name, final String message) {
	    mBareJid = bareJid;
	    mName = name;
	    mMessage = message;
	    mIsError = false;
	}

	/**
	 * Constructor.
	 * @param bareJid A String containing the bare JID of the message's author.
	 * @param name A String containing the name of the message's author.
	 * @param message A String containing the message.
	 * @param isError if the message is an error message.
	 */
	public MessageText(final String bareJid, final String name, final String message, final boolean isError) {
	    mBareJid = bareJid;
	    mName = name;
	    mMessage = message;
	    mIsError = isError;
	}

	/**
	 * Constructor.
	 * @param bareJid A String containing the bare JID of the message's author.
	 * @param name A String containing the name of the message's author.
	 * @param message A String containing the message.
	 * @param isError if the message is an error message.
	 * @param date the time of the message.
	 */
	public MessageText(final String bareJid, final String name, final String message, final boolean isError,
	    final Date date) {
	    mBareJid = bareJid;
	    mName = name;
	    mMessage = message;
	    mIsError = isError;
	    mTimestamp = date;
	}

	/**
	 * JID attribute accessor.
	 * @return A String containing the bare JID of the message's author.
	 */
	public String getBareJid() {
	    return mBareJid;
	}

	/**
	 * Name attribute accessor.
	 * @return A String containing the name of the message's author.
	 */
	public String getName() {
	    return mName;
	}

	/**
	 * Message attribute accessor.
	 * @return A String containing the message.
	 */
	public String getMessage() {
	    return mMessage;
	}

	/**
	 * JID attribute mutator.
	 * @param bareJid A String containing the author's bare JID of the message.
	 */
	@SuppressWarnings("unused")
	public void setBareJid(String bareJid) {
	    mBareJid = bareJid;
	}

	/**
	 * Name attribute mutator.
	 * @param name A String containing the author's name of the message.
	 */
	@SuppressWarnings("unused")
	public void setName(String name) {
	    mName = name;
	}

	/**
	 * Message attribute mutator.
	 * @param message A String containing a message.
	 */
	public void setMessage(String message) {
	    mMessage = message;
	}

	/**
	 * Get the message type.
	 * @return true if the message is an error message.
	 */
	public boolean isError() {
	    return mIsError;
	}

	/**
	 * Set the Date of the message.
	 * @param date date of the message.
	 */
	public void setTimestamp(Date date) {
	    mTimestamp = date;
	}

	/**
	 * Get the Date of the message.
	 * @return if it is a delayed message get the date the message was sended.
	 */
	public Date getTimestamp() {
	    return mTimestamp;
	}

    }

    /**
     * This class is in charge of getting the new chat in the activity if someone talk to you.
     */
    private class ChatManagerListener extends IChatManagerListener.Stub {

	/**
	 * Constructor.
	 */
	public ChatManagerListener() {
	}

	@Override
	public void chatCreated(IChat chat, boolean locally) {
	    if (locally)
		return;
	    try {
		String contactJid = mContact.getJIDWithRes();
		String chatJid = chat.getParticipant().getJIDWithRes();
		if (chatJid.equals(contactJid)) {
		    // This should not be happened but to be sure
		    if (mChat != null) {
			mChat.setOpen(false);
			mChat.removeMessageListener(mMessageListener);
		    }
		    mChat = chat;
		    mChat.setOpen(true);
		    mChat.addMessageListener(mMessageListener);
		    mChatManager.deleteChatNotification(mChat);
		}
	    } catch (RemoteException ex) {
		Log.e(TAG, "A remote exception occurs during the creation of a chat", ex);
	    }
	}
    }
}
