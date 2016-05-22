package com.plasticwater.applauncherlib;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Uses AppList Builder in the background to prevent a UI freeze.
 * Created by Arindam on 4/26/2016.
 */
public class AppDataLoaderTask extends AsyncTask<AppListBuilder, Integer, Integer> {
    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        builder.progress = 100;
    }

    private AppListBuilder builder;

    //Run the applist builder in the backgound
    @Override
    protected Integer doInBackground(AppListBuilder... params) {
        builder = params[0];

        builder.buildAppList();;
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        Log.d(AppListBuilder.DEBUG_TAG, "Starting data loader AsyncTask");
    }

}
