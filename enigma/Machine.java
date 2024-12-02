package enigma;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Smit Malde
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _plugboard = new Permutation("", alpha);
        _alphabet = alpha;
        _rotorSlots = numRotors;
        _numPawls = pawls;
        _allrotors = new ArrayList<Rotor>();
        _allrotors.addAll(allRotors);
        _hashRotors = new HashMap<String, Rotor>();
        for (Rotor r: allRotors) {
            _hashRotors.put(r.name(), r);
        }
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _rotorSlots;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _numPawls;
    }

    /** Return Rotor #K, where Rotor #0 is the reflector, and Rotor
     *  #(numRotors()-1) is the fast Rotor.  Modifying this Rotor has
     *  undefined results. */
    Rotor getRotor(int k) {
        return _allrotors.get(k);
    }

    Alphabet alphabet() {
        return _alphabet;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        int numMovingRotors = 0;
        for (int i = 0; i < rotors.length; i++) {
            if (!_hashRotors.containsKey(rotors[i])) {
                throw new EnigmaException("Rotor doesnt exist in _config!");
            }
            if (_hashRotors.get(rotors[i]).rotates()) {
                numMovingRotors++;
            }
            _allrotors.set(i, _hashRotors.get(rotors[i]));
        }
        if (numMovingRotors != numPawls()) {
            throw new EnigmaException("Moving rotors and pawls are not equal!");
        }
        if (!_allrotors.get(0).reflecting()) {
            throw new EnigmaException("Machine reflector not in 1st slot!");
        }
        for (int i = 1; i < rotors.length; i++) {
            if (!_allrotors.get(i).rotates()) {
                if (_allrotors.get(i - 1).rotates()) {
                    throw new EnigmaException("Rotors arranged incorrectly!");
                }
            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        for (int i = 0; i < setting.length(); i++) {
            _allrotors.get(i + 1).set(setting.charAt(i));
        }
    }

    /** Set my rotors according to ringSetting.
     * @param ringSetting  */
    void setRingSetting(String ringSetting) {
        for (int i = 0; i < ringSetting.length(); i++) {
            _allrotors.get(i + 1).setRingSetting(
                    _alphabet.toInt(ringSetting.charAt(i)));
        }
    }

    /** Return the current plugboard's permutation. */
    Permutation plugboard() {
        return _plugboard;
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        advanceRotors();
        if (Main.verbose()) {
            System.err.printf("[");
            for (int r = 1; r < numRotors(); r += 1) {
                System.err.printf("%c",
                        alphabet().toChar(getRotor(r).setting()));
            }
            System.err.printf("] %c -> ", alphabet().toChar(c));
        }
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(c));
        }
        c = applyRotors(c);
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c%n", alphabet().toChar(c));
        }
        return c;
    }

    /** Advance all rotors to their next position. */
    private void advanceRotors() {
        for (int i = 1; i < numRotors() - 1; i++) {
            if (_allrotors.get(i + 1).atNotch()) {
                _allrotors.get(i).advance();
                continue;
            }
            if (_allrotors.get(i).atNotch()) {
                if (_allrotors.get(i - 1).rotates()) {
                    _allrotors.get(i).advance();
                }
            }
        }
        _allrotors.get(numRotors() - 1).advance();
    }

    /** Return the result of applying the rotors to the character C (as an
     *  index in the range 0..alphabet size - 1). */
    private int applyRotors(int c) {
        if (c > alphabet().size() - 1) {
            throw new EnigmaException(c + " is out of range");
        }
        for (int i = _rotorSlots - 1; i >= 0; i--) {
            c = _allrotors.get(i).convertForward(c);
        }
        for (int i = 1; i < _rotorSlots; i++) {
            c = _allrotors.get(i).convertBackward(c);
        }
        return c;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        StringBuilder encrypt = new StringBuilder();
        for (int i = 0; i < msg.length(); i++) {
            if (msg.charAt(i) == ' ') {
                continue;
            }
            int converted = convert(_alphabet.toInt(msg.charAt(i)));
            encrypt.append(_alphabet.toChar(converted));
        }
        return encrypt.toString();
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of rotor slots. */
    private int _rotorSlots;

    /** Number of pawls. */
    private int _numPawls;

    /** Permutation for plugboard. */
    private Permutation _plugboard;

    /** A list containing all inserted rotors. */
    private ArrayList<Rotor> _allrotors;

    /** A hashmap containing all rotors. */
    private HashMap<String, Rotor> _hashRotors;


}
