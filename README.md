# APS-04-Lanchonete-Online-em-Java

### Passos para executar

1. **Clone o repositório:**

   ```bash
   git clone <url-do-repositorio>
   cd APS-04-Lanchonete-Online-em-Java
   ```

2. **Suba os containers com Docker Compose:**

   ```bash
   docker-compose up --build -d
   ```

   Isso irá criar e iniciar os containers do banco de dados PostgreSQL e do servidor Tomcat com a aplicação.

3. **Acesse a aplicação:**

   - API: [http://localhost:8080](http://localhost:8080)

4. **Para parar e remover tudo:**

   ```bash
   docker-compose down -v
   ```

### Observações
- O banco de dados será inicializado automaticamente com as tabelas necessárias e o usuário de admin para login.
- Caso precise alterar configurações, edite os arquivos `docker-compose.yml` ou `banco.sql`.
- Se houver problemas de cache no navegador, utilize Ctrl+F5 ou limpe o cache manualmente.


### Testes
- Link para o Plano de Testes: https://docs.google.com/document/d/1XyHxbNJgMFJG76MgUY0vU-eWYDfmVbzAZv1eNe0G7No/edit?usp=sharing
- Link para os testes manuais: https://docs.google.com/document/d/1MWN0Kdl-o1lq6h9GUeLx_ytgldcQYsEqpFnZy_02nqw/edit?usp=sharing
- Link para a ferramenta de registro dos testes: https://codeburgers.testmo.net/runs/1 
- Link para casos de teste: https://docs.google.com/document/d/1dujNiqBl8LvU-J3ZCebb3JcYdp0hdQ3gIZPoI6AeVmc/edit?usp=sharing
- Link para o repositório forkado com o projeto original: https://github.com/lucasbogado-qa/APS-04-Lanchonete-Online-em-Java.git
- Link para o documento da Entrega 2: https://docs.google.com/document/d/1qrHQxUQhWTA5-kWVUBvzyp99cH1zmqBtfYL4RjJRV1k/edit?tab=t.0
- Configurações
    - Será necessário Java8+, Maven e Docker para executar o projeto. A nível dos testes unitários, utilizamos o JUnit para desenvolver e executar os casos de Teste.

###Prints da plataforma de gestão dos registros de testes:
<img width="1902" height="942" alt="image" src="https://github.com/user-attachments/assets/ffd06467-70de-4d80-b4d0-9acfefc3ba83" />
<img width="1917" height="939" alt="image" src="https://github.com/user-attachments/assets/f8a34460-c579-4bfc-b0ce-2257c761e9ff" />
<img width="1919" height="941" alt="image" src="https://github.com/user-attachments/assets/2ab77c5b-6ac6-4219-95bc-d3868112e508" />
<img width="1911" height="944" alt="image" src="https://github.com/user-attachments/assets/23d3a30d-c403-46f5-8a98-04a9450c2919" />

- Arquivos de teste implementados:
   - CadastroTest.java
   - ComprarTest.Java
   - GetRelatorioLanchesTest.java
   - PedidoTest.java
   - SalvarBebidaTest.java
   - SalvarLancheTest.java
   -ValidarTokenTest.java
   
- Gestão de bugs:
   Utilizamos o github issues
   
- Casos de teste:
   Utilizamos o testmo (conforme link acima) e além disso, um arquivo de texto também anexado acima.
   
- Plano de teste:
   Arquivo de texto anexado acima

## Sobre
Com o objetivo de desenvolver a capacidade dos alunos e obter nota na disciplina APS (Atividades Práticas Supervisionadas), 
foi proposto um projeto de desenvolvimento de um sistema para uma lanchonete online, onde o administrador consiga controlar 
os pedidos da lanchonete e emitir relatórios. A lanchonete devera permitir o cadastro dos usuários, para que eles possam realizar seus pedidos, 
e o cadastro de produtos, que ficariam por parte do administrador. Após o cadastro,  cliente poderá utilizar os ingredientes cadastrados para 
criar seu lanche personalizado. O sistema deverá fazer o controle dos pedidos de forma que agrade os clientes, e controlar tambem o estoque de produtos.

## Tecnologias Utilizadas

O Sistema funciona com base em um Frontend Utilizando HTML 5, CSS3 e JavaScript, e um Backend baseado em Java Web utilizando-se do Servidor Glassfish 4 
e muito baseado no uso de Servlets para a Comunicação atraves de requisições. Além disso o Sistema utiliza das Bibliotecas gson-2.8.6 e json-20200518 
Para a manipulação de Arquivos JSON dentro do Código Java, e de um Banco de Dados PostgreSQL, do qual o Código base também se encontra no repositório.

## Alguns Screenshots

![alt text](https://i.ibb.co/BPn99jW/248f5162-df3a-4754-8ade-82b9784f94d8.jpg)
![alt text](https://i.ibb.co/GM3r7Dd/daf6e1f9-676e-4a27-9669-80036dc52cce.jpg)
![alt text](https://i.ibb.co/kXdFFq5/e378bda9-bcc8-4483-bb2f-f2143a79817e.jpg)
![alt text](https://i.ibb.co/z7kqx4x/a5a0e3f3-3605-4d3f-b2ba-f54c2ef76f18.jpg)
![alt text](https://i.ibb.co/C6kMZLW/c1bad7f9-c79a-4516-9d08-bc2548ee9880.jpg)
![alt text](https://i.ibb.co/2321674/8a74fb26-1db0-49df-b2d7-2479d0567a4e.jpg)
![alt text](https://i.ibb.co/2YSbvGZ/8d3386e3-d13b-4a42-b389-151fbadb1d77.jpg)


### Pré-requisitos
- [Docker](https://www.docker.com/get-started) e [Docker Compose](https://docs.docker.com/compose/install/)
- [Java 8+](https://adoptopenjdk.net/) e [Maven](https://maven.apache.org/)


## Execução dos testes de integração 
- Clonar esse repositório
- Abrir o terminal e executar o comando cd trabalho-qualidade-e-teste-qa-raiders
- Executar o comando docker compose up -d
- Abrir o Postman e importar a collection. Arquivo json Lanchonete Online - Testes de Integração.postman_collection.json
- Rodar os testes no Postman
