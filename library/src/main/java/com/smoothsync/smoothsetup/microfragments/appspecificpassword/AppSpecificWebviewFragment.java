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

package com.smoothsync.smoothsetup.microfragments.appspecificpassword;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.google.android.material.snackbar.Snackbar;
import com.smoothsync.smoothsetup.R;

import org.dmfs.android.microfragments.FragmentEnvironment;
import org.dmfs.android.microfragments.transitions.BackTransition;
import org.dmfs.android.microfragments.transitions.BackWithResultTransition;
import org.dmfs.pigeonpost.Cage;

import java.net.URI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


/**
 * A fragment that presents a website to create an app specific password.
 *
 * @author Marten Gajda
 */
public final class AppSpecificWebviewFragment extends Fragment implements View.OnKeyListener
{

    public static final int PASSWORD_PROBE_PERIOD = 500;
    ;
    private final Handler mHandler = new Handler();
    private WebView mWebView;
    private String mAppSpecificPassword;
    private final View.OnClickListener mOnCopyPasswordListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            FragmentEnvironment<Params> env = new FragmentEnvironment<>(AppSpecificWebviewFragment.this);
            env.host().execute(
                    getActivity(),
                    new BackWithResultTransition<>(env.microFragment().parameter().cage(), new SimplePasswordResult(mAppSpecificPassword)));
        }
    };
    private AppSpecificPasswordProbe mPasswordProbe;
    private final Runnable mPasswordProbeRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            if (mWebView != null && mPasswordProbe != null)
            {
                mPasswordProbe.executeOn(mWebView);
            }
            mHandler.postDelayed(mPasswordProbeRunnable, PASSWORD_PROBE_PERIOD);
        }
    };
    private Snackbar mSnackbar;
    private ProgressBar mProgress;


    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.smoothsetup_microfragment_webview, container, false);
        mWebView = (WebView) root.findViewById(R.id.smoothsetup_webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.setOnKeyListener(this);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onPageFinished(WebView view, String url)
            {
                super.onPageFinished(view, url);
                mProgress.animate().alpha(0).start();
            }
        });
        mWebView.addJavascriptInterface(new AppSpecificPasswordCallback()
        {
            @JavascriptInterface
            @Override
            public void onAppSpecificPassword(final String appSpecificPassword)
            {
                if (!TextUtils.isEmpty(appSpecificPassword) && !TextUtils.equals(mAppSpecificPassword, appSpecificPassword))
                {
                    mAppSpecificPassword = appSpecificPassword;
                    mSnackbar = Snackbar.make(mWebView, getString(R.string.smoothsetup_snackbar_found_app_specific_password, appSpecificPassword),
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.smoothsetup_button_copy_app_specific_password,
                                    mOnCopyPasswordListener);
                    mSnackbar.show();
                }
            }
        }, "SmoothSetup");
        String url = new FragmentEnvironment<Params>(this).microFragment().parameter().uri().toASCIIString();
        if (savedInstanceState == null)
        {
            mWebView.loadUrl(url);
        }
        else
        {
            mWebView.restoreState(savedInstanceState);
        }
        mPasswordProbe = new DefaultPasswordProbeFactory().forUrl(URI.create(url));
        mProgress = (ProgressBar) root.findViewById(android.R.id.progress);

        // start probing for the password
        mHandler.postDelayed(mPasswordProbeRunnable, PASSWORD_PROBE_PERIOD);

        return root;
    }


    @Override
    public void onResume()
    {
        super.onResume();
        if (mWebView != null)
        {
            mWebView.onResume();
        }
    }


    @Override
    public void onPause()
    {
        if (mWebView != null)
        {
            mWebView.onPause();
        }
        super.onPause();
    }


    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        mWebView.saveState(outState);
    }


    @Override
    public void onDestroyView()
    {
        mHandler.removeCallbacks(mPasswordProbeRunnable);
        if (mWebView != null)
        {
            mWebView.destroy();
        }
        if (mSnackbar != null)
        {
            mSnackbar.dismiss();
        }
        super.onDestroyView();
    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if (event.getAction() == KeyEvent.ACTION_DOWN)
            {
                if (mWebView.canGoBack())
                {
                    // user went back a step
                    mWebView.goBack();
                }
                else
                {
                    // the user cancelled the authorization flow
                    new FragmentEnvironment<>(this).host().execute(getActivity(), new BackTransition());
                }
            }
            return true;
        }
        return false;
    }


    public interface PasswordResult extends Parcelable
    {
        @NonNull
        String password();
    }


    public interface Params
    {
        @NonNull
        URI uri();

        @NonNull
        Cage<PasswordResult> cage();
    }
}
