package idv.const_x.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Description <pre>
 *
 * </pre>
 * @Author const.x
 * @Date 2022-10-13
 */
public class StringExtUtils {

    /**
     * 是否包含中文
     * @param str
     * @return
     */
    public static boolean containChinese(String str) {
        if (str == null || str.trim().equals("")) {
            return false;
        } else {
            for (char c : str.toCharArray()) {
                if (isChinese(c)){
                    return true;
                }
            }
            return false;
        }
    }

    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    public static int countChineseCharacters(String str) {
        if (str == null || str.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (char c : str.toCharArray()) {
            // 判断字符是否为中文字符
            if (isChineseChar(c)) {
                count++;
            }
        }
        return count;
    }

    public static boolean isChineseChar(char ch) {
        // 判断字符是否在中文字符的Unicode范围内
        // 中文汉字
        if (ch >= '\u4e00' && ch <= '\u9fa5') {
            return true;
        }
        // CJK 符号和标点
        if (ch >= '\u3000' && ch <= '\u303F') {
            return true;
        }
        // 全角 ASCII 字符（包括全角标点、字母、数字）
        if (ch >= '\uFF01' && ch <= '\uFF5E') {
            return true;
        }
        // 特殊补充：全角空格 \u3000 已包含在上面，但有时也单独强调
        // 注意：\u3000 是中文全角空格，已包含在 \u3000-\u303F 中

        return false;
    }



    public static String replaceUtf8mb4(String str) {
        if (null == str) {
            return str;
        }
        final int LAST_BMP = 0xFFFF;
        StringBuilder sb = new StringBuilder(str.length());
        for (int i = 0; i < str.length(); i++) {
            int codePoint = str.codePointAt(i);
            if (codePoint < LAST_BMP) {
                sb.appendCodePoint(codePoint);
            } else {
                if (i < str.length() - 1) {
                    sb.append("\\u").append(Integer.toHexString(str.charAt(i)))
                            .append("\\u").append(Integer.toHexString(str.charAt(i + 1)));
                    i++;
                } else {
                    // 逻辑到这里说明存在尾部单字符utf8mb，直接去除
                }
            }
        }
        return sb.toString();
    }

    private static final Pattern UTF8MB4_PATTERN = Pattern.compile("\\\\u([0-9a-zA-Z]{4})\\\\u([0-9a-zA-Z]{4})");

    /**
     * 将uxxxxuxxxx格式字符串还原成utf8mb字符
     *
     * @param str
     * @return
     */
    public static String regainUtf8mb4(String str) {
        if (null == str) {
            return str;
        }
        Matcher matcher = UTF8MB4_PATTERN.matcher(str);
        StringBuffer sb = new StringBuffer(str.length());
        while (matcher.find()) {
            matcher.appendReplacement(sb, new String(new char[]{(char) Integer.parseInt(matcher.group(1), 16),
                    (char) Integer.parseInt(matcher.group(2), 16)}));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static String removeUtf8Mb4(String text)  {
        byte[] bytes = new byte[0];
        try {
            bytes = text.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        int i = 0;
        while (i < bytes.length) {
            short b = bytes[i];
            if (b > 0) {
                buffer.put(bytes[i++]);
                continue;
            }
            b += 256;
            if ((b ^ 0xC0) >> 4 == 0) {
                buffer.put(bytes, i, 2);
                i += 2;
            }
            else if ((b ^ 0xE0) >> 4 == 0) {
                buffer.put(bytes, i, 3);
                i += 3;
            }
            else if ((b ^ 0xF0) >> 4 == 0) {
                i += 4;
            }
        }
        buffer.flip();
        try {
            return new String(buffer.array(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return text;
    }


    private static final Pattern pattern = Pattern.compile("-?[0-9]+(\\.[0-9]+)?");

    /**
     * 匹配是否为数字
     */
    public static boolean isNumeric(String str) {
        // 该正则表达式可以匹配所有的数字 包括负数
        String numberStr;
        try {
            numberStr = new BigDecimal(str).toString();
        } catch (Exception e) {
            return false;
        }

        Matcher isNum = pattern.matcher(numberStr);
        return isNum.matches();
    }

    public static boolean hasEmoji(String str) {
        if (null == str) {
            return false;
        }
        final int LAST_BMP = 0xFFFF;
        for (int i = 0; i < str.length(); i++) {
            if (str.codePointAt(i) >= LAST_BMP) {
                return true;
            }
        }
        return false;
    }


    public static String removeEmojis(String str) {
        if(str == null || str.trim().equals("")){
            return str;
        }
        str = str.replaceAll("[\ud800\udc00-\udbff\udfff\ud800-\udfff]", "");
        return str;
    }


    /**
     *
     * @param value
     * @param len
     * @return
     */
    public static String fillStringLen(String value,int len){
        return fillStringLen(value, len, ' ', 0,false,false);
    }

    /**
     *
     * @param value
     * @param len
     * @return
     */
    public static String fillZhLen(String value,int len){
        return fillStringLen(value, len, ' ', 0,false,true);
    }

    /**
     *
     * @param value
     * @param len
     * @param fill
     * @param direction 1 前添加 ，0 后添加
     * @return
     */
    public static String fillStringLen(String value,int len,char fill,int direction){
        return fillStringLen(value, len, fill, direction,false,false);
    }

    /**
     *
     * @param value
     * @param len
     * @param fill
     * @param direction 1 前添加 ，0 后添加
     * @param cut 超长内容是否截断
     * @param caseZh 中文按两个长度处理
     * @return
     */
    public static String fillStringLen(String value,int len,char fill,int direction,boolean cut,boolean caseZh){
        if (value == null) {
            value = "null";
        }
        int slen = value.length();
        if (caseZh) {
            int i = countChineseCharacters(value);
            slen = slen - i + i * 2;
        }

        if(slen > len && cut){
            if(direction > 0){
                return value.substring(slen - len);
            }
            return value.substring(0, len);
        }
        if(slen < len){
            if(direction > 0){
                StringBuilder num = new StringBuilder();
                for (int j = 0; j < len - slen; j++) {
                    num.append(fill);
                }
                num.append(value);
                return num.toString();
            }
            StringBuilder num = new StringBuilder(value);
            for (int j = 0; j < len - slen; j++) {
                num.append(fill);
            }
            return num.toString();
        }
        return value;
    }

    public static boolean isNotBlank(String value){
        return value != null  && !value.trim().equals("");
    }

    public static boolean isBlank(String value){
        return  !isNotBlank(value);
    }

    /**
     * 保留 前begin长度 和后面end长度的内容 其他替换为*
     * @param data
     * @param begin
     * @param end
     * @return
     * @throws Exception
     */
    public static String sensitive(String data,int begin, int end) throws Exception {
        if (isBlank(data) || (begin<=0 && end <= 0)) {
            return data;
        }
        if (begin < 0) {
            begin = 0;
        }
        if (end < 0) {
            end = 0;
        }
        if (data.length() <= begin && begin > 0) {
            return ( data.length() == 1 ? data : data.substring(0,data.length() -1) )+ "*";
        }
        String header = begin > 0 ? data.substring(0,begin) : "";
        if (end <= 0) {
            return header + "*";
        }

        int left =  data.length() - begin;
        if (end == left) {
            end = left -1;
        }
        String tail = end > 0 ? data.substring(data.length() - end) : "";

        return header + "*" + tail;
    }

    public static <T> String join(String delimiter, T... values) {
        return Arrays.stream(values).map(String::valueOf).collect(Collectors.joining(delimiter));
    }

    public static String replaceZeroWidthChars(String text) {
        // 零宽度字符的Unicode码点
        char[] zeroWidthChars = {'\u200B', '\u200C', '\u200D', '\uFEFF'};

        StringBuilder s = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {

            char currentChar = text.charAt(i);
            for (char zwChar : zeroWidthChars) {
                if (currentChar == zwChar) {
                    s.append(Integer.toHexString(currentChar));
                }else{
                    s.append(currentChar);
                }
            }
        }
        return s.toString();
    }


}
