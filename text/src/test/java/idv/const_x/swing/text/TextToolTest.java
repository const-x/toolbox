package idv.const_x.swing.text;

import idv.const_x.swing.text.handler.BatchHandler;
import idv.const_x.swing.text.handler.LineCutHandler;
import idv.const_x.swing.text.handler.LineMergeHandler;

import java.io.IOException;

/**
 * @Description <pre>
 *
 * </pre>
 * @Author const.x
 * @Date 2024-02-03
 */
public class TextToolTest {


    public static void main(String[] args) throws IOException {
        new TextTool()
                .regHandler(new BatchHandler())
                .regHandler(new LineCutHandler())
                .regHandler(new LineMergeHandler())
                .run();
    }
}
