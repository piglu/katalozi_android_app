B4A=true
Group=Default Group
ModulesStructureVersion=1
Type=Activity
Version=8.3
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: True
#End Region

Sub Process_Globals
	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.

End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.
	Dim wv As WebView
	Dim vrijednost As String
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	'Activity.LoadLayout("Layout1")
	wv.Initialize("wv")
	Activity.AddView(wv, 0, 0, 100%x, 100%y)

	DL_stranice
End Sub

Sub DL_stranice
	Dim j As HttpJob

	ProgressDialogShow("Preuzimanje...")
	j.Initialize("", Me)
	j.Download(Main.linkK)
	Wait For (j) JobDone(j As HttpJob)
	If j.Success Then
		ParsajStranicuZaLink(j.GetString)
	End If
	j.Release
	ProgressDialogHide
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub ParsajStranicuZaLink(stranica As String)
	Dim drugi As Boolean

	vrijednost = ""
'	Dim m As Matcher = Regex.Matcher($"<div id="katalog_show"><div data-configid="(\d+/\d+)" style="width:\d+px; height:\d+px;" class="issuuembed"></div>\s+<script type="text/rocketscript" data-rocketsrc="//e.issuu.com/embed.js" async="true"></script></div>"$, stranica)
'	Dim m As Matcher = Regex.Matcher($"<div id="katalog_show"><div data-configid="(\d+/\d+)" style="width:\d+px; height:\d+px;" class="issuuembed"></div>\s+<script type="text/javascript" src="//e.issuu.com/embed.js" async="true"></script></div>"$, stranica)
	Dim m As Matcher = Regex.Matcher($"<div id="katalog_show"><div data-configid="(\d+/\d+)""$, stranica)
	Do While m.Find
'		Log(m.Group(1))
		vrijednost = m.Group(1)
	Loop

	Dim m As Matcher = Regex.Matcher($"<div class="display_outer"><a href="(.*?)""$, stranica)
	Do While m.Find
'		Log(m.Group(1))
		vrijednost = m.Group(1)
		drugi = True
	Loop

	If vrijednost.Length > 1 Then
		PregledKataloga(drugi)
	Else
		Dim m As Matcher = Regex.Matcher($"src="(//e.issuu.com/embed.html#\d+/\d+)""$, stranica)
		' http://e.issuu.com/embed.html#5420532/42640638
		Do While m.Find
'			Log(m.Group(1))
			vrijednost = m.Group(1)
		Loop
'		vrijednost = "http:" & vrijednost
		If vrijednost.Length > 1 Then
			PregledKataloga2
		Else
			Msgbox("Neki problemi?!", "Greška")
			Activity.Finish
		End If
	End If

'	ProgressDialogHide
End Sub

Sub PregledKataloga(d As Boolean)
	If d Then
		wv.LoadUrl(vrijednost)
	Else
		wv.LoadUrl("http://e.issuu.com/embed.html#" & vrijednost)
	End If
End Sub

Sub PregledKataloga2
	wv.LoadUrl("http:" & vrijednost)
End Sub

Sub wv_PageFinished (Url As String)
	Log("pejdž finišd")
	ProgressDialogHide
End Sub
