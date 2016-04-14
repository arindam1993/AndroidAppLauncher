using UnityEngine;
using System.Collections;
using System.Collections.Generic;

namespace AndroidAppLauncher  {
    #if UNITY_ANDROID
    public class AppLauncher{

        //Cache the reference to the activity
        AndroidJavaObject currentActivity;

        private static AppLauncher _instance;


        private string[] _param;

        private List<string> _appNames;

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
            }

            _param = new string[1];
            _appNames = new List<string>();
        }

        public List<string> GetMatchingPackages(string regex){
            _param[0] = regex;
            string apps = currentActivity.Call<string>("GetApps", _param);
            _appNames.Clear();

            _appNames.AddRange(apps.Split('\t'));

            return _appNames;

        }


        public bool LaunchApp(string packageName){
            _param[0] = packageName;
            bool isLaunched = currentActivity.Call<bool>("LaunchApp", _param);

            return isLaunched;
        }
        
    }
    #endif
}


