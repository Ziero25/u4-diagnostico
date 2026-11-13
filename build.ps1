param(
    [string]$SdkPath,
    [string]$JavaBin,
    [string]$BuildToolsVersion = '34.0.0',
    [string]$Platform = 'android-35',
    [string]$KeystorePath,
    [string]$StorePassword = $env:U4_STORE_PASSWORD,
    [string]$KeyPassword = $env:U4_KEY_PASSWORD,
    [string]$KeyAlias = 'diagnostico'
)
$ErrorActionPreference = 'Stop'
if (!$SdkPath) { $SdkPath = $env:ANDROID_SDK_ROOT }
if (!$SdkPath) { $SdkPath = $env:ANDROID_HOME }
if (!$SdkPath -and $env:LOCALAPPDATA) { $SdkPath = Join-Path $env:LOCALAPPDATA 'Android\Sdk' }
if (!$SdkPath) { throw 'Informe -SdkPath ou configure ANDROID_SDK_ROOT.' }
if (!$JavaBin -and $env:JAVA_HOME) { $JavaBin = Join-Path $env:JAVA_HOME 'bin' }
if (!$JavaBin) { $JavaBin = Split-Path (Get-Command javac -ErrorAction Stop).Source }
$u4Tools = Join-Path $SdkPath "build-tools\$BuildToolsVersion"
$u4Android = Join-Path $SdkPath "platforms\$Platform\android.jar"
if (!(Test-Path -LiteralPath $u4Android)) { throw "Plataforma SDK ausente: $u4Android" }
if (!(Test-Path -LiteralPath (Join-Path $u4Tools 'aapt.exe'))) { throw "Build Tools ausentes: $u4Tools" }
$u4Build = Join-Path $PSScriptRoot 'build'
$u4Classes = Join-Path $u4Build 'classes'
$u4Dex = Join-Path $u4Build 'dex'
$u4Dist = Join-Path $PSScriptRoot 'dist'
$u4Output = Join-Path $u4Dist 'U4-Diagnostico.apk'
New-Item -ItemType Directory -Force -Path $u4Classes,$u4Dex,$u4Dist | Out-Null
function Invoke-U4BuildTool {
    param([string]$Program,[string[]]$ToolArguments)
    & $Program @ToolArguments
    if ($LASTEXITCODE -ne 0) { throw "Falha na ferramenta: $Program (código $LASTEXITCODE)" }
}
$u4Sources = @(Get-ChildItem -LiteralPath (Join-Path $PSScriptRoot 'src') -Filter '*.java' -Recurse | Select-Object -ExpandProperty FullName)
Invoke-U4BuildTool (Join-Path $JavaBin 'javac.exe') (@('-encoding','UTF-8','-source','8','-target','8','-classpath',$u4Android,'-d',$u4Classes) + $u4Sources)
$u4ClassJar = Join-Path $u4Build 'classes.jar'
Invoke-U4BuildTool (Join-Path $JavaBin 'jar.exe') @('cf',$u4ClassJar,'-C',$u4Classes,'.')
Invoke-U4BuildTool (Join-Path $JavaBin 'java.exe') @('-cp',(Join-Path $u4Tools 'lib\d8.jar'),'com.android.tools.r8.D8','--min-api','23','--lib',$u4Android,'--output',$u4Dex,$u4ClassJar)
$u4Unsigned = Join-Path $u4Build 'unsigned.apk'
Invoke-U4BuildTool (Join-Path $u4Tools 'aapt.exe') @('package','-f','-M',(Join-Path $PSScriptRoot 'AndroidManifest.xml'),'-S',(Join-Path $PSScriptRoot 'res'),'-I',$u4Android,'-F',$u4Unsigned)
Push-Location $u4Dex
try { Invoke-U4BuildTool (Join-Path $u4Tools 'aapt.exe') @('add',$u4Unsigned,'classes.dex') } finally { Pop-Location }
$u4Aligned = Join-Path $u4Build 'aligned.apk'
Invoke-U4BuildTool (Join-Path $u4Tools 'zipalign.exe') @('-f','4',$u4Unsigned,$u4Aligned)
if (!$KeystorePath) {
    $KeystorePath = Join-Path $u4Build 'local-signing.p12'
    $StorePassword = 'android'
    $KeyPassword = 'android'
    if (!(Test-Path -LiteralPath $KeystorePath)) {
        Invoke-U4BuildTool (Join-Path $JavaBin 'keytool.exe') @('-genkeypair','-keystore',$KeystorePath,'-storetype','PKCS12','-storepass',$StorePassword,'-keypass',$KeyPassword,'-alias',$KeyAlias,'-keyalg','RSA','-keysize','2048','-validity','3650','-dname','CN=U4 Local Development')
    }
} elseif (!$StorePassword) { throw 'Informe a senha da chave própria por parâmetro ou U4_STORE_PASSWORD.' }
if (!$KeyPassword) { $KeyPassword = $StorePassword }
$u4Signer = Join-Path $u4Tools 'lib\apksigner.jar'
Invoke-U4BuildTool (Join-Path $JavaBin 'java.exe') @('-jar',$u4Signer,'sign','--ks',$KeystorePath,'--ks-key-alias',$KeyAlias,'--ks-pass',"pass:$StorePassword",'--key-pass',"pass:$KeyPassword",'--out',$u4Output,$u4Aligned)
Invoke-U4BuildTool (Join-Path $JavaBin 'java.exe') @('-jar',$u4Signer,'verify','--verbose',$u4Output)
Get-Item -LiteralPath $u4Output | Select-Object Name,Length
