/*
 * Copyright (C) 2013 AChep@xda <artemchep@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.achep.FifteenPuzzle;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.widget.RemoteViews;

public class NotificationUtils {

	public static final int DOWNLOAD_SERVICE = 0;

	@SuppressWarnings("deprecation")
	public static Notification getNotification(Context context,
			String contentTitle, String contentText, int imageResource,
			PendingIntent imagePendingIntent, PendingIntent contentPendingIntent) {
		Notification n = new Notification();

		int sdk = Build.VERSION.SDK_INT;
		if (sdk <= Build.VERSION_CODES.GINGERBREAD_MR1) {
			// Gingerbread and lower
			n.setLatestEventInfo(context, contentTitle, contentText, null);
		} else {
			// Honeycomb and higher
			RemoteViews rv = new RemoteViews(context.getPackageName(),
					R.layout.notification_download_service_error);
			rv.setTextViewText(R.id.title, contentTitle);
			rv.setTextViewText(R.id.text, contentText);
			rv.setImageViewResource(R.id.button, imageResource);
			rv.setOnClickPendingIntent(R.id.button, imagePendingIntent);

			n.contentView = rv;
		}
		n.contentIntent = contentPendingIntent;
		n.icon = R.drawable.ic_statusbar_new_version;
		n.tickerText = contentText;
		n.flags = Notification.FLAG_AUTO_CANCEL;
		return n;
	}
}