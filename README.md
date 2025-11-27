# 🧠 Smart Todo AI
### Gerenciamento de Tarefas Inteligente com Spring Boot & Google Gemini

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.5-brightgreen?style=for-the-badge&logo=spring-boot)
![MySQL](https://img.shields.io/badge/mysql-%2300f.svg?style=for-the-badge&logo=mysql&logoColor=white)
![Google Gemini](https://img.shields.io/badge/Google%20Gemini-8E75B2?style=for-the-badge&logo=google&logoColor=white)
![License](https://img.shields.io/badge/license-MIT-blue?style=for-the-badge)

---

## 📖 Índice
- [Sobre o Projeto](#-sobre-o-projeto)
- [Funcionalidades](#-funcionalidades)
- [Arquitetura](#-arquitetura)
- [Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [Pré-requisitos](#-pré-requisitos)
- [Instalação e Configuração](#-instalação-e-configuração)
- [Como Usar](#-como-usar)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Roadmap](#-roadmap)
- [Contribuição](#-contribuição)
- [Licença](#-licença)

---

## 💡 Sobre o Projeto

O **Smart Todo AI** não é apenas mais uma lista de tarefas. É uma plataforma full-stack projetada para aumentar a produtividade através da inteligência artificial.

Diferente de CRUDs tradicionais, este sistema utiliza o **Google Vertex AI (Gemini)** para analisar o contexto da tarefa criada pelo usuário. O sistema define automaticamente o nível de **prioridade** e sugere uma **data de conclusão** ideal baseada na urgência do texto digitado.

Além disso, possui uma interface moderna com Dark Mode, visualização estilo Kanban e um Chatbot integrado para auxiliar na organização pessoal.

---

## 🚀 Funcionalidades

- **🤖 Criação Inteligente:** A IA analisa o texto (ex: "Estudar para a prova de amanhã") e define Prioridade (ALTA) e Data (D+1) automaticamente.
- **🔐 Autenticação Segura:** Sistema completo de Login e Registro com **JWT (JSON Web Token)** e Spring Security.
- **📊 Dashboard Kanban:** Visualização de tarefas por status (Draft, In Progress, Done) com arrastar e soltar (visual).
- **💬 Chatbot AI:** Assistente virtual integrado para tirar dúvidas e ajudar no planejamento.
- **🎨 UI Moderna:** Interface responsiva, Dark Mode nativo e feedback visual de carregamento.
- **🔍 Busca Instantânea:** Filtragem de tarefas em tempo real no frontend.

---

## 🏗 Arquitetura

O projeto segue uma arquitetura monolítica em camadas, servindo tanto a API REST quanto os recursos estáticos.



---

## 🛠 Tecnologias Utilizadas

### Backend
- **Java 17** (LTS)
- **Spring Boot 3.5.5** (Web, Security, Data JPA, Validation)
- **Spring AI 1.0.0-M1** (Integração com Vertex AI/Gemini)
- **MySQL 8** (Persistência de dados)
- **Flyway** (Versionamento e Migração de Banco de Dados)
- **JWT (jjwt 0.12.5)** (Autenticação Stateless)
- **Lombok** (Redução de boilerplate)

### Frontend
- **HTML5 / CSS3** (Variáveis CSS, Flexbox, Grid)
- **JavaScript (ES6+)** (Fetch API, DOM Manipulation)
- **Feather Icons** (Ícones leves)
- **Fonte Inter** (Google Fonts)

---

## 📋 Pré-requisitos

Antes de começar, você precisará ter instalado em sua máquina:

1.  **Java JDK 17+**
2.  **Maven 3.8+**
3.  **MySQL 8.0+**
4.  **Google Cloud SDK (gcloud)** (Para autenticação com a Vertex AI)
5.  **Git**

---

## 🔧 Instalação e Configuração

### 1. Clone o Repositório
```bash
git clone [https://github.com/SEU-USUARIO/smart-todo-spring.git](https://github.com/SEU-USUARIO/smart-todo-spring.git)
cd smart-todo-spring