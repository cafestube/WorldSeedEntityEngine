package net.worldseed.resourcepack.multipart.generator;


import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonString;
import javax.json.JsonValue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Ported from molang.ts from BlockBench (licenced under GNU General Public License v3.0)
 */
public class MolangInverter {

    private static final String BRACKET_OPEN = "([{";
    private static final String BRACKET_CLOSE = ")]}";

    public static JsonValue invertMolang(JsonValue value) {
        switch (value.getValueType()) {
            case NUMBER:
                JsonNumber num = (JsonNumber) value;
                return Json.createValue(-num.doubleValue());

            case STRING:
                String str = ((JsonString) value).getString();
                String result = invertMolangString(str);
                return Json.createValue(result);

            default:
                return value; // unchanged
        }
    }

    private static String invertMolangString(String molang) {
        if (molang.isEmpty() || molang.equals("0")) return molang;

        if (isStringNumber(molang)) {
            double val = Double.parseDouble(molang);
            return Double.toString(-val);
        }

        if (molang.contains("return ")) {
            Pattern pattern = Pattern.compile("return (.+?)(;|$)");
            Matcher matcher = pattern.matcher(molang);
            StringBuffer sb = new StringBuffer();

            while (matcher.find()) {
                String expression = matcher.group(1);
                String end = matcher.group(2);
                String replacement = "return " + invertMolangString(expression) + end;
                matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
            }
            matcher.appendTail(sb);
            return sb.toString();
        }

        boolean invert = true;
        int bracketDepth = 0;
        String lastOperator = null;
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < molang.length(); i++) {
            char ch = molang.charAt(i);

            if (bracketDepth == 0) {
                String operator = null;
                boolean hadInput = true;

                if (ch == '-' && !("*".equals(lastOperator) || "/".equals(lastOperator))) {
                    if (!invert && lastOperator == null) result.append('+');
                    invert = false;
                    continue;
                } else if (ch == ' ' || ch == '\n') {
                    hadInput = false;
                } else if (ch == '+' && !("*".equals(lastOperator) || "/".equals(lastOperator))) {
                    result.append('-');
                    invert = false;
                    continue;
                } else if (ch == '?' || ch == ':') {
                    invert = true;
                    operator = String.valueOf(ch);
                } else if (invert) {
                    result.append('-');
                    invert = false;
                } else if ("+-*/&|".indexOf(ch) != -1) {
                    operator = String.valueOf(ch);
                }

                if (hadInput) {
                    lastOperator = operator;
                }
            }

            if (BRACKET_OPEN.indexOf(ch) != -1) {
                bracketDepth++;
            } else if (BRACKET_CLOSE.indexOf(ch) != -1) {
                bracketDepth--;
            }

            result.append(ch);
        }

        return result.toString();
    }

    private static boolean isStringNumber(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}