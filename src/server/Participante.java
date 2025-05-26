package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Participante implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(Participante.class.getName());
    private Socket socket;
    private List<Participante> participantes;
    private ExecutorService fofoqueiro;
    private PrintWriter out;
    private BufferedReader in;
    private String apelido;

    public Participante(Socket socket, List<Participante> participantes, ExecutorService fofoqueiro) {
        this.socket = socket;
        this.participantes = participantes;
        this.fofoqueiro = fofoqueiro;
    }

    @Override
    public void run() {
        try {
            // Inicializa os fluxos de entrada e saída para comunicacao com o cliente
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Le o apelido do participante
            apelido = in.readLine();

            // Se o apelido for null ou vazio, nao adiciona o usuario ao chat
            if (apelido == null || apelido.trim().isEmpty()) {
                LOGGER.log(Level.WARNING, "Falha ao receber apelido do participante.");
                return;
            }

            // Garante que so mensagens validas sao enviadas para os clientes
            String mensagemEntrada = "[CHAT] " + apelido + " entrou no chat.";
            enviarMensagemParaTodos(mensagemEntrada);

            LOGGER.log(Level.INFO, "{0} entrou no chat.", apelido);
            LOGGER.log(Level.FINE, "Participante {0} conectado de {1}", new Object[]{apelido, socket.getInetAddress()});

            String mensagem;
            while ((mensagem = in.readLine()) != null) {
                mensagem = mensagem.trim();
                if (mensagem.isEmpty()) { continue; }

                LOGGER.log(Level.FINE, "Mensagem recebida de {0}: {1}", new Object[]{apelido, mensagem});

                // Se o usuario solicitar a lista de participantes, envia a resposta
                if (mensagem.equalsIgnoreCase("/usuarios")) {
                    out.println(Servidor.listarUsuarios());
                    continue;
                }

                if (mensagem.equalsIgnoreCase("##sair##")) {
                    LOGGER.log(Level.INFO, "{0} solicitou saida do chat.", apelido);
                    break;
                }

                // Envia a mensagem para o worker thread "fofoqueiro" para ser distribuida
                fofoqueiro.execute(new ServicoMensagem(apelido, mensagem, participantes));
            }
        } catch (IOException e) { LOGGER.log(Level.SEVERE, "Erro na comunicacao com {0}", apelido); }
        finally {
            encerrarConexao();
        }
    }

    public void enviarMensagem(String mensagem) { out.println(mensagem); }

    private void enviarMensagemParaTodos(String mensagem) {
        synchronized (participantes) {
            for (Participante participante : participantes) {
                participante.enviarMensagem(mensagem);
            }
        }
    }

    public String getApelido() {
        return apelido;
    }

    private void encerrarConexao() {
        try {
            socket.close();
            LOGGER.log(Level.FINE, "Conexão fechada para {0}", apelido);
        } catch (IOException e) { LOGGER.log(Level.SEVERE, "Erro ao fechar conexão de {0}", apelido); }

        participantes.remove(this);
        LOGGER.log(Level.INFO, "{0} saiu do chat. Total de participantes: {1}", new Object[]{apelido, participantes.size()});
        enviarMensagemParaTodos("[CHAT] " + apelido + " saiu do chat.");
    }
}
