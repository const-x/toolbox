package idv.const_x.utils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * list 定长拆解器
 * @author const.x
 */
public class ListSpliter<T> {

	private List<T> source;
	
	private int step = 50;
	
	private int page = 1;
	
	private int currentIdx = 0;
	
     private final boolean deadlockwatcher = true;
	
	private AtomicInteger watcher = new AtomicInteger(0);
	
	public ListSpliter(T[] source){
		this.source = Arrays.asList(source);
	}

	public ListSpliter(List<T> source){
		this.source = source;
	}
	
	public ListSpliter(T[] source, int step){
		this.source = Arrays.asList(source);
		this.step = step;
	}
	
	public ListSpliter(List<T> source, int step){
		this.source = source;
		this.step = step;
	}

	public List<?> getSource() {
		return source;
	}

	public void reInit(List<T> source,int step) {
		this.watcher = new AtomicInteger(0);
		this.source = source;
		this.step = step;
		this.page = 1;
		this.currentIdx = 0;
	}
	
	public  void reInit(T[] source,int step){
		this.watcher = new AtomicInteger(0);
		this.source = Arrays.asList(source);
		this.step = step;
		this.currentIdx = 0;
	}
	
	public void reInit(List<T> source) {
		this.watcher = new AtomicInteger(0);
		this.source = source;
		this.currentIdx = 0;
	}

	public  void reInit(T[] source){
		this.watcher = new AtomicInteger(0);
		this.source = Arrays.asList(source);
		this.currentIdx = 0;
	}
	
	public int getStep() {
		return step;
	}


	public boolean hasNext(){
		if(deadlockwatcher){
			int counter = watcher.incrementAndGet();
			if(counter >=5 ){
				throw new RuntimeException("检测到对ListSpliter.hasNext()方法的多次调用,但未检测到对ListSpliter.next()方法的调用，可能存在死循环风险，强制中断");
			}
			if(counter > 1){
				System.out.println("warn: 未调用 ListSpliter 的next()方法,可能存在死循环风险 ");
			}
		}
		return this.source.size() > currentIdx;
	}
	
	public List<T> next(){
		if(deadlockwatcher){
			watcher.decrementAndGet();
		}
		if(currentIdx == 0 && source.size() <= step){
			currentIdx = source.size();
			return source;
		}
		if(source.size() <= currentIdx){
			return null;
		}
		int max = currentIdx+step;
		max = max >= source.size() ? source.size() :max;
		int min = currentIdx;
		currentIdx = max;
		this.page++;
		return source.subList(min, max);
	}

	public int getTotal() {
		return source.size();
	}

	public int getPage() {
		return page;
	}

	
}
