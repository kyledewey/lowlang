package lowlang;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;

import lowlang.tokenizer.Tokenizer;
import lowlang.tokenizer.TokenizerException;
import lowlang.parser.Parser;
import lowlang.parser.Program;
import lowlang.parser.ParseException;
import lowlang.typechecker.Typechecker;
import lowlang.typechecker.TypeErrorException;
import lowlang.codegen.MIPSCodeGenerator;

public class Compiler {
    public static void printUsage() {
        System.out.println("Takes the following params:");
        System.out.println("-Input filename (.ll)");
        System.out.println("-Output filename (.asm)");
    }

    public static String fileContentsAsString(final String inputFilename) throws IOException {
        final StringBuilder builder = new StringBuilder();
        final BufferedReader reader = new BufferedReader(new FileReader(inputFilename));
        try {
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }
            return builder.toString();
        } finally {
            reader.close();
        }
    }

    public static void compile(final String inputFilename,
                               final String outputFilename)
        throws IOException,
               TokenizerException,
               ParseException,
               TypeErrorException {
        final String input = fileContentsAsString(inputFilename);
        final Program program = Parser.parse(Tokenizer.tokenize(input));
        Typechecker.typecheckProgramExternalEntry(program);
        MIPSCodeGenerator.compile(program, new File(outputFilename));
    }

    public static void main(final String[] args)
        throws IOException,
               TokenizerException,
               ParseException,
               TypeErrorException {
        if (args.length != 2) {
            printUsage();
        } else {
            compile(args[0], args[1]);
        }
    }
}
