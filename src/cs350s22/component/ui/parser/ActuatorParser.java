package cs350s22.component.ui.parser;

import cs350s22.component.sensor.A_Sensor;
import cs350s22.support.Identifier;
import cs350s22.test.ActuatorPrototype;
import java.util.ArrayList;
import java.util.List;

public class ActuatorParser implements SubParser {
	private final String[]       args;
    private final A_ParserHelper parserHelper;

    /** Constructor. */
    public ActuatorParser(String[] args, A_ParserHelper parserHelper) {
        this.args         = args;
        this.parserHelper = parserHelper;
    }

    @Override
    public void parse() {
        // Provided arguments start at commandTextSplit[2], type of actuator guaranteed
        // to be first argument, id the second
        // initialize all variables
        List<Identifier> groups                  = new ArrayList<Identifier>();
        List<A_Sensor>   sensors                 = new ArrayList<A_Sensor>();
        double           accelerationLeadin      = 0;
        double           accelerationLeadout     = 0;
        double           accelerationRelax       = 0;
        double           velocityLimit           = 0;
        double           valueInitial            = 0;
        double           valueMin                = 0;
        double           valueMax                = 0;
        double           inflectionJerkThreshold = 0;
        int              index                   = 4;
        String           actuatorType            = args[2].toUpperCase();
        Identifier       id                      = Identifier.make(args[3]);
        List<String>     possibleCommands = List.of("ACCELERATION", "JERK", "VELOCITY");

        try {
            while (index < args.length) {
                switch (args[index].toUpperCase()) {
					case "SENSOR":
					case "SENSORS":
                        index++;
                        // not yet implemented
                        while (! possibleCommands.contains(args[index].toUpperCase())) {
                            index++;
                            sensors.add(parserHelper.getSymbolTableSensor().get(Identifier.make(args[index])));
                        }
                        break;
                    case "ACCELERATION":
                        index++;
                        while (! possibleCommands.contains(args[index].toUpperCase())) {
                            if (args[index].toUpperCase().equals("LEADIN")) {
                                index++;
                                accelerationLeadin = Double.parseDouble(args[index]);
                                index++;
                            } else if (args[index].toUpperCase().equals("LEADOUT")) {
                                index++;
                                accelerationLeadout = Double.parseDouble(args[index]);
                                index++;
                            } else if (args[index].toUpperCase().equals("RELAX")) {
                                index++;
                                accelerationRelax = Double.parseDouble(args[index]);
                                index++;
                            }
                        }
                        break;
                    case "VELOCITY":
                        index++;
                        while (! possibleCommands.contains(args[index].toUpperCase())) {
                            if (args[index].toUpperCase().equals("LIMIT")) {
                                index++;
                                velocityLimit = Double.parseDouble(args[index]);
                            } else if (args[index].toUpperCase().equals("MIN")) {
                                index++;
                                valueMin = Double.parseDouble(args[index]);
                            } else if (args[index].toUpperCase().equals("MAX")) {
                                index++;
                                valueMax = Double.parseDouble(args[index]);
                            } else if (args[index].toUpperCase().equals("INITIAL")) {
                                index++;
                                valueInitial = Double.parseDouble(args[index]);
                            } else if (args[index].toUpperCase().equals("VALUE"))
                                index++;
                        }
                        break;
                    case "JERK":
                        index++;
                        if (args[index].toUpperCase().equals("LIMIT")) {
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
        ActuatorPrototype actuator = new ActuatorPrototype(id, groups, accelerationLeadin,
													       accelerationLeadout, accelerationRelax,
													       velocityLimit, valueInitial, valueMin, valueMax,
													       inflectionJerkThreshold, sensors);

        parserHelper.getSymbolTableActuator().add(id, actuator);
    }
}
