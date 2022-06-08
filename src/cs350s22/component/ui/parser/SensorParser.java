package cs350s22.component.ui.parser;

/**
 * The sensor command is responsible for creating a sensor with optional reporters,
 * optional watchdogs, and an optional mapper.
 */
public class SensorParser implements SubParser {
    private final String[]       args;
    private final A_ParserHelper parserHelper;

    /** Constructor. */
    public SensorParser(String[] args, A_ParserHelper parserHelper) {
        this.args         = args;
        this.parserHelper = parserHelper;
    }

    @Override
    public void parse() { }
}
