package idv.const_x.tools.jdbc.mutipage;

import java.util.List;

public interface IPageQueryExecutor<T,E> {

	List<T> doQuery(int page, int pageSize, E context);
	
}
