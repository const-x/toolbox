package idv.const_x;


import idv.const_x.utils.StackTracesUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/**
 * @Description <pre>
 *
 * </pre>
 * @Author const.x
 * @Date 2023-09-06
 */
public abstract class LogBuilder<T extends LogBuilder>{

    private StringBuilder bodybuilder;

    private StringBuilder headbuilder;

    /** 单行日志记录最大长度 小于0则无限制*/
    private int maxLengthForLog = 1000;

    /** 单行日志记录超过最大长度时 拆分成指定行*/
    private int maxLineForLog = 1;

    private boolean changed = false;



    public LogBuilder() {
        init();
    }

    protected void  init(){
        headbuilder =  new StringBuilder();
        bodybuilder = new StringBuilder();
        changed = false;
    }

    public  T maxLength(int maxLengthForLog) {
        this.maxLengthForLog = maxLengthForLog;
        return (T)this;
    }

    public  T maxLine(int maxLineForLog) {
        this.maxLineForLog = maxLineForLog;
        return (T)this;
    }

    /**
     * 添加自定义附加内容到日志头 比如关联Id号
     * @param o
     * @return
     */
    public T appendHead(Object o){
        headbuilder.append("[").append(o == null?"null":o.toString()).append("]");
        changed = true;
        return (T)this;
    }


    public T append(Object o){
        bodybuilder.append(o == null?"null":o.toString());
        changed = true;
        return (T)this;
    }


    public T append(Object key, Object value){
        append(String.valueOf(key)).append(":").append(value).append(",");
        return (T)this;
    }

    public T appendError(String key, Throwable e){
        append(key).append(":").append(e.getMessage() == null? e.getClass().getSimpleName():e.getMessage()).append(",");
        return (T)this;
    }

    public  <E> T appendArray(Object key, Collection<E> list){
        appendArray(key,list,null,null);
        return (T)this;
    }

    /**
     * 打印集合中指定个数的内容
     * @param key
     * @param list
     * @param maxSize
     * @param func
     * @return
     * @param <R>
     * @param <E>
     */
    public <R, E> T appendArray(Object key, Collection<E> list, Integer maxSize, Function<E, R> func){
        append(String.valueOf(key)).append(":");
        if (list == null || list.size() == 0) {
            append(list == null? "null":"[]");
            return (T)this;
        }
        Iterator<E> iterator = list.iterator();
        int count = 0;
        startArray();
        while (iterator.hasNext()){
            if (maxSize != null && count >= maxSize) {
                append("'"+(list.size()-maxSize)+" more'");
                break;
            }
            E next = iterator.next();
            if (func == null) {
                append(next).append(",");
            }else{
                R apply = func.apply(next);
                append(apply).append(",");
            }
            count++;
        }
        endArray();
        return (T)this;
    }

    public T format(String format, Object... ar) {
        this.append(String.format(format,ar));
        return (T)this;
    }

    public T startObject() {
        append("{");
        return (T)this;
    }

    public T endObject() {
        delLastComma();
        append("},");
        return (T)this;
    }

    public T startArray() {
        append("[");
        return (T)this;
    }

    public T endArray() {
        delLastComma();
        append("],");
        return (T)this;
    }

    public T delLastComma() {
        if (bodybuilder.charAt(bodybuilder.length() - 1) == ',') {
            bodybuilder.deleteCharAt(bodybuilder.length() - 1);
        }
        return (T)this;
    }


    private String build(){
        delLastComma();
        return headbuilder.toString() + bodybuilder.toString();
    }

    @Override
    public String toString() {
        return build();
    }

    /**
     * 日志打印
     * @param logger
     * @param level
     */
    public <E> void print(ILogger<E> logger, E level){
        print(logger,level,build(),null);
    }

    /**
     * 日志打印
     * @param logger
     * @param level
     */
    public <E> void print(ILogger<E> logger, E level, Throwable e){
        print(logger,level,build(),e);
    }

    protected <E> void print(ILogger<E> logger,E level,String msg, Throwable e){
        if (logger.isEnabledFor(level) && changed) {
            //超过单个日志记录最大长度 则截取
            if (msg !=null && maxLengthForLog > 0 && msg.length() > maxLengthForLog) {
                if (maxLineForLog <= 1) {
                    msg = msg.substring(0, maxLengthForLog -3) + "...";
                    if (e != null) {
                        logger.log(level,msg, StackTracesUtils.filterStackTraces(e));
                    }else{
                        logger.log(level,msg);
                    }
                }else{
                    List<String> list = splitLog();
                    for (String s : list) {
                        if (e != null) {
                            logger.log(level,s, StackTracesUtils.filterStackTraces(e));
                        }else{
                            logger.log(level,s);
                        }
                    }
                }
            }else {
                if (e != null) {
                    logger.log(level,msg, StackTracesUtils.filterStackTraces(e));
                }else{
                    logger.log(level,msg);
                }
            }
        }
        init();
    }

    public List<String> splitLog() {
        return splitLog(maxLineForLog < 0);
    }

    public List<String> splitLog(boolean full) {
        List<String> result = new ArrayList<>();
        delLastComma();
        String body = bodybuilder.toString();
        String header = headbuilder.toString();
        //base的内容不写入message字段 因此不占长度
        int h = header.length();

        if (body.length() > maxLengthForLog - h) {

            int maxBodyLen = maxLengthForLog - h - 3;

            List<String> list = splitString(body, maxBodyLen);
            if (full){
                for (int i = 0; i < list.size(); i++) {
                    result.add(header+"["+i+"]"+list.get(i));
                }
            }else{
                for (int i = 0; i < list.size(); i++) {
                    if (i < maxLineForLog - 1) {
                        result.add(header+"["+i+"]"+list.get(i));
                    }else if (i == maxLineForLog - 1) {
                        String sub = list.get(i);
                        if (list.size() > 3) {
                            sub = (sub.substring(0,sub.length() - 3) + "...");
                        }
                        result.add(header+"["+i+"]"+sub);
                    }else{
                        break;
                    }
                }
            }
        }else {
            result.add(header+body);
        }
        return result;
    }

