using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using AndroidAppLauncher;
using System.Collections.Generic;
using System.IO;
using System.Linq;
public class ApplicationLauncherTest : MonoBehaviour {


    public Dropdown appList;

    public Dropdown fileList;

    public Texture2D test;

    public Text progressText;

    public Text iconPathtext;

    bool loaded = false;

	public Button btn;
	// Use this for initialization
	void Start () {
        TestBtnSprite();
        iconPathtext.text = Application.temporaryCachePath;
	}
	
    // Update is called once per frame;
	void Update () {
        AppLauncher.Instance.UpdateProgress();
        progressText.text = AppLauncher.Instance.Progress + " ";

        if (AppLauncher.Instance.Progress == 100 ){
            if(!loaded){
                OnLoadComplete();
                loaded = true;
            }
        }
	}


    public void GetApps(){

        appList.ClearOptions();
        AppLauncher.Instance.StartBuildingAppList("com.google.*");

    }

    private void OnLoadComplete(){
        List<string> _appNames = AppLauncher.Instance.GetAppList();
        appList.AddOptions(_appNames);

        iconPathtext.text = AppLauncher.Instance.AppIconDirectory;
        //getIcon(_appNames[0]);

        GetFileList();
        getIcon(_appNames[0]);
    }


    void GetFileList(){
        DirectoryInfo info =new DirectoryInfo(AppLauncher.Instance.AppIconDirectory);

        FileInfo[] files = info.GetFiles();

        IEnumerable<string> fileNames = files.Select( x=> x.Name);


        List<string> _fileNames = new List<string>();
        _fileNames.AddRange(fileNames);
        fileList.AddOptions(_fileNames);

    }
    void getIcon(string appName){

        Texture2D tex = AppLauncher.Instance.GetAppIcon(appName);

        Sprite sprite;
        sprite = Sprite.Create (tex, new Rect (0, 0, test.width, test.height), new Vector2 (test.width / 2, test.height / 2));
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
