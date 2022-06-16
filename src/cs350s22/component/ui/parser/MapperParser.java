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
    private final                String[] args;
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
            throw new IOException("Invalid or missing arguments for CREATE MAPPER");
        }

        Identifier              id  = Identifier.make(args[2]);
        HashMap<String, String> map = recParser(4, new HashMap<>() {
            { put("mapType", args[3].toUpperCase()); }
        });

        switch (map.get("mapType")) {
            /// CREATE MAPPER id EQUATION PASSTHROUGH
            case "EQUATION_PASSTHROUGH":
                passThroughEquation(id);
                break;
            /// CREATE MAPPER id EQUATION SCALE value
            case "EQUATION_SCALE":
                scaleEquation(id, Integer.parseInt(map.get("mapTypeValueOne")));
                break;
            /// CREATE MAPPER id EQUATION NORMALIZE value1 value2
            case "EQUATION_NORMALIZE":
                normalizeEquation(id, Integer.parseInt(map.get("mapTypeValueOne")),
                                  Integer.parseInt(map.get("mapTypeValueTwo")));
                break;
            /// CREATE MAPPER id INTERPOLATION SPLINE DEFINITION string
            case "INTERPOLATION_LINEAR":
                linearInterpolation(id, Filespec.make(map.get("definition")));
                break;
            /// CREATE MAPPER id INTERPOLATION SPLINE DEFINITION string
            case "INTERPOLATION_SPLINE":
                splineInterpolation(id, Filespec.make(map.get("definition")));
                break;
            default:
                throw new IOException("Invalid map type provided");
        }
    }

    /**
     * Recursively parse through the command.
     *
     * @param arg Index of command argument to reference.
     * @param map Hashmap to store identify information of the given command.
     * @return    The Hashmap.
     */
    private HashMap<String, String> recParser(int arg, HashMap<String, String> map)
            throws IOException {
        if (arg > numOfCmdArgs) {
            return map;
        }

        String mapType = map.get("mapType");

        switch (mapType) {
            case "EQUATION":
            case "INTERPOLATION":
                map.put("mapType", mapType + "_" + args[arg]);
                break;
            /// CREATE MAPPER id EQUATION SCALE value
            case "EQUATION_SCALE":
                map.put("mapTypeValueOne", args[arg]);
                break;
            /// CREATE MAPPER id EQUATION NORMALIZE value1 value2
            case "EQUATION_NORMALIZE":
                if (! map.containsKey("mapTypeValueOne")) {
                    map.put("mapTypeValueOne", args[arg]);
                } else {
                    map.put("mapTypeValueTwo", args[arg]);
                }
                break;
            /// CREATE MAPPER id INTERPOLATION (LINEAR | SPLINE) DEFINITION string
            case "INTERPOLATION_LINEAR":
            case "INTERPOLATION_SPLINE":
                if (args[arg].equalsIgnoreCase("DEFINITION") && ++arg <= numOfCmdArgs) {
                    map.put("definition", args[arg]);
                } else {
                    throw new IOException("Invalid map type provided");
                }
                break;
            default:
                throw new IOException("Invalid map type provided");
        }

        return recParser(++arg, map);
    }

    /**
     * Creates mapper id that does not remap the raw value. This is equivalent to CREATE
     * MAPPER id EQUATION SCALE 1.
     *
     * @param id Name of the mapper object to be created.
     */
    private void passThroughEquation(Identifier id) {
        EquationPassthrough newMapper      = new EquationPassthrough();
        MapperEquation      mapperEquation = new MapperEquation(newMapper);
        parserHelper.getSymbolTableMapper().add(id, mapperEquation);
    }

    /**
     * Creates mapper id that remaps the raw value by the linear coefficient value.
     *
     * @param id    Name of the mapper object to be created.
     * @param value The linear coefficient value passed to the mapper.
     */
    private void scaleEquation(Identifier id, int value) {
        EquationScaled newMapper      = new EquationScaled(value);
        MapperEquation equationMapper = new MapperEquation(newMapper);
        parserHelper.getSymbolTableMapper().add(id, equationMapper);
    }

    /**
     * Creates mapper id that remaps the raw value onto a percentage scale as defined by
     * the lower limit valueOne and upper limit valueTwo.
     *
     * @param id       Name of the mapper object to be created.
     * @param valueOne Lower limit of the mapper.
     * @param valueTwo Upper limit of the mapper.
     */
    private void normalizeEquation(Identifier id, int valueOne, int valueTwo) {
        EquationNormalized newMapper      = new EquationNormalized(valueOne, valueTwo);
        MapperEquation     mapperEquation = new MapperEquation(newMapper);
        parserHelper.getSymbolTableMapper().add(id, mapperEquation);
    }

    /**
     * Creates mapper id that remaps the raw value based on the comma-delimited
     * interpolation table defined in string. Each row defines a point in the
     * two-dimensional graph. The first value is the raw sensor value; the second is its
     * mapped value. LINEAR mode does linear interpolation; SPLINE does a smoother
     * nonlinear interpolation.
     *
     * @param id           Name of the mapper object to be created.
     * @param filename     Name of the file to be used.
     * @throws IOException Invalid input.
     */
    private void linearInterpolation(Identifier id, Filespec filename)
            throws IOException {
        MapLoader           newMapper = new MapLoader(filename);
        InterpolatorLinear  thing     = new InterpolatorLinear(newMapper.load());
        MapperInterpolation ting      = new MapperInterpolation(thing);
        parserHelper.getSymbolTableMapper().add(id, ting);
    }

    /**
     * Creates mapper id that remaps the raw value based on the comma-delimited
     * interpolation table defined in string. Each row defines a point in the
     * two-dimensional graph. The first value is the raw sensor value; the second is its
     * mapped value. LINEAR mode does linear interpolation; SPLINE does a smoother
     * nonlinear interpolation.
     *
     * @param id           Identifier for the command type.
     * @param filename     Name of the file to be used.
     * @throws IOException Invalid input.
     */
    private void splineInterpolation(Identifier id, Filespec filename)
            throws IOException {
        MapLoader           newMapper = new MapLoader(filename);
        InterpolatorSpline  thing     = new InterpolatorSpline(newMapper.load());
        MapperInterpolation ting      = new MapperInterpolation(thing);
        parserHelper.getSymbolTableMapper().add(id, ting);
    }
}
