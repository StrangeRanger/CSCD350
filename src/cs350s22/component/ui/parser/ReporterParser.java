package cs350s22.component.ui.parser;

import cs350s22.component.sensor.reporter.ReporterChange;
import cs350s22.component.sensor.reporter.ReporterFrequency;
import cs350s22.support.Identifier;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Reporter commands are responsible for creating reporters. The job of a reporter is to
 * inform recipients of the value of a sensor based on a trigger event.
 */
public class ReporterParser implements SubParser {
    private final                String[] args;
    private final A_ParserHelper parserHelper;
    private final int            numOfCmdArgs;

    /** Constructor. */
    public ReporterParser(String[] args, A_ParserHelper parserHelper) {
        this.args         = args;
        this.parserHelper = parserHelper;
        this.numOfCmdArgs = args.length - 1;
    }

    @Override
    public void parse() throws IOException {
        ////[ Command Verification ]////////////////////////////////////////////////////

        String argTwo       = args[2].toUpperCase();
        String argFour      = args[4].toUpperCase();
        String argSecToLast = args[numOfCmdArgs - 1].toUpperCase();

        /// Check if specific arguments that always exist at specific indexes, are where
        /// they should be. If not, that's an indicator that the command as a whole, is
        /// invalid, missing arguments, or is incorrectly formatted.
        if (! (argTwo.equals("CHANGE") || argTwo.equals("FREQUENCY"))
            && ! argFour.equals("NOTIFY")
            && ! (argSecToLast.equals("DELTA") || argSecToLast.equals("FREQUENCY"))
            && numOfCmdArgs < 5) {
            throw new IOException("Invalid or missing arguments for command "
                                  + "CREATE PARSER");
        }

        ////[ Main section of parse method ]////////////////////////////////////////////

        int                   value  = Integer.parseInt(args[numOfCmdArgs]);
        Identifier            id     = Identifier.make(args[3]);
        ArrayList<Identifier> ids    = new ArrayList<>();
        ArrayList<Identifier> groups = new ArrayList<>();
        List<String> argList = Arrays.asList("FREQUENCY", "DELTA", "IDS", "GROUPS");

        /// Starting at command 'NOTIFY', iterate through the given commands, creating
        /// and adding the specified 'IDS' and 'GROUPS' to their respective ArrayList.
        for (int arg = 5; arg < numOfCmdArgs;) {
            switch (args[arg++].toUpperCase()) {
                case "IDS":
                    for (; ! argList.contains(args[arg]) && arg < numOfCmdArgs; arg++) {
                        ids.add(Identifier.make(args[arg]));
                    }
                    break;
                case "GROUPS":
                    for (; ! argList.contains(args[arg]) && arg < numOfCmdArgs; arg++) {
                        groups.add(Identifier.make(args[arg]));
                    }
                    break;
            }
        }

        if (argTwo.equals("CHANGE")) {
            if (groups.isEmpty()) {
                changeReporter(id, ids, value);
            } else {
                changeReporter(id, ids, groups, value);
            }
        } else {
            if (groups.isEmpty()) {
                frequencyReporter(id, ids, value);
            } else {
                frequencyReporter(id, ids, groups, value);
            }
        }
    }

    /**
     * Creates reporter id that informs the recipients when the sensor value has
     * changed by at least value.
     *
     * @param id    Name of the reporter object to be created.
     * @param ids   Collection ids, if given, passed by the command.
     * @param value Value passed by command.
     */
    private void changeReporter(Identifier id, ArrayList<Identifier> ids, int value) {
        ReporterChange newReporter = new ReporterChange(ids, value);
        parserHelper.getSymbolTableReporter().add(id, newReporter);
    }

    /**
     * Creates reporter id that informs the recipients when the sensor value has
     * changed by at least value.
     *
     * @param id     Name of the reporter object to be created.
     * @param ids    Collection ids who are informed in relation to the value.
     * @param groups Collection groups who are informed in relation to the value.
     * @param value  The value that dictates when the reporter acts.
     */
    private void changeReporter(Identifier id, ArrayList<Identifier> ids,
                                ArrayList<Identifier> groups, int value) {
        ReporterChange newReporter = new ReporterChange(ids, groups, value);
        parserHelper.getSymbolTableReporter().add(id, newReporter);
    }

    /**
     * Creates reporter id that informs the recipients every value updates.
     *
     * @param id    Name of the reporter object to be created.
     * @param ids   Collection ids who are informed in relation to the value.
     * @param value The value that dictates when the reporter acts.
     */
    private void frequencyReporter(Identifier id, ArrayList<Identifier> ids,
                                   int value) {
        ReporterFrequency newReporter = new ReporterFrequency(ids, value);
        parserHelper.getSymbolTableReporter().add(id, newReporter);
    }

    /**
     * Creates reporter id that informs the recipients every value updates.
     *
     * @param id     Name of the reporter object to be created.
     * @param ids    Collection ids who are informed in relation to the value.
     * @param groups Collection groups who are informed in relation to the value.
     * @param value  The value that dictates when the reporter acts.
     */
    private void frequencyReporter(Identifier id, ArrayList<Identifier> ids,
                                   ArrayList<Identifier> groups, int value) {
        ReporterFrequency newReporter = new ReporterFrequency(ids, groups, value);
        parserHelper.getSymbolTableReporter().add(id, newReporter);
    }
}
