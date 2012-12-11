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

*/
package com.beem.project.beem.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.io.InputStream;
import java.io.IOException;

import org.jivesoftware.smack.util.StringUtils;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

import com.beem.project.beem.R;
import com.beem.project.beem.BeemApplication;
import com.beem.project.beem.providers.AvatarProvider;
import com.beem.project.beem.service.Contact;
import com.beem.project.beem.service.PresenceAdapter;
import com.beem.project.beem.service.aidl.IBeemRosterListener;
import com.beem.project.beem.service.aidl.IChatManager;
import com.beem.project.beem.service.aidl.IRoster;
import com.beem.project.beem.service.aidl.IXmppFacade;
import com.beem.project.beem.ui.dialogs.builders.Alias;
import com.beem.project.beem.ui.dialogs.builders.ChatList;
import com.beem.project.beem.ui.dialogs.builders.DeleteContact;
import com.beem.project.beem.ui.dialogs.builders.ResendSubscription;
import com.beem.project.beem.utils.BeemBroadcastReceiver;
import com.beem.project.beem.utils.SortedList;
import com.beem.project.beem.utils.Status;

/**
 * The contact list activity displays the roster of the user.
 */
public class ContactList extends Activity {

    private static final Intent SERVICE_INTENT = new Intent();
    static {
	SERVICE_INTENT.setComponent(new ComponentName("com.beem.project.beem", "com.beem.project.beem.BeemService"));
    }

    private static final String TAG = "ContactList";
    private final BeemContactList mAdapterContactList = new BeemContactList();
    private final List<String> mListGroup = new ArrayList<String>();

    /** Map containing a list of the different contacts of a given group.
     * Each list is a @{link SortedList} so there is no need to sort it again.
     * */
    private final Map<String, List<Contact>> mContactOnGroup = new HashMap<String, List<Contact>>();
    private final BeemContactListOnClick mOnContactClick = new BeemContactListOnClick();
    private final Handler mHandler = new Handler();
    private final ServiceConnection mServConn = new BeemServiceConnection();
    private final BeemBroadcastReceiver mReceiver = new BeemBroadcastReceiver();
    private final ComparatorContactListByStatusAndName<Contact> mComparator =
	new ComparatorContactListByStatusAndName<Contact>();
    private final BeemRosterListener mBeemRosterListener = new BeemRosterListener();
    private List<Contact> mListContact;
    private String mSelectedGroup;
    private IRoster mRoster;
    private Contact mSelectedContact;
    private IXmppFacade mXmppFacade;
    private IChatManager mChatManager;
    private SharedPreferences mSettings;
    private LayoutInflater mInflater;
    private BeemBanner mAdapterBanner;
    private boolean mBinded;

    /**
     * Constructor.
     */
    public ContactList() {
    }

