package cs350s22.component.ui.parser;

import cs350s22.component.sensor.reporter.ReporterChange;
import cs350s22.component.sensor.reporter.ReporterFrequency;
import cs350s22.support.Identifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** REPORTER command class. */
public class ReporterParser implements SubParser {
    private String[] args;
    private int            numOfCmdArgs;
    private A_ParserHelper parserHelper;

    @Override
    public void parse(String[] args, A_ParserHelper parserHelper) {
        this.args         = args;
        this.numOfCmdArgs = args.length - 1;
        this.parserHelper = parserHelper;

        ////[ Command Verification ]////////////////////////////////////////////////////

        String argTwo       = args[2].toUpperCase();
        String argFour      = args[4].toUpperCase();
        String argSecToLast = args[numOfCmdArgs - 1].toUpperCase();

        /// Check if specific arguments that always exist at specific indexes, are where
        /// they should be. If not, that's an indicator that the command as a whole, is
        /// invalid, missing arguments, or is incorrectly formatted.
        /// TODO: Refactor maybe?
        if (! (argTwo.equals("CHANGE") || argTwo.equals("FREQUENCY"))
            && ! argFour.equals("NOTIFY")
            && ! (argSecToLast.equals("DELTA") || argSecToLast.equals("FREQUENCY"))
            && numOfCmdArgs < 5) {
            System.out.println("Invalid or missing arguments for command "
                               + "CREATE PARSER");
            return;
        }

        ////[ Main section of parse method ]////////////////////////////////////////////

        int                   value  = Integer.parseInt(args[numOfCmdArgs]);
        Identifier            id     = Identifier.make(args[3]);
        ArrayList<Identifier> ids    = new ArrayList<>();
        ArrayList<Identifier> groups = new ArrayList<>();
        List<String> argList = Arrays.asList("FREQUENCY", "DELTA", "IDS", "GROUPS");

        /// Starting at command 'NOTIFY', iterate through the given commands, creating
        /// and adding the specified 'IDS' and 'GROUPS' to their respective ArrayLists.
        for (int arg = 5; arg < numOfCmdArgs;) {
            switch (args[arg++].toUpperCase()) {
                case "IDS":
                    for (; ! argList.contains(args[arg]); arg++) {
                        ids.add(Identifier.make(args[arg]));
                    }
                    break;
                case "GROUPS":
                    for (; ! argList.contains(args[arg]); arg++) {
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
     */
    private void changeReporter(Identifier id, ArrayList<Identifier> ids, int value) {
        ReporterChange newReporter = new ReporterChange(ids, value);
        parserHelper.getSymbolTableReporter().add(id, newReporter);
    }

    /**
     * Creates reporter id that informs the recipients when the sensor value has
     * changed by at least value.
     */
    private void changeReporter(Identifier id, ArrayList<Identifier> ids,
                                ArrayList<Identifier> groups, int value) {
        ReporterChange newReporter = new ReporterChange(ids, groups, value);
        parserHelper.getSymbolTableReporter().add(id, newReporter);
    }

    /**
     * Creates reporter id that informs the recipients every value updates.
     */
    private void frequencyReporter(Identifier id, ArrayList<Identifier> ids,
                                   int value) {
        ReporterFrequency newReporter = new ReporterFrequency(ids, value);
        parserHelper.getSymbolTableReporter().add(id, newReporter);
    }

    /**
     * Creates reporter id that informs the recipients every value updates.
     */
    private void frequencyReporter(Identifier id, ArrayList<Identifier> ids,
                                   ArrayList<Identifier> groups, int value) {
        ReporterFrequency newReporter = new ReporterFrequency(ids, groups, value);
        parserHelper.getSymbolTableReporter().add(id, newReporter);
    }
}
