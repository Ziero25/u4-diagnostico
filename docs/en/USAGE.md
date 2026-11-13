# Usage guide

[← English overview](../../README.en.md) · [Português](../USO.md)

## 1. Install

Download `U4-Diagnostico-1.2.apk` from [this repository's release](https://github.com/Ziero25/u4-diagnostico/releases/tag/v1.2). Copy it to a USB drive, connect the drive to the projector and open the APK in a file manager.

Approve the installation when Android asks. An existing version signed with the same key can be updated in place. Check [known compatibility](COMPATIBILITY.md) first: the U4 name alone does not identify a firmware revision.

The app's interface is in Portuguese. The instructions below keep the original button names so you can identify them.

## 2. Open the original menu

Select **Abrir menu CVTE identificado no seu projetor** — “Open the CVTE menu identified on your projector”. Read the confirmation, then select **Abrir menu** — “Open menu”.

The helper verifies the installed component and requests its original CVTE window. It does not replace the manufacturer app or turn ADB on automatically.

In the original menu:

```text
Factory Menu
└── Debug
    └── ADB Enable → On
```

**Change only ADB Enable and exit with Back.** Reset, panel, motor and calibration settings are outside this procedure.

## 3. Connect a computer

Install the official [Android SDK Platform Tools](https://developer.android.com/tools/releases/platform-tools). The computer and projector must be reachable on the local network.

Find the projector's current IP address in its network details, a system information app or your router's connected-device list. Replace the example address:

```text
adb connect 192.168.1.100:5555
adb devices -l
```

`192.168.1.100` is only an example. The device should appear with status `device`. If the projector asks for authorization, approve only the computer you are using.

Enabling ADB does not improve performance by itself. It provides access for separate diagnostics or adjustments controlled from the computer.

## 4. End access

1. Open the CVTE menu again.
2. Set **Debug → ADB Enable → Off**.
3. Exit with **Back**.
4. Verify that a new connection is rejected.

```text
adb disconnect 192.168.1.100:5555
adb connect 192.168.1.100:5555
```

If a new connection still succeeds, the firmware is keeping the service available. `adb disconnect` alone closes the client connection; it does not disable debugging on the device.

**Fechar menu CVTE** means “Close CVTE menu”. It closes the technical window without switching off ADB.

## Export a diagnostic ZIP

Select **3. Gerar diagnóstico para análise** — “Generate a diagnostic report for analysis”. Allow storage access when prompted. The output is:

```text
Download/U4-Diagnostico/U4-diagnostico.zip
```

Copy the ZIP with a file manager. It can help investigate versions, components and settings packages. Read [what it contains](PRIVACY.md) before sharing it.

## Troubleshooting

| Result | What to check |
| :--- | :--- |
| Different version or signature | The component does not match the reviewed revision. Record its version; do not remove the checks to call an unknown menu. |
| Android blocked the menu call | The firmware may restrict the service. Record the message shown by the helper. |
| A technical menu is already open | Exit with **Back** or use **Fechar menu CVTE**, then try again. |
| Menu opens but has no ADB option | The firmware's effective configuration may differ from the packaged menu. Do not explore calibration settings for this procedure. |
| Connection refused | Check the current IP and that ADB Enable is On. |
| Connection timed out | Check power, network, current IP and router device isolation. |
| ZIP export fails | Check storage permission and free space. The app displays the error. |

## Update or uninstall

If a version 1.1 root attempt left a pending restore, use **Desfazer alterações do modo avançado** — “Undo advanced-mode changes” — before clearing app data or uninstalling. Version 1.2 retains that button only when a restore is recorded.

Uninstalling the helper does not automatically disable ADB enabled through the manufacturer's menu.
