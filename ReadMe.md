# Java Chat with Sockets 🚀

## 📌 About the Project

This is a chat system developed in **Java** using **sockets**, allowing multiple clients to connect to a server and exchange messages in real time. Communication is handled via **TCP**, ensuring reliability and control over message transmission and delivery.

The project implements a multi-threaded server that manages client connections and a worker thread (nicknamed "Fofoqueiro", or "gossiper") that efficiently distributes messages.

## 🏗️ Project Structure

```
📂 ChatJavaSockets
├── 📂 src
│   ├── 📂 client        # Client implementation
│   │   ├── Client.java
│   ├── 📂 server        # Server implementation
│   │   ├── LoggerConfig.java
│   │   ├── Participant.java
│   │   ├── MessageService.java
│   │   ├── ChatServer.java
├── 📂 bin               # Compiled classes
├── ChatServer.jar              # Executable JAR for the server
├── Client.jar              # Executable JAR for the client
└── README.md               # This file
```

## 🎯 Implemented Features

✅ **Multiple simultaneous connections** – The server supports multiple clients concurrently.

✅ **Worker Thread ("Gossiper")** – Uses a `FixedThreadPool` to distribute messages.

✅ **Proper application shutdown** – Clients detect when the server goes offline and terminate gracefully.

✅ **Participant list storage** – Uses a concurrent-safe list to avoid synchronization issues.

✅ **Structured logging** – Uses `Logger` with `INFO`, `FINE`, and `SEVERE` levels.

✅ **Special Commands** – Useful commands to enhance user experience.

## 📥 How to Run

### **1️⃣ Running with JAR**

- **Server:**
  ```bash
  java -jar ChatServer.jar
  ```

- **Client:**
  ```bash
  java -jar Client.jar <SERVER_IP> <NICKNAME>
  ```
  Example:
  ```bash
  java -jar Client.jar 127.0.0.1 Joao
  ```

### **2️⃣ Running from Source Code**

1. **Compile the source files:**
   ```bash
   javac -d bin src/server/*.java src/client/*.java
   ```

2. **Start the server:**
   ```bash
   java -cp bin server.ChatServer
   ```

3. **Start a client:**
   ```bash
   java -cp bin client.Client 127.0.0.1 Joao
   ```

## 🔥 Available Special Commands

Inside the chat, users can use the following commands:

| Command       | Description                                      |
| ------------- | ------------------------------------------------ |
| `/users`   | Lists all currently connected users.             |
| `/clear`     | Clears the terminal screen.                      |
| `/help`      | Shows the list of available commands.            |
| `##quit##`    | Exits the chat and disconnects from the server.  |

## ✨ Example Chat Output

### **1️⃣ Starting the Server**
```bash
java -jar ChatServer.jar
```
**Expected server output:**
```bash
Server started on port 50123
Waiting for client connections...
```

### **2️⃣ Connecting Clients**
```bash
java -jar Client.jar 127.0.0.1 Joao
java -jar Client.jar 127.0.0.1 Maria
java -jar Client.jar 127.0.0.1 Pedro
```
**Expected chat output:**
```bash
Joao joined the chat.
Maria joined the chat.
Pedro joined the chat.
```

### **3️⃣ Sending Messages**
**Joao types:**
```bash
Hello everyone!
```
**Maria and Pedro see:**
```bash
17/02/2025 19:10 (Joao) - Hello everyone!
```

### **4️⃣ Listing Connected Users**
**Maria types `/usuarios` and sees:**
```bash
Connected users:
- Joao
- Maria
- Pedro
```

### **5️⃣ Clearing the Terminal**
**Joao types `/limpar` and his terminal is cleared.**

### **6️⃣ Leaving the Chat**
**Pedro types `##sair##` and the others see:**
```bash
Pedro left the chat.
```

## 📜 Server Logging

The server produces detailed logs for debugging and monitoring:

- **INFO** – Main events (user join/leave, messages sent)
- **FINE** – Debug-level details
- **SEVERE** – Critical errors

## 📦 Packaging the Project as JAR

To generate the `.jar` files, use:

```bash
javac -d bin src/server/*.java src/client/*.java
jar cfe Server.jar server.Server -C bin .
jar cfe Client.jar client.Client -C bin .
```

## 📌 Conclusion

This project demonstrates a fully functional **Java socket-based chat application**, ensuring concurrency, stability, and real-time communication between users. It follows best practices in concurrent programming and clean code design. 🚀

📌 **Built for the Concurrent Programming Fundamentals course.**