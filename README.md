# Rota para Todos - API (Domain Driven Design Using Java)

API REST em **Java 21 + Spring Boot 4.1** (mesma versão do projeto de aula, Apostila16), com **DAO em JDBC puro** (sem JPA/Hibernate/Spring Data),
banco **Oracle da FIAP**, DTOs com Bean Validation, ModelMapper, handler global de exceções e CORS.
Global Solution 2026/2 - CitizenTech.

## Integrantes

| Nome | RM | Turma |
|---|---|---|
| Lucas Kaftan | 571302 | 1TDSPV |
| Matheus Ruas | 571047 | 1TDSPV |
| João Carlos Lopes | 568952 | 1TDSPV |

## Links

- API no Render: `[PREENCHER]`
- Vídeo (YouTube, não listado): `[PREENCHER]`
- Front-end (Vercel): `[PREENCHER]`

## Como rodar localmente

Pré-requisitos: JDK 21 ou mais novo. O Maven não precisa estar instalado (o projeto traz o Maven Wrapper).

1. Crie as tabelas no Oracle: abra `sql/01_criar_tabelas.sql` no SQL Developer e execute (F5).
   Opcional: execute `sql/02_dados_iniciais.sql` para ter dados e contas de demonstração
   (os arquivos estão em UTF-8).
2. Defina as variáveis de ambiente (os valores nunca ficam no código). No PowerShell:

   ```powershell
   $env:DB_URL = "jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL"
   $env:DB_USER = "seu_usuario"
   $env:DB_PASSWORD = "sua_senha"
   ```

3. Suba a API:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

4. Teste: `http://localhost:8080/` deve responder `API Rota para Todos no ar!`.

### Variáveis de ambiente

| Nome | Obrigatória | Para que serve |
|---|---|---|
| `DB_URL` | sim | URL JDBC do Oracle |
| `DB_USER` | sim | Usuário do Oracle |
| `DB_PASSWORD` | sim | Senha do Oracle |
| `APP_TOKEN_SECRET` | não | Segredo que assina o token de sessão. Sem ele, a API gera um a cada início |
| `PORT` | não | Porta HTTP (padrão 8080; o Render define sozinho) |

## Deploy no Render (Docker)

1. Suba esta pasta em um repositório do GitHub.
2. No Render: **New > Web Service**, escolha o repositório, **Language: Docker** (ele usa o `Dockerfile` multi-stage).
   Se a API estiver em uma subpasta do repositório, informe-a em **Root Directory**.
3. Em **Environment**, cadastre `DB_URL`, `DB_USER`, `DB_PASSWORD` e `APP_TOKEN_SECRET`.
4. Depois do deploy, abra a URL do serviço e confira a rota `/`.

## Estrutura (um pacote por responsabilidade, no padrão da Apostila16)

```
br.com.fiap.rotaparatodos
├── model       entidades (Usuario, Relato, Validacao) e enums, com int id e relacionamento por objeto
├── dao         UsuarioDao, RelatoDao, ValidacaoDao: JDBC com DataSource no construtor e PreparedStatement
├── dto         XxxRequestDto (entrada, com Bean Validation) e XxxResponseDto (saída)
├── controller  rotas REST: chamam o DAO direto, convertem com ModelMapper, devolvem ResponseEntity
├── exception   EntidadeNaoEncontradaException e as exceções das regras de negócio (extends Exception)
├── handler     ApiExceptionHandler (@RestControllerAdvice): erro em JSON padronizado
├── config      CorsConfig, ModelMapperConfig e SenhaConfig (BCrypt)
└── service     só o que não é CRUD: cálculo das rotas, painel, classificação e login (ver abaixo)
```

Caminho de uma requisição: **controller** recebe o **RequestDto** (validado com `@Valid`) → converte para o
**model** com ModelMapper → chama o **DAO**, que executa o SQL no Oracle → o controller devolve o **ResponseDto**.

## Domínio

