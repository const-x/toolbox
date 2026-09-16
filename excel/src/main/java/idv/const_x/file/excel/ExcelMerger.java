package idv.const_x.file.excel;

import idv.const_x.file.excel.matcher.ILineMatcher;
import idv.const_x.file.excel.utils.ExcelUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ExcelMerger implements AutoCloseable {
	

	List<Object[]> sources = new ArrayList<Object[]>();
	
	private final ExcelReader reader;

	
	public ExcelMerger(String path){
		reader = new ExcelReader(path);
		reader.loadSheet(0);
	}
	
	public ExcelMerger(String path, String sheet){
		reader = new ExcelReader(path);
		reader.loadSheet(sheet);
	}

	public void addSource(String path,String sheet,ILineMatcher matcher,SourceColumn ...columns){
		Object[] source = new Object[4];
		sources.add(source);
		source[0] = matcher;
		source[1] = columns;
		List<Line> lines = new ArrayList<Line>();
		source[2] = lines;
		source[3] = sheet + " @ " + path;
		try (ExcelReader sourceFile = new ExcelReader(path)) {
			if(StringUtils.isNotBlank(sheet)){
				sourceFile.loadSheet(sheet);
			}
			while (sourceFile.hasNext()) {
				Line line  = new Line();
				Object[] values = sourceFile.nextRow();
				line.rowIndex = sourceFile.getCurrentIndex();
				line.values = values;
				lines.add(line);
			}
		}
	}
	
	public void parse() throws Exception{
		try {
			while (reader.hasNext()) {
				Object[] values = reader.nextRow();
				System.out.println("处理 目标表单第" + reader.getCurrentIndex() + "行");
				for (Object[] obj : sources) {
					System.out.println("+   开始比对源表单:" + obj[3]);
					ILineMatcher matcher = (ILineMatcher) obj[0];
					SourceColumn[] cols = (SourceColumn[]) obj[1];
					List<Line> lines  = (List<Line>) obj[2];
					for (Line line : lines) {
						boolean match = matcher.match(line.values, line.rowIndex, values, reader.getCurrentIndex());
						if(match){
							for (SourceColumn col : cols) {
								if(col.sColIdx >= line.values.length){
									continue;
								}
								Object value = line.values[col.sColIdx];
								if(value != null){
									// 使用 Renderer 进行值转换
									if (col.renderer != null) {
										value = col.renderer.valueRenderer(value);
									}
									reader.updateCell(reader.getCurrentIndex(), col.aColIdx, value.toString());
								}
							}
						}
					}
				}
			}
			reader.flush();
			System.out.println("文件处理完成");
		} finally {
			this.close();
		}
	}
	
	public static class SourceColumn{
		Integer sColIdx, aColIdx;
		Renderer renderer;

	    
		public SourceColumn(int sColIdx,int aColIdx){
			this.sColIdx = sColIdx;
			this.aColIdx = aColIdx;
		}

		public SourceColumn(String sColIdxName,String aColIdxName){
			this.sColIdx = ExcelUtils.columnNameToNumber(sColIdxName);
			this.aColIdx = ExcelUtils.columnNameToNumber(aColIdxName);
		}

		public SourceColumn(int sColIdx,int aColIdx,Renderer renderer){
			this.sColIdx = sColIdx;
			this.aColIdx = aColIdx;
			this.renderer = renderer;
		}

		public SourceColumn(String sColIdxName,String aColIdxName,Renderer renderer){
			this.sColIdx = ExcelUtils.columnNameToNumber(sColIdxName);
			this.aColIdx = ExcelUtils.columnNameToNumber(aColIdxName);
			this.renderer = renderer;
		}
	}

	public static  interface Renderer{

		public Object valueRenderer(Object origVal);
	}
	
	class Line{
		protected int rowIndex;
		protected Object[] values;
	}

	/**
	 * 关闭 ExcelMerger，释放资源
	 */
	@Override
	public void close() {
		if (reader != null) {
			reader.close();
		}
		sources.clear();
	}
}