    /**
     * Callback for menu creation.
     * @param menu the menu created
     * @return true on success, false otherwise
     */
    @Override
    public final boolean onCreateOptionsMenu(Menu menu) {
	super.onCreateOptionsMenu(menu);
	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.contact_list, menu);
	return true;
    }

    @Override
    public final boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	    case R.id.contact_list_menu_settings:
		startActivity(new Intent(this, Settings.class));
		return true;
	    case R.id.contact_list_menu_add_contact:
		startActivity(new Intent(ContactList.this, AddContact.class));
		return true;
	    case R.id.menu_change_status:
		startActivity(new Intent(ContactList.this, ChangeStatus.class));
		return true;
	    case R.id.contact_list_menu_chatlist:
		List<Contact> openedChats;
		try {
		    openedChats = mChatManager.getOpenedChatList();
		    Log.d(TAG, "opened chats = " + openedChats);
		    Dialog chatList = new ChatList(ContactList.this, openedChats).create();
		    chatList.show();
		} catch (RemoteException e) {
		    e.printStackTrace();
		}
		return true;
	    case R.id.menu_disconnect:
		stopService(SERVICE_INTENT);
		finish();
		return true;
	    default:
		return false;
	}
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
	super.onCreateContextMenu(menu, v, menuInfo);
	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.contactlist_context, menu);
	AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
	Contact c = mListContact.get(info.position);
	try {
	    mSelectedContact = mRoster.getContact(c.getJID());
	} catch (RemoteException e) {
	    e.printStackTrace();
	}
	menu.setHeaderTitle(mSelectedContact.getJID());
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
	Intent in;
	boolean result;
	if (mSelectedContact != null) {
	    switch (item.getItemId()) {
		case R.id.contact_list_context_menu_chat_item:
		    List<String> res = mSelectedContact.getMRes();
		    if (res.isEmpty()) {
			result = false;
			break;
		    }
		    for (String resv : res) {
			in = new Intent(this, Chat.class);
			in.setData(mSelectedContact.toUri(resv));
			item.getSubMenu().add(resv).setIntent(in);
		    }
		    result = true;
		    break;
		case R.id.contact_list_context_menu_call_item:
		    try {
			mXmppFacade.call(mSelectedContact.getJID() + "/psi");
			result = true;
		    } catch (RemoteException e) {
			e.printStackTrace();
		    }
		    result = true;
		    break;
		case R.id.contact_list_context_menu_user_info:
		    item.getSubMenu().setHeaderTitle(mSelectedContact.getJID());
		    result = true;
		    break;
		case R.id.contact_list_context_menu_userinfo_alias:
		    Dialog alias = new Alias(ContactList.this, mRoster, mSelectedContact).create();
		    alias.show();
		    result = true;
		    break;
		case R.id.contact_list_context_menu_userinfo_group:
		    in = new Intent(this, GroupList.class);
		    in.putExtra("contact", mSelectedContact);
		    startActivity(in);
		    result = true;
		    break;
		case R.id.contact_list_context_menu_userinfo_subscription:
		    Dialog subscription = new ResendSubscription(ContactList.this,
			mXmppFacade, mSelectedContact).create();
		    subscription.show();
		    result = true;
		    break;
		case R.id.contact_list_context_menu_userinfo_block:
		    result = true;
		    break;
		case R.id.contact_list_context_menu_userinfo_delete:
		    Dialog delete = new DeleteContact(ContactList.this, mRoster, mSelectedContact).create();
		    delete.show();
		    result = true;
		    break;
		default:
		    result = super.onContextItemSelected(item);
		    break;
	    }
	    return result;
	}
	return super.onContextItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle saveBundle) {
	super.onCreate(saveBundle);
	mSettings = PreferenceManager.getDefaultSharedPreferences(this);
	setContentView(R.layout.contactlist);

	this.registerReceiver(mReceiver, new IntentFilter(BeemBroadcastReceiver.BEEM_CONNECTION_CLOSED));

	mInflater = getLayoutInflater();
	mAdapterBanner = new BeemBanner(mInflater, mListGroup);
	mListContact = new ArrayList<Contact>();
	ListView listView = (ListView) findViewById(R.id.contactlist);
	listView.setOnItemClickListener(mOnContactClick);
	registerForContextMenu(listView);
	listView.setAdapter(mAdapterContactList);
    }

    @Override
    protected void onResume() {
	super.onResume();
	if (!mBinded)
	    mBinded = bindService(SERVICE_INTENT, mServConn, BIND_AUTO_CREATE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPause() {
	super.onPause();
	try {
	    if (mRoster != null) {
		mRoster.removeRosterListener(mBeemRosterListener);
		mRoster = null;
	    }
	} catch (RemoteException e) {
	    Log.d("ContactList", "Remote exception", e);
	}
	if (mBinded) {
	    unbindService(mServConn);
	    mBinded = false;
	}
	mXmppFacade = null;
    }

    @Override
    protected void onDestroy() {
	super.onDestroy();
	this.unregisterReceiver(mReceiver);
	Log.e(TAG, "onDestroy activity");
    }

    /**
     * Build and display the contact list.
     * @param group name of the contact list.
     */
    private void buildContactList(String group) {
	mListContact = mContactOnGroup.get(group);
	mSelectedGroup = group;
	Log.d(TAG, "buildContactList for group " + group);
	mAdapterContactList.notifyDataSetChanged();
    }

    /**
     * Show the groups view.
     */
    private void showGroups() {

	ViewStub stub = (ViewStub) findViewById(R.id.contactlist_stub);
	if (stub != null) {
	    View v = stub.inflate();
	    Gallery g = (Gallery) v.findViewById(R.id.contactlist_banner);
	    g.setOnItemClickListener(new OnItemClickGroupName());
	    g.setAdapter(mAdapterBanner);
	    g.setSelection(0);
	} else {
	    ((LinearLayout) findViewById(R.id.contactlist_groupstub)).setVisibility(View.VISIBLE);
	    Gallery g = (Gallery) findViewById(R.id.contactlist_banner);
	    g.setSelection(0);
	}
    }

    /**
     * Hide the groups view.
     */
    private void hideGroups() {
	View v = findViewById(R.id.contactlist_groupstub);
	if (v != null)
	    v.setVisibility(View.GONE);
    }

    /**
     * Listener on service event.
     */
    private class BeemRosterListener extends IBeemRosterListener.Stub {
	/**
	 * Constructor.
	 */
	public BeemRosterListener() {
	}

	/**
	 * {@inheritDoc}
	 * Simple stategy to handle the onEntriesAdded event.
	 * if contact has to be shown :
	 * <ul>
	 * <li> add him to his groups</li>
	 * <li> add him to the specials groups</>
	 * </ul>
	 */
	@Override
	public void onEntriesAdded(final List<String> addresses) throws RemoteException {
	    final boolean hideDisconnected = mSettings.getBoolean(BeemApplication.SHOW_OFFLINE_CONTACTS_KEY, false);
	    for (String newName : addresses) {
		Contact contact = mRoster.getContact(newName);
		boolean visible = !hideDisconnected || Status.statusOnline(contact.getStatus());
		List<String> groups = contact.getGroups();
		if (visible) {
		    for (String group : groups) {
			if (!mListGroup.contains(group)) {
			    mListGroup.add(mListGroup.size() - 1, group);
			    List<Contact> tmplist = new SortedList<Contact>(new LinkedList<Contact>(), mComparator);
			    mContactOnGroup.put(group, tmplist);
			}
			List<Contact> contactByGroups = mContactOnGroup.get(group);
			if (mSelectedGroup.equals(group)) {
			    updateCurrentList(group, contact);
			    continue;
			}
			contactByGroups.add(contact);
		    }

		    // add the contact to all and no groups
		    addToSpecialList(contact);
		}
	    }
	}

	/**
	 * {@inheritDoc}
	 * Simple stategy to handle the onEntriesDeleted event.
	 * <ul>
	 * <li> Remove the contact from all groups</li>
	 * </ul>
	 */
	@Override
	public void onEntriesDeleted(final List<String> addresses) throws RemoteException {
	    Log.d(TAG, "onEntries deleted " + addresses);
	    for (String cToDelete : addresses) {
		Contact contact = new Contact(cToDelete);
		for (Map.Entry<String, List<Contact>> entry : mContactOnGroup.entrySet()) {
		    List<Contact> contactByGroups = entry.getValue();
		    if (mSelectedGroup.equals(entry.getKey())) {
			updateCurrentList(entry.getKey(), contact);
			continue;
		    }
		    contactByGroups.remove(contact);
		}
		cleanBannerGroup();
	    }

	    mHandler.post(new Runnable() {
		public void run() {
		    mSelectedGroup = getString(R.string.contact_list_all_contact);
		    mListContact = mContactOnGroup.get(mSelectedGroup);

		    mAdapterContactList.notifyDataSetChanged();
		}
	    });

	}

	/**
	 * {@inheritDoc}
	 * Simple stategy to handle the onEntriesUpdated event.
	 * <ul>
	 * <li> Remove the contact from all groups</li>
	 * <li> if contact has to be shown add it to his groups</li>
	 * <li> if contact has to be shown add it to the specials groups</li>
	 * </ul>
	 */
	@Override
	public void onEntriesUpdated(final List<String> addresses) throws RemoteException {
	    final boolean hideDisconnected = mSettings.getBoolean(BeemApplication.SHOW_OFFLINE_CONTACTS_KEY, false);
	    for (String adr : addresses) {
		Contact contact = mRoster.getContact(adr);
		boolean visible = !hideDisconnected || Status.statusOnline(contact.getStatus());
		List<String> groups = contact.getGroups();
		for (Map.Entry<String, List<Contact>> entry : mContactOnGroup.entrySet()) {
		    List<Contact> contactByGroups = entry.getValue();
		    if (mSelectedGroup.equals(entry.getKey())) {
			updateCurrentList(entry.getKey(), contact);
			continue;
		    }
		    contactByGroups.remove(contact);
		    if (visible) {
			for (String group : groups) {
			    if (!mListGroup.contains(group)) {
				mListGroup.add(mListGroup.size() - 1, group);
				List<Contact> tmplist = new SortedList<Contact>(
				    new LinkedList<Contact>(), mComparator);
				mContactOnGroup.put(group, tmplist);
			    }
			    mContactOnGroup.get(group).remove(contact);
			}
		    }

		}

		// add the contact to all and no groups
		if (visible) {
		    addToSpecialList(contact);
		}
	    }
	    cleanBannerGroup();
	}

	/**
	 * {@inheritDoc}
	 * Simple stategy to handle the onPresenceChanged event.
	 * <ul>
	 * <li> Remove the contact from all groups</li>
	 * <li> if contact has to be shown add it to his groups</li>
	 * <li> if contact has to be shown add it to the specials groups</li>
	 * </ul>
	 */
	@Override
	public void onPresenceChanged(PresenceAdapter presence) throws RemoteException {
	    String from = presence.getFrom();
	    final boolean hideDisconnected = mSettings.getBoolean(BeemApplication.SHOW_OFFLINE_CONTACTS_KEY, false);
	    final Contact contact = mRoster.getContact(StringUtils.parseBareAddress(from));
	    boolean visible = !hideDisconnected || Status.statusOnline(contact.getStatus());
	    List<String> groups = contact.getGroups();
	    for (Map.Entry<String, List<Contact>> entry : mContactOnGroup.entrySet()) {
		List<Contact> contactByGroups = entry.getValue();
		if (mSelectedGroup.equals(entry.getKey())) {
		    updateCurrentList(entry.getKey(), contact);
		    continue;
		}
		contactByGroups.remove(contact);
		if (visible) {
		    if (groups.contains(entry.getKey())) {
			contactByGroups.add(contact);
		    }
		}
	    }
	    if (visible) {
		addToSpecialList(contact);
	    }
	}

	/**
	 * Add a contact to the special list No Group and All contacts.
	 * The contact will be added if the list is not the current list otherwise
	 * the list must be modified in a Handler.
	 *
	 * @param contact the contact to add.
	 */
	private void addToSpecialList(Contact contact) {
	    List<String> groups = contact.getGroups();
	    List<Contact> list = mContactOnGroup.get(getString(R.string.contact_list_all_contact));
	    if (list != mListContact) {
		list.add(contact);
	    }
	    list = mContactOnGroup.get(getString(R.string.contact_list_no_group));
	    if (list != mListContact && groups.isEmpty()) {
		list.add(contact);
	    }
	}

	/**
	 * Update the current list with the status of contact.
	 *
	 * @param listName name of the current list
	 * @param contact contact to update
	 */
	private void updateCurrentList(String listName, final Contact contact) {
	    final boolean hideDisconnected = mSettings.getBoolean(BeemApplication.SHOW_OFFLINE_CONTACTS_KEY, false);
	    final List<String> groups = contact.getGroups();
	    String noGroup = getString(R.string.contact_list_no_group);
	    String allGroup = getString(R.string.contact_list_all_contact);
	    final boolean add = ((!hideDisconnected || Status.statusOnline(contact.getStatus())) &&	// must show and
		(
		    (listName.equals(noGroup) && groups.isEmpty()) ||			// in no group
		    groups.contains(listName) ||					// or in current
		    listName.equals(allGroup)						// or in all
		));
	    mHandler.post(new Runnable() {
		public void run() {
		    mListContact.remove(contact);
		    if (add) {
			mListContact.add(contact);
		    }
		    mAdapterContactList.notifyDataSetChanged();
		}
	    });

	}

	/**
	 * Remove old groups on the banner.
	 * @throws RemoteException if an error occur when communicating with the service
	 */
	private void cleanBannerGroup() throws RemoteException {
	    List<String> rosterGroups = mRoster.getGroupsNames();
	    List<String> realGroups = mListGroup.subList(1, mListContact.size() - 1);
	    realGroups.retainAll(rosterGroups);
	}

    }

    /**
     * Adapter contact list.
     */
    private class BeemContactList extends BaseAdapter implements Filterable {

	private final ContactFilter mFilter;

	/**
	 * Constructor.
	 */
	public BeemContactList() {
	    mFilter = new ContactFilter();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getCount() {
	    return mListContact.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getItem(int position) {
	    return mListContact.get(position);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getItemId(int position) {
	    return mListContact.get(position).hashCode();
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    View v = convertView;
	    if (convertView == null) {
		v = mInflater.inflate(R.layout.contactlistcontact, null);
	    }
	    Contact c = mListContact.get(position);
	    if (mRoster != null) {
		try {
		    c = mRoster.getContact(c.getJID());
		} catch (RemoteException e) {
		    e.printStackTrace();
		}
	    }
	    bindView(v, c);
	    return v;
	}

	@Override
	public Filter getFilter() {
	    return mFilter;
	}

	/**
	 * Adapte curContact to the view.
	 * @param view the row view.
	 * @param curContact the current contact.
	 */
	private void bindView(View view, Contact curContact) {
	    if (curContact != null) {
		TextView v = (TextView) view.findViewById(R.id.contactlistpseudo);
		v.setText(curContact.getName());
		v = (TextView) view.findViewById(R.id.contactlistmsgperso);
		v.setText(curContact.getMsgState());
		ImageView img = (ImageView) view.findViewById(R.id.avatar);
		String avatarId = curContact.getAvatarId();
		int contactStatus = curContact.getStatus();
		Drawable avatar = getAvatarStatusDrawable(avatarId);
		img.setImageDrawable(avatar);
		img.setImageLevel(contactStatus);
	    }
	}

	/**
	 * Get a LayerDrawable containing the avatar and the status icon.
	 * The status icon will change with the level of the drawable.
	 * @param avatarId the avatar id to retrieve or null to get default
	 * @return a LayerDrawable
	 */
	private Drawable getAvatarStatusDrawable(String avatarId) {
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
		    Log.w(TAG, "Error while setting the avatar " + avatarId, e);
		}
	    }
	    if (avatarDrawable == null)
		avatarDrawable = getResources().getDrawable(R.drawable.beem_launcher_icon_silver);
	    LayerDrawable ld = (LayerDrawable) getResources().getDrawable(R.drawable.avatar_status);
	    ld.setLayerInset(1, 36, 36, 0, 0);
	    ld.setDrawableByLayerId(R.id.avatar, avatarDrawable);
	    return ld;
	}

	/**
	 * A Filter which select Contact to display by searching in ther Jid.
	 */
	private class ContactFilter extends Filter {

	    /**
	     * Create a ContactFilter.
	     */
	    public ContactFilter() { }

	    @Override
	    protected Filter.FilterResults performFiltering(CharSequence constraint) {
		Log.d(TAG, "performFiltering");
		List<Contact> result = mListContact;
		if (constraint.length() > 0) {
		    result = new LinkedList<Contact>();
		    for (Contact c : mContactOnGroup.get(mSelectedGroup)) {
			if (c.getJID().contains(constraint))
			    result.add(c);
		    }
		}
		Filter.FilterResults fr = new Filter.FilterResults();
		fr.values = result;
		fr.count = result.size();
		return fr;
	    }

	    @Override
	    protected void publishResults(CharSequence constraint, Filter.FilterResults  results) {
		Log.d(TAG, "publishResults");
		List<Contact> contacts = (List<Contact>) results.values;
		mListContact = contacts;
		notifyDataSetChanged();
	    }
	}
    }

    /**
     * Adapter banner list.
     */
    private static class BeemBanner extends BaseAdapter {
	private List<String> mGroups;
	private LayoutInflater mInflater;

	/**
	 * Constructor.
	 * @param inflater the inflater use to create the view for the banner
	 * @param groups list of the differents groups to adapt
	 */
	public BeemBanner(final LayoutInflater inflater, final List<String> groups) {
	    mGroups = groups;
	    mInflater = inflater;
	}

	@Override
	public int getCount() {
	    return mGroups.size();
	}

	@Override
	public Object getItem(int position) {
	    return mGroups.get(position);
	}

	@Override
	public long getItemId(int position) {
	    return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    View v = convertView;
	    if (convertView == null) {
		v = mInflater.inflate(R.layout.contactlist_group, null);
	    }
	    ((TextView) v).setText(mGroups.get(position));
	    return v;
	}
    }

    /**
     * The service connection used to connect to the Beem service.
     */
    private class BeemServiceConnection implements ServiceConnection {

	/**
	 * Constructor.
	 */
	public BeemServiceConnection() {
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
	    mXmppFacade = IXmppFacade.Stub.asInterface(service);
	    try {
		mRoster = mXmppFacade.getRoster();
		if (mRoster != null) {
		    List<String> tmpGroupList = mRoster.getGroupsNames();
		    Collections.sort(tmpGroupList);
		    mListGroup.clear();
		    mListGroup.add(getString(R.string.contact_list_all_contact));
		    mListGroup.addAll(tmpGroupList);
		    mListGroup.add(getString(R.string.contact_list_no_group));
		    assignContactToGroups(mRoster.getContactList(), tmpGroupList);
		    makeSortedList(mContactOnGroup);
		    if (!mSettings.getBoolean(BeemApplication.HIDE_GROUPS_KEY, false))
			showGroups();
		    else
			hideGroups();
		    String group = getString(R.string.contact_list_all_contact);
		    buildContactList(group);
		    mRoster.addRosterListener(mBeemRosterListener);
		    Log.d(TAG, "add roster listener");
		    mChatManager = mXmppFacade.getChatManager();
		}
	    } catch (RemoteException e) {
		e.printStackTrace();
	    }
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
	    try {
		mRoster.removeRosterListener(mBeemRosterListener);
	    } catch (RemoteException e) {
		e.printStackTrace();
	    }
	    mXmppFacade = null;
	    mChatManager = null;
	    mRoster = null;
	    mListContact.clear();
	    mListGroup.clear();
	    mContactOnGroup.clear();
	    mBinded = false;
	}

	/**
	 * Assign the differents contact to their groups.
	 * This methods will fill the mContactOnGroup map.
	 *
	 * @param contacts list of contacts
	 * @param groupNames list of existing groups
	 */
	private void assignContactToGroups(List<Contact> contacts, List<String> groupNames) {
	    boolean hideDisconnected = mSettings.getBoolean(BeemApplication.SHOW_OFFLINE_CONTACTS_KEY, false);
	    mContactOnGroup.clear();
	    List<Contact> all = new LinkedList<Contact>();
	    List<Contact> noGroups = new LinkedList<Contact>();
	    for (String group : groupNames) {
		mContactOnGroup.put(group, new LinkedList<Contact>());
	    }
	    for (Contact c : contacts) {
		if (hideDisconnected && !Status.statusOnline(c.getStatus())) {
		    continue;
		}
		all.add(c);
		List<String> groups = c.getGroups();
		if (groups.isEmpty())
		    noGroups.add(c);
		else {
		    for (String currentGroup : groups) {
			List<Contact> contactsByGroups = mContactOnGroup.get(currentGroup);
			contactsByGroups.add(c);
		    }
		}
	    }
	    mContactOnGroup.put(getString(R.string.contact_list_no_group), noGroups);
	    mContactOnGroup.put(getString(R.string.contact_list_all_contact), all);
	}

	/**
	 * Make the List of the map became Insertion sorted list.
	 *
	 * @param map the map to convert.
	 */
	private void makeSortedList(Map<String, List<Contact>> map) {
	    for (Map.Entry<String, List<Contact>> entry : map.entrySet()) {
		List<Contact> l = entry.getValue();
		entry.setValue(new SortedList<Contact>(l, mComparator));
	    }
	}
    }




    /**
     * Comparator Contact by status and name.
     */
    private static class ComparatorContactListByStatusAndName<T> implements Comparator<T> {
	/**
	 * Constructor.
	 */
	public ComparatorContactListByStatusAndName() {
	}

	@Override
	public int compare(T c1, T c2) {
	    if (((Contact) c1).getStatus() < ((Contact) c2).getStatus()) {
		return 1;
	    } else if (((Contact) c1).getStatus() > ((Contact) c2).getStatus()) {
		return -1;
	    } else
		return ((Contact) c1).getName().compareToIgnoreCase(((Contact) c2).getName());
	}
    }

    /**
     * Event simple click on item of the contact list.
     */
    private class BeemContactListOnClick implements OnItemClickListener {
	/**
	 * Constructor.
	 */
	public BeemContactListOnClick() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int pos, long lpos) {
	    Contact c = mListContact.get(pos);
	    Intent i = new Intent(ContactList.this, Chat.class);
	    i.setData(c.toUri());
	    startActivity(i);
	}
    }

    /**
     * Event simple click on middle groupe name.
     */
    private class OnItemClickGroupName implements OnItemClickListener {

	/**
	 * Constructor.
	 */
	public OnItemClickGroupName() {
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int i, long l) {
	    String group = mListGroup.get(i);
	    buildContactList(group);
	}
    }

}
