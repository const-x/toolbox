package idv.const_x.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 支持控制台输出颜色
 */
public class ColoredStringUtils {

//            30-37：将文本颜色设置为0到7中的一种颜色，
//            40-47：将背景颜色设置为0到7中的一种颜色，
//            39：将文本颜色重置为默认值，
//            49：将背景颜色重置为默认值，

//0                终端默认设置 将所有文本属性（颜色，背景，亮度等）重置为其默认值。
//1                高亮显示 22：关闭变粗/明亮效果
//4                使用下划线
//5                闪烁
//7                反白显示 即调换文字和背景颜色
//8                不可见
//            例如，可以使用代码\x1B[35;1;42m在绿色背景上选择亮紫色文本

    public static final int  COLOR_BLACK = 0,
            COLOR_RED = 1,
            COLOR_GREEN = 2,
            COLOR_YELLOW = 3,
            COLOR_BLUE = 4,
            COLOR_PURPLE = 5,
            COLOR_AQUA = 6,
            COLOR_GRAY = 7,
            COLOR_WHITE = 8,

            COLOR_DEFAULT = 9;


    public static final String COLOR_TEXT_PROFIX = "3", COLOR_BACKGROUND_PROFIX = "4";

    public static final String SHOW_OPPTION_LIGNT_ON = "1", SHOW_LIGNT_OFF = "22";
    public static final String SHOW_OPPTION_UNDER_LINE = "4";
    public static final String SHOW_OPPTION_TEXT_BLING = "5";
    public static final String SHOW_OPPTION_INVERSE = "7";
    public static final String SHOW_OPPTION_UNVISABLE = "8";

    public static final String PROFIX = "\u001B[", END = "m";

    public static final String  COLOR_TEXT_DEFAULT = "\u001B[39m",
            COLOR_BACKGROUND_DEFAULT = "\u001B[49m",
            COLOR_ALL_DEFAULT = "\u001B[0m";


    public static String colorString(String s, int color) {
        if (s == null) {
            return s;
        }
        return StringExtUtils.join("",PROFIX, COLOR_TEXT_PROFIX, color, END, s, COLOR_ALL_DEFAULT);
    }

    public static String colorStringWithBackgroud(String s, int textColor, int backgroudColor) {
        if (s == null) {
            return s;
        }
        return StringExtUtils.join("",PROFIX, COLOR_TEXT_PROFIX, textColor, ";", COLOR_BACKGROUND_PROFIX, backgroudColor, END, s, COLOR_ALL_DEFAULT);
    }

    public static String ligntColorString(String s, int textColor) {
        if (s == null) {
            return s;
        }
        return StringExtUtils.join("",PROFIX, COLOR_TEXT_PROFIX, textColor, ";", SHOW_OPPTION_LIGNT_ON, END, s, COLOR_ALL_DEFAULT);
    }

    public static String ligntColorStringWithBackgroud(String s, int textColor, int backgroudColor) {
        if (s == null) {
            return s;
        }
        return StringExtUtils.join("",PROFIX, COLOR_TEXT_PROFIX, textColor, ";", SHOW_OPPTION_LIGNT_ON, ";", COLOR_BACKGROUND_PROFIX, backgroudColor, END, s, COLOR_ALL_DEFAULT);
    }

    public static String colorString(String s, int textColor, String ... showOpption) {
        if (s == null) {
            return s;
        }
        String opption = "";
        if (showOpption != null){
            opption = StringExtUtils.join(";",showOpption);
            opption += ";";
        }
        return StringExtUtils.join("",PROFIX, COLOR_TEXT_PROFIX, textColor, ";",opption, END, s, COLOR_TEXT_DEFAULT);
    }

    public static String lightUpWords(String s,  String ... contents) {
        return lightUpWords(s,null,null,contents);
    }

    public static String lightUpWords(String s, Collection<String> contents) {
        return lightUpWords(s,null,null,contents.toArray(new String[0]));
    }

    public static String lightUpWords(String s, int color, String ... contents) {
        return lightUpWords(s,color,null,contents);
    }

