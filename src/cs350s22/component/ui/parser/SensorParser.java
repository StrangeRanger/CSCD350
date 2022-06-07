package cs350s22.component.ui.parser;

/**
 *
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
