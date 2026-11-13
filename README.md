<div align="center">

![U4 Diagnóstico — diagnóstico offline e acesso ao menu CVTE](docs/assets/cover.svg)

# U4 Diagnóstico

**O menu certo. Os dados do seu aparelho. Tudo local.**

Um pequeno auxiliar Android para abrir o menu original da CVTE e investigar o BYINTEK U4.

[![Release v1.2](https://img.shields.io/badge/release-v1.2-14b8a6?style=flat-square)](https://github.com/Ziero25/u4-diagnostico/releases/tag/v1.2)
[![Licença MIT](https://img.shields.io/badge/licen%C3%A7a-MIT-14b8a6?style=flat-square)](LICENSE)
[![Android 9 testado](https://img.shields.io/badge/Android-9%20testado-3b82f6?style=flat-square)](docs/COMPATIBILIDADE.md)
[![Offline](https://img.shields.io/badge/conex%C3%A3o-offline-64748b?style=flat-square)](docs/PRIVACIDADE.md)

### [Baixar APK 1.2](https://github.com/Ziero25/u4-diagnostico/releases/download/v1.2/U4-Diagnostico-1.2.apk)

[Começar a usar](docs/USO.md) · [Compatibilidade](docs/COMPATIBILIDADE.md) · [Privacidade](docs/PRIVACIDADE.md) · [Compilar](docs/DESENVOLVIMENTO.md)

</div>

---

## O que ele faz

| Recurso | Como funciona |
| :--- | :--- |
| **Atalho para o menu CVTE** | Abre a janela técnica original para você acessar a opção ADB. |
| **Conferência do componente** | Verifica versão, certificado e acesso ao serviço antes de chamá-lo. |
| **Diagnóstico em um arquivo** | Reúne dados técnicos e APKs de configurações em um ZIP salvo no aparelho. |
| **Funcionamento offline** | O auxiliar não possui permissão de acesso à internet. |

O acesso foi confirmado em **uma unidade BYINTEK U4 com Android 9**, firmware `20250217_111113`. O menu abriu, o usuário ativou a opção ADB e a conexão pela rede funcionou. Outras revisões precisam de verificação própria.

## Comece em quatro passos

1. **Instale** o [APK da release](https://github.com/Ziero25/u4-diagnostico/releases/tag/v1.2), por exemplo usando um pendrive.
2. **Abra** o U4 Diagnóstico e escolha **Abrir menu CVTE identificado no seu projetor**.
3. **Ative** somente **Debug → ADB Enable → On**, dentro do menu original.
4. **Saia** com **Voltar** e conecte o computador ao IP do projetor, na porta `5555` nesse firmware.

> [!IMPORTANT]
> Use somente **ADB Enable**. As outras opções do menu original incluem reset, painel, motores e calibração e podem desconfigurar o aparelho. Ao concluir, coloque **ADB Enable → Off** e confira se a conexão foi encerrada.

O botão **Fechar menu CVTE** fecha a janela técnica. Ele não desliga o ADB que você tenha ativado. Veja o [guia de uso](docs/USO.md) para os passos completos e solução de problemas.

## Feito para uma revisão conhecida

| Item | Identificação conferida |
| :--- | :--- |
| Sistema testado | Android 9 · API 28 |
| Plataforma da unidade testada | HiSilicon Hi3751V350 · `bigfish` |
| Firmware exibido no aparelho | `20250217_111113` |
| Componente do menu | `com.cvte.fac.menu`, versão `1.2.70` |
| Acesso ao atalho | Sem exigir root |

O nome **U4** ou o processador sozinho não confirma compatibilidade. O aplicativo também confere a assinatura do componente e bloqueia a chamada quando ela diverge. [Entenda as verificações e os limites →](docs/COMPATIBILIDADE.md)

## Seus dados ficam no aparelho

O diagnóstico é salvo em:

```text
Download/U4-Diagnostico/U4-diagnostico.zip
```

Ele inclui informações técnicas selecionadas, inventário de apps de sistema e cópias dos programas de configurações que puderem ser lidos. O auxiliar não copia dados privados desses aplicativos nem envia o ZIP automaticamente.

**Relatórios de diagnóstico não devem ser publicados em issues públicas.** Eles podem conter informações específicas do aparelho e software proprietário do fabricante. Consulte a [descrição dos dados e permissões](docs/PRIVACIDADE.md).

## Para desenvolver

O projeto usa **Java e Android SDK**, com scripts PowerShell. Não depende de Gradle.

```powershell
# Executar as verificações locais
.\test.ps1

# Gerar um APK com assinatura local de desenvolvimento
.\build.ps1 -SdkPath "$env:LOCALAPPDATA\Android\Sdk"
```

O ambiente validado usa **JDK 21**, **Android Platform 35** e **Build Tools 34.0.0**. Os testes cobrem **26 verificações** de identidade, comandos e restauração de estados ADB.

[Preparar o ambiente, compilar e assinar →](docs/DESENVOLVIMENTO.md)

<details>
<summary><strong>Verificar o APK publicado</strong></summary>

SHA-256 de `U4-Diagnostico-1.2.apk`:

```text
a72044cc22f8cadd87a3e589926ff9a5d3ae353961394849ffb8503bbceb3187
```

No PowerShell:

```powershell
Get-FileHash .\U4-Diagnostico-1.2.apk -Algorithm SHA256
```

O arquivo de checksums está na [release](https://github.com/Ziero25/u4-diagnostico/releases/tag/v1.2) e em [downloads/SHA256SUMS.txt](downloads/SHA256SUMS.txt).

</details>

## Navegue pelo projeto

| Documento | Conteúdo |
| :--- | :--- |
| [Guia de uso](docs/USO.md) | Instalação, menu ADB, diagnóstico e problemas comuns. |
| [Compatibilidade](docs/COMPATIBILIDADE.md) | Revisão testada, contrato do serviço e limitações. |
| [Privacidade](docs/PRIVACIDADE.md) | Dados coletados, permissões e cuidado com os relatórios. |
| [Desenvolvimento](docs/DESENVOLVIMENTO.md) | Ambiente, testes, compilação e assinatura. |
| [Histórico](CHANGELOG.md) | Evolução das versões do auxiliar. |
| [Como contribuir](CONTRIBUTING.md) | Relatos úteis e alterações no código. |

## Licença

**Gratuito e de código aberto, sob a [licença MIT](LICENSE).** Você pode usar, modificar e redistribuir o código, inclusive comercialmente, preservando os avisos de autoria e licença. O software é fornecido sem garantia.

A licença cobre o código e os materiais autorais deste projeto. Os APKs do fabricante eventualmente copiados pelo diagnóstico permanecem sujeitos às licenças dos respectivos titulares e não são distribuídos neste repositório.

<div align="center">

Mantido por [Ziero25](https://github.com/Ziero25) · Projeto independente, sem vínculo oficial com BYINTEK ou CVTE.

</div>
