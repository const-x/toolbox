package idv.const_x.swing;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

/**
 * description:自定义的Document
 * 可以控制最大行数
 *     默认最大为1000行
 *     超过最大行时，上面的一行将被截取
 * 
 * 
 */
public class LimitativeDocument extends PlainDocument{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4105181853944941570L;
	
    private final JTextComponent textComponent;
    private int lineMax = 1000;
    public   LimitativeDocument(JTextComponent tc,int lineMax){     
        textComponent = tc;
        this.lineMax = lineMax;
    }
    public   LimitativeDocument(JTextComponent tc){     
        textComponent = tc;
    }
    
    @Override
    public void insertString(int offset, String s, AttributeSet attributeSet) throws BadLocationException {
        
        String value =   textComponent.getText();   
        int overrun = 0;
        if(value!=null && value.indexOf(' ') >= 0 && value.split(" ").length>=lineMax){
            overrun = value.indexOf(' ')+1;
            super.remove(0, overrun);
        }
        super.insertString(offset-overrun,   s,   attributeSet);     
    }
}
