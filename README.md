![programacao-java](https://user-images.githubusercontent.com/62728757/154475418-a971c6b3-5802-4302-8485-2f40e8022e52.jpg)

<br/>

[![Spring](https://img.shields.io/badge/-Spring-%236DB33F?logo=Spring&logoColor=%23FFF)](https://spring.io/)
[![MongoDB](https://img.shields.io/badge/-MongoDB-%2347A248?logo=MongoDB&logoColor=%23FFF)](https://www.mongodb.com/pt-br)
[![Docker](https://img.shields.io/badge/-Docker-%232496ED?logo=Docker&logoColor=%23FFF)](https://www.docker.com/)


<br/>

# ☁️ Casas de Hogwarts

CRUD para cadastro e atualização das casas de Hogwarts.

## 📚 Resources

Recurso responsável por manter os tipos de produto disponíveis proposta e apólices.

## ✔️ Houses


| METHOD | ENDPOINT | DESCRIPTION | ESCOPE |
| --- | --- | --- | --- |
| **POST** | `/hogwarts/v1/houses` | Adiciona uma nova casa. | <kbd>REQUEST</kbd>
| **PATCH** | `/hogwarts/v1/houses/{houseId}/leader` |Atualiza o nome do líder da casa.| <kbd>REQUEST</kbd>
| **PUT** | `/hogwarts/v1/houses/{houseId}` | Atualiza os dados de uma casa.| <kbd>REQUEST</kbd>
| **GET** | `/hogwarts/v1/houses` | 	Recupera uma lista de casas. | <kbd>REQUEST</kbd>
| **GET** | `/hogwarts/v1/houses/{houseId}` | Recupera os dados de uma casa específica. | <kbd>REQUEST</kbd>

<br/> 

## 📐 Arquitetura

Esse microsserviço foi estruturado usando MVC seguindo a estrutura de pastas abaixo

```
    /application
        /adapters
            /http
                /inbound
                    /controllers
                        /dto
                            /request
                            /response
                        /swagger
    /config
        /application
    /domain
        /entities
        /exception
        /services
            /interfaces
    /infrasctructure
        /database   
            /mongo    
    /utils
        /mapper
        /messages
        /pagination
```

<br/>

## ⌛️ Serviços

- ### 🌐 **HTTP**
  Esse microsserviço realiza integração via Rest 

- ### 🍃 **MongoDB**
  Esse microsserviço usa armazenamento com banco de dados não relacional com [MongoDB](https://www.mongodb.com/).


<br/>

## ⚡ Getting started


## ☕ Executar

### Executar o docker compose para subir as imagens necessárias em container docker
```
cp .env.sample .env
cd docker && docker-compose up -d
```

### Compilar o projeto
```
### Executando **local**

mvn clean install

docker build -t hogwarts-ms:master .
docker run -d -p 8080:8080 --name hogwarts-ms:master 
```
```
### Executando os **testes**

mvn test
```




