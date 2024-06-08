//package com.example.cantuscodex.notifications;
//
//import static android.content.Context.JOB_SCHEDULER_SERVICE;
//import static androidx.core.content.ContextCompat.getSystemService;
//
//import com.example.cantuscodex.R;
//import com.google.firebase.Timestamp;
//
//import android.app.job.JobInfo;
//import android.app.job.JobScheduler;
//import android.content.ComponentName;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.CompoundButton;
//import android.widget.RadioGroup;
//import android.widget.SeekBar;
//import android.widget.Switch;
//import android.widget.TextView;
//import android.widget.Toast;
//public class NotificationScheduler {
//
//        private JobScheduler mScheduler;
//
//        private void scheduleJob(int jobId) {
//            Log.i("PERO","scheduleJob() in MainActivity");
//            mScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//
//            int selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE;
//
//            ComponentName serviceName = new ComponentName("com.example.cantuscodex",
//                    NotificationJobService.class.getName());
//            JobInfo.Builder builder = new JobInfo.Builder(jobId, serviceName)
//                    .setMinimumLatency(100000);
//
//                JobInfo myJobInfo = builder.build();
//                mScheduler.schedule(myJobInfo);
//
//        }
//
//        /**
//         * onClick method for cancelling all existing jobs
//         */
//
//        private void cancelJob(int id) {
//            if (mScheduler != null){
//                mScheduler.cancel(id);
//            }
//        }
//    }
//
//
