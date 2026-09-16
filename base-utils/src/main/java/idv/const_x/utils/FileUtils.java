package idv.const_x.utils;


import idv.const_x.file.line.handler.FileLineHandler;
import idv.const_x.file.FileOutWriter;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


public class FileUtils {



	public static void openFile(File file) throws IOException {
		Desktop.getDesktop().open(file);
	}

	public static void openFile(String filename) throws IOException {
		File file = new File(filename);
		openFile(file);
	}
	
	public static File createFile(String filePath) {
		  File file = new File(filePath);
	      if (!file.exists()) {
	    	  file.getParentFile().mkdirs();
	          try {
				file.createNewFile();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
	      }
	      return file;
	  }


	   public static void copyFile(String source,String aim,boolean repalce) throws Exception{
		   FileOutWriter writer = new FileOutWriter(aim,repalce);
		   File file = new File(source);
		   InputStream input = new FileInputStream(file);
		   writer.write(input);
		   writer.close();
	   }
	   
	   public static void delete(String source) throws Exception{
		   File dir = new File(source);
		   if (dir.isDirectory()) {
	           String[] children = dir.list();
			   if (children != null) {
				   //递归删除目录中的子目录下
				   for (int i=0; i<children.length; i++) {
					   delete( source + File.separator + children[i]);
				   }
			   }
	       }
	       // 目录此时为空，可以删除
	       dir.delete();
	   }
		
	
	
	public static String readFile(String filePath) throws IOException{
		File file = new File(filePath);
		FileReader reader = new FileReader(file);
		
		StringBuilder builder = new StringBuilder();
		char[] buffer = new char[1024];
		int i = 0;
		while ((i =reader.read(buffer))!=-1){
			String str = new String(buffer,0 ,i);
			builder.append(str);
		}
		reader.close();
		return builder.toString();
	}


	public static String GetEncoding(String file)
	{
		String charset = "GBK";
		byte[] first3Bytes = new byte[3];
		try {
			boolean checked = false;
			InputStream is = new FileInputStream(file);
			int read = is.read(first3Bytes, 0, 3);

			if (read == -1)
				return charset;
			if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
				charset = "UTF-16LE";
				checked = true;
			} else if (first3Bytes[0] == (byte) 0xFE
					&& first3Bytes[1] == (byte) 0xFF) {
				charset = "UTF-16BE";
				checked = true;
			} else if (first3Bytes[0] == (byte) 0xEF
					&& first3Bytes[1] == (byte) 0xBB
					&& first3Bytes[2] == (byte) 0xBF) {
				charset = "UTF-8";
				checked = true;
			}else if (first3Bytes[0] == (byte) 0xA
					&& first3Bytes[1] == (byte) 0x5B
					&& first3Bytes[2] == (byte) 0x30) {
				charset = "UTF-8";
				checked = true;
			}else if (first3Bytes[0] == (byte) 0xD
					&& first3Bytes[1] == (byte) 0xA
					&& first3Bytes[2] == (byte) 0x5B) {
				charset = "GBK";
				checked = true;
			}else if (first3Bytes[0] == (byte) 0x5B
					&& first3Bytes[1] == (byte) 0x54
					&& first3Bytes[2] == (byte) 0x49) {
				charset = "windows-1251";
				checked = true;
			}
			//bis.reset();
			InputStream istmp = new FileInputStream(file);
			if (!checked) {
				int loc = 0;
				while ((read = istmp.read()) != -1) {
					loc++;
					if (read >= 0xF0)
						break;
					if (0x80 <= read && read <= 0xBF)
						break;
					if (0xC0 <= read && read <= 0xDF) {
						read = istmp.read();
						if (0x80 <= read && read <= 0xBF)
							continue;
						else
							break;
					} else if (0xE0 <= read && read <= 0xEF) {
						read = istmp.read();
						if (0x80 <= read && read <= 0xBF) {
							read = istmp.read();
							if (0x80 <= read && read <= 0xBF) {
								charset = "UTF-8";
								break;
							} else
								break;
						} else
							break;
					}
				}
			}
			is.close();
			istmp.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return charset;
	}
	
	public static void renameFile(String filePath,String name) throws IOException{
		File file = new File(filePath);
		renameFile(file,name);
	}
	
	public static void renameFile(File file,String name) throws IOException{
		String filePath = file.getPath();
		String base = filePath.substring(0,filePath.indexOf(file.getName()));
		file.renameTo(new File(base + name));
	}
	
	public static void readFile(File file, FileLineHandler handler, String encoding){
		int linenum = 0;
		String preLine = null;
		String currLine = null;
		String nextLine = null;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),encoding));
			if(handler.startScaleFile(file)){
				while (true) {
					String line = reader.readLine();
					preLine = currLine;
					currLine = nextLine;
					nextLine = line;
					if (currLine == null) {
						continue;
					}
					linenum++;
					if (!handler.handleLine(file, preLine, currLine, nextLine, linenum)) {
						break;
					}
					if (line == null) {
						break;
					}
				}
				handler.endScaleFile(file);
			}
		} catch (IOException ex) {
			handler.handleException(file, preLine, currLine, nextLine, linenum,ex);
		}finally{
			if(reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
    public static String getFileMD5(File file) {  
        if (!file.isFile()) {  
            return null;  
        }  
          
        MessageDigest digest = null;  
        FileInputStream in = null;  
        byte[] buffer = new byte[1024];
        int len;  
        try {  
            digest = MessageDigest.getInstance("MD5");  
            in = new FileInputStream(file);  
            while ((len = in.read(buffer, 0, 1024)) != -1) {  
                digest.update(buffer, 0, len);  
            }  
            in.close();  
  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        BigInteger bigInt = new BigInteger(1, digest.digest());  
  
        return bigInt.toString(16);  
    } 
	
   //-查找文件----------------------------------------------------------------------------
	public static List<File> findFiles(FileFilter[] filters) throws IOException {
		return findFiles(null, filters);
	}

	public static List<File> findFiles(String basePath, FileFilter ... filters) throws IOException {
        List<File> res = new ArrayList<File>();
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
			FileUtils util = new FileUtils();
			FileFilterAdapter filterAdapt = util.new FileFilterAdapter(swfilter);
			File[] files = chooser.getSelectedFiles();
			findFile(files,res,filterAdapt);
		}
		return res;
	}
	
	/**
	 * swing 文件过滤器 到 java io 文件过滤器的 适配
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
	
	
	private static void findFile(File[] files,List<File> res,FileFilterAdapter fliter){
		for(File file : files){
			if(file.isFile()){
				res.add(file);
			}else{
				File[] chFiles = file.listFiles(fliter);
				if (chFiles != null) {
					findFile(chFiles,res,fliter);
				}
			}
		}
		
	}
	
	//-end 查找文件-------------------------------------------------------------------------

	
}
