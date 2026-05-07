# 🛠️ HC Tooling - Governança e Automação DDD

Este repositório contém o conjunto de ferramentas de automação, arquétipos Maven e scripts de infraestrutura para o desenvolvimento de microsserviços multimodulares utilizando **Quarkus**, **Java 17+** e os princípios de **Domain-Driven Design (DDD)**.

## 📂 Estrutura do Repositório

```text
archetypes/
├── dominio-archetype/            # Template para novos domínios (REST, gRPC, Kafka, JPA)
├── arquitetura-archetype/        # Template para o módulo central 'arch-check' (ArchUnit)
├── scripts/
│   ├── gerar-modulo.sh           # Automação para criação e registro de novos módulos
│   └── remover-modulo.sh         # Automação para exclusão limpa de módulos e dependências
└── pom.xml                       # Gerenciador de build dos arquétipos
```

## 🚀 Como Utilizar

Os scripts foram desenhados para serem executados na raiz do seu projeto de **Backend**.

### Configuração Inicial (Links Simbólicos)

Para usar os scripts sem precisar copiá-los, crie links simbólicos no seu projeto de backend:

```bash
ln -s ../scripts/gerar-modulo.sh gerar-modulo.sh
ln -s ../scripts/remover-modulo.sh remover-modulo.sh
chmod +x *.sh
```

### 1. Criando um Novo Domínio

Para gerar um módulo completo com suporte a gRPC, Kafka e persistência:

```bash
./gerar-modulo.sh dominio-compras
```

* **Ações executadas:**
  * Gera a estrutura de pastas DDD.
  * Registra o módulo no `dependencyManagement` do projeto pai.
  * Adiciona o módulo como dependência no `app-shell` (runner).

### 2. Criando o Módulo de Governança

Para criar o módulo central de validação de regras de arquitetura:

```bash
./gerar-modulo.sh --arch
```

* **Ações executadas:** Cria o módulo `arch-check` com as dependências do **ArchUnit** pré-configuradas.

---

## 🏗️ Padrão Arquitetural (Building Blocks)

Os módulos gerados seguem a **Arquitetura Hexagonal**:

* **`api`**: Porta de entrada REST (JAX-RS) e gRPC. Contém os DTOs e Mappers de entrada.
* **`application`**: Casos de Uso (`usecase`) e orquestradores de eventos (`handler`).
* **`domain`**: O núcleo do negócio. Contém `model` (Entidades e Agregados), `repository` (Interfaces/Ports) e Value Objects.
* **`infrastructure`**: Implementações técnicas. Adapters Inbound (Listeners Kafka) e Outbound (JPA, Clientes gRPC).

---

## ⚖️ Regras de Arquitetura (ArchUnit)

O módulo `arch-check` garante que o design não degrade com o tempo. Algumas regras validadas:

* **Isolamento do Domínio:** O pacote `domain` não pode depender de `infrastructure` ou `api`.
* **Fluxo de Dependência:** A camada `api` deve se comunicar apenas com `application` ou `domain`.
* **Integridade do Repositório:** Interfaces de repositório devem residir apenas no pacote `domain.repository`.

---

## 🔧 Manutenção dos Templates

Se você alterar qualquer código dentro da pasta `arquitetura-archetype/` ou `dominio-archetype/`, é necessário reinstalá-los para que os scripts reflitam as mudanças:

```bash
# Na raiz do hc-tooling
mvn clean install
```

---

## 💡 Dicas de Desenvolvimento

1. **gRPC:** Após gerar um módulo, rode `./mvnw compile` para que as classes Java do Proto sejam geradas em `target/generated-sources`.
2. **Imutabilidade:** Utilize **Java Records** para seus Value Objects dentro do domínio.
3. **Configurações:** Use o arquivo `src/main/resources/META-INF/microprofile-config.properties` dentro de cada módulo para definir configurações padrões específicas daquele domínio.

---

**Desenvolvido para padronização de microsserviços Java.**
