package idv.const_x.tools;

import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRowGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @Description <pre>
 *
 * </pre>
 * @Author const.x
 * @Date 2024-02-19
 */
public class TextComparer {

    public static void rightCompare(String... strs) {
        compare(true, strs);
    }

    public static void compare(String... strs) {
        compare(false, strs);
    }

    public static void compare(boolean right, String... strs) {
        List<String> list = new ArrayList<>();
        String longer = "";
        List<StringBuilder> sbs = new ArrayList<>();
        for (String s : strs) {
            if (s.length() > longer.length()) {
                longer = s;
            }
            sbs.add(new StringBuilder());
            list.add(s);
        }
        list.remove(longer);
        StringBuilder base = sbs.get(strs.length - 1);
        sbs.remove(base);


        String split = colorChar('|', 7);

        List<Character> exists = new ArrayList<>();

        int counter = 0;

        if (right) {
            for (int i = longer.length() - 1; i >= 0; i--) {
                counter = compareChar(list, longer, sbs, base, split, counter, exists, i);
            }
        } else {
            for (int i = 0; i < longer.length(); i++) {
                counter = compareChar(list, longer, sbs, base, split, counter, exists, i);
            }
        }

        if (base.length() > 0) {
            System.out.println(base.toString());
            base.setLength(0);
            for (StringBuilder sb : sbs) {
                System.out.println(sb.toString());
                sb.setLength(0);
            }
        }

    }

    private static int compareChar(List<String> list, String longer, List<StringBuilder> sbs, StringBuilder base, String split, int counter, List<Character> exists, int i) {
        char basech = longer.charAt(i);
        exists.clear();

        base.append(basech).append(split);
        for (int i1 = 0; i1 < list.size(); i1++) {
            String s = list.get(i1);
            StringBuilder sb = sbs.get(i1);
            if (s.length() > i) {
                char ch = s.charAt(i);
                if (ch == basech) {
                    sb.append(ch).append(split);
                } else {
                    if (!exists.contains(ch)) {
                        exists.add(ch);
                    }
                    sb.append(colorChar(ch, exists.indexOf(ch) + 1)).append(split);
                }
            } else {
                sb.append(" ").append(split);
            }
        }

        counter++;
        if (counter == 80) {
            System.out.println(base.toString());
            base.setLength(0);
            for (StringBuilder sb : sbs) {
                System.out.println(sb.toString());
                sb.setLength(0);
            }
            System.out.println();
            counter = 0;
        }
        return counter;
    }

    private static String colorChar(char source, int code) {
        return "\u001b[38;5;" + code + "m" + source + "\u001b[0m";
    }


    public static final String
            PROFIX = "\u001B[",
            END = "m",
            COLOR_TEXT_DEFAULT = "\u001B[39m",
            COLOR_TEXT_PROFIX = "3";


    public static final int
            COLOR_RED = 1,
            COLOR_YELLOW = 3,
            COLOR_PURPLE = 5;

    static BiFunction<DiffRow.Tag, Boolean, String> tagFun = (tag, f) -> {
        String s = PROFIX + COLOR_TEXT_PROFIX;
        switch (tag) {
            case INSERT:
                return f ? s + COLOR_RED + END : COLOR_TEXT_DEFAULT;
            case CHANGE:
                return f ? s + COLOR_YELLOW + END : COLOR_TEXT_DEFAULT;
            case DELETE:
                return f ? s + COLOR_PURPLE + END : COLOR_TEXT_DEFAULT;
        }
        return "";
    };

    static DiffRowGenerator generator = DiffRowGenerator.create()
            .showInlineDiffs(true)
            .inlineDiffByWord(true)
            .oldTag(tagFun)      //introduce markdown style for strikethrough
            .newTag(tagFun)     //introduce markdown style for bold
            .build();

    public static List<String> compareGitChange(String a, String b) {
        List<DiffRow> rows = generator.generateDiffRows(
                Arrays.asList(a),
                Arrays.asList(b));
        List<String> result = Arrays.asList(rows.get(0).getOldLine(), rows.get(0).getNewLine());
        ArrayList<String> list = new ArrayList<>(result.size());
        for (int i = 0; i < result.size(); i++) {
            String s = result.get(i);
            s = s.replaceAll("&lt;", "<");
            s = s.replaceAll("&gt;", ">");
            list.add(i, s);
        }
        return list;
    }

}
