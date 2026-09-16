package idv.const_x.console.completer;

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.List;
import java.util.Objects;

/**
 * @Description <pre>
 *
 * </pre>
 * @Author const.x
 * @Date 2026-01-30
 */
public abstract class FirstLimitedCompleter implements Completer {

    protected final String root;

    public FirstLimitedCompleter(String root) {
        this.root = Objects.requireNonNull(root, "root command must not be null");
    }


    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {
        List<String> words = parsedLine.words();
        // 仅当已输入 root 且至少有一个后续 token 时才处理
        if (words == null || words.size() < 2 || !root.equals(words.get(0))) {
            return;
        }
        doComplete(lineReader, parsedLine, list);
    }

    abstract void  doComplete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list);
}
