# U4 Diagnóstico

Aplicativo Android offline para abrir o menu técnico original da CVTE e reunir informações de diagnóstico em um projetor BYINTEK U4.

**[Baixar APK 1.2](https://github.com/Ziero25/u4-diagnostico/releases/download/v1.2/U4-Diagnostico-1.2.apk)** · [Releases](https://github.com/Ziero25/u4-diagnostico/releases)

O atalho foi confirmado em uma unidade U4 com Android 9 e firmware `20250217_111113`: o menu abriu, a opção ADB foi habilitada pelo usuário e a conexão pela rede funcionou. Isso não confirma compatibilidade com todas as revisões do U4.

O projeto não é um aplicativo oficial da BYINTEK ou da CVTE. Ele não instala root nem firmware e não remove aplicativos.

## Usar o atalho

1. Instale o APK e abra **U4 Diagnóstico**.
2. Escolha **Abrir menu CVTE identificado no seu projetor** e confirme.
3. No menu original, entre apenas em **Debug → ADB Enable → On**.
4. Saia usando **Voltar**. A conexão ADB usa o IP local do aparelho e a porta `5555` nesse firmware.
5. Ao terminar o trabalho, volte ao mesmo menu, coloque **ADB Enable → Off** e confirme que a conexão fechou.

**Use somente a opção ADB.** O menu original inclui `Shipping Init`, `Board Init`, `Aging Mode` e opções de painel, motores e calibração. Essas opções podem resetar ou desconfigurar o aparelho. O auxiliar envia apenas os comandos de abrir e fechar a janela.

O botão **Fechar menu CVTE** fecha a janela; ele não desliga um ADB que você tenha ativado. A proteção do ADB na rede depende do firmware: utilize apenas uma rede confiável e desative a depuração ao concluir.

## Compatibilidade conferida pelo app

O atalho só chama o componente quando encontra:

- Pacote `com.cvte.fac.menu`, versão `1.2.70`.
- Serviço `com.cvte.fac.menu.app.TvMenuWindowManagerService`, habilitado e exportado, sem permissão adicional de chamada.
- Certificado público SHA-256 `8152aa9954af235d7aa208b4b7df2d8cac8b2dc44a33d8d9c3ca3e05ca2000ca`.

Em caso de diferença, o app informa o bloqueio. Ele não substitui nem inclui o APK da fabricante. O certificado acima é uma impressão pública, não uma chave privada.

## Diagnóstico e privacidade

**Gerar diagnóstico para análise** salva `Download/U4-Diagnostico/U4-diagnostico.zip` no aparelho. O arquivo contém propriedades técnicas selecionadas, lista de aplicativos de sistema, atividades dos apps de configurações, impressões dos certificados públicos e cópias dos APKs de configurações que puderem ser lidos.

O app não tem permissão `INTERNET`. Não copia dados privados dos aplicativos, contas, senhas, fotos ou vídeos. Não coleta número de série, Android ID, IP ou MAC. Solicita acesso ao armazenamento no Android 6 a 9 apenas para salvar o ZIP. A permissão `WRITE_SECURE_SETTINGS`, declarada para uma tentativa compatível com builds privilegiadas, normalmente não é concedida a um APK comum.

O ZIP de diagnóstico pode conter software proprietário do fabricante e informações específicas do aparelho. **Não o publique em issues públicas.** O repositório distribui apenas o código do auxiliar e seu APK, não os relatórios ou APKs coletados.

O código mantém a restauração de uma tentativa de ADB por root preexistente das versões anteriores. Na interface 1.2, esse botão só aparece se houver uma restauração pendente. Não desinstale nem apague os dados do auxiliar antes de concluir essa restauração.

## Compilar no Windows

Requisitos: PowerShell, JDK 21, Android SDK Platform `android-35` e Build Tools `34.0.0`. Não há Gradle nem dependências de execução externas.

```powershell
.\test.ps1
.\build.ps1 -SdkPath "$env:LOCALAPPDATA\Android\Sdk"
```

Se o Java não estiver no `PATH`, informe `-JavaBin 'caminho\do\jdk\bin'` aos dois scripts. Também é possível usar `JAVA_HOME`, `ANDROID_SDK_ROOT` ou `ANDROID_HOME`.

O resultado local fica em `dist/U4-Diagnostico.apk`. A compilação cria uma chave local em `build/local-signing.p12`, ignorada pelo Git. Ela usa senha de desenvolvimento conhecida e serve apenas para builds locais. Para assinar com uma chave própria, informe `-KeystorePath`, `-StorePassword`, `-KeyPassword` e `-KeyAlias`; as senhas também podem vir de `U4_STORE_PASSWORD` e `U4_KEY_PASSWORD`.

**A chave da release não está neste repositório.** Uma compilação com outra chave não atualiza por cima do APK publicado. Se houver restauração pendente, conclua-a antes de remover uma versão instalada.

## Verificação

Os testes locais cobrem 11 verificações de identidade/comando CVTE e 15 verificações de validação e restauração dos estados ADB. O APK publicado teve sua assinatura e integridade ZIP verificadas.

SHA-256 do APK 1.2:

```text
a72044cc22f8cadd87a3e589926ff9a5d3ae353961394849ffb8503bbceb3187
```

Esses testes não substituem a confirmação no dispositivo de outra revisão de hardware ou firmware.
