#!/bin/bash

# 1. Configuração de Variáveis e Flags
ARCH_ONLY=false
NOME_MODULO=""

for arg in "$@"; do
  if [ "$arg" == "--arch" ]; then
    ARCH_ONLY=true
    NOME_MODULO="arch-check"
  elif [ -z "$NOME_MODULO" ]; then
    NOME_MODULO="$arg"
  fi
done

# Verifica se o desenvolvedor passou o nome ou a flag
if [ -z "$NOME_MODULO" ]; then
  echo "❌ Erro: Informe o nome do módulo."
  echo "Uso: ./gerar-modulo.sh dominio-pagamento ou ./gerar-modulo.sh --arch"
  exit 1
fi

# 2. Definição de Arquétipo e Pacote
if [ "$ARCH_ONLY" = true ]; then
    ARCHETYPE_ID="arquitetura-archetype"
    PACOTE="dev.ofernando.arquitetura"
    echo "🛡️  Modo Arquitetura ativado. Gerando módulo 'arch-check'..."
else
    ARCHETYPE_ID="dominio-archetype"
    SUFIXO_MODULO="${NOME_MODULO#dominio-}"
    SUFIXO_PACOTE="${SUFIXO_MODULO//-/}"
    PACOTE="dev.ofernando.${SUFIXO_PACOTE}"
    DOMAIN_CLASS_NAME=$(echo "$SUFIXO_MODULO" | sed 's/.*/\L&/; s/./\U&/')
fi

echo "🔍 Lendo informações do projeto pai..."

# O Maven extrai dinamicamente as informações do pom.xml atual na raiz!
PARENT_GROUP_ID=$(./mvnw help:evaluate -Dexpression=project.groupId -q -DforceStdout)

if [ $? -ne 0 ] || [[ "$PARENT_GROUP_ID" == *"ERROR"* ]]; then
  echo "❌ Erro: O Maven falhou ao ler o pom.xml pai."
  echo "Por favor, rode './mvnw clean compile' para verificar os erros do seu projeto."
  exit 1
fi

PARENT_ARTIFACT_ID=$(./mvnw help:evaluate -Dexpression=project.artifactId -q -DforceStdout)
PARENT_VERSION=$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout)

if [ $? -ne 0 ] || [[ "$PARENT_ARTIFACT_ID" == *"ERROR"* ]] || [[ "$PARENT_VERSION" == *"ERROR"* ]]; then
  echo "❌ Erro: O Maven falhou ao ler as informações do pom.xml pai."
  exit 1
fi

echo "🚀 Gerando o módulo '$NOME_MODULO' herdando de '$PARENT_ARTIFACT_ID'..."

# 3. Execução do Maven Archetype
./mvnw archetype:generate \
  -DarchetypeGroupId=dev.ofernando \
  -DarchetypeArtifactId=$ARCHETYPE_ID \
  -DarchetypeVersion=1.0-SNAPSHOT \
  -DarchetypeCatalog=local \
  -DgroupId="$PARENT_GROUP_ID" \
  -DparentArtifactId="$PARENT_ARTIFACT_ID" \
  -DartifactId="$NOME_MODULO" \
  -Dpackage="$PACOTE" \
  -Dversion="$PARENT_VERSION" \
  -DinteractiveMode=false \
  -DdomainClassName="$DOMAIN_CLASS_NAME"

if [ $? -ne 0 ]; then
  echo "❌ Erro: Falha ao gerar o módulo via Archetype."
  exit 1
fi

echo "============================================================"
echo "✅ Gerando arquivos..."

# 4. Adiciona no dependencyManagement do pom.xml raiz (parent)
echo "⚙️  Registrando '$NOME_MODULO' no pom.xml (dependencyManagement)..."
if ! grep -q "<artifactId>$NOME_MODULO</artifactId>" pom.xml; then
  SCOPE_TAG=""
  [ "$ARCH_ONLY" = true ] && SCOPE_TAG="        <scope>test</scope>\n"

  # CORREÇÃO: Procura o fechamento de dependencies DENTRO de dependencyManagement
  sed -i "/<dependencyManagement>/, /<\/dependencies>/ { /<\/dependencies>/ s/<\/dependencies>/      <dependency>\n        <groupId>\${project.groupId}<\/groupId>\n        <artifactId>$NOME_MODULO<\/artifactId>\n        <version>\${project.version}<\/version>\n$SCOPE_TAG      <\/dependency>\n    <\/dependencies>/ }" pom.xml
fi

# 5. Adiciona no app/pom.xml (Apenas se NÃO for arquitetura)
if [ "$ARCH_ONLY" = false ]; then
    echo "⚙️  Adicionando '$NOME_MODULO' no app/pom.xml (runner)..."
    if ! grep -q "<artifactId>$NOME_MODULO</artifactId>" app/pom.xml; then
        # CORREÇÃO: Injeta antes do fechamento da tag </dependencies> principal
        sed -i "/<\/dependencies>/ { 0, /<\/dependencies>/ s/<\/dependencies>/        <dependency>\n            <groupId>\${project.groupId}<\/groupId>\n            <artifactId>$NOME_MODULO<\/artifactId>\n        <\/dependency>\n    <\/dependencies>/ }" app/pom.xml
    fi
fi

# Mensagem Final Sucesso
echo -e "\n============================================================"
echo -e "\033[32m🚀 Sucesso! Módulo '$NOME_MODULO' gerado e registrado automaticamente.\033[0m"
echo -e "Rode \033[1;33m./mvnw clean compile\033[0m para validar."

if [ "$ARCH_ONLY" = false ]; then
    echo -e "\n\033[1;33m📋 PRÓXIMOS PASSOS PARA IMPLEMENTAR O DOMÍNIO:\033[0m"
    echo -e "1. Defina os atributos em: \033[32m${NOME_MODULO}/src/main/java/${PACOTE//./\/}/domain/model/${DOMAIN_CLASS_NAME}.java\033[0m"
    echo -e "2. Implemente o repositório JPA no adapter outbound."
    echo -e "3. Remova os 'UnsupportedOperationException' após implementar a lógica."
else
    echo -e "\n\033[1;33m📋 PRÓXIMOS PASSOS PARA O MÓDULO DE ARQUITETURA:\033[0m"
    echo -e "1. Defina as regras do ArchUnit em: \033[32m${NOME_MODULO}/src/main/java/dev/ofernando/arquitetura/RegrasDddBase.java\033[0m"
fi
echo "==========================================================="
