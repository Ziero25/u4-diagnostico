# Privacy and permissions

[← English overview](../../README.en.md) · [Português](../PRIVACIDADE.md)

## Local operation

The helper does not declare the Android `INTERNET` permission. It does not request an account, send reports or upload generated files automatically.

ADB is a separate Android service. When the user enables it through the CVTE menu, the projector's firmware determines network exposure and authorization. End that access when finished.

## Diagnostic contents

| ZIP entry | Contents |
| :--- | :--- |
| `diagnostico.txt` | Selected technical properties, system version, ADB status and the result of a previous root attempt, if any. |
| `pacotes-sistema.txt` | System-app package names, labels and versions. |
| `atividades-configuracoes.txt` | Components found in candidate settings apps, with enabled/exported status and permissions. |
| `certificados-sistema.txt` | SHA-256 fingerprints of selected public signing certificates. |
| `arquivos-copiados.txt` | A record of copied or skipped APKs. |
| `apks/` | Readable installation APKs from settings and factory packages. |

The APKs are installed program files, not their private data directories. The helper does not copy accounts, passwords, photos, videos or personal files. It does not collect a device serial number, Android ID, IP address or MAC address.

`ro.build.fingerprint` identifies an Android build; it is not a biometric fingerprint.

## Declared permissions

| Permission | Purpose |
| :--- | :--- |
| `WRITE_EXTERNAL_STORAGE`, through API 28 | Save the ZIP to public Downloads after user authorization. Android also grants the corresponding read permission. |
| `WRITE_SECURE_SETTINGS` | Attempt direct activation only if the firmware already granted this privileged permission. An ordinary APK normally does not receive it. |

The version 1.2 CVTE shortcut calls the identified exported component and does not depend on granting `WRITE_SECURE_SETTINGS` to the helper.

## Sharing reports

The ZIP may contain proprietary manufacturer software and installation-specific information. Review it before sharing. **Do not post diagnostic ZIPs, collected APKs, identifying screenshots or credentials in public issues.**

A useful issue normally needs only the Android version, firmware displayed by the menu, helper version, error message and reproduction steps.

## Retention and removal

The ZIP stays in `Download/U4-Diagnostico/` until removed with a file manager. A new export replaces the helper's previous ZIP.

The helper may also retain values in private storage to restore a previous root attempt. Complete a pending restore before clearing app data or uninstalling.

## Public repository contents

The repository includes the helper's original source, visual assets, tests and release APK. Device reports, manufacturer APKs and private signing keys are not published.
