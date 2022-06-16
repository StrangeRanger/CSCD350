package cs350s22.component.ui.parser;

import java.io.IOException;

/** Interface implemented by all sub parsers of Parser. */
public interface SubParser {
    /**
     * Method each sub parser is required to use to begin parsing their respective
     * commands.
     *
     * @throws IOException Invalid input.
     */
    void parse() throws IOException;
}
