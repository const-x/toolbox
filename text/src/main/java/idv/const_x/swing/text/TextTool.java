package idv.const_x.swing.text;

import idv.const_x.swing.JOptionPaneExt;
import idv.const_x.swing.JSplitPaneExt;
import idv.const_x.swing.LimitativeDocument;
import idv.const_x.swing.LineNumberHeaderView;
import idv.const_x.swing.VFlowLayout;
import idv.const_x.swing.text.handler.AbsHandler;
import idv.const_x.swing.text.handler.BatchHandler;
import idv.const_x.swing.text.handler.LineMergeHandler;
import idv.const_x.swing.text.param.AbsParamComponent;
import idv.const_x.utils.FileUtils;
import idv.const_x.utils.OSUtils;
import idv.const_x.utils.StringExtUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author const.x
 */
public class TextTool{

	private  JTextArea mainInput, logOutput,mainOutput, descOutput;
	private  JPanel paramInputArea;
	private  JLabel label;
	private  JDialog dialog;
	private  JFrame window;

	private  final Map<JRadioButton, AbsHandler> handlers = new LinkedHashMap<JRadioButton, AbsHandler>();
	private  AbsHandler curHandler;

	private  IPrinter printer = null;

	private  boolean exitWhenClose = false;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new TextTool().regHandler(new BatchHandler()).regHandler(new LineMergeHandler()).exitWhenClose(false).run();
		});
	}

	public TextTool() {
		printer = new IPrinter() {

			private void ensureEdt(Runnable action) {
				if (SwingUtilities.isEventDispatchThread()) {
					action.run();
				} else {
					SwingUtilities.invokeLater(action);
				}
			}

			@Override
			public void print(String str,String desc) {
				ensureEdt(() -> {
					mainOutput.append(str);
					if(StringExtUtils.isNotBlank(desc)){
						descOutput.append(desc);
					}
					dialog.setVisible(true);
				});
			}

			@Override
			public void printLog(String str) {
				ensureEdt(() -> {
					logOutput.append(str);
					logOutput.append("\n");
				});
			}

			@Override
			public void clear() {
				ensureEdt(() -> {
					mainOutput.setText("");
					descOutput.setText("");
				});
			}

			@Override
			public void clearLog() {
				ensureEdt(() -> {
					logOutput.setText("");
				});
			}

			@Override
			public void clearWarn() {
				ensureEdt(() -> {
					label.setText("");
				});
			}

			@Override
			public void printWarn(String str) {
				ensureEdt(() -> {
					Toolkit.getDefaultToolkit().beep();
					label.setForeground(Color.red);
					label.setText(str);
				});
			}

			@Override
			public void printSuccess(String str) {
				final String message = StringExtUtils.isBlank(str) ? "操作成功" : str;
				ensureEdt(() -> {
					JOptionPaneExt.showTextMessageDialog("提示", message);
				});
			}

		};
	}


	public TextTool exitWhenClose(boolean exitWhenClose) {
		this.exitWhenClose = exitWhenClose;
		return this;
	}

	public void run() {
		// 单选
		ButtonGroup group = new ButtonGroup();
		for (JRadioButton jRadioButton : handlers.keySet()) {
			group.add(jRadioButton);
		}
		JPanel radios = new JPanel();
		radios.setLayout(new FlowLayout(FlowLayout.LEFT));
		for (JRadioButton jRadioButton : handlers.keySet()) {
			radios.add(jRadioButton);
		}

		JButton open = new JButton("打开");
		open.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				open();
			}

		});

		JButton save = new JButton("保存");
		save.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				save();
			}

		});

