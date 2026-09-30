# Requisitos do ShopFlow

Este documento define **o que** o ShopFlow faz e com quais qualidades. O **como** está em [ARCHITECTURE.md](ARCHITECTURE.md).

| Marca           | Significado                         | nificado |
| --------------- | ----------------------------------- | -------- |
| 🟢 Implementado | Será entregue como código           |          |
| 🔵 Planejado    | Projetado e documentado, sem código | o        |

---

## Sumário

1. [Escopo](#1-escopo)low](#requisitos-do-shopflow)
2. [Atores](#2-atores))
3. [Requisitos funcionais](#3-requisitos-funcionais)
4. [Regras de negócio](#4-regras-de-negócio)cionalidades)
5. [Requisitos não funcionais](#5-requisitos-não-funcionais)
6. [Requisitos planejados, sem código](#6-requisitos-planejados-sem-código)
7. [Riscos e mitigações](#7-riscos-e-mitigações)ce)](#31-autenticação-e-sessão-identity-service)
8. [Premissas e restrições](#8-premissas-e-restrições)atálogo-e-estoque-catalog-service)
9. [Matriz de rastreabilidade](#9-matriz-de-rastreabilidade)ice)
10. [Glossário](#10-glossário)er-service e catalog-service)](#34-pedidos-e-saga-order-service-e-catalog-service) - [3.5 Notificações e integrações (notification-service)](#35-notificações-e-integrações-notification-service)
    --- - [3.6 Frontend](#36-frontend) - [3.7 Plataforma](#37-plataforma)

## 1. Escopoas de negócio](#4-regras-de-negócio)

- [5. Requisitos não funcionais](#5-requisitos-não-funcionais)

````mermaidquisitos planejados, sem código](#6-requisitos-planejados-sem-código)
flowchart TBos e mitigações](#7-riscos-e-mitigações)
    subgraph impl["🟢 Implementado"]remissas-e-restrições)
        direction TBstreabilidade](#9-matriz-de-rastreabilidade)
        a1["Autenticação JWT, refresh e roles"]
        a2["Catálogo, categorias e estoque"]
        a3["Carrinho, checkout e pedidos"]
        a4["Pagamento e entrega simulados"]
        a5["API Gateway"]
        a6["Kafka, Transactional Outbox, consumidores idempotentes"]
        a7["Cache Redis"]
        a8["Auditoria de alterações"]
        a9["Webhooks e e-mails"]do"]
        a10["Frontend cliente e admin"]
    end a1["Autenticação JWT, refresh e roles"]
    subgraph plan["🔵 Planejado, sem código"]
        direction TB, checkout e pedidos"]
        b1["Observabilidade: logs, métricas, traces"]
        b2["Rate limiting, blacklist de IPs, Blind Login, 2FA"]
        b3["Idempotency-Key no checkout"]consumidores idempotentes"]
        b4["Circuit Breaker e políticas de retry HTTP"]
        b5["BFF consumidor de webhooks"]
        b6["Debezium e CDC para projeções"]
        b7["OpenSearch para busca inteligente"]
        b8["Kong Gateway como alternativa"]
    endgraph plan["🔵 Planejado, sem código"]
    subgraph out["Fora de escopo"]
        direction TBilidade: logs, métricas, traces"]
        c1["Pagamento real, gateways de pagamento"]Login, 2FA"]
        c2["Logística real, cálculo de frete"]
        c3["Multi-moeda, impostos, cupons"]retry HTTP"]
        c4["Multi-região, service mesh"]
    end b6["Debezium e CDC para projeções"]
```     b7["OpenSearch para busca inteligente"]
        b8["Kong Gateway como alternativa"]
### Mapa de funcionalidades
    subgraph out["Fora de escopo"]
```mermaidrection TB
mindmap c1["Pagamento real, gateways de pagamento"]
  root((Requisitos))a real, cálculo de frete"]
    Conta3["Multi-moeda, impostos, cupons"]
      Cadastrolti-região, service mesh"]
      Login e refresh
      Logout
      Perfil
      Roles funcionalidades
    Catálogo
      Categorias
      Produtos
      Busca e paginação
      Estoque
      Auditoria
    Compran e refresh
      Carrinho
      Checkout assíncrono
      Pagamento simulado
      Entrega simulada
      Histórico de status
    Integração
      E-mails paginação
      Webhooks
    Interfaceia
      Loja
      Painel admin
    Plataforma assíncrono
      Gatewayto simulado
      Compose simulada
      Seedsrico de status
      OpenAPIo
```   E-mails
      Webhooks
--- Interface
      Loja
## 2. Atores admin
    Plataforma
| Ator | Descrição | Acesso |
| --- | --- | --- |
| Visitante | Pessoa sem login | Catálogo, cadastro e login |
| Cliente (`CUSTOMER`) | Usuário cadastrado | Tudo do visitante, carrinho, checkout e seus pedidos |
| Administrador (`ADMIN`) | Operador da loja | Gestão de catálogo, estoque, auditoria, todos os pedidos e webhooks |
| Sistema parceiro | Sistema externo simulado | Recebe webhooks |
| Operador de plataforma | Quem opera o ambiente | Inspeciona Kafka, DLT e logs |

```mermaides
flowchart LR
    V(["Visitante"]) --> RF1["Ver catálogo<br/>RF-CAT-03, 04"]                                                             |
    V --> RF2["Cadastrar e entrar<br/>RF-AUT-01, 02"]| ------------------------------------------------------------------- |
    C(["Cliente"]) --> RF3["Carrinho<br/>RF-CAR-01 a 05"]tálogo, cadastro e login                                          |
    C --> RF4["Checkout e pedidos<br/>RF-PED-01, 08, 09"]do do visitante, carrinho, checkout e seus pedidos                |
    A(["Admin"]) --> RF5["Catálogo e estoque<br/>RF-CAT-01, 02, 05"]álogo, estoque, auditoria, todos os pedidos e webhooks |
    A --> RF6["Auditoria<br/>RF-CAT-06"]rno simulado | Recebe webhooks                                                     |
    A --> RF7["Pedidos e webhooks<br/>RF-PED-10, RF-NOT-04"]ciona Kafka, DLT e logs                                        |
    P(["Parceiro"]) --> RF8["Receber webhook<br/>RF-NOT-02, 03"]
    Op(["Operador"]) --> RF9["Kafka UI e DLT<br/>RF-PLT-05"]
```wchart LR
    V(["Visitante"]) --> RF1["Ver catálogo<br/>RF-CAT-03, 04"]
--- V --> RF2["Cadastrar e entrar<br/>RF-AUT-01, 02"]
    C(["Cliente"]) --> RF3["Carrinho<br/>RF-CAR-01 a 05"]
## 3. Requisitos funcionaisedidos<br/>RF-PED-01, 08, 09"]
    A(["Admin"]) --> RF5["Catálogo e estoque<br/>RF-CAT-01, 02, 05"]
Prioridade: **M** (Must), **S** (Should), **C** (Could).
    A --> RF7["Pedidos e webhooks<br/>RF-PED-10, RF-NOT-04"]
### 3.1 Autenticação e sessão (identity-service)>RF-NOT-02, 03"]
    Op(["Operador"]) --> RF9["Kafka UI e DLT<br/>RF-PLT-05"]
| ID | Requisito | Prioridade | Critério de aceite |
| --- | --- | --- | --- |
| RF-AUT-01 | O visitante pode se cadastrar com nome, e-mail único e senha forte | M | E-mail duplicado retorna 409. Senha fraca retorna 422. Senha armazenada com Argon2id |
| RF-AUT-02 | O usuário pode entrar e receber access token JWT e refresh token | M | Access token expira em 15 min. Credenciais inválidas retornam 401 com mensagem genérica |
| RF-AUT-03 | O usuário pode renovar a sessão com rotação de refresh token | M | Refresh usado uma vez não funciona de novo. Reuso revoga a família inteira |
| RF-AUT-04 | O usuário pode encerrar a sessão | M | O refresh token é revogado |
| RF-AUT-05 | O usuário logado pode consultar seu perfil | S | `GET /api/auth/me` retorna dados sem hash de senha |
| RF-AUT-06 | O sistema distingue `CUSTOMER` e `ADMIN` | M | Rotas administrativas retornam 403 para cliente e 401 sem token |
### 3.1 Autenticação e sessão (identity-service)
### 3.2 Catálogo e estoque (catalog-service)
| ID        | Requisito                                                          | Prioridade | Critério de aceite                                                                      |
| ID | Requisito | Prioridade | Critério de aceite |---------------------------- | ---------- | --------------------------------------------------------------------------------------- |
| --- | --- | --- | --- | pode se cadastrar com nome, e-mail único e senha forte | M          | E-mail duplicado retorna 409. Senha fraca retorna 422. Senha armazenada com Argon2id    |
| RF-CAT-01 | O admin gerencia categorias | M | Criar, editar, listar. Slug único |M          | Access token expira em 15 min. Credenciais inválidas retornam 401 com mensagem genérica |
| RF-CAT-02 | O admin gerencia produtos, incluindo ativar e inativar (soft delete) | M | SKU único. Produto inativo não aparece na loja nem entra no carrinho |lia inteira              |
| RF-CAT-03 | Qualquer pessoa lista produtos com paginação, filtro por categoria, busca por nome e ordenação | M | Resposta paginada. Apenas produtos ativos |                          |
| RF-CAT-04 | Qualquer pessoa consulta o detalhe de um produto | M | Inclui disponibilidade de estoque. 404 se inexistente ou inativo |sh de senha                                      |
| RF-CAT-05 | O admin ajusta o estoque | M | Estoque nunca fica negativo (`CHECK` no banco) | | Rotas administrativas retornam 403 para cliente e 401 sem token                         |
| RF-CAT-06 | O admin consulta a auditoria de alterações | M | Filtros por entidade, ator e período. Mostra valores antes e depois |
| RF-CAT-07 | Categorias e produtos usam cache de leitura | M | Segunda leitura idêntica é servida pelo Redis. Alteração invalida o cache |

### 3.3 Carrinho (order-service)                                                                             | Prioridade | Critério de aceite                                                        |
| --------- | ---------------------------------------------------------------------------------------------- | ---------- | ------------------------------------------------------------------------- |
| ID | Requisito | Prioridade | Critério de aceite |                                                         | M          | Criar, editar, listar. Slug único                                         |
| --- | --- | --- | --- |encia produtos, incluindo ativar e inativar (soft delete)                           | M          | SKU único. Produto inativo não aparece na loja nem entra no carrinho      |
| RF-CAR-01 | O cliente adiciona produto ao carrinho | M | Produto inexistente ou inativo retorna 422 |nação | M          | Resposta paginada. Apenas produtos ativos                                 |
| RF-CAR-02 | O cliente altera a quantidade | M | Quantidade mínima 1 e máxima 99 |                          | M          | Inclui disponibilidade de estoque. 404 se inexistente ou inativo          |
| RF-CAR-03 | O cliente remove um item | M | |                                                               | M          | Estoque nunca fica negativo (`CHECK` no banco)                            |
| RF-CAR-04 | O cliente visualiza o carrinho com preços atuais e total | M | Preços vêm do catálogo. O total é apenas informativo | por entidade, ator e período. Mostra valores antes e depois       |
| RF-CAR-05 | O cliente esvazia o carrinho | S | |leitura                                                    | M          | Segunda leitura idêntica é servida pelo Redis. Alteração invalida o cache |
| RF-CAR-06 | O carrinho persiste entre sessões | M | Guardado por usuário no PostgreSQL |
### 3.3 Carrinho (order-service)
### 3.4 Pedidos e saga (order-service e catalog-service)
| ID        | Requisito                                                | Prioridade | Critério de aceite                                   |
| ID | Requisito | Prioridade | Critério de aceite |------------------ | ---------- | ---------------------------------------------------- |
| --- | --- | --- | --- |diciona produto ao carrinho                   | M          | Produto inexistente ou inativo retorna 422           |
| RF-PED-01 | O cliente finaliza a compra e recebe `202 Accepted` com o `orderId` | M | Pedido nasce em `PLACED`. Carrinho é esvaziado na mesma transação |
| RF-PED-02 | O total é calculado no servidor a partir de preços autoritativos | M | Nenhum valor monetário do cliente é aceito |          |
| RF-PED-03 | O item do pedido guarda snapshot de SKU, nome e preço | M | Alterar o preço depois não muda pedidos existentes | informativo |
| RF-PED-04 | O estoque é reservado de forma atômica e tudo ou nada | M | 10 compras simultâneas da última unidade resultam em 1 aprovada e 9 rejeitadas, sem estoque negativo |
| RF-PED-05 | O pagamento é simulado com cenário selecionável (`APPROVE`, `REJECT`) | M | Aprovado leva a `PAID`. Rejeitado leva a `CANCELLED` |
| RF-PED-06 | Falha de pagamento ou de estoque cancela o pedido e libera o estoque reservado (compensação) | M | Estoque volta ao valor anterior |
| RF-PED-07 | A entrega é simulada e avança `PAID → SHIPPED → COMPLETED` | M | Atrasos configuráveis |
| RF-PED-08 | O cliente consulta seus pedidos (lista paginada e detalhe) | M | Cliente não acessa pedido de outro (404) |
| RF-PED-09 | O detalhe mostra o histórico de status com data e motivo | M | Baseado em `order_status_history` |oridade | Critério de aceite                                                                                   |
| RF-PED-10 | O admin lista todos os pedidos com filtros | S | Filtros por status e período |------------- | ---------- | ---------------------------------------------------------------------------------------------------- |
| RF-PED-11 | Pedidos parados são cancelados automaticamente | M | `PLACED` ou `STOCK_RESERVED` por mais de 5 min viram `CANCELLED` (`TIMEOUT`) |`. Carrinho é esvaziado na mesma transação                                    |
| RF-PED-12 | Falha temporária do Kafka não perde pedidos | M | Evento permanece `PENDING` e é publicado quando o Kafka volta |m valor monetário do cliente é aceito                                                           |
| RF-PED-13 | Reentrega de mensagem não duplica efeitos | M | Mesmo `eventId` processado uma única vez |   | M          | Alterar o preço depois não muda pedidos existentes                                                   |
| RF-PED-04 | O estoque é reservado de forma atômica e tudo ou nada                                        | M          | 10 compras simultâneas da última unidade resultam em 1 aprovada e 9 rejeitadas, sem estoque negativo |
### 3.5 Notificações e integrações (notification-service)ável (`APPROVE`, `REJECT`)                        | M          | Aprovado leva a `PAID`. Rejeitado leva a `CANCELLED`                                                 |
| RF-PED-06 | Falha de pagamento ou de estoque cancela o pedido e libera o estoque reservado (compensação) | M          | Estoque volta ao valor anterior                                                                      |
| ID | Requisito | Prioridade | Critério de aceite |SHIPPED → COMPLETED`                                   | M          | Atrasos configuráveis                                                                                |
| --- | --- | --- | --- |onsulta seus pedidos (lista paginada e detalhe)                                   | M          | Cliente não acessa pedido de outro (404)                                                             |
| RF-NOT-01 | O cliente recebe e-mail em pedido confirmado, enviado, entregue e cancelado | M | Visível no MailHog. Registrado em `email_log` |tus_history`                                                                    |
| RF-NOT-02 | Ao concluir o pedido, o sistema envia um webhook JSON para as URLs configuradas | M | Evento `order.completed` entregue ao endpoint inscrito |                                                                   |
| RF-NOT-03 | O webhook é assinado, tem retries com backoff e histórico | M | Header `X-ShopFlow-Signature` (HMAC-SHA256). 5 tentativas. Estado `DEAD` após esgotar |min viram `CANCELLED` (`TIMEOUT`)                         |
| RF-NOT-04 | O admin gerencia endpoints de webhook e reenvia entregas | S | CRUD, listagem de entregas e reenvio manual |Evento permanece `PENDING` e é publicado quando o Kafka volta                                        |
| RF-PED-13 | Reentrega de mensagem não duplica efeitos                                                    | M          | Mesmo `eventId` processado uma única vez                                                             |
### 3.6 Frontend
### 3.5 Notificações e integrações (notification-service)
| ID | Requisito | Prioridade |
| --- | --- | --- |sito                                                                       | Prioridade | Critério de aceite                                                                    |
| RF-FRO-01 | Loja: listagem com filtros, detalhe do produto, carrinho, checkout (com seletor do cenário de pagamento para demonstração) | M |---------------------------------------------------- |
| RF-FRO-02 | Loja: página de pedidos com linha do tempo de status atualizada por polling | M | M          | Visível no MailHog. Registrado em `email_log`                                         |
| RF-FRO-03 | Cadastro, login e renovação silenciosa de sessão | M |para as URLs configuradas | M          | Evento `order.completed` entregue ao endpoint inscrito                                |
| RF-FRO-04 | Painel admin: produtos, categorias, estoque, pedidos | M |                      | M          | Header `X-ShopFlow-Signature` (HMAC-SHA256). 5 tentativas. Estado `DEAD` após esgotar |
| RF-FRO-05 | Painel admin: visualizador de auditoria e gestão de webhooks | S |              | S          | CRUD, listagem de entregas e reenvio manual                                           |
| RF-FRO-06 | Layout responsivo e acessível (navegação por teclado, contraste) | S |
### 3.6 Frontend
### 3.7 Plataforma
| ID        | Requisito                                                                                                                  | Prioridade |
| ID | Requisito | Prioridade | Critério de aceite |------------------------------------------------------------------------------------ | ---------- |
| --- | --- | --- | --- |gem com filtros, detalhe do produto, carrinho, checkout (com seletor do cenário de pagamento para demonstração) | M          |
| RF-PLT-01 | Ponto único de entrada via API Gateway | M | Cliente só conhece `/api/**` |                                                | M          |
| RF-PLT-02 | Ambiente completo com `docker compose up` | M | Todos os serviços saudáveis sem passos manuais |                           | M          |
| RF-PLT-03 | Seeds para demonstração | M | Usuários admin e cliente, categorias, produtos e estoque |                                   | M          |
| RF-PLT-04 | Documentação OpenAPI por serviço | S | Swagger UI acessível |                                                              | S          |
| RF-PLT-05 | Inspeção de mensageria e e-mails em ferramentas de desenvolvimento | S | Kafka UI, MailHog e WireMock |                    | S          |
| RF-PLT-06 | Health checks | M | `/actuator/health` (liveness e readiness) em todos os serviços |
### 3.7 Plataforma
---
| ID        | Requisito                                                          | Prioridade | Critério de aceite                                             |
## 4. Regras de negócio--------------------------------------------------------- | ---------- | -------------------------------------------------------------- |
| RF-PLT-01 | Ponto único de entrada via API Gateway                             | M          | Cliente só conhece `/api/**`                                   |
| ID | Regra |Ambiente completo com `docker compose up`                          | M          | Todos os serviços saudáveis sem passos manuais                 |
| --- | --- | Seeds para demonstração                                            | M          | Usuários admin e cliente, categorias, produtos e estoque       |
| RN-01 | O preço de venda é sempre o do catálogo no momento do checkout. O cliente nunca informa valores |acessível                                           |
| RN-02 | O estoque disponível nunca é negativo | ferramentas de desenvolvimento | S          | Kafka UI, MailHog e WireMock                                   |
| RN-03 | Uma reserva de estoque cobre todos os itens do pedido ou nenhum |      | M          | `/actuator/health` (liveness e readiness) em todos os serviços |
| RN-04 | Estados finais do pedido (`COMPLETED`, `CANCELLED`) são imutáveis |
| RN-05 | Cliente só acessa os próprios pedidos e carrinho. Admin acessa todos |
| RN-06 | Produto inativo não pode ser comprado, mas pedidos antigos que o contêm permanecem intactos |
| RN-07 | Todo pedido cancelado após reserva devolve o estoque exatamente uma vez |
| RN-08 | Toda alteração de preço, status ou estoque de produto gera registro de auditoria na mesma transação |
| RN-09 | Um e-mail é único por usuário (sem diferenciar maiúsculas de minúsculas) |                          |
| RN-10 | Quantidade por item vai de 1 a 99 |---------------------------------------------------------------- |
| RN-01 | O preço de venda é sempre o do catálogo no momento do checkout. O cliente nunca informa valores     |
---N-02 | O estoque disponível nunca é negativo                                                               |
| RN-03 | Uma reserva de estoque cobre todos os itens do pedido ou nenhum                                     |
## 5. Requisitos não funcionaisido (`COMPLETED`, `CANCELLED`) são imutáveis                                   |
| RN-05 | Cliente só acessa os próprios pedidos e carrinho. Admin acessa todos                                |
Valores de desempenho referem-se ao ambiente local em Docker Compose, com carga leve, e servem como meta de projeto.
| RN-07 | Todo pedido cancelado após reserva devolve o estoque exatamente uma vez                             |
| ID | Categoria | Requisito | Verificação | Status |de produto gera registro de auditoria na mesma transação |
| --- | --- | --- | --- | --- | usuário (sem diferenciar maiúsculas de minúsculas)                            |
| RNF-01 | Consistência | Estoque nunca negativo nem vendido além do disponível sob concorrência | Teste de concorrência com Testcontainers | 🟢 |
| RNF-02 | Consistência | Nenhum evento perdido após commit (sem dual write) | Teste com Kafka indisponível | 🟢 |
| RNF-03 | Consistência | Convergência eventual do pedido em até 5 s no fluxo normal | Teste E2E com Awaitility | 🟢 |
| RNF-04 | Confiabilidade | Consumidores idempotentes com semântica at-least-once | Teste de reentrega | 🟢 |
| RNF-05 | Confiabilidade | Mensagens não processáveis vão para DLT sem bloquear a partição | Teste de mensagem inválida | 🟢 |
| RNF-06 | Disponibilidade | O checkout continua aceitando pedidos com Kafka fora do ar | Teste de caos simples | 🟢 |
| RNF-07 | Disponibilidade | Falha do Redis degrada desempenho, não funcionalidade | Teste com Redis parado | 🟢 |o.
| RNF-08 | Desempenho | Listagem de catálogo com cache hit: p95 menor que 100 ms | Teste de carga leve (k6) | 🟢 |
| RNF-09 | Desempenho | Resposta do checkout (`202`): p95 menor que 500 ms | Teste de carga leve | 🟢 |                                                                           | Verificação                              | Status |
| RNF-10 | Escalabilidade | Serviços sem estado, escaláveis horizontalmente. Múltiplas instâncias do relay e dos consumidores sem duplicação | Teste com 2 réplicas | 🟢 |------- | ---------------------------------------- | ------ |
| RNF-11 | Escalabilidade | Particionamento por `orderId` (3 partições) para paralelismo e ordem por pedido | Configuração revisada | 🟢 |                                        | Teste de concorrência com Testcontainers | 🟢     |
| RNF-12 | Segurança | Senhas com Argon2id. JWT RS256 com expiração curta. Refresh com rotação | Testes unitários e de integração | 🟢 |                                          | Teste com Kafka indisponível             | 🟢     |
| RNF-13 | Segurança | Proteção contra IDOR e escalada de privilégio | Testes de autorização por endpoint | 🟢 |                                                                  | Teste E2E com Awaitility                 | 🟢     |
| RNF-14 | Segurança | Validação de entrada. Erros no padrão RFC 9457 sem stack trace | Testes de contrato | 🟢 |                                                                 | Teste de reentrega                       | 🟢     |
| RNF-15 | Segurança | Segredos por variável de ambiente, nenhum segredo no repositório. Dependências verificadas no CI | Revisão de CI | 🟢 |                                    | Teste de mensagem inválida               | 🟢     |
| RNF-16 | Segurança | Assinatura HMAC e proteção anti-replay em webhooks. URLs de destino não podem apontar para IPs privados (anti-SSRF) fora do modo de desenvolvimento | Testes do dispatcher | 🟢 |s                    | 🟢     |
| RNF-17 | Privacidade | Logs sem dados pessoais em claro (e-mail mascarado). Payloads de eventos com o mínimo necessário | Revisão de código e logs | 🟢 |                       | Teste com Redis parado                   | 🟢     |
| RNF-18 | Manutenibilidade | Cobertura de linhas do domínio de pelo menos 80%. Regras de arquitetura verificadas por ArchUnit | Gate no CI | 🟢 |                                | Teste de carga leve (k6)                 | 🟢     |
| RNF-19 | Testabilidade | Testes de integração com infraestrutura real (Testcontainers) | CI | 🟢 |                                                                              | Teste de carga leve                      | 🟢     |
| RNF-20 | Portabilidade | Execução local com um comando, sem dependência do host além de Docker | README | 🟢 |os consumidores sem duplicação                                    | Teste com 2 réplicas                     | 🟢     |
| RNF-21 | Documentação | Toda decisão relevante em ADR. Todo fluxo relevante em diagrama | Revisão de docs | 🟢 |                                                                | Configuração revisada                    | 🟢     |
| RNF-22 | Observabilidade | Logs estruturados, métricas, traces e alertas | Ver seção 6 | 🔵 |otação                                                                             | Testes unitários e de integração         | 🟢     |
| RNF-13 | Segurança        | Proteção contra IDOR e escalada de privilégio                                                                                                       | Testes de autorização por endpoint       | 🟢     |
```mermaid Segurança        | Validação de entrada. Erros no padrão RFC 9457 sem stack trace                                                                                      | Testes de contrato                       | 🟢     |
flowchart LRegurança        | Segredos por variável de ambiente, nenhum segredo no repositório. Dependências verificadas no CI                                                    | Revisão de CI                            | 🟢     |
    Q["Qualidades priorizadas"] --> C["Consistência<br/>RNF-01 a 04"]em webhooks. URLs de destino não podem apontar para IPs privados (anti-SSRF) fora do modo de desenvolvimento | Testes do dispatcher                     | 🟢     |
    Q --> R["Resiliência<br/>RNF-05 a 07"]os pessoais em claro (e-mail mascarado). Payloads de eventos com o mínimo necessário                                                    | Revisão de código e logs                 | 🟢     |
    Q --> P["Desempenho e escala<br/>RNF-08 a 11"]do domínio de pelo menos 80%. Regras de arquitetura verificadas por ArchUnit                                                    | Gate no CI                               | 🟢     |
    Q --> S["Segurança e privacidade<br/>RNF-12 a 17"] infraestrutura real (Testcontainers)                                                                                       | CI                                       | 🟢     |
    Q --> M["Manutenção e testes<br/>RNF-18 a 21"]m comando, sem dependência do host além de Docker                                                                               | README                                   | 🟢     |
| RNF-21 | Documentação     | Toda decisão relevante em ADR. Todo fluxo relevante em diagrama                                                                                     | Revisão de docs                          | 🟢     |
    C --> T1["Outbox, UPDATE atômico, idempotência"]ricas, traces e alertas                                                                                                       | Ver seção 6                              | 🔵     |
    R --> T2["Retry, DLT, degradação graciosa"]
    P --> T3["Cache, partições, serviços stateless"]
    S --> T4["Argon2id, RS256, HMAC, ownership"]
    M --> T5["Testcontainers, ArchUnit, ADRs"]ência<br/>RNF-01 a 04"]
``` Q --> R["Resiliência<br/>RNF-05 a 07"]
    Q --> P["Desempenho e escala<br/>RNF-08 a 11"]
--- Q --> S["Segurança e privacidade<br/>RNF-12 a 17"]
    Q --> M["Manutenção e testes<br/>RNF-18 a 21"]
## 6. Requisitos planejados, sem código
    C --> T1["Outbox, UPDATE atômico, idempotência"]
Documentados com diagrama em [ARCHITECTURE.md, seção 11](ARCHITECTURE.md#11-planejado-sem-código).
    P --> T3["Cache, partições, serviços stateless"]
| ID | Requisito planejado 🔵 | Objetivo | Encaixe na arquitetura |
| --- | --- | --- | --- |ers, ArchUnit, ADRs"]
| RP-01 | Observabilidade: logs JSON, métricas Prometheus, traces OpenTelemetry, dashboards Grafana e alertas | Diagnosticar e operar em produção | Collector OTLP e propagação de `traceparent` no envelope de eventos |
| RP-02 | Rate limiting por rota, IP e usuário | Conter abuso e força bruta | Filtro do gateway com token bucket no Redis |
| RP-03 | Blacklist temporária de IPs | Bloquear reincidentes | Filtro do gateway consulta o Redis antes de rotear |
| RP-04 | Blind Login (sem enumeração de usuários, tempo constante) | Impedir descoberta de contas | identity-service |
| RP-05 | 2FA por TOTP com códigos de recuperação | Reduzir tomada de conta | identity-service e fluxo de login em duas etapas |
| RP-06 | Idempotency-Key no checkout e no pagamento | Evitar pedido ou cobrança duplicados em retry do cliente | Tabela `idempotency_keys` no PostgreSQL, unicidade `(user_id, key)` |
| RP-07 | Circuit Breaker, Bulkhead e retry HTTP | Isolar falhas do catálogo no checkout | Resilience4j na chamada de cotação |
| RP-08 | BFF consumidor de webhooks | Agregar dados por canal (mobile) | Consumidor externo do webhook `order.completed` |
| RP-09 | Schema Registry e CDC com Debezium | Contratos formais e projeções de leitura | Kafka Connect, PostgreSQL logical replication e indexador |                                 | Encaixe na arquitetura                                              |
| RP-10 | mTLS entre serviços | Autenticação de serviço a serviço | Substitui o `X-Internal-Token` |--------- | -------------------------------------------------------- | ------------------------------------------------------------------- |
| RP-11 | OpenSearch para busca inteligente | Fuzzy search, autocomplete e relevância | Índice assíncrono; PostgreSQL continua como fonte de verdade || RP-01 | Observabilidade: logs JSON, métricas Prometheus, traces OpenTelemetry, dashboards Grafana e alertas | Diagnosticar e operar em produção                        | Collector OTLP e propagação de `traceparent` no envelope de eventos |
| RP-12 | Observabilidade distribuída | Diagnóstico de falhas e análise de desempenho | OpenTelemetry, Prometheus, Grafana, Loki e Tempo |P-02 | Rate limiting por rota, IP e usuário                                                                | Conter abuso e força bruta                               | Filtro do gateway com token bucket no Redis                         |
| RP-03 | Blacklist temporária de IPs                                                                         | Bloquear reincidentes                                    | Filtro do gateway consulta o Redis antes de rotear                  |
| RP-04 | Blind Login (sem enumeração de usuários, tempo constante)                                           | Impedir descoberta de contas                             | identity-service                                                    |
| RP-05 | 2FA por TOTP com códigos de recuperação                                                             | Reduzir tomada de conta                                  | identity-service e fluxo de login em duas etapas                    |
| RP-06 | Idempotency-Key no checkout e no pagamento | Evitar pedido ou cobrança duplicados em retry do cliente | Tabela `idempotency_keys` no PostgreSQL, unicidade `(user_id, key)` |
| RP-07 | Circuit Breaker, Bulkhead e retry HTTP | Isolar falhas do catálogo no checkout | Resilience4j na chamada de cotação |
| RP-08 | BFF consumidor de webhooks | Agregar dados por canal (mobile) | Consumidor externo do webhook `order.completed` |
| RP-09 | Schema Registry e CDC com Debezium | Contratos formais e projeções de leitura | Kafka Connect, PostgreSQL logical replication e indexador |                                 | Encaixe na arquitetura                                              |
| RP-10 | mTLS entre serviços | Autenticação de serviço a serviço | Substitui o `X-Internal-Token` |--------- | -------------------------------------------------------- | ------------------------------------------------------------------- |
| RP-11 | OpenSearch para busca inteligente | Fuzzy search, autocomplete e relevância | Índice assíncrono; PostgreSQL continua como fonte de verdade || RP-01 | Observabilidade: logs JSON, métricas Prometheus, traces OpenTelemetry, dashboards Grafana e alertas | Diagnosticar e operar em produção                        | Collector OTLP e propagação de `traceparent` no envelope de eventos |
| RP-12 | Observabilidade distribuída | Diagnóstico de falhas e análise de desempenho | OpenTelemetry, Prometheus, Grafana, Loki e Tempo |P-02 | Rate limiting por rota, IP e usuário                                                                | Conter abuso e força bruta                               | Filtro do gateway com token bucket no Redis                         |
| RP-03 | Blacklist temporária de IPs                                                                         | Bloquear reincidentes                                    | Filtro do gateway consulta o Redis antes de rotear                  |
| RP-04 | Blind Login (sem enumeração de usuários, tempo constante)                                           | Impedir descoberta de contas                             | identity-service                                                    |
| RP-05 | 2FA por TOTP com códigos de recuperação                                                             | Reduzir tomada de conta                                  | identity-service e fluxo de login em duas etapas                    |
| RP-06 | Idempotency-Key no checkout e no pagamento | Evitar pedido ou cobrança duplicados em retry do cliente | Tabela `idempotency_keys` no PostgreSQL, unicidade `(user_id, key)` |
| RP-07 | Circuit Breaker, Bulkhead e retry HTTP | Isolar falhas do catálogo no checkout | Resilience4j na chamada de cotação |
| RP-08 | BFF consumidor de webhooks | Agregar dados por canal (mobile) | Consumidor externo do webhook `order.completed` |
| RP-09 | Schema Registry e CDC com Debezium | Contratos formais e projeções de leitura | Kafka Connect, PostgreSQL logical replication e indexador |                                 | Encaixe na arquitetura                                              |
| RP-10 | mTLS entre serviços | Autenticação de serviço a serviço | Substitui o `X-Internal-Token` |--------- | -------------------------------------------------------- | ------------------------------------------------------------------- |
| RP-11 | OpenSearch para busca inteligente | Fuzzy search, autocomplete e relevância | Índice assíncrono; PostgreSQL continua como fonte de verdade || RP-01 | Observabilidade: logs JSON, métricas Prometheus, traces OpenTelemetry, dashboards Grafana e alertas | Diagnosticar e operar em produção                        | Collector OTLP e propagação de `traceparent` no envelope de eventos |
| RP-12 | Observabilidade distribuída | Diagnóstico de falhas e análise de desempenho | OpenTelemetry, Prometheus, Grafana, Loki e Tempo |P-02 | Rate limiting por rota, IP e usuário                                                                | Conter abuso e força bruta                               | Filtro do gateway com token bucket no Redis                         |
| RP-03 | Blacklist temporária de IPs                                                                         | Bloquear reincidentes                                    | Filtro do gateway consulta o Redis antes de rotear                  |
| RP-04 | Blind Login (sem enumeração de usuários, tempo constante)                                           | Impedir descoberta de contas                             | identity-service                                                    |
| RP-05 | 2FA por TOTP com códigos de recuperação                                                             | Reduzir tomada de conta                                  | identity-service e fluxo de login em duas etapas                    |
| RP-06 | Idempotency-Key no checkout e no pagamento | Evitar pedido ou cobrança duplicados em retry do cliente | Tabela `idempotency_keys` no PostgreSQL, unicidade `(user_id, key)` |
| RP-07 | Circuit Breaker, Bulkhead e retry HTTP | Isolar falhas do catálogo no checkout | Resilience4j na chamada de cotação |
| RP-08 | BFF consumidor de webhooks | Agregar dados por canal (mobile) | Consumidor externo do webhook `order.completed` |
| RP-09 | Schema Registry e CDC com Debezium | Contratos formais e projeções de leitura | Kafka Connect, PostgreSQL logical replication e indexador |                                 | Encaixe na arquitetura                                              |
| RP-10 | mTLS entre serviços | Autenticação de serviço a serviço | Substitui o `X-Internal-Token` |--------- | -------------------------------------------------------- | ------------------------------------------------------------------- |
| RP-11 | OpenSearch para busca inteligente | Fuzzy search, autocomplete e relevância | Índice assíncrono; PostgreSQL continua como fonte de verdade || RP-01 | Observabilidade: logs JSON, métricas Prometheus, traces OpenTelemetry, dashboards Grafana e alertas | Diagnosticar e operar em produção                        | Collector OTLP e propagação de `traceparent` no envelope de eventos |
| RP-12 | Observabilidade distribuída | Diagnóstico de falhas e análise de desempenho | OpenTelemetry, Prometheus, Grafana, Loki e Tempo |P-02 | Rate limiting por rota, IP e usuário                                                                | Conter abuso e força bruta                               | Filtro do gateway com token bucket no Redis                         |
| RP-03 | Blacklist temporária de IPs                                                                         | Bloquear reincidentes                                    | Filtro do gateway consulta o Redis antes de rotear                  |
| RP-04 | Blind Login (sem enumeração de usuários, tempo constante)                                           | Impedir descoberta de contas                             | identity-service                                                    |
| RP-05 | 2FA por TOTP com códigos de recuperação                                                             | Reduzir tomada de conta                                  | identity-service e fluxo de login em duas etapas                    |
| RP-06 | Idempotency-Key no checkout e no pagamento | Evitar pedido ou cobrança duplicados em retry do cliente | Tabela `idempotency_keys` no PostgreSQL, unicidade `(user_id, key)` |
| RP-07 | Circuit Breaker, Bulkhead e retry HTTP | Isolar falhas do catálogo no checkout | Resilience4j na chamada de cotação |
| RP-08 | BFF consumidor de webhooks | Agregar dados por canal (mobile) | Consumidor externo do webhook `order.completed` |
| RP-09 | Schema Registry e CDC com Debezium | Contratos formais e projeções de leitura | Kafka Connect, PostgreSQL logical replication e indexador |                                 | Encaixe na arquitetura                                              |
| RP-10 | mTLS entre serviços | Autenticação de serviço a serviço | Substitui o `X-Internal-Token` |--------- | -------------------------------------------------------- | ------------------------------------------------------------------- |
| RP-11 | OpenSearch para busca inteligente | Fuzzy search, autocomplete e relevância | Índice assíncrono; PostgreSQL continua como fonte de verdade || RP-01 | Observabilidade: logs JSON, métricas Prometheus, traces OpenTelemetry, dashboards Grafana e alertas | Diagnosticar e operar em produção                        | Collector OTLP e propagação de `traceparent` no envelope de eventos |
| RP-12 | Observabilidade distribuída | Diagnóstico de falhas e análise de desempenho | OpenTelemetry, Prometheus, Grafana, Loki e Tempo |P-02 | Rate limiting por rota, IP e usuário                                                                | Conter abuso e força bruta                               | Filtro do gateway com token bucket no Redis                         |
| RP-03 | Blacklist temporária de IPs                                                                         | Bloquear reincidentes                                    | Filtro do gateway consulta o Redis antes de rotear                  |
| RP-04 | Blind Login (sem enumeração de usuários, tempo constante)                                           | Impedir descoberta de contas                             | identity-service                                                    |
| RP-05 | 2FA por TOTP com códigos de recuperação                                                             | Reduzir tomada de conta                                  | identity-service e fluxo de login em duas etapas                    |
| RP-06 | Idempotency-Key no checkout e no pagamento | Evitar pedido ou cobrança duplicados em retry do cliente | Tabela `idempotency_keys` no PostgreSQL, unicidade `(user_id, key)` |
| RP-07 | Circuit Breaker, Bulkhead e retry HTTP | Isolar falhas do catálogo no checkout | Resilience4j na chamada de cotação |
| RP-08 | BFF consumidor de webhooks | Agregar dados por canal (mobile) | Consumidor externo do webhook `order.completed` |
| RP-09 | Schema Registry e CDC com Debezium | Contratos formais e projeções de leitura | Kafka Connect, PostgreSQL logical replication e indexador |                                 | Encaixe na arquitetura                                              |
| RP-10 | mTLS entre serviços | Autenticação de serviço a serviço | Substitui o `X-Internal-Token` |--------- | -------------------------------------------------------- | ------------------------------------------------------------------- |
| RP-11 | OpenSearch para busca inteligente | Fuzzy search, autocomplete e relevância | Índice assíncrono; PostgreSQL continua como fonte de verdade || RP-01 | Observabilidade: logs JSON, métricas Prometheus, traces OpenTelemetry, dashboards Grafana e alertas | Diagnosticar e operar em produção                        | Collector OTLP e propagação de `traceparent` no envelope de eventos |
| RP-12 | Observabilidade distribuída | Diagnóstico de falhas e análise de desempenho | OpenTelemetry, Prometheus, Grafana, Loki e Tempo |P-02 | Rate limiting por rota, IP e usuário                                                                | Conter abuso e força bruta                               | Filtro do gateway com token bucket no Redis                         |
| RP-03 | Blacklist temporária de IPs                                                                         | Bloquear reincidentes                                    | Filtro do gateway consulta o Redis antes de rotear                  |
| RP-04 | Blind Login (sem enumeração de usuários, tempo constante)                                           | Impedir descoberta de contas                             | identity-service                                                    |
| RP-05 | 2FA por TOTP com códigos de recuperação                                                             | Reduzir tomada de conta                                  | identity-service e fluxo de login em duas etapas                    |
| RP-06 | Idempotency-Key no checkout e no pagamento | Evitar pedido ou cobrança duplicados em retry do cliente | Tabela `idempotency_keys` no PostgreSQL, unicidade `(user_id, key)` |
| RP-07 | Circuit Breaker, Bulkhead e retry HTTP | Isolar falhas do catálogo no checkout | Resilience4j na chamada de cotação |
| RP-08 | BFF consumidor de webhooks | Agregar dados por canal (mobile) | Consumidor externo do webhook `order.completed` |
| RP-09 | Schema Registry e CDC com Debezium | Contratos formais e projeções de leitura | Kafka Connect, PostgreSQL logical replication e indexador |                                 | Encaixe na arquitetura                                              |
| RP-10 | mTLS entre serviços | Autenticação de serviço a serviço | Substitui o `X-Internal-Token` |--------- | -------------------------------------------------------- | ------------------------------------------------------------------- |
| RP-11 | OpenSearch para busca inteligente | Fuzzy search, autocomplete e relevância | Índice assíncrono; PostgreSQL continua como fonte de verdade || RP-01 | Observabilidade: logs JSON, métricas Prometheus, traces OpenTelemetry, dashboards Grafana e alertas | Diagnosticar e operar em produção                        | Collector OTLP e propagação de `traceparent` no envelope de eventos |
| RP-12 | Observabilidade distribuída | Diagnóstico de falhas e análise de desempenho | OpenTelemetry, Prometheus, Grafana, Loki e Tempo |P-02 | Rate limiting por rota, IP e usuário                                                                | Conter abuso e força bruta                               | Filtro do gateway com token bucket no Redis                         |
| RP-03 | Blacklist temporária de IPs                                                                         | Bloquear reincidentes                                    | Filtro do gateway consulta o Redis antes de rotear                  |
| RP-04 | Blind Login (sem enumeração de usuários, tempo constante)                                           | Impedir descoberta de contas                             | identity-service                                                    |
| RP-05 | 2FA por TOTP com códigos de recuperação                                                             | Reduzir tomada de conta                                  | identity-service e fluxo de login em duas etapas                    |
| RP-06 | Idempotency-Key no checkout e no pagamento | Evitar pedido ou cobrança duplicados em retry do cliente | Tabela `idempotency_keys` no PostgreSQL, unicidade `(user_id, key)` |
| RP-07 | Circuit Breaker, Bulkhead e retry HTTP | Isolar falhas do catálogo no checkout | Resilience4j na chamada de cotação |
| RP-08 | BFF consumidor de webhooks | Agregar dados por canal (mobile) | Consumidor externo do webhook `order.completed` |
| RP-09 | Schema Registry e CDC com Debezium | Contratos formais e projeções de leitura | Kafka Connect, PostgreSQL logical replication e indexador |                                 | Encaixe na arquitetura                                              |
| RP-10 | mTLS entre serviços | Autenticação de serviço a serviço | Substitui o `X-Internal-Token` |--------- | -------------------------------------------------------- | ------------------------------------------------------------------- |
| RP-11 | OpenSearch para busca inteligente | Fuzzy search, autocomplete e relevância | Índice assíncrono; PostgreSQL continua como fonte de verdade || RP-01 | Observabilidade: logs JSON, métricas Prometheus, traces OpenTelemetry, dashboards Grafana e alertas | Diagnosticar e operar em produção                        | Collector OTLP e propagação de `traceparent` no envelope de eventos |
| RP-12 | Observabilidade distribuída | Diagnóstico de falhas e análise de desempenho | OpenTelemetry, Prometheus, Grafana, Loki e Tempo |P-02 | Rate limiting por rota, IP e usuário                                                                | Conter abuso e força bruta                               | Filtro do gateway com token bucket no Redis                         |
| RP-03 | Blacklist temporária de IPs                                                                         | Bloquear reincidentes                                    | Filtro do gateway consulta o Redis antes de rotear                  |
| RP-04 | Blind Login (sem enumeração de usuários, tempo constante)                                           | Impedir descoberta de contas                             | identity-service                                                    |
| RP-05 | 2FA por TOTP com códigos de recuperação                                                             | Reduzir tomada de conta                                  | identity-service e fluxo de login em duas etapas                    |
| RP-06 | Idempotency-Key no checkout e no pagamento | Evitar pedido ou cobrança duplicados em retry do cliente | Tabela `idempotency_keys` no PostgreSQL, unicidade `(user_id, key)` |
| RP-07 | Circuit Breaker, Bulkhead e retry HTTP | Isolar falhas do catálogo no checkout | Resilience4j na chamada de cotação |
| RP-08 | BFF consumidor de webhooks | Agregar dados por canal (mobile) | Consumidor externo do webhook `order.completed` |
| RP-09 | Schema Registry e CDC com Debezium | Contratos formais e projeções de leitura | Kafka Connect, PostgreSQL logical replication e indexador |                                 | Encaixe na arquitetura                                              |
| RP-10 | mTLS entre serviços | Autenticação de serviço a serviço | Substitui o `X-Internal-Token` |--------- | -------------------------------------------------------- | ------------------------------------------------------------------- |
| RP-11 | OpenSearch para busca inteligente | Fuzzy search, autocomplete e relevância | Índice assíncrono; PostgreSQL continua como fonte de verdade || RP-01 | Observabilidade: logs JSON, métricas Prometheus, traces OpenTelemetry, dashboards Grafana e alertas | Diagnosticar e operar em produção                        | Collector OTLP e propagação de `traceparent` no envelope de eventos |
| RP-12 | Observabilidade distribuída | Diagnóstico de falhas e análise de desempenho | OpenTelemetry, Prometheus, Grafana, Loki e Tempo |P-02 | Rate limiting por rota, IP e usuário                                                                | Conter abuso e força bruta                               | Filtro do gateway com token bucket no Redis                         |
| RP-03 | Blacklist temporária de IPs                                                                         | Bloquear reincidentes                                    | Filtro do gateway consulta o Redis antes de rotear                  |
| RP-04 | Blind Login (sem enumeração de usuários, tempo constante)                                           | Impedir descoberta de contas                             | identity-service                                                    |
| RP-05 | 2FA por TOTP com códigos de recuperação                                                             | Reduzir tomada de conta                                  | identity-service e fluxo de login em duas etapas                    |
| RP-06 | Idempotency-Key no checkout e no pagamento | Evitar pedido ou cobrança duplicados em retry do cliente | Tabela `idempotency_keys` no PostgreSQL, unicidade `(user_id, key)` |
| RP-07 | Circuit Breaker, Bulkhead e retry HTTP | Isolar falhas do catálogo no checkout | Resilience4j na chamada de cotação |
| RP-08 | BFF consumidor de webhooks | Agregar dados por canal (mobile) | Consumidor externo do webhook `order.completed` |
| RP-09 | Schema Registry e CDC com Debezium | Contratos formais e projeções de leitura | Kafka Connect, PostgreSQL logical replication e indexador |                                 | Encaixe na arquitetura                                              |
| RP-10 | mTLS entre serviços | Autenticação de serviço a serviço | Substitui o `X-Internal-Token` |--------- | -------------------------------------------------------- | ------------------------------------------------------------------- |
| RP-11 | OpenSearch para busca inteligente | Fuzzy search, autocomplete e relevância | Índice assíncrono; PostgreSQL continua como fonte de verdade || RP-01 | Observabilidade: logs JSON, métricas Prometheus, traces OpenTelemetry, dashboards Grafana e alertas | Diagnosticar e operar em produção                        | Collector OTLP e propagação de `traceparent` no envelope de eventos |
| RP-12 | Observabilidade distribuída | Diagnóstico de falhas e análise de desempenho | OpenTelemetry, Prometheus, Grafana, Loki e Tempo |P-02 | Rate limiting por rota, IP e usuário                                                                | Conter abuso e força bruta                               | Filtro do gateway com token bucket no Redis                         |
| RP-03 | Blacklist temporária de IPs                                                                         | Bloquear reincidentes                                    | Filtro do gateway consulta o Redis antes de rotear                  |
| RP-04 | Blind Login (sem enumeração de usuários, tempo constante)                                           | Impedir descoberta de contas                             | identity-service                                                    |
| RP-05 | 2FA por TOTP com códigos de recuperação                                                             | Reduzir tomada de conta                                  | identity-service e fluxo de login em duas etapas                    |
| RP-06 | Idempotency-Key no checkout e no pagamento | Evitar pedido ou cobrança duplicados em retry do cliente | Tabela `idempotency_keys` no PostgreSQL, unicidade `(user_id, key)` |
| RP-07 | Circuit Breaker, Bulkhead e retry HTTP | Isolar falhas do catálogo no checkout | Resilience4j na chamada de cotação |
| RP-08 | BFF consumidor de webhooks | Agregar dados por canal (mobile) | Consumidor externo do webhook `order.completed` |
| RP-09 | Schema Registry e CDC com Debezium | Contratos formais e projeções de leitura | Kafka Connect, PostgreSQL logical replication e indexador |                                 | Encaixe na arquitetura                                              |
| RP-10 | mTLS entre serviços | Autenticação de serviço a serviço | Substitui o `X-Internal-Token` |--------- | -------------------------------------------------------- | ------------------------------------------------------------------- |
| RP-11 | OpenSearch para busca inteligente | Fuzzy search, autocomplete e relevância | Índice assíncrono; PostgreSQL continua como fonte de verdade || RP-01 | Observabilidade: logs JSON, métricas Prometheus, traces OpenTelemetry, dashboards Grafana e alertas | Diagnosticar e operar em produção                        | Collector OTLP e propagação de `traceparent` no envelope de eventos |
| RP-12 | Observabilidade distribuída | Diagnóstico de falhas e análise de desempenho | OpenTelemetry, Prometheus, Grafana, Loki e Tempo |P-02 | Rate limiting por rota, IP e usuário                                                                | Conter abuso e força bruta                               | Filtro do gateway com token bucket no Redis                         |
| RP-03 | Blacklist temporária de IPs                                                                         | Bloquear reincidentes                                    | Filtro do gateway consulta o Redis antes de rotear                  |
| RP-04 | Blind Login (sem enumeração de usuários, tempo constante)                                           | Impedir descoberta de contas                             | identity-service                                                    |
| RP-05 | 2FA por TOTP com códigos de recuperação                                                             | Reduzir tomada de conta                                  | identity-service e fluxo de login em duas etapas                    |
| RP-06 | Idempotency-Key no checkout e no pagamento | Evitar pedido ou cobrança duplicados em retry do cliente | Tabela `idempotency_keys` no PostgreSQL, unicidade `(user_id, key)` |
| RP-07 | Circuit Breaker, Bulkhead e retry HTTP | Isolar falhas do catálogo no checkout | Resilience4j na chamada de cotação |
| RP-08 | BFF consumidor de webhooks | Agregar dados por canal (mobile) | Consumidor externo do webhook `order.completed` |
| RP-09 | Schema Registry e CDC com Debezium | Contratos formais e projeções de leitura | Kafka Connect, PostgreSQL logical replication e indexador |                                 | Encaixe na arquitetura                                              |
| RP-10 | mTLS entre serviços | Autenticação de serviço a serviço | Substitui o `X-Internal-Token` |--------- | -------------------------------------------------------- | ------------------------------------------------------------------- |
| RP-11 | OpenSearch para busca inteligente | Fuzzy search, autocomplete e relevância | Índice assíncrono; PostgreSQL continua como fonte de verdade || RP-01 | Observabilidade: logs JSON, métricas Prometheus, traces OpenTelemetry, dashboards Grafana e alertas | Diagnosticar e operar em produção                        | Collector OTLP e propagação de `traceparent` no envelope de eventos |
| RP-12 | Observabilidade distribuída | Diagnóstico de falhas e análise de desempenho | OpenTelemetry, Prometheus, Grafana, Loki e Tempo |P-02 | Rate limiting por rota, IP e usuário                                                                | Conter abuso e força bruta                               | Filtro do gateway com token bucket no Redis                         |
| RP-03 | Blacklist temporária de IPs                                                                         | Bloquear reincidentes                                    | Filtro do gateway consulta o Redis antes de rotear                  |
| RP-04 | Blind Login (sem enumeração de usuários, tempo constante)                                           | Impedir descoberta de contas                             | identity-service                                                    |
| RP-05 | 2FA por TOTP com códigos de recuperação                                                             | Reduzir tomada de conta                                  | identity-service e fluxo de login em duas etapas                    |
| RP-06 | Idempotency-Key no checkout e no pagamento | Evitar pedido ou cobrança duplicados em retry do cliente | Tabela `idempotency_keys` no PostgreSQL, unicidade `(user_id, key)` |
| RP-07 | Circuit Breaker, Bulkhead e retry HTTP | Isolar falhas do catálogo no checkout | Resilience4j na chamada de cotação |
| RP-08 | BFF consumidor de webhooks | Agregar dados por canal (mobile) | Consumidor externo do webhook `order.completed` |
| RP-09 | Schema Registry e CDC com Debezium | Contratos formais e projeções de leitura | Kafka Connect, PostgreSQL logical replication e indexador |                                 | Encaixe na arquitetura                                              |
| RP-10 | mTLS entre serviços | Autenticação de serviço a serviço | Substitui o `X-Internal-Token` |--------- | -------------------------------------------------------- | ------------------------------------------------------------------- |
| RP-11 | OpenSearch para busca inteligente | Fuzzy search, autocomplete e relevância | Índice assíncrono; PostgreSQL continua como fonte de verdade || RP-01 | Observabilidade: logs JSON, métricas Prometheus, traces OpenTelemetry, dashboards Grafana e alertas | Diagnosticar e operar em produção                        | Collector OTLP e propagação de `traceparent` no envelope de eventos |
| RP-12 | Observabilidade distribuída | Diagnóstico de falhas e análise de desempenho | OpenTelemetry, Prometheus, Grafana, Loki e Tempo |P-02 | Rate limiting por rota, IP e usuário                                                                | Conter abuso e força bruta                               | Filtro do gateway com token bucket no Redis                         |
| RP-03 | Blacklist temporária de IPs                                                                         | Bloquear reincidentes                                    | Filtro do gateway consulta o Redis antes de rotear                  |
| RP-04 | Blind Login (sem enumeração de usuários, tempo constante)                                           | Impedir descoberta de contas                             | identity-service                                                    |
| RP-05 | 2FA por TOTP com códigos de recuperação                                                             | Reduzir tomada de conta                                  | identity-service e fluxo de login em duas etapas                    |
| RP-06 | Idempotency-Key no checkout e no pagamento | Evitar pedido ou cobrança duplicados em retry do cliente | Tabela `idempotency_keys` no PostgreSQL, unicidade `(user_id, key)` |
| RP-07 | Circuit Breaker, Bulkhead e retry HTTP | Isolar falhas do catálogo no checkout | Resilience4j na chamada de cotação |
| RP-08 | BFF consumidor de webhooks | Agregar dados por canal (mobile) | Consumidor externo do webhook `order.completed` |
| RP-09 | Schema Registry e CDC com Debezium | Contratos formais e projeções de leitura | Kafka Connect, PostgreSQL logical replication e indexador |                                 | Encaixe na arquitetura                                              |
| RP-10 | mTLS entre serviços | Autenticação de serviço a serviço | Substitui o `X-Internal-Token` |--------- | -------------------------------------------------------- | ------------------------------------------------------------------- |
| RP-11 | OpenSearch para busca inteligente | Fuzzy search, autocomplete e relevância | Índice assíncrono; PostgreSQL continua como fonte de verdade || RP-01 | Observabilidade: logs JSON, métricas Prometheus, traces OpenTelemetry, dashboards Grafana e alertas | Diagnosticar e operar em produção                        | Collector OTLP e propagação de `traceparent` no envelope de eventos |
| RP-12 | Observabilidade distribuída | Diagnóstico de falhas e análise de desempenho | OpenTelemetry, Prometheus, Grafana, Loki e Tempo |P-02 | Rate limiting por rota, IP e usuário                                                                | Conter abuso e força bruta                               | Filtro do gateway com token bucket no Redis                         |
| RP-03 | Blacklist temporária de IPs                                                                         | Bloquear reincidentes                                    | Filtro do gateway consulta o Redis antes de rotear                  |
| RP-04 | Blind Login (sem enumeração de usuários, tempo constante)                                           | Impedir descoberta de contas                             | identity-service                                                    |
| RP-05 | 2FA por TOTP com códigos de recuperação                                                             | Reduzir tomada de conta                                  | identity-service e fluxo de login em duas etapas                    |
| RP-06 | Idempotency-Key no checkout e no pagamento | Evitar pedido ou cobrança duplicados em retry do cliente | Tabela `idempotency_keys` no PostgreSQL, unicidade `(user_id, key)` |
| RP-07 | Circuit Breaker, Bulkhead e retry HTTP | Isolar falhas do catálogo no checkout | Resilience4j na chamada de cotação |
| RP-08 | BFF consumidor de webhooks | Agregar dados por canal (mobile) | Consumidor externo do webhook `order.completed` |
| RP-09 | Schema Registry e CDC com Debezium | Contratos formais e projeções de leitura | Kafka Connect, PostgreSQL logical replication e indexador |                                 | Encaixe na arquitetura                                              |
| RP-10 | mTLS entre serviços | Autenticação de serviço a serviço | Substitui o `X-Internal-Token` |--------- | -------------------------------------------------------- | ------------------------------------------------------------------- |
| RP-11 | OpenSearch para busca inteligente | Fuzzy search, autocomplete e relevância | Índice assíncrono; PostgreSQL continua como fonte de verdade || RP-01 | Observabilidade: logs JSON, métricas Prometheus, traces OpenTelemetry, dashboards Grafana e alertas | Diagnosticar e operar em produção                        | Collector OTLP e propagação de `traceparent` no envelope de eventos |
| RP-12 | Observabilidade distribuída | Diagnóstico de falhas e análise de desempenho | OpenTelemetry, Prometheus, Grafana, Loki e Tempo |P-02 | Rate limiting por rota, IP e usuário                                                                | Conter abuso e força bruta                               | Filtro do gateway com token bucket no Redis                         |
| RP-03 | Blacklist temporária de IPs                                                                         | Bloquear reincidentes                                    | Filtro do gateway consulta o Redis antes de rotear                  |
| RP-04 | Blind Login (sem enumeração de usuários, tempo constante)                                           | Impedir descoberta de contas                             | identity-service                                                    |
| RP-05 | 2FA por TOTP com códigos de recuperação                                                             | Reduzir tomada de conta                                  | identity-service e fluxo de login em duas etapas                    |
| RP-06 | Idempotency-Key no checkout e no pagamento | Evitar pedido ou cobrança duplicados em retry do cliente | Tabela `idempotency_keys` no PostgreSQL, unicidade `(user_id, key)` |
| RP-07 | Circuit Breaker, Bulkhead e retry HTTP | Isolar falhas do catálogo no checkout | Resilience4j na chamada de cotação |
| RP-08 | BFF consumidor de webhooks | Agregar dados por canal (mobile) | Consumidor externo do webhook `order.completed` |
| RP-09 | Schema Registry e CDC com Debezium | Contratos formais e projeções de leitura | Kafka Connect, PostgreSQL logical replication e indexador |                                 | Encaixe na arquitetura                                              |
| RP-10 | mTLS entre serviços | Autenticação de serviço a serviço | Substitui o `X-Internal-Token` |--------- | -------------------------------------------------------- | ------------------------------------------------------------------- |
| RP-11 | OpenSearch para busca inteligente | Fuzzy search, autocomplete e relevância | Índice assíncrono; PostgreSQL continua como fonte de verdade || RP-01 | Observabilidade: logs JSON, métricas Prometheus, traces OpenTelemetry, dashboards Grafana e alertas | Diagnosticar e operar em produção                        | Collector OTLP e propagação de `traceparent` no envelope de eventos |
| RP-12 | Observabilidade distribuída | Diagnóstico de falhas e análise de desempenho | OpenTelemetry, Prometheus, Grafana, Loki e Tempo |P-02 | Rate limiting por rota, IP e usuário                                                                | Conter abuso e força bruta                               | Filtro do gateway com token bucket no Redis                         |
| RP-03 | Blacklist temporária de IPs                                                                         | Bloquear reincidentes                                    | Filtro do gateway consulta o Redis antes de rotear                  |
| RP-04 | Blind Login (sem enumeração de usuários, tempo constante)                                           | Impedir descoberta de contas                             | identity-service                                                    |
| RP-05 | 2FA por TOTP com códigos de recuperação                                                             | Reduzir tomada de conta                                  | identity-service e fluxo de login em duas etapas                    |
| RP-06 | Idempotency-Key no checkout e no pagamento | Evitar pedido ou cobrança duplicados em retry do cliente | Tabela `idempotency_keys` no PostgreSQL, unicidade `(user_id, key)` |
| RP-07 | Circuit Breaker, Bulkhead e retry HTTP | Isolar falhas do catálogo no checkout | Resilience4j na chamada de cotação |
| RP-08 | BFF consumidor de webhooks | Agregar dados por canal (mobile) | Consumidor externo do webhook `order.completed` |
| RP-09 | Schema Registry e CDC com Debezium | Contratos formais e projeções de leitura | Kafka Connect, PostgreSQL logical replication e indexador |                                 | Encaixe na arquitetura                                              |
| RP-10 | mTLS entre serviços | Autenticação de serviço a serviço | Substitui o `X-Internal-Token` |--------- | -------------------------------------------------------- | ------------------------------------------------------------------- |
| RP-11 | OpenSearch para busca inteligente | Fuzzy search, autocomplete e relevância | Índice assíncrono; PostgreSQL continua como fonte de verdade || RP-01 | Observabilidade: logs JSON, métricas Prometheus, traces OpenTelemetry, dashboards Grafana e alertas | Diagnosticar e operar em produção                        | Collector OTLP e propagação de `traceparent` no envelope de eventos |
| RP-12 | Observabilidade distribuída | Diagnóstico de falhas e análise de desempenho | OpenTelemetry, Prometheus, Grafana, Loki e Tempo |P-02 | Rate limiting por rota, IP e usuário                                                                | Conter abuso e força bruta                               | Filtro do gateway com token bucket no Redis                         |
| RP-03 | Blacklist temporária de IPs                                                                         | Bloquear reincidentes                                    | Filtro do gateway consulta o Redis antes de rotear                  |
| RP-04 | Blind Login (sem enumeração de usuários, tempo constante)                                           | Impedir descoberta de contas                             | identity-service                                                    |
| RP-05 | 2FA por TOTP com códigos de recuperação                                                             | Reduzir tomada de conta                                  | identity-service e fluxo de login em duas etapas                    |
| RP-06 | Idempotency-Key no checkout e no pagamento | Evitar pedido ou cobrança duplicados em retry do cliente | Tabela `idempotency_keys` no PostgreSQL, unicidade `(user_id, key)` |
| RP-07 | Circuit Breaker, Bulkhead e retry HTTP | Isolar falhas do catálogo no checkout | Resilience4j na chamada de cotação |
| RP-08 | BFF consumidor de webhooks | Agregar dados por canal (mobile) | Consumidor externo do webhook `order.completed` |
| RP-09 | Schema Registry e CDC com Debezium | Contratos formais e projeções de leitura | Kafka Connect, PostgreSQL logical replication e indexador |                                 | Encaixe na arquitetura                                              |
| RP-10 | mTLS entre serviços | Autenticação de serviço a serviço | Substitui o `X-Internal-Token` |--------- | -------------------------------------------------------- | ------------------------------------------------------------------- |
| RP-11 | OpenSearch para busca inteligente | Fuzzy search, autocomplete e relevância | Índice assíncrono; PostgreSQL continua como fonte de verdade || RP-01 | Observabilidade: logs JSON, métricas Prometheus, traces OpenTelemetry, dashboards Grafana e alertas | Diagnosticar e operar em produção                        | Collector OTLP e propagação de `traceparent` no envelope de eventos |
| RP-12 | Observabilidade distribuída | Diagnóstico de falhas e análise de desempenho | OpenTelemetry, Prometheus, Grafana, Loki e Tempo |P-02 | Rate limiting por rota, IP e usuário                                                                | Conter abuso e força bruta                               | Filtro do gateway com token bucket no Redis                         |
| RP-03 | Blacklist temporária de IPs                                                                         | Bloquear reincidentes                                    | Filtro do gateway consulta o Redis antes de rotear                  |
| RP-04 | Blind Login (sem enumeração de usuários, tempo constante)                                           | Impedir descoberta de contas                             | identity-service                                                    |
| RP-05 | 2FA por TOTP com códigos de recuperação                                                             | Reduzir tomada de conta                                  | identity-service e fluxo de login em duas etapas                    |
| RP-06 | Idempotency-Key no checkout e no pagamento | Evitar pedido ou cobrança duplicados em retry do cliente | Tabela `idempotency_keys` no PostgreSQL, unicidade `(user_id, key)` |
| RP-07 | Circuit Breaker, Bulkhead e retry HTTP | Isolar falhas do catálogo no checkout | Resilience4j na chamada de cotação |
| RP-08 | BFF consumidor de webhooks | Agregar dados por canal (mobile) | Consumidor externo do webhook `order.completed` |
| RP-09 | Schema Registry e CDC com Debezium | Contratos formais e projeções de leitura | Kafka Connect, PostgreSQL logical replication e indexador |                                 | Encaixe na arquitetura                                              |
| RP-10 | mTLS entre serviços | Autenticação de serviço a serviço | Substitui o `X-Internal-Token` |--------- | -------------------------------------------------------- | ------------------------------------------------------------------- |
| RP-11 | OpenSearch para busca inteligente | Fuzzy search, autocomplete e relevância | Índice assíncrono; PostgreSQL continua como fonte de verdade || RP-01 | Observabilidade: logs JSON, métricas Prometheus, traces OpenTelemetry, dashboards Grafana e alertas | Diagnosticar e operar em produção                        | Collector OTLP e propagação de `traceparent` no envelope de eventos |
| RP-12 | Observabilidade distribuída | Diagnóstico de falhas e análise de desempenho | OpenTelemetry, Prometheus, Grafana, Loki e Tempo |P-02 | Rate limiting por rota, IP e usuário                                                                | Conter abuso e força bruta                               | Filtro do gateway com token bucket no Redis                         |
| RP-03 | Blacklist temporária de IPs                                                                         | Bloquear reincidentes                                    | Filtro do gateway consulta o Redis antes de rotear                  |
| RP-04 | Blind Login (sem enumeração de usuários, tempo constante)                                           | Impedir descoberta de contas                             | identity-service                                                    |
| RP-05 | 2FA por TOTP com códigos de recuperação                                                             | Reduzir tomada de conta                                  | identity-service e fluxo de login em duas etapas                    |
| RP-06 | Idempotency-Key no checkout e no pagamento | Evitar pedido ou cobrança duplicados em retry do cliente | Tabela `idempotency_keys` no PostgreSQL, unicidade `(user_id, key)` |
| RP-07 | Circuit Breaker, Bulkhead e retry HTTP | Isolar falhas do catálogo no checkout | Resilience4j na chamada de cotação |
| RP-08 | BFF consumidor de webhooks | Agregar dados por canal (mobile) | Consumidor externo do webhook `order.completed` |
| RP-09 | Schema Registry e CDC com Debezium | Contratos formais e projeções de leitura | Kafka Connect, PostgreSQL logical replication e indexador |                                 | Encaixe na arquitetura                                              |
| RP-10 | mTLS entre serviços | Autenticação de serviço a serviço | Substitui o `X-Internal-Token` |--------- | -------------------------------------------------------- | ------------------------------------------------------------------- |
| RP-11 | OpenSearch para busca inteligente | Fuzzy search, autocomplete e relevância | Índice assíncrono; PostgreSQL continua como fonte de verdade || RP-01 | Observabilidade: logs JSON, métricas Prometheus, traces OpenTelemetry, dashboards Grafana e alertas | Diagnosticar e operar em produção                        | Collector OTLP e propagação de `traceparent` no envelope de eventos |
| RP-12 | Observabilidade distribuída | Diagnóstico de falhas e análise de desempenho | OpenTelemetry, Prometheus, Grafana, Loki e Tempo |P-02 | Rate limiting por rota, IP e usuário                                                                | Conter abuso e força bruta                               | Filtro do gateway com token bucket no Redis                         |
| RP-03 | Blacklist temporária de IPs                                                                         | Bloquear reincidentes                                    | Filtro do gateway consulta o Redis antes de rotear                  |
| RP-04 | Blind Login (sem enumeração de usuários, tempo constante)                                           | Impedir descoberta de contas                             | identity-service                                                    |
| RP-05 | 2FA por TOTP com códigos de recuperação                                                             | Reduzir tomada de conta                                  | identity-service e fluxo de login em duas etapas                    |
| RP-06 | Idempotency-Key no checkout e no pagamento | Evitar pedido ou cobrança duplicados em retry do cliente | Tabela `idempotency_keys` no PostgreSQL, unicidade `(user_id, key)` |
| RP-07 | Circuit Breaker, Bulkhead e retry HTTP | Isolar falhas do catálogo no checkout | Resilience4j na chamada de cotação |
| RP-08 | BFF consumidor de webhooks | Agregar dados por canal (mobile) | Consumidor externo do webhook `order.completed` |
| RP-09 | Schema Registry e CDC com Debezium | Contratos formais e projeções de leitura | Kafka Connect, PostgreSQL logical replication e indexador |                                 | Encaixe na arquitetura                                              |
| RP-10 | mTLS entre serviços | Autenticação de serviço a serviço | Substitui o `X-Internal-Token` |--------- | -------------------------------------------------------- | ------------------------------------------------------------------- |
| RP-11 | OpenSearch para busca inteligente | Fuzzy search, autocomplete e relevância | Índice assíncrono; PostgreSQL continua como fonte de verdade || RP-01 | Observabilidade: logs JSON, métricas Prometheus, traces OpenTelemetry, dashboards Grafana e alertas | Diagnosticar e operar em produção                        | Collector OTLP e propagação de `traceparent` no envelope de eventos |
| RP-12 | Observabilidade distribuída | Diagnóstico de falhas e análise de desempenho | OpenTelemetry, Prometheus, Grafana, Loki e Tempo |P-02 | Rate limiting por rota, IP e usuário                                                                | Conter abuso e força bruta                               | Filtro do gateway com token bucket no Redis                         |
| RP-03 | Blacklist temporária de IPs                                                                         | Bloquear reincidentes                                    | Filtro do gateway consulta o Redis antes de rotear                  |
| RP-04 | Blind Login (sem enumeração de usuários, tempo constante)                                           | Impedir descoberta de contas                             | identity-service                                                    |
| RP-05 | 2FA por TOTP com códigos de recuperação                                                             | Reduzir tomada de conta                                  | identity-service e fluxo de login em duas etapas                    |
| RP-06 | Idempotency-Key no checkout e no pagamento | Evitar pedido ou cobrança duplicados em retry do cliente | Tabela `idempotency_keys` no PostgreSQL, unicidade `(user_id, key)` |
| RP-07 | Circuit Breaker, Bulkhead e retry HTTP | Isolar falhas do catálogo no checkout | Resilience4j na chamada de cotação |
| RP-08 | BFF consumidor de webhooks | Agregar dados por canal (mobile) | Consumidor externo do webhook `order.completed` |
| RP-09 | Schema Registry e CDC com Debezium | Contratos formais e projeções de leitura | Kafka Connect, PostgreSQL logical replication e indexador |                                 | Encaixe na arquitetura                                              |
| RP-10 | mTLS entre serviços | Autenticação de serviço a serviço | Substitui o `X-Internal-Token` |--------- | -------------------------------------------------------- | ------------------------------------------------------------------- |
| RP-11 | OpenSearch para busca inteligente | Fuzzy search, autocomplete e relevância | Índice assíncrono; PostgreSQL continua como fonte de verdade || RP-01 | Observabilidade: logs JSON, métricas Prometheus, traces OpenTelemetry, dashboards Grafana e alertas | Diagnosticar e operar em produção                        | Collector OTLP e propagação de `traceparent` no envelope de eventos |
| RP-12 | Observabilidade distribuída | Diagnóstico de falhas e análise de desempenho | OpenTelemetry, Prometheus, Grafana, Loki e Tempo |P-02 | Rate limiting por rota, IP e usuário                                                                | Conter abuso e força bruta                               | Filtro do gateway com token bucket no Redis                         |
| RP-03 | Blacklist temporária de IPs                                                                         | Bloquear reincidentes                                    | Filtro do gateway consulta o Redis antes de rotear                  |
| RP-04 | Blind Login (sem enumeração de usuários, tempo constante)                                           | Impedir descoberta de contas                             | identity-service                                                    |
| RP-05 | 2FA por TOTP com códigos de recuperação                                                             | Reduzir tomada de conta                                  | identity-service e fluxo de login em duas etapas                    |
| RP-06 | Idempotency-Key no checkout e no pagamento | Evitar pedido ou cobrança duplicados em retry do cliente | Tabela `idempotency_keys` no PostgreSQL, unicidade `(user_id, key)` |
| RP-07 | Circuit Breaker, Bulkhead e retry HTTP | Isolar falhas do catálogo no checkout | Resilience4j na chamada de cotação |
| RP-08 | BFF consumidor de webhooks | Agregar dados por canal (mobile) | Consumidor externo do webhook `order.completed` |
| RP-09 | Schema Registry e CDC com Debezium | Contratos formais e projeções de leitura | Kafka Connect, PostgreSQL logical replication e indexador |                                 | Encaixe na arquitetura                                              |
| RP-10 | mTLS entre serviços | Autenticação de serviço a serviço | Substitui o `X-Internal-Token` |--------- | -------------------------------------------------------- | ------------------------------------------------------------------- |
| RP-11 | OpenSearch para busca inteligente | Fuzzy search, autocomplete e relevância | Índice assíncrono; PostgreSQL continua como fonte de verdade || RP-01 | Observabilidade: logs JSON, métricas Prometheus, traces OpenTelemetry, dashboards Grafana e alertas | Diagnosticar e operar em produção                        | Collector OTLP e propagação de `traceparent` no envelope de eventos |
| RP-12 | Observabilidade distribuída | Diagnóstico de falhas e análise de desempenho | OpenTelemetry, Prometheus, Grafana, Loki e Tempo |P-02 | Rate limiting por rota, IP e usuário                                                                | Conter abuso e força bruta                               | Filtro do gateway com token bucket no Redis                         |
| RP-03 | Blacklist temporária de IPs                                                                         | Bloquear reincidentes                                    | Filtro do gateway consulta o Redis antes de rotear                  |
| RP-04 | Blind Login (sem enumeração de usuários, tempo constante)                                           | Impedir descoberta de contas                             | identity-service                                                    |
| RP-05 | 2FA por TOTP com códigos de recuperação                                                             | Reduzir tomada de conta                                  | identity-service e fluxo de login em duas etapas                    |
| RP-06 | Idempotency-Key no checkout e no pagamento | Evitar pedido ou cobrança duplicados em retry do cliente | Tabela `idempotency_keys` no PostgreSQL, unicidade `(user_id, key)` |
| RP-07 | Circuit Breaker, Bulkhead e retry HTTP | Isolar falhas do catálogo no checkout | Resilience4j na chamada de cotação |
| RP-08 | BFF consumidor de webhooks | Agregar dados por canal (mobile) | Consumidor externo do webhook `order.completed` |
| RP-09 | Schema Registry e CDC com Debezium | Contratos formais e projeções de leitura | Kafka Connect, PostgreSQL logical replication e indexador |                                 | Encaixe na arquitetura                                              |
| RP-10 | mTLS entre serviços | Autenticação de serviço a serviço | Substitui o `X-Internal-Token` |--------- | -------------------------------------------------------- | ------------------------------------------------------------------- |
| RP-11 | OpenSearch para busca inteligente | Fuzzy search, autocomplete e relevância | Índice assíncrono; PostgreSQL continua como fonte de verdade || RP-01 | Observabilidade: logs JSON, métricas Prometheus, traces OpenTelemetry, dashboards Grafana e alertas | Diagnosticar e operar em produção                        | Collector OTLP e propagação de `traceparent` no envelope de eventos |
| RP-12 | Observabilidade distribuída | Diagnóstico de falhas e análise de desempenho | OpenTelemetry, Prometheus, Grafana, Loki e Tempo |P-02 | Rate limiting por rota, IP e usuário                                                                | Conter abuso e força bruta                               | Filtro do gateway com token bucket no Redis                         |
| RP-03 | Blacklist temporária de IPs                                                                         | Bloquear reincidentes                                    | Filtro do gateway consulta o Redis antes de rotear                  |
| RP-04 | Blind Login (sem enumeração de usuários, tempo constante)                                           | Impedir descoberta de contas                             | identity-service                                                    |
| RP-05 | 2FA por TOTP com códigos de recuperação                                                             | Reduzir tomada de conta                                  | identity-service e fluxo de login em duas etapas                    |
| RP-06 | Idempotency-Key no checkout e no pagamento | Evitar pedido ou cobrança duplicados em retry do cliente | Tabela `idempotency_keys` no PostgreSQL, unicidade `(user_id, key)` |
| RP-07 | Circuit Breaker, Bulkhead e retry HTTP | Isolar falhas do catálogo no checkout | Resilience4j na chamada de cotação |
| RP-08 | BFF consumidor de webhooks | Agregar dados por canal (mobile) | Consumidor externo do webhook `order.completed` |
| RP-09 | Schema Registry e CDC com Debezium | Contratos formais e projeções de leitura | Kafka Connect, PostgreSQL logical replication e indexador |                                 | Encaixe na arquitetura                                              |
| RP-10 | mTLS entre serviços | Autenticação de serviço a serviço | Substitui o `X-Internal-Token` |--------- | -------------------------------------------------------- | ------------------------------------------------------------------- |
| RP-11 | OpenSearch para busca inteligente | Fuzzy search, autocomplete e relevância | Índice assíncrono; PostgreSQL continua como fonte de verdade || RP-01 | Observabilidade: logs JSON, métricas Prometheus, traces OpenTelemetry, dashboards Grafana e alertas | Diagnosticar e operar em produção                        | Collector OTLP e propagação de `traceparent` no envelope de eventos |
| RP-12 | Observabilidade distribuída | Diagnóstico de falhas e análise de desempenho | OpenTelemetry, Prometheus, Grafana, Loki e Tempo |P-02 | Rate limiting por rota, IP e usuário                                                                | Conter abuso e força bruta                               | Filtro do gateway com token bucket no Redis                         |
| RP-03 | Blacklist temporária de IPs                                                                         | Bloquear reincidentes                                    | Filtro do gateway consulta o Redis antes de rotear                  |
| RP-04 | Blind Login (sem enumeração de usuários, tempo constante)                                           | Impedir descoberta de contas                             | identity-service                                                    |
| RP-05 | 2FA por TOTP com códigos de recuperação                                                             | Reduzir tomada de conta                                  | identity-service e fluxo de login em duas etapas                    |
| RP-06 | Idempotency-Key no checkout e no pagamento | Evitar pedido ou cobrança duplicados em retry do cliente | Tabela `idempotency_keys` no PostgreSQL, unicidade `(user_id, key)` |
| RP-07 | Circuit Breaker, Bulkhead e retry HTTP | Isolar falhas do catálogo no checkout | Resilience4j na chamada de cotação |
| RP-08 | BFF consumidor de webhooks | Agregar dados por canal (mobile) | Consumidor externo do webhook `order.completed` |
| RP-09 | Schema Registry e CDC com Debezium | Contratos formais e projeções de leitura | Kafka Connect, PostgreSQL logical replication e indexador |                                 | Encaixe na arquitetura                                              |
| RP-10 | mTLS entre serviços | Autenticação de serviço a serviço | Substitui o `X-Internal-Token` |--------- | -------------------------------------------------------- | ------------------------------------------------------------------- |
| RP-11 | OpenSearch para busca inteligente | Fuzzy search, autocomplete e relevância | Índice assíncrono; PostgreSQL continua como fonte de verdade || RP-01 | Observabilidade: logs JSON, métricas Prometheus, traces OpenTelemetry, dashboards Grafana e alertas | Diagnosticar e operar em produção                        | Collector OTLP e propagação de `traceparent` no envelope de eventos |
| RP-12 | Observabilidade distribuída | Diagnóstico de falhas e análise de desempenho | OpenTelemetry, Prometheus, Grafana, Loki e Tempo |P-02 | Rate limiting por rota, IP e usuário                                                                | Conter abuso e força bruta                               | Filtro do gateway com token bucket no Redis                         |
| RP-03 | Blacklist temporária de IPs                                                                         | Bloquear reincidentes                                    | Filtro do gateway consulta o Redis antes de rotear                  |
| RP-04 | Blind Login (sem enumeração de usuários, tempo constante)                                           | Impedir descoberta de contas                             | identity-service                                                    |
| RP-05 | 2FA por TOTP com códigos de recuperação                                                             | Reduzir tomada de conta                                  | identity-service e fluxo de login em duas etapas                    |
| RP-06 | Idempotency-Key no checkout e no pagamento | Evitar pedido ou cobrança duplicados em retry do cliente | Tabela `idempotency_keys` no PostgreSQL, unicidade `(user_id, key)` |
| RP-07 | Circuit Breaker, Bulkhead e retry HTTP | Isolar falhas do catálogo no checkout | Resilience4j na chamada de cotação |
| RP-08 | BFF consumidor de webhooks | Agregar dados por canal (mobile) | Consumidor externo do webhook `order.completed` |
| RP-09 | Schema Registry e CDC com Debezium | Contratos formais e projeções de leitura | Kafka Connect, PostgreSQL logical replication e indexador |                                 | Encaixe na arquitetura                                              |
| RP-10 | mTLS entre serviços | Autenticação de serviço a serviço | Substitui o `X-Internal-Token` |--------- | -------------------------------------------------------- | ------------------------------------------------------------------- |
| RP-11 | OpenSearch para busca inteligente | Fuzzy search, autocomplete e relevância | Índice assíncrono; PostgreSQL continua como fonte de verdade || RP-01 | Observabilidade: logs JSON, métricas Prometheus, traces OpenTelemetry, dashboards Grafana e alertas | Diagnosticar e operar em produção                        | Collector OTLP e propagação de `traceparent` no envelope de eventos |
| RP-12 | Observabilidade distribuída | Diagnóstico de falhas e análise de desempenho | OpenTelemetry, Prometheus, Grafana, Loki e Tempo |P-02 | Rate limiting por rota, IP e usuário                                                                | Conter abuso e força bruta                               | Filtro do gateway com token bucket no Redis                         |
| RP-03 | Blacklist temporária de IPs                                                                         | Bloquear reincidentes                                    | Filtro do gateway consulta o Redis antes de rotear                  |
| RP-04 | Blind Login (sem enumeração de usuários, tempo constante)                                           | Impedir descoberta de contas                             | identity-service                                                    |
| RP-05 | 2FA por TOTP com códigos de recuperação                                                             | Reduzir tomada de conta                                  | identity-service e fluxo de login em duas etapas                    |
| RP-06 | Idempotency-Key no checkout e no pagamento | Evitar pedido ou cobrança duplicados em retry do cliente | Tabela `idempotency_keys` no PostgreSQL, unicidade `(user_id, key)` |
| RP-07 | Circuit Breaker, Bulkhead e retry HTTP | Isolar falhas do catálogo no checkout | Resilience4j na chamada de cotação |
| RP-08 | BFF consumidor de webhooks | Agregar dados por canal (mobile) | Consumidor externo do webhook `order.completed` |
| RP-09 | Schema Registry e CDC com Debezium | Contratos formais e projeções de leitura | Kafka Connect, PostgreSQL logical replication e indexador |                                 | Encaixe na arquitetura                                              |
| RP-10 | mTLS entre serviços | Autenticação de serviço a serviço | Substitui o `X-Internal-Token` |--------- | -------------------------------------------------------- | ------------------------------------------------------------------- |
| RP-11 | OpenSearch para busca inteligente | Fuzzy search, autocomplete e relevância | Índice assíncrono; PostgreSQL continua como fonte de verdade || RP-01 | Observabilidade: logs JSON, métricas Prometheus, traces OpenTelemetry, dashboards Grafana e alertas | Diagnosticar e operar em produção                        | Collector OTLP e propagação de `traceparent` no envelope de eventos |
| RP-12 | Observabilidade distribuída | Diagnóstico de falhas e análise de desempenho | OpenTelemetry, Prometheus, Grafana, Loki e Tempo |P-02 | Rate limiting por rota, IP e usuário                                                                | Conter abuso e força bruta                               | Filtro do gateway com token bucket no Redis                         |
| RP-03 | Blacklist temporária de IPs                                                                         | Bloquear reincidentes                                    | Filtro do gateway consulta o Redis antes de rotear                  |
| RP-04 | Blind Login (sem enumeração de usuários, tempo constante)                                           | Impedir descoberta de contas                             | identity-service                                                    |
| RP-05 | 2FA por TOTP com códigos de recuperação                                                             | Reduzir tomada de conta                                  | identity-service e fluxo de login em duas etapas                    |
| RP-06 | Idempotency-Key no checkout e no pagamento | Evitar pedido ou cobrança duplicados em retry do cliente | Tabela `idempotency_keys` no PostgreSQL, unicidade `(user_id, key)` |
| RP-07 | Circuit Breaker, Bulkhead e retry HTTP | Isolar falhas do catálogo no checkout | Resilience4j na chamada de cotação |
| RP-08 | BFF consumidor de webhooks | Agregar dados por canal (mobile) | Consumidor externo do webhook `order.completed` |
| RP-09 | Schema Registry e CDC com Debezium | Contratos formais e projeções de leitura | Kafka Connect, PostgreSQL logical replication e indexador |                                 | Encaixe na arquitetura                                              |
| RP-10 | mTLS entre serviços | Autenticação de serviço a serviço | Substitui o `X-Internal-Token` |--------- | -------------------------------------------------------- | ------------------------------------------------------------------- |
| RP-11 | OpenSearch para busca inteligente | Fuzzy search, autocomplete e relevância | Índice assíncrono; PostgreSQL continua como fonte de verdade || RP-01 | Observabilidade: logs JSON, métricas Prometheus, traces OpenTelemetry, dashboards Grafana e alertas | Diagnosticar e operar em produção                        | Collector OTLP e propagação de `traceparent` no envelope de eventos |
| RP-12 | Observabilidade distribuída | Diagnóstico de falhas e análise de desempenho | OpenTelemetry, Prometheus, Grafana, Loki e Tempo |P-02 | Rate limiting por rota, IP e usuário                                                                | Conter abuso e força bruta                               | Filtro do gateway com token bucket no Redis                         |
| RP-03 | Blacklist temporária de IPs                                                                         | Bloquear reincidentes                                    | Filtro do gateway consulta o Redis antes de rotear                  |
| RP-04 | Blind Login (sem enumeração de usuários, tempo constante)                                           | Impedir descoberta de contas                             | identity-service                                                    |
| RP-05 | 2FA por TOTP com códigos de recuperação                                                             | Reduzir tomada de conta                                  | identity-service e fluxo de login em duas etapas                    |
| RP-06 | Idempotency-Key no checkout e no pagamento | Evitar pedido ou cobrança duplicados em retry do cliente | Tabela `idempotency_keys` no PostgreSQL, unicidade `(user_id, key)` |
| RP-07 | Circuit Breaker, Bulkhead e retry HTTP | Isolar falhas do catálogo no checkout | Resilience4j na chamada de cotação |