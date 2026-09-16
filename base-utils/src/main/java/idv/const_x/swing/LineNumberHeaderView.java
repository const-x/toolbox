package idv.const_x.swing;

import javax.swing.*;
import java.awt.*;
//TEXTAREA 行号显示插件
public class LineNumberHeaderView extends javax.swing.JComponent {
 
    /**
	 * JAVA TextArea行数显示插件
	 */
	private static final long serialVersionUID = 1L;
    public final Color DEFAULT_BACKGROUD = new  Color(245, 245, 245);
    public final Color DEFAULT_FOREGROUD = Color.GRAY;
    public final int nHEIGHT = Integer.MAX_VALUE - 1000000;
    public final int MARGIN = 5;
    private int fontLineHeight;
    private int currentRowWidth;
    private FontMetrics fontMetrics;
 
    public LineNumberHeaderView(JTextArea input) {
        setFont(input.getFont());
        setForeground(DEFAULT_FOREGROUD);
        setBackground(DEFAULT_BACKGROUD);
        setPreferredSize(9999);
    }
 
    public void setPreferredSize(int row) {
        int width = fontMetrics.stringWidth(String.valueOf(row));
        if (currentRowWidth < width) {
            currentRowWidth = width;
            setPreferredSize(new Dimension(2 * MARGIN + width + 1, nHEIGHT));
        }
    }
 
    @Override
    public void setFont(Font font) {
        super.setFont(font);
        fontMetrics = getFontMetrics(getFont());
        fontLineHeight = fontMetrics.getHeight();
    }
 
    public int getStartOffset() {
        return 4;
    }
 
    @Override
    protected void paintComponent(Graphics g) {
        int nlineHeight = fontLineHeight;
        int startOffset = getStartOffset();
        Rectangle drawHere = g.getClipBounds();
        g.setColor(getBackground());
        g.fillRect(drawHere.x, drawHere.y, drawHere.width, drawHere.height);
        g.setColor(getForeground());
        int startLineNum = (drawHere.y / nlineHeight) + 1;
        int endLineNum = startLineNum + (drawHere.height / nlineHeight);
        int start = (drawHere.y / nlineHeight) * nlineHeight + nlineHeight - startOffset;
        for (int i = startLineNum; i <= endLineNum; ++i) {
            String lineNum = String.valueOf(i);
            int width = fontMetrics.stringWidth(lineNum);
            g.drawString(lineNum + " ", MARGIN + currentRowWidth - width - 1, start+2);
            start += nlineHeight;
        }
        setPreferredSize(endLineNum);
    }
}

