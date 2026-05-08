#!/bin/bash

# 1. Configuração de Variáveis e Flags
ARCH_ONLY=false
NOME_MODULO=""
RUNNER_FOLDER="app"

for arg in "$@"; do
  if [ "$arg" == "--arch" ]; then
    ARCH_ONLY=true
    NOME_MODULO="arch-check"
  elif [ -z "$NOME_MODULO" ]; then
    NOME_MODULO="$arg"
  elif [ "$arg" == "--runner-folder" ]; then
    # Próximo argumento deve ser o nome da pasta runner
    continue
  elif [ "$ARCH_ONLY" = false ] && [ -n "$NOME_MODULO" ] && [ -d "$arg" ]; then
    # remover ultima barra se existir
    RUNNER_FOLDER="${arg%/}"
  fi
done

# Verifica se o desenvolvedor passou o nome ou a flag
if [ -z "$NOME_MODULO" ]; then
  echo "❌ Erro: Informe o nome do módulo."
  echo "Uso: ./gerar-modulo.sh dominio-pagamento ou ./gerar-modulo.sh --arch"
  exit 1
fi

# adicionar validaçao se pasta /app existe, se nao existir, lançar mensagem de erro e solicitar o nome da pasta runner em uma flag
if [ ! -d "$RUNNER_FOLDER" ]; then
  echo "❌ Erro: A pasta '$RUNNER_FOLDER' não foi encontrada. Por favor, informe a flag '--runner-folder' caso a pasta runner for diferente de ./app seguida do nome da pasta runner"
  echo "Uso: './gerar-modulo.sh dominio-pagamento --runner-folder ./app-runner'"
  exit 1
else
  echo "✅ Pasta runner '$RUNNER_FOLDER' encontrada. Continuando com pasta padrão..."
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

# 2. Definição de Arquétipo e Pacote
if [ "$ARCH_ONLY" = true ]; then
    ARCHETYPE_ID="arquitetura-archetype"
    PACOTE="$PARENT_GROUP_ID.arquitetura"
    echo "🛡️  Modo Arquitetura ativado. Gerando módulo 'arch-check'..."
else
    ARCHETYPE_ID="dominio-archetype"
    SUFIXO_MODULO="${NOME_MODULO#dominio-}"
    SUFIXO_PACOTE="${SUFIXO_MODULO//-/}"
    PACOTE="$PARENT_GROUP_ID.${SUFIXO_PACOTE}"
    DOMAIN_CLASS_NAME=$(echo "$SUFIXO_MODULO" | sed 's/.*/\L&/; s/./\U&/')
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

# 4. Configuração Global (Parent POM)
echo "⚙️  Verificando configurações de governança no pom.xml..."

# 4.1 Properties (archunitVersion e argLine)
# Insere logo após a propriedade <revision> para ficar no bloco principal
if ! grep -q "<archunitVersion>" pom.xml; then
    sed -i "/<revision>/a \    <archunitVersion>1.4.2</archunitVersion>\n    <argLine></argLine>" pom.xml
fi

# 4.2 DependencyManagement (arch-check e archunit)
if ! grep -q "<artifactId>$NOME_MODULO</artifactId>" pom.xml; then
  SCOPE_TAG=""
  [ "$ARCH_ONLY" = true ] && SCOPE_TAG="        <scope>test</scope>\n"

  # Procura o fechamento de dependencies DENTRO de dependencyManagement
  sed -i "/<dependencyManagement>/, /<\/dependencies>/ { /<\/dependencies>/ s;</dependencies>;      <dependency>\n        <groupId>\${project.groupId}<\/groupId>\n        <artifactId>$NOME_MODULO<\/artifactId>\n        <version>\${project.version}<\/version>\n$SCOPE_TAG      <\/dependency>\n    <\/dependencies>; }" pom.xml
fi

# Garante archunit-junit5 no dependencyManagement
if ! grep -q "<artifactId>archunit-junit5</artifactId>" pom.xml; then
    sed -i "/<dependencyManagement>/, /<\/dependencies>/ { /<\/dependencies>/ s;</dependencies>;      <dependency>\n        <groupId>com.tngtech.archunit<\/groupId>\n        <artifactId>archunit-junit5<\/artifactId>\n        <version>\${archunitVersion}<\/version>\n      <\/dependency>\n    <\/dependencies>; }" pom.xml
fi

# 4.3 Surefire Plugin (dependenciesToScan e argLine fix)
# Corrige @{argLine} para ${argLine} para evitar crash sem JaCoCo
sed -i 's/@{argLine}/\${argLine}/g' pom.xml

if ! grep -q "<dependenciesToScan>" pom.xml; then
    # Injeta dependenciesToScan logo após a tag <configuration> do surefire
    sed -i '/<artifactId>maven-surefire-plugin<\/artifactId>/, /<\/configuration>/ s/<configuration>/<configuration>\n          <dependenciesToScan>\n            <dependency>\${project.groupId}:arch-check<\/dependency>\n          <\/dependenciesToScan>/' pom.xml
fi

# 5. Adiciona dependência do arch-check no novo módulo (Se NÃO for arch-check)
if [ "$ARCH_ONLY" = false ]; then
    echo "⚙️  Vinculando '$NOME_MODULO' ao 'arch-check'..."
    # Injeta antes do fechamento da tag </dependencies> do novo módulo
    sed -i "/<\/dependencies>/ i \        <dependency>\n            <groupId>\${project.groupId}</groupId>\n            <artifactId>arch-check</artifactId>\n        </dependency>" "$NOME_MODULO/pom.xml"
fi

# 5. Adiciona no app/pom.xml (Apenas se NÃO for arquitetura)
if [ "$ARCH_ONLY" = false ]; then
    echo "⚙️  Adicionando '$NOME_MODULO' no $RUNNER_FOLDER/pom.xml (runner)..."
    if ! grep -q "<artifactId>$NOME_MODULO</artifactId>" "$RUNNER_FOLDER/pom.xml"; then
        # CORREÇÃO: Injeta antes do fechamento da tag </dependencies> principal
        sed -i "/<\/dependencies>/ { 0, /<\/dependencies>/ s/<\/dependencies>/        <dependency>\n            <groupId>\${project.groupId}<\/groupId>\n            <artifactId>$NOME_MODULO<\/artifactId>\n        <\/dependency>\n    <\/dependencies>/ }" "$RUNNER_FOLDER/pom.xml"

        if [ $? -ne 0 ]; then
          echo "❌ Erro: Falha ao adicionar o módulo no $RUNNER_FOLDER/pom.xml."
          exit 1
        fi
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
    echo -e "1. Defina as regras do ArchUnit em: \033[32m${NOME_MODULO}/src/main/java/${PACOTE//./\/}/RegrasDddBase.java\033[0m"
fi
echo "==========================================================="
