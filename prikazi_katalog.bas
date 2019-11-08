B4A=true
Group=Default Group
ModulesStructureVersion=1
Type=Activity
Version=7.3
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: True
#End Region

Sub Process_Globals
	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.
	Private awake As PhoneWakeState
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
	If Main.manager.GetBoolean("check1") Then
		awake.KeepAlive(True)
	End If
End Sub

Sub Activity_Pause (UserClosed As Boolean)
	awake.ReleaseKeepAlive
End Sub

Sub ParsajStranicuZaLink(stranica As String)
	Dim prvi As Boolean = False

	vrijednost = ""

	Dim m As Matcher = Regex.Matcher($"<div id="katalog_show"><iframe allowfullscreen allow=".*" style=".*" src="(.*)"></iframe></div>"$, stranica)
	Do While m.Find
		vrijednost = m.Group(1)
		prvi = True
	Loop

	If prvi = False Then
		Dim m As Matcher = Regex.Matcher($"<div class="display_outer"><a href="(.*)" rel=".*" title=""><img alt="" title="" src=".*" /></a></div>"$, stranica)
		Do While m.Find
			vrijednost = m.Group(1)
		Loop
	End If
	
	PregledKataloga2
End Sub

'Sub PregledKataloga(d As Boolean)
'	If d Then
'		wv.LoadUrl(vrijednost)
'	Else
'		wv.LoadUrl("http://e.issuu.com/embed.html#" & vrijednost)
'	End If
'End Sub

Sub PregledKataloga2
	wv.LoadUrl("http:" & vrijednost)
'	wv.LoadUrl(vrijednost)
End Sub

Sub wv_PageFinished (Url As String)
'	Log("pejdž finišd")
	ProgressDialogHide
End Sub
