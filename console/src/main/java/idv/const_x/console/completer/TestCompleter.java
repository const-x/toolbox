package idv.const_x.console.completer;

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.utils.AttributedString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author const.x
 * @since 2024-04-30
 */
public class TestCompleter implements Completer {


    @Override
    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
        try {
            List<String> words = line.words();

            candidates.add(createCandidate("cur:["+line.word()+"]"));
            candidates.add(createCandidate("len:["+line.cursor()+"]"));
            candidates.add(createCandidate("idx:["+line.wordIndex()+"]"));
            if (words.size() >= 2) {
                String pre = words.get(words.size() - 2);
                candidates.add(createCandidate("pre:["+pre+"]"));

                // 构建非 option 的参数序列（用于树路径和索引）
                List<String> nonOptionWords = new ArrayList<>();
                for (String word : words) {
                    if (!word.startsWith("-")) {
                        nonOptionWords.add(word);
                    }
                }

                // 上一个已完成的非 option token（用于树查找）
                // -1是当前正在输入的（不完整）token
                String lastNonOption = null;
                if (nonOptionWords.size() >= 2) {
                    lastNonOption = nonOptionWords.get(nonOptionWords.size() - 2);
                }

                candidates.add(createCandidate("preNonOpt:["+lastNonOption+"]"));

                // 当前正在输入的是第几个非 option 参数（0 是 root）
                int currentNonOptionIndex = nonOptionWords.size() - 1;
                candidates.add(createCandidate("preNonOptIdx:["+currentNonOptionIndex+"]"));


            }
            candidates.add(createCandidate("all:["+line.words()+"]"));
        }catch (Throwable e){
            candidates.add(createCandidate(e.getMessage()));
        }

    }

    // ========== 工具方法 ==========

    private Candidate createCandidate(String value) {
        String clean = AttributedString.stripAnsi(value);
        return new Candidate(clean, clean, null, null, null, null, true);
    }
}