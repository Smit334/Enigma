package enigma;

import java.util.ArrayList;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Smit Malde
 */
class Permutation {
    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _permuted = new ArrayList<Character>();
        for (int i = 0; i < _alphabet.size(); i++) {
            _permuted.add(_alphabet.toChar(i));
        }
        addCycle(cycles);
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        for (int i = 0; i < _permuted.size(); i++) {
            if (cycle.contains(String.valueOf(_permuted.get(i)))) {
                for (int c = 0; c < cycle.length(); c++) {
                    if ((cycle.charAt(c) == _permuted.get(i))
                            && (cycle.charAt(c + 1) != ')')) {
                        _permuted.set(i, cycle.charAt(c + 1));
                        break;
                    }
                    if ((cycle.charAt(c) == _permuted.get(i))
                            && (cycle.charAt(c + 1) == ')')) {
                        for (int back = c; back >= 0; back--) {
                            if (cycle.charAt(back) == '(') {
                                _permuted.set(i, cycle.charAt(back + 1));
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        char perms = _permuted.get(wrap(p));
        return _alphabet.toInt(perms);
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        char perms = _alphabet.toChar(wrap(c));
        return _permuted.indexOf(perms);
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        int index = _alphabet.toInt(p);
        return _permuted.get(index);
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        int index = _permuted.indexOf(c);
        return _alphabet.toChar(index);
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (int i = 0; i < _alphabet.size(); i++) {
            if (_permuted.get(i) == _alphabet.toChar(i)) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** A list containing permuted letters. */
    private ArrayList<Character> _permuted;
}
