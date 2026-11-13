param([string]$JavaBin)
$ErrorActionPreference = 'Stop'
if (!$JavaBin -and $env:JAVA_HOME) { $JavaBin = Join-Path $env:JAVA_HOME 'bin' }
if (!$JavaBin) { $JavaBin = Split-Path (Get-Command javac -ErrorAction Stop).Source }
$u4Tests = Join-Path $PSScriptRoot 'test-build'
New-Item -ItemType Directory -Force -Path $u4Tests | Out-Null
$u4TestSources = @(
    (Join-Path $PSScriptRoot 'src\local\codex\u4diagnostico\RootAdbPlan.java'),
    (Join-Path $PSScriptRoot 'src\local\codex\u4diagnostico\CvteMenuGate.java'),
    (Join-Path $PSScriptRoot 'tests\RootAdbPlanTest.java'),
    (Join-Path $PSScriptRoot 'tests\CvteMenuGateTest.java')
)
& (Join-Path $JavaBin 'javac.exe') --release 8 -encoding UTF-8 -d $u4Tests @u4TestSources
if ($LASTEXITCODE -ne 0) { throw 'Falha ao compilar os testes.' }
& (Join-Path $JavaBin 'java.exe') -cp $u4Tests RootAdbPlanTest
if ($LASTEXITCODE -ne 0) { throw 'Falha nos testes de restauração.' }
& (Join-Path $JavaBin 'java.exe') -cp $u4Tests CvteMenuGateTest (Join-Path $PSScriptRoot 'tests\fixtures\cvte-menu.properties')
if ($LASTEXITCODE -ne 0) { throw 'Falha nos testes do menu CVTE.' }
