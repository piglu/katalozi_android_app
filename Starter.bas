B4A=true
Group=Default Group
ModulesStructureVersion=1
Type=Service
Version=7.3
@EndOfDesignText@
#Region  Service Attributes 
	#StartAtBoot: False
	#ExcludeFromLibrary: True
#End Region

Sub Process_Globals
	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.
'	Public rp As RuntimePermissions
'	Public FilePath As String
'	Dim mapa As Map
	Public upit As SQL
	Public SourceFolder As String
	Public rp As RuntimePermissions
End Sub

Sub Service_Create
	'This is the program entry point.
	'This is a good place to load resources that are not specific to a single activity.
	SourceFolder = rp.GetSafeDirDefaultExternal("")
	upit.Initialize(SourceFolder, "katalozi_arhiva.db", True)
'	upit.ExecNonQuery("DROP TABLE IF EXISTS arhiva")
	upit.ExecNonQuery("CREATE TABLE IF NOT EXISTS arhiva (id INTEGER PRIMARY KEY AUTOINCREMENT, naslov TEXT, linkSlika TEXT, linkKatalog TEXT, opis TEXT, ducan TEXT)")
End Sub

Sub Service_Start (StartingIntent As Intent)
'	mapa.Initialize
'	FilePath = rp.GetSafeDirDefaultExternal("")
'
'	If File.Exists(FilePath, "postavke") = False Then
'		mapa.Put("zadnji", "najnoviji")
'		File.WriteMap(FilePath, "postavke", mapa)
'	End If
End Sub

Sub Service_TaskRemoved
	'This event will be raised when the user removes the app from the recent apps list.
End Sub

'Return true to allow the OS default exceptions handler to handle the uncaught exception.
Sub Application_Error (Error As Exception, StackTrace As String) As Boolean
	Return True
End Sub

Sub Service_Destroy

End Sub