- **Usuario** 1:N **Relato** (um cidadão faz vários relatos) - `tb_usuario`, `tb_relato`
- **Relato** 1:N **Validacao** (um relato recebe vários votos) - `tb_relato`, `tb_validacao`

## Endpoints

Rotas marcadas com 🔒 exigem o cabeçalho `Authorization: Bearer <token>` (obtido no login).

| Método | Rota | Sucesso | Erros |
|---|---|---|---|
| POST | `/auth/login` | 200 | 400, 401 |
| POST | `/usuarios` | 201 + Location | 400, 409 |
| GET | `/usuarios` 🔒 gestor | 200 | 401, 403 |
| GET | `/usuarios/{id}` 🔒 | 200 | 401, 403, 404 |
| PUT | `/usuarios/{id}` 🔒 | 200 | 400, 401, 403, 404, 409 |
| DELETE | `/usuarios/{id}` 🔒 | 204 | 401, 403, 404, 409 |
| GET | `/usuarios/{id}/relatos` 🔒 | 200 | 401, 403, 404 |
| POST | `/relatos` 🔒 | 201 + Location | 400, 401, 409 |
| GET | `/relatos` | 200 | 400 |
| GET | `/relatos/{id}` | 200 | 404 |
| PUT | `/relatos/{id}` 🔒 | 200 | 400, 401, 403, 404, 409 |
| PATCH | `/relatos/{id}/status` 🔒 gestor | 200 | 400, 401, 403, 404, 409 |
| DELETE | `/relatos/{id}` 🔒 | 204 | 401, 403, 404, 409 |
| POST | `/relatos/classificacoes` | 200 | 400 |
| GET | `/relatos/{id}/validacoes` | 200 | 404 |
| POST | `/relatos/{id}/validacoes` 🔒 | 201 + Location | 400, 401, 404, 409 |
| GET | `/validacoes` | 200 | - |
| GET | `/validacoes/{id}` | 200 | 404 |
| PUT | `/validacoes/{id}` 🔒 | 200 | 400, 401, 403, 404 |
| DELETE | `/validacoes/{id}` 🔒 | 204 | 401, 403, 404 |
| POST | `/rotas/avaliacoes` | 200 | 400, 409 |
| GET | `/painel/indicadores` | 200 | - |
| GET | `/painel/ranking` | 200 | 400 |

Filtros de `GET /relatos`: `status`, `bairro`, `tipo`, `gravidade` e `latitude` + `longitude` + `raio`
(relatos ativos perto de um ponto). Método não suportado devolve 405; erro inesperado, 500 com mensagem genérica.

### Exemplo: criar relato

```json
POST /relatos
{
  "tipo": "BURACO",
  "gravidade": "ALTA",
  "descricao": "Buraco grande no meio da calçada",
  "endereco": "Rua Vergueiro, 1000",
  "bairro": "Paraíso",
  "latitude": -23.5712,
  "longitude": -46.6401
}
```

Resposta `201 Created` (cabeçalho `Location: /relatos/100`):

```json
{
  "id": 100,
  "protocolo": "RT-2026-00100",
  "tipo": "BURACO",
  "gravidade": "ALTA",
  "status": "PUBLICADO",
  "confiancaIa": 92,
  "confirmacoes": 0,
  "confirmacoesFaltantes": 3,
  "meu": true
}
```

### Formato padrão de erro

Igual ao `ErroResponseDto` da aula:

```json
{ "erro": "Not Found", "detalhe": "Relato não encontrado" }
```

Erro de validação (400) devolve um mapa campo → mensagem, como no `ApiExceptionHandler` da aula:

```json
{ "email": "Informe um e-mail válido.", "senha": "A senha deve ter entre 8 e 60 caracteres." }
```

## Regras de negócio (todas com exceção própria, HTTP 409)

