package idv.const_x.console.ext.handler;

import idv.const_x.console.handler.AbsInputLineHandler;
import idv.const_x.console.handler.HelpBuilder;
import idv.const_x.console.io.ConsoleIOProxy;
import idv.const_x.utils.DateUtils;

import java.util.Date;
import java.util.Map;

/**
 * @Description <pre>
 *
 * </pre>
 * @Author const.x
 * @Date 2023-06-16
 */
public class TimeHandler extends AbsInputLineHandler {

    @Override
    public void handleLine(String line, ConsoleIOProxy console, Map<String, String> options, String... params) {
        Date date = null;
        if (params == null || params.length == 0 ) {
             date = new Date();
        }else {
            String time = String.join(" ",params).trim();
            if (time.contains(":") || time.contains("-")) {
                date = DateUtils.formatDate(time);
            }else {
                date = new Date(Long.parseLong(time));
            }
        }

        if (hasOptions(options,"r")) {
            Date dateStart = DateUtils.getDateStart(date);
            console.info(DateUtils.toDateTimeString(dateStart) + " " + String.valueOf(dateStart.getTime()) );
            Date dateEnd = DateUtils.getDateEnd(date);
            console.info(DateUtils.toDateTimeString(dateEnd) + " " + String.valueOf(dateEnd.getTime()) );
        }else {
            console.info(DateUtils.toDateTimeString(date) + " " + String.valueOf(date.getTime()) );
        }
    }


    @Override
    public String getCmd() {
        return "time";
    }

    public static void main(String[] args) {
        new TimeHandler().handleLine("time 2024-07-01 00:00:00", ConsoleIOProxy.getInstance("UTF-8"), null);
    }


    public HelpBuilder initHelpBuilder() {
        return new HelpBuilder(getCmd(),"时间戳互转")
                .appendParamDesc("value","时间内容")
                .appendOptionDesc("r","输出起止时间")
                .build();
    }
}
