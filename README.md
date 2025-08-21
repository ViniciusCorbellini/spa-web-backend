# MicroTx — Rede Social de Microtextos

## 1) Problema
Usuários de redes sociais, especialmente estudantes e jovens, têm dificuldade em encontrar um espaço leve para compartilhar pensamentos curtos sem distrações excessivas.
Isso causa perda de engajamento e sobrecarga de informações irrelevantes.
No início, o foco será usuários que querem publicar e acompanhar mensagens rápidas com o objetivo de criar uma timeline simples, com posts temporários e frases anônimas.

## 2) Atores e Decisores (quem usa / quem decide)
Usuários principais: Estudantes, jovens e qualquer pessoa que queira trocar microtextos
Decisores/Apoiadores: Administradores; equipe de desenvolvimento

## 3) Casos de uso (de forma simples)
Todos: Logar/deslogar; Manter dados cadastrais;
Usuário:
- Criar, editar e remover posts
- Pesquisar por posts ou usuários
- Seguir outros usuários
- Postar frases temporárias e/ou anônimas
- Visualizar perfil (dados cadastrais)
- Alterar informações do perfil

<!-- TODO -->
## 4) Limites e suposições
<!-- Simples assim:
     - Limites = regras/prazos/obrigações que você não controla.
     - Suposições = coisas que você espera ter e podem falhar.
     - Plano B = como você segue com a 1ª fatia se algo falhar.
     EXEMPLO:
     Limites: entrega final até o fim da disciplina (ex.: 2025-11-30); rodar no navegador; sem serviços pagos.
     Suposições: internet no laboratório; navegador atualizado; acesso ao GitHub; 10 min para teste rápido.
     Plano B: sem internet → rodar local e salvar em arquivo/LocalStorage; sem tempo do professor → testar com 3 colegas. -->
Limites: [prazo final], [regras/tecnologias obrigatórias], [restrições]  
Suposições: [internet/navegador/GitHub/tempo de teste]  
Plano B: [como continua entregando a 1ª fatia se algo falhar]

<!-- TODO -->
## 5) Hipóteses + validação
H-Valor: Se os usuários puderem publicar posts curtos e temporários, então o engajamento melhora em quantidade de posts criados e interações na timeline.
Validação (valor): teste com 5 usuários; meta: ≥4 conseguem postar e ver posts sem ajuda.

H-Viabilidade: Com Java Spring Boot + React, carregar timeline leva até 2s.
Validação (viabilidade): medir no protótipo com 30 ações; meta: ≥27 de 30 ações em ≤2s.

## 6) Fluxo principal e primeira fatia
**Fluxo principal (curto):**  
1) Usuário se cadastra ou faz login → 2) Cria post ou frase temporária → 3) Sistema salva no banco → 4) Timeline mostra posts

**Primeira fatia vertical (escopo mínimo):**  
Inclui: cadastro/login, criar post, listar posts
Critérios de aceite:
- Criar post → aparece na timeline
- Post temporário → some após tempo definido
- Cadastro → usuário pode logar em seguida

## 7) Esboços de telas do sistema (wireframes)
![Protótipo das telas](img_prototipo_sist.png)

<!-- TODO -->
## 8) Tecnologias

<!-- TODO -->
### 8.1 Navegador
**Navegador:** React + TailwindCSS
**Armazenamento local (se usar):** LocalStorage (para sessão)
**Hospedagem:** execução local via Docker

<!-- TODO -->
### 8.2 Front-end (servidor de aplicação, se existir)
**Front-end (servidor):** React
**Hospedagem:** container Docker

### 8.3 Back-end (API/servidor, se existir)
**Back-end (API):** Spring Boot (Java)
**Banco de dados:** PostgreSQL
**Deploy do back-end:** ambos rodando em containers Docker (API + banco)

## 9) Plano de Dados

### 9.1 Entidades
- Usuario — pessoa que usa o sistema
- Post — microtexto criado por um usuário
- Seguidor — relação entre usuários (quem segue quem)
- FraseAnonima — mensagem temporária sem identificação do autor

### 9.2 Campos por entidade

#### Usuario

| Campo       | Tipo     | Obrigatório | Exemplo           |
|-------------|----------|-------------|-------------------|
| id          | número   | sim         | 1                 |
| nome        | texto    | sim         | "Ana Souza"       |
| email       | texto    | sim (único) | "ana@exemplo.com" |
| senha_hash  | texto    | sim         | "$2a$10$..."      |
| fotoPerfil  | texto    | não         | "ana.png"         |
| dataCriacao | data/hora| sim         | 2025-08-20 14:30  |

#### Post

| Campo       | Tipo        | Obrigatório | Exemplo               |
|-------------|-------------|-------------|-----------------------|
| id          | número      | sim         | 2                     |
| usuario_id  | número (fk) | sim         | 1                     |
| texto       | texto       | sim         | "Estudando Java hoje" |
| dataCriacao | data/hora   | sim         | 2025-08-20 14:35      |

#### Seguidor

| Campo        | Tipo        | Obrigatório | Exemplo |
|--------------|-------------|-------------|---------|
| id           | número      | sim         | 10      |
| seguidor_id  | número (fk) | sim         | 1       |
| seguido_id   | número (fk) | sim         | 2       |

#### FraseAnonima

| Campo        | Tipo      | Obrigatório | Exemplo                   |
|--------------|-----------|-------------|---------------------------|
| id           | número    | sim         | 50                        |
| texto        | texto     | sim         | "Força que vai dar certo" |
| dataCriacao  | data/hora | sim         | 2025-08-20 15:00          |
| dataExpiracao| data/hora | sim         | 2025-08-21 15:00          |

### 9.3 Relações entre entidades

- Um Usuario tem muitos Posts (1→N).  
- Um Post pertence a um Usuario (N→1).  
- Um Usuario pode seguir muitos outros (N→N via Seguidor).  
- Uma FraseAnonima pertence apenas ao sistema (não ao usuário visível).  