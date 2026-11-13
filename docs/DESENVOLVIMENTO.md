# Desenvolvimento

[← Voltar ao início](../README.md) · [English](en/DEVELOPMENT.md)

## Ambiente validado

| Ferramenta | Versão |
| :--- | :--- |
| Sistema de compilação | Windows e PowerShell |
| Java | JDK 21 |
| Android SDK Platform | `android-35` |
| Android SDK Build Tools | `34.0.0` |
| Linguagem de bytecode | Java 8 |
| API mínima do APK | 23 |

A compilação usa diretamente `javac`, D8, AAPT, Zipalign e Apksigner. Não há projeto Gradle nem dependências de terceiros em tempo de execução.

## Estrutura

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

| Parte | Responsabilidade |
| :--- | :--- |
| `MainActivity` | Interface, chamadas aos componentes Android e exportação local. |
| `CvteMenuGate` | Aceitar somente os metadados do serviço CVTE analisado. |
| `RootAdbPlan` | Validar os estados e comandos de uma tentativa por root preexistente das versões anteriores. |
| `tests/fixtures/` | Metadados públicos do contrato observado; não contém APKs nem relatórios privados. |

## Rodar os testes

```powershell
.\test.ps1
```

Se necessário:

```powershell
.\test.ps1 -JavaBin 'C:\caminho\do\jdk\bin'
```

As 26 verificações locais cobrem:

- Aceitação do componente CVTE conhecido e rejeição de metadados divergentes.
- Contrato dos comandos de abrir e fechar o menu.
- Validação de estados ADB e rejeição de valores inadequados para comandos.
- Preservação e restauração dos valores anteriores em uma tentativa por root.

São testes de lógica e contrato. A interação real com outro aparelho requer uma confirmação própria.

## Compilar

```powershell
.\build.ps1 -SdkPath "$env:LOCALAPPDATA\Android\Sdk"
```

O script também procura o SDK em `ANDROID_SDK_ROOT` ou `ANDROID_HOME`. O Java pode ser encontrado pelo `JAVA_HOME`, pelo `PATH` ou pelo parâmetro `-JavaBin`.

```powershell
.\build.ps1 `
  -SdkPath 'C:\Android\Sdk' `
  -JavaBin 'C:\caminho\do\jdk\bin' `
  -BuildToolsVersion '34.0.0' `
  -Platform 'android-35'
```

O APK fica em `dist/U4-Diagnostico.apk`. Os arquivos intermediários ficam em `build/`; as duas pastas são ignoradas pelo Git.

## Assinatura local

Por padrão, o script cria `build/local-signing.p12`, com a senha de desenvolvimento conhecida `android`. Essa chave é local e serve para testar uma compilação própria.

**Ela não é a chave da release publicada.** Uma compilação assinada com outra chave não atualiza por cima de um APK existente. Antes de remover uma versão do aparelho, conclua qualquer restauração pendente e desative o ADB se o trabalho tiver terminado.

Para usar uma chave própria, defina as senhas no ambiente e informe o arquivo:

```powershell
# Defina U4_STORE_PASSWORD e, se necessário, U4_KEY_PASSWORD no seu ambiente.
.\build.ps1 `
  -SdkPath 'C:\Android\Sdk' `
  -KeystorePath 'C:\chaves\minha-chave.p12' `
  -KeyAlias 'diagnostico'
```

Também existem os parâmetros `-StorePassword` e `-KeyPassword`. Evite gravar senhas ou chaves no repositório. Arquivos `.p12`, `.jks` e `.keystore` estão no `.gitignore`.

## Verificar uma release

```powershell
Get-FileHash .\U4-Diagnostico-1.2.1.apk -Algorithm SHA256
```

Compare o resultado com [SHA256SUMS.txt](../downloads/SHA256SUMS.txt). O script de build também executa a verificação de assinatura com Apksigner.

O build inclui `LICENSE`, `NOTICE` e o aviso MIT histórico como arquivos em `assets/` do APK. Eles são apresentados pelo botão **Licença e condições de uso**.

As novas publicações seguem os [termos de uso gratuito e redistribuição não comercial](../LICENSE), respeitando as permissões anteriores sobre material já publicado. Veja a [transição de licenciamento](LICENCA.md). Componentes proprietários do fabricante não fazem parte do projeto.
