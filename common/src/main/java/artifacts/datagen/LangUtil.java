package artifacts.datagen;

import com.google.common.base.CaseFormat;
import joptsimple.internal.Strings;

public class LangUtil {

    public static String fromCamelCasedString(String string) {
        return fromSnakeCasedString(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, string));
    }

    public static String fromSnakeCasedString(String string) {
        String[] words = string.split("_");
        for (int i = 0; i < words.length; i++) {
            words[i] = Character.toUpperCase(words[i].charAt(0)) + words[i].substring(1);
        }
        return Strings.join(words, " ")
                .replace(" A ", " a ")
                .replace(" An ", " an ")
                .replaceFirst(" In ", " in ")
                .replace(" Of ", " of ")
                .replace(" On ", " on ")
                .replaceFirst(" Per ", " per ")
                .replace(" The ", " the ");
    }
}
