package idv.const_x.file.excel.utils;

/**
 * @Description <pre>
 *
 * </pre>
 * @Author const.x
 * @Date 2024-01-26
 */
public class ExcelUtils {

    public static int columnNameToNumber(String columnName) {
        int number = 0;
        char[] chars = columnName.toUpperCase().toCharArray();
        for (int i = 0; i < chars.length; i++) {
            number = number * 26 + (chars[i] - 'A' + 1);
        }
        return number -1;
    }

    public static String columnNumberToName(int columnNumber) {
        if (columnNumber < 0) {
            throw new IllegalArgumentException("Column number must be greater than zero.");
        }
        //索引是从0开始的
        columnNumber =columnNumber +1;
        StringBuilder sb = new StringBuilder();
        while (columnNumber > 0) {
            int mod = (columnNumber - 1) % 26;
            sb.insert(0, (char) ('A' + mod));
            columnNumber = (columnNumber - mod) / 26;
        }
        return sb.toString();
    }
}
