using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using AndroidAppLauncher;
using System.Collections.Generic;

public class ApplicationLauncherTest : MonoBehaviour {


    public Dropdown appList;

    public Texture2D test;

	public Button btn;
	// Use this for initialization
	void Start () {
        TestBtnSprite();
	}
	
    // Update is called once per frame;
	void Update () {
	
	}


    public void GetApps(){

        appList.ClearOptions();
        List<string> apps= AppLauncher.Instance.GetMatchingPackages("com.google.*");
        appList.AddOptions(apps);

    }

	public void showIcon()
	{    
		
		byte[][] icons = AppLauncher.Instance.getIcons ();
		Texture2D tex = new Texture2D (512, 512);
		Sprite sprite;
		tex.LoadImage(icons[0]);
		sprite = Sprite.Create (tex, new Rect (0, 0, tex.width, tex.height), new Vector2 (tex.width / 2, tex.height / 2));
		btn.image.sprite= sprite;
		
	}

    public void TestBtnSprite(){
        Sprite sprite;
        sprite = Sprite.Create (test, new Rect (0, 0, test.width, test.height), new Vector2 (test.width / 2, test.height / 2));
        btn.image.sprite= sprite;
    }

    public void LaunchApp(){
        string appname = appList.captionText.text;
        AppLauncher.Instance.LaunchApp(appname);
    }


}
