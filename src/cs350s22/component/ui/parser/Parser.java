package cs350s22.component.ui.parser;

import java.io.IOException;
import java.util.Arrays;

public class Parser {
    private String commandText;
    public Parser(A_ParserHelper parserHelper, String commandText) {
        this.commandText = commandText;
    }

    public void parse() throws IOException {
        String[] metaCommands     = {"@CLOCK", "@EXIT", "@RUN", "@CONFIGURE"};
        String[] commandTextSplit = commandText.split(" ");
        String argZero            = commandTextSplit[0].toUpperCase();

        if (argZero.contains("CREATE")) {
            String argOne = commandTextSplit[1].toUpperCase();

            if (argOne.contains("ACTUATOR")) {
                ActuatorParser actuatorParser = new ActuatorParser(commandTextSplit);
                // System.out.println("CREATE ACTUATOR");
            } else if (argOne.contains("MAPPER")) {
                MapperParser mapperParser = new MapperParser(commandTextSplit);
                // System.out.println("CREATE MAPPER");
            } else if (argOne.contains("REPORTER")) {
                ReporterParser reporterParser = new ReporterParser(commandTextSplit);
                // System.out.println("CREATE REPORTER");
            } else if (argOne.contains("SENSOR")) {
                SensorParser sensorParser = new SensorParser(commandTextSplit);
                // System.out.println("CREATE SENSOR");
            } else if (argOne.contains("WATCHDOG")) {
                WatchdogParser watchdogParser = new WatchdogParser(commandTextSplit);
                // System.out.println("CREATE WATCHDOG");
            }
        } else if (argZero.contains("SEND")) {
            SendParser sendParser = new SendParser(commandTextSplit);
            // System.out.println("SEND");
        } else if (Arrays.asList(metaCommands).contains(argZero)) {
            MetaParser metaParser = new MetaParser(commandTextSplit);
            // System.out.println("META");
        } else if (argZero.contains("BUILD")) {
            BuildParser buildParser = new BuildParser(commandTextSplit);
            // System.out.println("BUILD");
        } else {
            System.out.println("Invalid command provided");
        }
    }
}
