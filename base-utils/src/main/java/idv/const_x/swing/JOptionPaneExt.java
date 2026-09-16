package idv.const_x.swing;

import idv.const_x.utils.OSUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class JOptionPaneExt {


	public static void showTextMessageDialog(String title,String msg) {
		JTextArea textArea = new JTextArea();
        textArea.setRows(30);
        textArea.setColumns(100);
        textArea.setLineWrap(true);
        textArea.setText(msg);
        textArea.setEditable(false);
        JScrollPane jScrollPane = new JScrollPane(textArea);
		int res = showCompomentDialog(title,"点击 是 复制内容到粘贴板",JOptionPane.OK_OPTION,jScrollPane);
		if(JOptionPane.OK_OPTION == res){
			OSUtils.setSysClipboardText(msg);
		}
	}
	


	public static String showTextInputDialog(String title) {
		return showTextInputDialog(title,null,null);
	}
	
	public static String showTextInputDialog(String title,String note,String defaulyValue) {
		JTextArea textArea = new JTextArea();
        textArea.setRows(30);
        textArea.setColumns(100);
        textArea.setLineWrap(true);
        if(defaulyValue != null){
        	textArea.setText(defaulyValue);
        }
        JScrollPane jScrollPane = new JScrollPane(textArea);
		int res = showCompomentDialog(title,note,JOptionPane.OK_CANCEL_OPTION,jScrollPane);
		if(res == JOptionPane.CLOSED_OPTION || res == JOptionPane.CANCEL_OPTION){
			return null;
		}
		String localObject = textArea.getText();
		return localObject;
	}
	
	public static String[] showDualTextInputDialog(String title) {
		return showDualTextInputDialog(title,null,null,null);
	}
	
	public static String[] showDualTextInputDialog(String title,String note,String defaulyValue1,String defaulyValue2) {
		JTextArea textArea1 = new JTextArea();
		textArea1.setRows(2);
		textArea1.setColumns(60);
		textArea1.setLineWrap(true);
        if(defaulyValue1 != null){
        	textArea1.setText(defaulyValue1);
        }
        JScrollPane jScrollPane1 = new JScrollPane(textArea1);
        
        JTextArea textArea2 = new JTextArea();
        textArea2.setRows(2);
        textArea2.setColumns(60);
        textArea2.setLineWrap(true);
        if(defaulyValue2 != null){
        	textArea2.setText(defaulyValue2);
        }
        JScrollPane jScrollPane2 = new JScrollPane(textArea2);
        
        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.add(jScrollPane1);
        panel.add(jScrollPane2);
		int res = showCompomentDialog(title,note,JOptionPane.OK_CANCEL_OPTION,panel);
		if(res == JOptionPane.CLOSED_OPTION || res == JOptionPane.CANCEL_OPTION){
			return null;
		}
		String[] localObject ={ textArea1.getText(),textArea2.getText()};
		return localObject;
	}

	public static int showCompomentDialog(String title,String note,int type,JComponent compoment) {
		JOptionPane localJOptionPane = new JOptionPane(note,JOptionPane.PLAIN_MESSAGE,type);

		localJOptionPane.add(compoment, 1);
		JDialog localJDialog = localJOptionPane.createDialog(localJOptionPane, title);
		localJDialog.setAlwaysOnTop(true);
		localJDialog.setVisible(true);

		
		Object  selectedValue = localJOptionPane.getValue();
		localJDialog.dispose();
        if(selectedValue == null){
        	return JOptionPane.CLOSED_OPTION;
        }
        if(selectedValue instanceof Integer){
        	return ((Integer)selectedValue).intValue();
        }
        return JOptionPane.CLOSED_OPTION;    
	}
	
	public static ProgressBarHandler showProgressBar(String title,boolean stoppable) {
		JPanel panel = new JPanel(new VFlowLayout() );
		JLabel mainlabel = new JLabel(" ");
		JLabel mainnum = new JLabel(" ");
		JPanel label1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		label1.add(mainlabel);
		label1.add(mainnum);
		JProgressBar main = new JProgressBar();
		main.setForeground(Color.GREEN);
		main.setBackground(new Color(245, 245, 245));
		main.setBorderPainted(false);
		main.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
            	if(!main.isIndeterminate()){
            		mainnum.setText(main.getValue() +"/"+ main.getMaximum());
            		if(main.getValue()  == main.getMaximum()){
            			mainlabel.setText("完成");
            		}
            	}
            }
        });
	    
		JLabel sublabel = new JLabel(" ");
		JLabel subnum = new JLabel(" ");
		JPanel label2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		label2.add(sublabel);
		label2.add(subnum);
		label2.setVisible(false);
		JProgressBar sub = new JProgressBar();
		sub.setForeground(Color.ORANGE);
		sub.setBackground(new Color(245, 245, 245));
		sub.setBorderPainted(false);
		sub.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
            	if(!sub.isIndeterminate()){
            		subnum.setText(sub.getValue() +"/"+ sub.getMaximum());
            		if(sub.getValue()  == sub.getMaximum()){
            			sublabel.setText("完成");
            		}
            	}
            }
        });
		sub.setVisible(false);
		
		JTextArea logOutput = new JTextArea();
		logOutput.setLineWrap(true);
		logOutput.setRows(2);
		logOutput.setBackground(new Color(245, 245, 245));
		logOutput.setDocument(new LimitativeDocument(logOutput));
		logOutput.setEditable(false);
		JScrollPane logOutputjScroll = new JScrollPane(logOutput);
		logOutputjScroll.setVisible(false);
		
		panel.add(label1);
		panel.add(main);
		panel.add(label2);
		panel.add(sub);
		panel.add(logOutputjScroll);
		
		JFrame window = new JFrame(title);
		window.setIconImage(new ImageIcon("images/logo-l.png").getImage());
		window.add(panel);
		window.setSize(600, 210);
		window.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if(stoppable){
					System.exit(0);
				}else{
					JOptionPane.showMessageDialog(null, "程序不可终止 将转入后台执行");
				}
			}
		});
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e1) {
		}
		window.setExtendedState(JFrame.EXIT_ON_CLOSE);
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize(); 
		window.setLocation((int)(d.getWidth() - window.getWidth())/2, (int)(d.getHeight() - window.getHeight())/2);
		window.setVisible(true);
		main.setIndeterminate(true);
		return new ProgressBarHandler(main, sub, mainlabel, sublabel, logOutput, window);
	}
	
	
	
	public static void main(String[] args) {
		ProgressBarHandler handler = showProgressBar("标题",false);
		handler.startMainProgress("test", null, null);
	}

}