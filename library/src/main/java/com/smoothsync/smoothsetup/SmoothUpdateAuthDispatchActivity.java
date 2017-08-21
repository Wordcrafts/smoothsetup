/*
 * Copyright (c) 2017 dmfs GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.smoothsync.smoothsetup;

import android.accounts.Account;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.smoothsync.smoothsetup.microfragments.AccountLoadMicroFragment;

import org.dmfs.android.microfragments.MicroFragment;


/**
 * An activity taking an account and asking the user to update his/her credentials.
 *
 * @author Marten Gajda <marten@dmfs.org>
 */
public final class SmoothUpdateAuthDispatchActivity extends AppCompatActivity
{
    public final static String PARAM_ACCOUNT = "account";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Account account = getIntent().getParcelableExtra(PARAM_ACCOUNT);
        launchStep(new AccountLoadMicroFragment(account));
    }


    private void launchStep(MicroFragment<?> microFragment)
    {
        MicroFragmentHostActivity.launch(this, microFragment);
        finish();
    }
}