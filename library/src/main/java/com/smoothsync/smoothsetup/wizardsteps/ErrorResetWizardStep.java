/*
 * Copyright (C) 2016 Marten Gajda <marten@dmfs.org>
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
 *
 */

package com.smoothsync.smoothsetup.wizardsteps;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.smoothsync.smoothsetup.R;
import com.smoothsync.smoothsetup.model.WizardStep;
import com.smoothsync.smoothsetup.wizardtransitions.ResetWizardTransition;


/**
 * A WizardStep shows an error message with an option to retry.
 *
 * @author Marten Gajda <marten@dmfs.org>
 */
public final class ErrorResetWizardStep implements WizardStep
{

	private final static String ARG_RESET_STEP = "reset_step";

	private final WizardStep mRetryStep;


	public ErrorResetWizardStep(WizardStep retryStep)
	{
		this.mRetryStep = retryStep;
	}


	@Override
	public String title(Context context)
	{
		return context.getString(R.string.smoothsetup_wizard_title_error);
	}


	@Override
	public boolean skipOnBack()
	{
		return true;
	}


	@Override
	public Fragment fragment(Context context)
	{
		Fragment result = new ErrorFragment();
		Bundle arguments = new Bundle();
		arguments.putParcelable(ARG_RESET_STEP, mRetryStep);
		arguments.putParcelable(ARG_WIZARD_STEP, this);
		result.setArguments(arguments);
		result.setRetainInstance(true);
		return result;
	}


	@Override
	public int describeContents()
	{
		return 0;
	}


	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeParcelable(mRetryStep, flags);
	}

	public final static Creator<ErrorResetWizardStep> CREATOR = new Creator<ErrorResetWizardStep>()
	{
		@Override
		public ErrorResetWizardStep createFromParcel(Parcel source)
		{
			return new ErrorResetWizardStep((WizardStep) source.readParcelable(getClass().getClassLoader()));
		}


		@Override
		public ErrorResetWizardStep[] newArray(int size)
		{
			return new ErrorResetWizardStep[size];
		}
	};

	/**
	 * A Fragment that shows an error with an option to retry.
	 */
	public static class ErrorFragment extends Fragment implements View.OnClickListener
	{

		@Nullable
		@Override
		public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
		{
			View result = inflater.inflate(R.layout.smoothsetup_wizard_fragment_error, container, false);
			((Button) result.findViewById(android.R.id.button1)).setOnClickListener(this);
			return result;
		}


		@Override
		public void onClick(View v)
		{
			new ResetWizardTransition((WizardStep) getArguments().getParcelable(ARG_RESET_STEP)).execute(getContext());
		}
	}
}
