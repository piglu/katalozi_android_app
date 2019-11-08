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

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "com.katalozi2", "com.katalozi2.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
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
		activityBA = new BA(this, layout, processBA, "com.katalozi2", "com.katalozi2.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.katalozi2.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
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
		return main.class;
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
        BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
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
            main mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
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
public static String _vv1 = "";
public static anywheresoftware.b4a.objects.preferenceactivity.PreferenceManager _vv2 = null;
public static anywheresoftware.b4a.objects.preferenceactivity.PreferenceScreenWrapper _vv3 = null;
public static anywheresoftware.b4a.phone.Phone.PhoneWakeState _vvvvvvv6 = null;
public static String _link_katalozi = "";
public b4a.example3.customlistview _clv1 = null;
public anywheresoftware.b4a.objects.collections.List _vvvvvvv4 = null;
public anywheresoftware.b4a.objects.collections.List _vvvvvvv5 = null;
public anywheresoftware.b4a.objects.B4XViewWrapper.XUI _vvvvvvvv2 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbltitle = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imageview1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblaction1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblcontent = null;
public anywheresoftware.b4a.objects.StringUtils _vvvvvvvv3 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edttrazi = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnizbrisi = null;
public static boolean _vvvvvvv7 = false;
public com.katalozi2.starter _vvvvvv7 = null;
public com.katalozi2.prikazi_katalog _prikazi_katalog = null;
public com.katalozi2.arhiva _vvvvvv0 = null;
public com.katalozi2.httputils2service _vvvvvvv1 = null;
public static class _katalog{
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

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
vis = vis | (prikazi_katalog.mostCurrent != null);
vis = vis | (arhiva.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 42;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 44;BA.debugLine="Activity.LoadLayout(\"clv\")";
mostCurrent._activity.LoadLayout("clv",mostCurrent.activityBA);
 //BA.debugLineNum = 46;BA.debugLine="If FirstTime Then";
if (_firsttime) { 
 //BA.debugLineNum = 47;BA.debugLine="CreatePreferenceScreen";
_vvvvvvv2();
 //BA.debugLineNum = 48;BA.debugLine="If manager.GetAll.Size = 0 Then SetDefaults";
if (_vv2.GetAll().getSize()==0) { 
_vvvvvvv3();};
 };
 //BA.debugLineNum = 51;BA.debugLine="Activity.AddMenuItem(\"Postavke\", \"postavke\")";
mostCurrent._activity.AddMenuItem(BA.ObjectToCharSequence("Postavke"),"postavke");
 //BA.debugLineNum = 52;BA.debugLine="Activity.AddMenuItem(\"Arhiva\", \"arhiva\")";
mostCurrent._activity.AddMenuItem(BA.ObjectToCharSequence("Arhiva"),"arhiva");
 //BA.debugLineNum = 53;BA.debugLine="Activity.AddMenuItem(\"O...\", \"o\")";
mostCurrent._activity.AddMenuItem(BA.ObjectToCharSequence("O..."),"o");
 //BA.debugLineNum = 55;BA.debugLine="listaKataloga.Initialize";
mostCurrent._vvvvvvv4.Initialize();
 //BA.debugLineNum = 56;BA.debugLine="listaKatalogaTrazi.Initialize";
mostCurrent._vvvvvvv5.Initialize();
 //BA.debugLineNum = 58;BA.debugLine="DL_Najnovije";
_dl_najnovije();
 //BA.debugLineNum = 59;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 244;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 245;BA.debugLine="awake.ReleaseKeepAlive";
_vvvvvvv6.ReleaseKeepAlive();
 //BA.debugLineNum = 246;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 238;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 239;BA.debugLine="If manager.GetBoolean(\"check1\") Then";
if (_vv2.GetBoolean("check1")) { 
 //BA.debugLineNum = 240;BA.debugLine="awake.KeepAlive(True)";
_vvvvvvv6.KeepAlive(processBA,anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 242;BA.debugLine="End Sub";
return "";
}
public static String  _arhiva_click() throws Exception{
 //BA.debugLineNum = 93;BA.debugLine="Sub arhiva_Click";
 //BA.debugLineNum = 94;BA.debugLine="StartActivity(arhiva)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._vvvvvv0.getObject()));
 //BA.debugLineNum = 95;BA.debugLine="End Sub";
return "";
}
public static String  _btnizbrisi_click() throws Exception{
 //BA.debugLineNum = 308;BA.debugLine="Sub btnIzbrisi_Click";
 //BA.debugLineNum = 309;BA.debugLine="If edtTrazi.Text.EqualsIgnoreCase(\"\") = False The";
if (mostCurrent._edttrazi.getText().equalsIgnoreCase("")==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 310;BA.debugLine="clv1.Clear";
mostCurrent._clv1._clear();
 //BA.debugLineNum = 311;BA.debugLine="edtTrazi.Text = \"\"";
mostCurrent._edttrazi.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 312;BA.debugLine="izTrazi = False";
_vvvvvvv7 = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 313;BA.debugLine="DL_Najnovije";
_dl_najnovije();
 };
 //BA.debugLineNum = 315;BA.debugLine="End Sub";
return "";
}
public static String  _clv1_visiblerangechanged(int _firstindex,int _lastindex) throws Exception{
int _extrasize = 0;
int _i = 0;
anywheresoftware.b4a.objects.B4XViewWrapper _p = null;
com.katalozi2.main._katalog _kk = null;
 //BA.debugLineNum = 201;BA.debugLine="Sub clv1_VisibleRangeChanged (FirstIndex As Int, L";
 //BA.debugLineNum = 202;BA.debugLine="Dim ExtraSize As Int = 20";
_extrasize = (int) (20);
 //BA.debugLineNum = 204;BA.debugLine="For i = 0 To clv1.Size - 1";
{
final int step2 = 1;
final int limit2 = (int) (mostCurrent._clv1._getsize()-1);
_i = (int) (0) ;
for (;_i <= limit2 ;_i = _i + step2 ) {
 //BA.debugLineNum = 205;BA.debugLine="Dim p As B4XView = clv1.GetPanel(i)";
_p = new anywheresoftware.b4a.objects.B4XViewWrapper();
_p = mostCurrent._clv1._getpanel(_i);
 //BA.debugLineNum = 206;BA.debugLine="If i > FirstIndex - ExtraSize And i < LastIndex";
if (_i>_firstindex-_extrasize && _i<_lastindex+_extrasize) { 
 //BA.debugLineNum = 208;BA.debugLine="If p.NumberOfViews = 0 Then";
if (_p.getNumberOfViews()==0) { 
 //BA.debugLineNum = 209;BA.debugLine="Dim kk As katalog = clv1.GetValue(i)";
_kk = (com.katalozi2.main._katalog)(mostCurrent._clv1._getvalue(_i));
 //BA.debugLineNum = 210;BA.debugLine="p.LoadLayout(\"stavka\")";
_p.LoadLayout("stavka",mostCurrent.activityBA);
 //BA.debugLineNum = 211;BA.debugLine="lblTitle.Text = kk.naslov";
mostCurrent._lbltitle.setText(BA.ObjectToCharSequence(_kk.naslov /*String*/ ));
 //BA.debugLineNum = 212;BA.debugLine="lblContent.Text = kk.opis";
mostCurrent._lblcontent.setText(BA.ObjectToCharSequence(_kk.opis /*String*/ ));
 //BA.debugLineNum = 213;BA.debugLine="SetTextSize(lblContent, lblContent.Text)";
_vvvvvvv0(mostCurrent._lblcontent,mostCurrent._lblcontent.getText());
 //BA.debugLineNum = 214;BA.debugLine="SetColorStateList(lblAction1, xui.Color_LightG";
_vvvvvvvv1(mostCurrent._lblaction1,mostCurrent._vvvvvvvv2.Color_LightGray,mostCurrent._lblaction1.getTextColor());
 //BA.debugLineNum = 215;BA.debugLine="dl_slike(kk.linkSlika, ImageView1)";
_dl_slike(_kk.linkSlika /*String*/ ,mostCurrent._imageview1);
 };
 }else {
 //BA.debugLineNum = 219;BA.debugLine="If p.NumberOfViews > 0 Then";
if (_p.getNumberOfViews()>0) { 
 //BA.debugLineNum = 220;BA.debugLine="p.RemoveAllViews '<--- remove the layout";
_p.RemoveAllViews();
 };
 };
 }
};
 //BA.debugLineNum = 224;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvv2() throws Exception{
anywheresoftware.b4a.objects.preferenceactivity.PreferenceCategoryWrapper _cat1 = null;
 //BA.debugLineNum = 74;BA.debugLine="Sub CreatePreferenceScreen";
 //BA.debugLineNum = 75;BA.debugLine="screen.Initialize(\"Postavke\", \"\")";
_vv3.Initialize("Postavke","");
 //BA.debugLineNum = 77;BA.debugLine="Dim cat1 As PreferenceCategory";
_cat1 = new anywheresoftware.b4a.objects.preferenceactivity.PreferenceCategoryWrapper();
 //BA.debugLineNum = 78;BA.debugLine="cat1.Initialize(\"Ostalo\")";
_cat1.Initialize("Ostalo");
 //BA.debugLineNum = 79;BA.debugLine="cat1.AddCheckBox(\"check1\", \"Arhiva\", \"Prikaži sam";
_cat1.AddCheckBox("check1","Arhiva","Prikaži samo odabrani dućan (inače svi)",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 80;BA.debugLine="cat1.AddCheckBox(\"check2\", \"Uključi\", \"Ekran uvij";
_cat1.AddCheckBox("check2","Uključi","Ekran uvijek uključen",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 89;BA.debugLine="screen.AddPreferenceCategory(cat1)";
_vv3.AddPreferenceCategory(_cat1);
 //BA.debugLineNum = 91;BA.debugLine="End Sub";
return "";
}
public static void  _dl_najnovije() throws Exception{
ResumableSub_DL_Najnovije rsub = new ResumableSub_DL_Najnovije(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_DL_Najnovije extends BA.ResumableSub {
public ResumableSub_DL_Najnovije(com.katalozi2.main parent) {
this.parent = parent;
}
com.katalozi2.main parent;
com.katalozi2.httpjob _j = null;
String _s = "";
anywheresoftware.b4a.keywords.Regex.MatcherWrapper _m = null;
com.katalozi2.main._katalog _kat = null;
String _ss = "";
anywheresoftware.b4a.sql.SQL.CursorWrapper _cursor1 = null;
anywheresoftware.b4a.objects.B4XViewWrapper _p = null;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 104;BA.debugLine="Dim j As HttpJob";
_j = new com.katalozi2.httpjob();
 //BA.debugLineNum = 106;BA.debugLine="listaKataloga.Clear";
parent.mostCurrent._vvvvvvv4.Clear();
 //BA.debugLineNum = 108;BA.debugLine="j.Initialize(\"\", Me)";
_j._initialize /*String*/ (processBA,"",main.getObject());
 //BA.debugLineNum = 109;BA.debugLine="j.Download(link_katalozi)";
_j._vv7 /*String*/ (parent.mostCurrent._link_katalozi);
 //BA.debugLineNum = 110;BA.debugLine="Wait For (j) JobDone(j As HttpJob)";
anywheresoftware.b4a.keywords.Common.WaitFor("jobdone", processBA, this, (Object)(_j));
this.state = 29;
return;
case 29:
//C
this.state = 1;
_j = (com.katalozi2.httpjob) result[0];
;
 //BA.debugLineNum = 111;BA.debugLine="If j.Success Then";
if (true) break;

case 1:
//if
this.state = 28;
if (_j._vvvvv5 /*boolean*/ ) { 
this.state = 3;
}else {
this.state = 27;
}if (true) break;

case 3:
//C
this.state = 4;
 //BA.debugLineNum = 112;BA.debugLine="Dim s As String = j.GetString";
_s = _j._vvv7 /*String*/ ();
 //BA.debugLineNum = 113;BA.debugLine="s = s.Replace(\"[&hellip;]\", \"...\")";
_s = _s.replace("[&hellip;]","...");
 //BA.debugLineNum = 114;BA.debugLine="s = s.Replace(\"&#038;\", \"&\")";
_s = _s.replace("&#038;","&");
 //BA.debugLineNum = 115;BA.debugLine="s = s.Replace(\"&#8211;\", \"-\")";
_s = _s.replace("&#8211;","-");
 //BA.debugLineNum = 116;BA.debugLine="s = s.Replace(\"&#8230;\", \"...\")";
_s = _s.replace("&#8230;","...");
 //BA.debugLineNum = 117;BA.debugLine="s = s.Replace(\"&#8216;\", \"-\")";
_s = _s.replace("&#8216;","-");
 //BA.debugLineNum = 118;BA.debugLine="s = s.Replace(\"&#038;\", \"&\")";
_s = _s.replace("&#038;","&");
 //BA.debugLineNum = 119;BA.debugLine="s = s.Replace(\"&amp;\", \"&\")";
_s = _s.replace("&amp;","&");
 //BA.debugLineNum = 120;BA.debugLine="s = s.Replace(\"&#8220;\", Chr(30))'$\"\"\"$)";
_s = _s.replace("&#8220;",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (30))));
 //BA.debugLineNum = 121;BA.debugLine="s = s.Replace(\"&#8221;\", Chr(30))'$\"\"\"$)";
_s = _s.replace("&#8221;",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (30))));
 //BA.debugLineNum = 122;BA.debugLine="s = s.Replace(\"&#8217;\", Chr(31))'\"'\")";
_s = _s.replace("&#8217;",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (31))));
 //BA.debugLineNum = 123;BA.debugLine="Dim m As Matcher = Regex.Matcher($\"<div class=\"m";
_m = new anywheresoftware.b4a.keywords.Regex.MatcherWrapper();
_m = anywheresoftware.b4a.keywords.Common.Regex.Matcher(("<div class=\"mainbox mt mb\">\\s+<div class=\"post\" id=\"post-\\d+\">\\s+<div class=\"details\">\\s+<div class=\"imgOuter\">\\s+<img alt=\"(.*?)\" title=\".*?\" src=\"(.*?)\" width=\"\\d+px\" height=\"\\d+px\" />\\s+</div>\\s+<div class=\"detailsOuter\">\\s+<h2 class=\"indextitle\">\\s+<a href=\"(.*?)\" title=\".*?\">.*?</a><span>.*?</span>\\s+</h2>\\s+<p>(.*?)</p>\\s+</div>\\s+<div class=\"clear\"></div>\\s+<a href=\".*?\" class=\"permalink\" title=\".*?\">.*?</a>\\s+<div class=\"button_over extra\">\\s+<div class=\"over_div extrahide\">\\s+<div class=\"extrainner\">\\s+<a href=\".*?\" title=\".*?\" class=\"izd\">.*?</a><a href=\".*?\" title=\".*?</a><a href=\".*?\" title=\".*?\">.*?</a><a href=\".*?\" title=\".*?\">.*?</a>\\s+</div>\\s+</div>\\s+</div>\\s+</div>\\s+</div>\\s+</div>"),_s);
 //BA.debugLineNum = 124;BA.debugLine="If m.Find Then";
if (true) break;

case 4:
//if
this.state = 25;
if (_m.Find()) { 
this.state = 6;
}else {
this.state = 16;
}if (true) break;

case 6:
//C
this.state = 7;
 //BA.debugLineNum = 125;BA.debugLine="Log(\"matcher 1\")";
anywheresoftware.b4a.keywords.Common.LogImpl("1524310","matcher 1",0);
 //BA.debugLineNum = 126;BA.debugLine="Do While m.Find";
if (true) break;

case 7:
//do while
this.state = 14;
while (_m.Find()) {
this.state = 9;
if (true) break;
}
if (true) break;

case 9:
//C
this.state = 10;
 //BA.debugLineNum = 127;BA.debugLine="Dim kat As katalog";
_kat = new com.katalozi2.main._katalog();
 //BA.debugLineNum = 128;BA.debugLine="kat.Initialize";
_kat.Initialize();
 //BA.debugLineNum = 129;BA.debugLine="Dim ss As String = m.Group(1)'cursor1.GetStrin";
_ss = _m.Group((int) (1));
 //BA.debugLineNum = 130;BA.debugLine="ss = ss.Replace(Chr(31), \"'\")";
_ss = _ss.replace(BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (31))),"'");
 //BA.debugLineNum = 131;BA.debugLine="ss = ss.Replace(Chr(30),Chr(34))";
_ss = _ss.replace(BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (30))),BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (34))));
 //BA.debugLineNum = 132;BA.debugLine="kat.naslov = ss'Cursor1.GetString(\"naslov\")";
