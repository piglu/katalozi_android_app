package com.katalozi2.designerscripts;
import anywheresoftware.b4a.objects.TextViewWrapper;
import anywheresoftware.b4a.objects.ImageViewWrapper;
import anywheresoftware.b4a.BA;


public class LS_stavka{

public static void LS_general(java.util.LinkedHashMap<String, anywheresoftware.b4a.keywords.LayoutBuilder.ViewWrapperAndAnchor> views, int width, int height, float scale) {
anywheresoftware.b4a.keywords.LayoutBuilder.setScaleRate(0.3);
//BA.debugLineNum = 3;BA.debugLine="If ActivitySize < 4.5 Then"[stavka/General script]
if ((anywheresoftware.b4a.keywords.LayoutBuilder.getScreenSize()<4.5d)) { 
;
//BA.debugLineNum = 4;BA.debugLine="lblTitle.TextSize = 18"[stavka/General script]
((anywheresoftware.b4a.keywords.LayoutBuilder.DesignerTextSizeMethod)views.get("lbltitle").vw).setTextSize((float)(18d));
//BA.debugLineNum = 5;BA.debugLine="ImageView1.SetLeftAndRight(Panel1.Width - 105dip, Panel1.Width - 5dip)"[stavka/General script]
views.get("imageview1").vw.setLeft((int)((views.get("panel1").vw.getWidth())-(105d * scale)));
views.get("imageview1").vw.setWidth((int)((views.get("panel1").vw.getWidth())-(5d * scale) - ((views.get("panel1").vw.getWidth())-(105d * scale))));
//BA.debugLineNum = 6;BA.debugLine="End If"[stavka/General script]
;};

}
}