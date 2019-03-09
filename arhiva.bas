B4A=true
Group=Default Group
ModulesStructureVersion=1
Type=Activity
Version=8.5
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
	Dim xui As XUI
	Private spnDucan As Spinner
	Private clvA As CustomListView
	Private lblTitle As Label
	Private ImageView1 As ImageView
	Private lblAction1 As Label
	Private lblContent As Label
	Dim stu As StringUtils
	Private listaKataloga As List
	Type katalog2(naslov As String, linkSlika As String, linkKatalog As String, opis As String)
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("arhiva")

	listaKataloga.Initialize

	DodajImenaDucana
End Sub

Sub DodajImenaDucana
	Dim Cursor1 As Cursor
	Dim l As List

	l.Initialize
	Cursor1 = Starter.upit.ExecQuery("SELECT DISTINCT ducan FROM arhiva")
	For i = 0 To Cursor1.RowCount - 1
		Cursor1.Position = i
		l.Add(Cursor1.GetString("ducan"))
	Next
	l.Sort(True)
	spnDucan.AddAll(l)
End Sub

Sub UcitajArhivuZaDucan(ducan As String)
	Dim Cursor1 As Cursor
	
	If Main.manager.GetBoolean("check1") Then
		clvA.Clear
	End If

	Cursor1 = Starter.upit.ExecQuery("SELECT naslov, linkSlika, linkKatalog, opis, ducan FROM arhiva WHERE ducan = '" & ducan & "'")
	For i = 0 To Cursor1.RowCount - 1
		Cursor1.Position = i
		Dim kat As katalog2
		kat.Initialize
		Dim ss As String = Cursor1.GetString("naslov")
		ss = ss.Replace(Chr(31), "'")
		ss = ss.Replace(Chr(30),Chr(34))
		kat.naslov = ss'Cursor1.GetString("naslov")
		kat.linkSlika = Cursor1.GetString("linkSlika")
		kat.linkKatalog = Cursor1.GetString("linkKatalog")
		kat.opis = Cursor1.GetString("opis")
		Dim p As B4XView = xui.CreatePanel("")
		p.SetLayoutAnimated(0, 0, 0, clvA.AsView.Width, 280dip)
		clvA.Add(p, kat)
		listaKataloga.Add(kat)
	Next
	Cursor1.Close
End Sub

Sub Activity_Resume
	If Main.manager.GetBoolean("check1") Then
		awake.KeepAlive(True)
	End If
End Sub

Sub Activity_Pause (UserClosed As Boolean)
	awake.ReleaseKeepAlive
End Sub

Sub spnDucan_ItemClick (Position As Int, Value As Object)
	UcitajArhivuZaDucan(Value)
End Sub

Sub dl_slike(lnk As String, iv As ImageView)
	Dim j As HttpJob

	ProgressDialogShow("Preuzimanje...")
	j.Initialize("", Me)
	j.Download(lnk)
	Wait For (j) JobDone(j As HttpJob)
	If j.Success Then
		iv.SetBackgroundImage(j.GetBitmap)
	End If
	j.Release
	ProgressDialogHide
End Sub

Sub clvA_VisibleRangeChanged (FirstIndex As Int, LastIndex As Int)
	Dim ExtraSize As Int = 20

	For i = 0 To clvA.Size - 1
		Dim p As B4XView = clvA.GetPanel(i)
		If i > FirstIndex - ExtraSize And i < LastIndex + ExtraSize Then
			'visible+
			If p.NumberOfViews = 0 Then
				Dim kk As katalog2 = clvA.GetValue(i)
				p.LoadLayout("stavka")
				lblTitle.Text = kk.naslov
				lblContent.Text = kk.opis
				SetTextSize(lblContent, lblContent.Text)
				SetColorStateList(lblAction1, xui.Color_LightGray, lblAction1.TextColor)
				dl_slike(kk.linkSlika, ImageView1)
			End If
		Else
			'not visible
			If p.NumberOfViews > 0 Then
				p.RemoveAllViews '<--- remove the layout
			End If
		End If
	Next
End Sub

Sub SetColorStateList(Btn As Label,Pressed As Int,Enabled As Int)
	Dim States(2,1) As Int
	States(0,0) = 16842919    'Pressed
	States(1,0) = 16842910    'Enabled

	Dim CSL As JavaObject
	CSL.InitializeNewInstance("android.content.res.ColorStateList",Array(States,Array As Int(Pressed, Enabled)))

	Dim B1 As JavaObject = Btn
	B1.RunMethod("setTextColor", Array As Object(CSL))
End Sub

Sub lblAction1_Click
	Dim index As Int = clvA.GetItemFromView(Sender)

	Dim kk As katalog2
	kk.Initialize
	kk = listaKataloga.Get(index)
	Log(kk.linkKatalog)

	Main.linkK = kk.linkKatalog
	StartActivity(prikazi_katalog)
End Sub

Sub SetTextSize(lbl As Label, txt As String)
	Dim dt As Float
	Dim limit = 0.5 As Float
	Dim h As Int

	lbl.Text = txt
	lbl.TextSize = 72
	dt = lbl.TextSize
	h = stu.MeasureMultilineTextHeight(lbl, txt)
	Do While dt > limit Or h > lbl.Height
		dt = dt / 2
		h = stu.MeasureMultilineTextHeight(lbl, txt)
		If h > lbl.Height Then
			lbl.TextSize = lbl.TextSize - dt
		Else
			lbl.TextSize = lbl.TextSize + dt
		End If
	Loop
End Sub