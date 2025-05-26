# Chat em Java com Sockets 🚀

## 📌 Sobre o Projeto

Este é um sistema de chat desenvolvido em **Java** utilizando **sockets**, permitindo que múltiplos clientes se conectem a um servidor e enviem mensagens entre si em tempo real. A comunicação é feita via **TCP**, garantindo confiabilidade e controle sobre as mensagens enviadas e recebidas.

O projeto implementa um servidor multi-threaded que gerencia as conexões dos clientes e um worker thread ("fofoqueiro") que distribui as mensagens de maneira eficiente.

## 🏗️ Estrutura do Projeto

```
📂 ChatJavaSockets
├── 📂 src
│   ├── 📂 client        # Implementação do Cliente
│   │   ├── Cliente.java
│   ├── 📂 server        # Implementação do Servidor
│   │   ├── LoggerConfig.java
│   │   ├── Participante.java
│   │   ├── ServicoMensagem.java
│   │   ├── Servidor.java
├── 📂 bin               # Arquivos compilados
├── Servidor.jar            # JAR executável do Servidor
├── Cliente.jar             # JAR executável do Cliente
└── README.md               # Este arquivo
```

## 🎯 Funcionalidades Implementadas

✅ **Múltiplas conexões simultâneas** – O servidor aceita vários clientes ao mesmo tempo. 

✅ **Worker Thread ("Fofoqueiro")** – Utiliza um `FixedThreadPool` para distribuir mensagens. 

✅ **Encerramento correto da aplicação** – O cliente detecta quando o servidor fecha e encerra corretamente. 

✅ **Armazenamento de participantes** – Lista concorrente para evitar problemas de sincronização. 

✅ **Logs estruturados** – Utilizando `Logger` com níveis `INFO`, `FINE` e `SEVERE`. 

✅ **Comandos Especiais** – Comandos para melhorar a experiência do usuário.

## 📥 Como Executar

### **1️⃣ Executando com JAR**

- **Servidor:**
  ```bash
  java -jar Servidor.jar
  ```
- **Cliente:**
  ```bash
  java -jar Cliente.jar <IP_SERVIDOR> <APELIDO>
  ```
  Exemplo:
  ```bash
  java -jar Cliente.jar 127.0.0.1 Joao
  ```

### **2️⃣ Executando via Código-Fonte**

1. **Compilar os arquivos:**
   ```bash
   javac -d bin src/server/*.java src/client/*.java
   ```
2. **Iniciar o servidor:**
   ```bash
   java -cp bin server.Servidor
   ```
3. **Iniciar um cliente:**
   ```bash
   java -cp bin client.Cliente 127.0.0.1 Joao
   ```

## 🔥 Comandos Especiais Disponíveis

Dentro do chat, os usuários podem utilizar comandos especiais:

| Comando     | Descrição                                   |
| ----------- | ------------------------------------------- |
| `/usuarios` | Lista todos os usuários conectados ao chat. |
| `/limpar`   | Limpa a tela do terminal.                   |
| `/ajuda`    | Exibe a lista de comandos disponíveis.      |
| `##sair##`  | Sai do chat e desconecta do servidor.       |

## ✨ Exemplo de Funcionamento do Chat

### **1️⃣ Iniciando o Servidor**
   ```bash
   java -jar Servidor.jar
   ```
   **Saída esperada no console do servidor:**
   ```bash
   Servidor iniciado na porta 50123
   Aguardando conexões de clientes...
   ```

### **2️⃣ Conectando Clientes**
   ```bash
   java -jar Cliente.jar 127.0.0.1 Joao
   java -jar Cliente.jar 127.0.0.1 Maria
   java -jar Cliente.jar 127.0.0.1 Pedro
   ```
   **Saída esperada no chat dos clientes**
   ```bash
   Joao entrou no chat.
   Maria entrou no chat.
   Pedro entrou no chat.
   ```

### **3️⃣ Enviando Mensagens**
   **Joao envia:**
   ```bash
   Oi pessoal!
   ```
   **Maria e Pedro veem:**
   ```bash
   17/02/2025 19:10 (Joao) - Oi pessoal!
   ```

### **4️⃣ Listando Usuários Conectados**
   **Maria digita `/usuarios` e vê:**
   ```bash
   Usuários conectados:
   - Joao
   - Maria
   - Pedro
   ```

### **4️⃣ Limpando o Terminal**
  **João digita `/limpar`, e o terminal é limpo.**

### **4️⃣ Saindo do Chat**
   **Pedro digita `##sair##`, e os outros veem:**
   ```bash
   Pedro saiu do chat.
   ```

## 📜 Logs no Servidor

O servidor exibe logs detalhados para depuração e monitoramento:

- **INFO** – Eventos principais (entrada/saída de clientes, mensagens enviadas)
- **FINE** – Logs detalhados para depuração
- **SEVERE** – Erros críticos

## 📦 Empacotamento do Projeto em JAR

Para criar os arquivos `.jar`, siga os passos:

```bash
javac -d bin src/server/*.java src/client/*.java
jar cfe Servidor.jar server.Servidor -C bin .
jar cfe Cliente.jar client.Cliente -C bin .
```

## 📌 Conclusão

Este projeto implementa um **chat funcional em Java utilizando sockets**, garantindo concorrência, estabilidade e interatividade para os usuários. Ele segue as melhores práticas de programação concorrente e estruturação de código. 🚀

📌 **Criado para a disciplina de Fundamentos de Programação Concorrente.**

