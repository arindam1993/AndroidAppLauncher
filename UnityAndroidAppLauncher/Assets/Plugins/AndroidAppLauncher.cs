using UnityEngine;
using System.Collections;
using System.Collections.Generic;

namespace AndroidAppLauncher  {
    #if UNITY_ANDROID
    public class AppLauncher{

        //Cache the reference to the activity
        AndroidJavaObject currentActivity;

        AndroidJavaObject appDataBridge;

        private static AppLauncher _instance;

        public int Progress{get; private set;}

        private bool _started = false;
        private string _appIconDirectory = null;
        public string AppIconDirectory{
            get{
                if( _appIconDirectory == null){
                    _appIconDirectory = appDataBridge.Call<string>("GetAbsolutePathToIcons");
                }

                return _appIconDirectory;
            }
        }

        private string[] _param;

        private List<string> _appNames;

        private byte[] _cache;

        public static AppLauncher Instance{
            get{
                if( _instance == null){
                    _instance = new AppLauncher();
                }

                return _instance;
            }


        }

        AppLauncher(){
           
            using(AndroidJavaClass unity = new AndroidJavaClass ("com.unity3d.player.UnityPlayer")){
                currentActivity = unity.GetStatic<AndroidJavaObject> ("currentActivity");

                Debug.Assert(currentActivity!=null, "Current ACtivity is null");
                AndroidJavaObject currentContext = currentActivity.Call<AndroidJavaObject>("getApplicationContext");

                Debug.Assert(currentContext!=null, "Curent Context is null");

                object[] _bridgeParams = {currentContext};

                appDataBridge = new AndroidJavaObject("com.plasticwater.applauncherlib.UnityAppLauncherBridge", _bridgeParams);
            }

            _param = new string[1];
            _appNames = new List<string>();
            Progress = 0;
        }

        public void StartBuildingAppList(string regex){
            _param[0] = regex;
             //currentActivity.Call<int>("StartBuildingAppList", _param);
            appDataBridge.Call<int>("StartBuildingAppList", _param);
             
            _started = true;
        }

        public void UpdateProgress(){
            if(_started )
                Progress = appDataBridge.Call<int>("GetAppListProgress");
        }

        public List<string> GetAppList(){
            string apps = appDataBridge.Call<string>("GetAppList");

            string[] _list = apps.Split('\t');

            _appNames.AddRange(_list);

            return _appNames;
        }
       

        public bool LaunchApp(string packageName){

            if (Progress < 100) return false;

            _param[0] = packageName;
            bool isLaunched = appDataBridge.Call<bool>("LaunchApp", _param);
            return isLaunched;
        }


        public Texture2D  GetAppIcon(string packageName){
            string path = AppLauncher.Instance.AppIconDirectory + "/" + packageName + ".png";

            Texture2D tex = new Texture2D(2,2);

            _cache = System.IO.File.ReadAllBytes(path);

            tex.LoadImage(_cache);

            return tex;

        }
       
	
    }
    #endif
}


