# Identity

Microserviço responsável pela identidade dos usuários: cadastro, autenticação,
alteração de senha, endereços, tokens de acesso/renovação e controle de acesso.

## Estrutura do projeto

```text
src/main/java/com/matheuscrz/identity
├── IdentityApplication.java       # ponto de entrada da aplicação Spring Boot
├── adapter
│   ├── in/web                    # entrada HTTP (controllers, DTOs e erros)
│   └── out
│       ├── persistence           # implementação da persistência JPA
│       └── security              # hash de senha e tokens JWT
├── application
│   ├── port
│   │   ├── in                   # casos de uso expostos pela aplicação
│   │   └── out                  # contratos das dependências externas
│   └── service                  # orquestração dos casos de uso
├── config                         # configurações do Spring e infraestrutura
└── domain
├── exception                # exceções e regras de erro do domínio
├── model                    # entidades, value objects, enums
└── service                   # serviços de domínio (quando necessários)
```

## Arquitetura

O projeto segue uma arquitetura hexagonal (Ports and Adapters):

```text
HTTP -> adapter.in.web -> application.service -> application.port.out
├── persistence
└── security
```

- **Domínio**: contém regras e conceitos centrais, sem depender de frameworks.
- **Aplicação**: define os casos de uso e coordena o domínio.
- **Portas de entrada**: interfaces dos casos de uso (`application/port/in`).
- **Portas de saída**: contratos para banco, tokens e segurança (`port/out`).
- **Adaptadores de entrada**: controllers REST e objetos de requisição/resposta.
- **Adaptadores de saída**: implementações JPA, Argon2 e JWT.

## Componentes principais

### Entrada HTTP

- `AuthController`: login, renovação de token e operações de autenticação.
- `UserController`: cadastro e gerenciamento de usuários.
- `UserAddressController`: gerenciamento dos endereços do usuário.
- `GlobalExceptionHandler`: converte exceções em respostas HTTP padronizadas.
- `ErrorTypes`: classificação dos erros retornados pela API.
- `dto/`: contratos públicos da API; não expõem diretamente entidades internas.

### Casos de uso

- `AuthenticateUserUseCase`: autenticação e ciclo de vida dos tokens.
- `ManageUserUseCase`: criação, consulta, atualização e operações de usuário.
- `ManageUserAddressUseCase`: criação, alteração e remoção de endereços.
- `AuthApplicationService`, `UserApplicationService` e
`UserAddressApplicationService`: implementam esses fluxos.
- `UserInputNormalizer`: normaliza entradas antes da aplicação das regras.

### Domínio

- `User`: agregado principal do usuário.
- `Email`: value object que valida e representa e-mails.
- `UserAddress`: endereço associado ao usuário.
- `RefreshToken`: token persistido para renovação de sessão.
- `Role` e `UserStatus`: permissões e estado do usuário.
- `RefreshTokenStatus`: estado do token de renovação.
- `DomainException` e subclasses: erros de negócio, como credenciais inválidas, e-mail duplicado ou token inválido.

### Persistência

- Entidades JPA: `UserJpaEntity`, `UserAddressJpaEntity` e
`RefreshTokenJpaEntity`, com `BaseJpaEntity` para dados compartilhados.
- Repositórios Spring Data: interfaces `SpringData*Repository`.
- Adaptadores: `UserPersistenceAdapter`, `UserAddressPersistenceAdapter` e `RefreshTokenPersistenceAdapter`.
- Mappers: convertem entre modelos de domínio e entidades JPA.
- `db/migration/V1__create_identity_tables.sql`: esquema inicial do banco,executado pelo mecanismo de migrations configurado na aplicação.

### Segurança

- `Argon2PasswordSecurityAdapter`: gera e verifica hashes de senha; senhas não devem ser armazenadas em texto puro.
- `JwtTokenProviderAdapter`: emite e valida tokens JWT.
- `SecurityConfig`: define autenticação, autorização e rotas públicas/protegidas.

## Configuração

- `application.yml`: configurações externas da aplicação, banco e segurança.
- `ClockConfig`: fornece um relógio controlável para regras dependentes de tempo.
- `JpaAuditingConfig`: habilita auditoria das entidades persistidas.

Não versione segredos, chaves JWT ou senhas de banco. Use variáveis de ambiente ou um gerenciador de segredos para esses valores.

## Testes

- `ArchitectureTest`: verifica as regras de dependência entre camadas.
- `IdentityApplicationTests`: valida o carregamento básico da aplicação.
- Testes de segurança: cobrem Argon2 e emissão/validação de JWT.
- `AuthApplicationServiceTest`: cobre o fluxo de autenticação.
- `EmailTest` e `UserTest`: cobrem regras do domínio.

Execute os testes com o gerenciador de build configurado no projeto. Antes de alterar uma camada, mantenha suas dependências apontando para dentro: controllers dependem de portas de entrada, serviços dependem de portas, e adaptadores implementam portas sem vazar detalhes de infraestrutura para o domínio.

## Fluxo típico de autenticação

1. O cliente envia credenciais ao endpoint de autenticação.
2. O controller transforma a requisição em dados de entrada.
3. O serviço busca o usuário pela porta de persistência.
4. A porta de segurança verifica a senha com Argon2.
5. O provedor JWT emite o token de acesso e o refresh token é persistido.
6. Exceções de domínio são tratadas pelo `GlobalExceptionHandler`.

## Convenções para evolução

- Coloque regras de negócio em modelos/serviços de domínio, não em controllers.
- Adicione novos recursos por meio de uma porta de entrada e um caso duso.
- Mantenha DTOs, entidades JPA e modelos de domínio separados.
- Crie uma migration nova para cada alteração de banco; não edite migrations já aplicadas em ambientes compartilhados.
- Adicione testes unitários para regras novas e testes de arquitetura quando aestrutura de dependências for alterada.
