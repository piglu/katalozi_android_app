package com.katalozi2;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class arhiva extends Activity implements B4AActivity{
	public static arhiva mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = true;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mostCurrent = this;
		if (processBA == null) {
			processBA = new BA(this.getApplicationContext(), null, null, "com.katalozi2", "com.katalozi2.arhiva");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (arhiva).");
				p.finish();
			}
		}
        processBA.setActivityPaused(true);
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(this, processBA, wl, false))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "com.katalozi2", "com.katalozi2.arhiva");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.katalozi2.arhiva", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (arhiva) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (arhiva) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEventFromUI(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return arhiva.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null)
            return;
        if (this != mostCurrent)
			return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (arhiva) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        if (mostCurrent != null)
            processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
            arhiva mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (arhiva) Resume **");
            if (mc != mostCurrent)
                return;
		    processBA.raiseEvent(mc._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        for (int i = 0;i < permissions.length;i++) {
            Object[] o = new Object[] {permissions[i], grantResults[i] == 0};
            processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
        }
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.phone.Phone.PhoneWakeState _vvvvvvv6 = null;
public anywheresoftware.b4a.objects.B4XViewWrapper.XUI _vvvvvvvv2 = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _spnducan = null;
public b4a.example3.customlistview _clva = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbltitle = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imageview1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblaction1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblcontent = null;
public anywheresoftware.b4a.objects.StringUtils _vvvvvvvv3 = null;
public anywheresoftware.b4a.objects.collections.List _vvvvvvv4 = null;
public com.katalozi2.main _vvvvvv6 = null;
public com.katalozi2.starter _vvvvvv7 = null;
public com.katalozi2.prikazi_katalog _prikazi_katalog = null;
public com.katalozi2.httputils2service _vvvvvvv1 = null;
public static class _katalog2{
public boolean IsInitialized;
public String naslov;
public String linkSlika;
public String linkKatalog;
public String opis;
public void Initialize() {
IsInitialized = true;
naslov = "";
linkSlika = "";
linkKatalog = "";
opis = "";
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 27;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 29;BA.debugLine="Activity.LoadLayout(\"arhiva\")";
mostCurrent._activity.LoadLayout("arhiva",mostCurrent.activityBA);
 //BA.debugLineNum = 31;BA.debugLine="listaKataloga.Initialize";
mostCurrent._vvvvvvv4.Initialize();
 //BA.debugLineNum = 33;BA.debugLine="DodajImenaDucana";
_vvvvvvvv0();
 //BA.debugLineNum = 34;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 83;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 84;BA.debugLine="awake.ReleaseKeepAlive";
_vvvvvvv6.ReleaseKeepAlive();
 //BA.debugLineNum = 85;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 77;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 78;BA.debugLine="If Main.manager.GetBoolean(\"check1\") Then";
if (mostCurrent._vvvvvv6._vv2 /*anywheresoftware.b4a.objects.preferenceactivity.PreferenceManager*/ .GetBoolean("check1")) { 
 //BA.debugLineNum = 79;BA.debugLine="awake.KeepAlive(True)";
_vvvvvvv6.KeepAlive(processBA,anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 81;BA.debugLine="End Sub";
return "";
}
public static String  _clva_visiblerangechanged(int _firstindex,int _lastindex) throws Exception{
int _extrasize = 0;
int _i = 0;
anywheresoftware.b4a.objects.B4XViewWrapper _p = null;
com.katalozi2.arhiva._katalog2 _kk = null;
 //BA.debugLineNum = 105;BA.debugLine="Sub clvA_VisibleRangeChanged (FirstIndex As Int, L";
 //BA.debugLineNum = 106;BA.debugLine="Dim ExtraSize As Int = 20";
_extrasize = (int) (20);
 //BA.debugLineNum = 108;BA.debugLine="For i = 0 To clvA.Size - 1";
{
final int step2 = 1;
final int limit2 = (int) (mostCurrent._clva._getsize()-1);
_i = (int) (0) ;
for (;_i <= limit2 ;_i = _i + step2 ) {
 //BA.debugLineNum = 109;BA.debugLine="Dim p As B4XView = clvA.GetPanel(i)";
_p = new anywheresoftware.b4a.objects.B4XViewWrapper();
_p = mostCurrent._clva._getpanel(_i);
 //BA.debugLineNum = 110;BA.debugLine="If i > FirstIndex - ExtraSize And i < LastIndex";
if (_i>_firstindex-_extrasize && _i<_lastindex+_extrasize) { 
 //BA.debugLineNum = 112;BA.debugLine="If p.NumberOfViews = 0 Then";
if (_p.getNumberOfViews()==0) { 
 //BA.debugLineNum = 113;BA.debugLine="Dim kk As katalog2 = clvA.GetValue(i)";
_kk = (com.katalozi2.arhiva._katalog2)(mostCurrent._clva._getvalue(_i));
 //BA.debugLineNum = 114;BA.debugLine="p.LoadLayout(\"stavka\")";
_p.LoadLayout("stavka",mostCurrent.activityBA);
 //BA.debugLineNum = 115;BA.debugLine="lblTitle.Text = kk.naslov";
mostCurrent._lbltitle.setText(BA.ObjectToCharSequence(_kk.naslov /*String*/ ));
 //BA.debugLineNum = 116;BA.debugLine="lblContent.Text = kk.opis";
mostCurrent._lblcontent.setText(BA.ObjectToCharSequence(_kk.opis /*String*/ ));
 //BA.debugLineNum = 117;BA.debugLine="SetTextSize(lblContent, lblContent.Text)";
_vvvvvvv0(mostCurrent._lblcontent,mostCurrent._lblcontent.getText());
 //BA.debugLineNum = 118;BA.debugLine="SetColorStateList(lblAction1, xui.Color_LightG";
_vvvvvvvv1(mostCurrent._lblaction1,mostCurrent._vvvvvvvv2.Color_LightGray,mostCurrent._lblaction1.getTextColor());
 //BA.debugLineNum = 119;BA.debugLine="dl_slike(kk.linkSlika, ImageView1)";
_dl_slike(_kk.linkSlika /*String*/ ,mostCurrent._imageview1);
 };
 }else {
 //BA.debugLineNum = 123;BA.debugLine="If p.NumberOfViews > 0 Then";
if (_p.getNumberOfViews()>0) { 
 //BA.debugLineNum = 124;BA.debugLine="p.RemoveAllViews '<--- remove the layout";
_p.RemoveAllViews();
 };
 };
 }
};
 //BA.debugLineNum = 128;BA.debugLine="End Sub";
return "";
}
public static void  _dl_slike(String _lnk,anywheresoftware.b4a.objects.ImageViewWrapper _iv) throws Exception{
ResumableSub_dl_slike rsub = new ResumableSub_dl_slike(null,_lnk,_iv);
rsub.resume(processBA, null);
}
public static class ResumableSub_dl_slike extends BA.ResumableSub {
public ResumableSub_dl_slike(com.katalozi2.arhiva parent,String _lnk,anywheresoftware.b4a.objects.ImageViewWrapper _iv) {
this.parent = parent;
this._lnk = _lnk;
this._iv = _iv;
}
com.katalozi2.arhiva parent;
String _lnk;
anywheresoftware.b4a.objects.ImageViewWrapper _iv;
com.katalozi2.httpjob _j = null;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 92;BA.debugLine="Dim j As HttpJob";
_j = new com.katalozi2.httpjob();
 //BA.debugLineNum = 94;BA.debugLine="ProgressDialogShow(\"Preuzimanje...\")";
anywheresoftware.b4a.keywords.Common.ProgressDialogShow(mostCurrent.activityBA,BA.ObjectToCharSequence("Preuzimanje..."));
 //BA.debugLineNum = 95;BA.debugLine="j.Initialize(\"\", Me)";
_j._initialize /*String*/ (processBA,"",arhiva.getObject());
 //BA.debugLineNum = 96;BA.debugLine="j.Download(lnk)";
_j._vv7 /*String*/ (_lnk);
 //BA.debugLineNum = 97;BA.debugLine="Wait For (j) JobDone(j As HttpJob)";
anywheresoftware.b4a.keywords.Common.WaitFor("jobdone", processBA, this, (Object)(_j));
this.state = 5;
return;
case 5:
//C
this.state = 1;
_j = (com.katalozi2.httpjob) result[0];
;
 //BA.debugLineNum = 98;BA.debugLine="If j.Success Then";
if (true) break;

case 1:
//if
this.state = 4;
if (_j._vvvvv5 /*boolean*/ ) { 
this.state = 3;
}if (true) break;

case 3:
//C
this.state = 4;
 //BA.debugLineNum = 99;BA.debugLine="iv.SetBackgroundImage(j.GetBitmap)";
_iv.SetBackgroundImageNew((android.graphics.Bitmap)(_j._vvv2 /*anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper*/ ().getObject()));
 if (true) break;

case 4:
//C
this.state = -1;
;
 //BA.debugLineNum = 101;BA.debugLine="j.Release";
_j._vvvvv3 /*String*/ ();
 //BA.debugLineNum = 102;BA.debugLine="ProgressDialogHide";
anywheresoftware.b4a.keywords.Common.ProgressDialogHide();
 //BA.debugLineNum = 103;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static void  _jobdone(com.katalozi2.httpjob _j) throws Exception{
}
public static String  _vvvvvvvv0() throws Exception{
anywheresoftware.b4a.sql.SQL.CursorWrapper _cursor1 = null;
anywheresoftware.b4a.objects.collections.List _l = null;
int _i = 0;
 //BA.debugLineNum = 36;BA.debugLine="Sub DodajImenaDucana";
 //BA.debugLineNum = 37;BA.debugLine="Dim Cursor1 As Cursor";
_cursor1 = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
 //BA.debugLineNum = 38;BA.debugLine="Dim l As List";
_l = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 40;BA.debugLine="l.Initialize";
_l.Initialize();
 //BA.debugLineNum = 41;BA.debugLine="Cursor1 = Starter.upit.ExecQuery(\"SELECT DISTINCT";
_cursor1.setObject((android.database.Cursor)(mostCurrent._vvvvvv7._v5 /*anywheresoftware.b4a.sql.SQL*/ .ExecQuery("SELECT DISTINCT ducan FROM arhiva")));
 //BA.debugLineNum = 42;BA.debugLine="For i = 0 To Cursor1.RowCount - 1";
{
final int step5 = 1;
final int limit5 = (int) (_cursor1.getRowCount()-1);
_i = (int) (0) ;
for (;_i <= limit5 ;_i = _i + step5 ) {
 //BA.debugLineNum = 43;BA.debugLine="Cursor1.Position = i";
_cursor1.setPosition(_i);
 //BA.debugLineNum = 44;BA.debugLine="l.Add(Cursor1.GetString(\"ducan\"))";
_l.Add((Object)(_cursor1.GetString("ducan")));
 }
};
 //BA.debugLineNum = 46;BA.debugLine="l.Sort(True)";
_l.Sort(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 47;BA.debugLine="spnDucan.AddAll(l)";
mostCurrent._spnducan.AddAll(_l);
 //BA.debugLineNum = 48;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 12;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 15;BA.debugLine="Dim xui As XUI";
mostCurrent._vvvvvvvv2 = new anywheresoftware.b4a.objects.B4XViewWrapper.XUI();
 //BA.debugLineNum = 16;BA.debugLine="Private spnDucan As Spinner";
mostCurrent._spnducan = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 17;BA.debugLine="Private clvA As CustomListView";
mostCurrent._clva = new b4a.example3.customlistview();
 //BA.debugLineNum = 18;BA.debugLine="Private lblTitle As Label";
mostCurrent._lbltitle = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 19;BA.debugLine="Private ImageView1 As ImageView";
mostCurrent._imageview1 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 20;BA.debugLine="Private lblAction1 As Label";
mostCurrent._lblaction1 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Private lblContent As Label";
mostCurrent._lblcontent = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Dim stu As StringUtils";
mostCurrent._vvvvvvvv3 = new anywheresoftware.b4a.objects.StringUtils();
 //BA.debugLineNum = 23;BA.debugLine="Private listaKataloga As List";
mostCurrent._vvvvvvv4 = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 24;BA.debugLine="Type katalog2(naslov As String, linkSlika As Stri";
;
 //BA.debugLineNum = 25;BA.debugLine="End Sub";
return "";
}
public static String  _lblaction1_click() throws Exception{
int _index = 0;
com.katalozi2.arhiva._katalog2 _kk = null;
 //BA.debugLineNum = 142;BA.debugLine="Sub lblAction1_Click";
 //BA.debugLineNum = 143;BA.debugLine="Dim index As Int = clvA.GetItemFromView(Sender)";
_index = mostCurrent._clva._getitemfromview((anywheresoftware.b4a.objects.B4XViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.B4XViewWrapper(), (java.lang.Object)(anywheresoftware.b4a.keywords.Common.Sender(mostCurrent.activityBA))));
 //BA.debugLineNum = 145;BA.debugLine="Dim kk As katalog2";
_kk = new com.katalozi2.arhiva._katalog2();
 //BA.debugLineNum = 146;BA.debugLine="kk.Initialize";
_kk.Initialize();
 //BA.debugLineNum = 147;BA.debugLine="kk = listaKataloga.Get(index)";
_kk = (com.katalozi2.arhiva._katalog2)(mostCurrent._vvvvvvv4.Get(_index));
 //BA.debugLineNum = 148;BA.debugLine="Log(kk.linkKatalog)";
anywheresoftware.b4a.keywords.Common.LogImpl("12949126",_kk.linkKatalog /*String*/ ,0);
 //BA.debugLineNum = 150;BA.debugLine="Main.linkK = kk.linkKatalog";
mostCurrent._vvvvvv6._vv1 /*String*/  = _kk.linkKatalog /*String*/ ;
 //BA.debugLineNum = 151;BA.debugLine="StartActivity(prikazi_katalog)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._prikazi_katalog.getObject()));
 //BA.debugLineNum = 152;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 9;BA.debugLine="Private awake As PhoneWakeState";
_vvvvvvv6 = new anywheresoftware.b4a.phone.Phone.PhoneWakeState();
 //BA.debugLineNum = 10;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvv1(anywheresoftware.b4a.objects.LabelWrapper _btn,int _pressed,int _enabled) throws Exception{
int[][] _states = null;
anywheresoftware.b4j.object.JavaObject _csl = null;
anywheresoftware.b4j.object.JavaObject _b1 = null;
 //BA.debugLineNum = 130;BA.debugLine="Sub SetColorStateList(Btn As Label,Pressed As Int,";
 //BA.debugLineNum = 131;BA.debugLine="Dim States(2,1) As Int";
_states = new int[(int) (2)][];
{
int d0 = _states.length;
int d1 = (int) (1);
for (int i0 = 0;i0 < d0;i0++) {
_states[i0] = new int[d1];
}
}
;
 //BA.debugLineNum = 132;BA.debugLine="States(0,0) = 16842919    'Pressed";
_states[(int) (0)][(int) (0)] = (int) (16842919);
 //BA.debugLineNum = 133;BA.debugLine="States(1,0) = 16842910    'Enabled";
_states[(int) (1)][(int) (0)] = (int) (16842910);
 //BA.debugLineNum = 135;BA.debugLine="Dim CSL As JavaObject";
_csl = new anywheresoftware.b4j.object.JavaObject();
 //BA.debugLineNum = 136;BA.debugLine="CSL.InitializeNewInstance(\"android.content.res.Co";
_csl.InitializeNewInstance("android.content.res.ColorStateList",new Object[]{(Object)(_states),(Object)(new int[]{_pressed,_enabled})});
 //BA.debugLineNum = 138;BA.debugLine="Dim B1 As JavaObject = Btn";
_b1 = new anywheresoftware.b4j.object.JavaObject();
_b1.setObject((java.lang.Object)(_btn.getObject()));
 //BA.debugLineNum = 139;BA.debugLine="B1.RunMethod(\"setTextColor\", Array As Object(CSL)";
_b1.RunMethod("setTextColor",new Object[]{(Object)(_csl.getObject())});
 //BA.debugLineNum = 140;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvv0(anywheresoftware.b4a.objects.LabelWrapper _lbl,String _txt) throws Exception{
float _dt = 0f;
float _limit = 0f;
int _h = 0;
 //BA.debugLineNum = 154;BA.debugLine="Sub SetTextSize(lbl As Label, txt As String)";
 //BA.debugLineNum = 155;BA.debugLine="Dim dt As Float";
_dt = 0f;
 //BA.debugLineNum = 156;BA.debugLine="Dim limit = 0.5 As Float";
_limit = (float) (0.5);
 //BA.debugLineNum = 157;BA.debugLine="Dim h As Int";
_h = 0;
 //BA.debugLineNum = 159;BA.debugLine="lbl.Text = txt";
_lbl.setText(BA.ObjectToCharSequence(_txt));
 //BA.debugLineNum = 160;BA.debugLine="lbl.TextSize = 72";
_lbl.setTextSize((float) (72));
 //BA.debugLineNum = 161;BA.debugLine="dt = lbl.TextSize";
_dt = _lbl.getTextSize();
 //BA.debugLineNum = 162;BA.debugLine="h = stu.MeasureMultilineTextHeight(lbl, txt)";
_h = mostCurrent._vvvvvvvv3.MeasureMultilineTextHeight((android.widget.TextView)(_lbl.getObject()),BA.ObjectToCharSequence(_txt));
 //BA.debugLineNum = 163;BA.debugLine="Do While dt > limit Or h > lbl.Height";
while (_dt>_limit || _h>_lbl.getHeight()) {
 //BA.debugLineNum = 164;BA.debugLine="dt = dt / 2";
_dt = (float) (_dt/(double)2);
 //BA.debugLineNum = 165;BA.debugLine="h = stu.MeasureMultilineTextHeight(lbl, txt)";
_h = mostCurrent._vvvvvvvv3.MeasureMultilineTextHeight((android.widget.TextView)(_lbl.getObject()),BA.ObjectToCharSequence(_txt));
 //BA.debugLineNum = 166;BA.debugLine="If h > lbl.Height Then";
if (_h>_lbl.getHeight()) { 
 //BA.debugLineNum = 167;BA.debugLine="lbl.TextSize = lbl.TextSize - dt";
_lbl.setTextSize((float) (_lbl.getTextSize()-_dt));
 }else {
 //BA.debugLineNum = 169;BA.debugLine="lbl.TextSize = lbl.TextSize + dt";
_lbl.setTextSize((float) (_lbl.getTextSize()+_dt));
 };
 }
;
 //BA.debugLineNum = 172;BA.debugLine="End Sub";
return "";
}
public static String  _spnducan_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 87;BA.debugLine="Sub spnDucan_ItemClick (Position As Int, Value As";
 //BA.debugLineNum = 88;BA.debugLine="UcitajArhivuZaDucan(Value)";
_vvvvvvvvv1(BA.ObjectToString(_value));
 //BA.debugLineNum = 89;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvvv1(String _ducan) throws Exception{
anywheresoftware.b4a.sql.SQL.CursorWrapper _cursor1 = null;
int _i = 0;
com.katalozi2.arhiva._katalog2 _kat = null;
String _ss = "";
anywheresoftware.b4a.objects.B4XViewWrapper _p = null;
 //BA.debugLineNum = 50;BA.debugLine="Sub UcitajArhivuZaDucan(ducan As String)";
 //BA.debugLineNum = 51;BA.debugLine="Dim Cursor1 As Cursor";
_cursor1 = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
 //BA.debugLineNum = 53;BA.debugLine="If Main.manager.GetBoolean(\"check1\") Then";
if (mostCurrent._vvvvvv6._vv2 /*anywheresoftware.b4a.objects.preferenceactivity.PreferenceManager*/ .GetBoolean("check1")) { 
 //BA.debugLineNum = 54;BA.debugLine="clvA.Clear";
mostCurrent._clva._clear();
 };
 //BA.debugLineNum = 57;BA.debugLine="Cursor1 = Starter.upit.ExecQuery(\"SELECT naslov,";
_cursor1.setObject((android.database.Cursor)(mostCurrent._vvvvvv7._v5 /*anywheresoftware.b4a.sql.SQL*/ .ExecQuery("SELECT naslov, linkSlika, linkKatalog, opis, ducan FROM arhiva WHERE ducan = '"+_ducan+"'")));
 //BA.debugLineNum = 58;BA.debugLine="For i = 0 To Cursor1.RowCount - 1";
{
final int step6 = 1;
final int limit6 = (int) (_cursor1.getRowCount()-1);
_i = (int) (0) ;
for (;_i <= limit6 ;_i = _i + step6 ) {
 //BA.debugLineNum = 59;BA.debugLine="Cursor1.Position = i";
_cursor1.setPosition(_i);
 //BA.debugLineNum = 60;BA.debugLine="Dim kat As katalog2";
_kat = new com.katalozi2.arhiva._katalog2();
 //BA.debugLineNum = 61;BA.debugLine="kat.Initialize";
_kat.Initialize();
 //BA.debugLineNum = 62;BA.debugLine="Dim ss As String = Cursor1.GetString(\"naslov\")";
_ss = _cursor1.GetString("naslov");
 //BA.debugLineNum = 63;BA.debugLine="ss = ss.Replace(Chr(31), \"'\")";
_ss = _ss.replace(BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (31))),"'");
 //BA.debugLineNum = 64;BA.debugLine="ss = ss.Replace(Chr(30),Chr(34))";
_ss = _ss.replace(BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (30))),BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (34))));
 //BA.debugLineNum = 65;BA.debugLine="kat.naslov = ss'Cursor1.GetString(\"naslov\")";
_kat.naslov /*String*/  = _ss;
 //BA.debugLineNum = 66;BA.debugLine="kat.linkSlika = Cursor1.GetString(\"linkSlika\")";
_kat.linkSlika /*String*/  = _cursor1.GetString("linkSlika");
 //BA.debugLineNum = 67;BA.debugLine="kat.linkKatalog = Cursor1.GetString(\"linkKatalog";
_kat.linkKatalog /*String*/  = _cursor1.GetString("linkKatalog");
 //BA.debugLineNum = 68;BA.debugLine="kat.opis = Cursor1.GetString(\"opis\")";
_kat.opis /*String*/  = _cursor1.GetString("opis");
 //BA.debugLineNum = 69;BA.debugLine="Dim p As B4XView = xui.CreatePanel(\"\")";
_p = new anywheresoftware.b4a.objects.B4XViewWrapper();
_p = mostCurrent._vvvvvvvv2.CreatePanel(processBA,"");
 //BA.debugLineNum = 70;BA.debugLine="p.SetLayoutAnimated(0, 0, 0, clvA.AsView.Width,";
_p.SetLayoutAnimated((int) (0),(int) (0),(int) (0),mostCurrent._clva._asview().getWidth(),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (280)));
 //BA.debugLineNum = 71;BA.debugLine="clvA.Add(p, kat)";
mostCurrent._clva._add(_p,(Object)(_kat));
 //BA.debugLineNum = 72;BA.debugLine="listaKataloga.Add(kat)";
mostCurrent._vvvvvvv4.Add((Object)(_kat));
 }
};
 //BA.debugLineNum = 74;BA.debugLine="Cursor1.Close";
_cursor1.Close();
 //BA.debugLineNum = 75;BA.debugLine="End Sub";
return "";
}
}
