Instruções para o diretório db/

Arquivo principal:
- `schema.sql` — script para criar o banco `delivery_system` e tabelas utilizadas pelo projeto.

Como usar
1. Abra seu cliente MySQL (linha de comando ou GUI) e execute o arquivo `schema.sql`.

Pela linha de comando (Linux/macOS):

```bash
mysql -u SEU_USUARIO -p < db/schema.sql
```

Substitua SEU_USUARIO pelo usuário do MySQL. Você será solicitado a inserir a senha.

Notas importantes
- O script assume uso do mecanismo InnoDB e charset utf8mb4.
- O script cria um par de restaurantes e alguns itens de exemplo; ajuste ou remova conforme necessário.
- Se quiser recriar tabelas que possuam chaves estrangeiras, pode desabilitar temporariamente as checagens com:

```sql
SET FOREIGN_KEY_CHECKS = 0;
-- DROP / CREATE ...
SET FOREIGN_KEY_CHECKS = 1;
```

Próximos passos sugeridos
- Se quiser, posso gerar um script mais completo baseado nos DAOs do projeto (por exemplo, adicionando índices, dados de teste mais ricos ou usuários de sistema).
- Posso também ajudar a migrar o projeto para Maven/Gradle e incluir um target/task que execute automaticamente esse script no banco local.
