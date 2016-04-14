using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using AndroidAppLauncher;
using System.Collections.Generic;

public class ApplicationLauncherTest : MonoBehaviour {


    public Dropdown appList;
	// Use this for initialization
	void Start () {
	
	}
	
    // Update is called once per frame;
	void Update () {
	
	}


    public void GetApps(){

        appList.ClearOptions();
        List<string> apps= AppLauncher.Instance.GetMatchingPackages("com.google.*");
        appList.AddOptions(apps);

    }

    public void LaunchApp(){
        string appname = appList.captionText.text;
        AppLauncher.Instance.LaunchApp(appname);
    }
}
