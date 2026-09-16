package idv.const_x.file.line;


import idv.const_x.file.line.handler.FileLineHandler;
import idv.const_x.utils.FileUtils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * 
 * 
 * @since 6.3
 * @version 2014-04-23 10:35:15
 * @author const.x
 */
public class FileLineReader {

	private String encoding = "UTF-8";

	
	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}


	private FileLineHandler handler;

	public void ChooseFiles(FileLineHandler handler,FileFilter ...  filters)  {
		this.ChooseFiles(null,handler,filters);
	}

	public void ChooseFiles(String basePath,FileLineHandler handler,FileFilter ... filters) {
		this.handler = handler;

		JFileChooser chooser = new JFileChooser(basePath);
		if (filters != null) {
			for (FileFilter filter : filters) {
				chooser.setFileFilter(filter);
			}
		}
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser.setMultiSelectionEnabled(true);
		int returnVal = chooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			FileFilter swfilter = chooser.getFileFilter();
			FileFilterAdapter filterAdapt = null;
			if(swfilter != null){
				filterAdapt = new FileFilterAdapter(swfilter);
			}
			File[] files = chooser.getSelectedFiles();
			readFiles(files,handler,filterAdapt);
		}
	}


	public void readFiles(File[] files, FileLineHandler handler, java.io.FileFilter  filter) {
		this.handler = handler;
		for (File file : files) {
			this.findLines(file, filter);
		}
	}



	private void findLines(File file, java.io.FileFilter filter)  {
		if (file.isDirectory()) {
			File[] files;
			if(filter != null){
				files = file.listFiles(filter);
			}else{
				files = file.listFiles();
			}
			if (files != null) {
				for (File ch : files) {
					this.findLines(ch, filter);
				}
			}
		} else {
			FileUtils.readFile(file,this.handler,encoding);
		}
	}


	/**
	 * swing 文件过滤器 到 java io 文件过滤器的 适配
	 *
	 * @since 6.3
	 * @version 2014-04-23 10:25:28
	 * @author const.x
	 */
	class FileFilterAdapter implements java.io.FileFilter {

		private final FileFilter swfilter;

		public FileFilterAdapter(FileFilter swfilter) {
			this.swfilter = swfilter;
		}

		@Override
		public boolean accept(File pathname) {
			return this.swfilter.accept(pathname);
		}

	}




}
