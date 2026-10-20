# TodoList — Login e Logout

Recorte do projeto TodoList (Jakarta EE + PrimeFaces) com **apenas** a autenticação:
tela de login, logout, conexão com o PostgreSQL e proteção das páginas de `/app/*`.

**Stack:** Jakarta EE 10 (GlassFish 7) · JSF + PrimeFaces 13 + tema Manhattan · JPA + EJB · PostgreSQL · Flyway · Jakarta Security.

## O que está incluído

- Tela de login (`login.xhtml`) com mensagem de erro para credenciais inválidas.
- Logout pelo menu do avatar → **Sair** (encerra a sessão e volta ao login).
- Senha guardada como hash PBKDF2; usuários bloqueados (`ativo = false`) não entram.
- Páginas de `/app/*` exigem login; ao abrir uma delas deslogado, o usuário vai para o login e **volta para a página pedida** depois de entrar.
- Conexão com o banco (`<data-source>` no `web.xml`) e criação da tabela `usuario` + administrador inicial via Flyway no deploy.
- `app/dashboard.xhtml` é uma **página provisória** ("Olá, fulano!") só para o login ter para onde ir.

**Fora desta etapa:** cadastro de conta, perfil/troca de senha, área admin, tarefas e demais telas, páginas de erro 404/500.

## Como rodar no Windows

Pré-requisitos: JDK 21, NetBeans 23+, GlassFish 7.0.x, PostgreSQL 14+ e o jar do tema Manhattan (`manhattan-7.0.0.jar`) — os mesmos do projeto completo.

1. **Banco** (no psql/pgAdmin como `postgres`):
   ```sql
   CREATE USER todolist WITH PASSWORD 'todolist';
   CREATE DATABASE todolist OWNER todolist;
   ```
   > ⚠️ Use um banco **vazio**. Se o banco `todolist` já foi usado pelo projeto completo, o Flyway recusa o deploy
   > (`Migration checksum mismatch for migration version 1`), porque o `V1__schema.sql` daqui só tem a tabela `usuario`.
   > Nesse caso crie outro banco (e troque `<database-name>` no `web.xml`) ou apague o schema do banco antigo.
2. **Driver JDBC**: copie `postgresql-42.7.4.jar` para `C:\glassfish7\glassfish\domains\domain1\lib\` e reinicie o GlassFish.
3. **Conexão**: ajuste o bloco `<data-source>` em `src/main/webapp/WEB-INF/web.xml` se o seu PostgreSQL não for `localhost:5432` / `todolist` / `todolist` / `todolist`.
4. **Tema Manhattan**: se o projeto da disciplina já compila na sua máquina, o jar já está no seu `~/.m2`. Senão:
   ```bat
   mvnw.cmd install:install-file -Dfile=C:\caminho\manhattan-7.0.0.jar -DgroupId=org.primefaces.themes -DartifactId=manhattan -Dversion=7.0.0 -Dpackaging=jar
   ```
5. **Executar**: abra a pasta no NetBeans, selecione o GlassFish 7 e dê **Run**. URL: `http://localhost:8080/todolist/`.

**Primeiro acesso:** `admin@todolist.local` / `Admin@12345`.
(Não há tela para trocar a senha nesta etapa; isso vem com a tela de perfil.)

**Testes unitários** (sem banco nem servidor): `mvnw.cmd test`

## Roteiro de teste manual

1. Abra `http://localhost:8080/todolist/app/dashboard.xhtml` deslogado → vai para o login.
2. Entre com senha errada → "E-mail ou senha inválidos."
3. Entre com o admin → volta para a página que você tinha pedido (Início).
4. Avatar (canto superior direito) → **Sair** → volta ao login. Tente abrir `.../app/dashboard.xhtml` de novo → vai para o login outra vez.
5. Com o admin logado, acesse `.../login.xhtml` → redireciona direto para o Início.

## Estrutura

```
src/main/java/br/upf/todolist/
  config/      FlywayMigrator (roda as migrations no deploy)
  controller/  LoginController, LogoutController, UsuarioLogado (usuário da sessão)
  entity/      Usuario, Papel
  facade/      AbstractFacade, UsuarioFacade (buscarPorEmail)
  security/    SecurityConfig (login por formulário próprio) + JpaIdentityStore (confere e-mail/senha no banco)
  util/        Emails (normalização), Mensagens (growl)
src/main/resources/
  META-INF/persistence.xml
  db/migration/V1__schema.sql (tabela usuario), V2__admin_inicial.sql (admin)
src/main/webapp/
  login.xhtml, index.xhtml
  app/dashboard.xhtml            (provisória)
  WEB-INF/ template, topbar, sidebar, footer, web.xml, glassfish-web.xml, beans.xml
  resources/manhattan-layout/    (só o tema usado: layout cyan/deeporange, fontes, ícones, imagens)
```

## Relação com o projeto completo

Mesmos caminhos e nomes do projeto original, então as próximas funcionalidades entram como commits aditivos.

- **Idênticos ao original:** `pom.xml`, `mvnw*`, `.mvn/`, `persistence.xml`, `V2__admin_inicial.sql`, `FlywayMigrator`, `LoginController`, `LogoutController`, `UsuarioLogado`, `Usuario`, `Papel`, `AbstractFacade`, `JpaIdentityStore`, `SecurityConfig`, `Emails`, `Mensagens`, `login.xhtml`*, `template.xhtml`, `footer.xhtml`, `glassfish-web.xml`, `beans.xml`, `index.xhtml`, `JpaIdentityStoreTest`, `MensagensTest`.
  (*`login.xhtml` sem o link "Criar conta".)
- **Reduzidos:** `UsuarioFacade` (só `buscarPorEmail`), `V1__schema.sql` (só `usuario`), `web.xml` (sem a constraint de `/admin/*` e sem as páginas de erro 404/500), `topbar.xhtml` (sem sino e sem "Meu perfil"), `sidebar.xhtml` (só "Início"), `app.css` (vazio), `.gitignore`.
- **Novos:** `app/dashboard.xhtml` (provisória), `UsuarioFacadeTest` (testa `buscarPorEmail`).
