package idv.const_x.tools.jdbc.utils;

import idv.const_x.tools.jdbc.info.AbsDBInfo;


public class ExpCommadBuilder {

  public static String getExp(String table, String where, AbsDBInfo schema) {
    StringBuilder sb = new StringBuilder("exp ");
    sb.append(schema.getUser());
    sb.append("/");
    sb.append(schema.getPasswd());
    sb.append("@");
    sb.append(schema.getHost());
    sb.append(" statistics= none compress= n consistent= y file= D:/");
    sb.append(table);
    sb.append(".dmp tables= ");
    sb.append(table);
    sb.append(" query= \\\"");
    sb.append(where);
    sb.append(" \\\";");
    return sb.toString();
  }

  public static String getImp(String table, AbsDBInfo schema) {
    StringBuilder sb = new StringBuilder("imp ");
    sb.append(schema.getUser());
    sb.append("/");
    sb.append(schema.getPasswd());
    sb.append("@");
    sb.append(schema.getHost());
    sb.append(" statistics= none commit=y ignore=y  file= D:/");
    sb.append(table);
    sb.append(".dmp tables= ");
    sb.append(table);
    return sb.toString();
  }

}
