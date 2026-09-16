package idv.const_x.utils;

import java.util.Arrays;
import java.util.List;

import static idv.const_x.utils.AnsiUtils.COLOR_GRAY;
import static idv.const_x.utils.AnsiUtils.COLOR_GREEN;
import static idv.const_x.utils.AnsiUtils.COLOR_PURPLE;
import static idv.const_x.utils.AnsiUtils.COLOR_WHITE;
import static idv.const_x.utils.AnsiUtils.SHOW_OPPTION_INVERSE;
import static idv.const_x.utils.AnsiUtils.SHOW_OPPTION_TEXT_BLING;
import static idv.const_x.utils.AnsiUtils.SHOW_OPPTION_UNDER_LINE;
import static idv.const_x.utils.AnsiUtils.cleanLine;
import static idv.const_x.utils.AnsiUtils.cleanScreen;
import static idv.const_x.utils.AnsiUtils.color256String;
import static idv.const_x.utils.AnsiUtils.colorString;
import static idv.const_x.utils.AnsiUtils.colorStringWithBackgroud;
import static idv.const_x.utils.AnsiUtils.lightColorStringWithBackgroud;
import static idv.const_x.utils.AnsiUtils.lightUpWords;
import static idv.const_x.utils.AnsiUtils.mutiLineLightUpWords;

/**
 * @Description <pre>
 *
 * </pre>
 * @Author const.x
 * @Date 2024-02-04
 */
public class AnsiUtilsTest {

    public static void main(String[] args) {
        String s = lightColorStringWithBackgroud("hello", COLOR_GREEN, COLOR_WHITE);
        s += " ";
        s += colorStringWithBackgroud("world", COLOR_PURPLE,COLOR_GREEN, SHOW_OPPTION_TEXT_BLING,SHOW_OPPTION_UNDER_LINE,SHOW_OPPTION_INVERSE);
        System.out.println(s);

        System.out.println("16色");
        for (int i = 0; i <= 8; i++) {
            System.out.print(colorString(StringExtUtils.fillStringLen(String.valueOf(i),4,' ',0),i));
        }
        System.out.println("");
        System.out.println("256色");
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                int code = i * 16 + j;
                System.out.print(color256String(StringExtUtils.fillStringLen(String.valueOf(code),4,' ',0),code));
            }
            System.out.println();
        }

        System.out.println("文字标记");
        s = "1234506012356700123";
        System.out.println(s);
        System.out.println(lightUpWords(s,null,COLOR_GRAY,"123","1234","56"));
        System.out.println("跨行文字标记");
        List<String> lines = Arrays.asList("bbbbbabb", "babc", "cabb", "bb", "babb");
        List<String> contents = Arrays.asList("bbb", "bb", "cc");
        List<String> result = mutiLineLightUpWords(lines,null,COLOR_GRAY,contents.toArray(new String[0]));
        for (int i = 0; i < lines.size(); i++) {
            System.out.println(StringExtUtils.fillStringLen(lines.get(i),10,' ',0) + " : " + (i < result.size()?result.get(i):null) );
        }

        System.out.println();
        System.out.print("即将被清除的行");
        try {
            Thread.currentThread().sleep(1500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.print(cleanLine());
        try {
            Thread.currentThread().sleep(1500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.print("即将清除屏幕");
        try {
            Thread.currentThread().sleep(1500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.print(cleanScreen());
    }


}