//		JButton submit = new JButton("确定");
		URL resource = TextTool.class.getResource("/images/logo-l.png");
		JButton submit = new JButton(new ImageIcon(new ImageIcon(resource).getImage().getScaledInstance(58, 18, Image.SCALE_AREA_AVERAGING)));
		submit.setPreferredSize(new Dimension(60, 20));
		submit.setMargin(new Insets(0,0,0,0));
		submit.setBackground(Color.green);
		submit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (curHandler == null) {
					printer.printWarn("请先选择操作类型");
					return;
				}
				String input = mainInput.getText();
				printer.clear();
				printer.clearLog();
				Map<String, Object> params = new HashMap<>();
				if(curHandler.getParamsComponent() != null){
					for (AbsParamComponent paramInput : curHandler.getParamsComponent()) {
						params.put(paramInput.getKey(), paramInput.getValue());
					}
				}
				// 使用 SwingWorker 在后台执行，避免阻塞 EDT
				new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						for (Entry<JRadioButton, AbsHandler> handler : handlers.entrySet()) {
							if (handler.getKey().isSelected()) {
								handler.getValue().handle(input, params);
								break;
							}
						}
						return null;
					}

					@Override
					protected void done() {
						try {
							get(); // 检查是否有异常
						} catch (Exception x) {
							printer.printWarn(x.getMessage());
						}
					}
				}.execute();
			}

		});

		JButton clear = new JButton("清空");
		clear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainInput.setText("");
			}
		});

		JPanel menuButton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		menuButton.add(open);
		menuButton.add(save);
		menuButton.add(submit);
		menuButton.add(clear);

		JPanel menuArea = new JPanel(new BorderLayout());
		menuArea.setBorder(new TitledBorder("操作"));
		menuArea.add(radios, BorderLayout.CENTER);
		menuArea.add(menuButton, BorderLayout.EAST);
		label = new JLabel(" ");
		menuArea.add(label, BorderLayout.SOUTH);

		mainInput = new JTextArea();
		mainInput.setLineWrap(true);
		JScrollPane mainInputJScroll = new JScrollPane(mainInput);
		mainInputJScroll.setRowHeaderView(new LineNumberHeaderView(mainInput));

		paramInputArea = new JPanel();
		JScrollPane paramInputJScroll = new JScrollPane(paramInputArea);

		JSplitPaneExt inputArea = new JSplitPaneExt();
		inputArea.addComponent(mainInputJScroll, paramInputJScroll, JSplitPane.HORIZONTAL_SPLIT);
		inputArea.setBorder(new TitledBorder("输入区"));
		
		logOutput = new JTextArea();
		logOutput.setLineWrap(true);
		logOutput.setRows(3);
		logOutput.setBackground(new Color(245, 245, 245));
		logOutput.setDocument(new LimitativeDocument(logOutput));
		logOutput.setEditable(false);
		JScrollPane logOutputjScroll = new JScrollPane(logOutput);
		JPanel logOutputArea = new JPanel(new BorderLayout());
		logOutputArea.setBorder(new TitledBorder(" "));
		logOutputArea.add(logOutputjScroll);

		JPanel panel = new JPanel(new BorderLayout(1, 1));
		panel.add(menuArea, BorderLayout.NORTH);
		
		JSplitPaneExt operArea = new JSplitPaneExt();
		operArea.addComponent(inputArea, logOutputArea, JSplitPane.VERTICAL_SPLIT);
		panel.add(operArea, BorderLayout.CENTER);
		
		window = new JFrame("文本处理工具");
		window.setIconImage(new ImageIcon("./images/logo-s.png").getImage());
		window.add(panel);
		window.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (curHandler != null && !curHandler.canExit()) {
					printer.printWarn("系统正在处理中 不能中断");
				} else {
					window.dispose();
					if (exitWhenClose) {
						System.exit(0);
					}
				}
			}
		});


		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e1) {
		}

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize(); 
		window.setBounds(50, 20, d.width - 100, d.height - 100);
	
		inputArea.setDividerLocation(d.width - 200);
		operArea.setDividerLocation(d.height - 200);
		window.setUndecorated(false);
		window.setExtendedState(JFrame.MAXIMIZED_BOTH);
		window.setVisible(true);
		window.getRootPane().setDefaultButton(submit);
		window.addComponentListener(new ComponentAdapter(){ 
            public void componentResized(ComponentEvent e) { 
            	operArea.reSetDividerLocation();
				inputArea.reSetDividerLocation();
            } 
        });


		JButton copy = new JButton("复制");
		copy.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				OSUtils.setSysClipboardText(mainOutput.getText());
			}
		});
		JButton copy2 = new JButton("复制到输入区");
		copy2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				mainInput.setText(mainOutput.getText());
				printer.clear();
				dialog.setVisible(false);
			}
		});
		JButton copy3 = new JButton("关闭");
		copy3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				printer.clear();
				dialog.setVisible(false);
			}
		});

		JPanel outAreaButton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		outAreaButton.add(copy);
		outAreaButton.add(copy2);
		outAreaButton.add(copy3);

		mainOutput = new JTextArea();
		mainOutput.setLineWrap(true);
		mainOutput.setRows(30);
		JScrollPane mainOutputjScroll = new JScrollPane(mainOutput);
		mainOutputjScroll.setRowHeaderView(new LineNumberHeaderView(mainOutput));

		descOutput = new JTextArea();
		descOutput.setBackground(new Color(228, 228, 228));
		descOutput.setDocument(new LimitativeDocument(descOutput));
		descOutput.setLineWrap(true);
		JScrollPane descOutputjScroll = new JScrollPane(descOutput);

		JSplitPaneExt outputArea = new JSplitPaneExt();
		outputArea.addComponent(mainOutputjScroll, descOutputjScroll, JSplitPane.VERTICAL_SPLIT);
		outputArea.setDividerLocation(560);

		JPanel outputPanel = new JPanel(new BorderLayout(2, 1));
		outputPanel.add(outAreaButton, BorderLayout.NORTH);
		outputPanel.add(outputArea, BorderLayout.CENTER);

		dialog = new JDialog(window, "处理结果", true);
		dialog.add(outputPanel);
		dialog.setBounds(40, 20, 1200, 700);
	}

	public TextTool regHandler(AbsHandler handler) {
		handler.setPrinter(printer);
		JRadioButton jRadioButton = new JRadioButton(handler.getName());
		jRadioButton.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					JRadioButton jRadioButton = (JRadioButton) e.getSource();
					AbsHandler handler = handlers.get(jRadioButton);
					label.setForeground(Color.BLACK);
					label.setText(handler.getDescription());
					List<AbsParamComponent> paramKeys = handler.getParamsComponent();
					paramInputArea.removeAll();
					paramInputArea.setLayout(new VFlowLayout());
					if (paramKeys != null) {
						for (AbsParamComponent param : paramKeys) {
							paramInputArea.add(param.getUI());
						}
					}
					curHandler = handler;
				}
			}
		});
		handlers.put(jRadioButton, handler);
		return this;
	}

	private  FileDialog opendialog;
	
	private  File opend;

	private  void open() {
		if (opendialog == null) {
			opendialog = new FileDialog(window, "打开", FileDialog.LOAD);
		}
		opendialog.setVisible(true);
		String path = opendialog.getDirectory();
		String name = opendialog.getFile();

		if (path != null && name != null) {

			opend = new File(path + name);
			try (InputStream is = new FileInputStream(opend);
				 InputStreamReader isr = new InputStreamReader(is, FileUtils.GetEncoding(opend.getPath()));
				 BufferedReader br = new BufferedReader(isr)) {
				while (br.ready()) {
					mainInput.append(br.readLine() + "\n");
				}
			} catch (Exception e1) {
				printer.printWarn(e1.getMessage());
			}

		}

	}

	private  FileDialog savedialog;

	private  void save() {
		if (savedialog == null) {
			savedialog = new FileDialog(window, "保存", FileDialog.SAVE);
			if(opend != null){
				savedialog.setFile(opend.getPath());
			}else{
				savedialog.setDirectory(OSUtils.getDesktopPath());
			}
		}
		savedialog.setVisible(true);
		if (savedialog.getDirectory() != null && savedialog.getFile() != null) {
			try (OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(savedialog.getDirectory() + savedialog.getFile()))) {
				out.write(mainInput.getText());
				out.flush();
			} catch (Exception e) {
				printer.printWarn(e.getMessage());
			}
		}
	}

}
