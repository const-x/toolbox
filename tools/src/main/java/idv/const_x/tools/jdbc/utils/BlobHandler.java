package idv.const_x.tools.jdbc.utils;

import java.io.Serializable;

//import oracle.sql.BLOB;

/**
 * Blob的读取写入
 * 
 * @since 6.3
 * @version 2013-05-09 10:53:11
 * @author const.x
 */
public class BlobHandler<E extends Serializable> {

//  public E BlobToObject(BLOB desblob) throws Exception {
//    ObjectInputStream in = new ObjectInputStream(desblob.getBinaryStream());
//    E obj = (E) in.readObject();
//    in.close();
//    return obj;
//  }
//
//  public Map<String, E> queryBlobObjs(String keyCln, String blobCln,
//      String table, String condition, Connection conn) throws Exception {
//    StringBuilder sb = new StringBuilder(" select ");
//    sb.append(keyCln);
//    sb.append(",");
//    sb.append(blobCln);
//    sb.append(" from ");
//    sb.append(table);
//    sb.append(" where ");
//    sb.append(condition);
//    Map<String, E> list = new HashMap<String, E>();
//    Statement stmt = conn.createStatement();
//    ResultSet rs = stmt.executeQuery(sb.toString());
//    while (rs.next()) {
//      String key = rs.getString(1);
//      BLOB blob = (BLOB) rs.getBlob(2);
//      E obj = this.BlobToObject(blob);
//      list.put(key, obj);
//    }
//    stmt.close();
//    conn.close();
//    return list;
//  }
//
//  public void save(InputStream in, String table, String blobCln,
//      String condition, Connection conn) throws Exception {
//    ResultSet rs = null;
//    conn.setAutoCommit(false);
//    Statement stmt = conn.createStatement();
//    // insert into table ( blobCln ) values('EMPTY_BLOB()');
//    String sql1 =
//        "select " + blobCln + " from " + table + " where " + condition
//            + " for update"; // 使用"FOR  UPDATE"得到表的写锁
//    rs = stmt.executeQuery(sql1);
//    if (rs.next()) {
//      BLOB blob = (BLOB) rs.getBlob(1); // 得到BLOB对象
//      @SuppressWarnings("deprecation")
//      OutputStream output = blob.getBinaryOutputStream(); // 建立输出流
//      int size = blob.getBufferSize();
//      byte[] buffer = new byte[size]; // 建立缓冲区
//      int len;
//      while ((len = in.read(buffer)) != -1) {
//        output.write(buffer, 0, len);
//      }
//      in.close();
//      output.flush();
//      output.close();
//    }
//    conn.commit();
//    stmt.close();
//    conn.close();
//  }
//
//  public void save(Serializable obj, String table, String blobCln,
//      String condition, Connection conn) throws Exception {
//    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
//    ObjectOutputStream outObj = new ObjectOutputStream(byteOut);
//    outObj.writeObject(obj);
//    final byte[] objbytes = byteOut.toByteArray();
//    ResultSet rs = null;
//    conn.setAutoCommit(false);
//    Statement stmt = conn.createStatement();
//    // insert into table ( blobCln ) values('EMPTY_BLOB()');
//    String sql1 =
//        "select " + blobCln + " from " + table + " where " + condition
//            + " for update"; // 使用"FOR  UPDATE"得到表的写锁
//    rs = stmt.executeQuery(sql1);
//    if (rs.next()) {
//      BLOB blob = (BLOB) rs.getBlob(1); // 得到BLOB对象
//      @SuppressWarnings("deprecation")
//      OutputStream output = blob.getBinaryOutputStream(); // 建立输出流
//      output.write(objbytes, 0, objbytes.length);
//      output.flush();
//      output.close();
//    }
//    conn.commit();
//    stmt.close();
//    conn.close();
//
//  }

}
