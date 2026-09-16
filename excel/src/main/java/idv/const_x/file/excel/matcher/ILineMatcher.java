package idv.const_x.file.excel.matcher;

/**
 * excel行数据匹配器
 */
public interface ILineMatcher {
       
	    /**
	     * 
	     * @param source 源行数据
	     * @param sRowIdx 源行索引
	     * @param aim  目标行数据
	     * @param aRowIdx 目标行索引
	     * @return
	     * @throws Exception
	     */
		boolean match(Object[] source,int sRowIdx ,Object[] aim,int aRowIdx) throws Exception;
}
