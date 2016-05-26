package fj.demo;

import fj.F;
import fj.F1Functions;
import fj.F1W;
import fj.Unit;
import fj.data.IO;
import fj.data.IOFunctions;
import fj.data.IOW;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static fj.data.IOFunctions.stdinReadLine;
import static fj.data.IOFunctions.stdoutPrint;
import static fj.data.IOFunctions.stdoutPrintln;
import static fj.data.IOFunctions.toSafeValidation;

/**
 * Demonstrates how to use <code>IO</code> and basic function composition.
 */
public class IOWalkthrough {
    public static void main(String[] args) {

        // IO is just a container to defer a computation (lazy), with the intention to encapsulate computations that either
        // consume and/or produce side-effects
        // the computation is not (yet) executed on creation hence it can be treated like a value

        final IO<Unit> askName = () -> {
            System.out.println("Hi, what's your name?");
            return Unit.unit();
        };

        // fj.data.IOFunctions contains a lot of convenience functions regarding IO, the above example could be rewritten with IOFunctions.stdoutPrintln
        // we now create an IO value to prompt for the name if executed

        IO<Unit> promptName = IOFunctions.stdoutPrint("Name: ");

        // we can compose these two values with fj.data.IOFunctions.append, since they both are not interested in any runtime value

        IO<Unit> askAndPromptName = IOFunctions.append(askName, promptName);

        // now we create an IO value to read a line from stdin

        final IO<String> readName = () -> new BufferedReader(new InputStreamReader(System.in)).readLine();

        // this is the same as IOFunctions.stdinReadLine()

        // now we create a function which takes a string, upper cases it and creates an IO value that would print the upper cased string if executed

        final F<String, IO<Unit>> upperCaseAndPrint = F1Functions.<String, IO<Unit>, String>o(IOFunctions::stdoutPrintln).f(String::toUpperCase);

        // we now want to compose reading the name with printing it, for that we need to have access to the runtime value that is returned when the
        // IO value for read is executed, hence we use fj.data.IOFunctions.bind instead of fj.data.IOFunctions.append

        final IO<Unit> readAndPrintUpperCasedName = IOFunctions.bind(readName, upperCaseAndPrint);

        // so append is really just a specialised form of bind, ignoring the runtime value of the IO execution that was composed before us

        final IO<Unit> program = IOFunctions.bind(askAndPromptName, ignored -> readAndPrintUpperCasedName);

        // this is the same as writing IOFunctions.append(askAndPromptName, readAndPrintUpperCasedName)

        // we have recorded the entire program, but have not run anything yet
        // now we get to the small dirty part at the end of our program where we actually execute it
        // we can either choose to just call program.run(), which allows the execution to escape
        // or we use safe to receive an fj.data.Either with the potential exception on the left side

        toSafeValidation(program).run().on((IOException e) -> { e.printStackTrace(); return Unit.unit(); });

        // doing function composition like this can be quite cumbersome, since you will end up nesting parenthesis unless you flatten it out by
        // assigning the functions to variables like above, but you can use the fj.F1W syntax wrapper for composing single-argument functions and fj.data.IOW
        // for composing IO values instead, the entire program can be written like so:

        IOW.lift(stdoutPrintln("What's your name again?"))
                .append(stdoutPrint("Name: "))
                .append(stdinReadLine())
                .bind(F1W.lift((String s) -> s.toUpperCase()).andThen(IOFunctions::stdoutPrintln))
                .safe().run().on((IOException e) -> { e.printStackTrace(); return Unit.unit(); });
    }
}

