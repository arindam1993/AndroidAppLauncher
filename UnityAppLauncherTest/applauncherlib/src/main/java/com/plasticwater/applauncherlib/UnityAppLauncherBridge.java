package com.plasticwater.applauncherlib;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by Arindam on 5/22/2016.
 */
public class UnityAppLauncherBridge {


    Context appContext;
    AppListBuilder builder;
    AsyncTask<AppListBuilder, Integer, Integer> dataloader;


    public UnityAppLauncherBridge(Context currentContext){
        this.appContext = currentContext;
    }

    public int StartBuildingAppList(String regex){

        Log.d("PluginDebug", "GetAppsCalled with regex " + regex);
        PackageManager pm = appContext.getPackageManager();
        if( builder == null ){
            Log.d("PluginDebug", "Building builder " );
            builder = new AppListBuilder(regex, appContext);

            if( dataloader == null){
                dataloader = new AppDataLoaderTask();
            }


        }
        dataloader.execute(builder);
        return builder.progress;

    }

    /*
    Return percentage of data load completed
     */
    public int GetAppListProgress(){
        if( builder == null) return 0;

        return builder.progress;
    }

    /*
    Returns the list of apps to Unity
     */
    public String GetAppList(){
        return builder.GetMatchingApps();
    }

    /*
    Launches the specified app
     */
    public boolean LaunchApp(String appPackage){

        //Fail if builder has not finished loading all app adata
        if( builder.progress < 100) return false;
        Context currentContext = appContext;

        return builder.LaunchApp(appPackage, currentContext);
    }

    /*
    Returns the absolute path of the folder which conntains icons
     */
    public String GetAbsolutePathToIcons(){
        return builder.getIconDirectory();
    }
}
