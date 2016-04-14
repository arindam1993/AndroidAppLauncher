package com.PlasticWater.AppLauncherPlugin;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;


import com.unity3d.player.UnityPlayerActivity;


import android.content.Intent;
import android.content.pm.ApplicationInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class MainActivity extends UnityPlayerActivity {

    /**
     * A class which stores and builds up a list of apps which have matching package names according to the specified regex.
     *
     * Eg: Providing com.google.* as the regex should load all google apps.
     *
     *
     * Created by Arindam on 4/13/2016.
     */
    public class AppListBuilder {

        //Sore this to return the list(Tab-seperated values) of packages found to Unity
        String targetPackages;

        //Cache launch intnts for fast aunches
        final Map<String,Intent> launchIntents;
        String appRegex;

        //Compile the regex once since  use it repeadtedly
        Pattern compiledRegex;

        //Package Manager which provides app names
        PackageManager pm;

        /*
        Provide the regex to match package names against in the constructor.
        And the PackageManager which is used to get the lost of apps
         */
        public AppListBuilder(String regex, PackageManager pm){
            Log.d("PluginDebug", "IN CONSTRUCTOR ");
            this.initPatternMatecher(regex);

            ;
            this.launchIntents = new HashMap<>();
            this.pm = pm;

            this.buildAppList();

        }

        public void RebuildAppList(String regex,PackageManager pm){
            initPatternMatecher(regex);
            buildAppList();
            this.pm = pm;
        }

        public String GetMatchingApps(){
            Log.d("PluginDebug", "Returning "+ targetPackages);
            return targetPackages;
        }

        public boolean LaunchApp(String packageName, Context context){
            Intent launchIntent = launchIntents.get(packageName);

            //requested package name was not cached
            if( launchIntent == null ) return false;
            context.startActivity(launchIntent);

            return true;
        }

        private void initPatternMatecher(String pattern){
            compiledRegex = Pattern.compile(pattern);

        }
        private void buildAppList(){
            targetPackages="";
            launchIntents.clear();

            Log.d("PluginDebug", "Building App List");

            //get a list of installed apps.
            List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

            //Loop over apps ad store the matcing ones
            for( int i = 0 ; i<packages.size() ; i++){
                ApplicationInfo app = packages.get(i);
                String appName = app.packageName;

                //using compiled regeex to get a Matcher
                Matcher m = compiledRegex.matcher(appName);

                //When match is found
                if ( m.matches() ){
                    targetPackages += appName + '\t';

                    Intent launchIntent = pm.getLaunchIntentForPackage(appName);
                    launchIntents.put(appName, launchIntent);
                }

            }
        }

    }

    AppListBuilder builder;

    /*
    Returns the Tab seperated list of apps to Unity, lazily initializes the builder
     */
    public String GetApps(String regex){

        Log.d("PluginDebug", "GetAppsCalled with regex " + regex);
        PackageManager pm = getPackageManager();
        if( builder == null ){
            Log.d("PluginDebug", "Building builder " );
            builder = new AppListBuilder(regex, pm);
        }

        return builder.GetMatchingApps();
    }

    /*
    Launches the specified app
     */
    public boolean LaunchApp(String appPackage){

        Context currentContext = getApplicationContext();

        return builder.LaunchApp(appPackage, currentContext);
    }
}