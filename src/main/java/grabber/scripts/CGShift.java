package grabber.scripts;

import java.util.HashMap;

public class CGShift {
    private static CGShift cgShift;
    private final HashMap<String, String> charMap;

    private CGShift() {
        charMap = new HashMap<>();
        charMap.put("a","t");
        charMap.put("b","o");
        charMap.put("c","n");
        charMap.put("d","q");
        charMap.put("e","u");
        charMap.put("f","e");
        charMap.put("g","r");
        charMap.put("h","z");
        charMap.put("i","l");
        charMap.put("j","a");
        charMap.put("k","w");
        charMap.put("l","i");
        charMap.put("m","c");
        charMap.put("n","v");
        charMap.put("o","f");
        charMap.put("p","j");
        charMap.put("q","p");
        charMap.put("r","s");
        charMap.put("s","y");
        charMap.put("t","h");
        charMap.put("u","g");
        charMap.put("v","d");
        charMap.put("w","m");
        charMap.put("x","k");
        charMap.put("y","b");
        charMap.put("z","x");
        charMap.put("A","J");
        charMap.put("B","K");
        charMap.put("C","A");
        charMap.put("D","B");
        charMap.put("E","R");
        charMap.put("F","U");
        charMap.put("G","D");
        charMap.put("H","Q");
        charMap.put("I","Z");
        charMap.put("J","C");
        charMap.put("K","T");
        charMap.put("L","H");
        charMap.put("M","F");
        charMap.put("N","V");
        charMap.put("O","L");
        charMap.put("P","I");
        charMap.put("Q","W");
        charMap.put("R","N");
        charMap.put("S","E");
        charMap.put("T","Y");
        charMap.put("U","P");
        charMap.put("V","S");
        charMap.put("W","X");
        charMap.put("X","G");
        charMap.put("Y","O");
        charMap.put("Z","M");
    }

    public static CGShift getInstance() {
        if(cgShift == null) {
            cgShift = new CGShift();
        }
        return cgShift;
    }

    public String decrypt(String encryptedString) {
        StringBuilder decryptedString = new StringBuilder();
        for(int i = 0; i < encryptedString.length(); i++) {
            char toReplace = encryptedString.charAt(i);
            String replaced = charMap.get(String.valueOf(toReplace));
            if(replaced == null) replaced = String.valueOf(toReplace);
            decryptedString.append(replaced);
        }
        return decryptedString.toString();
    }
}
