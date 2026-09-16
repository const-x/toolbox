package idv.const_x.swing.text.handler;

import idv.const_x.swing.text.param.BooleanParamComponent;
import idv.const_x.swing.text.param.FileChooseParamComponent;
import idv.const_x.swing.text.param.TextInputParamComponent;
import idv.const_x.utils.StringExtUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class LineCutHandler extends AbsHandler {

    public LineCutHandler() {

        TextInputParamComponent begin = new TextInputParamComponent("begin");
        begin.setDescription("截取前特征");
        super.addParamComponent(begin);

        TextInputParamComponent end = new TextInputParamComponent("end");
        end.setDescription("截取后特征");
        super.addParamComponent(end);
        BooleanParamComponent match = new BooleanParamComponent("match");
        match.setDescription("必须同时匹配");
        match.setDefaultValue(true);
        super.addParamComponent(match);

        BooleanParamComponent rmMuti = new BooleanParamComponent("rmMuti");
        rmMuti.setDescription("是否去重");
        rmMuti.setDefaultValue(true);
        super.addParamComponent(rmMuti);

        BooleanParamComponent nulLine = new BooleanParamComponent("nulLine");
        nulLine.setDescription("无匹配行是否展示");
        nulLine.setDefaultValue(false);
        super.addParamComponent(nulLine);

        FileChooseParamComponent p = new FileChooseParamComponent("files");
        p.setDescription("选择文件");
        super.addParamComponent(p);

    }


    @Override
    public void handle(String input, Map<String, Object> params) throws Exception {
        Object beginObj = params.get("begin");
        Object endObj = params.get("end");
        String begin = beginObj == null || beginObj.equals("") ? null : beginObj.toString();
        String end = endObj == null || endObj.equals("") ? null : endObj.toString();
        boolean rmMuti = (boolean) params.get("rmMuti");
        boolean nulLine = (boolean) params.get("nulLine");
        boolean match = (boolean) params.get("match");

        if (begin == null && end == null) {
            super.block("截取特征值必须设置");
            return;
        }

        Map<String, Integer> muti = new HashMap<>();
        Set<String> all = new HashSet<>();
        AtomicInteger total = new AtomicInteger(0);
        StringBuilder sb = new StringBuilder();

        List<File> files = (List<File>) params.get("files");
        for (File file : files) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
                reader.lines().forEach(line -> this.handleLine(line, begin, end, all, muti, sb, total, rmMuti, nulLine, match));
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        }

        if (StringExtUtils.isNotBlank(input)) {
            String[] res = input.split("\n");
            for (String line : res) {
                this.handleLine(line, begin, end, all, muti, sb, total, rmMuti, nulLine, match);
            }
        }

        StringBuilder sb2 = new StringBuilder();
        sb2.append("合计:").append(total.get()).append("项, 其中").append(all.size()).append("个有效项 ").append(muti.size()).append("个重复项").append("\n");
        if (!muti.isEmpty()) {
            sb2.append("\n----------重复项------------\n");
            for (Entry<String, Integer> entry : muti.entrySet()) {
                sb2.append(entry.getKey()).append(" : ").append(entry.getValue()).append("\n");
            }
        }
        super.print(sb.toString(), sb2.toString());
    }

    private void handleLine(String line, String begin, String end, Set<String> all, Map<String, Integer> muti, StringBuilder sb, AtomicInteger total, boolean rmMuti, boolean nulLine, boolean match) {
        if (line == null || line.trim().equals("")) {
            return;
        }
        int sIndex = 0;
        Integer eIndex = line.length();
        boolean bgMatched = true;
        boolean endMatched = true;

        if (StringExtUtils.isNotBlank(begin)) {
            if (!line.contains(begin)) {
                bgMatched = false;
            } else {
                sIndex = line.indexOf(begin) + begin.length();
                line = line.substring(sIndex);
            }
        }
        if (StringExtUtils.isNotBlank(end)) {
            if (!line.contains(end)) {
                endMatched = false;
            } else {
                eIndex = line.indexOf(end);
                line = line.substring(0,eIndex);
            }
        }

        if (match && !(bgMatched && endMatched)) {
            System.out.println(line);
            if (nulLine) {
                sb.append(line).append("\n");
            }
            return;
        }

        if (all.contains(line)) {
            if (muti.containsKey(line)) {
                muti.put(line, muti.get(line) + 1);
            } else {
                muti.put(line, 1);
            }
            if (!rmMuti) {
                sb.append(line).append("\n");
            }
        } else {
            all.add(line);
            sb.append(line).append("\n");
        }
        total.incrementAndGet();
    }


    @Override
    public String getName() {
        return "行截取";
    }

    @Override
    public String getDescription() {
        return "按指定关键字对每行数据进行截取处理";
    }

}
