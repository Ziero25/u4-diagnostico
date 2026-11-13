# Compatibility and behavior

[← English overview](../../README.en.md) · [Português](../COMPATIBILIDADE.md)

## Confirmed test unit

| Field | Observed value |
| :--- | :--- |
| Commercial model | BYINTEK U4 |
| Android | 9 · API 28 |
| Platform | HiSilicon Hi3751V350 · `bigfish` |
| Firmware shown by the manufacturer menu | `20250217_111113` |
| Build identification | `Hi3751V352F_DMO`, `userdebug`, `dev-keys` |
| Settings package | `com.cvte.settings` |
| Technical menu package | `com.cvte.fac.menu` · `1.2.70` |

The version 1.2 shortcut opened the menu on this unit. The owner enabled ADB manually, and a computer successfully connected. Version 1.2.1 adds license terms and notices; its build and local checks passed, without a new hardware test. The `userdebug` build type was not treated as evidence of root: no accessible `su` executable was found.

## Checks before the call

[CvteMenuGate](../../src/local/codex/u4diagnostico/CvteMenuGate.java) accepts only the reviewed contract:

```text
Package:   com.cvte.fac.menu
Version:   1.2.70
Service:   com.cvte.fac.menu.app.TvMenuWindowManagerService
Exported:  yes
Enabled:   yes
Additional calling permission: none
```

Public signing certificate SHA-256:

```text
8152aa9954af235d7aa208b4b7df2d8cac8b2dc44a33d8d9c3ca3e05ca2000ca
```

This is a public verification fingerprint, not a private signing key.

## Shortcut commands

The helper sends an explicit `Intent` to the original service using the extra below. The spelling with three letters **m** matches the reviewed component:

```text
Extra: com.cvte.fac.menu.commmand
Open:  com.cvte.fac.menu.commmand.factory_menu
Close: com.cvte.fac.menu.commmand.close_factory_menu
```

The helper sends only these window commands. The user changes ADB manually in the original menu. In the reviewed manufacturer code, the ADB control starts or stops `adbd` and changes `adb_enabled`.

The original app also signals menu visibility through `sys.factorymenu`; its normal exit clears that state. This behavior belongs to the CVTE component.

## Limits

- Another U4 revision may use a different manufacturer's system and menu.
- The same processor does not establish the same software contract.
- An exported service can still face runtime restrictions.
- External firmware configuration can change the menu included in the APK.
- Opening the menu does not prove that network ADB is available; a connection test is required.
- This helper does not add panel resolution, install firmware or calibrate picture, focus or motors.

## Android support

The manifest permits installation from API 23. Public Downloads export was implemented for **Android 6–9**. Other devices and Android releases have not been confirmed. The compile SDK or the ability to install the APK does not expand CVTE menu compatibility.

To report another revision, see the [contribution guidance](DEVELOPMENT.md#contribute). Do not attach private reports or proprietary APKs to a public issue.
