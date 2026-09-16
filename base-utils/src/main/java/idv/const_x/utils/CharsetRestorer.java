package idv.const_x.utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class CharsetRestorer {

    public static class RestoreResult {
        public String originalText;
        public String restoredText;
        public String cause;
        public boolean isSuccess;

        public RestoreResult(String original, String restored, String cause, boolean success) {
            this.originalText = original;
            this.restoredText = restored;
            this.cause = cause;
            this.isSuccess = success;
        }

        @Override
        public String toString() {
            // 【修改点1】强制使用 System.out 打印，避免被默认编码干扰显示
            return String.format("【%s】\n原因: %s\n结果: %s",
                    originalText, cause, restoredText);
        }
    }

    // 调整后的候选编码列表
    // 策略：先试 ISO-8859-1 (兼容性好)，再试 GBK (针对中文)
    private static final List<Charset> CANDIDATE_CHARSETS = Arrays.asList(
            StandardCharsets.ISO_8859_1,     // 1. 优先尝试 ISO (Emoji 和英文兼容性最好)
            Charset.forName("GBK"),          // 2. 再尝试 GBK (中文乱码)
            Charset.forName("Windows-1252"),
            StandardCharsets.UTF_16
    );

    public static RestoreResult restore(String garbledText) {
        if (garbledText == null || garbledText.isEmpty()) {
            return new RestoreResult(garbledText, garbledText, "输入为空", false);
        }

        RestoreResult bestResult = null;

        for (Charset wrongCharset : CANDIDATE_CHARSETS) {
            try {
                byte[] bytes = garbledText.getBytes(wrongCharset);
                String fixedText = new String(bytes, StandardCharsets.UTF_8);

                // 【关键修改】如果修复结果包含问号，说明有无法识别的字符，不算完美修复
                // 除非这是唯一的结果，否则我们继续尝试其他编码
                if (fixedText.contains("?")) {
                    if (bestResult == null) {
                        bestResult = new RestoreResult(garbledText, fixedText, "尝试 [" + wrongCharset.name() + "] (包含无法识别字符)", false);
                    }
                    continue;
                }

                // 如果修复结果和原文一样，说明没变化
                if (fixedText.equals(garbledText)) {
                    continue;
                }

                // 只有通过严格校验（包含汉字或Emoji，且无问号），才算成功
                if (isValidContent(fixedText)) {
                    String causeMsg = String.format("原始文本是 UTF-8 编码，但被错误地用 [%s] 解码读取", wrongCharset.name());
                    return new RestoreResult(garbledText, fixedText, causeMsg, true);
                }

            } catch (Exception e) {
                // 忽略
            }
        }

        // 如果没有完美修复，返回那个“包含问号”的最佳尝试（作为备选）
        if (bestResult != null) {
            return bestResult;
        }

        return new RestoreResult(garbledText, garbledText, "无法识别乱码原因", false);
    }

    // 增强版校验：只有包含常用汉字或Emoji才算有效
    private static boolean isValidContent(String text) {
        if (text == null || text.isEmpty()) return false;
        int score = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            // 常用汉字
            if (c >= 0x4E00 && c <= 0x9FA5) score += 2;
                // ASCII 字母数字 (排除纯符号)
            else if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) score += 1;
                // Emoji
            else if (Character.isHighSurrogate(c)) score += 2;
        }
        // 分数必须大于 0，且平均每字符得分大于 0.5
        return score > 0 && (double) score / text.length() > 0.5;
    }

    // --- 修改后的 Main 方法 ---
    public static void main(String[] args) {
        System.out.println("=== 最终修复测试 ===\n");

        // 测试 1: 中文乱码
        // 注意：这里直接写死乱码字符串。
        // 如果你的源文件编码不对，这里可能会变成问号，但 restore 逻辑会尝试修复
        String input1 = "绫宠壊銆愬皬7鏍笺€";
        System.out.println("输入: " + input1);

        RestoreResult result1 = restore(input1);
        System.out.println(result1);

        // 强制验证：如果上面失败了，我们手动执行一次 GBK 修复看看
        if (!result1.isSuccess) {
            System.out.println("\n[调试] 自动检测失败，强制尝试 GBK 修复...");
            try {
                byte[] bytes = input1.getBytes(Charset.forName("GBK"));
                String manualFix = new String(bytes, StandardCharsets.UTF_8);
                System.out.println("[调试] 强制修复结果: " + manualFix);
            } catch (Exception e) {
                System.out.println("[调试] 强制修复也失败了，说明输入字符串本身已损坏（全是问号）");
            }
        }

        System.out.println("-------------------");

        // 测试 2: Emoji 乱码
        try {
            String rawEmoji = "测试😂Emoji";
            String garbledEmoji = new String(rawEmoji.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
            System.out.println("输入: " + garbledEmoji);
            System.out.println(restore(garbledEmoji));
        } catch (Exception e) { e.printStackTrace(); }
    }
}