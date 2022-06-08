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

		// Run your tests like this.
		startup.parse("@CONFIGURE LOG \"a.txt\" DOT SEQUENCE \"b.txt\" NETWORK \"c.txt\" XML \"d.txt\"");

		startup.parse(
				"CREATE ACTUATOR LINEAR a1 ACCELERATION LEADIN 0.1 LEADOUT -0.2 RELAX 0.3 VELOCITY LIMIT 5 VALUE MIN 1 MAX 20 INITIAL 2 JERK LIMIT 3");
		// startup.parse("SEND MESSAGE PING");
		startup.parse("@exit");
	}

	/**
	 *
	 * @param parse
	 * @throws Exception
	 */
	private void parse(final String parse) throws Exception {
		System.out.println("PARSE> " + parse);

		Parser parser = new Parser(_parserHelper, parse);

		parser.parse();
	}
}
