> **Publicação anterior sob MIT.** A versão atual é a [1.2.1](https://github.com/Ziero25/u4-diagnostico/releases/tag/v1.2.1), oferecida sob novos termos de redistribuição não comercial. Esta release 1.2 e os direitos já concedidos para seu material permanecem sob os termos anteriores. Veja o [aviso de transição](https://github.com/Ziero25/u4-diagnostico/blob/main/NOTICE).

**BYINTEK U4 — acesso ao ADB pelo menu original CVTE, sem root na revisão testada.**

APK gratuito e offline. Testado em uma unidade com Android 9 e firmware `20250217_111113`. A ativação do ADB é manual no menu do fabricante.

**[Guia de uso](https://github.com/Ziero25/u4-diagnostico/blob/main/docs/USO.md)** · **[Compatibilidade](https://github.com/Ziero25/u4-diagnostico/blob/main/docs/COMPATIBILIDADE.md)** · **[Licença MIT desta publicação](https://github.com/Ziero25/u4-diagnostico/blob/main/licenses/legacy/MIT.txt)**

**[English documentation](https://github.com/Ziero25/u4-diagnostico/blob/main/README.en.md)** · **[English usage guide](https://github.com/Ziero25/u4-diagnostico/blob/main/docs/en/USAGE.md)**

- Atalho para abrir e fechar o menu CVTE identificado no U4, sem exigir root.
- Conferência de versão, certificado e acesso do serviço antes de chamá-lo.
- Exportação local de diagnóstico para Android 6 a 9.
- Conexão ADB confirmada em uma unidade U4 Android 9 após ativação manual em Debug → ADB Enable.
- 26 verificações locais de validação/comando/restauração e assinatura APK conferida.

O aplicativo não é oficial da BYINTEK/CVTE. Compatibilidade conferida para com.cvte.fac.menu 1.2.70 com o certificado indicado no guia de compatibilidade. Outras revisões não estão confirmadas.

Use somente Debug → ADB Enable. As outras opções do menu original incluem reset e calibração. Ao terminar, desligue o ADB no mesmo menu e confirme o fechamento da conexão.

SHA-256 do APK: a72044cc22f8cadd87a3e589926ff9a5d3ae353961394849ffb8503bbceb3187.

O código e os materiais autorais do auxiliar são gratuitos e distribuídos sob licença MIT. O arquivo LICENSE acompanha esta release. Os APKs proprietários do fabricante coletados em diagnósticos não fazem parte da distribuição.

### English

**Earlier MIT publication.** The current release is [1.2.1](https://github.com/Ziero25/u4-diagnostico/releases/tag/v1.2.1), offered under new noncommercial-redistribution terms. This 1.2 release and previously granted rights for its material remain under the earlier terms; see [NOTICE](https://github.com/Ziero25/u4-diagnostico/blob/main/NOTICE).

U4 Diagnóstico is a free, offline helper for accessing the original CVTE menu on the **BYINTEK U4 Android projector**. The shortcut worked without root on one tested unit running **Android 9, firmware `20250217_111113`**, with `com.cvte.fac.menu` version `1.2.70` and the documented signing certificate. Support for other revisions is unconfirmed.

Install the APK, select **Abrir menu CVTE identificado no seu projetor**, then change only **Debug → ADB Enable → On** in the original menu. Exit with **Back**. The app opens the menu; ADB activation remains manual. Disable ADB and verify that access has stopped when finished.

The APK interface remains in Portuguese. Source and original project materials use the MIT license. The APK binary and its checksum are unchanged by the documentation update.