_kat.naslov /*String*/  = _ss;
 //BA.debugLineNum = 134;BA.debugLine="kat.linkSlika = m.Group(2)";
_kat.linkSlika /*String*/  = _m.Group((int) (2));
 //BA.debugLineNum = 135;BA.debugLine="kat.linkKatalog = m.Group(3)";
_kat.linkKatalog /*String*/  = _m.Group((int) (3));
 //BA.debugLineNum = 136;BA.debugLine="kat.opis = m.Group(4)'s";
_kat.opis /*String*/  = _m.Group((int) (4));
 //BA.debugLineNum = 137;BA.debugLine="Dim ss As String = m.Group(3)";
_ss = _m.Group((int) (3));
 //BA.debugLineNum = 138;BA.debugLine="ss = ss.SubString(ss.LastIndexOf(\"/\")+1)";
_ss = _ss.substring((int) (_ss.lastIndexOf("/")+1));
 //BA.debugLineNum = 139;BA.debugLine="ss = ss.SubString2(0, ss.IndexOf(\"-\"))";
_ss = _ss.substring((int) (0),_ss.indexOf("-"));
 //BA.debugLineNum = 140;BA.debugLine="ss = ss.SubString2(0, 1).ToUpperCase & ss.SubS";
_ss = _ss.substring((int) (0),(int) (1)).toUpperCase()+_ss.substring((int) (1),_ss.length());
 //BA.debugLineNum = 142;BA.debugLine="Dim cursor1 As Cursor";
