package idv.const_x.file.line.handler;

import java.io.File;

/**
 * @Description <pre>
 *
 * </pre>
 * @Author const.x
 * @Date 2024-02-03
 */
public interface FileLineHandler {

    boolean startScaleFile(File file);

    boolean handleLine(File file,String preLine,String curline,String nextline,int lineIndex);

    void handleException(File file,String preLine,String curline,String nextline,int lineIndex,Exception e);

    void endScaleFile(File file);


}
