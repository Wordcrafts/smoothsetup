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

package com.smoothsync.smoothsetup.wizard;

import android.content.Context;
import android.net.Uri;
import android.os.Parcelable;

import com.smoothsync.smoothsetup.microfragments.SetupDispatchMicroFragment;

import org.dmfs.android.microfragments.MicroFragment;
import org.dmfs.android.microwizard.MicroWizard;
import org.dmfs.android.microwizard.box.AbstractSingleBox;
import org.dmfs.android.microwizard.box.Box;

import androidx.annotation.NonNull;


/**
 * @author Marten Gajda
 */
public final class Dispatching implements MicroWizard<Uri>
{
    private final MicroWizard<Void> mNext;


    public Dispatching(MicroWizard<Void> next)
    {
        mNext = next;
    }


    @Override
    public MicroFragment<?> microFragment(@NonNull Context context, @NonNull Uri dataUri)
    {
        return new SetupDispatchMicroFragment(dataUri, mNext);
    }


    @Override
    public Box<MicroWizard<Uri>> boxed()
    {
        return new WizardBox(mNext);
    }


    private final static class WizardBox extends AbstractSingleBox<MicroWizard<Void>, MicroWizard<Uri>>
    {

        private WizardBox(MicroWizard<Void> next)
        {
            super(next, Dispatching::new);
        }


        public final static Parcelable.Creator<Box<MicroWizard<Uri>>> CREATOR = new SingleBoxableBoxCreator<>(WizardBox::new, WizardBox[]::new);
    }
}
