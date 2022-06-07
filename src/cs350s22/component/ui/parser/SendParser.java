package cs350s22.component.ui.parser;

import java.util.ArrayList;
import java.util.List;

import cs350s22.component.ui.CommandLineInterface;
import cs350s22.message.A_Message;
import cs350s22.message.actuator.MessageActuatorReportPosition;
import cs350s22.message.actuator.MessageActuatorRequestPosition;
import cs350s22.message.ping.MessagePing;
import cs350s22.support.Identifier;

/**
 *
 */
public class SendParser implements SubParser {
	@Override
	public void parse(String[] args, A_ParserHelper parserHelper) {
		// Check if sending a message
		if (!args[1].toUpperCase().equals("MESSAGE")) {
			return;
		}
		List<Identifier> ids = null;
		List<Identifier> groups = null;
		double value = 0;
		int index = 2;
		CommandLineInterface cli = parserHelper.getCommandLineInterface();
		List<String> possibleCommands = List.of("PING", "ID", "GROUPS", "POSITION");
		while (index < args.length) {
			switch (args[index].toUpperCase()) {
			case "PING":
				index++;
				cli.issueMessage(new MessagePing());
				break;
			case "ID":
				index++;
				ids = new ArrayList<Identifier>();
				while (!possibleCommands.contains(args[index])) {
					ids.add(Identifier.make(args[index]));
					index++;
				}
			case "GROUPS":
				index++;
				groups = new ArrayList<Identifier>();
				while (!possibleCommands.contains(args[index])) {
					groups.add(Identifier.make(args[index]));
					index++;
				}
			case "POSITION":
				index++;
				A_Message message;
				if (args[index].toUpperCase().equals("REQUEST")) {
					index++;
					value = Double.parseDouble(args[index]);
					if (ids != null) {
						message = new MessageActuatorRequestPosition(ids, value);
						cli.issueMessage(message);
					}
					if (groups != null) {
						message = new MessageActuatorRequestPosition(groups, value);
						cli.issueMessage(message);
					}

				} else {
					index++;
					if (ids != null) {
						message = new MessageActuatorReportPosition(ids);
						cli.issueMessage(message);
					}
					if (groups != null) {
						message = new MessageActuatorReportPosition(groups);
						cli.issueMessage(message);
					}
				}
			default:
				break;
			}
		}
	}
}
