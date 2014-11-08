package com.hereastory.service.impl;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import com.hereastory.MapActivity_bo;
import com.hereastory.R;

public class ErrorDialogService {
	
	public static void showGeneralError(String logTag, int errorMessage, Exception e, final Context context) {
		Log.e(logTag, context.getString(errorMessage), e);
		new AlertDialog.Builder(context).setTitle(R.string.error_occurred).setMessage(errorMessage)
		.setNeutralButton(R.string.return_to_map, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(context, MapActivity_bo.class);
				context.startActivity(intent);					
			}
		}).setIcon(android.R.drawable.ic_dialog_alert).show();
	}
}
