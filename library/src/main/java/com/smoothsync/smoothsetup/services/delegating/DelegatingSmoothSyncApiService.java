/*
 * Copyright (c) 2018 dmfs GmbH
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

package com.smoothsync.smoothsetup.services.delegating;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.smoothsync.api.SmoothSyncApi;
import com.smoothsync.api.SmoothSyncApiRequest;

import org.dmfs.httpessentials.exceptions.ProtocolError;
import org.dmfs.httpessentials.exceptions.ProtocolException;

import java.io.IOException;

import androidx.annotation.Nullable;


/**
 * An abstract service that provides the SmoothSync API.
 *
 * @author Marten Gajda
 */
public abstract class DelegatingSmoothSyncApiService extends Service
{

    private SmoothSyncApiServiceBinder mBinder;
    private SmoothSyncApiFactory mApiFactory;


    public DelegatingSmoothSyncApiService(SmoothSyncApiFactory apiFactory)
    {
        mApiFactory = apiFactory;
    }


    @Override
    public final void onCreate()
    {
        super.onCreate();
        mBinder = new SmoothSyncApiServiceBinder(mApiFactory.smoothSyncApi(this.getApplicationContext()));
    }


    @Nullable
    @Override
    public final IBinder onBind(Intent intent)
    {
        return mBinder;
    }


    /**
     * A factory that creates SmoothSyncApi instances.
     */
    public interface SmoothSyncApiFactory
    {
        /**
         * Create a new SmoothSyncApi.
         *
         * @param context
         *         A Context.
         *
         * @return
         */
        SmoothSyncApi smoothSyncApi(Context context);
    }


    /**
     * A {@link Binder} that gives access to the SmoothSync API.
     */
    private final static class SmoothSyncApiServiceBinder extends Binder implements SmoothSyncApi
    {

        private final SmoothSyncApi mApi;


        public SmoothSyncApiServiceBinder(SmoothSyncApi api)
        {
            mApi = api;
        }


        @Override
        public <T> T resultOf(SmoothSyncApiRequest<T> smoothSyncApiRequest) throws IOException, ProtocolError, ProtocolException
        {
            // just forward the call.
            return mApi.resultOf(smoothSyncApiRequest);
        }
    }
}
