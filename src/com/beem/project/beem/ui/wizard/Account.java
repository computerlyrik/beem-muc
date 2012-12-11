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
package com.beem.project.beem.ui.wizard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;

import com.beem.project.beem.ui.CreateAccount;
import com.beem.project.beem.R;

/**
 * The first activity of an user friendly wizard to configure a XMPP account.
 *
 * @author Da Risk <darisk972@gmail.com>
 */
public class Account extends Activity implements OnClickListener, RadioGroup.OnCheckedChangeListener {

    private RadioGroup mConfigureGroup;
    private Button mNextButton;

    /**
     * Constructor.
     */
    public Account() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.wizard_account);
	mConfigureGroup = (RadioGroup) findViewById(R.id.configure_group);
	mConfigureGroup.setOnCheckedChangeListener(this);
	mNextButton = (Button) findViewById(R.id.next);
	mNextButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
	if (v == mNextButton) {
	    int selectedid = mConfigureGroup.getCheckedRadioButtonId();
	    Intent i = null;
	    if (selectedid == R.id.configure_account) {
		i = new Intent(this, AccountConfigure.class);
		finish();
	    } else if (selectedid == R.id.create_account) {
		i = new Intent(this, CreateAccount.class);
	    }
	    if (i != null) {
		startActivity(i);
	    }
	}
    }

    @Override
    public void onCheckedChanged(RadioGroup  group, int checkedId) {
	if (checkedId == -1)
	    mNextButton.setEnabled(false);
	else
	    mNextButton.setEnabled(true);
    }
}
