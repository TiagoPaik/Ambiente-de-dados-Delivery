# Ambiente de Dados — Delivery

Sistema acadêmico simples de delivery desenvolvido em Java com JDBC e MySQL. A interface gráfica é feita com Swing e a aplicação segue uma organização em camadas (model, dao, ui).

## Principais pontos
- Linguagem: Java
- Persistência: JDBC (MySQL)
- UI: Swing

## Estrutura do projeto (resumo)

Estrutura relevante:

```
Ambiente-de-dados-Delivery/
├── src/
│   ├── Delivery/               # pacotes do projeto (ex.: Delivery.Main)
│   │   ├── config/             # configurações de conexão (Conexao.java)
│   │   ├── dao/                # classes DAO (AdminDAO, ClienteDAO, ...)
│   │   ├── modelo/             # modelos/entidades (Usuario, Pedido, ...)
│   │   └── ui/                 # telas Swing (LoginFrame, PainelClienteFrame, ...)
│   └── util/                   # utilitários (ConnectionFactory.java)
├── lib/                        # libs adicionadas manualmente (opcional)
├── README.md
└── ...
```

Arquivos importantes:
- `src/Delivery/Main.java` — classe principal que inicia a UI (LoginFrame).
- `src/Delivery/config/Conexao.java` e `src/util/ConnectionFactory.java` — pontos onde a conexão com o banco é configurada.

## Pré-requisitos

- Java 8 ou superior instalado
- MySQL (ou compatível) rodando localmente ou acessível pela rede
- Driver JDBC do MySQL (MySQL Connector/J) disponível no classpath da aplicação (via IDE ou adicionando o JAR em `lib/`)

## Configuração do banco de dados

1. Crie o banco (exemplo):

```sql
CREATE DATABASE delivery_system;
```

2. Crie as tabelas necessárias. Este repositório não contém um script SQL completo; se quiser, posso gerar um script inicial com as tabelas base (usuários, restaurantes, pedidos, itens, etc.).

3. Ajuste as credenciais e a URL de conexão nos arquivos abaixo, conforme seu ambiente:

- `src/Delivery/config/Conexao.java`
- `src/util/ConnectionFactory.java`

Exemplo (valores atuais encontrados no projeto):

```java
private static final String URL = "jdbc:mysql://localhost:3306/delivery_system";
private static final String USER = "root";
private static final String PASS = "paik2548";
```

Substitua `USER` e `PASS` pelas credenciais do seu MySQL local. Não mantenha senhas sensíveis em repositórios públicos.

## Como compilar e executar

Opções rápidas:

- IntelliJ IDEA (recomendado para desenvolvimento):
	1. Abra o projeto em File → Open.
	2. Configure o SDK do Java (Project Structure → SDKs).
	3. Adicione o JAR do MySQL Connector/J como biblioteca do projeto (Project Structure → Libraries) ou coloque-o na pasta `lib/` e adicione ao classpath.
	4. Rode a classe `Delivery.Main` (menu Run → Run 'Main').

- Linha de comando (exemplo mínimo):

```bash
# a) compile todas as classes (assumindo que você esteja na raiz do projeto)
find src -name "*.java" > sources.txt
javac -d out -sourcepath src @sources.txt -cp ".:lib/*"

# b) execute (inclua o connector no classpath)
java -cp out:lib/* Delivery.Main
```

Observações:
- Ajuste `lib/*` para o local onde você guardou o JAR do MySQL Connector/J.
- O projeto usa pacotes (ex.: `Delivery`), por isso é importante compilar mantendo a estrutura de pacotes.

## Problemas comuns

- Erro ao conectar no banco: verifique se o MySQL está rodando, se o banco `delivery_system` existe e se `USER`/`PASS` estão corretos.
- Driver JDBC ausente: adicione o MySQL Connector/J ao classpath.

## Boas práticas e segurança

- Não commit senhas ou credenciais em repositórios públicos. Considere usar variáveis de ambiente ou um arquivo de configuração ignorado pelo Git.
- Para produção, use um usuário de banco com permissões mínimas necessárias.

## Próximos passos sugeridos

- Adicionar um script SQL inicial em `db/schema.sql` para criar tabelas básicas.
- Converter o projeto para usar um sistema de build (Maven/Gradle) para gerenciar dependências (Connector/J) e facilitar a execução.

---

Se quiser, eu posso:
- Gerar o script SQL inicial com base nas classes `modelo` e `dao`.
- Adicionar um `README` em inglês também.

Caso deseje que eu atualize o README com informações adicionais (ex.: scripts SQL ou instruções específicas para Windows), me diga qual detalhe quer incluir.