_cursor1 = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
 //BA.debugLineNum = 143;BA.debugLine="cursor1 = Starter.upit.ExecQuery($\"SELECT nasl";
_cursor1.setObject((android.database.Cursor)(parent.mostCurrent._vvvvvv7._v5 /*anywheresoftware.b4a.sql.SQL*/ .ExecQuery(("SELECT naslov FROM arhiva WHERE naslov = '"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_m.Group((int) (1))))+"'"))));
 //BA.debugLineNum = 144;BA.debugLine="If cursor1.RowCount < 1 Then";
if (true) break;

case 10:
//if
this.state = 13;
if (_cursor1.getRowCount()<1) { 
this.state = 12;
}if (true) break;

case 12:
//C
this.state = 13;
 //BA.debugLineNum = 145;BA.debugLine="Log(\"već postoji\")";
anywheresoftware.b4a.keywords.Common.LogImpl("1524330","već postoji",0);
 //BA.debugLineNum = 146;BA.debugLine="Starter.upit.ExecNonQuery($\"INSERT INTO arhiv";
parent.mostCurrent._vvvvvv7._v5 /*anywheresoftware.b4a.sql.SQL*/ .ExecNonQuery(("INSERT INTO arhiva VALUES (?, '"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_m.Group((int) (1))))+"', '"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_m.Group((int) (2))))+"', '"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_m.Group((int) (3))))+"', '"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_m.Group((int) (4))))+"', '"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_ss))+"')"));
 if (true) break;

