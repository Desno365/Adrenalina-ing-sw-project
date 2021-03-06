package it.polimi.se2019.utils;

/**
 * Class to handle the generation of colored strings.
 *
 * @author MarcerAndrea
 * @author Desno365
 */
public class Color {

    /**
     * Since it's an utility class it can't be instantiated.
     */
    private Color() {
        throw new IllegalStateException("Cannot create an instance of this utility class.");
    }


    public static String getColoredString(String string, CharacterColorType characterColor, BackgroundColorType backgroundColor) {
        return setColorString(characterColor, backgroundColor) + string + resetColorString();
    }

    public static String getColoredString(String string, CharacterColorType characterColor) {
        return getColoredString(string, characterColor, BackgroundColorType.DEFAULT);
    }

    public static String getColoredCell(BackgroundColorType backgroundColor) {
        return setColorString(CharacterColorType.DEFAULT, backgroundColor) + " " + resetColorString();
    }


    private static String setColorString(CharacterColorType characterColor, BackgroundColorType backgroundColor) {
        return (char) 27 + "[" + characterColor.getCharacterColor() + ";" + backgroundColor.getBackgroundColor() + "m";
    }

    private static String resetColorString() {
        return setColorString(CharacterColorType.DEFAULT, BackgroundColorType.DEFAULT);
    }

    @SuppressWarnings("unused")
    private static void testColors() {
        for (CharacterColorType characterColor : CharacterColorType.values()) {
            for (BackgroundColorType backgroundColor : BackgroundColorType.values()) {
                System.out.print(setColorString(characterColor, backgroundColor) + " TEST " + resetColorString());
            }
            System.out.print("\n");
        }
    }

    /**
     * Character colors.
     */
    public enum CharacterColorType {
        // NOTE: the color BLACK may be rendered white in different terminals
        // NOTE: the color WHITE may be rendered grey in different terminals
        BLACK(30, null), RED(31, "dozer"), GREEN(32, "sprog"), YELLOW(33, "destructor"), BLUE(34, "banshee"), MAGENTA(35, "violet"), CYAN(36, null), WHITE(37, null), DEFAULT(39, null);

        private int characterColor;
        private String pgName;

        CharacterColorType(int characterColor, String characterName) {
            this.characterColor = characterColor;
            this.pgName = characterName;
        }

        public static BackgroundColorType convertBackgroundColor(CharacterColorType characterColorToConvert) {
            switch (characterColorToConvert) {
                case BLACK:
                    return BackgroundColorType.BLACK;
                case RED:
                    return BackgroundColorType.RED;
                case GREEN:
                    return BackgroundColorType.GREEN;
                case YELLOW:
                    return BackgroundColorType.YELLOW;
                case BLUE:
                    return BackgroundColorType.BLUE;
                case MAGENTA:
                    return BackgroundColorType.MAGENTA;
                case CYAN:
                    return BackgroundColorType.CYAN;
                case WHITE:
                    return BackgroundColorType.WHITE;
                default:
                    return BackgroundColorType.DEFAULT;
            }
        }

        public int getCharacterColor() {
            return characterColor;
        }

        public String getPgName() {
            return pgName;
        }
    }

    /**
     * Background colors.
     */
    public enum BackgroundColorType {
        // NOTE: the color BLACK may be rendered white in different terminals
        // NOTE: the color WHITE may be rendered grey in different terminals
        BLACK(40), RED(41), GREEN(42), YELLOW(43), BLUE(44), MAGENTA(45), CYAN(46), WHITE(47), DEFAULT(49);

        private int backgroundColor;

        BackgroundColorType(int backgroundColor) {
            this.backgroundColor = backgroundColor;
        }

        public static CharacterColorType convertBackgroundColor(CharacterColorType characterColorToConvert) {
            switch (characterColorToConvert) {
                case BLACK:
                    return CharacterColorType.BLACK;
                case RED:
                    return CharacterColorType.RED;
                case GREEN:
                    return CharacterColorType.GREEN;
                case YELLOW:
                    return CharacterColorType.YELLOW;
                case BLUE:
                    return CharacterColorType.BLUE;
                case MAGENTA:
                    return CharacterColorType.MAGENTA;
                case CYAN:
                    return CharacterColorType.CYAN;
                case WHITE:
                    return CharacterColorType.WHITE;
                default:
                    return CharacterColorType.DEFAULT;
            }
        }

        public int getBackgroundColor() {
            return backgroundColor;
        }
    }
}
