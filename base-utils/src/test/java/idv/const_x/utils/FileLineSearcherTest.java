package idv.const_x.utils;

import idv.const_x.file.line.FileLineReader;
import idv.const_x.file.line.handler.FileLineSearchHandler;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * @Description <pre>
 *
 * </pre>
 * @Author const.x
 * @Date 2024-02-03
 */
public class FileLineSearcherTest {


    public static void main(String[] args){
        String basePath = OSUtils.getDesktopPath();

        FileFilter TXT = new FileNameExtensionFilter("TXT", "txt");

        FileLineReader reader = new FileLineReader();

        FileLineSearchHandler handler = new FileLineSearchHandler();
        handler.setSearchInMutlLine(true);
        handler.addContent("bba","bbba","ccc");


        handler.init();
        reader.ChooseFiles(basePath,handler, TXT);
        handler.printResult();
    }
}
