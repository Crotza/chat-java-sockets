package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cliente {
    private static final Logger LOGGER = Logger.getLogger(Cliente.class.getName());
    private String servidorIP;
    private int porta = 50123;
    private String apelido;
    private volatile boolean conectado = true;

    public Cliente(String servidorIP, String apelido) {
        this.servidorIP = servidorIP;
        this.apelido = apelido;
    }

    // Inicia a conexao do cliente com o servidor
    public void iniciar() {
        try (Socket socket = new Socket(servidorIP, porta);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            LOGGER.log(Level.INFO, "Conectado ao servidor {0}:{1}", new Object[]{servidorIP, porta});
            out.println(apelido);
            LOGGER.log(Level.FINE, "Apelido enviado ao servidor: {0}", apelido);

            // Thread para receber mensagens do servidor
            Thread threadRecebeMensagens = new Thread(() -> {
                try {
                    String mensagemRecebida;
                    while ((mensagemRecebida = in.readLine()) != null) {
                        mensagemRecebida = mensagemRecebida.trim();

                        // Se a mensagem for vazia, ignora
                        if (mensagemRecebida.trim().isEmpty()) continue;

                        // Mensagem do chat -> Exibir diretamente no console, removendo o prefixo "[CHAT] "
                        if (mensagemRecebida.startsWith("[CHAT]")) {
                            System.out.println(mensagemRecebida.substring(7));
                            continue;
                        }

                        // Se for uma mensagem de lista de usuarios, exibir direto no terminal
                        if (mensagemRecebida.startsWith("Usuarios conectados:") || mensagemRecebida.startsWith("- ")) {
                            System.out.println("\n" + mensagemRecebida);
                            continue;
                        }

                        // Qualquer outra mensagem vai para o logger
                        else { LOGGER.log(Level.INFO, "{0}", mensagemRecebida); }
                    }
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Conexao com o servidor perdida. Cliente sera encerrado.");
                    conectado = false;
                }
            });
            threadRecebeMensagens.start();

            // Loop para enviar mensagens digitadas pelo usuario
            String mensagem;
            while (scanner.hasNextLine() && conectado) {
                mensagem = scanner.nextLine().trim();
                if (mensagem.isEmpty()) continue;
                processarComando(mensagem, out);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erro na conexao com o servidor", e);
        } finally {
            LOGGER.log(Level.INFO, "Cliente desconectado. Encerrando aplicacao.");
            System.exit(0);
        }
    }

    private void processarComando(String mensagem, PrintWriter out) {
        switch (mensagem.toLowerCase()) {
            case "/usuarios":
                LOGGER.log(Level.INFO, "Solicitando lista de usuarios...");
                out.println(mensagem);
                break;
            case "/limpar":
                LOGGER.log(Level.INFO, "Limpando tela do terminal...");
                System.out.print("\033[H\033[2J");
                System.out.flush();
                break;
            case "/ajuda":
                LOGGER.log(Level.INFO, "Mostrando lista de comandos disponiveis...");
                System.out.println("\nComandos disponiveis:");
                System.out.println("/usuarios - Lista todos os usuarios conectados.");
                System.out.println("/limpar - Limpa a tela do terminal.");
                System.out.println("/ajuda - Mostra esta lista de comandos.");
                System.out.println("##sair## - Sai do chat.\n");
                break;
            default:
                LOGGER.log(Level.FINE, "Enviando mensagem: {0}", mensagem);
                out.println(mensagem);
                if ("##sair##".equalsIgnoreCase(mensagem)) {
                    LOGGER.log(Level.INFO, "{0} saiu do chat.", apelido);
                    conectado = false;
                }
        }
    }

    // Inicia o cliente
    public static void main(String[] args) {
        if (args.length < 2) {
            LOGGER.log(Level.SEVERE, "Uso: java Cliente <servidorIP> <apelido>");
            return;
        }

        Cliente cliente = new Cliente(args[0], args[1]);
        cliente.iniciar();
    }
}
