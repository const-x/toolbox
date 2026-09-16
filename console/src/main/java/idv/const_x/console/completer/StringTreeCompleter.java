package idv.const_x.console.completer;

import idv.const_x.utils.ColoredStringUtils;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.reader.impl.completer.StringsCompleter;
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
 * 支持树状结构的命令补全器。
 * 所有补全行为仅在用户已输入 root 命令后触发（即命令行以 root 开头且至少有两个 token）。
 *
 * @author const.x
 * @since 2024-04-30
 */
public class StringTreeCompleter extends FirstLimitedCompleter {

    private final Map<String, List<Candidate>> tree = new HashMap<>();
    private final Map<Integer, List<Candidate>> indexTree = new HashMap<>();
    private final List<Candidate> freeOptions = new ArrayList<>();


    public StringTreeCompleter(String root) {
        super(root);
    }

    // ========== 添加子节点（基于父节点名称） ==========

    public StringTreeCompleter addSubStrings(String parent, String... children) {
        if (parent == null || children == null) return this;;
        for (String child : children) {
            if (child != null) {
                Candidate cand = createCandidate(child);
                tree.computeIfAbsent(parent, k -> new ArrayList<>()).add(cand);
            }
        }
        return this;
    }

    public StringTreeCompleter addSubStrings(String parent, Map<String,String> children) {
        if (parent == null || children == null) return this;
        for (Map.Entry<String, String> entry : children.entrySet()) {
            Candidate cand = createCandidate(entry.getKey(),entry.getValue());
            tree.computeIfAbsent(parent, k -> new ArrayList<>()).add(cand);
        }
        return this;
    }

    public StringTreeCompleter addSubStrings(String parent, Collection<String> children) {
        if (parent == null || children == null) return this;;
        for (String child : children) {
            if (child != null) {
                Candidate cand = createCandidate(child);
                tree.computeIfAbsent(parent, k -> new ArrayList<>()).add(cand);
            }
        }
        return this;
    }

    // ========== 按位置索引添加候选 ==========

    public StringTreeCompleter addSubStrings(int index, String... candidates) {
        if (index < 0 || candidates == null) return this;;
        for (String value : candidates) {
            if (value != null) {
                Candidate cand = createCandidate(value);
                indexTree.computeIfAbsent(index, k -> new ArrayList<>()).add(cand);
            }
        }
        return this;
    }

    public StringTreeCompleter addSubStrings(int index, Map<String,String> candidates) {
        if (index < 0 || candidates == null) return this;
        for (Map.Entry<String, String> entry : candidates.entrySet()) {
            Candidate cand = createCandidate(entry.getKey(),entry.getValue());
            indexTree.computeIfAbsent(index, k -> new ArrayList<>()).add(cand);
        }
        return this;
    }

    public StringTreeCompleter addSubStrings(int index, Collection<String> candidates) {
        if (index < 0 || candidates == null) return this;;
        for (String value : candidates) {
            if (value != null) {
                Candidate cand = createCandidate(value);
                indexTree.computeIfAbsent(index, k -> new ArrayList<>()).add(cand);
            }
        }
        return this;
    }

    // ========== 添加自由可选项（全局可用，不重复） ==========

    public StringTreeCompleter addFreeStrings(String... options) {
        if (options == null) return this;;
        for (String opt : options) {
            if (opt != null) {
                freeOptions.add(createCandidate(opt));
            }
        }
        return this;
    }

    public StringTreeCompleter addFreeStrings(Collection<String> options) {
        if (options == null) return this;;
        for (String opt : options) {
            if (opt != null) {
                freeOptions.add(createCandidate(opt));
            }
        }
        return this;
    }

    public StringTreeCompleter addFreeStrings(Map<String,String> candidates) {
        if (candidates == null) return this;
        for (Map.Entry<String, String> entry : candidates.entrySet()) {
            Candidate cand = createCandidate(entry.getKey(),entry.getValue());
            freeOptions.add(cand);
        }
        return this;
    }


    // ========== 补全主逻辑 ==========

    @Override
    void doComplete(LineReader lineReader, ParsedLine line, List<Candidate> candidates) {
       try {
        List<String> words = line.words();
        // 仅当已输入 root 且至少有一个后续 token 时才处理
        if (words == null || words.size() < 2 || !root.equals(words.get(0))) {
            return;
        }

        // 当前光标所在的 word 索引
        int wordIndex = line.wordIndex();

        // 构建光标之前的非 option 参数序列（不包含当前正在输入的 word）
        List<String> nonOptionWords = new ArrayList<>();
        for (int i = 0; i < wordIndex; i++) {
            String word = words.get(i);
            if (!word.startsWith("-")) {
                nonOptionWords.add(word);
            }
        }

        // 当前正在输入的是第几个非 option 参数（0 是 root）
        int currentNonOptionIndex = nonOptionWords.size();

        // 光标前最后一个非 option token（用于基于父节点查找子节点）
        String lastNonOption = nonOptionWords.isEmpty() ? null : nonOptionWords.get(nonOptionWords.size() - 1);

        // 1. 基于上一个非 option token 从 tree 查找子节点
        if (lastNonOption != null && tree.containsKey(lastNonOption)) {
            candidates.addAll(tree.get(lastNonOption));
        }

        // 2. 基于当前非 option 索引位置从 indexTree 匹配
        if (indexTree.containsKey(currentNonOptionIndex)) {
            candidates.addAll(indexTree.get(currentNonOptionIndex));
        }

        // 3. 添加自由可选项（未在光标之前 words 中出现的）
        if (!freeOptions.isEmpty()) {
            Set<String> existing = new HashSet<>(words);
            for (Candidate free : freeOptions) {
                if (!existing.contains(free.value())) {
                    candidates.add(free);
                }
            }
        }

        //candidates.add(createCandidate("ci:"+wordIndex + "cw:" + words.get(words.size()-1)));
        //candidates.add(createCandidate("li:" + currentNonOptionIndex  + "lw:" + lastNonOption));

       } catch (Throwable e) {
           candidates.add(createCandidate(root + ":" + e.getMessage()));
       }
    }

    // ========== 工具方法 ==========
    private Candidate createCandidate(String value) {
        return createCandidate(value,null);
    }

    private Candidate createCandidate(String value,String desc) {
        String clean = AttributedString.stripAnsi(value);
        if (desc != null) {
            desc = ColoredStringUtils.colorString(desc,ColoredStringUtils.COLOR_GRAY);
        }
        return new Candidate(clean, clean, null, desc, null, null, true);
    }
}