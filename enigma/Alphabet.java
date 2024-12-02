package enigma;
import java.util.ArrayList;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Smit Malde
 */
class Alphabet {
    /** A new alphabet containing CHARS. The K-th character has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        alph = new ArrayList<Character>();
        for (int i = 0; i < chars.length(); i++) {
            if (alph.contains(chars.charAt(i))) {
                continue;
            }
            alph.add(chars.charAt(i));
        }
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return alph.size();
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        return alph.contains(ch);
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        return alph.get(index);
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        return alph.indexOf(ch);
    }

    @Override
    public String toString() {
        StringBuilder alphabet = new StringBuilder();
        for (Character letter: alph) {
            alphabet.append(letter);
        }
        return alphabet.toString();
    }

    /** A list that contains the alphabets. */
    private ArrayList<Character> alph;


}
