package cs350s22.component.ui.parser;

import java.util.ArrayList;
import java.util.List;

import cs350s22.component.sensor.A_Sensor;
import cs350s22.support.Identifier;
import cs350s22.test.ActuatorPrototype;

/**
 * The actuator command is responsible for creating an actuator and optionally
 * connecting sensors to it.
 *
 * ...
 */
public class ActuatorParser implements SubParser {
	private final String[] args;
	private final A_ParserHelper parserHelper;

	/** Constructor. */
	public ActuatorParser(String[] args, A_ParserHelper parserHelper) {
		this.args = args;
		this.parserHelper = parserHelper;
	}

	@Override
	public void parse() {
		// Provided arguments start at commandTextSplit[2], type of actuator guaranteed
		// to be first argument, id the second
		// initialize all variables
		List<Identifier> groups = new ArrayList<>();
		List<A_Sensor> sensors = new ArrayList<>();
		double accelerationLeadin = 0;
		double accelerationLeadout = 0;
		double accelerationRelax = 0;
		double velocityLimit = 0;
		double valueInitial = 0;
		double valueMin = 0;
		double valueMax = 0;
		double inflectionJerkThreshold = 0;
		int index = 4;
		Identifier id = Identifier.make(args[3]);
		List<String> possibleCommands = List.of("ACCELERATION", "JERK", "VELOCITY");

		try {
			while (index < args.length) {
				switch (args[index].toUpperCase()) {
				case "SENSOR":
				case "SENSORS":
					index++;
					// not yet implemented
					while (!possibleCommands.contains(args[index].toUpperCase())) {
						index++;
						sensors.add(parserHelper.getSymbolTableSensor().get(Identifier.make(args[index])));
					}
					break;
				case "ACCELERATION":
					index++;
					while (!possibleCommands.contains(args[index].toUpperCase())) {
						if (args[index].equalsIgnoreCase("LEADIN")) {
							index++;
							accelerationLeadin = Double.parseDouble(args[index]);
							index++;
						} else if (args[index].equalsIgnoreCase("LEADOUT")) {
							index++;
							accelerationLeadout = Double.parseDouble(args[index]);
							index++;
						} else if (args[index].equalsIgnoreCase("RELAX")) {
							index++;
							accelerationRelax = Double.parseDouble(args[index]);
							index++;
						}
					}
					break;
				case "VELOCITY":
					index++;
					while (!possibleCommands.contains(args[index].toUpperCase())) {
						if (args[index].equalsIgnoreCase("LIMIT")) {
							index++;
							velocityLimit = Double.parseDouble(args[index]);
						} else if (args[index].equalsIgnoreCase("MIN")) {
							index++;
							valueMin = Double.parseDouble(args[index]);
						} else if (args[index].equalsIgnoreCase("MAX")) {
							index++;
							valueMax = Double.parseDouble(args[index]);
						} else if (args[index].equalsIgnoreCase("INITIAL")) {
							index++;
							valueInitial = Double.parseDouble(args[index]);
						} else if (args[index].equalsIgnoreCase("VALUE"))
							index++;
						else
							index++;
					}
					break;
				case "JERK":
					index++;
					if (args[index].equalsIgnoreCase("LIMIT")) {
						index++;
						inflectionJerkThreshold = Double.parseDouble(args[index]);
					} else
						index++;
					break;
				default:
					groups.add(Identifier.make(args[index]));
					index++;
					break;
				}
			}
		} catch (IndexOutOfBoundsException e) {
			System.out.println("Logic Error in AcuatorParser, actuator has most likely been created fine");
		}

		ActuatorPrototype actuator = new ActuatorPrototype(id, groups, accelerationLeadin, accelerationLeadout,
				accelerationRelax, velocityLimit, valueInitial, valueMin, valueMax, inflectionJerkThreshold, sensors);
		parserHelper.getSymbolTableActuator().add(id, actuator);
	}
}
