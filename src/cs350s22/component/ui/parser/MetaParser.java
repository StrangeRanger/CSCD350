package cs350s22.component.ui.parser;

import java.io.IOException;

/**
 *
 */
public class MetaParser implements SubParser {
    String[] commandTextSplit;
    A_ParserHelper parserHelper;

    @Override
    public void parse(String[] commandTextSplit, A_ParserHelper parserHelper) {
        this.commandTextSplit = commandTextSplit;
        this.parserHelper     = parserHelper;

        switch (commandTextSplit[0].toUpperCase()) {
            case "@CLOCK":
                setClockHelper();
                break;
            case "@EXIT":
                // TODO: Currently produces a not initialized error. Fix?
                parserHelper.exit();
                System.exit(0);
                break;
            case "@RUN":
                try {
                    parserHelper.run(commandTextSplit[1]);
                } catch (ParseException | IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "@CONFIGURE":
                configure();
                break;
            default:
                System.out.println("Invalid META command: " + commandTextSplit[0]);
                break;
        }
    }

    /**
     *
     */
    private void setClockHelper() {
        if (commandTextSplit.length < 2) {
            System.out.println("Missing @CLOCK argument");
            return;
        }

        switch (commandTextSplit[0].toUpperCase()) {

        }
    }

    /**
     * 0 means pause, 1 means resume
     *
     * @param action Whether to pause or resume the ______.
     */
    private void setClockState(int action) { }

    /**
     *
     */
    private void setClockOnestep() { }

    /**
     *
     * @param onestep
     */
    private void setClockOnestep(int onestep) { }

    /**
     *
     * @param rate
     */
    private void setClockRate(int rate) { }

    /**
     *
     */
    private void configure() { }
}
