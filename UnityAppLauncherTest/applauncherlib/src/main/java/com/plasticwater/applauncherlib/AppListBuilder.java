package com.plasticwater.applauncherlib;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class which stores and builds up a list of apps which have matching package names according to the specified regex.
 *
 * Eg: Providing com.google.* as the regex should load all google apps.
 *
 *
 * Created by Arindam on 4/13/2016.
 */


public class AppListBuilder {
    public static String DEBUG_TAG = "PluginDebug";

    //Sore this to return the list(Tab-seperated values) of packages found to Unity
    String targetPackages;

    //Cache launch intnts for fast aunches
    final Map<String,Intent> launchIntents;


    //information for writing icons
    private File iconCacheDir;
    public static final String iconCacheFolder = "unityIconLoadCache";

    //Compile the regex once since  use it repeadtedly
    Pattern compiledRegex;

    //Package Manager which provides app names
    PackageManager pm;


    Integer progress;


    /*
    Provide the regex to match package names against in the constructor.
    And the PackageManager which is used to get the lost of apps
     */
    public AppListBuilder(String regex, Context  con){
        Log.d("PluginDebug", "IN CONSTRUCTOR ");
        this.initPatternMatecher(regex);

        File cache = con.getCacheDir();

        if( !cache.exists() ){
            Log.d(DEBUG_TAG, " Cache dir doesnt exist, WUT?, well creating anyway");
            cache.mkdirs();

            if( !cache.canWrite()){
                Log.d(DEBUG_TAG, " Cache dir isnt writable, WUT?, trying to make it writable");
                cache.setWritable(true);
            }
        }

        this.iconCacheDir = new File( con.getCacheDir(), iconCacheFolder);

        //Create the folder in cache dir if it doesnt exist
        boolean _isIconCacheExist = this.iconCacheDir.exists();

        Log.d(DEBUG_TAG, " Icon Cache Directory Status : " + _isIconCacheExist);

        if( !_isIconCacheExist ){
            Log.d(DEBUG_TAG, this.iconCacheDir.getName() + " doesnt exist , creating ..");

            //Clear the folder
            this.iconCacheDir.delete();

            boolean isDirectoryMade = this.iconCacheDir.mkdirs();

            if( isDirectoryMade){
                Log.d(DEBUG_TAG, "Icon Cache folder succesfully created");
            }else{
                Log.d(DEBUG_TAG, "Creating Icon Cache folder  failed or already existed");
            }
            if( !this.iconCacheDir.canWrite() ){
                Log.d(DEBUG_TAG, " Can't write to icon cache SubDirectory, making it writable");
                this.iconCacheDir.setWritable(true);
            }
        }

        this.launchIntents = new HashMap<>();
        this.pm = con.getPackageManager();
        this.progress = 0;
        //this.buildAppList();

    }


    public String GetMatchingApps(){
        Log.d("PluginDebug", "Returning " + targetPackages);
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
    public void buildAppList(){
        targetPackages="";
        //Clear caches
        launchIntents.clear();
        this.clearCacheDir();
        Log.d("PluginDebug", "Building App List");

        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        _numPackages = 0;
        //Loop over apps and store the matcing ones
        for( int i = 0 ; i<packages.size() ; i++){

            ApplicationInfo app = packages.get(i);
            String appName = app.packageName;

            //using compiled regeex to get a Matcher
            Matcher m = compiledRegex.matcher(appName);

            //When match is found
            if ( m.matches() ){
                targetPackages += appName + '\t';
                _numPackages++;
                progress = _numPackages;
                //Cache launch intent fro quic app start
                Intent launchIntent = pm.getLaunchIntentForPackage(appName);
                launchIntents.put(appName, launchIntent);

                //Create a fixed size bitmap for the applications icon
                Drawable icon = pm.getApplicationIcon(app);
                Bitmap bitmap = ((BitmapDrawable)icon).getBitmap();
                Bitmap resized = Bitmap.createScaledBitmap(bitmap, 512, 512, true);

                saveIconToDisk(appName, resized);
            }

        }
        Log.d("PluginDebug", "Found " + _numPackages + "packages");
    }


    private void saveIconToDisk(String packageName, Bitmap icon){
        Log.d(DEBUG_TAG, "saving "+packageName+ " to disk");
        FileOutputStream out = null;
        //Location of cache directory

        try {
            File toWrite = new File( this.iconCacheDir, packageName + ".png");

            out = new FileOutputStream(toWrite);
            icon.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    //Save the fie
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    //Clears the cache directory
    private void clearCacheDir(){

        String[] iconFiles = this.iconCacheDir.list();

        if (iconFiles != null) {
            for(String iconFile : iconFiles){
                File _temp  = new File(this.iconCacheDir,iconFile);

                if( _temp.delete() ){
                    Log.d(DEBUG_TAG, _temp.getName() + " deleted");
                }
            }
        }
    }

    /*
    Returns the absoute path of the directory which stores the icons
     */
    public String getIconDirectory(){
        return this.iconCacheDir.getAbsolutePath();
    }

}