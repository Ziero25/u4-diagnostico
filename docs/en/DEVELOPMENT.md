# Development

[← English overview](../../README.en.md) · [Português](../DESENVOLVIMENTO.md)

## Validated tools

| Tool | Version |
| :--- | :--- |
| Build environment | Windows and PowerShell |
| Java | JDK 21 |
| Android SDK Platform | `android-35` |
| Android SDK Build Tools | `34.0.0` |
| Bytecode language level | Java 8 |
| Minimum APK API | 23 |

The build calls `javac`, D8, AAPT, Zipalign and Apksigner directly. There is no Gradle project or third-party runtime dependency.

## Layout

```text
.
├── AndroidManifest.xml
├── build.ps1
├── test.ps1
├── src/local/codex/u4diagnostico/
│   ├── MainActivity.java
│   ├── CvteMenuGate.java
│   └── RootAdbPlan.java
├── res/
├── tests/
│   └── fixtures/cvte-menu.properties
├── downloads/
└── docs/
```

`MainActivity` provides the interface, Android component calls and local export. `CvteMenuGate` validates the reviewed service metadata. `RootAdbPlan` validates ADB states and commands retained for existing-root attempts from earlier versions. The fixtures contain public contract metadata, not manufacturer APKs or private reports.

## Run tests

```powershell
.\test.ps1

# If the JDK is not available through JAVA_HOME or PATH:
.\test.ps1 -JavaBin 'C:\path\to\jdk\bin'
```

The 26 local checks cover component identity and rejection of mismatches, open/close commands, valid ADB states, rejection of unsafe command values and preservation/restoration of previous states.

These are logic and contract tests. Real-device behavior on another configuration needs its own confirmation.

## Build

```powershell
.\build.ps1 -SdkPath "$env:LOCALAPPDATA\Android\Sdk"
```

The script also looks for the SDK in `ANDROID_SDK_ROOT` and `ANDROID_HOME`. Java can be located through `JAVA_HOME`, `PATH` or `-JavaBin`.

```powershell
.\build.ps1 `
  -SdkPath 'C:\Android\Sdk' `
  -JavaBin 'C:\path\to\jdk\bin' `
  -BuildToolsVersion '34.0.0' `
  -Platform 'android-35'
```

Output: `dist/U4-Diagnostico.apk`. Intermediate files go to `build/`. Both directories are ignored by Git.

## Local signing

By default, the script creates `build/local-signing.p12` with the known development password `android`. This local key is for testing your own build.

**It is not the published release's signing key.** A build signed with a different key cannot update an existing installation in place. Before removing an installed version, complete any pending restore and disable ADB if your work is finished.

To use an existing key, set the passwords in your environment and pass its path:

```powershell
# Set U4_STORE_PASSWORD and, if needed, U4_KEY_PASSWORD in your environment.
.\build.ps1 `
  -SdkPath 'C:\Android\Sdk' `
  -KeystorePath 'C:\keys\my-key.p12' `
  -KeyAlias 'diagnostico'
```

The script also accepts `-StorePassword` and `-KeyPassword`. Do not commit passwords or private keys. `.p12`, `.jks` and `.keystore` files are ignored.

## Verify a release

```powershell
Get-FileHash .\U4-Diagnostico-1.2.1.apk -Algorithm SHA256
```

Compare with [SHA256SUMS.txt](../../downloads/SHA256SUMS.txt). The build script also runs Apksigner verification.

## Contribute

Open an [issue](https://github.com/Ziero25/u4-diagnostico/issues) with the helper version, commercial model, Android version, displayed firmware, exact message, reproduction steps and expected behavior. Keep reports, manufacturer APKs and credentials out of public issues; see [privacy](PRIVACY.md).

Keep changes scoped. Run the tests and build for code changes, update relevant documentation and describe what was validated locally or on hardware. Preserve version, certificate and service-access checks. A new revision requires evidence of its own contract.

The CVTE shortcut does not include reset, calibration, app removal or root installation routines. New contributions use the [current free-use and noncommercial-redistribution terms](../../LICENSE), subject to previously granted rights; see the [licensing transition](LICENSING.md). Manufacturer components are not part of the distribution.

The build embeds `LICENSE`, `NOTICE` and the historical MIT notice as APK assets. They are displayed by the **Licença e condições de uso** button.
