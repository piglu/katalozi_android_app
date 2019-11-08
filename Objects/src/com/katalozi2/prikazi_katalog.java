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

public class prikazi_katalog extends Activity implements B4AActivity{
	public static prikazi_katalog mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "com.katalozi2", "com.katalozi2.prikazi_katalog");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (prikazi_katalog).");
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
		activityBA = new BA(this, layout, processBA, "com.katalozi2", "com.katalozi2.prikazi_katalog");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.katalozi2.prikazi_katalog", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (prikazi_katalog) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (prikazi_katalog) Resume **");
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
		return prikazi_katalog.class;
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
        BA.LogInfo("** Activity (prikazi_katalog) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
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
            prikazi_katalog mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (prikazi_katalog) Resume **");
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
public anywheresoftware.b4a.objects.WebViewWrapper _vvvvvvvv4 = null;
public static String _vvvvvvvv6 = "";
public com.katalozi2.main _vvvvvv6 = null;
public com.katalozi2.starter _vvvvvv7 = null;
public com.katalozi2.arhiva _vvvvvv0 = null;
public com.katalozi2.httputils2service _vvvvvvv1 = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 19;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 22;BA.debugLine="wv.Initialize(\"wv\")";
mostCurrent._vvvvvvvv4.Initialize(mostCurrent.activityBA,"wv");
 //BA.debugLineNum = 23;BA.debugLine="Activity.AddView(wv, 0, 0, 100%x, 100%y)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._vvvvvvvv4.getObject()),(int) (0),(int) (0),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 25;BA.debugLine="DL_stranice";
_dl_stranice();
 //BA.debugLineNum = 26;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 48;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 49;BA.debugLine="awake.ReleaseKeepAlive";
_vvvvvvv6.ReleaseKeepAlive();
 //BA.debugLineNum = 50;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 42;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 43;BA.debugLine="If Main.manager.GetBoolean(\"check1\") Then";
if (mostCurrent._vvvvvv6._vv2 /*anywheresoftware.b4a.objects.preferenceactivity.PreferenceManager*/ .GetBoolean("check1")) { 
 //BA.debugLineNum = 44;BA.debugLine="awake.KeepAlive(True)";
_vvvvvvv6.KeepAlive(processBA,anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 46;BA.debugLine="End Sub";
return "";
}
public static void  _dl_stranice() throws Exception{
ResumableSub_DL_stranice rsub = new ResumableSub_DL_stranice(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_DL_stranice extends BA.ResumableSub {
public ResumableSub_DL_stranice(com.katalozi2.prikazi_katalog parent) {
this.parent = parent;
}
com.katalozi2.prikazi_katalog parent;
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
 //BA.debugLineNum = 29;BA.debugLine="Dim j As HttpJob";
_j = new com.katalozi2.httpjob();
 //BA.debugLineNum = 31;BA.debugLine="ProgressDialogShow(\"Preuzimanje...\")";
anywheresoftware.b4a.keywords.Common.ProgressDialogShow(mostCurrent.activityBA,BA.ObjectToCharSequence("Preuzimanje..."));
 //BA.debugLineNum = 32;BA.debugLine="j.Initialize(\"\", Me)";
_j._initialize /*String*/ (processBA,"",prikazi_katalog.getObject());
 //BA.debugLineNum = 33;BA.debugLine="j.Download(Main.linkK)";
_j._vv7 /*String*/ (parent.mostCurrent._vvvvvv6._vv1 /*String*/ );
 //BA.debugLineNum = 34;BA.debugLine="Wait For (j) JobDone(j As HttpJob)";
anywheresoftware.b4a.keywords.Common.WaitFor("jobdone", processBA, this, (Object)(_j));
this.state = 5;
return;
case 5:
//C
this.state = 1;
_j = (com.katalozi2.httpjob) result[0];
;
 //BA.debugLineNum = 35;BA.debugLine="If j.Success Then";
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
 //BA.debugLineNum = 36;BA.debugLine="ParsajStranicuZaLink(j.GetString)";
_vvvvvvvv5(_j._vvv7 /*String*/ ());
 if (true) break;

case 4:
//C
this.state = -1;
;
 //BA.debugLineNum = 38;BA.debugLine="j.Release";
_j._vvvvv3 /*String*/ ();
 //BA.debugLineNum = 39;BA.debugLine="ProgressDialogHide";
anywheresoftware.b4a.keywords.Common.ProgressDialogHide();
 //BA.debugLineNum = 40;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static void  _jobdone(com.katalozi2.httpjob _j) throws Exception{
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 12;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 15;BA.debugLine="Dim wv As WebView";
mostCurrent._vvvvvvvv4 = new anywheresoftware.b4a.objects.WebViewWrapper();
 //BA.debugLineNum = 16;BA.debugLine="Dim vrijednost As String";
mostCurrent._vvvvvvvv6 = "";
 //BA.debugLineNum = 17;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvv5(String _stranica) throws Exception{
boolean _prvi = false;
anywheresoftware.b4a.keywords.Regex.MatcherWrapper _m = null;
 //BA.debugLineNum = 52;BA.debugLine="Sub ParsajStranicuZaLink(stranica As String)";
 //BA.debugLineNum = 53;BA.debugLine="Dim prvi As Boolean = False";
_prvi = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 55;BA.debugLine="vrijednost = \"\"";
mostCurrent._vvvvvvvv6 = "";
 //BA.debugLineNum = 57;BA.debugLine="Dim m As Matcher = Regex.Matcher($\"<div id=\"katal";
_m = new anywheresoftware.b4a.keywords.Regex.MatcherWrapper();
_m = anywheresoftware.b4a.keywords.Common.Regex.Matcher(("<div id=\"katalog_show\"><iframe allowfullscreen allow=\".*\" style=\".*\" src=\"(.*)\"></iframe></div>"),_stranica);
 //BA.debugLineNum = 58;BA.debugLine="Do While m.Find";
while (_m.Find()) {
 //BA.debugLineNum = 59;BA.debugLine="vrijednost = m.Group(1)";
mostCurrent._vvvvvvvv6 = _m.Group((int) (1));
 //BA.debugLineNum = 60;BA.debugLine="prvi = True";
_prvi = anywheresoftware.b4a.keywords.Common.True;
 }
;
 //BA.debugLineNum = 63;BA.debugLine="If prvi = False Then";
if (_prvi==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 64;BA.debugLine="Dim m As Matcher = Regex.Matcher($\"<div class=\"d";
_m = new anywheresoftware.b4a.keywords.Regex.MatcherWrapper();
_m = anywheresoftware.b4a.keywords.Common.Regex.Matcher(("<div class=\"display_outer\"><a href=\"(.*)\" rel=\".*\" title=\"\"><img alt=\"\" title=\"\" src=\".*\" /></a></div>"),_stranica);
 //BA.debugLineNum = 65;BA.debugLine="Do While m.Find";
while (_m.Find()) {
 //BA.debugLineNum = 66;BA.debugLine="vrijednost = m.Group(1)";
mostCurrent._vvvvvvvv6 = _m.Group((int) (1));
 }
;
 };
 //BA.debugLineNum = 70;BA.debugLine="PregledKataloga2";
_vvvvvvvv7();
 //BA.debugLineNum = 71;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvv7() throws Exception{
 //BA.debugLineNum = 81;BA.debugLine="Sub PregledKataloga2";
 //BA.debugLineNum = 82;BA.debugLine="wv.LoadUrl(\"http:\" & vrijednost)";
mostCurrent._vvvvvvvv4.LoadUrl("http:"+mostCurrent._vvvvvvvv6);
 //BA.debugLineNum = 84;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 9;BA.debugLine="Private awake As PhoneWakeState";
_vvvvvvv6 = new anywheresoftware.b4a.phone.Phone.PhoneWakeState();
 //BA.debugLineNum = 10;BA.debugLine="End Sub";
return "";
}
public static String  _wv_pagefinished(String _url) throws Exception{
 //BA.debugLineNum = 86;BA.debugLine="Sub wv_PageFinished (Url As String)";
 //BA.debugLineNum = 88;BA.debugLine="ProgressDialogHide";
anywheresoftware.b4a.keywords.Common.ProgressDialogHide();
 //BA.debugLineNum = 89;BA.debugLine="End Sub";
return "";
}
}
