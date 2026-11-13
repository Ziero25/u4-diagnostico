# Guia de uso

[← Voltar ao início](../README.md)

## 1. Instalar

Baixe `U4-Diagnostico-1.2.apk` pela [release oficial deste repositório](https://github.com/Ziero25/u4-diagnostico/releases/tag/v1.2). Você pode copiar o arquivo para um pendrive, conectá-lo ao projetor e abrir o APK no gerenciador de arquivos.

Confirme a instalação quando o Android solicitar. Se já houver uma versão anterior assinada com a mesma chave, o instalador permite atualizar por cima dela.

Antes de começar, confira a [compatibilidade conhecida](COMPATIBILIDADE.md). A presença do nome U4 na carcaça não identifica sozinha a revisão de hardware e firmware.

## 2. Abrir o menu original

No aplicativo, selecione **Abrir menu CVTE identificado no seu projetor**. Leia a confirmação e escolha **Abrir menu**.

O auxiliar verifica o componente instalado e solicita a abertura da janela original da CVTE. Ele não substitui esse aplicativo e não ativa a opção ADB automaticamente.

No menu original:

```text
Factory Menu
└── Debug
    └── ADB Enable → On
```

**Altere somente ADB Enable e saia com Voltar.** As opções de reset, painel, motores e calibração não fazem parte deste procedimento.

## 3. Conectar o computador

Instale as [Android SDK Platform Tools](https://developer.android.com/tools/releases/platform-tools) no computador. Ele e o projetor precisam se alcançar pela rede local.

Descubra o IP local do projetor nas informações de rede do aparelho, em um aplicativo de informações do sistema ou na lista de dispositivos conectados do roteador.

Substitua o IP do exemplo pelo endereço real:

```text
adb connect 192.168.1.100:5555
adb devices -l
```

O endereço `192.168.1.100` acima é apenas um exemplo. O dispositivo deve aparecer com estado `device`. Se o aparelho mostrar uma solicitação de autorização, aceite somente o computador que está usando.

Ligar ADB não melhora o desempenho por si só: ele permite executar um diagnóstico ou ajustes separados, sob controle de quem opera o computador.

## 4. Encerrar o acesso

Ao terminar:

1. Abra o menu CVTE novamente.
2. Coloque **Debug → ADB Enable → Off**.
3. Saia com **Voltar**.
4. Confira no computador que uma nova conexão não é aceita.

```text
adb disconnect 192.168.1.100:5555
adb connect 192.168.1.100:5555
```

Uma nova conexão aceita indica que o firmware ainda mantém o serviço disponível. Encerrar apenas o cliente com `adb disconnect` não desativa a depuração no aparelho.

## Gerar o ZIP de diagnóstico

Escolha **3. Gerar diagnóstico para análise** e permita o acesso ao armazenamento quando solicitado. O arquivo fica em:

```text
Download/U4-Diagnostico/U4-diagnostico.zip
```

Copie o ZIP pelo gerenciador de arquivos. Ele pode ajudar a analisar versões, componentes e os aplicativos de configurações. Leia a [descrição dos dados coletados](PRIVACIDADE.md) antes de compartilhá-lo.

## Problemas comuns

| Resultado | O que verificar |
| :--- | :--- |
| Versão ou assinatura diferente | O componente não corresponde à revisão analisada. Registre as versões; não remova as verificações para tentar abrir outro menu. |
| O Android não permitiu chamar o menu | O firmware pode restringir o serviço. Anote a mensagem exibida pelo app. |
| O sistema indica um menu já aberto | Saia com **Voltar** ou use **Fechar menu CVTE**, depois tente novamente. |
| A janela abriu, mas não há ADB | A configuração efetiva do menu pode diferir da incluída no APK. Não procure em opções de calibração. |
| Conexão recusada | Confira se o IP está correto e se ADB Enable está em On. |
| Tempo de conexão esgotado | Confira alimentação, rede, IP atual e eventual isolamento entre dispositivos no roteador. |
| Não foi possível gerar o ZIP | Confira a permissão de armazenamento e o espaço livre. O app mostra a falha encontrada. |

## Atualizar ou desinstalar

Se houver uma restauração pendente da tentativa de root da versão 1.1, conclua-a pelo botão correspondente antes de apagar os dados ou desinstalar o app. A versão 1.2 preserva esse caminho apenas quando há uma restauração registrada.

Desinstalar o auxiliar não desliga automaticamente o ADB habilitado no menu do fabricante.
