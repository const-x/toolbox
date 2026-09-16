package idv.const_x.tools.jdbc.utils;

import java.util.List;



/**
 * 构建插入语句
 * 
 * @since 6.3
 * @version 2013-05-07 13:36:41
 * @author const.x
 */
public class InsertBuilder {
  
  public static void getInserts(String sql, String[] types,
      List<List<Object>> data) {
    if (sql.startsWith(" update")) {
      return;
    }
    char[] chars = sql.toCharArray();
    int beginindex = sql.lastIndexOf("(");
    StringBuilder str;
    for (int i = 0; i < data.size(); i++) {
      str = new StringBuilder();
      str.append(sql, 0, beginindex);
      str.append(InsertBuilder.replacce(chars, beginindex, types, data.get(i)));
    }

  }

  private static String replacce(char[] chars, int beginindex,
		  String[] types, List<Object> data) {
    StringBuilder str = new StringBuilder();
    int index = 0;
    for (int i = beginindex; i < chars.length; i++) {
      if (chars[i] == '?') {
        if (types[index].equalsIgnoreCase("Integer")) {
          str.append(data.get(index));
        }
        else {
          str.append("'");
          str.append(data.get(index));
          str.append("'");
        }
        index++;
      }
      else {
        str.append(chars[i]);
      }
    }
    return str.toString();
  }
}
