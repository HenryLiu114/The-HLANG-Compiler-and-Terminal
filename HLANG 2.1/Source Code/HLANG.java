import java.io.EOFException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

public class HLANG {
    private enum TokenType {
        // Command Types
        arithmetic,
        singleArith,
        logical,
        logicalnot,
        var,
        varname,
        vardec,
        conditional,
        ifstmt,
        prog,
        attribute,
        functcreate,
        functdec,
        output,
        listdec,
        cons,
        car,
        cdr,
        listlogic,
        input,
        mapcar,
        returnto,
        releaseto,
        // Data Types
        integer,
        floating,
        str,
        bool,
        list,
        wildcard,
    }

    private static class Token<T> {
        TokenType type;
        T data;

        public Token(T value, TokenType type) {
            this.data = value;
            this.type = type;
        }

        @Override
        public String toString() {
            return data + "";

        }

        public String toStringDebug() {
            return type + " : " + data.getClass().getSimpleName() + " " + data;
        }
    }

    private static class HLangFunct {
        String functName;
        private LinkedHashMap<String, Token<?>> param;
        Token<?> prog;

        HLangFunct(String functN, Token<?> pro) {
            functName = functN;
            prog = pro;
            param = new LinkedHashMap<>();
        }

        void AddParam(String varName) {
            // System.out.println("Adding param: [" + varName + "]");
            param.put(varName, null);
        }

        void SetParam(String varName, Token<?> data) {
            param.replace(varName, data);
        }

        void ResetParams() {
            for (String n : param.keySet()) {
                param.replace(n, null);
            }
        }

        // Debug Method
        LinkedList<String> GetParams() {
            LinkedList<String> res = new LinkedList<>();
            for (String n : param.keySet()) {
                res.add(n + " = " + param.get(n));
            }
            return res;
        }

        void CallFunction(
                LinkedList<Token<?>> parameters,
                HashMap<String, Token<?>> variables,
                Stack<Token<?>> ValStack,
                HashMap<String, HLangFunct> function,
                Stack<Token<?>> returnStack) throws Exception {
            //System.out.println(parameters);
            if (!parameters.isEmpty()) {
                HashMap<String, Token<?>> localVars = new HashMap<>();
                for (String n : param.keySet()) {
                    Token<?> param1 = parameters.remove();

                    if (param1.type == TokenType.var) {
                        localVars.put(
                                n,
                                variables.get(((String) param1.data).substring(1)));
                    } else {
                        localVars.put(n, param1);
                    }
                }
                Compiler((String) prog.data, localVars, ValStack, function, returnStack);
            } else {
                Compiler((String) prog.data, new HashMap<>(), ValStack, function, returnStack);
            }

        }
    }

