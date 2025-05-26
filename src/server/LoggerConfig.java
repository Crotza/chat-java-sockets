/*
 * Niveis de Log:
 * - SEVERE: Exibe erros criticos que podem causar falhas na aplicacao.
 *   Exemplo: Erro ao iniciar o servidor, falha na conexao com o cliente.
 *
 * - WARNING: Indica situacoes inesperadas que nao causam falha, mas podem ser um problema.
 *   Exemplo: Nenhum participante disponivel para receber mensagens.
 *
 * - INFO: Exibe informacoes importantes sobre eventos normais da aplicacao.
 *   Exemplo: Cliente conectado, usuario saiu do chat.
 *
 * - FINE: Detalha eventos uteis para debug durante o desenvolvimento.
 *   Exemplo: Mensagem recebida e enviada, acoes internas do servidor.
 *
 * - ALL: Captura todos os niveis de log, util para depuracao completa.
*/
package server;

import java.io.File;
import java.io.IOException;
import java.util.logging.*;

public class LoggerConfig {
    // Define o nivel de log para FINE durante o desenvolvimento para capturar logs detalhados.
    // Para producao, alterar para INFO para reduzir a quantidade de logs.
    private static final Level NIVEL_LOG = Level.INFO;

    public static void configureLogger(Logger logger) {
        logger.setUseParentHandlers(false);

        // Criar diretorio logs se nao existir
        File logDir = new File("logs");
        if (!logDir.exists() && logDir.mkdirs()) { System.out.println("DiretOrio 'logs' criado."); }

        // Configurar saIda no console
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(NIVEL_LOG);
        consoleHandler.setFormatter(new SimpleFormatter());
        logger.addHandler(consoleHandler);

        try {
            // Configurar saIda para arquivo de log
            FileHandler fileHandler = new FileHandler("logs/server.log", true);
            fileHandler.setFormatter(new SimpleFormatter());

            // Registra todos os niveis no arquivo de log
            fileHandler.setLevel(Level.ALL);

            logger.addHandler(fileHandler);
        } catch (IOException e) {
            System.err.println("Erro ao configurar o logger: " + e.getMessage());
            logger.log(Level.SEVERE, "Erro ao configurar o logger", e);
        }
    }
}
