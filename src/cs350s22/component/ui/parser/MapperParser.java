package cs350s22.component.ui.parser;

import cs350s22.component.sensor.mapper.MapperEquation;
import cs350s22.component.sensor.mapper.MapperInterpolation;
import cs350s22.component.sensor.mapper.function.equation.EquationNormalized;
import cs350s22.component.sensor.mapper.function.equation.EquationPassthrough;
import cs350s22.component.sensor.mapper.function.equation.EquationScaled;
import cs350s22.component.sensor.mapper.function.interpolator.InterpolatorLinear;
import cs350s22.component.sensor.mapper.function.interpolator.InterpolatorSpline;
import cs350s22.component.sensor.mapper.function.interpolator.loader.MapLoader;
import cs350s22.support.Filespec;
import cs350s22.support.Identifier;
import java.io.IOException;
import java.util.HashMap;

/**
 * Mapper commands are responsible for creating mappers that modify the raw value
 * reported directly by a sensor.
 */
public class MapperParser implements SubParser {
    private final String[]       args;
    private final A_ParserHelper parserHelper;
    private final int            numOfCmdArgs;

    /** Constructor. */
    public MapperParser(String[] args, A_ParserHelper parserHelper) {
        this.args         = args;
        this.parserHelper = parserHelper;
        this.numOfCmdArgs = args.length - 1;
    }

    @Override
    public void parse() throws IOException {
        if (numOfCmdArgs < 3) {
            System.out.println("Invalid or missing arguments for CREATE MAPPER");
            return;
        }

        Identifier              id  = Identifier.make(args[2]);
        HashMap<String, String> map = recParser(4, new HashMap<>() {
            { put("mapType", args[3].toUpperCase()); }
        });

        switch (map.get("mapType")) {
            case "EQUATION_PASSTHROUGH":
                passthroughEquation(id);
                break;
            case "EQUATION_SCALE":
                scaleEquation(id, Integer.parseInt(map.get("mapTypeValueOne")));
                break;
            case "EQUATION_NORMALIZE":
                normalizeEquation(id, Integer.parseInt(map.get("mapTypeValueOne")),
                                  Integer.parseInt(map.get("mapTypeValueTwo")));
                break;
            case "INTERPOLATION_LINEAR":
                linearInterpolation(id, Filespec.make(map.get("definition")));
                break;
            case "INTERPOLATION_SPLINE":
                splineInterpolation(id, Filespec.make(map.get("definition")));
                break;
            default:
                System.out.println("Something is up!!!");
                break;
        }
    }

    /**
     *
     * @param arg
     * @param map
     * @return
     */
    private HashMap<String, String> recParser(int arg, HashMap<String, String> map) {
        if (arg > numOfCmdArgs) {
            return map;
        }

        String mapType = map.get("mapType");

        switch (mapType) {
            case "EQUATION":
            case "INTERPOLATION":
                map.put("mapType", mapType + "_" + args[arg]);
                break;
            case "EQUATION_SCALE":
                map.put("mapTypeValueOne", args[arg]);
                break;
            case "EQUATION_NORMALIZE":
                if (! map.containsKey("mapTypeValueOne")) {
                    map.put("mapTypeValueOne", args[arg]);
                } else {
                    map.put("mapTypeValueTwo", args[arg]);
                }
                break;
            case "INTERPOLATION_LINEAR":
            case "INTERPOLATION_SPLINE":
                if (args[arg].equalsIgnoreCase("DEFINITION") && ++arg <= numOfCmdArgs) {
                    map.put("definition", args[arg]);
                } else {
                    System.out.println("Invalid map type provided!!!");
                }
                break;
            default:
                System.out.println("Invalid map type provided!!!");
                break;
        }

        return recParser(++arg, map);
    }

    /**
     *
     * @param id
     */
    private void passthroughEquation(Identifier id) {
        EquationPassthrough newMapper      = new EquationPassthrough();
        MapperEquation      mapperEquation = new MapperEquation(newMapper);
        parserHelper.getSymbolTableMapper().add(id, mapperEquation);
    }

    /**
     *
     * @param id
     * @param value
     */
    private void scaleEquation(Identifier id, int value) {
        EquationScaled newMapper      = new EquationScaled(value);
        MapperEquation equationMapper = new MapperEquation(newMapper);
        parserHelper.getSymbolTableMapper().add(id, equationMapper);
    }

    /**
     *
     * @param id
     * @param valueOne
     * @param valueTwo
     */
    private void normalizeEquation(Identifier id, int valueOne, int valueTwo) {
        EquationNormalized newMapper      = new EquationNormalized(valueOne, valueTwo);
        MapperEquation     mapperEquation = new MapperEquation(newMapper);
        parserHelper.getSymbolTableMapper().add(id, mapperEquation);
    }

    /**
     *
     * @param id
     * @param filename
     * @throws IOException
     */
    private void linearInterpolation(Identifier id, Filespec filename)
            throws IOException {
        MapLoader           newMapper = new MapLoader(filename);
        InterpolatorLinear  thing     = new InterpolatorLinear(newMapper.load());
        MapperInterpolation ting      = new MapperInterpolation(thing);
        parserHelper.getSymbolTableMapper().add(id, ting);
    }

    /**
     *
     * @param id
     * @param filename
     * @throws IOException
     */
    private void splineInterpolation(Identifier id, Filespec filename)
            throws IOException {
        MapLoader           newMapper = new MapLoader(filename);
        InterpolatorSpline  thing     = new InterpolatorSpline(newMapper.load());
        MapperInterpolation ting      = new MapperInterpolation(thing);
        parserHelper.getSymbolTableMapper().add(id, ting);
    }
}
