package idv.const_x.tools.jdbc.mutipage;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 跨页数据查询者
 * @author const.x
 *
 * @param <T>
 */
public class MutiPageQueryer<T,E> implements Iterable<T>{

	private IPageQueryExecutor<T,E> executor = null;
	
	private int currentPage = 1;
	
	private int pageSize = 1000;
	
	private E condition = null;
	
	private List<T> current = null;
	
	private int currentInx = -1;
	
	private Iterator<T> iterator = null;
	
	public MutiPageQueryer(IPageQueryExecutor<T,E> executor,E condition){
		this.executor = executor;
		this.condition = condition;
		this.iterator = new Iterator<T>(){

			@Override
			public boolean hasNext() {
				return MutiPageQueryer.this.hasNext();
			}

			@Override
			public T next() {
				return MutiPageQueryer.this.next();
			}
			
		};
	}
	
	public MutiPageQueryer(IPageQueryExecutor<T,E> executor,E condition,int pageSize){
		this.executor = executor;
		this.condition = condition;
		this.pageSize = pageSize;
		this.iterator = new Iterator<T>(){

			@Override
			public boolean hasNext() {
				return this.hasNext();
			}

			@Override
			public T next() {
				return this.next();
			}
			
		};
	}
	
	public boolean hasNext(){
		if(current == null){
			current = executor.doQuery(currentPage, pageSize,condition);
			if(current == null){
				current = Collections.emptyList();
			}
			System.out.println("[MutiPageQueryer]:查询第"+currentPage+"页数据,"+current.size()+"条");
			currentInx = -1;
			currentPage++;
		}
		if(current.size() == 0){
			return false;
		}
		if(currentInx < current.size() -1){
			currentInx++;
			return true;
		}else{
			current = null;
			return hasNext();
		}
		
	}
	
	public T next(){
		return current.get(currentInx);
	}

	@Override
	public Iterator<T> iterator() {
		return iterator;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getCurrentPage() {
		return currentPage;
	}
	
	
}