case 13:
//C
this.state = 7;
;
 //BA.debugLineNum = 148;BA.debugLine="Dim p As B4XView = xui.CreatePanel(\"\")";
_p = new anywheresoftware.b4a.objects.B4XViewWrapper();
_p = parent.mostCurrent._vvvvvvvv2.CreatePanel(processBA,"");
 //BA.debugLineNum = 149;BA.debugLine="p.SetLayoutAnimated(0, 0, 0, clv1.AsView.Width";
_p.SetLayoutAnimated((int) (0),(int) (0),(int) (0),parent.mostCurrent._clv1._asview().getWidth(),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (280)));
 //BA.debugLineNum = 150;BA.debugLine="clv1.Add(p, kat)";
parent.mostCurrent._clv1._add(_p,(Object)(_kat));
 //BA.debugLineNum = 151;BA.debugLine="listaKataloga.Add(kat)";
parent.mostCurrent._vvvvvvv4.Add((Object)(_kat));
 if (true) break;

case 14:
//C
this.state = 25;
;
 if (true) break;

case 16:
//C
this.state = 17;
 //BA.debugLineNum = 154;BA.debugLine="Log(\"matcher 2\")";
anywheresoftware.b4a.keywords.Common.LogImpl("1524339","matcher 2",0);
 //BA.debugLineNum = 155;BA.debugLine="Dim m As Matcher = Regex.Matcher($\"<div class=\"";
_m = new anywheresoftware.b4a.keywords.Regex.MatcherWrapper();
_m = anywheresoftware.b4a.keywords.Common.Regex.Matcher(("<div class=\"post p\\d+\" id=\"post-\\d+\">\\s+<a class=\".*?\" id=\"arrow-\\d+\" href=\".*?;\"></a>\\s+<div class=\".*?\">\\s+<div class=\".*?\">\\s+<img alt=\".*?\" title=\"(.*?)\" src=\"(.*?)\" />\\s+</div>\\s+</div>\\s+<h2><a class=\".*?\" href=\"(.*?)\">.*?</a></h2>\\s+<div class=\".*?\">\\s+<div class=\".*?\">&raquo;<a href=\".*?\" class=\".*?\" title=\".*?\">.*?</a></div><div class=\".*?\">(.*?)</div>\\s+</div>"),_s);
 //BA.debugLineNum = 156;BA.debugLine="Do While m.Find";
