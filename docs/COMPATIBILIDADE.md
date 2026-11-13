# Compatibilidade e funcionamento

[← Voltar ao início](../README.md) · [English](en/COMPATIBILITY.md)

## Unidade em que o acesso foi confirmado

| Campo | Valor observado |
| :--- | :--- |
| Modelo comercial | BYINTEK U4 |
| Android | 9 · API 28 |
| Plataforma | HiSilicon Hi3751V350 · `bigfish` |
| Firmware no menu do fabricante | `20250217_111113` |
| Identificação da compilação | `Hi3751V352F_DMO`, `userdebug`, `dev-keys` |
| Aplicativo de configurações | `com.cvte.settings` |
| Aplicativo do menu técnico | `com.cvte.fac.menu` · `1.2.70` |

O atalho da versão 1.2 abriu o menu técnico nessa unidade. O usuário ativou ADB e o computador confirmou a conexão. A revisão 1.2.1 acrescenta termos e avisos de licença; foi compilada e verificada localmente, sem novo teste no aparelho. A compilação `userdebug` não foi usada como prova de root; o relatório não encontrou um executável `su` acessível.

## Verificações antes da chamada

O código de [CvteMenuGate](../src/local/codex/u4diagnostico/CvteMenuGate.java) aceita somente o contrato analisado:

```text
Pacote:    com.cvte.fac.menu
Versão:    1.2.70
Serviço:   com.cvte.fac.menu.app.TvMenuWindowManagerService
Exportado: sim
Habilitado: sim
Permissão adicional de chamada: nenhuma
```

Certificado público SHA-256:

```text
8152aa9954af235d7aa208b4b7df2d8cac8b2dc44a33d8d9c3ca3e05ca2000ca
```

O certificado é uma impressão pública usada na verificação, não uma chave que permita assinar programas como fabricante.

## Comandos usados pelo atalho

A chamada envia um `Intent` explícito ao serviço original, com a chave de extra abaixo. A grafia com três letras **m** corresponde ao aplicativo analisado:

```text
Chave:  com.cvte.fac.menu.commmand
Abrir:  com.cvte.fac.menu.commmand.factory_menu
Fechar: com.cvte.fac.menu.commmand.close_factory_menu
```

O auxiliar envia apenas esses comandos de janela. A opção ADB é acionada manualmente no menu original. No código analisado, o controle ADB inicia ou interrompe `adbd` e altera `adb_enabled`.

O aplicativo original também sinaliza a exibição da janela com `sys.factorymenu`. A saída normal remove essa sinalização. Esse comportamento pertence ao menu da CVTE.

## Limites

- Outra revisão U4 pode usar um sistema de outro fabricante e oferecer menus diferentes.
- O mesmo processador não confirma o mesmo software.
- Um serviço registrado e exportado ainda pode sofrer restrições durante a execução.
- Um menu presente no APK pode ser alterado por uma configuração externa do firmware.
- A abertura da janela não comprova que a conexão ADB pela rede está disponível; isso exige teste.
- Este projeto não acrescenta pixels ao painel, não instala firmware e não calibra imagem, foco ou motores.

## Compatibilidade do auxiliar Android

O manifesto permite instalação a partir da API 23. A exportação para a pasta pública de Downloads foi implementada para **Android 6 a 9**. A execução em outros aparelhos e versões não foi confirmada. O alvo de compilação ou a possibilidade de instalar um APK não ampliam o contrato de compatibilidade do menu CVTE.

Para relatar outra revisão, siga [Como contribuir](../CONTRIBUTING.md), sem anexar relatórios privados ou APKs proprietários em uma issue pública.
