package cs350s22.component.ui.parser;

import cs350s22.component.logger.LoggerMessage;
import cs350s22.component.logger.LoggerMessageSequencing;
import cs350s22.support.Clock;
import cs350s22.support.Filespec;
import java.io.IOException;

/** META command class. */
public class MetaParser implements SubParser {
    String[] cmdTextSplit;
    int            numOfCmdArgs;
    A_ParserHelper parserHelper;

    @Override
    public void parse(String[] cmdTextSplit, A_ParserHelper parserHelper)
            throws IOException {
        this.cmdTextSplit = cmdTextSplit;
        this.numOfCmdArgs = cmdTextSplit.length - 1;
        this.parserHelper = parserHelper;

        switch (cmdTextSplit[0].toUpperCase()) {
            case "@CLOCK" -> setClockHelper();
            case "@EXIT" -> exit();
            case "@RUN" -> run();
            case "@CONFIGURE" -> configure();
            default -> System.out.println("Invalid META command: " + cmdTextSplit[0]);
        }
    }

    ////[ Clock Methods ]///////////////////////////////////////////////////////////////

    /** Parses @CLOCK sub commands, and execute their respective methods. */
    private void setClockHelper() {
        if (numOfCmdArgs == 0) {
            displayClock();
            return;
        }

        String argOne = cmdTextSplit[1].toUpperCase();

        /// @CLOCK (PAUSE | RESUME)
        if (argOne.contains("PAUSE") || argOne.contains("RESUME")) {
            setClockState(argOne);
        }
        /// @CLOCK ONESTEP [count]
        else if (argOne.contains("ONESTEP")) {
            if (numOfCmdArgs >= 2) {
                setClockOnestep(Integer.parseInt(cmdTextSplit[2]));
            } else {
                setClockOnestep();
            }
        }
        /// META commands that require 2 or more arguments.
        else if (numOfCmdArgs >= 2) {
            String argTwo = cmdTextSplit[2].toUpperCase();

            /// @CLOCK SET RATE value
            if (argOne.contains("SET") && argTwo.contains("RATE")) {
                if (numOfCmdArgs >= 3) {
                    setClockRate(Integer.parseInt(cmdTextSplit[3]));
                } else {
                    System.out.println("Missing argument for @CLOCK SET RATE");
                }
            }
            /// @CLOCK WAIT FOR value
            else if (argOne.contains("WAIT") && argTwo.contains("FOR")) {
                if (numOfCmdArgs >= 3) {
                    setClockWaitFor(Double.parseDouble(cmdTextSplit[3]));
                } else {
                    System.out.println("Missing argument for @CLOCK WAIT FOR");
                }
            }
            /// @CLOCK WAIT UNTIL value
            else if (argOne.contains("WAIT") && argTwo.contains("UNTIL")) {
                if (numOfCmdArgs >= 3) {
                    setClockWaitUntil(Double.parseDouble(cmdTextSplit[3]));
                } else {
                    System.out.println("Missing argument for @CLOCK WAIT UNTIL");
                }
            } else {
                System.out.println("Invalid arguments for @CLOCK: " + argOne + " "
                                   + argTwo);
            }
        }
        /// Anything else.
        else {
            System.out.println("Invalid argument for @CLOCK: " + argOne);
        }
    }

    /** Prints the current time to standard output. */
    private void displayClock() { System.out.println(Clock.getInstance().getTime()); }

    /**
     * Pauses or resumes automated updating by the clock.
     *
     * @param arg The state the clock should be set to.
     */
    private void setClockState(String arg) {
        Clock.getInstance().isActive(arg.contains("RESUME"));
    }

    /**
     * Updates the clock manually one time. This is valid only while the clock is
     * paused.
     */
    private void setClockOnestep() {
        if (Clock.getInstance().isActive()) {
            System.out.println("Clock must be paused to use this command");
        } else {
            Clock.getInstance().onestep();
        }
    }

    /**
     * Updates the clock 'count' times. This is valid only while the clock is paused.
     *
     * @param onestep The amount to update the clock by.
     */
    private void setClockOnestep(int onestep) {
        if (Clock.getInstance().isActive()) {
            System.out.println("Clock must be paused to use this command");
        } else {
            Clock.getInstance().onestep(onestep);
        }
    }

    /**
     * Sets the clock rate value in milliseconds per update.
     *
     * @param rate The rate for which the clock should be set to.
     */
    private void setClockRate(int rate) { Clock.getInstance().setRate(rate); }

    /**
     * Waits for value seconds before processing the next command.
     *
     * @param value The number of seconds to wait for.
     */
    private void setClockWaitFor(double value) { Clock.getInstance().waitFor(value); }

    /**
     * Waits until time is at least value seconds before processing the next command.
     *
     * @param value Seconds to wait till the time reaches.
     */
    private void setClockWaitUntil(double value) {
        Clock.getInstance().waitUntil(value);
    }

    ////[ Non-Clock Methods ]///////////////////////////////////////////////////////////

    /**
     * Exits the system. This must be the last statement; otherwise, log files may not
     * be complete.
     */
    private void exit() {
        // parserHelper.exit(); // TODO: Uncomment when ready...
        System.exit(0);
    }

    /** Loads and runs the script in fully qualified filename string. */
    private void run() {
        try {
            parserHelper.run(cmdTextSplit[1]);
        } catch (ParseException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Defines where the output goes for logging and reporting. This must be the first
     * command issued.
     *
     * @throws IOException <-- TODO: Define argument
     */
    private void configure() throws IOException {
        String log = null, dotSequence = null, network = null, xml = null;

        /// NOTE: The for statement only goes up to the second to last index in array on
        //        purpose. It's not a typo or mistake.
        for (int i = 1; i < numOfCmdArgs; i++) {
            String cmdArg         = cmdTextSplit[i].toUpperCase();
            String cmdArgPrevious = cmdTextSplit[i - 1].toUpperCase();

            if (cmdArg.contains("LOG")) {
                log = cmdTextSplit[i + 1];
            } else if (cmdArgPrevious.contains("DOT") && cmdArg.contains("SEQUENCE")) {
                dotSequence = cmdTextSplit[i + 1];
            } else if (cmdArg.contains("NETWORK")) {
                network = cmdTextSplit[i + 1];
            } else if (cmdArg.contains("XML")) {
                xml = cmdTextSplit[i + 1];
            }
        }

        if (log != null && dotSequence != null && network != null) {
            LoggerMessage.initialize(Filespec.make(log));
            LoggerMessageSequencing.initialize(Filespec.make(dotSequence),
                                               Filespec.make(network));
        } else {
            throw new IOException(
                    "One or more required arguments is either missing or incorrect for @CONFIGURE");
        }
    }
}
