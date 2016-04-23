package com.PlasticWater.AppLauncherPlugin;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import android.util.Log;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import com.unity3d.player.UnityPlayerActivity;


import android.content.Intent;
import android.content.pm.ApplicationInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.ByteArrayOutputStream;


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

        //Has app finished fetching other applist?
        byte[][] iconTextures;

        //List of matched apps
        String appsList;

        /*
        Provide the regex to match package names against in the constructor.
        And the PackageManager which is used to get the lost of apps
         */
        public AppListBuilder(String regex, PackageManager pm){
            Log.d("PluginDebug", "IN CONSTRUCTOR ");
            this.initPatternMatecher(regex);


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

        private int _numPackages ;
        private void buildAppList(){
            targetPackages="";
            launchIntents.clear();

            Log.d("PluginDebug", "Building App List");

            //get a list of installed apps.
            List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
            _numPackages = 0;
            //Loop over apps ad store the matcing ones
            for( int i = 0 ; i<packages.size() ; i++){

                ApplicationInfo app = packages.get(i);
                String appName = app.packageName;

                //using compiled regeex to get a Matcher
                Matcher m = compiledRegex.matcher(appName);

                //When match is found
                if ( m.matches() ){
                    targetPackages += appName + '\t';
                    _numPackages++;
                    Intent launchIntent = pm.getLaunchIntentForPackage(appName);
                    launchIntents.put(appName, launchIntent);
                }

            }
            buildIcons();
            Log.d("PluginDebug","Found "+_numPackages + "packages");
        }


        private void buildIcons(){

            Set<Map.Entry<String,Intent>> entrySet = launchIntents.entrySet();
            iconTextures = new byte[_numPackages][262144];
            int i=0;
            for(Map.Entry<String, Intent> entry : entrySet){
                String appName = entry.getKey();

                Drawable   icon ;
                byte[] iconBytes;
                try {
                    icon = getPackageManager().getApplicationIcon(appName);
                    Bitmap bitmap = ((BitmapDrawable)icon).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    Bitmap resized = Bitmap.createScaledBitmap(bitmap, 512, 512, true);
                    resized.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    iconBytes = stream.toByteArray();
                    Log.d("PluginDebug", "Icon Size: "+ iconBytes.length);
                    iconTextures[i] = iconBytes;
                    i++;
                }
                catch (Exception e) {
                    Log.d("PluginDebug", "Icon Size: "+ iconTextures.length);
                    Log.d("%s Package not found! " + e.toString(),appName);
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

     public byte[][] GetIcons() {
         return builder.iconTextures;
    }

}