package cs350s22.component.ui.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** The main parser class used to interpret commands. */
public class Parser {
    private final String         commandText;
    private final A_ParserHelper parserHelper;

    /**
     * Constructor.
     *
     * @param parserHelper ...
     * @param commandText  The string of commands to be parsed.
     */
    public Parser(A_ParserHelper parserHelper, String commandText) {
        this.commandText  = commandText;
        this.parserHelper = parserHelper;
    }

    /**
     * Parse the first one or two commands/arguments passed by the calling object (set
     * via the constructor), then initialize and execute the appropriate subParser.
     */
    public void parse() throws IOException {
        if (commandText.isBlank()) {
            throw new IOException("Input was not provided");
        }

        String[] metaCommands = {"@CLOCK", "@EXIT", "@RUN", "@CONFIGURE"};
        String[] args         = commandText.replace("\"", "").split(" ");
        String    argZero     = args[0].toUpperCase();
        SubParser subParser;

        if (argZero.contains("CREATE")) {
            String argOne = args[1].toUpperCase();
            switch (argOne) {
                case "ACTUATOR" -> subParser = new ActuatorParser(args, parserHelper);
                case "MAPPER"   -> subParser = new MapperParser(args, parserHelper);
                case "REPORTER" -> subParser = new ReporterParser(args, parserHelper);
                case "SENSOR"   -> subParser = new SensorParser(args, parserHelper);
                case "WATCHDOG" -> subParser = new WatchdogParser(args, parserHelper);
                default -> {
                    System.out.println("Invalid CREATE argument: " + args[1]);
                    return;
                }
            }
        } else if (argZero.contains("SEND")) {
            subParser = new SendParser(args, parserHelper);
        } else if (Arrays.asList(metaCommands).contains(argZero)) {
            subParser = new MetaParser(args, parserHelper);
        } else if (argZero.contains("BUILD")) {
            subParser = new BuildParser(args, parserHelper);
        } else {
            System.out.println("Invalid command: " + args[0]);
            return;
        }

        subParser.parse();
    }
}