    private static LinkedList<Token<?>> Lexer(String cmd) throws Exception {
        boolean isString = false;
        boolean isFinished = false;
        int progCount = 0;
        boolean attribute = false;
        int listBrackets = 0;
        String cur = "";
        LinkedList<String> strList = new LinkedList<>();
        LinkedList<Token<?>> lexedList = new LinkedList<>();
        int i = 0;
        // Custom Split
        while (i < cmd.length() && !isFinished) {
            char curChar = cmd.charAt(i);
            //System.out.println("Cur: " + cur);
            if (listBrackets > 0) {
                cur += curChar;

                if (curChar == '[') {
                    listBrackets++;
                } else if (curChar == ']') {
                    listBrackets--;

                    if (listBrackets == 0) {
                        strList.add(cur);
                        cur = "";
                    }
                } else if (curChar == '"') {
                    isString = !isString;
                }
            } else if (isString) {
                if (curChar == '"') {
                    cur += curChar;
                    isString = !isString;
                } else {
                    cur += curChar;
                }
            } else if (progCount > 0) {
                cur += curChar;

                if (curChar == '{') {
                    progCount++;
                } else if (curChar == '}') {
                    progCount--;

                    if (progCount == 0) {
                        strList.add(cur);
                        cur = "";
                    }
                } else if (curChar == '"') {
                    isString = !isString;
                }
            } else if (attribute) {
                cur += curChar;
                if (curChar == ')') {
                    attribute = false;
                    strList.add(cur);
                    cur = "";
                } else if (curChar == '"') {
                    isString = !isString;
                }
            } else {
                switch (curChar) {
                    case ' ':
                        if (cur.length() != 0) {
                            strList.add(cur);
                            cur = "";
                        }

                        break;
                    case '{':
                        cur += curChar;
                        progCount++;
                        break;
                    case '(':
                        cur += curChar;
                        attribute = true;
                        break;
                    case '[':
                        cur += curChar;
                        listBrackets++;
                        break;
                    case '.':
                        if (cur.length() != 0) {
                            strList.add(cur);
                        }
                        isFinished = true;
                        break;
                    case '"':
                        cur += curChar;
                        isString = !isString;
                        break;
                    default:
                        cur += curChar;
                        break;
                }
            }
            i++;
        }

        for (i = 0; i < strList.size(); i++) {
            String curStr = strList.get(i);
            if (curStr.charAt(0) == '/') {
                switch (curStr) {
                    case "/add", "/sub", "/mul", "/div", "/mod", "/pow":
                        lexedList.add(new Token<String>(curStr, TokenType.arithmetic));
                        break;
                    case "/abs":
                        lexedList.add(new Token<String>(curStr, TokenType.singleArith));
                        break;
                    case "/print", "/println":
                        lexedList.add(new Token<String>(curStr, TokenType.output));
                        break;
                    case "/var":
                        lexedList.add(new Token<String>(curStr, TokenType.vardec));
                        break;
                    case "/and", "/or":
                        lexedList.add(new Token<String>(curStr, TokenType.logical));
                        break;
                    case "/gteq", "/gt", "/lteq", "/lt", "/eq", "/neq":
                        lexedList.add(new Token<String>(curStr, TokenType.conditional));
                        break;
                    case "/not":
                        lexedList.add(new Token<String>(curStr, TokenType.logicalnot));
                        break;
                    case "/if":
                        lexedList.add(new Token<String>(curStr, TokenType.ifstmt));
                        break;
                    case "/usefun":
                        lexedList.add(new Token<String>(curStr, TokenType.functdec));
                        break;
                    case "/defun":
                        lexedList.add(new Token<String>(curStr, TokenType.functcreate));
                        break;
                    case "/list":
                        lexedList.add(new Token<String>(curStr, TokenType.listdec));
                        break;
                    case "/cons":
                        lexedList.add(new Token<String>(curStr, TokenType.cons));
                        break;
                    case "/car":
                        lexedList.add(new Token<String>(curStr, TokenType.car));
                        break;
                    case "/cdr":
                        lexedList.add(new Token<String>(curStr, TokenType.cdr));
                        break;
                    case "/isempty":
                        lexedList.add(new Token<String>(curStr, TokenType.listlogic));
                        break;
                    case "/prompt":
                        lexedList.add(new Token<String>(curStr, TokenType.input));
                        break;
                    case "/mapcar":
                        lexedList.add(new Token<String>(curStr, TokenType.mapcar));
                        break;
                    case "/?":
                        lexedList.add(new Token<String>(curStr, TokenType.wildcard));
                        break;
                    case "/ret":
                        lexedList.add(new Token<String>(curStr, TokenType.returnto));
                        break;
                    case "/rel":
                        lexedList.add(new Token<String>(curStr, TokenType.releaseto));
                        break;
                    default:
                        lexedList.add(new Token<String>(curStr, TokenType.var));
                        break;
                }
            } else if (curStr.charAt(0) == '"') {
                lexedList.add(new Token<String>(curStr.substring(1, curStr.length() - 1), TokenType.str));
            } else if (curStr.charAt(0) == '(') {
                lexedList.add(new Token<String>(curStr.substring(1, curStr.length() - 1), TokenType.attribute));
            } else if (curStr.charAt(0) == '{') {
                lexedList.add(new Token<String>(curStr.substring(1, curStr.length() - 1), TokenType.prog));
            } else if (curStr.charAt(0) == '[') {
                List<String> lis = splitTopLevel(curStr.substring(1, curStr.length() - 1));
                LinkedList<Token<?>> list = new LinkedList<>();
                for (int k = 0; k < lis.size(); k++) {
                    list.add(Lexer(lis.get(k) + ".").get(0));
                }
                lexedList.add(new Token<LinkedList<Token<?>>>(list, TokenType.list));
            } else {
                if (curStr.contains("_")) {
                    try {
                        curStr = curStr.replace("_", ".");
                        lexedList.add(new Token<Double>(Double.parseDouble(curStr), TokenType.floating));
                    } catch (NumberFormatException e) {
                        throw new Exception("HLANG Lexer cannot determine that " + curStr + " is a float.");
                    }
                } else if (curStr.equals("T")) {
                    lexedList.add(new Token<Boolean>(true, TokenType.bool));
                } else if (curStr.equals("NIL")) {
                    lexedList.add(new Token<Boolean>(false, TokenType.bool));
                } else {
                    try {
                        lexedList.add(new Token<Integer>(Integer.parseInt(curStr), TokenType.integer));
                    } catch (NumberFormatException e) {
                        lexedList.add(new Token<String>(curStr, TokenType.varname));
                    }
                }
            }
        }
        // System.out.println(lexedList);
        return lexedList;
    }

