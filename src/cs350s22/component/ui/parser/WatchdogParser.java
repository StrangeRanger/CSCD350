package cs350s22.component.ui.parser;

/**
 * Watchdog commands are responsible for monitoring the value of a sensor and reporting
 * whether constraints are violated.
 */
public class WatchdogParser implements SubParser {
    private final String[]       args;
    private final A_ParserHelper parserHelper;

    /** Constructor. */
    public WatchdogParser(String[] args, A_ParserHelper parserHelper) {
        this.args         = args;
        this.parserHelper = parserHelper;
    }

    @Override
    public void parse() { }
}
