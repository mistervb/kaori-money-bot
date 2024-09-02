package br.com.victorbarberino.kaori.notify;

import br.com.victorbarberino.kaori.model.PropertiesData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WhatsappNotifier {
    private static final Logger log = LoggerFactory.getLogger(WhatsappNotifier.class);
    private static final String API_URL = "https://graph.facebook.com/v20.0/${phone.id}/messages";

    public static void sendMessage(PropertiesData pd, String message) {
        try {
            String whatsappAPIUrl = API_URL.replace("${phone.id}", pd.getWhatsappBotIdNumber());
            URL url = new URL(whatsappAPIUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + pd.getWhatsappAuthToken());
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = "{"
                    + "\"messaging_product\": \"whatsapp\","
                    + "\"to\": \"" + pd.getWhatsappMyNumber() + "\","
                    + "\"type\": \"text\","
                    + "\"text\": {"
                    + "  \"body\": \"" + message + "\""
                    + "}, "
                    + "\"language\": { \"code\": \"pt_BR\" } }"
                    + "}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = conn.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                log.info("[WHATSAPP NOTFIER] - Mensagem de oportunidade enviada para o whatsapp do usuário!");
            } else {
                log.error("[WHATSAPP NOTFIER] - Failed to send message. Response code: " + code);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
