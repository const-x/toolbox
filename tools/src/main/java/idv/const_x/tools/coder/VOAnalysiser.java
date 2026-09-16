package idv.const_x.tools.coder;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.Expression;
import idv.const_x.utils.StringExtUtils;

import java.util.*;

public class VOAnalysiser {

    public static List<VOField> extractFieldsFromSnippet(String codeSnippet) {
        // 配置 JavaParser 保留行尾注释（启用保存所有注释）
        ParserConfiguration config = new ParserConfiguration()
                .setStoreTokens(true)
                .setLexicalPreservationEnabled(false);
        JavaParser parser = new JavaParser(config);

        String wrappedCode = codeSnippet;
        if (!codeSnippet.contains(" class ")) {
            // 将片段包装成类，以便解析（因为字段不能独立存在）
            wrappedCode = "class Dummy {\n" + codeSnippet + "\n}";
        }


        ParseResult<CompilationUnit> result = parser.parse(wrappedCode);
        if (!result.isSuccessful()) {
            //解析失败 尝试逐行解析
            return analysisByLine(codeSnippet);
        }

        CompilationUnit cu = result.getResult().get();

        // 步骤1: 收集所有 LineComment 及其行号
        Map<Integer, String> lineCommentByLine = new HashMap<>();
        cu.walk(comment -> {
            if (comment instanceof LineComment) {
                int line = comment.getBegin().map(pos -> pos.line).orElse(-1);
                if (line != -1) {
                    String content = ((LineComment) comment).getContent().trim();
                    lineCommentByLine.put(line, content);
                }
            }
        });

        List<VOField> fields = new ArrayList<>();
        // 步骤2: 遍历所有字段
        cu.findAll(FieldDeclaration.class).forEach(field -> {
            field.getVariables().forEach(var -> {
                VOField f = new VOField();
                fields.add(f);
                f.setName(var.getNameAsString());

                String commentText = "";
                // 1. Javadoc
                if (field.getJavadocComment().isPresent()) {
                    commentText = cleanComment(field.getJavadocComment().get().getContent());
                }
                // 2. 块注释或普通注释（上方）
                else if (field.getComment().isPresent()) {
                    commentText = cleanComment(field.getComment().get().getContent());
                }
                // 3. 行末注释（关键！）
                else {
                    int endLine = field.getEnd().map(pos -> pos.line).orElse(-1);
                    if (endLine != -1 && lineCommentByLine.containsKey(endLine)) {
                        commentText = lineCommentByLine.get(endLine);
                    }
                }
                f.setComment(commentText.isEmpty() ? null : commentText);

                f.setType(var.getTypeAsString());

                // 获取默认值（初始化表达式）
                String defaultValue = "";
                Optional<Expression> initializer = var.getInitializer();
                if (initializer.isPresent()) {
                    defaultValue = initializer.get().toString();
                }
                f.setDefValue(defaultValue);

                f.setStatic(field.isStatic());
            });
        });
        return fields;
    }


    private static List<VOField> analysisByLine(String s) {
        List<VOField> fields = new ArrayList<>();
        if (StringExtUtils.isNotBlank(s)) {
            String[] res = s.split("\n");
            String alias = null;
            for (String line : res) {
                line = line.trim();
                if (line.startsWith("/**") || line.startsWith("//") || line.startsWith("/*") || line.startsWith("*")) {
                    int inx = -1;
                    if (line.startsWith("/**") || line.startsWith("/*")) {
                        if (!"/***/".equals(line.trim())) {
                            if (inx < 0) {
                                inx = line.indexOf("**/");
                            }
                            if (inx < 0) {
                                inx = line.indexOf("*/");
                            }
                            if (inx < 0) {
                                if (line.length() > 3) {
                                    alias = line.substring(3);
                                }
                            } else {
                                alias = line.substring(3, inx);
                            }
                        }
                    } else if (line.startsWith("*")) {
                        if (line.length() > 1 && !line.startsWith("*/") && !line.startsWith("**/")) {
                            alias = line.substring(1, inx > 0 ? inx : line.length());
                        }
                    } else {
                        if (line.length() > 2) {
                            alias = line.substring(2, inx > 0 ? inx : line.length());
                        }
                    }
                } else if (line.startsWith("private ") || line.startsWith("protected ") || line.startsWith("public ")) {
                    if (!line.contains("(")
                            && !line.contains(" class ")) {
                        String name = null;
                        String type = null;
                        String def = null;
                        if (line.contains("=")) {
                            def = line.substring(line.indexOf("=") + 1, line.lastIndexOf(";"));
                            line = line.substring(0, line.indexOf("="));
                        }
                        String[] keys = line.split(" ");

                        for (int i = keys.length - 1; i >= 0; i--) {
                            String string = keys[i];
                            if (StringExtUtils.isBlank(string)) {
                                continue;
                            }
                            if (!string.equals(" ")) {
                                if (name == null) {
                                    if (string.contains(";")) {
                                        name = string.substring(0, string.lastIndexOf(";")).trim();
                                    } else {
                                        name = string.trim();
                                    }
                                } else {
                                    type = string.trim();
                                    break;
                                }
                            }
                        }


                        if (StringExtUtils.isNotBlank(name)) {
                            VOField f = new VOField();
                            f.name = name;
                            f.comment = alias == null ? null : alias.trim();
                            f.type = type == null ? null : type.trim();
                            f.defValue = def == null ? null : def.trim();
                            f.isStatic = line.contains("static");
                            f.isFinal = line.contains("final");
                            fields.add(f);
                            alias = null;
                        }
                    }
                }
            }
        }
        return fields;
    }

    private static String cleanComment(String comment) {
        if (comment == null) return "";
        return comment
                .replaceAll("(?m)^\\s*\\*\\s?", "")
                .replaceAll("^/\\*\\*?|\\*/$", "")
                .trim()
                .replaceAll("\\s+", " ");
    }

    // 测试
    public static void main(String[] args) {
        String snippet = "public class Person {\n" +
                "    /** 用户唯一ID */\n" +
                "    private static long id;\n" +
                "\n" +
                "    /*\n" +
                "     * 用户姓名\n" +
                "     * 可能包含中文\n" +
                "     */\n" +
                "    private String name;\n" +
                "\n" +
                "    // 年龄字段\n" +
                "    int age = 0;\n" +
                "\n" +
                "    private String email; // 电子邮箱\n";
                //+"}";

        System.out.println(extractFieldsFromSnippet(snippet));
    }
}