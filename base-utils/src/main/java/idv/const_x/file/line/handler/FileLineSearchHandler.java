package idv.const_x.file.line.handler;


import idv.const_x.file.line.handler.FileLineHandler;
import idv.const_x.utils.AnsiUtils;
import idv.const_x.utils.StringExtUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileLineSearchHandler implements FileLineHandler {

    protected List<String> contents = new ArrayList<>();

    protected Boolean searchInMutlLine = false;

    private int filecounter = 0;
    private int filefounded = 0;
    private int totalfounded = 0;

    private int maxLineLen = -1;
    private boolean hasFoundinCurFile = false;

    private Map<String,Map<Integer,List<String>>> result = new HashMap<>();

    /**
     * @param searchInMutlLine the searchInMutlLine to set
     * @author const.x
     * @createDate 2014年8月18日
     */
    public void setSearchInMutlLine(Boolean searchInMutlLine) {
        this.searchInMutlLine = searchInMutlLine;
    }


    @Override
    public boolean startScaleFile(File file) {
        System.out.println("开始扫描:" + file.getAbsoluteFile());
        lineRef.clear();
        this.filecounter++;
        return true;
    }

    @Override
    public boolean handleLine(File file, String preLine, String currLine, String nextLine, int lineNum) {
        if (this.searchLine(file, preLine, currLine, nextLine, lineNum)) {
            totalfounded++;
        }
        return true;
    }

    @Override
    public void endScaleFile(File file) {
        if (this.hasFoundinCurFile) {
            this.filefounded++;
        }
        this.hasFoundinCurFile = false;
    }

    private String getWappedLineNum(int lineNum) {

        if (maxLineLen < 0) {
            for (Map<Integer, List<String>> map : result.values()) {
                for (Integer integer : map.keySet()) {
                    int length = String.valueOf(integer).length();
                    if (length > maxLineLen) {
                        maxLineLen = integer;
                    }
                }
            }
        }

        String s = StringExtUtils.fillStringLen(String.valueOf(lineNum), maxLineLen, ' ', 0);
        return s + ": ";
    }


    @Override
    public void handleException(File file, String preLine, String curline, String nextline, int lineIndex, Exception e) {

    }


    protected boolean searchLine(File file, String preLine, String currLine, String nextLine, int lineNum) {
        boolean foundedInLine = this.searchInSignLine(file,lineNum,preLine, currLine, nextLine);
        boolean foundedInMutLIne = false;
        if (this.searchInMutlLine) {
            foundedInMutLIne = this.searchInMutlLine(file,lineNum,preLine, currLine, nextLine);
        }

        if (foundedInLine || foundedInMutLIne) {
            this.hasFoundinCurFile = true;
            return true;
        } else {
            return false;
        }
    }

    private Map<Integer,Integer> lineRef = new HashMap<>();

    private boolean searchInMutlLine(File file,int lineNum,String preLine, String currLine, String nextLine) {
        if (StringExtUtils.isBlank(currLine) ) {
            return false;
        }
        if (StringExtUtils.isBlank(preLine)) {
            return false;
        }
        for (String content : contents) {
            String line = preLine.length() > content.length() - 1 ? preLine.substring(preLine.length() - content.length() + 1):preLine;
            line  += currLine.length() > content.length() - 1 ? currLine.substring(0,content.length() - 1):currLine;
            if (line.contains(content)) {
                if (lineRef.containsKey(lineNum - 1)) {
                    int refLine = lineRef.get(lineNum - 1);
                    result.computeIfAbsent(file.getAbsolutePath(), x -> new HashMap<Integer, List<String>>()).get(refLine).add(currLine);
                    lineRef.put(lineNum,refLine);
                }else {
                    ArrayList<String> list = new ArrayList<>();
                    list.add(preLine);
                    list.add(currLine);
                    result.computeIfAbsent(file.getAbsolutePath(), x -> new HashMap<Integer, List<String>>()).put(lineNum-1, list);
                    lineRef.put(lineNum,lineNum-1);
                }


                return true;
            }
        }
        return false;
    }

    private boolean searchInSignLine(File file,int lineNum,String preLine, String currLine, String nextLine) {
        if (StringExtUtils.isBlank(currLine)) {
            return false;
        }
        for (String content : contents) {
            if (currLine.contains(content)) {
                result.computeIfAbsent(file.getAbsolutePath(), x -> new HashMap<Integer, List<String>>()).put(lineNum, Arrays.asList(currLine));
                return true;
            }
        }
        return false;
    }


    public void addContent(String ... content) {
        for (String s : content) {
            this.contents.add(s);
        }
    }

    public void setContents(List<String> contents) {
        this.contents = contents;
    }

    public void init(){
        contents.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o2.length() - o1.length();
            }
        });
        filecounter = 0;
        filefounded = 0;
        totalfounded = 0;
        maxLineLen = -1;
        hasFoundinCurFile = false;
        result = new HashMap<>();
    }

    public void printResult(){
        System.out.println("共扫描" + filecounter + "个文件,发现" + filefounded + "个文件共" + totalfounded + "行内容");
        for (Map.Entry<String, Map<Integer, List<String>>> entry : result.entrySet()) {
            System.out.println(AnsiUtils.colorString(entry.getKey(),AnsiUtils.COLOR_GREEN) );

            for (Map.Entry<Integer, List<String>> line : entry.getValue().entrySet()) {
                List<String> value = line.getValue();
                if (value.size() > 1) {
                    List<String> lights = AnsiUtils.mutiLineLightUpWords(value, contents);
                    for (int i = 0; i < lights.size(); i++) {
                        System.out.println(AnsiUtils.colorString(getWappedLineNum(line.getKey() + i),AnsiUtils.COLOR_GRAY) + lights.get(i));
                    }
                }else{
                    System.out.println(AnsiUtils.colorString(getWappedLineNum(line.getKey()),AnsiUtils.COLOR_GRAY) + AnsiUtils.lightUpWords(value.get(0),contents));
                }
                System.out.println();
            }
        }
    }
}
