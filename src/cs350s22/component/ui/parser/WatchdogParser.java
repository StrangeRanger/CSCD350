package cs350s22.component.ui.parser;

import cs350s22.component.sensor.watchdog.WatchdogHigh;
import cs350s22.component.sensor.watchdog.WatchdogLow;
import cs350s22.component.sensor.watchdog.WatchdogBand;
import cs350s22.component.sensor.watchdog.WatchdogNotch;
import cs350s22.component.sensor.watchdog.A_Watchdog;
import cs350s22.support.Identifier;
import java.util.ArrayList;
import java.util.List;



import java.io.IOException;

public class WatchdogParser implements SubParser {
	
	private final String [] args;
	private final A_ParserHelper parserHelper;
	private final int numOfCmdArgs;
	
	public WatchdogParser(String [] args, A_ParserHelper parserHelper) {
		this.args = args;
		this.parserHelper = parserHelper;
		this.numOfCmdArgs = args.length - 1;
	}
	
	
	@Override 
	public void parse() throws IOException{
		if(numOfCmdArgs < 4) {
			System.out.println("There are no argument for CREATE WATCHDOG");
			return;
		}
		
		List<Identifier> groups = new ArrayList<>();
		List<A_Watchdog> watchdogs = new ArrayList<>();

		double WatchdogInstantaneous = 0;
		double WatchdogModeAverage = 0;
		double WatchdogModeStandardDeviation = 0;
		double WatchdogLow = 0;
		double WatchdogHigh = 0;
		double WatchdogBand = 0;
		double WatchdogNotch = 0;
		
		Identifier ID = Identifier.make(args[3]);
		List<String> Commands = List.of("MODE","ACCELERATION", "THRESHOLD");
		int index = 4;
		try {
			while(index < args.length) {
				switch(args[index].toUpperCase()) {
				case "MODE":
					index++;
					while(!Commands.contains(args[index].toUpperCase())) {
						if(args[index].equalsIgnoreCase("INSTANTANEOUS")) {
							index++;
							WatchdogInstantaneous = Double.parseDouble(args[index]);
							index++;
							
						} else if (args[index].equalsIgnoreCase("AVERAGE")) {
							index++;
							WatchdogModeAverage = Double.parseDouble(args[index]);
							index++;
						} else if (args[index].equalsIgnoreCase("STANDARD DEVIATION")) {
							index++;
							WatchdogModeStandardDeviation = Double.parseDouble(args[index]);
							index++;
						}
					}
					break;
				case "ACCELERATION":
					index++;
					while(!Commands.contains(args[index].toUpperCase())) {
						if(args[index].equalsIgnoreCase("LOW")) {
							index++;
							WatchdogLow= Double.parseDouble(args[index]);
							index++;
						}
						
						else if(args[index].equalsIgnoreCase("HIGH")) {
							index++;
							WatchdogHigh= Double.parseDouble(args[index]);
							index++;
						}
						
					}
					break;
				case "THRESHOLD":
					index++;
					if(args[index].equalsIgnoreCase("BAND")) {
						index++;
						WatchdogBand= Double.parseDouble(args[index]);
					}
						
					else if(args[index].equalsIgnoreCase("NOTCH")) {
						index++;
						WatchdogNotch= Double.parseDouble(args[index]);
						
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
		System.out.println("Logic Error in WatchdogParser");
	}
		
	parserHelper.getSymbolTableWatchdog().add(ID, null);
	
	
	}
}
