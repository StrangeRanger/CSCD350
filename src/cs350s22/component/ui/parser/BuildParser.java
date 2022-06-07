package cs350s22.component.ui.parser;

import java.util.ArrayList;
import java.util.List;

import cs350s22.component.A_Component;
import cs350s22.support.Identifier;

/**
 *
 */
public class BuildParser implements SubParser {
	@Override
	public void parse(String[] args, A_ParserHelper parserHelper) {
		int index = 1;
		List<A_Component> components = new ArrayList<A_Component>();
		if (args[index].equalsIgnoreCase("NETWORK")) {
			index = 4;
			if (index < args.length) {
				components.add(parserHelper.getSymbolTableController().get(Identifier.make(args[index])));
				index++;
				if (index < args.length) {
					components.add(parserHelper.getSymbolTableActuator().get(Identifier.make(args[index])));
					index++;
					if (index < args.length) {
						components.add(parserHelper.getSymbolTableSensor().get(Identifier.make(args[index])));
						index++;
					}
				}
			}
		}
		parserHelper.getControllerMaster().addComponents(components);
		parserHelper.getNetwork().writeOutput();
	}
}
