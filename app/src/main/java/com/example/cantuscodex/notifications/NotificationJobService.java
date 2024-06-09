/*
 * Copyright (C) 2016 Google Inc.
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

package com.example.cantuscodex.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.cantuscodex.MainActivity;
import com.example.cantuscodex.R;
import com.example.cantuscodex.data.events.model.Event;

public class NotificationJobService extends JobService {

    private static final String CHANNEL_ID = "com.example.cantuscodex.notifications";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        PersistableBundle pb = jobParameters.getExtras();
        PendingIntent contentPendingIntent = PendingIntent.getActivity
                 (this, 0,
                        new Intent(this, MainActivity.class),
                         PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            CharSequence name = "Events";
            String Description = "Reminder for upcoming events";

            NotificationChannel mChannel = new
                    NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            manager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(NotificationJobService.this, CHANNEL_ID)
                .setContentTitle(pb.getString(Event.FIELD_NAME))
                .setContentText(getString(R.string.is_starting_in_one_hour_better_start_preparing_to_leave))
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.drawable.ic_menu_events)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);

        Notification notification = builder.build();

        manager.notify(pb.getInt(Event.FIELD_START_DATE), notification);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.i("TAG", "onStopJob: stopped");
        return true;
    }
}