    public static String lightUpWords(String s, Integer color,Integer defaultColor, String ... contentx) {
        if (s == null || s.trim().equals("")) {
            return s;
        }
        if (contentx == null || contentx.length == 0) {
            return s;
        }
        Map<String,Integer> colorMap = new HashMap<>();
        if (defaultColor == null) {
            defaultColor = COLOR_DEFAULT;
        }
        if (color != null) {
            for (String content : contentx) {
                colorMap.put(content,color);
            }
        }else {
            for (String content : contentx) {
                if (content == null) {
                    continue;
                }
                if (color == null) {
                    color = COLOR_RED;
                    colorMap.put(content, color);
                } else {
                    color = (color + 1) % 8;
                    if (color == defaultColor || color == COLOR_BLACK || color == COLOR_GRAY || color == COLOR_BLUE) {
                        color = (color + 1) % 8;
                    }
                    colorMap.put(content, color);
                }
            }
        }
        return lightUpWords(s,colorMap,defaultColor);
    }

    public static String lightUpWords(String s, Map<String,Integer> contentColorMap,Integer defaultColor) {
        if (defaultColor == null) {
            defaultColor = COLOR_DEFAULT;
        }
        List<String> contents = new ArrayList<>(contentColorMap.keySet());
        if (contents.size() == 0) {
            return s;
        }
        //优先匹配较长的内容
        contents.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o2.length() - o1.length();
            }
        });

        List<String> strings = color(s, contents,  defaultColor, contentColorMap,0);
        return String.join("",strings);
    }



    private static  List<String> color(String s,List<String> contents,int defaultColor,Map<String,Integer> colorMap,int i){
        if (s.length() == 0) {
            return Collections.emptyList();
        }
        if (i >= contents.size()) {
            return Collections.singletonList(colorString(s,defaultColor));
        }
        String content = contents.get(i);
        Integer color = colorMap.get(content);

        if (s.length() < content.length() || !s.contains(content)) {
            return color(s, contents, defaultColor, colorMap,i + 1);
        }
        String colored = colorString(content, color);
        if (Objects.equals(s,content)) {
            return Collections.singletonList(colored);
        }
        List<String> result = new ArrayList<>();

        String[] split = s.split(content);
        // 123123 按 123 split 得到的数组是 0长度
        if (split.length == 0) {
            int muti = s.length()/content.length();
            for (int j = 0; j < muti; j++) {
                result.add(colored);
            }
        }

        for (int i1 = 0; i1 < split.length; i1++) {
            String s1 = split[i1];
            // 1234 按 123 split 得到的数组是 ["",4]
            if (s1.length() == 0) {
                result.add(colored);
                continue;
            }
            List<String> color1 = color(s1, contents, defaultColor, colorMap,i + 1);
            result.addAll(color1);
            if (i1 < split.length - 1) {
                result.add(colored);
            }
        }

        // 4123 按 123 split 得到的数组是 [4]
        if (s.endsWith(content)) {
            result.add(colored);
        }

        return result;
    }


    public static String colorStringWithBackgroud(String s, int textColor, int backgroudColor,String ... showOpption) {
        if (s == null) {
            return s;
        }
        String opption = "";
        if (showOpption != null){
            opption = StringExtUtils.join(";",showOpption);
            opption += ";";
        }
        return StringExtUtils.join("",PROFIX, COLOR_TEXT_PROFIX, textColor, ";", opption,  COLOR_BACKGROUND_PROFIX, backgroudColor, END, s, COLOR_ALL_DEFAULT);
    }

    public static void main(String[] args) throws InterruptedException {
        String s = ligntColorStringWithBackgroud("hello", COLOR_GREEN, COLOR_WHITE);
        s += " ";
        s += colorStringWithBackgroud("world", COLOR_PURPLE,COLOR_GREEN, SHOW_OPPTION_TEXT_BLING,SHOW_OPPTION_UNDER_LINE,SHOW_OPPTION_INVERSE);
        System.out.println(s);

    }
}