| Regra | Onde | Exceção |
|---|---|---|
| Máximo de 10 relatos por usuário em 24 h (RN10) | `RelatoController` | `LimiteRelatosException` |
| Não duplicar: mesmo tipo, ativo, a menos de 15 m (RF12) | `RelatoController` | `RelatoDuplicadoException` |
| Confiança da classificação < 70 vai para moderação (RN11) | `RelatoController` | - |
| O autor não valida o próprio relato (RN16) | `RelatoController` | `ValidacaoNaoPermitidaException` |
| Só vota quem está a até 100 m do ponto (RN14) | `RelatoController` | `ValidacaoNaoPermitidaException` |
| Um voto por pessoa; 3 votos iguais mudam o status (RN15) | `RelatoController` + `Relato.recalcularStatus` | `ValidacaoNaoPermitidaException` |
| Relato sem confirmação em 90 dias expira (RN17) | `Relato` | - |
| Não remover registro com filhos | controllers | `RegistroComDependenciasException` |
| E-mail único | `UsuarioController` | `EmailJaCadastradoException` |
| Índice de acessibilidade por perfil, bloqueio e regra noturna (RN02, RN04, RN05, RN06, RN08) | `RotaService` | - |
| Índice de Impacto = 0,40 G + 0,25 C + 0,20 P + 0,15 F (RN19) | `PainelService` | - |

## Simplificações desta versão acadêmica

- A classificação "por IA" é **simulada** por palavras-chave da descrição (`ClassificadorService`).
  O resultado (tipo, gravidade, confiança) é o do projeto, pronto para trocar por um modelo real.
- Equipamentos públicos e fluxo de pedestres usados no Índice de Impacto são uma amostra fixa
  com coordenadas aproximadas (`PainelService`).
- Sem verificação de e-mail, login social, desfoque de rostos, reputação, exportação CSV e navegação por voz.

## O que vai além do que foi visto em aula (e por quê)

O projeto segue o padrão da Apostila16. Estes pontos não aparecem nela e foram mantidos só porque são
necessários para o que a GS pede:

| O quê | Onde | Por que é necessário |
|---|---|---|
| Pacote `service` | `RotaService`, `PainelService`, `ClassificadorService` | São cálculos (índice da rota, ranking, classificação), não CRUD. Dentro dos controllers eles ficariam enormes |
| Login com token | `TokenService`, `AutenticacaoService`, `@RequestHeader("Authorization")` | Relatar, votar e mudar status exigem saber quem é o usuário (RN01, RN13, RN16) |
| Hash de senha (BCrypt) | `SenhaConfig`, dependência `spring-security-crypto` | Senha nunca pode ser gravada em texto puro (RNF01) |
| `enum` | `model` (`TipoObstaculo`, `Gravidade`, `StatusRelato`...) | Limitam os valores aceitos e guardam dados de cada opção (pesos, fator da gravidade) |
| `@Value` | `TokenService` | Lê o segredo do token da variável de ambiente, sem deixar no código |
| `Comparator` e `List.sort` | services e `RelatoController` | Ordenar rotas, ranking e relatos por distância |
| Status 401/403/409/415 | `ApiExceptionHandler` | A rubrica pede 409 para regra de negócio; 401/403 vêm do login |
| CLOB com `setCharacterStream` | `RelatoDao` | A foto em base64 é um texto grande demais para `setString` |

Diferenças intencionais em relação ao exemplo da aula:

- **Usuário e senha do banco ficam em variáveis de ambiente**, não no `application.properties`
  (a rubrica tira 10 pontos se estiverem no código).
- O handler de `SQLException` **não devolve `e.getMessage()`**: a mensagem do SQL vai só para o log
  (a rubrica tira 5 pontos se a resposta mostrar detalhes do SQL).
- O `ApiExceptionHandler` fica no pacote `handler`, como pede o slide de arquitetura da GS.
- O protocolo do relato (`RT-2026-00042`) não é uma coluna: é montado pelo `Relato.getProtocolo()` a partir do id.

## Uso de IA generativa

`[PREENCHER: ferramentas usadas, para quê, e o que o grupo revisou]`
