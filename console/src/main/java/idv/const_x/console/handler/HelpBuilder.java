package idv.const_x.console.handler;

import idv.const_x.utils.ColoredStringUtils;
import idv.const_x.utils.StringExtUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description <pre>
 *
 * </pre>
 * @Author const.x
 * @Date 2023-08-29
 */
public class HelpBuilder {

    private List<Arg> args = new ArrayList<>();


    private List<Param> params = new ArrayList<>();

    private Map<String,Option> options = new HashMap<>();

    private String help = "";

    private String cmd = "";

    private String desc = "";

    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return getHelp();
    }


    public HelpBuilder(String cmd,String desc) {
        this.cmd = cmd;
        this.desc = desc;
    }


    /**
     * 必要参数
     * @param name
     * @param desc
     * @return
     */
    public HelpBuilder appendParamDesc(String name,String desc){
        appendParamDesc(name,desc,true);
        return this;
    }

    /**
     * 参数
     * @param name
     * @param desc
     * @return
     */
    public HelpBuilder appendParamDesc(String name,String desc,boolean required){
        Param param = new Param(name, desc, required);
        args.add(param);
        params.add(param);
        return this;
    }

    public HelpBuilder appendParamDesc(Param param){
        args.add(param);
        params.add(param);
        return this;
    }

    /**
     * 配置项
     * @param name
     * @param desc
     * @return
     */
    public HelpBuilder appendOptionDesc(String name,String desc){
        appendOptionDesc(name, desc,false);
        return this;
    }

    /**
     * 带输入信息的配置项
     * @param name
     * @param desc
     * @return
     */
    public HelpBuilder appendOptionDesc(String name,String desc,boolean needValue){
        Option option = new Option(name, desc, needValue);
        args.add(option);
        options.put(name,option);
        return this;
    }

    /**
     * 带输入信息的配置项
     * @param name
     * @param desc
     * @param selects 可选值
     * @return
     */
    public HelpBuilder appendOptionDesc(String name,String desc,String ... selects){
        Option option = new Option(name, desc, selects);
        options.put(name,option);
        args.add(option);
        return this;
    }

    public HelpBuilder appendOptionDesc(Option option){
        options.put(option.name,option);
        args.add(option);
        return this;
    }

    public HelpBuilder build(){
        StringBuilder builder = new StringBuilder();
        builder.append(ColoredStringUtils.colorString(StringExtUtils.fillStringLen(cmd, 8, ' ', 0) + ":" + desc, ColoredStringUtils.COLOR_GREEN));

        if (args.size() > 0) {
            builder.append("\n\t   ").append(cmd).append(" ");
            StringBuilder parambuilder = new StringBuilder();
            for (Arg arg : args) {
                builder.append(" ").append(arg.cmd());
                parambuilder.append("\n\t\t    - ").append(arg.getHelper());
            }
            builder.append(parambuilder);
        }
        this.help = builder.toString();
        return this;
    }

    public List<Arg> getArgs() {
        return args;
    }

    public List<Param> getParams() {
        return params;
    }

    public Map<String, Option> getOptions() {
        return options;
    }

    public String getHelp() {
        if (StringExtUtils.isBlank(help)) {
            build();
        }
        return help;
    }


    public static abstract class Arg<T extends Arg>{
        protected String name,desc;
        boolean required = true;

        List<String> selects = new ArrayList<>();

        public Arg(String name, String desc) {
            this.name = name;
            this.desc = desc;
        }

        public Arg(String name,String desc,Boolean required) {
            this.name = name;
            this.desc = desc;
            this.required = required;
        }

        public T addSelecet(String ... values){
            for (String value : values) {
                selects.add(value);
            }
            return (T) this;
        }

        public T addSelecet(Collection<String> values){
            selects.addAll(values);
            return (T) this;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        abstract String cmd();

        String getHelper() {
            return StringExtUtils.fillStringLen(cmd(),12) + " : " + desc +(required?" 必需":"");
        }

    }
    public static class Param extends Arg<Param> {

        public Param(String name, String desc) {
            super(name, desc);
        }

        public Param(String name, String desc, Boolean required) {
            super(name, desc, required);
        }

        @Override
        String cmd() {
            return (required?"<":"[") +name +(required?">":"]");
        }
    }

    public static class Option extends Arg<Option> {

        boolean hasValue = false;

        boolean valueRequired = false;

        public Option(String name,String desc,Boolean valueRequired) {
            super(name,desc);
            super.required = false;
            this.valueRequired = valueRequired;
            if (valueRequired) {
                this.hasValue = true;
            }
        }

        public Option(String name,String desc,String[] selects) {
            super(name,desc);
            super.required = false;
            this.hasValue = true;
            this.valueRequired = true;
            this.addSelecet(selects);
        }

        public Option(String name,String desc,boolean required,boolean hasValue,String[] selects,boolean valueRequired) {
            super(name,desc);
            super.required = required;
            this.hasValue = hasValue;
            this.valueRequired = valueRequired;
            this.addSelecet(selects);
        }

        public Option(String name,String desc) {
            super(name,desc);
            super.required = false;
        }

        @Override
        String cmd() {
            return (required?"<-":"[-") +name +  (hasValue?((valueRequired?":<val>":"[val]")):"") +(required?">":"]");
        }

        @Override
        String getHelper() {
            if (selects.size() == 0) {
                return super.getHelper();
            }
            return super.getHelper() + " 可选值:" + StringExtUtils.join(",",selects);
        }
    }
}
