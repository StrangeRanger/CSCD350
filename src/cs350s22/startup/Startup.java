package cs350s22.startup;

import cs350s22.component.ui.parser.A_ParserHelper;
import cs350s22.component.ui.parser.Parser;
import cs350s22.component.ui.parser.ParserHelper;

/** Main class. */
public class Startup {
    private final A_ParserHelper _parserHelper = new ParserHelper();

    /** Main method. */
    public static void main(final String[] arguments) throws Exception {
        Startup startup = new Startup();

        // This command must come first. The filenames do not matter here.
        startup.parse("@CONFIGURE LOG \"a.txt\" DOT SEQUENCE \"b.txt\" NETWORK "
                      + "\"c.txt\" XML \"d.txt\"");

        // Run your tests like this.
        startup.parse("@exit");
    }

    /**
     * Begin the process of parsing the command string.
     *
     * @param parse      Command string to be parsed.
     * @throws Exception Invalid input.
     */
    private void parse(final String parse) throws Exception {
        System.out.println("PARSE> " + parse);

        Parser parser = new Parser(_parserHelper, parse);

        parser.parse();
    }
}
