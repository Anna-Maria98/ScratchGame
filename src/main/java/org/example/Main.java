package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import org.example.game.ConfigInput;
import org.example.game.GameOutput;
import org.example.game.GamePlay;

public class Main {

    public static void main(String[] args) throws IOException {
        String configPath = null;
        Double betAmount = null;
        int i = 0;
        while (i < args.length) {
            if ("--config".equals(args[i]) && i + 1 < args.length) {
                configPath = args[++i];
            } else if ("--betting-amount".equals(args[i]) && i + 1 < args.length) {
                betAmount = Double.parseDouble(args[++i]);
            }
            i++;
        }
        if (configPath == null || betAmount == null) {
            Logger.getLogger("Usage: java -jar <jar-file> --config <config.json> --betting-amount <amount>");
            System.exit(1);
        }

        ObjectMapper fileMapper = new ObjectMapper();
        ConfigInput config = fileMapper.readValue(new File(configPath), ConfigInput.class);

        GamePlay game = new GamePlay(config);
        GameOutput result = game.play(betAmount);

        ObjectMapper jsonMapper = new ObjectMapper();
        System.out.println(jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result));
    }
}