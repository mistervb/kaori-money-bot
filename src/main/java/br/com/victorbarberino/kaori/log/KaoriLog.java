package br.com.victorbarberino.kaori.log;

import br.com.victorbarberino.kaori.config.KaoriSystem;
import br.com.victorbarberino.kaori.enums.KaoriSystemPropertiesKeys;
import br.com.victorbarberino.kaori.model.SystemProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class KaoriLog {
    private static final String BANNER_FILE_PATH = "src/main/resources/banner.txt";
    private static Logger log = LoggerFactory.getLogger(KaoriLog.class);

    public static void logBanner() {
        SystemProperties sysPd = KaoriSystem.getInstance().getSystemPropertiesInstance();
        Map<String, String> replacements = getAllReplacements(sysPd);

        try {
            String bannerContent = new String(Files.readAllBytes(Paths.get(BANNER_FILE_PATH)));
            for (Map.Entry<String, String> entry : replacements.entrySet()) {
                bannerContent = bannerContent.replace(entry.getKey(), entry.getValue());
            }
            System.out.println(bannerContent + "\n");
        } catch (IOException e) {
            log.error("Error reading banner file [banner.txt]: {}", e.getMessage());
        }
    }

    private static Map<String, String> getAllReplacements(SystemProperties sysPd) {
        Map<String, String> replacements = new HashMap<>();
        replacements.put("${" + KaoriSystemPropertiesKeys.KAORI_VERSION.getKey() + "}", sysPd.getKaoriVersion());
        replacements.put("${current.year}", String.valueOf(LocalDate.now().getYear()));

        return replacements;
    }
}