    private static List<String> splitTopLevel(String s) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        int depth = 0;
        boolean inStr = false;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == '"') {
                inStr = !inStr;
                current.append(c);
                continue;
            }

            if (!inStr) {
                if (c == '[') {
                    depth++;
                    current.append(c);
                } else if (c == ']') {
                    depth--;
                    current.append(c);
                } else if (c == ',' && depth == 0) {
                    result.add(current.toString().trim());
                    current.setLength(0);
                } else {
                    current.append(c);
                }
            } else {
                current.append(c);
            }
        }

        if (current.length() > 0) {
            result.add(current.toString().trim());
        }

        return result;
    }

    static class TreeNode {
        Token<?> data;
        LinkedList<TreeNode> children;

        TreeNode(Token<?> d) {
            data = d;
            children = new LinkedList<>();
        }
    }

    private static TreeNode Parser(LinkedList<Token<?>> lexedList) {
        if (lexedList.isEmpty()) {
            return null;
        }
        Token<?> cur = lexedList.removeFirst();

        TreeNode curNode = new TreeNode(cur);
        int childrenCount = 0;
        switch (cur.type) {
            case TokenType.arithmetic, TokenType.vardec, TokenType.logical, TokenType.conditional, TokenType.functdec,
                    TokenType.listdec, TokenType.cons, TokenType.car, TokenType.cdr, TokenType.input, TokenType.mapcar:
                childrenCount = 2;
                break;
            case TokenType.singleArith, TokenType.logicalnot, TokenType.output, TokenType.listlogic, TokenType.returnto,
                    TokenType.releaseto:
                childrenCount = 1;
                break;
            case TokenType.ifstmt, TokenType.functcreate:
                childrenCount = 3;
                break;
            default:
                childrenCount = 0;
                break;
        }

        if (childrenCount != -1) {
            for (int i = 0; i < childrenCount; i++) {
                TreeNode child = Parser(lexedList);
                curNode.children.add(child);
            }
        }
        return curNode;
    }

    private static LinkedList<Token<?>> Interpreter(TreeNode tree) {

        Stack<TreeNode> postStack = new Stack<>();
        Stack<Token<?>> popOrder = new Stack<>();
        LinkedList<Token<?>> res = new LinkedList<>();

        // Uses DFS
        postStack.push(tree);
        while (!postStack.isEmpty()) {
            TreeNode cur = postStack.pop();
            popOrder.push(cur.data);
            if (!cur.children.isEmpty()) {
                for (int i = cur.children.size() - 1; i >= 0; i--) {
                    postStack.push(cur.children.get(i));
                }
            }
        }

        while (!popOrder.isEmpty()) {
            res.add(popOrder.pop());
        }

        return res;
    }

    private static void SingleLineCompiler(String cmd, HashMap<String, Token<?>> variables, Stack<Token<?>> ValStack,
            HashMap<String, HLangFunct> functions, Stack<Token<?>> returnStack)
            throws Exception {
        // System.out.println(cmd);
        LinkedList<Token<?>> interpret = Interpreter(Parser(Lexer(cmd)));
        // System.out.println("Val Stack: " + ValStack);
        // System.out.println("Interpeted String: " + interpret);
        // System.out.println("Vars: " + variables);
        // System.out.println("Return Stack: " + returnStack);
        while (!interpret.isEmpty()) {
            Token<?> cur = interpret.remove();
            switch (cur.type) {
                case TokenType.integer, TokenType.floating, TokenType.str, TokenType.bool, TokenType.varname,
                        TokenType.prog, TokenType.attribute, TokenType.list:
                    ValStack.push(cur);
                    break;
                case TokenType.arithmetic:
                    switch ((String) cur.data) {
                        case "/add":
                            Token<?> X1 = ValStack.pop();
                            Token<?> X2 = ValStack.pop();
                            if (X1.type == TokenType.integer && X2.type == TokenType.integer) {
                                int v1 = (Integer) X1.data;
                                int v2 = (Integer) X2.data;

                                ValStack.push(new Token<>(v1 + v2, TokenType.integer));
                            } else if (X1.type == TokenType.integer && X2.type == TokenType.floating) {
                                int v1 = (Integer) X1.data;
                                double v2 = (Double) X2.data;

                                ValStack.push(new Token<>(v1 + v2, TokenType.floating));
                            } else if (X1.type == TokenType.floating && X2.type == TokenType.integer) {
                                double v1 = (Double) X1.data;
                                int v2 = (Integer) X2.data;

                                ValStack.push(new Token<>(v1 + v2, TokenType.floating));
                            } else if (X1.type == TokenType.floating && X2.type == TokenType.floating) {
                                double v1 = (Double) X1.data;
                                double v2 = (Double) X2.data;

                                ValStack.push(new Token<>(v1 + v2, TokenType.floating));
                            } else if (X1.type == TokenType.str) {
                                String v1 = (String) X1.data;
                                if (X2.type == TokenType.str) {
                                    String v2 = (String) X2.data;
                                    ValStack.push(new Token<>(v2 + v1, TokenType.str));
                                } else if (X2.type == TokenType.integer) {
                                    int v2 = (Integer) X2.data;
                                    ValStack.push(new Token<>(v2 + v1, TokenType.str));
                                } else if (X2.type == TokenType.floating) {
                                    double v2 = (Double) X2.data;
                                    ValStack.push(new Token<>(v2 + v1, TokenType.str));
                                } else if (X2.type == TokenType.bool) {
                                    boolean v2 = (Boolean) X2.data;
                                    ValStack.push(new Token<>(v2 + v1, TokenType.str));
                                }
                            } else if (X2.type == TokenType.str) {
                                String v2 = (String) X2.data;
                                if (X1.type == TokenType.str) {
                                    String v1 = (String) X1.data;
                                    ValStack.push(new Token<>(v1 + v2, TokenType.str));
                                } else if (X1.type == TokenType.integer) {
                                    int v1 = (Integer) X1.data;
                                    ValStack.push(new Token<>(v1 + v2, TokenType.str));
                                } else if (X1.type == TokenType.floating) {
                                    double v1 = (Double) X1.data;
                                    ValStack.push(new Token<>(v1 + v2, TokenType.str));
                                } else if (X1.type == TokenType.bool) {
                                    boolean v1 = (Boolean) X1.data;
                                    ValStack.push(new Token<>(v1 + v2, TokenType.str));
                                }
                            } else if (X1.type == TokenType.list && X2.type == TokenType.list) {
                                @SuppressWarnings("unchecked")
                                LinkedList<Token<?>> v1 = (LinkedList<Token<?>>) X1.data;
                                @SuppressWarnings("unchecked")
                                LinkedList<Token<?>> v2 = (LinkedList<Token<?>>) X2.data;
                                v1.addAll(v2);
                                ValStack.push(new Token<>(v1, TokenType.list));
                            } else if (X1.type == TokenType.list) {
                                @SuppressWarnings("unchecked")
                                LinkedList<Token<?>> v1 = (LinkedList<Token<?>>) X1.data;
                                v1.addLast(X2);
                                ValStack.push(new Token<>(v1, TokenType.list));
                            } else if (X2.type == TokenType.list) {
                                @SuppressWarnings("unchecked")
                                LinkedList<Token<?>> v2 = (LinkedList<Token<?>>) X2.data;
                                v2.addFirst(X1);
                                ValStack.push(new Token<>(v2, TokenType.list));
                            } else {
                                throw new Exception("Cannot Compile: Addition Type Error!");
                            }
                            break;
                        case "/sub":
                            X1 = ValStack.pop();
                            X2 = ValStack.pop();
                            if (X1.type == TokenType.integer && X2.type == TokenType.integer) {
                                int v1 = (Integer) X1.data;
                                int v2 = (Integer) X2.data;

                                ValStack.push(new Token<>(v1 - v2, TokenType.integer));
                            } else if (X1.type == TokenType.integer && X2.type == TokenType.floating) {
                                int v1 = (Integer) X1.data;
                                double v2 = (Double) X2.data;

                                ValStack.push(new Token<>(v1 - v2, TokenType.floating));
                            } else if (X1.type == TokenType.floating && X2.type == TokenType.integer) {
                                double v1 = (Double) X1.data;
                                int v2 = (Integer) X2.data;

                                ValStack.push(new Token<>(v1 - v2, TokenType.floating));
                            } else if (X1.type == TokenType.floating && X2.type == TokenType.floating) {
                                double v1 = (Double) X1.data;
                                double v2 = (Double) X2.data;

                                ValStack.push(new Token<>(v1 - v2, TokenType.floating));
                            } else if (X1.type == TokenType.str && X2.type == TokenType.integer) {
                                String v1 = (String) X1.data;
                                int v2 = (int) X2.data;
                                String result = new StringBuilder(v1).deleteCharAt(v2).toString();
                                ValStack.push(new Token<>(result, TokenType.str));
                            } else if (X1.type == TokenType.integer && X2.type == TokenType.str) {
                                String v1 = (String) X2.data;
                                int v2 = (int) X1.data;
                                ValStack.push(new Token<>(v1.charAt(v2) + "", TokenType.str));
                            } else {
                                throw new Exception("Cannot Compile: Subtraction Type Error!");
                            }
                            break;
                        case "/mul":
                            X1 = ValStack.pop();
                            X2 = ValStack.pop();
                            if (X1.type == TokenType.integer && X2.type == TokenType.integer) {
                                int v1 = (Integer) X1.data;
                                int v2 = (Integer) X2.data;

                                ValStack.push(new Token<>(v1 * v2, TokenType.integer));
                            } else if (X1.type == TokenType.integer && X2.type == TokenType.floating) {
                                int v1 = (Integer) X1.data;
                                double v2 = (Double) X2.data;

                                ValStack.push(new Token<>(v1 * v2, TokenType.floating));
                            } else if (X1.type == TokenType.floating && X2.type == TokenType.integer) {
                                double v1 = (Double) X1.data;
                                int v2 = (Integer) X2.data;

                                ValStack.push(new Token<>(v1 * v2, TokenType.floating));
                            } else if (X1.type == TokenType.floating && X2.type == TokenType.floating) {
                                double v1 = (Double) X1.data;
                                double v2 = (Double) X2.data;

                                ValStack.push(new Token<>(v1 * v2, TokenType.floating));
                            } else if (X1.type == TokenType.str && X2.type == TokenType.integer) {
                                String v1 = (String) X1.data;
                                int v2 = (int) X2.data;
                                String muled = "";

                                for (int i = 0; i < v2; i++) {
                                    muled += v1;
                                }

                                ValStack.push(new Token<>(muled, TokenType.str));
                            } else if (X1.type == TokenType.integer && X2.type == TokenType.str) {
                                int v1 = (int) X1.data;
                                String v2 = (String) X2.data;
                                String muled = "";
                                for (int i = 0; i < v1; i++) {
                                    muled += v1;
                                }

                                ValStack.push(new Token<>(muled, TokenType.str));
                            } else if (X1.type == TokenType.str && X2.type == TokenType.str) {
                                String v1 = (String) X1.data;
                                String v2 = (String) X2.data;

                                ValStack.push(new Token<>(countSubstringOccurrences(v1, v2), TokenType.str));
                            } else if (X1.type == TokenType.str && X2.type == TokenType.wildcard) {
                                String v1 = (String) X1.data;
                                String v2 = (String) X2.data;
                                ValStack.push(new Token<>(v1.length(), TokenType.integer));
                            } else if (X1.type == TokenType.wildcard && X2.type == TokenType.wildcard
                                    || X2.type == TokenType.str) {
                                throw new Exception("Infinity Error");
                            } else {
                                throw new Exception("Cannot Compile: Multiplication Type Error!");
                            }
                            break;
                        case "/div":
                            X1 = ValStack.pop();
                            X2 = ValStack.pop();
                            if (X1.type == TokenType.integer && X2.type == TokenType.integer) {
                                int v1 = (Integer) X1.data;
                                int v2 = (Integer) X2.data;

                                ValStack.push(new Token<>(v1 / v2, TokenType.integer));
                            } else if (X1.type == TokenType.integer && X2.type == TokenType.floating) {
                                int v1 = (Integer) X1.data;
                                double v2 = (Double) X2.data;

                                ValStack.push(new Token<>(v1 / v2, TokenType.floating));
                            } else if (X1.type == TokenType.floating && X2.type == TokenType.integer) {
                                double v1 = (Double) X1.data;
                                int v2 = (Integer) X2.data;

                                ValStack.push(new Token<>(v1 / v2, TokenType.floating));
                            } else if (X1.type == TokenType.floating && X2.type == TokenType.floating) {
                                double v1 = (Double) X1.data;
                                double v2 = (Double) X2.data;

                                ValStack.push(new Token<>(v1 / v2, TokenType.floating));
                            } else if (X1.type == TokenType.str && X2.type == TokenType.integer) {
                                String v1 = (String) X1.data;
                                int v2 = (int) X2.data;

                                ValStack.push(new Token<>(v1.substring(0, v2), TokenType.str));
                            } else if (X1.type == TokenType.integer && X2.type == TokenType.str) {
                                int v1 = (int) X1.data;
                                String v2 = (String) X2.data;

                                ValStack.push(new Token<>(v2.substring(v1, v2.length()), TokenType.str));
                            } else if (X1.type == TokenType.str && X2.type == TokenType.str) {
                                String v1 = (String) X1.data;
                                String v2 = (String) X2.data;

                                ValStack.push(new Token<>(v1.replaceAll(v2, ""), TokenType.str));
                            } else {
                                throw new Exception("Cannot Compile: Division Type Error!");
                            }
                            break;
                        case "/mod":
                            X1 = ValStack.pop();
                            X2 = ValStack.pop();
                            if (X1.type == TokenType.integer && X2.type == TokenType.integer) {
                                int v1 = (Integer) X1.data;
                                int v2 = (Integer) X2.data;

                                ValStack.push(new Token<>(v1 % v2, TokenType.integer));
                            } else {
                                throw new Exception("Cannot Compile: Modulus Type Error!");
                            }
                            break;
                        case "/pow":
                            X1 = ValStack.pop();
                            X2 = ValStack.pop();
                            if (X1.type == TokenType.integer && X2.type == TokenType.integer) {
                                int v1 = (Integer) X1.data;
                                int v2 = (Integer) X2.data;

                                ValStack.push(new Token<>(Math.pow(v1, v2), TokenType.floating));
                            } else if (X1.type == TokenType.integer && X2.type == TokenType.floating) {
                                int v1 = (Integer) X1.data;
                                double v2 = (Double) X2.data;

                                ValStack.push(new Token<>(Math.pow(v1, v2), TokenType.floating));
                            } else if (X1.type == TokenType.floating && X2.type == TokenType.integer) {
                                double v1 = (Double) X1.data;
                                int v2 = (Integer) X2.data;

                                ValStack.push(new Token<>(Math.pow(v1, v2), TokenType.floating));

                            } else if (X1.type == TokenType.floating && X2.type == TokenType.floating) {
                                double v1 = (Double) X1.data;
                                double v2 = (Double) X2.data;

                                ValStack.push(new Token<>(Math.pow(v1, v2), TokenType.floating));
                            } else {
                                throw new Exception("Cannot Compile: Exponent Type Error!");
                            }
                            break;
                        default:
                            throw new Exception("Cannot Compile: Invaild Command!");
                    }
                    break;
                case TokenType.singleArith:
                    switch ((String) cur.data) {
                        case "/abs":
                            Token<?> X = ValStack.pop();
                            if (X.type == TokenType.integer) {
                                int v = (Integer) X.data;
                                ValStack.push(new Token<>(Math.abs(v), TokenType.floating));
                            } else if (X.type == TokenType.floating) {
                                double v = (Double) X.data;
                                ValStack.push(new Token<>(Math.abs(v), TokenType.floating));
                            } else {
                                throw new Exception("Cannot Compile: Addition Type Error!");
                            }
                            break;
                        default:
                            throw new Exception("Cannot Compile: Invaild Command!");
                    }
                    break;
                case TokenType.output:
                    Token<?> print = ValStack.pop();
                    switch ((String) cur.data) {
                        case "/print":
                            System.out.print(print.data);
                            break;
                        case "/println":
                            System.out.println(print.data);
                            break;
                        default:
                            throw new Exception("Cannot Compile: Invaild Command!");
                    }
                    break;
                case TokenType.vardec:
                    switch ((String) cur.data) {
                        case "/var":
                            Token<?> variable = ValStack.pop();
                            Token<?> dat = ValStack.pop();
                            if (variable.type == TokenType.varname) {
                                variables.put((String) variable.data, dat);
                            } else {
                                throw new Exception("Cannot Compile: Invaild Variable Name!");
                            }
                            break;
                        default:
                            throw new Exception("Cannot Compile: Invaild Command!");
                    }
                    break;
                case TokenType.var:
                    String varName = ((String) cur.data).substring(1);
                    if (variables.containsKey(varName)) {
                        ValStack.push(variables.get(varName));
                    }
                    break;
                case TokenType.logical:
                    switch ((String) cur.data) {
                        case "/and":
                            Token<?> X2 = ValStack.pop();
                            Token<?> X1 = ValStack.pop();
                            if (X1.type == TokenType.bool && X2.type == TokenType.bool) {
                                ValStack.push(new Token<>(((Boolean) X1.data) && ((Boolean) X2.data), TokenType.bool));
                            } else {
                                throw new Exception("Not a bool type.");
                            }
                            break;
                        case "/or":
                            X2 = ValStack.pop();
                            X1 = ValStack.pop();
                            if (X1.type == TokenType.bool && X2.type == TokenType.bool) {
                                ValStack.push(new Token<>(((Boolean) X1.data) || ((Boolean) X2.data), TokenType.bool));
                            } else {
                                throw new Exception("Not a bool type.");
                            }
                            break;
                        default:
                            throw new Exception("Cannot Compile: Invaild Command!");
                    }
                    break;
                case TokenType.logicalnot:
                    switch ((String) cur.data) {
                        case "/not":
                            Token<?> X = ValStack.pop();
                            if (X.type == TokenType.bool) {
                                ValStack.push(new Token<>(!((Boolean) X.data), TokenType.bool));
                            } else {
                                throw new Exception("Not a bool type.");
                            }
                            break;
                        default:
                            throw new Exception("Cannot Compile: Invaild Command!");
                    }
                    break;
                case TokenType.conditional:
                    switch ((String) cur.data) {
                        case "/gteq":
                            Token<?> X1 = ValStack.pop();
                            Token<?> X2 = ValStack.pop();

                            if (X1.type == X2.type) {
                                if (X1.type == TokenType.integer) {
                                    int v1 = (Integer) X1.data;
                                    int v2 = (Integer) X2.data;
                                    if (v1 >= v2) {
                                        ValStack.push(new Token<>(true, TokenType.bool));
                                    } else {
                                        ValStack.push(new Token<>(false, TokenType.bool));
                                    }
                                } else if (X1.type == TokenType.floating) {
                                    double v1 = (Double) X1.data;
                                    double v2 = (Double) X2.data;
                                    if (v1 >= v2) {
                                        ValStack.push(new Token<>(true, TokenType.bool));
                                    } else {
                                        ValStack.push(new Token<>(false, TokenType.bool));
                                    }
                                } else if (X1.type == TokenType.str) {
                                    String v1 = (String) X1.data;
                                    String v2 = (String) X2.data;
                                    if (v1.compareTo(v2) >= 0) {
                                        ValStack.push(new Token<>(true, TokenType.bool));
                                    } else {
                                        ValStack.push(new Token<>(false, TokenType.bool));
                                    }
                                } else {
                                    throw new Exception("Cannot compare /gteq with bools.");
                                }
                            } else {
                                throw new Exception("Type mismatch. Can only compare two values of the same type.");
                            }
                            break;
                        case "/gt":
                            X1 = ValStack.pop();
                            X2 = ValStack.pop();

                            if (X1.type == X2.type) {
                                if (X1.type == TokenType.integer) {
                                    int v1 = (Integer) X1.data;
                                    int v2 = (Integer) X2.data;
                                    if (v1 > v2) {
                                        ValStack.push(new Token<>(true, TokenType.bool));
                                    } else {
                                        ValStack.push(new Token<>(false, TokenType.bool));
                                    }
                                } else if (X1.type == TokenType.floating) {
                                    double v1 = (Double) X1.data;
                                    double v2 = (Double) X2.data;
                                    if (v1 > v2) {
                                        ValStack.push(new Token<>(true, TokenType.bool));
                                    } else {
                                        ValStack.push(new Token<>(false, TokenType.bool));
                                    }
                                } else if (X1.type == TokenType.str) {
                                    String v1 = (String) X1.data;
                                    String v2 = (String) X2.data;
                                    if (v1.compareTo(v2) > 0) {
                                        ValStack.push(new Token<>(true, TokenType.bool));
                                    } else {
                                        ValStack.push(new Token<>(false, TokenType.bool));
                                    }
                                } else {
                                    throw new Exception("Cannot compare /gteq with bools.");
                                }
                            } else {
                                throw new Exception("Type mismatch. Can only compare two values of the same type.");
                            }
                            break;
                        case "/lteq":
                            X1 = ValStack.pop();
                            X2 = ValStack.pop();

                            if (X1.type == X2.type) {
                                if (X1.type == TokenType.integer) {
                                    int v1 = (Integer) X1.data;
                                    int v2 = (Integer) X2.data;
                                    if (v1 <= v2) {

                                        ValStack.push(new Token<>(true, TokenType.bool));
                                    } else {
                                        ValStack.push(new Token<>(false, TokenType.bool));
                                    }
                                } else if (X1.type == TokenType.floating) {
                                    double v1 = (Double) X1.data;
                                    double v2 = (Double) X2.data;
                                    if (v1 <= v2) {
                                        ValStack.push(new Token<>(true, TokenType.bool));
                                    } else {
                                        ValStack.push(new Token<>(false, TokenType.bool));
                                    }
                                } else if (X1.type == TokenType.str) {
                                    String v1 = (String) X1.data;
                                    String v2 = (String) X2.data;
                                    if (v1.compareTo(v2) <= 0) {
                                        ValStack.push(new Token<>(true, TokenType.bool));
                                    } else {
                                        ValStack.push(new Token<>(false, TokenType.bool));
                                    }
                                } else {
                                    throw new Exception("Cannot compare /gteq with bools.");
                                }
                            } else {
                                throw new Exception("Type mismatch. Can only compare two values of the same type.");
                            }
                            break;
                        case "/lt":
                            X1 = ValStack.pop();
                            X2 = ValStack.pop();

                            if (X1.type == X2.type) {
                                if (X1.type == TokenType.integer) {
                                    int v1 = (Integer) X1.data;
                                    int v2 = (Integer) X2.data;
                                    if (v1 < v2) {
                                        ValStack.push(new Token<>(true, TokenType.bool));
                                    } else {
                                        ValStack.push(new Token<>(false, TokenType.bool));
                                    }
                                } else if (X1.type == TokenType.floating) {
                                    double v1 = (Double) X1.data;
                                    double v2 = (Double) X2.data;
                                    if (v1 < v2) {
                                        ValStack.push(new Token<>(true, TokenType.bool));
                                    } else {
                                        ValStack.push(new Token<>(false, TokenType.bool));
                                    }
                                } else if (X1.type == TokenType.str) {
                                    String v1 = (String) X1.data;
                                    String v2 = (String) X2.data;
                                    if (v1.compareTo(v2) < 0) {
                                        ValStack.push(new Token<>(true, TokenType.bool));
                                    } else {
                                        ValStack.push(new Token<>(false, TokenType.bool));
                                    }
                                } else {
                                    throw new Exception("Cannot compare /gteq with bools.");
                                }
                            } else {
                                throw new Exception("Type mismatch. Can only compare two values of the same type.");
                            }
                            break;
                        case "/eq":
                            X1 = ValStack.pop();
                            X2 = ValStack.pop();

                            if (X1.type == X2.type) {
                                if (X1.type == TokenType.integer) {
                                    int v1 = (Integer) X1.data;
                                    int v2 = (Integer) X2.data;
                                    if (v1 == v2) {
                                        ValStack.push(new Token<>(true, TokenType.bool));
                                    } else {
                                        ValStack.push(new Token<>(false, TokenType.bool));
                                    }
                                } else if (X1.type == TokenType.floating) {
                                    double v1 = (Double) X1.data;
                                    double v2 = (Double) X2.data;
                                    if (v1 == v2) {
                                        ValStack.push(new Token<>(true, TokenType.bool));
                                    } else {
                                        ValStack.push(new Token<>(false, TokenType.bool));
                                    }
                                } else if (X1.type == TokenType.str) {
                                    String v1 = (String) X1.data;
                                    String v2 = (String) X2.data;
                                    if (v1.compareTo(v2) == 0) {
                                        ValStack.push(new Token<>(true, TokenType.bool));
                                    } else {
                                        ValStack.push(new Token<>(false, TokenType.bool));
                                    }
                                } else {
                                    throw new Exception("Cannot compare /gteq with bools.");
                                }
                            } else {
                                throw new Exception("Type mismatch. Can only compare two values of the same type.");
                            }
                            break;
                        case "/neq":
                            X1 = ValStack.pop();
                            X2 = ValStack.pop();

                            if (X1.type == X2.type) {
                                if (X1.type == TokenType.integer) {
                                    int v1 = (Integer) X1.data;
                                    int v2 = (Integer) X2.data;
                                    if (v1 != v2) {
                                        ValStack.push(new Token<>(true, TokenType.bool));
                                    } else {
                                        ValStack.push(new Token<>(false, TokenType.bool));
                                    }
                                } else if (X1.type == TokenType.floating) {
                                    double v1 = (Double) X1.data;
                                    double v2 = (Double) X2.data;
                                    if (v1 != v2) {
                                        ValStack.push(new Token<>(true, TokenType.bool));
                                    } else {
                                        ValStack.push(new Token<>(false, TokenType.bool));
                                    }
                                } else if (X1.type == TokenType.str) {
                                    String v1 = (String) X1.data;
                                    String v2 = (String) X2.data;
                                    if (v1.compareTo(v2) != 0) {
                                        ValStack.push(new Token<>(true, TokenType.bool));
                                    } else {
                                        ValStack.push(new Token<>(false, TokenType.bool));
                                    }
                                } else {
                                    throw new Exception("Cannot compare /gteq with bools.");
                                }
                            } else {
                                throw new Exception("Type mismatch. Can only compare two values of the same type.");
                            }
                            break;
                        default:
                            throw new Exception("Cannot Compile: Invaild Command!");
                    }
                    break;
                case TokenType.ifstmt:
                    Token<?> checkCond = ValStack.pop();
                    Token<?> trueCond = ValStack.pop();
                    Token<?> elseCond = ValStack.pop();

                    Compiler((String) checkCond.data, variables, ValStack, functions, returnStack);
                    Token<?> condition = ValStack.pop();
                    boolean cond = (Boolean) condition.data;
                    if (cond) {
                        Compiler((String) trueCond.data, variables, ValStack, functions, returnStack);
                    } else {
                        Compiler((String) elseCond.data, variables, ValStack, functions, returnStack);
                    }
                    break;
                case TokenType.functcreate:
                    Token<?> functName = ValStack.pop();
                    Token<?> varNames = ValStack.pop();
                    Token<?> prog = ValStack.pop();
                    HLangFunct n = new HLangFunct((String) functName.data, prog);
                    String[] varNameList = ((String) varNames.data).trim().split("\\s+");
                    ;
                    for (int i = 0; i < varNameList.length; i++) {
                        n.AddParam(varNameList[i]);
                    }
                    functions.put((String) functName.data, n);
                    break;
                case TokenType.functdec:
                    functName = ValStack.pop();
                    Token<?> params = ValStack.pop();
                    LinkedList<Token<?>> paramList = new LinkedList<>();
                    List<String> paramArr = splitArgs(((String) params.data));
                    for (int i = 0; i < paramArr.size(); i++) {
                        paramList.add(Lexer(paramArr.get(i) + ".").get(0));
                    }
                    // System.out.println(paramList);
                    functions.get((String) functName.data).CallFunction(paramList, variables, ValStack, functions,
                            returnStack);
                    break;
                case TokenType.listdec:
                    Token<?> varname = ValStack.pop();
                    Token<?> list = ValStack.pop();
                    variables.put((String) varname.data, list);
                    break;
                case TokenType.cons:
                    varname = ValStack.pop();
                    Token<?> item = ValStack.pop();
                    Token<?> listToken = variables.get((String) varname.data);

                    @SuppressWarnings("unchecked")
                    LinkedList<Token<?>> lists = (LinkedList<Token<?>>) listToken.data;

                    lists.add(item);
                    break;
                case TokenType.car:
                    varname = ValStack.pop();
                    Token<?> store = ValStack.pop();
                    listToken = variables.get((String) varname.data);
                    @SuppressWarnings("unchecked")
                    LinkedList<Token<?>> listcar = (LinkedList<Token<?>>) listToken.data;
                    variables.put((String) store.data, listcar.getFirst());
                    break;
                case TokenType.cdr:
                    varname = ValStack.pop();
                    listToken = variables.get((String) varname.data);
                    store = ValStack.pop();
                    @SuppressWarnings("unchecked")
                    LinkedList<Token<?>> original = (LinkedList<Token<?>>) listToken.data;
                    original.removeFirst();
                    variables.put((String) store.data, new Token<>(original, TokenType.list));
                    break;
                case TokenType.listlogic:
                    varname = ValStack.pop();
                    listToken = variables.get((String) varname.data);
                    @SuppressWarnings("unchecked")
                    LinkedList<Token<?>> checklen = (LinkedList<Token<?>>) listToken.data;
                    ValStack.push(new Token<Boolean>(checklen.isEmpty(), TokenType.bool));
                    break;
                case TokenType.input:
                    Scanner sc = new Scanner(System.in);
                    Token<?> prompt = ValStack.pop();
                    varname = ValStack.pop();
                    System.out.print((String) prompt.data);
                    variables.put((String) varname.data, Lexer(sc.nextLine() + ".").get(0));
                    break;
                case TokenType.mapcar:
                    // Sometime Soon
                    break;
                case TokenType.wildcard:
                    ValStack.push(new Token<>("?", TokenType.wildcard));
                    break;
                case TokenType.returnto:
                    Token<?> dat = ValStack.pop();
                    returnStack.push(dat);
                    break;
                case TokenType.releaseto:
                    Token<?> variable = ValStack.pop();
                    dat = returnStack.pop();
                    if (variable.type == TokenType.varname) {
                        variables.put((String) variable.data, dat);
                    } else {
                        throw new Exception("Cannot Compile: Invaild Variable Name!");
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public static List<String> splitArgs(String s) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        int bracketDepth = 0;
        int parenDepth = 0;
        boolean inString = false;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            // Toggle string mode when we encounter a quote
            if (c == '"') {
                inString = !inString;
                current.append(c);
                continue;
            }

            // Only track brackets/parentheses when we're outside a string
            if (!inString) {
                switch (c) {
                    case '[':
                        bracketDepth++;
                        current.append(c);
                        break;

                    case ']':
                        bracketDepth--;
                        current.append(c);
                        break;

                    case '(':
                        parenDepth++;
                        current.append(c);
                        break;

                    case ')':
                        parenDepth--;
                        current.append(c);
                        break;

                    default:
                        if (Character.isWhitespace(c)
                                && bracketDepth == 0
                                && parenDepth == 0) {

                            if (current.length() > 0) {
                                result.add(current.toString());
                                current.setLength(0);
                            }
                        } else {
                            current.append(c);
                        }
                        break;
                }
            } else {
                // Inside a string, whitespace is part of the argument
                current.append(c);
            }
        }

        if (current.length() > 0) {
            result.add(current.toString());
        }

        return result;
    }

    public static void Compiler(String cmd, HashMap<String, Token<?>> variables, Stack<Token<?>> ValStack,
            HashMap<String, HLangFunct> functions, Stack<Token<?>> returnStack)
            throws Exception {
        ArrayList<String> compiledLines = customSplit(cmd);
        // System.out.println("Split: " + compiledLines);
        for (int i = 0; i < compiledLines.size(); i++) {
            // System.out.println(compiledLines.get(i));
            SingleLineCompiler(compiledLines.get(i) + ".", variables, ValStack, functions, returnStack);
        }
    }

    private static ArrayList<String> customSplit(String cmd) {
        ArrayList<String> res = new ArrayList<>();
        StringBuilder cur = new StringBuilder();

        int braceDepth = 0;
        int parenDepth = 0;
        int arrDepth = 0;
        boolean inStr = false;

        for (int i = 0; i < cmd.length(); i++) {
            char c = cmd.charAt(i);

            if (c == '"') {
                inStr = !inStr;
                cur.append(c);
                continue;
            }

            if (!inStr) {
                if (c == '{')
                    braceDepth++;
                else if (c == '}')
                    braceDepth--;
                else if (c == '(')
                    parenDepth++;
                else if (c == ')')
                    parenDepth--;
                else if (c == '[')
                    arrDepth++;
                else if (c == ']')
                    arrDepth--;

                // split ONLY at top level
                if ((c == '.' || c == ',') && braceDepth == 0 && parenDepth == 0 && arrDepth == 0 && !inStr) {
                    String token = cur.toString().trim();
                    if (!token.isEmpty())
                        res.add(token);
                    cur.setLength(0);
                    continue;
                }
            }

            cur.append(c);
        }

        if (cur.length() > 0) {
            String token = cur.toString().trim();
            if (!token.isEmpty())
                res.add(token);
        }

        return res;
    }

    private static int countSubstringOccurrences(String text, String target) {
        if (text == null || target == null || target.isEmpty()) {
            return 0;
        }

        int count = 0;
        int index = 0;

        while ((index = text.indexOf(target, index)) != -1) {
            count++;
            index += target.length();
        }

        return count;
    }
}
