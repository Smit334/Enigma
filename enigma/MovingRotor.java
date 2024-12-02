package enigma;

import java.util.ArrayList;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Smit Malde
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        notch = notches;
        ArrayList<Character> n = new ArrayList<Character>();
        for (int i = 0; i < notches.length(); i++) {
            n.add(notches.charAt(i));
        }
        setNotches(n);
    }

    /** Override for rotates method. */
    @Override
    boolean rotates() {
        return true;
    }

    /** Override for advance method. */
    @Override
    void advance() {
        set((setting() + 1) % alphabet().size());
    }

    /** Override for notches method. */
    @Override
    String notches() {
        return notch;
    }

    /** String containing notches. */
    private String notch;

}
