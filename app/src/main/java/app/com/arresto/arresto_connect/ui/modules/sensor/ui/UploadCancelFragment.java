/*
 * Copyright (c) 2015, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package app.com.arresto.arresto_connect.ui.modules.sensor.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.ui.modules.sensor.server.DfuService;

/**
 * When cancel button is pressed during uploading this fragment shows uploading cancel dialog
 */
public class UploadCancelFragment extends DialogFragment {
	private static final String TAG = "UploadCancelFragment";

	private CancelFragmentListener mListener;

	public interface CancelFragmentListener {
		public void onCancelUpload();
	}

	public static UploadCancelFragment getInstance() {
		return new UploadCancelFragment();
	}

	public void setCancelListner(CancelFragmentListener cancelListner){
		mListener = cancelListner;
	}

	@NonNull
    @Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		setStyle(DialogFragment.STYLE_NORMAL, R.style.theme_dialog_wrap);
		return new AlertDialog.Builder(getActivity()).setTitle("Application Uploading").setMessage("Are you sure to cancel upload?").setCancelable(false)
				.setPositiveButton(R.string.lbl_yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int whichButton) {
						final LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getActivity());
						final Intent pauseAction = new Intent(DfuService.BROADCAST_ACTION);
						pauseAction.putExtra(DfuService.EXTRA_ACTION, DfuService.ACTION_ABORT);
						manager.sendBroadcast(pauseAction);

						mListener.onCancelUpload();
					}
				}).setNegativeButton(R.string.lbl_no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int which) {
						dialog.cancel();
					}
				}).create();
	}

	@Override
	public void onCancel(final DialogInterface dialog) {
		final LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getActivity());
		final Intent pauseAction = new Intent(DfuService.BROADCAST_ACTION);
		pauseAction.putExtra(DfuService.EXTRA_ACTION, DfuService.ACTION_RESUME);
		manager.sendBroadcast(pauseAction);
	}
}
