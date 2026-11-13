# Como contribuir

Obrigado pelo interesse em melhorar o U4 Diagnóstico.

## Relatar um problema

Abra uma [issue](https://github.com/Ziero25/u4-diagnostico/issues) com:

- Versão do auxiliar, modelo comercial e versão do Android.
- Firmware exibido no menu do aparelho.
- Mensagem apresentada pelo app.
- Passos para reproduzir e resultado esperado.

Não anexe ZIPs de diagnóstico, APKs da fabricante, senhas, identificadores pessoais ou capturas com esses dados em uma issue pública. Consulte o [guia de privacidade](docs/PRIVACIDADE.md).

## Propor uma alteração

1. Descreva o problema que a mudança resolve.
2. Faça uma alteração com escopo definido.
3. Execute `test.ps1` e a compilação com `build.ps1`.
4. Atualize a documentação quando mudar o comportamento.
5. Informe o que foi verificado localmente e, se houver, no aparelho.

Mantenha as verificações de versão, certificado e acesso ao componente. O suporte a outra revisão precisa de evidência própria do contrato do menu; o mesmo nome comercial ou processador não basta.

O projeto não distribui APKs proprietários ou chaves privadas e não inclui rotinas de reset, calibração, remoção de aplicativos ou instalação de root como parte do atalho CVTE.

As contribuições incorporadas ao projeto são distribuídas sob a [licença MIT](LICENSE).
