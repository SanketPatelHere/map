package com.example.mygooglemapscreen;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import java.util.ArrayList;

public class ProfileFragment {
    private static String TAG = MainActivity.class.getSimpleName();

    private PlacesAutoCompleteAdapter2 mAdapter;

    HandlerThread mHandlerThread;
    Handler mThreadHandler;
    public ProfileFragment() {
        // Required empty public constructor

        if (mThreadHandler == null) {
            // Initialize and start the HandlerThread
            // which is basically a Thread with a Looper
            // attached (hence a MessageQueue)
            mHandlerThread = new HandlerThread(TAG, android.os.Process.THREAD_PRIORITY_BACKGROUND);
            mHandlerThread.start();

            // Initialize the Handler
            mThreadHandler = new Handler(mHandlerThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 1) {
                        ArrayList<String> results = mAdapter.resultList;

                        if (results != null && results.size() > 0) {
                            mAdapter.notifyDataSetChanged();
                        }
                        else {
                            mAdapter.notifyDataSetInvalidated();
                        }
                    }
                }
            };
        }

    }
}
