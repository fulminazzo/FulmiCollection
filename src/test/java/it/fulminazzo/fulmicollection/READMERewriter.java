package it.fulminazzo.fulmicollection;

import it.fulminazzo.fulmicollection.interfaces.functions.FunctionException;
import it.fulminazzo.fulmicollection.utils.ClassUtils;
import it.fulminazzo.markdownparser.nodes.HeaderNode;
import it.fulminazzo.markdownparser.nodes.Node;
import it.fulminazzo.markdownparser.nodes.RootNode;
import it.fulminazzo.markdownparser.nodes.TableNode;
import it.fulminazzo.markdownparser.objects.TableRow;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.fail;

class READMERewriter {
    private static final List<String> VALUES = Arrays.asList("", "Bi", "Tri", "Tetra", "Penta", "Hexa", "Septa");
    private static final String[] PARAMETERS = new String[]{"f", "s", "t", "q", "p", "h", "e"};
    private static final String FUNCTIONS_REGEX = "(.*)(Consumer|Function)(.*)";

    @Test
    void rewrite() throws IOException {
        File file = new File("README.md");
        if (!file.exists()) fail("Could not find README.md file in root project directory.");

        RootNode rootNode = new RootNode(file);
        handleNode(rootNode);
        rootNode.write(file);
    }

    private void handleNode(Node node) {
        if (node == null) return;
        if (node instanceof HeaderNode) {
            String text = ((HeaderNode) node).getHeaderText();
            if (text.equals("Functions")) handleTable(node.getChild());
        }
        handleNode(node.getNext());
        handleNode(node.getChild());
    }

    private void handleTable(Node node) {
        if (node == null) return;
        if (node instanceof TableNode) {
            TableNode tableNode = (TableNode) node;
            tableNode.setTitleRow(new TableRow("| Contents | Description |"));
            tableNode.getTableRows().clear();
            Pattern pattern = Pattern.compile(FUNCTIONS_REGEX);
            String packageName = FunctionException.class.getPackage().getName();
            tableNode.setTableRows(ClassUtils.findClassesInPackage(packageName, FunctionException.class).stream()
                    .map(Class::getSimpleName)
                    .sorted(Comparator.comparing(n -> {
                        Matcher matcher = pattern.matcher(n);
                        if (matcher.matches()) {
                            String match = matcher.group(1);
                            if (VALUES.contains(match)) return VALUES.indexOf(match);
                        }
                        return Integer.MAX_VALUE;
                    }))
                    .map(n -> {
                        Matcher matcher = pattern.matcher(n);
                        String description;
                        if (matcher.matches()) {
                            int size = VALUES.indexOf(matcher.group(1));
                            String type = matcher.group(2);
                            String exception = matcher.group(3);
                            String parameters = "";
                            for (int i = 0; i <= size; i++) parameters += PARAMETERS[i] + ", ";
                            description = String.format("(%s) -> %s%s",
                                    parameters.substring(0, parameters.length() - 2),
                                    type.equalsIgnoreCase("Function") ? "r" : "void",
                                    exception.isEmpty() ? "" : " throws Exception"
                            );
                        } else description = "";
                        return String.format("|[%s](src/main/java/%s.java)|`%s`|", n,
                                packageName.replace(".", "/") + "/" + n, description);
                    })
                    .map(TableRow::new)
                    .collect(Collectors.toList()));
        }
        handleTable(node.getNext());
        handleTable(node.getChild());
    }
}