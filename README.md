# 🔐 Login Service - Spring Boot + JWT + Docker Compose

Este projeto faz parte de um projeto academico .É um microserviço de autenticação desenvolvido com Spring Boot. Ele utiliza JWT (JSON Web Token) para autenticação e está pronto para ser executado com Docker Compose.
##Observação 
A base de dados necessita do serviço principal  

## ⚙️ Tecnologias Utilizadas

- Java 17
- Spring Boot 3.x
- Spring Security
- JWT (jjwt)
- MySQL (como banco de dados)
- Docker + Docker Compose

## 🚀 Como Executar

### Pré-requisitos

- Docker
- Docker Compose

### Subir o projeto

```bash
docker-compose up --build
```
A aplicação estará acessível em:

http://localhost:8080 – Serviço de autenticação e cadastro


🔐 Autenticação
A API utiliza autenticação via JWT, fornecida pelo auth-service.
Para acessar endpoints protegidos do serviço principal:

Faça login no auth-service:

POST /api/login

Utilize o token JWT retornado no header Authorization como:

css
Copiar
Editar
Authorization: Bearer {token}
📮 Endpoints da API
🔐 Microsserviço de Login (auth-service - porta 8080)
POST /api/usuarios – Criar novo usuário
json
```
{
  "nome": "teste",
  "email": "teste@gmail.com",
  "senha": "123456"
}
```
POST /api/login – Autenticar usuário
json
```
{
  "email": "teste@gmail@gmail.com",
  "senha": "123456"
}
```
Retorna um token JWT com validade de 24h.
