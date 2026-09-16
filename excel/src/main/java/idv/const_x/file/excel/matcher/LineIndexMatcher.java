package idv.const_x.file.excel.matcher;

public class LineIndexMatcher implements ILineMatcher{
	

	@Override
	public boolean match(Object[] source, int sRowIdx, Object[] aim, int aRowIdx) throws Exception {
		return sRowIdx == aRowIdx;
	}

}
