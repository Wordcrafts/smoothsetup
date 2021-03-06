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

package com.smoothsync.smoothsetup.utils;

/**
 * An {@link AsyncTaskResult} that actually carries a value.
 *
 * @author Marten Gajda
 */
public final class ValueAsyncTaskResult<T> implements AsyncTaskResult<T>
{
    private final T mValue;


    public ValueAsyncTaskResult(T value)
    {
        mValue = value;
    }


    @Override
    public T value() throws Exception
    {
        return mValue;
    }
}
