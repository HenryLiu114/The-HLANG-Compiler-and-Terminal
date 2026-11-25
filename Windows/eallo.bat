@echo off

powershell -STA -Command ^
"Add-Type -AssemblyName PresentationFramework; ^
if([System.Windows.MessageBox]::Show('Open with Notepad?','HLang','YesNo') -eq 'Yes'){Start-Process notepad '%~1.hlang'}"