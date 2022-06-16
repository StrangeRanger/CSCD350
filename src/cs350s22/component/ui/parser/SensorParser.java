package cs350s22.component.ui.parser;

import java.util.ArrayList;
import java.util.List;
import cs350s22.component.sensor.reporter.A_Reporter;
import cs350s22.component.sensor.watchdog.A_Watchdog;
import cs350s22.component.sensor.mapper.A_Mapper;
import cs350s22.support.Identifier;
import cs350s22.test.MySensor;


public class SensorParser implements SubParser {
	
	private final String [] args;
	private final A_ParserHelper parserHelper;
	
	public SensorParser(String[] args, A_ParserHelper parserHelper) {
		this.args = args;
		this.parserHelper = parserHelper;
	}
	
	@Override
	public void parse() {
		List<Identifier> groups = new ArrayList<>();
		List<A_Reporter> reporters = new ArrayList<>();
		List<A_Watchdog> watchdogs = new ArrayList<>();
		List<A_Mapper> mappers = new ArrayList<>();
		
		String Sensors = args[2].toUpperCase();
		
		if(Sensors != "SPEED" || Sensors != "POSITION")
			System.out.println("INVALID SPEED OR POSITION");
		
		Identifier ID = Identifier.make(args[3]);
		
		List<String> Commands = List.of("REPORTERS", "MAPPER");
		int index = 3;
		try {
			while(index < args.length) {
				switch(args[index].toUpperCase()) {
				case "REPORTERS":
					index++;
					while(!Commands.contains(args[index].toUpperCase())) {
						index++;
						reporters.add(parserHelper.getSymbolTableReporter().get(Identifier.make(args[index])));
					}
					break;
				
				case "WATCHDOGS" :
					index++;
					while(!Commands.contains(args[index].toUpperCase())) {
						index++;
						watchdogs.add(parserHelper.getSymbolTableWatchdog().get(Identifier.make(args[index])));
					}
					break;
					
					
				case "MAPPER" :
					index++;
					while(!Commands.contains(args[index].toUpperCase())) {
						index++;
						mappers.add(parserHelper.getSymbolTableMapper().get(Identifier.make(args[index])));
					}
					break;
				
				default:
					groups.add(Identifier.make(args[index]));
					index++;
					break;
				
				}
			}
		
		}catch(IndexOutOfBoundsException e) {
			System.out.println("Logic Error in SensorParser");
		}
		
		MySensor sensor = new MySensor(ID, groups, reporters, watchdogs);
		parserHelper.getSymbolTableSensor().add(ID, sensor);

	}
}
