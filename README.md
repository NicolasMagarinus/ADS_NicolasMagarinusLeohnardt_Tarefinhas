# TodoList

Aplicação web Java (Jakarta EE 10) empacotada como WAR. O projeto usa Java 21, GlassFish 7, PostgreSQL e o Maven Wrapper. As instruções abaixo preparam um ambiente de desenvolvimento no Ubuntu; o Windows continua suportado pelo `mvnw.cmd`.

## Requisitos

- Ubuntu 24.04 LTS (ou versão compatível com OpenJDK 21).
- JDK 21, PostgreSQL, `curl` e `unzip`.
- Eclipse GlassFish 7.1.x com perfil Jakarta EE Platform 10 e configurado para usar JDK 21.
- Tema Manhattan 7.0.0 instalado no repositório Maven local. Esse artefato não está no Maven Central. Sem ele, use a opção de build sem o tema descrita abaixo.

## Preparar o Ubuntu

Instale o JDK e o PostgreSQL pelos pacotes da distribuição:

```bash
sudo apt update
sudo apt install openjdk-21-jdk postgresql curl unzip
sudo systemctl enable --now postgresql
```

Confirme que o Java 21 está selecionado:

```bash
java -version
javac -version
```

Se houver mais de uma versão do Java instalada, selecione o JDK 21 com `sudo update-alternatives --config java` e `sudo update-alternatives --config javac`. Defina `JAVA_HOME` para a instalação selecionada se o GlassFish não encontrá-la automaticamente. Em Ubuntu, o caminho costuma ser `/usr/lib/jvm/java-21-openjdk-amd64`.

Depois de instalar o GlassFish e baixar o driver JDBC conforme as seções abaixo, valide o ambiente com:

```bash
export GF_HOME="$HOME/glassfish7"
./scripts/check-ubuntu.sh
```

O script só verifica os requisitos; ele não instala pacotes nem altera configurações do sistema.

## PostgreSQL

O datasource declarado em `src/main/webapp/WEB-INF/web.xml` espera PostgreSQL local na porta `5432`, banco `todolist`, usuário `todolist` e senha `todolist`. Crie-os uma vez:

```bash
sudo -u postgres createuser --pwprompt todolist
sudo -u postgres createdb --owner=todolist todolist
```

Quando solicitado, defina a senha `todolist` para corresponder ao datasource. Para outro endereço, porta, banco ou credenciais, ajuste o elemento `<data-source>` em `src/main/webapp/WEB-INF/web.xml`.

No primeiro deploy, o Flyway cria a tabela `usuario` e o administrador inicial. Acesse com `admin@todolist.local` e senha `Admin@12345`; altere a senha no primeiro acesso.

## GlassFish 7

Baixe uma distribuição Eclipse GlassFish **7.1.x Jakarta EE Platform** na [página oficial de downloads](https://glassfish.org/download_gf7.html) e extraia em um diretório de sua escolha. A linha 7 implementa Jakarta EE 10, usada por esta aplicação. Para iniciar o domínio padrão:

```bash
export GF_HOME="$HOME/.local/opt/glassfish7/glassfish7"
"$GF_HOME/bin/asadmin" start-domain domain1
```

Se extraiu o GlassFish em outro local, ajuste `GF_HOME`. Instale o driver JDBC do PostgreSQL no diretório de bibliotecas do domínio e reinicie o servidor para que ele carregue o driver:

```bash
cp "$HOME/.m2/repository/org/postgresql/postgresql/42.7.4/postgresql-42.7.4.jar" \
  "$GF_HOME/glassfish/domains/domain1/lib/"
"$GF_HOME/bin/asadmin" restart-domain domain1
```

Se o driver ainda não estiver em `~/.m2`, o build abaixo o baixa como dependência. Como alternativa, baixe o artefato `org.postgresql:postgresql:42.7.4` pelo Maven e copie o JAR para o diretório do domínio.

## Compilar e executar

Na raiz do projeto, o wrapper Linux já tem permissão de execução. O primeiro uso baixa o Maven 3.9.9:

```bash
./mvnw -version
./mvnw -P '!manhattan' package
```

O comando acima compila sem o tema comercial Manhattan, que precisa ser instalado manualmente no Maven local para usar o perfil padrão. Depois de instalar o tema, basta executar `./mvnw package`.

Com o GlassFish iniciado, faça o deploy e acesse a aplicação:

```bash
"$GF_HOME/bin/asadmin" deploy --force=true target/todolist.war
```

Abra <http://localhost:8080/todolist/>. Para parar o domínio:

```bash
"$GF_HOME/bin/asadmin" stop-domain domain1
```

## Windows

No Windows, use `mvnw.cmd` no lugar de `./mvnw`. As mesmas dependências de execução (JDK 21, GlassFish 7 e PostgreSQL) e a configuração do datasource se aplicam.