if (true) break;

case 17:
//do while
this.state = 24;
while (_m.Find()) {
this.state = 19;
if (true) break;
}
if (true) break;

case 19:
//C
this.state = 20;
 //BA.debugLineNum = 157;BA.debugLine="Dim kat As katalog";
_kat = new com.katalozi2.main._katalog();
 //BA.debugLineNum = 158;BA.debugLine="kat.Initialize";
_kat.Initialize();
 //BA.debugLineNum = 159;BA.debugLine="kat.naslov = m.Group(1)";
_kat.naslov /*String*/  = _m.Group((int) (1));
 //BA.debugLineNum = 160;BA.debugLine="kat.linkSlika = m.Group(2)";
_kat.linkSlika /*String*/  = _m.Group((int) (2));
 //BA.debugLineNum = 161;BA.debugLine="kat.linkKatalog = m.Group(3)";
_kat.linkKatalog /*String*/  = _m.Group((int) (3));
 //BA.debugLineNum = 162;BA.debugLine="kat.opis = m.Group(4)'s";
_kat.opis /*String*/  = _m.Group((int) (4));
 //BA.debugLineNum = 163;BA.debugLine="Dim ss As String = m.Group(3)";
_ss = _m.Group((int) (3));
 //BA.debugLineNum = 164;BA.debugLine="ss = ss.SubString(ss.LastIndexOf(\"/\")+1)";
_ss = _ss.substring((int) (_ss.lastIndexOf("/")+1));
 //BA.debugLineNum = 165;BA.debugLine="ss = ss.SubString2(0, ss.IndexOf(\"-\"))";
_ss = _ss.substring((int) (0),_ss.indexOf("-"));
 //BA.debugLineNum = 166;BA.debugLine="ss = ss.SubString2(0, 1).ToUpperCase & ss.SubS";
_ss = _ss.substring((int) (0),(int) (1)).toUpperCase()+_ss.substring((int) (1),_ss.length());
 //BA.debugLineNum = 168;BA.debugLine="Dim cursor1 As Cursor";
_cursor1 = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
 //BA.debugLineNum = 169;BA.debugLine="cursor1 = Starter.upit.ExecQuery($\"SELECT nasl";
_cursor1.setObject((android.database.Cursor)(parent.mostCurrent._vvvvvv7._v5 /*anywheresoftware.b4a.sql.SQL*/ .ExecQuery(("SELECT naslov FROM arhiva WHERE naslov = '"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_m.Group((int) (1))))+"'"))));
 //BA.debugLineNum = 170;BA.debugLine="If cursor1.RowCount < 1 Then";
if (true) break;

case 20:
//if
this.state = 23;
if (_cursor1.getRowCount()<1) { 
this.state = 22;
}if (true) break;

case 22:
//C
this.state = 23;
 //BA.debugLineNum = 171;BA.debugLine="Log(\"već postoji\")";
anywheresoftware.b4a.keywords.Common.LogImpl("1524356","već postoji",0);
 //BA.debugLineNum = 172;BA.debugLine="Starter.upit.ExecNonQuery($\"INSERT INTO arhiv";
parent.mostCurrent._vvvvvv7._v5 /*anywheresoftware.b4a.sql.SQL*/ .ExecNonQuery(("INSERT INTO arhiva VALUES (?, '"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_m.Group((int) (1))))+"', '"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_m.Group((int) (2))))+"', '"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_m.Group((int) (3))))+"', '"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_m.Group((int) (4))))+"', '"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_ss))+"')"));
 if (true) break;

case 23:
//C
this.state = 17;
;
 //BA.debugLineNum = 174;BA.debugLine="Dim p As B4XView = xui.CreatePanel(\"\")";
_p = new anywheresoftware.b4a.objects.B4XViewWrapper();
_p = parent.mostCurrent._vvvvvvvv2.CreatePanel(processBA,"");
 //BA.debugLineNum = 175;BA.debugLine="p.SetLayoutAnimated(0, 0, 0, clv1.AsView.Width";
_p.SetLayoutAnimated((int) (0),(int) (0),(int) (0),parent.mostCurrent._clv1._asview().getWidth(),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (280)));
 //BA.debugLineNum = 176;BA.debugLine="clv1.Add(p, kat)";
parent.mostCurrent._clv1._add(_p,(Object)(_kat));
 //BA.debugLineNum = 177;BA.debugLine="listaKataloga.Add(kat)";
parent.mostCurrent._vvvvvvv4.Add((Object)(_kat));
 if (true) break;

case 24:
//C
this.state = 25;
;
 if (true) break;

case 25:
//C
this.state = 28;
;
 if (true) break;

case 27:
//C
this.state = 28;
 //BA.debugLineNum = 181;BA.debugLine="Log(\"neka greška!\")";
anywheresoftware.b4a.keywords.Common.LogImpl("1524366","neka greška!",0);
 if (true) break;

case 28:
//C
this.state = -1;
;
 //BA.debugLineNum = 184;BA.debugLine="j.Release";
