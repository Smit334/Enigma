package enigma;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import ucb.util.CommandArgs;
import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Smit Malde
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            CommandArgs options =
                new CommandArgs("--verbose --=(.*){1,3}", args);
            if (!options.ok()) {
                throw error("Usage: java enigma.Main [--verbose] "
                            + "[INPUT [OUTPUT]]");
            }

            _verbose = options.contains("--verbose");
            new Main(options.get("--")).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Open the necessary files for non-option arguments ARGS (see comment
      *  on main). */
    Main(List<String> args) {
        _config = getInput(args.get(0));

        if (args.size() > 1) {
            _input = getInput(args.get(1));
        } else {
            _input = new Scanner(System.in);
        }

        if (args.size() > 2) {
            _output = getOutput(args.get(2));
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine enigma = readConfig();
        String settingLine = _input.nextLine();
        do {
            if (settingLine.charAt(0) != '*') {
                throw new EnigmaException("bad input, missing asterisk(*)");
            }
            setUp(enigma, settingLine);
            while (!_input.hasNext("[\\*]") && _input.hasNextLine()) {
                printMessageLine(enigma.convert(_input.nextLine()));
            }
            if (_input.hasNextLine()) {
                settingLine = _input.nextLine();
                while ((settingLine.equals("") || settingLine.charAt(0) != '*')
                        && _input.hasNextLine()) {
                    settingLine = _input.nextLine();
                    _output.println();
                }
            }
        } while (_input.hasNext());
    }


    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            if (!_config.hasNext("[^\\s()]*")) {
                throw new EnigmaException("wrong alphabet");
            }
            _alphabet = new Alphabet(_config.next());
            if (!_config.hasNextInt()) {
                throw new EnigmaException("wrong number of rotor slots");
            }
            int rotorSlots = _config.nextInt();
            if (!_config.hasNextInt()) {
                throw new EnigmaException("wrong number of pawls");
            }
            int numPawls = _config.nextInt();
            if (numPawls >= rotorSlots) {
                throw new EnigmaException(
                        "wrong number of pawls, more pawls than rotors");
            }
            ArrayList<Rotor> allrotors = new ArrayList<Rotor>();
            while (_config.hasNext()) {
                allrotors.add(readRotor());
            }
            return new Machine(_alphabet, rotorSlots, numPawls, allrotors);
        } catch (NoSuchElementException excp) {
            throw new EnigmaException("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            if (!_config.hasNext("[^\\s()]+")) {
                throw new EnigmaException("wrong rotor name");
            }
            String rotorName = _config.next();
            if (!_config.hasNext("((M[" + _alphabet + "]*)|N|R)")) {
                throw new EnigmaException("wrong rotor notches");
            }
            String notches = _config.next();
            StringBuilder permutation = new StringBuilder();
            while (_config.hasNext("(\\([^\\s()]*\\))+")) {
                permutation.append(_config.next());
            }
            Rotor r;
            if (!checkPermutaion(permutation.toString(), _alphabet)) {
                throw new EnigmaException("bad permutation,"
                        + " illegal characters in permutation");
            }
            Permutation perms = new Permutation(
                    permutation.toString(), _alphabet);
            switch (notches.charAt(0)) {
            case 'M' : return new MovingRotor(rotorName,
                        perms, notches.substring(1));
            case 'N' : return new FixedRotor(rotorName, perms);
            case 'R' : return new Reflector(rotorName, perms);
            default : throw new EnigmaException(
                        "Rotor %s has incorrect notch(es)");
            }
        } catch (NoSuchElementException excp) {
            throw new EnigmaException("bad rotor description");
        }
    }

    private boolean checkRepeat(String permutation) {
        ArrayList<Character> permutedLetters = new ArrayList<Character>();
        for (int i = 0; i < permutation.length(); i++) {
            if (permutedLetters.contains(permutation.charAt(i))
                    && permutation.charAt(i) != '('
                    && permutation.charAt(i) != ')') {
                return false;
            }
            permutedLetters.add(permutation.charAt(i));
        }
        return true;
    }

    private boolean checkPermutaion(String permutation, Alphabet alphabet) {
        if (!checkRepeat(permutation)) {
            return false;
        }
        for (int i = 0; i < permutation.length(); i++) {
            if (!alphabet.contains(permutation.charAt(i))
                    && permutation.charAt(i) != '('
                    && permutation.charAt(i) != ')') {
                return false;
            }
        }
        return true;
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        Scanner setting = new Scanner(settings);
        String[] rotorNames = new String[M.numRotors()];
        setting.next();
        for (int i = 0; i < M.numRotors(); i++) {
            rotorNames[i] = setting.next();
        }
        M.insertRotors(rotorNames);
        M.setRotors(setting.next());
        if (setting.hasNext() && !setting.hasNext("(\\([^\\s\\(\\)]*\\))+")) {
            String ringSetting = setting.next();
            M.setRingSetting(ringSetting);
        }
        StringBuilder permutation = new StringBuilder();
        while (setting.hasNext("(\\([^\\s\\(\\)]*\\))+")) {
            permutation.append(setting.next());
        }
        if (!checkPermutaion(permutation.toString(), _alphabet)) {
            throw new EnigmaException(
                    "bad permutation, illegal characters in permutation");
        }
        Permutation perms = new Permutation(permutation.toString(), _alphabet);
        M.setPlugboard(perms);
    }



    /** Return true iff verbose option specified. */
    static boolean verbose() {
        return _verbose;
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < msg.length(); i++) {
            if ((i + 1) % 5 == 0) {
                message.append(msg.charAt(i));
                message.append(" ");
                continue;
            }
            message.append(msg.charAt(i));
        }
        _output.println(message.toString());
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** True if --verbose specified. */
    private static boolean _verbose;
}
