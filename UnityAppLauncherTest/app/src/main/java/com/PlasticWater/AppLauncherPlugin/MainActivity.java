package com.PlasticWater.AppLauncherPlugin;


import com.plasticwater.applauncherlib.UnityAppLauncherBridge;
import com.unity3d.player.UnityPlayerActivity;





public class MainActivity extends UnityPlayerActivity {




        UnityAppLauncherBridge runner;


        /*
        Returns the Tab seperated list of apps to Unity, lazily initializes the builder
         */
    public int StartBuildingAppList(String regex){

        if(runner == null )
            runner = new UnityAppLauncherBridge(this.getApplicationContext());

        return runner.StartBuildingAppList(regex);

    }

    /*
    Return percentage of data load completed
     */
    public int GetAppListProgress(){


        return runner.GetAppListProgress();
    }

    /*
    Returns the list of apps to Unity
     */
    public String GetAppList(){
        return runner.GetAppList();
    }

    /*
    Launches the specified app
     */
    public boolean LaunchApp(String appPackage){

        //Fail if builder has not finished loading all app adata
        return runner.LaunchApp(appPackage);
    }

    /*
    Returns the absolute path of the folder which conntains icons
     */
    public String GetAbsolutePathToIcons(){
        return runner.GetAbsolutePathToIcons();
    }


}