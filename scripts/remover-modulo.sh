#!/bin/bash

# Verifica se o desenvolvedor passou o nome do módulo
if [ -z "$1" ]; then
  echo "❌ Erro: Informe o nome do módulo."
  echo "Uso: ./remover-modulo.sh dominio-pagamento"
  exit 1
fi

NOME_MODULO=$1

echo -e "\n🗑️  Iniciando remoção completa do módulo: $NOME_MODULO"

# 1. Remover a pasta do módulo física
if [ -d "$NOME_MODULO" ]; then
    rm -rf "$NOME_MODULO"
    echo -e "   ✅ Pasta /$NOME_MODULO removida."
fi

# Função para remover blocos de dependência e tags de módulo de forma segura
limpar_pom() {
    local ARQUIVO=$1
    local MODULO=$2

    if [ -f "$ARQUIVO" ]; then
        # Remove o bloco <dependency>...</dependency> que contém o artifactId do módulo
        # Esta lógica do sed agrupa o bloco e deleta apenas se encontrar o nome exato
        sed -i "/<dependency>/{:a;N;/<\/dependency>/!ba;/<artifactId>$MODULO<\/artifactId>/d}" "$ARQUIVO"
        
        # Remove a declaração do <module>...</module>
        sed -i "/<module>$MODULO<\/module>/d" "$ARQUIVO"
        
        echo -e "   ✅ Referências limpas em: $ARQUIVO"
    fi
}

# 2. Limpar o pom.xml raiz (parent)
limpar_pom "pom.xml" "$NOME_MODULO"

# 3. Limpar o app/pom.xml (runner)
limpar_pom "app/pom.xml" "$NOME_MODULO"

# 4. Limpeza de linhas em branco duplicadas que o sed costuma deixar
# (Opcional, deixa o arquivo mais limpo)
if [[ "$OSTYPE" == "darwin"* ]]; then
    sed -i '' '/^$/N;/^\n$/D' pom.xml 2>/dev/null
else
    sed -i '/^$/N;/^\n$/D' pom.xml 2>/dev/null
fi

echo -e "\n============================================================"
echo -e "\033[1;32m🚀 Sucesso! Módulo e referências removidos.\033[0m"
echo -e "Rode \033[1;33m'./mvnw clean compile'\033[0m para validar o projeto."
echo -e "\n============================================================"
