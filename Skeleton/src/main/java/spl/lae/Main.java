package spl.lae;
import java.io.IOException;
import java.text.ParseException;

import parser.*;

public class Main {
    public static void main(String[] args) throws IOException {
      // TODO: main
      if (args.length != 2) {
        System.err.println("Usage: java Main <input.jsom> <output.json>") ;
        return ;
      }
      String inputPath = args[0] ;
      String outputPath = args[1] ;
      InputParser parser = new InputParser() ;
      try {
        ComputationNode root = parser.parse(inputPath) ;
        LinearAlgebraEngine engine = new LinearAlgebraEngine(4) ;
        engine.run(root) ;
        double[][] result = root.getMatrix() ;
        OutputWriter.write(result, outputPath) ;
      } catch (ParseException e) {
          OutputWriter.write("Computation error: " + e.getMessage(), outputPath) ;
      } catch (IllegalArgumentException e) {
          OutputWriter.write("Computation error: "+ e.getMessage(), outputPath) ;
      } catch (Exception e) {
        OutputWriter.write("Unexcepted error: " + e.getMessage(), outputPath) ;
      }
    }
}