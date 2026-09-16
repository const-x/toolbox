package idv.const_x.file.excel.matcher;

import idv.const_x.file.excel.utils.ExcelUtils;

import java.util.Objects;

/**
 * 指定列内容是否相等
 */
public class EqualToMatcher implements ILineMatcher{
	
	private final int sColIndex;
    private final int aColIndex;

	
	public EqualToMatcher(int sColIndex,int aColIndex){
		this.sColIndex = sColIndex;
		this.aColIndex = aColIndex;
	}

	public EqualToMatcher(String sColIdxName,String aColIdxName){
		this.sColIndex = ExcelUtils.columnNameToNumber(sColIdxName);
		this.aColIndex = ExcelUtils.columnNameToNumber(aColIdxName);
	}


	@Override
	public boolean match(Object[] source,int sRowIdx ,Object[] aim,int aRowIdx) throws Exception {
		if(source.length <= sColIndex ){
			return false;
		}
		Object s = source[sColIndex];
		if(aim.length <= aColIndex ){
			return false;
		}
		Object a = aim[aColIndex];
		return match(source, sRowIdx,s, aim, aRowIdx,a);
	}

	protected boolean match(Object[] source,int sRowIdx ,Object s,Object[] aim,int aRowIdx,Object a){
		boolean m = Objects.equals(s,a);
		System.out.println("+       比对 源第"+ sRowIdx+ "行["+s+"]与目标第"+ aRowIdx + "行["+a+"],结果:" + m);
		return m;
	}

}