_j._vvvvv3 /*String*/ ();
 //BA.debugLineNum = 185;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static void  _jobdone(com.katalozi2.httpjob _j) throws Exception{
}
public static void  _dl_slike(String _lnk,anywheresoftware.b4a.objects.ImageViewWrapper _iv) throws Exception{
ResumableSub_dl_slike rsub = new ResumableSub_dl_slike(null,_lnk,_iv);
rsub.resume(processBA, null);
}
public static class ResumableSub_dl_slike extends BA.ResumableSub {
public ResumableSub_dl_slike(com.katalozi2.main parent,String _lnk,anywheresoftware.b4a.objects.ImageViewWrapper _iv) {
this.parent = parent;
this._lnk = _lnk;
this._iv = _iv;
}
com.katalozi2.main parent;
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
 //BA.debugLineNum = 188;BA.debugLine="Dim j As HttpJob";
_j = new com.katalozi2.httpjob();
 //BA.debugLineNum = 190;BA.debugLine="ProgressDialogShow(\"Preuzimanje...\")";
anywheresoftware.b4a.keywords.Common.ProgressDialogShow(mostCurrent.activityBA,BA.ObjectToCharSequence("Preuzimanje..."));
 //BA.debugLineNum = 191;BA.debugLine="j.Initialize(\"\", Me)";
_j._initialize /*String*/ (processBA,"",main.getObject());
 //BA.debugLineNum = 192;BA.debugLine="j.Download(lnk)";
_j._vv7 /*String*/ (_lnk);
 //BA.debugLineNum = 193;BA.debugLine="Wait For (j) JobDone(j As HttpJob)";
anywheresoftware.b4a.keywords.Common.WaitFor("jobdone", processBA, this, (Object)(_j));
this.state = 5;
return;
case 5:
//C
this.state = 1;
_j = (com.katalozi2.httpjob) result[0];
;
 //BA.debugLineNum = 194;BA.debugLine="If j.Success Then";
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
 //BA.debugLineNum = 195;BA.debugLine="iv.SetBackgroundImage(j.GetBitmap)";
_iv.SetBackgroundImageNew((android.graphics.Bitmap)(_j._vvv2 /*anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper*/ ().getObject()));
 if (true) break;

case 4:
//C
this.state = -1;
;
 //BA.debugLineNum = 197;BA.debugLine="j.Release";
_j._vvvvv3 /*String*/ ();
 //BA.debugLineNum = 198;BA.debugLine="ProgressDialogHide";
anywheresoftware.b4a.keywords.Common.ProgressDialogHide();
 //BA.debugLineNum = 199;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _edttrazi_enterpressed() throws Exception{
com.katalozi2.main._katalog _kat = null;
int _i = 0;
String _s = "";
anywheresoftware.b4a.objects.B4XViewWrapper _p = null;
 //BA.debugLineNum = 285;BA.debugLine="Sub edtTrazi_EnterPressed";
 //BA.debugLineNum = 286;BA.debugLine="Dim kat As katalog";
_kat = new com.katalozi2.main._katalog();
 //BA.debugLineNum = 288;BA.debugLine="izTrazi = True";
_vvvvvvv7 = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 289;BA.debugLine="listaKatalogaTrazi.Clear";
mostCurrent._vvvvvvv5.Clear();
 //BA.debugLineNum = 290;BA.debugLine="clv1.Clear";
mostCurrent._clv1._clear();
 //BA.debugLineNum = 291;BA.debugLine="kat.Initialize";
_kat.Initialize();
 //BA.debugLineNum = 292;BA.debugLine="For i = 0 To listaKataloga.Size - 1";
{
final int step6 = 1;
final int limit6 = (int) (mostCurrent._vvvvvvv4.getSize()-1);
_i = (int) (0) ;
for (;_i <= limit6 ;_i = _i + step6 ) {
 //BA.debugLineNum = 293;BA.debugLine="Dim s As String = listaKataloga.Get(i)";
_s = BA.ObjectToString(mostCurrent._vvvvvvv4.Get(_i));
 //BA.debugLineNum = 294;BA.debugLine="If s.Contains(edtTrazi.Text) Then";
if (_s.contains(mostCurrent._edttrazi.getText())) { 
 //BA.debugLineNum = 295;BA.debugLine="kat = listaKataloga.Get(i)";
_kat = (com.katalozi2.main._katalog)(mostCurrent._vvvvvvv4.Get(_i));
 //BA.debugLineNum = 296;BA.debugLine="Dim p As B4XView = xui.CreatePanel(\"\")";
_p = new anywheresoftware.b4a.objects.B4XViewWrapper();
_p = mostCurrent._vvvvvvvv2.CreatePanel(processBA,"");
 //BA.debugLineNum = 297;BA.debugLine="p.SetLayoutAnimated(0, 0, 0, clv1.AsView.Width,";
_p.SetLayoutAnimated((int) (0),(int) (0),(int) (0),mostCurrent._clv1._asview().getWidth(),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (280)));
 //BA.debugLineNum = 298;BA.debugLine="clv1.Add(p, kat)";
mostCurrent._clv1._add(_p,(Object)(_kat));
 //BA.debugLineNum = 299;BA.debugLine="listaKatalogaTrazi.Add(kat)";
mostCurrent._vvvvvvv5.Add((Object)(_kat));
 };
 }
};
 //BA.debugLineNum = 302;BA.debugLine="If clv1.Size = 0 Then";
