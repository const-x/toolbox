package idv.const_x.swing;

import javax.swing.*;
import java.awt.*;

public class JSplitPaneExt extends JSplitPane {

	private double dividerLocation = 0.0;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -980413802053018430L;

	public void addComponent(JComponent com1, JComponent com2, int orientation) {
		this.setOneTouchExpandable(true);// 让分割线显示出箭头
		this.setContinuousLayout(true);// 操作箭头，重绘图形
		this.setOrientation(orientation);// 设置分割线方向
//		this.setDividerSize(10);// 设置分割线的宽度
		if (orientation == JSplitPane.HORIZONTAL_SPLIT) {
			this.setLeftComponent(com1);// 布局中添加组件 ，面板1
			this.setRightComponent(com2);// 添加面板2
		} else {
			this.setTopComponent(com1);
			this.setBottomComponent(com2);
		}
	}

	
	
	@Override
	protected void paintChildren(Graphics g) {
		int a = 0;
		int by = 0;
		if (this.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
			a = this.getLeftComponent().getWidth();
			by = getWidth() - getDividerSize();
		} else {
			a = this.getTopComponent().getHeight();
			by = getHeight() - getDividerSize();
		}
		if (a >= 0 && by >= 0) {
			dividerLocation =  (double) a / by;
		}
		super.paintChildren(g);
	}



	public void reSetDividerLocation() {
		if (dividerLocation > 0) {
			setDividerLocation(dividerLocation);
		}
	}

}
