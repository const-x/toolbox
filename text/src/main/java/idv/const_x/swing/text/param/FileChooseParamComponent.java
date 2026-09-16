package idv.const_x.swing.text.param;

import idv.const_x.swing.VFlowLayout;
import idv.const_x.utils.OSUtils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileChooseParamComponent extends AbsParamComponent<List<File>> {
	
	public FileChooseParamComponent(String key){
		super(key);
	}

    private final List<FileFilter> fileFilters = new ArrayList<>();
    private boolean muti = true;
    private String path = null;
    private List<File> choosedFiles = new ArrayList<>();
    private JFileChooser chooser = null;
    private  JTextField label = null;
    
	
	public static FileFilter JS = new FileNameExtensionFilter("JS", "js");
	public static FileFilter JAVA = new FileNameExtensionFilter("JAVA", "java");
	public static FileFilter JSP = new FileNameExtensionFilter("JSP", "jsp");
	public static FileFilter XML = new FileNameExtensionFilter("XML", "xml");
	public static FileFilter CSS = new FileNameExtensionFilter("CSS", "css");
	public static FileFilter DAT = new FileNameExtensionFilter("DAT", "dat");
	public static FileFilter TXT = new FileNameExtensionFilter("TXT", "txt");
	public static FileFilter EML = new FileNameExtensionFilter("EML", "eml");
	public static FileFilter ALL = null;
	
	public void addFileFilter(FileFilter fileFilter){
		fileFilters.add(fileFilter);
	}
	
	public void addFileFilterByExtension(String extension){
		FileFilter e = new FileNameExtensionFilter(extension.toUpperCase(), extension.toLowerCase());
		fileFilters.add(e);
	}
	
	public boolean isMuti() {
		return muti;
	}

	public void setMuti(boolean muti) {
		this.muti = muti;
	}
	
	

	public String getPath() {
		if (path == null) {
			path = OSUtils.getDesktopPath();
		}
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	protected JComponent initUI() {
		label = new JTextField();
		label.setEditable(false);
		JButton butt = new JButton("选择");
		JPanel fr1 = new JPanel(new BorderLayout());
		fr1.add(label,BorderLayout.CENTER);
		fr1.add(butt,BorderLayout.EAST);
		butt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(chooser == null){
					chooser = new JFileChooser(getPath());
					if (fileFilters != null) {
						for (FileFilter filter : fileFilters) {
							chooser.setFileFilter(filter);
						}
					}
					chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					chooser.setMultiSelectionEnabled(muti);
				}
				int returnVal = chooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					label.setText(chooser.getSelectedFile().getAbsolutePath());
					choosedFiles.clear();
					FileFilter swfilter = chooser.getFileFilter();
					FileFilterAdapter filterAdapt = null;
					if(swfilter != null){
						filterAdapt = new FileFilterAdapter(swfilter);
					}
					File[] files = chooser.getSelectedFiles();
					for (File file : files) {
						getFiles(file, filterAdapt);
					}
				}
			}
		});
		
		JPanel panel = new JPanel(new VFlowLayout() );
        JLabel label2 = new JLabel(getDescription());
        panel.add(label2);
        panel.add(fr1);
		return panel;
	}
	
	private void getFiles(File file,FileFilterAdapter filterAdapt){
		if (file.isDirectory()) {
			File[] files;
			if(filterAdapt != null){
				files = file.listFiles(filterAdapt);
			}else{
				files = file.listFiles();
			}
			for (File ch : files) {
				this.getFiles(ch, filterAdapt);
			}
		} else {
			choosedFiles.add(file);
		}
		
	}
	

	@Override
	public List<File> getValue() {
		return this.choosedFiles;
	}
	
	
	
	@Override
	public void setValue(List<File> value) {
		this.choosedFiles = value;
	}



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

	@Override
	public void clear() {
		choosedFiles.clear();
		label.setText("");
	}

}
