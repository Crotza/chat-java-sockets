package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor {
    private static final int PORTA = 50123;
    private static final Logger LOGGER = Logger.getLogger(Servidor.class.getName());

    // Lista concorrente para armazenar os participantes conectados
    private static List<Participante> participantes = new CopyOnWriteArrayList<>();

    // Pool de threads para envio de mensagens (Worker Thread "fofoqueiro")
    private static ExecutorService fofoqueiro = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        LoggerConfig.configureLogger(LOGGER);
        LOGGER.log(Level.INFO, "Servidor iniciado na porta {0}", PORTA);

        try (ServerSocket serverSocket = new ServerSocket(PORTA)) {
            LOGGER.log(Level.FINE, "Aguardando conexões de clientes...");
            while (true) {
                // Aceita conexão de um novo cliente
                Socket socket = serverSocket.accept();
                LOGGER.log(Level.FINE, "Cliente conectado: {0}", socket.getInetAddress());

                // Cria um novo participante e adiciona à lista de participantes
                Participante participante = new Participante(socket, participantes, fofoqueiro);
                participantes.add(participante);

                // Inicia uma nova thread para gerenciar o participante
                new Thread(participante).start();

                // Log interno para monitoramento
                LOGGER.log(Level.INFO, "Novo participante conectado: {0}:{1}. Total de participantes: {2}",
                        new Object[]{socket.getInetAddress(), PORTA, participantes.size()});
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erro ao iniciar o servidor", e);
        }
    }

    // Retornar a lista de usuarios conectados
    public static String listarUsuarios() {
        if (participantes.isEmpty()) {
            return "Nenhum usuario conectado.";
        }
        StringBuilder usuarios = new StringBuilder("Usuarios conectados:\n");
        for (Participante participante : participantes) {
            usuarios.append("- ").append(participante.getApelido()).append("\n");
        }
        return usuarios.toString();
    }

    // Enviar mensagens para todos os participantes
    private static void enviarMensagemParaTodos(String mensagem) {
        synchronized (participantes) {
            for (Participante participante : participantes) {
                participante.enviarMensagem(mensagem);
            }
        }
    }
}