if (mostCurrent._clv1._getsize()==0) { 
 //BA.debugLineNum = 303;BA.debugLine="MsgboxAsync(\"Nema traženog pojma!\", \"Info\")";
anywheresoftware.b4a.keywords.Common.MsgboxAsync(BA.ObjectToCharSequence("Nema traženog pojma!"),BA.ObjectToCharSequence("Info"),processBA);
 //BA.debugLineNum = 304;BA.debugLine="btnIzbrisi_Click";
_btnizbrisi_click();
 };
 //BA.debugLineNum = 306;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 24;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 27;BA.debugLine="Dim link_katalozi As String = \"http://katalozi.ne";
mostCurrent._link_katalozi = "http://katalozi.net";
 //BA.debugLineNum = 28;BA.debugLine="Private clv1 As CustomListView";
mostCurrent._clv1 = new b4a.example3.customlistview();
 //BA.debugLineNum = 29;BA.debugLine="Type katalog(naslov As String, linkSlika As Strin";
;
 //BA.debugLineNum = 30;BA.debugLine="Dim listaKataloga, listaKatalogaTrazi As List";
mostCurrent._vvvvvvv4 = new anywheresoftware.b4a.objects.collections.List();
mostCurrent._vvvvvvv5 = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 31;BA.debugLine="Dim xui As XUI";
mostCurrent._vvvvvvvv2 = new anywheresoftware.b4a.objects.B4XViewWrapper.XUI();
 //BA.debugLineNum = 32;BA.debugLine="Private lblTitle As Label";
mostCurrent._lbltitle = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 33;BA.debugLine="Private ImageView1 As ImageView";
mostCurrent._imageview1 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 34;BA.debugLine="Private lblAction1 As Label";
mostCurrent._lblaction1 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 35;BA.debugLine="Private lblContent As Label";
mostCurrent._lblcontent = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 36;BA.debugLine="Dim stu As StringUtils";
mostCurrent._vvvvvvvv3 = new anywheresoftware.b4a.objects.StringUtils();
 //BA.debugLineNum = 37;BA.debugLine="Private edtTrazi As EditText";
mostCurrent._edttrazi = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 38;BA.debugLine="Private btnIzbrisi As Button";
mostCurrent._btnizbrisi = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 39;BA.debugLine="Dim izTrazi As Boolean";
_vvvvvvv7 = false;
 //BA.debugLineNum = 40;BA.debugLine="End Sub";
