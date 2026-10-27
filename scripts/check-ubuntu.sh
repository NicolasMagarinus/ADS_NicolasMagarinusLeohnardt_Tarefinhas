#!/usr/bin/env bash
set -u

falhas=0

ok() {
    printf 'OK   %s\n' "$1"
}

falha() {
    printf 'FALTA %s\n' "$1"
    falhas=$((falhas + 1))
}

if command -v java >/dev/null 2>&1 && java -version 2>&1 | grep -Eq 'version "21([.]|\")'; then
    ok 'JDK 21 encontrado'
else
    falha 'JDK 21 (instale com: sudo apt install openjdk-21-jdk)'
fi

if command -v psql >/dev/null 2>&1; then
    ok 'cliente PostgreSQL encontrado'
else
    falha 'cliente PostgreSQL (instale com: sudo apt install postgresql)'
fi

if command -v pg_isready >/dev/null 2>&1 && pg_isready -h localhost -p 5432 >/dev/null 2>&1; then
    ok 'PostgreSQL responde em localhost:5432'
else
    falha 'PostgreSQL em execução em localhost:5432'
fi

if [[ -n "${GF_HOME:-}" && -x "$GF_HOME/bin/asadmin" ]]; then
    ok "GlassFish encontrado em $GF_HOME"
else
    falha 'GlassFish (defina GF_HOME para o diretório da instalação)'
fi

if [[ -f "$HOME/.m2/repository/org/postgresql/postgresql/42.7.4/postgresql-42.7.4.jar" ]]; then
    ok 'driver JDBC PostgreSQL encontrado no repositório Maven local'
else
    falha 'driver JDBC PostgreSQL local (execute o build Maven antes de copiá-lo para GlassFish)'
fi

if (( falhas > 0 )); then
    printf '\n%d requisito(s) precisam de atenção. Consulte a seção Ubuntu no README.\n' "$falhas"
    exit 1
fi

printf '\nAmbiente básico pronto. Confirme o banco todolist e o datasource no web.xml.\n'
