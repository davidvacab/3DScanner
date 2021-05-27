package Printing;

import java.awt.*;

import static java.awt.event.KeyEvent.*;

public class KeyboardSlow {
    private final Robot robot;

    public KeyboardSlow() throws AWTException{
        this.robot = new Robot();
    }

    public KeyboardSlow(Robot robot) {
        this.robot = robot;
    }

    public void type(CharSequence characters) throws InterruptedException {
        int length = characters.length();
        for (int i = 0; i < length; i++) {
            char character = characters.charAt(i);
            type(character);
        }
    }

    public void type(char character) throws InterruptedException {
        switch (character) {
            case 'a': doType(VK_A); Thread.sleep(100); break;
            case 'b': doType(VK_B); Thread.sleep(100);break;
            case 'c': doType(VK_C); Thread.sleep(100);break;
            case 'd': doType(VK_D); Thread.sleep(100);break;
            case 'e': doType(VK_E); Thread.sleep(100);break;
            case 'f': doType(VK_F); Thread.sleep(100);break;
            case 'g': doType(VK_G); Thread.sleep(100);break;
            case 'h': doType(VK_H); Thread.sleep(100);break;
            case 'i': doType(VK_I); Thread.sleep(100);break;
            case 'j': doType(VK_J); Thread.sleep(100);break;
            case 'k': doType(VK_K); Thread.sleep(100);break;
            case 'l': doType(VK_L); Thread.sleep(100);break;
            case 'm': doType(VK_M); Thread.sleep(100);break;
            case 'n': doType(VK_N); Thread.sleep(100);break;
            case 'o': doType(VK_O); Thread.sleep(100);break;
            case 'p': doType(VK_P); Thread.sleep(100);break;
            case 'q': doType(VK_Q); Thread.sleep(100);break;
            case 'r': doType(VK_R); Thread.sleep(100);break;
            case 's': doType(VK_S); Thread.sleep(100);break;
            case 't': doType(VK_T); Thread.sleep(100);break;
            case 'u': doType(VK_U); Thread.sleep(100);break;
            case 'v': doType(VK_V); Thread.sleep(100);break;
            case 'w': doType(VK_W); Thread.sleep(100);break;
            case 'x': doType(VK_X); Thread.sleep(100);break;
            case 'y': doType(VK_Y); Thread.sleep(100);break;
            case 'z': doType(VK_Z); Thread.sleep(100);break;
            case 'A': doType(VK_SHIFT, VK_A); Thread.sleep(100);break;
            case 'B': doType(VK_SHIFT, VK_B); Thread.sleep(100);break;
            case 'C': doType(VK_SHIFT, VK_C); Thread.sleep(100);break;
            case 'D': doType(VK_SHIFT, VK_D); Thread.sleep(100);break;
            case 'E': doType(VK_SHIFT, VK_E); Thread.sleep(100);break;
            case 'F': doType(VK_SHIFT, VK_F); Thread.sleep(100);break;
            case 'G': doType(VK_SHIFT, VK_G); Thread.sleep(100);break;
            case 'H': doType(VK_SHIFT, VK_H); Thread.sleep(100);break;
            case 'I': doType(VK_SHIFT, VK_I);Thread.sleep(100); break;
            case 'J': doType(VK_SHIFT, VK_J); Thread.sleep(100);break;
            case 'K': doType(VK_SHIFT, VK_K);Thread.sleep(100); break;
            case 'L': doType(VK_SHIFT, VK_L); Thread.sleep(100);break;
            case 'M': doType(VK_SHIFT, VK_M); Thread.sleep(100);break;
            case 'N': doType(VK_SHIFT, VK_N); Thread.sleep(100);break;
            case 'O': doType(VK_SHIFT, VK_O); Thread.sleep(100);break;
            case 'P': doType(VK_SHIFT, VK_P); Thread.sleep(100);break;
            case 'Q': doType(VK_SHIFT, VK_Q); Thread.sleep(100);break;
            case 'R': doType(VK_SHIFT, VK_R);Thread.sleep(100); break;
            case 'S': doType(VK_SHIFT, VK_S); Thread.sleep(100);break;
            case 'T': doType(VK_SHIFT, VK_T);Thread.sleep(100); break;
            case 'U': doType(VK_SHIFT, VK_U); Thread.sleep(100);break;
            case 'V': doType(VK_SHIFT, VK_V); Thread.sleep(100);break;
            case 'W': doType(VK_SHIFT, VK_W); Thread.sleep(100);break;
            case 'X': doType(VK_SHIFT, VK_X);Thread.sleep(100); break;
            case 'Y': doType(VK_SHIFT, VK_Y); Thread.sleep(100);break;
            case 'Z': doType(VK_SHIFT, VK_Z);Thread.sleep(100); break;
            case '`': doType(VK_BACK_QUOTE);Thread.sleep(100); break;
            case '0': doType(VK_0); Thread.sleep(100);break;
            case '1': doType(VK_1);Thread.sleep(100); break;
            case '2': doType(VK_2);Thread.sleep(100); break;
            case '3': doType(VK_3); Thread.sleep(100);break;
            case '4': doType(VK_4);Thread.sleep(100); break;
            case '5': doType(VK_5);Thread.sleep(100); break;
            case '6': doType(VK_6);Thread.sleep(100); break;
            case '7': doType(VK_7);Thread.sleep(100); break;
            case '8': doType(VK_8); Thread.sleep(100);break;
            case '9': doType(VK_9); Thread.sleep(100);break;
            case '-': doType(VK_MINUS);Thread.sleep(100); break;
            case '=': doType(VK_EQUALS); Thread.sleep(100);break;
            case '~': doType(VK_SHIFT, VK_BACK_QUOTE); Thread.sleep(100);break;
            case '!': doType(VK_EXCLAMATION_MARK);Thread.sleep(100); break;
            case '@': doType(VK_AT);Thread.sleep(100); break;
            case '#': doType(VK_NUMBER_SIGN); Thread.sleep(100);break;
            case '$': doType(VK_DOLLAR); Thread.sleep(100);break;
            case '%': doType(VK_SHIFT, VK_5);Thread.sleep(100); break;
            case '^': doType(VK_CIRCUMFLEX);Thread.sleep(100); break;
            case '&': doType(VK_AMPERSAND);Thread.sleep(100); break;
            case '*': doType(VK_ASTERISK); Thread.sleep(100);break;
            case '(': doType(VK_LEFT_PARENTHESIS);Thread.sleep(100); break;
            case ')': doType(VK_RIGHT_PARENTHESIS);Thread.sleep(100); break;
            case '_': doType(VK_UNDERSCORE);Thread.sleep(100); break;
            case '+': doType(VK_PLUS); Thread.sleep(100);break;
            case '\t': doType(VK_TAB); Thread.sleep(100);break;
            case '\n': doType(VK_ENTER);Thread.sleep(100); break;
            case '[': doType(VK_OPEN_BRACKET);Thread.sleep(100); break;
            case ']': doType(VK_CLOSE_BRACKET); Thread.sleep(100);break;
            case '\\': doType(VK_BACK_SLASH);Thread.sleep(100); break;
            case '{': doType(VK_SHIFT, VK_OPEN_BRACKET); Thread.sleep(100);break;
            case '}': doType(VK_SHIFT, VK_CLOSE_BRACKET);Thread.sleep(100); break;
            case '|': doType(VK_SHIFT, VK_BACK_SLASH); Thread.sleep(100);break;
            case ';': doType(VK_SEMICOLON); Thread.sleep(100);break;
            case ':': doType(VK_COLON);Thread.sleep(100); break;
            case '\'': doType(VK_QUOTE); Thread.sleep(100);break;
            case '"': doType(VK_QUOTEDBL); Thread.sleep(100);break;
            case ',': doType(VK_COMMA);Thread.sleep(100); break;
            case '<': doType(VK_SHIFT, VK_COMMA); Thread.sleep(100);break;
            case '.': doType(VK_PERIOD);Thread.sleep(100); break;
            case '>': doType(VK_SHIFT, VK_PERIOD); Thread.sleep(100);break;
            case '/': doType(VK_SLASH);Thread.sleep(100); break;
            case '?': doType(VK_SHIFT, VK_SLASH);Thread.sleep(100); break;
            case ' ': doType(VK_SPACE); Thread.sleep(100);break;
            default:
                throw new IllegalArgumentException("Cannot type character " + character);
        }
    }

    private void doType(int... keyCodes) {
        doType(keyCodes, 0, keyCodes.length);
    }

    private void doType(int[] keyCodes, int offset, int length) {
        if (length == 0) {
            return;
        }

        robot.keyPress(keyCodes[offset]);
        doType(keyCodes, offset + 1, length - 1);
        robot.keyRelease(keyCodes[offset]);
    }
}