return "";
}
public static String  _lblaction1_click() throws Exception{
int _index = 0;
com.katalozi2.main._katalog _kk = null;
 //BA.debugLineNum = 248;BA.debugLine="Sub lblAction1_Click";
 //BA.debugLineNum = 249;BA.debugLine="Dim index As Int = clv1.GetItemFromView(Sender)";
_index = mostCurrent._clv1._getitemfromview((anywheresoftware.b4a.objects.B4XViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.B4XViewWrapper(), (java.lang.Object)(anywheresoftware.b4a.keywords.Common.Sender(mostCurrent.activityBA))));
 //BA.debugLineNum = 251;BA.debugLine="Dim kk As katalog";
_kk = new com.katalozi2.main._katalog();
 //BA.debugLineNum = 252;BA.debugLine="kk.Initialize";
_kk.Initialize();
 //BA.debugLineNum = 253;BA.debugLine="If izTrazi Then";
if (_vvvvvvv7) { 
 //BA.debugLineNum = 254;BA.debugLine="kk = listaKatalogaTrazi.Get(index)";
_kk = (com.katalozi2.main._katalog)(mostCurrent._vvvvvvv5.Get(_index));
 }else {
 //BA.debugLineNum = 256;BA.debugLine="kk = listaKataloga.Get(index)";
_kk = (com.katalozi2.main._katalog)(mostCurrent._vvvvvvv4.Get(_index));
 };
 //BA.debugLineNum = 258;BA.debugLine="Log(kk.linkKatalog)";
anywheresoftware.b4a.keywords.Common.LogImpl("1917514",_kk.linkKatalog /*String*/ ,0);
 //BA.debugLineNum = 260;BA.debugLine="linkK = kk.linkKatalog";
_vv1 = _kk.linkKatalog /*String*/ ;
 //BA.debugLineNum = 262;BA.debugLine="StartActivity(prikazi_katalog)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._prikazi_katalog.getObject()));
 //BA.debugLineNum = 263;BA.debugLine="End Sub";
return "";
}
public static String  _o_click() throws Exception{
 //BA.debugLineNum = 97;BA.debugLine="Sub o_Click";
 //BA.debugLineNum = 98;BA.debugLine="MsgboxAsync(\"Prikaz najnovijih kataloga\" & CRLF &";
anywheresoftware.b4a.keywords.Common.MsgboxAsync(BA.ObjectToCharSequence("Prikaz najnovijih kataloga"+anywheresoftware.b4a.keywords.Common.CRLF+"sa stranice katalozi.net"+anywheresoftware.b4a.keywords.Common.CRLF+"v3.0"),BA.ObjectToCharSequence("Info"),processBA);
 //BA.debugLineNum = 99;BA.debugLine="End Sub";
return "";
}
public static String  _postavke_click() throws Exception{
 //BA.debugLineNum = 61;BA.debugLine="Sub postavke_Click";
 //BA.debugLineNum = 62;BA.debugLine="StartActivity(screen.CreateIntent)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_vv3.CreateIntent()));
 //BA.debugLineNum = 63;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
starter._process_globals();
prikazi_katalog._process_globals();
arhiva._process_globals();
httputils2service._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 18;BA.debugLine="Dim linkK As String";
_vv1 = "";
 //BA.debugLineNum = 19;BA.debugLine="Public manager As PreferenceManager";
_vv2 = new anywheresoftware.b4a.objects.preferenceactivity.PreferenceManager();
 //BA.debugLineNum = 20;BA.debugLine="Dim screen As PreferenceScreen";
_vv3 = new anywheresoftware.b4a.objects.preferenceactivity.PreferenceScreenWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Private awake As PhoneWakeState";
_vvvvvvv6 = new anywheresoftware.b4a.phone.Phone.PhoneWakeState();
 //BA.debugLineNum = 22;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvvv1(anywheresoftware.b4a.objects.LabelWrapper _btn,int _pressed,int _enabled) throws Exception{
int[][] _states = null;
anywheresoftware.b4j.object.JavaObject _csl = null;
anywheresoftware.b4j.object.JavaObject _b1 = null;
 //BA.debugLineNum = 226;BA.debugLine="Sub SetColorStateList(Btn As Label,Pressed As Int,";
 //BA.debugLineNum = 227;BA.debugLine="Dim States(2, 1) As Int";
_states = new int[(int) (2)][];
{
int d0 = _states.length;
int d1 = (int) (1);
for (int i0 = 0;i0 < d0;i0++) {
_states[i0] = new int[d1];
}
}
;
 //BA.debugLineNum = 228;BA.debugLine="States(0,0) = 16842919    'Pressed";
_states[(int) (0)][(int) (0)] = (int) (16842919);
 //BA.debugLineNum = 229;BA.debugLine="States(1,0) = 16842910    'Enabled";
_states[(int) (1)][(int) (0)] = (int) (16842910);
 //BA.debugLineNum = 231;BA.debugLine="Dim CSL As JavaObject";
_csl = new anywheresoftware.b4j.object.JavaObject();
 //BA.debugLineNum = 232;BA.debugLine="CSL.InitializeNewInstance(\"android.content.res.Co";
_csl.InitializeNewInstance("android.content.res.ColorStateList",new Object[]{(Object)(_states),(Object)(new int[]{_pressed,_enabled})});
 //BA.debugLineNum = 234;BA.debugLine="Dim B1 As JavaObject = Btn";
_b1 = new anywheresoftware.b4j.object.JavaObject();
_b1.setObject((java.lang.Object)(_btn.getObject()));
 //BA.debugLineNum = 235;BA.debugLine="B1.RunMethod(\"setTextColor\", Array As Object(CSL)";
_b1.RunMethod("setTextColor",new Object[]{(Object)(_csl.getObject())});
 //BA.debugLineNum = 236;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvv3() throws Exception{
 //BA.debugLineNum = 65;BA.debugLine="Sub SetDefaults";
 //BA.debugLineNum = 67;BA.debugLine="manager.SetBoolean(\"check1\", True)";
_vv2.SetBoolean("check1",anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 68;BA.debugLine="manager.SetBoolean(\"check2\", True)";
_vv2.SetBoolean("check2",anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 72;BA.debugLine="End Sub";
return "";
}
public static String  _vvvvvvv0(anywheresoftware.b4a.objects.LabelWrapper _lbl,String _txt) throws Exception{
float _dt = 0f;
float _limit = 0f;
int _h = 0;
 //BA.debugLineNum = 265;BA.debugLine="Sub SetTextSize(lbl As Label, txt As String)";
 //BA.debugLineNum = 266;BA.debugLine="Dim dt As Float";
_dt = 0f;
 //BA.debugLineNum = 267;BA.debugLine="Dim limit = 0.5 As Float";
_limit = (float) (0.5);
 //BA.debugLineNum = 268;BA.debugLine="Dim h As Int";
_h = 0;
 //BA.debugLineNum = 270;BA.debugLine="lbl.Text = txt";
_lbl.setText(BA.ObjectToCharSequence(_txt));
 //BA.debugLineNum = 271;BA.debugLine="lbl.TextSize = 72";
_lbl.setTextSize((float) (72));
 //BA.debugLineNum = 272;BA.debugLine="dt = lbl.TextSize";
_dt = _lbl.getTextSize();
 //BA.debugLineNum = 273;BA.debugLine="h = stu.MeasureMultilineTextHeight(lbl, txt)";
_h = mostCurrent._vvvvvvvv3.MeasureMultilineTextHeight((android.widget.TextView)(_lbl.getObject()),BA.ObjectToCharSequence(_txt));
 //BA.debugLineNum = 274;BA.debugLine="Do While dt > limit Or h > lbl.Height";
while (_dt>_limit || _h>_lbl.getHeight()) {
 //BA.debugLineNum = 275;BA.debugLine="dt = dt / 2";
_dt = (float) (_dt/(double)2);
 //BA.debugLineNum = 276;BA.debugLine="h = stu.MeasureMultilineTextHeight(lbl, txt)";
_h = mostCurrent._vvvvvvvv3.MeasureMultilineTextHeight((android.widget.TextView)(_lbl.getObject()),BA.ObjectToCharSequence(_txt));
 //BA.debugLineNum = 277;BA.debugLine="If h > lbl.Height Then";
if (_h>_lbl.getHeight()) { 
 //BA.debugLineNum = 278;BA.debugLine="lbl.TextSize = lbl.TextSize - dt";
_lbl.setTextSize((float) (_lbl.getTextSize()-_dt));
 }else {
 //BA.debugLineNum = 280;BA.debugLine="lbl.TextSize = lbl.TextSize + dt";
_lbl.setTextSize((float) (_lbl.getTextSize()+_dt));
 };
 }
;
 //BA.debugLineNum = 283;BA.debugLine="End Sub";
return "";
}
}
