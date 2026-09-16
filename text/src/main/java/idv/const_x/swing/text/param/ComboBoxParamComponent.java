package idv.const_x.swing.text.param;

import idv.const_x.swing.VFlowLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ComboBoxParamComponent<T> extends AbsParamComponent<T> {

    public ComboBoxParamComponent(String key) {
        super(key);
    }

    protected Map<String, T> items = new LinkedHashMap<>();

    protected JComboBox<String> jComboBox;

    private IChangeListener<T> listener;

    private boolean editable;

    private T selected;

    public void addItem(String name, T value) {
        items.put(name, value);
    }

    public void addChangeListener(IChangeListener<T> listener) {
        this.listener = listener;
    }


    public void setSelected(String key) {
        jComboBox.setSelectedItem(key);
    }

    @Override
    protected JComponent initUI() {
        jComboBox = new JComboBox<String>();
        for (Entry<String, T> item : items.entrySet()) {
            jComboBox.addItem(item.getKey());
            if (getDefaultValue() != null && item.getValue().equals(getDefaultValue())) {
                jComboBox.setSelectedItem(item.getKey());
                selected = getDefaultValue();
            }
        }
        jComboBox.setEditable(editable);
        JPanel panel = new JPanel(new VFlowLayout());
        JLabel label = new JLabel(getDescription());
        panel.add(label);
        panel.add(jComboBox);
        jComboBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                T s = items.get(jComboBox.getSelectedItem());
                if (listener != null) {
                    listener.onChange(selected, s);
                }
                selected = s;
            }


        });

        if (editable) {
            jComboBox.getEditor().getEditorComponent().addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    // 延迟执行，确保值已更新
                    SwingUtilities.invokeLater(() -> {
                        Object item = jComboBox.getSelectedItem();
                        String text = (item == null) ? "" : item.toString().trim();
                        T s;
                        if (items.containsKey(text)) {
                            s = items.get(text);
                        } else {
                            s = (T) text;
                        }
                        if (listener != null) {
                            listener.onChange(selected, s);
                        }
                        selected = s;
                    });
                }
            });

        }


        return panel;
    }

    @Override
    public T getValue() {
        return selected;
    }

    @Override
    public void setValue(T value) {
        for (Entry<String, T> e : items.entrySet()) {
            if (e.getValue().equals(value)) {
                this.selected = value;
                jComboBox.setSelectedItem(e.getKey());
                break;
            }
        }
    }

    @Override
    public void clear() {
        jComboBox.setSelectedItem(null);
        selected = null;
    }

    public interface IChangeListener<T> {
        void onChange(T orgi, T selected);
    }

    /**
     * 可编辑模式下 <T> 暂时只支持String类型
     *
     * @param editable
     */
    public void setEditable(boolean editable) {
        this.editable = editable;
    }
}

