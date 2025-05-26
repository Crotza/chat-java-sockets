package server;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServicoMensagem implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(ServicoMensagem.class.getName());
    private String apelido;
    private String texto;
    private List<Participante> participantes;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public ServicoMensagem(String apelido, String texto, List<Participante> participantes) {
        this.apelido = apelido;
        this.texto = texto;
        this.participantes = participantes;
    }

    @Override
    public void run() {
        if (participantes.isEmpty()) {
            LOGGER.log(Level.WARNING, "Nenhum participante disponivel para receber a mensagem.");
            return;
        }

        String timestamp = DATE_FORMAT.format(new Date());
        String mensagemFormatada = String.format("[CHAT] %s (%s) - %s", timestamp, apelido, texto);
        LOGGER.log(Level.FINE, "Mensagem formatada para envio: {0}", mensagemFormatada);

        // Envia a mensagem para todos os participantes conectados de forma segura
        synchronized (participantes) {
            for (Participante participante : participantes) {
                participante.enviarMensagem(mensagemFormatada);
            }
        }

    }
}
