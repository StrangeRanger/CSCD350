package cs350s22.component.ui.parser;

import cs350s22.component.logger.LoggerMessage;
import cs350s22.component.logger.LoggerMessageSequencing;
import cs350s22.support.Clock;
import cs350s22.support.Filespec;
import java.io.IOException;

/** META command class. */
public class MetaParser implements SubParser {
    private final String[]       args;
    private final A_ParserHelper parserHelper;
    private final int            numOfCmdArgs;

    /** Constructor. */
    public MetaParser(String[] args, A_ParserHelper parserHelper) {
        this.args         = args;
        this.parserHelper = parserHelper;
        this.numOfCmdArgs = args.length - 1;
    }

    @Override
    public void parse() throws IOException {
        switch (args[0].toUpperCase()) {
            case "@CLOCK" -> setClockHelper();
            case "@EXIT" -> exit();
            case "@RUN" -> run();
            case "@CONFIGURE" -> configure();
            default -> System.out.println("Invalid META command: " + args[0]);
        }
    }

    ////[ Clock Methods ]///////////////////////////////////////////////////////////////

    /** Parses @CLOCK sub commands, and execute their respective methods. */
    private void setClockHelper() {
        /// @CLOCK
        if (numOfCmdArgs == 0) {
            displayClock();
            return;
        }

        String argOne = args[1].toUpperCase();

        /// @CLOCK (PAUSE | RESUME)
        if (argOne.equals("PAUSE") || argOne.equals("RESUME")) {
            setClockState(argOne);
        }
        /// @CLOCK ONESTEP [count]
        else if (argOne.equals("ONESTEP")) {
            if (numOfCmdArgs >= 2) {
                setClockOnestep(Integer.parseInt(args[2]));
            } else {
                setClockOnestep();
            }
        }
        /// META commands that require 2 or more arguments.
        else if (numOfCmdArgs >= 3) {
            String argTwo   = args[2].toUpperCase();
            double argThree = Double.parseDouble(args[3]);

            /// @CLOCK SET RATE value
            if (argOne.equals("SET") && argTwo.equals("RATE")) {
                setClockRate(Integer.parseInt(args[3]));
            }
            /// @CLOCK WAIT FOR value
            else if (argOne.equals("WAIT") && argTwo.equals("FOR")) {
                setClockWaitFor(argThree);
            }
            /// @CLOCK WAIT UNTIL value
            else if (argOne.equals("WAIT") && argTwo.equals("UNTIL")) {
                setClockWaitUntil(argThree);
            } else {
                System.out.println("Invalid or missing arguments for @CLOCK");
            }
        } else {
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
        Clock.getInstance().isActive(arg.equals("RESUME"));
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
     * @param value The rate for which the clock should be set to.
     */
    private void setClockRate(int value) { Clock.getInstance().setRate(value); }

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
        parserHelper.exit();
    }

    /** Loads and runs the script in fully qualified filename string. */
    private void run() {
        try {
            parserHelper.run(args[1]);
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
            String cmdArg         = args[i].toUpperCase();
            String cmdArgPrevious = args[i - 1].toUpperCase();

            if (cmdArg.equals("LOG")) {
                log = args[i + 1];
            } else if (cmdArgPrevious.equals("DOT") && cmdArg.equals("SEQUENCE")) {
                dotSequence = args[i + 1];
            } else if (cmdArg.equals("NETWORK")) {
                network = args[i + 1];
            } else if (cmdArg.equals("XML")) {
                xml = args[i + 1];
            }
        }

        if (log != null && dotSequence != null && network != null) {
            LoggerMessage.initialize(Filespec.make(log));
            LoggerMessageSequencing.initialize(Filespec.make(dotSequence),
                                               Filespec.make(network));
        } else {
            throw new IOException("One or more required arguments is either missing "
                                  + "or incorrect for @CONFIGURE");
        }
    }
}
