package cs350s22.component.ui.parser;

import java.io.IOException;

/**
 *
 */
public interface SubParser {
    /**
     *
     * @param commandTextSplit
     * @param parserHelper
     */
    void parse(String[] commandTextSplit, A_ParserHelper parserHelper)
            throws IOException;
}
