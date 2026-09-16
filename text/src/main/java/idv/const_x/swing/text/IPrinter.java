package idv.const_x.swing.text;

public interface IPrinter {
    void print(String str,String desc);
    
    void clear();
    
    void printLog(String str);
    
    void clearLog();
    
    void printWarn(String str);
    
    void printSuccess(String str);
    
    void clearWarn();
}
