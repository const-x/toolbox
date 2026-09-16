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
public class AnsiUtils {

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
//            例如，可以使用代码\u001b[35;1;42m在绿色背景上选择亮紫色文本

//除了16色外，某些控制台支持输出256色。指令的形式如下：
//\u001b[38;5;${ID}m

    public static final int  COLOR_BLACK = 0,
            COLOR_RED = 1,
            COLOR_GREEN = 2,
            COLOR_YELLOW = 3,
            COLOR_BLUE = 4,
            COLOR_PURPLE = 5,
            COLOR_AQUA = 6,
            COLOR_GRAY = 7,
            COLOR_WHITE = 8;


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
        return String.join("",PROFIX, COLOR_TEXT_PROFIX, String.valueOf(color), END, s, COLOR_ALL_DEFAULT);
    }

    public static String colorStringWithBackgroud(String s, int textColor, int backgroudColor) {
        if (s == null) {
            return s;
        }
        return String.join("",PROFIX, COLOR_TEXT_PROFIX, String.valueOf(textColor), ";", COLOR_BACKGROUND_PROFIX, String.valueOf(backgroudColor), END, s, COLOR_ALL_DEFAULT);
    }

    public static String lightColorString(String s, int textColor) {
        if (s == null) {
            return s;
        }
        return String.join("",PROFIX, COLOR_TEXT_PROFIX, String.valueOf(textColor), ";", SHOW_OPPTION_LIGNT_ON, END, s, COLOR_ALL_DEFAULT);
    }

    public static String lightColorStringWithBackgroud(String s, int textColor, int backgroudColor) {
        if (s == null) {
            return s;
        }
        return String.join("",PROFIX, COLOR_TEXT_PROFIX, String.valueOf(textColor), ";", SHOW_OPPTION_LIGNT_ON, ";", COLOR_BACKGROUND_PROFIX, String.valueOf(backgroudColor), END, s, COLOR_ALL_DEFAULT);
    }

    public static String colorString(String s, int textColor, String ... showOpption) {
        if (s == null) {
            return s;
        }
        String opption = "";
        if (showOpption != null){
            opption = String.join("",showOpption);
            opption += ";;";
        }
        return String.join("",PROFIX, COLOR_TEXT_PROFIX, String.valueOf(textColor), ";",opption, END, s, COLOR_TEXT_DEFAULT);
    }

    public static String color256String(String s, int textColor) {
        if (s == null) {
            return s;
        }
        return String.join("",PROFIX, "38;5;", String.valueOf(textColor), END, s, COLOR_TEXT_DEFAULT);
    }

    public static String colorStringWithBackgroud(String s, int textColor, int backgroudColor,String ... showOpption) {
        if (s == null) {
            return s;
        }
        String opption = "";
        if (showOpption != null){
            opption = String.join("",showOpption);
            opption += ";";
        }
        return String.join("",PROFIX, COLOR_TEXT_PROFIX, String.valueOf(textColor), ";", opption,  COLOR_BACKGROUND_PROFIX,String.valueOf(backgroudColor), END, s, COLOR_ALL_DEFAULT);
    }

    public static String lightUpWords(String s,  String ... contents) {
        return lightUpWords(s,null,null,contents);
    }

    public static String lightUpWords(String s, Collection<String>  contents) {
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
            defaultColor = COLOR_WHITE;
        }
        if (color != null) {
            for (String content : contentx) {
                colorMap.put(content,color);
            }
        }else {
            for (String content : contentx) {
                if (color == null) {
                    color = COLOR_RED;
                    colorMap.put(content, color);
                } else {
                    color = (color + 1) % 8;
                    if (color == defaultColor || color == COLOR_BLACK) {
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
            defaultColor = COLOR_WHITE;
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

    public static List<String> mutiLineLightUpWords(List<String> lines,  String ... contents) {
        return mutiLineLightUpWords(lines,null,null,contents);
    }

    public static List<String> mutiLineLightUpWords(List<String> lines, Collection<String>  contents) {
        return mutiLineLightUpWords(lines,contents.toArray(new String[0]));
    }

    public static List<String>  mutiLineLightUpWords(List<String> lines, Integer color,Integer defaultColor, String ... contentx) {
        if (contentx == null || contentx.length == 0) {
            return lines;
        }
        Map<String,Integer> colorMap = new HashMap<>();
        if (defaultColor == null) {
            defaultColor = COLOR_WHITE;
        }
        if (color != null) {
            for (String content : contentx) {
                colorMap.put(content,color);
            }
        }else {
            for (String content : contentx) {
                if (color == null) {
                    color = COLOR_RED;
                    colorMap.put(content, color);
                } else {
                    color = (color + 1) % 8;
                    if (color == defaultColor || color == COLOR_BLACK) {
                        color = (color + 1) % 8;
                    }
                    colorMap.put(content, color);
                }
            }
        }
        return mutiLineLightUpWords(lines,colorMap,defaultColor);
    }

    public static List<String> mutiLineLightUpWords(List<String> lines, Map<String,Integer> contentColorMap,Integer defaultColor) {
        List<String> result = new ArrayList<>();
        if (defaultColor == null) {
            defaultColor = COLOR_WHITE;
        }
        List<String> contents = new ArrayList<>(contentColorMap.keySet());
        if (contents.size() == 0) {
            return lines;
        }
        //优先匹配较长的内容
        contents.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o2.length() - o1.length();
            }
        });

        String profix = null;
        String profixContent = null;
        for (int i = 0; i < lines.size(); i++) {
            String currLine = lines.get(i);
            String nxtLine = i == lines.size() -1?null: lines.get(i+1);
            String[] res = color(currLine,nxtLine,profix,profixContent,contents,defaultColor,contentColorMap,result);
            if (res != null) {
                profix =res[1];
                profixContent = res[0];
            }else {
                profix =null;
                profixContent = null;
            }
        }
        return result;
    }


    private static String[] color(String currLine, String nxtLine, String profix, String profixContent,
                                 List<String> contents, int defaultColor, Map<String,Integer> colorMap, List<String> result) {
        if (StringExtUtils.isBlank(currLine) ) {
            result.add(currLine);
            return null;
        }
        if (StringExtUtils.isNotBlank(profix)) {
            currLine = currLine.substring(profix.length());
        }
        if (StringExtUtils.isBlank(currLine) ) {
            result.add(colorCurrLine(currLine,profix,profixContent,defaultColor,colorMap));
            return null;
        }
        int maxIdx = currLine.length() - 1;
        if (nxtLine == null) {
            result.add(colorCurrLine(currLine,profix,profixContent,defaultColor,colorMap));
            return null;
        }
        int idx = -1;
        String tarContent = null;
        for (String content : contents) {
            String join = currLine + nxtLine ;
            while (true) {
                idx = -1;
                if (!join.contains(content)) {
                    break;
                }
                idx = join.lastIndexOf(content);
                if (idx > maxIdx) {
                    join = join.substring(0, idx);
                    continue;
                }
                if (idx + content.length() <= maxIdx + 1) {
                    idx = -1;
                }
                break;
            }
            if (idx > 0) {
                tarContent = content;
                break;
            }
        }
        if (tarContent != null) {
            int len = maxIdx - idx + 1;

            String begin = tarContent.substring(0, len);
            String light = colorCurrLine(currLine.substring(0, idx), profix, profixContent, defaultColor, colorMap);
            light += AnsiUtils.colorString(begin, colorMap.get(tarContent));
            result.add(light);

            String end = tarContent.substring(len);
            return new String[]{tarContent, end};
        } else {
            result.add(colorCurrLine(currLine,profix,profixContent,defaultColor,colorMap));
            return null;
        }
    }

    private static String colorCurrLine(String currLine, String profix, String profixContent, int defaultColor, Map<String,Integer> colorMap) {
        StringBuilder s = new StringBuilder();
        if (StringExtUtils.isNotBlank(profix)) {
            s.append(AnsiUtils.colorString(profix, colorMap.get(profixContent)));
        }
        if (StringExtUtils.isNotBlank(currLine)) {
            s.append(AnsiUtils.lightUpWords(currLine,colorMap,defaultColor));
        }
        return s.toString();
    }


    //光标移动
    //光标向上移动 \u001b[{n}A
    //光标向下移动 \u001b[{n}B
    //光标向右移动 \u001b[{n}D
    //光标向左移动 \u001b[{n}C
    //光标按行向下移动并移至行首 \u001b[{n}E
    //光标按行向上移动并移至行首 \u001b[{n}F
    //设置光标坐在列，行数不变 \u001b[{n}G
    //设置光标所在坐标（行，列） \u001b[{n};{m}H
    //清除屏幕： \u001b[{n}J
    //        n=0 清除光标到屏幕末尾的所有字符
    //        n=1 清除屏幕开头到光标的所有字符
    //        n=2 清除整个屏幕的字符
    //清除行 \u001b[{n}K
    //        n=0 清除光标到当前行末所有的字符
    //        n=1 清除当前行到光标的所有的字符
    //        n=2 清除当前行

    public static String cleanLine() {
        return "\u001b[2K\u001b[1G";
    }

    public static String cleanScreen() {
        return "\033c";
    }

}