    private List<String> splitString(String input, int length) {
        List<String> result = new ArrayList<>();
        int index = 0;
        while (index < input.length()) {
            result.add(input.substring(index, Math.min(index + length, input.length())));
            index += length;
        }
        return result;
    }



    public T startWatch() {
        this.changed = false;
        return (T)this;
    }

    /**
     * 从日志构造器生成(或最近一处显示调用startWatch()的地方)开始,到当前日志内容是否发生变更 用于判定当前日志是否包含需要输出的内容
     * @return
     */
    public boolean isChanged(){
        return changed;
    }

    public static interface ILogger<E>{

        boolean isEnabledFor(E level);

        void log(E level,String msg);

        void log(E level,String msg,Throwable e);

    }

    //以下用于耗时统计支持

    List<TimeRecord> timeRecords;
    Long baseTime;

    Long startTime;

    public T startTimer() {
        startTime = System.currentTimeMillis();
        baseTime = startTime;
        timeRecords = new ArrayList<>();
        return (T)this;
    }

    public T reBaseTimer() {
        if (timeRecords == null) {
            startTimer();
        }else {
            baseTime = System.currentTimeMillis();
        }
        return (T)this;
    }

    /**
     * 从上一次recordTimer操作 到当前的耗时记录 如果不想从上一次recordTimer操作开始记录 可以在合适的地方使用reBaseTimer()开始计时
     * @param message
     * @return
     */
    public T recordTimer(String message) {
        return recordTimer(message,null);
    }

    /**
     * 从上一次recordTimer操作 或上一次使用reBaseTimer()开始计时
     * 到当前的耗时记录
     * @param message
     * @return
     */
    public T recordTimer(String message,Long tooklmt) {
        if (timeRecords == null) {
            startTimer();
        }
        long timeMillis = System.currentTimeMillis();
        if (tooklmt != null && timeMillis - baseTime < tooklmt) {
            return (T)this;
        }
        timeRecords.add(new TimeRecord(baseTime,timeMillis,message));
        baseTime = timeMillis;
        return (T)this;
    }

    public T appendTook(long tooklmt) {
        Long total = System.currentTimeMillis() - startTime;
        if (total > tooklmt) {
            if (timeRecords != null && timeRecords.size() > 0) {
                for (TimeRecord timeRecord : timeRecords) {
                    long took = timeRecord.curTime-timeRecord.baseTime;
                    append(timeRecord.msg).append("["+took +"ms];" );
                }
            }
            append("[total:"+total +"ms]");
        }
        return (T)this;
    }

    static class TimeRecord{
        Long baseTime,curTime;
        String msg;

        public TimeRecord(Long baseTime, Long curTime, String msg) {
            this.baseTime = baseTime;
            this.curTime = curTime;
            this.msg = msg;
        }
    }

    //public static class log4jAdapter implements ILogger<org.apache.log4j.Priority>{
    //
    //    org.apache.log4j.Logger logger = null;
    //
    //    public log4jAdapter(org.apache.log4j.Logger logger) {
    //        this.logger = logger;
    //    }
    //
    //    @Override
    //    public boolean isEnabledFor(org.apache.log4j.Priority level) {
    //        return logger.isEnabledFor(level);
    //    }
    //
    //    @Override
    //    public void log(org.apache.log4j.Priority level, String msg) {
    //        logger.log(level,msg);
    //    }
    //
    //    @Override
    //    public void log(org.apache.log4j.Priority level, String msg, Throwable e) {
    //        logger.log(level,msg,e);
    //    }
    //}
    //
    //
    //public void printDebug(org.apache.log4j.Logger logger){
    //    print(new log4jAdapter(logger),org.apache.log4j.Priority.DEBUG);
    //}
    //
    //public void printInfo(org.apache.log4j.Logger logger){
    //    print(new log4jAdapter(logger),org.apache.log4j.Priority.INFO);
    //}
    //
    //public void printWarn(org.apache.log4j.Logger logger){
    //    print(new log4jAdapter(logger),org.apache.log4j.Priority.WARN);
    //}
    //
    ///**
    // * 打印异常,自动过滤掉异常信息中无意义的堆栈信息后打印
    // * @param logger
    // * @param level
    // */
    //public void printWarn(org.apache.log4j.Logger logger, Throwable e){
    //    print(new log4jAdapter(logger),org.apache.log4j.Priority.WARN,e);
    //}
    //
    ///**
    // * 打印异常,自动过滤掉异常信息中无意义的堆栈信息后打印
    // * @param logger
    // * @param level
    // */
    //public void printError(org.apache.log4j.Logger logger, Throwable e){
    //    print(new log4jAdapter(logger),org.apache.log4j.Priority.ERROR,e);
    //}
    //
    //public void multiPrintDebug(org.apache.log4j.Logger logger){
    //    multiPrint(new log4jAdapter(logger),org.apache.log4j.Priority.DEBUG);
    //}
    //
    //public void multiPrintInfo(org.apache.log4j.Logger logger){
    //    multiPrint(new log4jAdapter(logger),org.apache.log4j.Priority.INFO);
    //}
}
