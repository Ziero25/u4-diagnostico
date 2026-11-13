# Privacidade e permissões

[← Voltar ao início](../README.md)

## Funcionamento local

O manifesto do auxiliar não solicita a permissão `INTERNET`. O app não envia relatórios, não solicita uma conta e não faz upload automático dos arquivos gerados.

A conexão ADB é um serviço separado do próprio Android. Quando o usuário a ativa no menu CVTE, a exposição e a autorização na rede são determinadas pelo firmware do projetor. Encerre esse acesso ao concluir o trabalho.

## Conteúdo do diagnóstico

| Arquivo no ZIP | Conteúdo |
| :--- | :--- |
| `diagnostico.txt` | Propriedades técnicas selecionadas, versão do sistema, estado ADB e resultado de uma eventual tentativa anterior por root. |
| `pacotes-sistema.txt` | Nomes, rótulos e versões de aplicativos de sistema. |
| `atividades-configuracoes.txt` | Componentes encontrados nos apps candidatos de configurações, com estado, exportação e permissões. |
| `certificados-sistema.txt` | Impressões SHA-256 de certificados públicos de componentes selecionados. |
| `arquivos-copiados.txt` | Registro dos APKs copiados ou ignorados. |
| `apks/` | Cópias dos arquivos de instalação dos apps de configurações e fábrica que puderem ser lidos. |

Os APKs são os programas instalados, não seus diretórios privados de dados. O auxiliar não copia contas, senhas, fotos, vídeos ou arquivos pessoais. Também não coleta número de série, Android ID, IP ou MAC.

O nome técnico `ro.build.fingerprint` descreve uma compilação do Android; ele não é uma impressão biométrica.

## Permissões declaradas

| Permissão | Uso |
| :--- | :--- |
| `WRITE_EXTERNAL_STORAGE`, até API 28 | Salvar o ZIP na pasta pública de Downloads, após autorização do usuário. O Android também atribui a leitura correspondente. |
| `WRITE_SECURE_SETTINGS` | Tentativa de ativação direta apenas se o firmware já tiver concedido essa permissão privilegiada. Um APK comum normalmente não a recebe. |

O atalho CVTE da versão 1.2 chama o componente exportado identificado e não depende de uma concessão de `WRITE_SECURE_SETTINGS` ao auxiliar.

## Compartilhar com cuidado

O ZIP pode conter software proprietário da fabricante e detalhes específicos da instalação. Revise seu conteúdo antes de compartilhar. **Não publique o ZIP, APKs coletados, capturas com identificadores ou credenciais em issues públicas.**

Para uma issue, normalmente bastam a versão do Android, o firmware mostrado no menu, a versão do auxiliar, a mensagem de erro e os passos executados.

## Retenção e remoção

O ZIP fica em `Download/U4-Diagnostico/` e pode ser apagado pelo gerenciador de arquivos. Uma nova exportação substitui o ZIP anterior gerado pelo próprio app.

O aplicativo também pode manter no armazenamento privado os valores necessários para restaurar uma tentativa por root da versão anterior. Se essa restauração estiver pendente, conclua-a antes de apagar os dados ou remover o auxiliar.

## O que não entra no repositório

O repositório público inclui o código autoral do auxiliar, seus recursos visuais, testes e APK de release. Relatórios do aparelho, APKs da fabricante e chaves privadas de assinatura não são publicados.
