package cs350s22.component.ui.parser;

import java.io.IOException;
import java.util.Arrays;

/** The main parser class used to interpret commands. */
public class Parser {
    private final String         commandText;
    private final A_ParserHelper parserHelper;

    /**
     * Constructor.
     *
     * @param parserHelper Object to be used be each sub parser.
     * @param commandText  The string of commands to be parsed.
     */
    public Parser(A_ParserHelper parserHelper, String commandText) {
        this.commandText  = commandText;
        this.parserHelper = parserHelper;
    }

    /**
     * Parse the first one or two commands/arguments passed by the calling object (set
     * via the constructor), then initialize and execute the appropriate subParser.
     *
     * @throws IOException Invalid input.
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
                default -> throw new IOException("Invalid CREATE argument: " + args[0]);
            }
        } else if (argZero.contains("SEND")) {
            subParser = new SendParser(args, parserHelper);
        } else if (Arrays.asList(metaCommands).contains(argZero)) {
            subParser = new MetaParser(args, parserHelper);
        } else if (argZero.contains("BUILD")) {
            subParser = new BuildParser(args, parserHelper);
        } else {
            throw new IOException("Invalid command: " + args[0]);
        }

        subParser.parse();
    }
}
