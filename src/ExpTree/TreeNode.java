package ExpTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dande_000 on 8/11/2016.
 */
public class TreeNode
{
    static final private String[] ops = new String[]{"^", "*", "/", "+", "-"};
    static final private String[] oneOps = new String[]{"round", "floor", "ceil", "abs", "asin", "acos", "atan", "acsc", "asec", "acot", "sinh", "cosh", "tanh", "csch", "sech", "coth", "sin", "cos", "tan", "csc", "sec", "cot"};
    static final private String[] twoOps = new String[]{"_", "log", "max", "min"};
    static final private Rule[] rules = new Rule[]{
            /*new Rule("Infinity", "+", "Infinity", "Infinity", false), //infinity simplification
            new Rule("-Infinity", "-", "-Infinity", "Infinity", false),
            new Rule("Infinity", "*", "Infinity", "Infinity", false),
            new Rule("-Infinity", "*", "Infinity", "-Infinity", false),
            new Rule("-Infinity", "*", "-Infinity", "Infinity", false),*/

            new Rule("L", "+", "0.0", "L", true), //anything plus 0 is itself
            new Rule("L", "+", "L", "2 * L", true), //anything plus itself is 2 * itself


            new Rule("L", "-", "L", "0.0", false), //anything minus itself is 0
            new Rule("L", "-", "0.0", "L", false), //anything minus 0 is itself

            new Rule("L", "*", "1.0", "L", true), //anything times 1 is itself
            new Rule("L", "*", "0.0", "0.0", true), //anything times 0 is 0

            new Rule("L", "/", "1.0", "L", false), //anything divided by 1 is itself
            new Rule("L", "/", "L", "1.0", false), //anything divided by itself is 1
            new Rule("0.0", "/", "R", "0.0", false), //zero divided by (almost) anything is 0

            new Rule("L", "^", "0.0", "1.0", false), //anything to the 0th power is 1
            new Rule("L", "^", "1.0", "L", false), //anything to the 1st power is itself
            new Rule("1.0", "^", "R", "1.0", false), //1 to any power is

            new Rule("L", "log", "L", "1.0", true) //Log simplification. See manual addition below
    };
    static final private dRule[] derivatives = new dRule[]{
            new dRule("L", "+", "R", "(_(L)) + (_(R))"), //additive rule
            new dRule("L", "-", "R", "(_(L)) - (_(R))"), //subtractive rule
            new dRule("L", "^", "R", "(L ^ R) * ( ((_(L)) * (R/L)) + ((_(R)) * log(e, L)))"), //generalized power rule
            new dRule("L", "*", "R", "((_(L)) * R) + (L * (_(R))) "), //product rule
            new dRule("L", "/", "R", "( ((_(L))*R) - (L*(_(R))) ) / (R ^ 2)"), //quotient rule

            new dRule("L", "sin", "", "cos(L) * (_(L))"),
            new dRule("L", "cos", "", "-1 * sin(L) * (_(L))"),
            new dRule("L", "tan", "", "(sec(L) ^ 2) * (_(L))"),
            new dRule("L", "sec", "", "(sec(L) * tan(L)) * ((_)L)"),
            new dRule("L", "csc", "", "-1 * (csc(L) * cot(L)) * (_(L))"),
            new dRule("L", "cot", "", "-1 * (1 + (cot(L) ^ 2) ) * (_(L))"),
            new dRule("L", "cot", "", "-1 * (1 + (cot(L) ^ 2) ) * (_(L))"),

            new dRule("L", "asin", "", "(_(L)) / ((1-(L^2))^(1/2))"),
            new dRule("L", "acos", "", "(-1 * (_(L)))/ ((1-(L^2))^(1/2))"),
            new dRule("L", "atan", "", "(_(L))/ (1+(L^2))"),
            new dRule("L", "asec", "", "(_(L))/ (abs(L) * ((L^2) - 1)^(1/2))"),
            new dRule("L", "acsc", "", "(-1*(_(L)))/ (abs(L) * ((L^2) - 1)^(1/2))"),
            new dRule("L", "acot", "", "(-1*(_(L)))/ (1+(L^2))"),

            new dRule("L", "sinh", "", "cosh(L) * (_(L))"),
            new dRule("L", "cosh", "", "sinh(L) * (_(L))"),
            new dRule("L", "tanh", "", "(1 - (tanh(L) ^ 2)) * (_(L))"),
            new dRule("L", "sech", "", "(-1) * (sech(L) * tanh(L)) * (_(L))"),
            new dRule("L", "csch", "", "(-1) * (csch(L) * coth(L)) * (_(L))"),
            new dRule("L", "coth", "", "(1 - (coth(L) ^ 2) ) * (_(L))"),

            new dRule("L", "log", "R", "1/(R * log(e,L))"),
    };

    static final private lRule[] latex = new lRule[]
            {
                    new lRule("L", "+", "R", "L + R"), //addition
                    new lRule("L", "-", "R", "L - R"), //sub
                    new lRule("L", "^", "0.5", "\\sqrt{L}"), //power special case first
                    new lRule("L", "^", "R", "\\left(L\\right) ^ {R}"), //power
                    new lRule("L", "*", "R", "L \\left(R\\right)"), //mult
                    new lRule("L", "/", "R", "\\frac{L}{R}"), //division

                    new lRule("L", "sin", "", "\\sin\\left(L\\right)"),
                    new lRule("L", "cos", "", "\\cos\\left(L\\right)"),
                    new lRule("L", "tan", "", "\\tan\\left(L\\right)"),
                    new lRule("L", "sec", "", "\\sec\\left(L\\right)"),
                    new lRule("L", "csc", "", "\\csc\\left(L\\right)"),
                    new lRule("L", "cot", "", "\\cot\\left(L\\right)"),

                    new lRule("L", "asin", "", "\\arcsin\\left(L\\right)"),
                    new lRule("L", "acos", "", "\\arccos\\left(L\\right)"),
                    new lRule("L", "atan", "", "\\arctan\\left(L\\right)"),
                    new lRule("L", "asec", "", "\\arcsec\\left(L\\right)"),
                    new lRule("L", "acsc", "", "\\arccsc\\left(L\\right)"),
                    new lRule("L", "acot", "", "\\arccot\\left(L\\right)"),

                    new lRule("L", "sinh", "", "\\sinh\\left(L\\right)"),
                    new lRule("L", "cosh", "", "\\cosh\\left(L\\right)"),
                    new lRule("L", "tanh", "", "\\tanh\\left(L\\right)"),
                    new lRule("L", "sech", "", "\\sech\\left(L\\right)"),
                    new lRule("L", "csch", "", "\\csch\\left(L\\right)"),
                    new lRule("L", "coth", "", "\\coth\\left(L\\right)"),

                    new lRule("L", "log", "R", "\\log_{L}\\left(R\\right)"),
            };


    private TreeNode right = null;
    private TreeNode left = null;
    private String val;

    String getVal() {
        return val;
    }

    private void setVal(String val) {
        this.val = val;
    }

    TreeNode getRight() {
        return right;
    }

    private void setRight(TreeNode right) {
        this.right = right;
    }

    TreeNode getLeft() {
        return left;
    }

    private void setLeft(TreeNode left) {
        this.left = left;
    }

    private TreeNode(String val) {
        this.val = val;
    }


    private TreeNode(TreeNode old)
    {
        this(old.val);
        if (old.getLeft() != null)
            this.setLeft(new TreeNode(old.getLeft()));
        if (old.getRight() != null)
            this.setRight(new TreeNode(old.getRight()));
    }

    @Override
    public String toString()
    {
        String toRet = "(";
        if (left != null)
            toRet += left;
        toRet += val;
        if (right != null)
            toRet += right;
        toRet += ")";
        return toRet;
    }

    public static String latex(String s)
    {
        return toLatexR(solve(s)).getVal();
    }

    private static TreeNode toLatexR(TreeNode working)
    {
        TreeNode right;
        TreeNode left;
        try {   //recursively simplify if right/left is an operator with 2 parameters
            right = working.getRight() != null && (containsAny(working.getRight().getVal(), ops) || containsAny(working.getRight().getVal(), oneOps) || containsAny(working.getRight().getVal(), twoOps)) ? toLatexR(working.getRight()) : working.getRight();
            left = working.getLeft() != null && (containsAny(working.getLeft().getVal(), ops)  || containsAny(working.getLeft().getVal(), oneOps) || containsAny(working.getLeft().getVal(), twoOps)) ? toLatexR(working.getLeft()) : working.getLeft();

            working.setRight(right);
            working.setLeft(left);
        } catch (Exception e) {
            System.out.println(":(" + e);
            return working;
        }

        for (lRule l : latex) {
            if (l.tryRule(working))
            {
                working.setVal(l.applyRule(working));
                return working;
            }
        }
        return working;
    }

    private static TreeNode convertToTree(String postfix)
    {
        Stack<TreeNode> trees = new Stack<TreeNode>();
        String[] tokens = postfix.split(" ");
        for (String token: tokens)
        {
            TreeNode node = new TreeNode(token);
            if (containsAny(token, oneOps)) //operator only takes 1 parameter
            {
                node.setLeft(trees.pop());
            }
            else if (containsAny(token, ops) || containsAny(token, twoOps)) //operator, pop two, push new node w/ children as operators
            {
                node.setRight(trees.pop());
                node.setLeft(trees.pop());
            }
            trees.push(node);
        }
        return trees.size() == 1 ? trees.pop() : null;
    }

    private static String convertToPost(String infix)
    {
        String dynPattern = "";
        for (String s : oneOps)
            dynPattern += s + "|";
        for (String s : twoOps)
            dynPattern += s+ "|";
        String pattern = "(\\()|(?:(,|" + dynPattern + "\\))|((?:\\-|\\+)?(?:(?:\\d*\\.\\d+|\\d+)|[a-zA-Z]+)))([\\^\\*\\/\\+\\-])?";
        HashMap<String, Integer> opPrec = new HashMap<String, Integer>();
        opPrec.put("^", 3);
        opPrec.put("*", 2);
        opPrec.put("/", 2);
        opPrec.put("+", 1);
        opPrec.put("-", 1);
        for (String s : oneOps)
            opPrec.put(s, 4);
        for (String s : twoOps)
            opPrec.put(s, 4);
        Stack<String> operators = new Stack<String>();
        String post = "";

        infix = infix.replace(" ", "");
        Pattern p = Pattern.compile(pattern);
        //Group Key:
        // 1: Parenthesis 2 : prec num 3: operator 4: post num or parenthesis
        Matcher m = p.matcher(infix);
        ArrayList<String> split = new ArrayList<String>();
        while(m.find())//parse 2 ops 1 to the tight here
        {
            split.add(m.group(1));
            split.add(m.group(2));
            split.add(m.group(3));
            split.add(m.group(4));
        }
        for (int i = split.size()-1; i >=0; i--)
            if (split.get(i) == null || split.get(i).equals(","))
                split.remove(i);

        System.out.println(split);
        String[] res = new String[split.size()];
        split.toArray(res);
        for (int i = 0; i < res.length; i++)
        {
            String s = res[i];
            s = s.replace(" ", "");
            if (!opPrec.containsKey(s) && !s.equals("(") && !s.equals(")")) //1. print operands as they arrive
            {
                try {
                    post += Double.parseDouble(s) + " ";
                } catch (Exception e)
                {
                    post += s + " ";
                }
            }
            else if(operators.empty() || (operators.peek().equals("(") && !s.equals(")"))) //2. push if empty or ( on top
            {
                operators.push(s);
                post += " ";
            }
            else if(s.equals("(")) //3. push (
            {
                operators.push(s);
                post += " ";
            }
            else if(s.equals(")")) //4. pop and print until (
            {
                String popped;
                while (!operators.empty())
                {
                    post += " ";
                    popped = operators.pop();
                    if (popped.equals("("))
                        break;
                    post += popped ;
                }
            }
            else if(opPrec.get(s) > opPrec.get(operators.peek())) //5. op > .peek()
            {
                operators.push(s);
                post += " ";
            }
            else if(opPrec.get(s).equals(opPrec.get(operators.peek()))) //6. op == .peek()
            {
                post += operators.pop() + " ";
                operators.push(s);
            }
            else if(opPrec.get(s) < opPrec.get(operators.peek())) //7. op < .peek()
            {
                post += operators.pop() + " ";
                i--;
            }
        }
        while(!operators.empty())
        {
            post += " ";
            post += operators.pop();
        }
        while (post.contains("  "))
        {
            post = post.replaceAll("  ", " ");
        }
        if (post.charAt(0) == ' ')
        {
            post = post.substring(1);
        }
        if (post.charAt(post.length()-1) == ' ')
        {
            post = post.substring(0, post.length()-1);
        }
        return post;
    }

    private static TreeNode simplifyTree(TreeNode start) {
        TreeNode working = new TreeNode(start);
        TreeNode right;
        TreeNode left;
        if (working.getRight() == null && working.getLeft() == null)
            return working;
        //calc right and left node if operator detected

        try {   //recursively simplify if right/left is an operator with 2 parameters
            right = working.getRight() != null && (containsAny(working.getRight().getVal(), ops) || containsAny(working.getRight().getVal(), oneOps) || containsAny(working.getRight().getVal(), twoOps)) ? simplifyTree(working.getRight()) : working.getRight();
            left = working.getLeft() != null && (containsAny(working.getLeft().getVal(), ops)  || containsAny(working.getLeft().getVal(), oneOps) || containsAny(working.getLeft().getVal(), twoOps)) ? simplifyTree(working.getLeft()) : working.getLeft();

            working.setRight(right);
            working.setLeft(left);
        } catch (Exception e) {
            System.out.println(":(" + e);
            return working;
        }
        Double rightVal = null;
        Double leftVal = null;

        try {
            rightVal = Double.parseDouble(right.getVal());
        } catch (Exception e) {
        }
        try {
            leftVal = Double.parseDouble(left.getVal());
        } catch (Exception e) {
        }

        //null value means variable or operator values
        if (rightVal != null && (leftVal != null)) //solve 2 param funcs
        {
            double result = 0;
            switch (working.getVal()) {
                case "^":
                    result = Math.pow(leftVal, rightVal);
                    break;
                case "*":
                    result = leftVal * rightVal;
                    break;
                case "/":
                    result = leftVal / rightVal;
                    break;
                case "+":
                    result = leftVal + rightVal;
                    break;
                case "-":
                    result = leftVal - rightVal;
                    break;
                case "max":
                    result = Math.max(leftVal, rightVal);
                    break;
                case "min":
                    result = Math.min(leftVal, rightVal);
                    break;
                case "log":
                    result = Math.log(rightVal) / Math.log(leftVal);
                    break;
                case "_":
                    result = 0;
                    break;
            }
            return new TreeNode("" + result);
        } else if (rightVal == null && leftVal != null) //right is only variable? try rules
        {
            switch (working.getVal()) {
                case "sin":
                    return new TreeNode("" + Math.sin(leftVal));
                case "cos":
                    return new TreeNode("" + Math.cos(leftVal));
                case "tan":
                    return new TreeNode("" + Math.tan(leftVal));
                case "csc":
                    return new TreeNode("" + (1 / Math.sin(leftVal)));
                case "sec":
                    return new TreeNode("" + (1 / Math.cos(leftVal)));
                case "cot":
                    return new TreeNode("" + (1 / Math.tan(leftVal)));
                case "round":
                    return new TreeNode("" + Math.round(leftVal));
                case "floor":
                    return new TreeNode("" + Math.floor(leftVal));
                case "ceil":
                    return new TreeNode("" + Math.ceil(leftVal));
                case "_":
                    return TreeNode.solve("0");
                case "asin":
                    return new TreeNode("" + Math.asin(leftVal));
                case "acos":
                    return new TreeNode("" + Math.acos(leftVal));
                case "atan":
                    return new TreeNode("" + Math.atan(leftVal));
                case "acsc":
                    return new TreeNode("" + (1 / Math.asin(leftVal)));
                case "asec":
                    return new TreeNode("" + (1 / Math.acos(leftVal)));
                case "acot":
                    return new TreeNode("" + (1 / Math.atan(leftVal)));
                case "sinh":
                    return new TreeNode("" + Math.sinh(leftVal));
                case "cosh":
                    return new TreeNode("" + Math.cosh(leftVal));
                case "tanh":
                    return new TreeNode("" + Math.tanh(leftVal));
                case "csch":
                    return new TreeNode("" + (1 / Math.sinh(leftVal)));
                case "sech":
                    return new TreeNode("" + (1 / Math.cosh(leftVal)));
                case "coth":
                    return new TreeNode("" + (1 / Math.tanh(leftVal)));
                case "log":
                    if (working.getRight().getVal().equals("^"))
                    {
                        return TreeNode.solve(working.getRight().getRight() + "* log("+ working.getLeft().getVal() +"," + working.getRight().getLeft() +") ");
                    }
                    break;
                case "^":
                    if (working.getLeft().getVal().equals("^"))
                    {
                        return TreeNode.solve(working.getLeft().getLeft() + " ^ " + working.getLeft().getRight() + " * " + working.getRight() );
                    }
                    break;
            }

            for (int i = 0; i < rules.length; i++) {
                working = rules[i].tryRule(working);
            }
            return working;
        } else if (rightVal != null) //left is only variable? try rules + single params
        {
            switch (working.getVal()) {
                case "_":
                    return deriveTree(working.getRight(), working.getLeft().getVal());
            }
            for (int i = 0; i < rules.length; i++) {
                working = rules[i].tryRule(working);
            }
            return working;
        } else //both null
        {
            switch (working.getVal()) {
                case "_":
                    return deriveTree(working.getRight(), working.getLeft().getVal());
                case "log":
                    if (working.getRight().getVal().equals("^"))
                    {
                        return TreeNode.solve(working.getRight().getRight() + "* log("+ working.getLeft().getVal() +"," + working.getRight().getLeft() +") ");
                    }
                    break;
            }
            if (working.getLeft() != null && working.getRight() != null) //no operators to simplify.
            {
                for (int i = 0; i < rules.length; i++) {
                    working = rules[i].tryRule(working);
                }
            }
            return working;
        }
    }

    private static boolean containsAny(String toCheck, String[] strings) {
        if (toCheck == null || toCheck.length() == 0 || strings.length == 0)
            return false;
        for (String s : strings) {
            if (toCheck.equals(s))
                return true;
        }
        return false;
    }

    private static TreeNode limitTree(TreeNode start, String var, String approaches) {
        TreeNode working = new TreeNode(start);
        working = simplifyTree(working);
        System.out.println("W" + working);
        if (!working.toString().contains(var)) //limit of a constant is that constant
        {
            return working;
        }
        if (approaches.equals("Infinity") || approaches.equals("-Infinity")) {
            switch (working.getVal()) {
                case "/":
                    String denom = working.getLeft().toString();
                    String num = working.getRight().toString();
                    if (denom.contains(var) && num.contains(var))// L'hopital's rule to the rescue!
                    {
                        TreeNode dLeft = deriveTree(working.getLeft(), var);
                        TreeNode dRight = deriveTree(working.getRight(), var);
                        working.setLeft(dLeft);
                        working.setRight(dRight);
                        working = limitTree(working, var, approaches); //apply the rule recursively
                    } else //we have a winner!
                    {
                        TreeNode dLeft = deriveTree(working.getLeft(), var); //strip to the coefficients
                        TreeNode dRight = deriveTree(working.getRight(), var); //or something like that
                        working.setLeft(dLeft);
                        working.setRight(dRight);
                    }
                    return working;
                case "*":
                    working.setLeft(limitTree(working.getLeft(), var, approaches));
                    working.setRight(limitTree(working.getRight(), var, approaches));
                    return working;
                case "+":
                    if (working.getLeft().getVal().equals("-" + var)) {
                        working.getLeft().setVal("-");
                        working.getLeft().setLeft(new TreeNode("0"));
                        working.getLeft().setRight(new TreeNode(var));
                    }
                    working.setLeft(limitTree(working.getLeft(), var, approaches));
                    System.out.println("k");
                    if (working.getRight().getVal().equals("-" + var)) {
                        working.getRight().setVal("-");
                        working.getRight().setLeft(new TreeNode("0"));
                        working.getRight().setRight(new TreeNode(var));

                    }
                    working.setRight(limitTree(working.getRight(), var, approaches));
                    return working;
                case "-":
                    System.out.println(working);
                    String toSub = working.getRight().toString();
                    String number = working.getLeft().toString();
                    if (toSub.contains(var) && number.contains(var))// Indeterminate. Divide by x
                    {
                        TreeNode tempWorking = new TreeNode("/");
                        tempWorking.setLeft(new TreeNode(working));
                        tempWorking.setRight(new TreeNode(var));

                        working = limitTree(tempWorking, var, approaches); //apply the rule recursively
                    } else //we have a winner!
                    {
                        if (number.contains(var)) {
                            return new TreeNode(approaches);
                        } else if (toSub.contains(var)) {
                            return new TreeNode(approaches.equals("Infinity") ? "-Infinity" : "Infinity");
                        }
                    }
                    return working;
                default:
                    working = simplifyTree(substituteVarsInTree(working, new String[]{var}, new String[]{approaches}));
                    return working;
            }
        } else {
            //attempt a substitute
            switch (working.getVal()) {
                case "/":
                    TreeNode denom = simplifyTree(substituteVarsInTree(working.getRight(), new String[]{var}, new String[]{approaches}));
                    TreeNode num = simplifyTree(substituteVarsInTree(working.getLeft(), new String[]{var}, new String[]{approaches}));
                    if (denom.getVal().equals("0.0") && num.getVal().equals("0.0"))// L'hospital's rule to the rescue!
                    {
                        TreeNode dLeft = deriveTree(working.getLeft(), var);
                        TreeNode dRight = deriveTree(working.getRight(), var);
                        working.setLeft(dLeft);
                        working.setRight(dRight);
                        working = limitTree(working, var, approaches); //apply the rule recursively
                    } else //sub worked
                    {
                        working.setRight(denom);
                        working.setLeft(num);
                        working = simplifyTree(substituteVarsInTree(working, new String[]{var}, new String[]{approaches}));
                    }
                    return working;
                case "*":
                    working.setLeft(limitTree(working.getLeft(), var, approaches));
                    working.setRight(limitTree(working.getRight(), var, approaches));
                    return working;
                case "+":
                    working.setLeft(limitTree(working.getLeft(), var, approaches));
                    working.setRight(limitTree(working.getRight(), var, approaches));
                    return working;
                case "-":
                    working.setLeft(limitTree(working.getLeft(), var, approaches));
                    working.setRight(limitTree(working.getRight(), var, approaches));
                    return working;
                default:
                    working = simplifyTree(substituteVarsInTree(working, new String[]{var}, new String[]{approaches}));
                    return working;
            }
        }
    }

    private static TreeNode deriveTree(TreeNode start, String respectTo) {
        TreeNode working = new TreeNode(start);
        System.out.println(start.toString() + " with respect to " + respectTo);
        if (working.getVal().equals("e")) return TreeNode.solve("0");
        try {
            Double.parseDouble(working.getVal());
            return TreeNode.solve("0");
        } catch (Exception e) {
            if (working.getVal().equals(respectTo)) {
                return TreeNode.solve("1");}
        }
        for (dRule r : derivatives) {
            if (r.tryRule(working, respectTo))
            {
                return r.applyRule(working, respectTo);
            }
        }
        return TreeNode.solve("0");//TreeNode.solve("d" + working.getVal() + "/ d" + respectTo);
    }

    private static TreeNode substituteVarsInTree(TreeNode start, String[] vars, double[] nums) {
        if (vars.length != nums.length)
            return null;
        String[] newNums = new String[nums.length];
        for (int i = 0; i < newNums.length; i++) {
            newNums[i] = "" + nums[i];
        }
        return substituteVarsInTree(start, vars, newNums);
    }

    private static TreeNode substituteVarsInTree(TreeNode start, String[] vars, String[] nums) {
        if (vars.length != nums.length)
            return null;
        TreeNode working = new TreeNode(start);
        for (int i = 0; i < nums.length; i++) //put everything in a double friendly format
        {
            try {
                Double num = Double.parseDouble(nums[i]);
                nums[i] = num.toString();
            } catch (Exception e) {
            }
        }
        for (int i = 0; i < vars.length; i++) {
            if (working.getVal().equals(vars[i])) {
                working.setVal(nums[i]);
                break;
            }
        }
        if (working.getLeft() != null) {
            working.setLeft(substituteVarsInTree(working.getLeft(), vars, nums));
        }
        if (working.getRight() != null) {
            working.setRight(substituteVarsInTree(working.getRight(), vars, nums));
        }
        return working;
    }

    /*public static TreeNode limit(String var, String approaches, String s) {
        return simplifyTree(limitTree(solve(s), var, approaches));
    }*/

    public static TreeNode solve(String s) {return simplifyTree(convertToTree(convertToPost(s)));}

    public static TreeNode derive(String s, String respect) {
        return simplifyTree(deriveTree(solve(s), respect));
    }

    private static String buildLinearProblem(String s, String[] vars, String[] uncertainty)
    {
        String problem = "(";
        for (int i = 0; i < vars.length;i++)
        {
            problem += "((("+ vars[i] +")_("+ s +"))^2) * ((uncertainty"+ vars[i] +")^2)";
            if (i != vars.length-1)
                problem += "+";
        }
        problem += ")^(1/2)";
        for (int i = 0; i < vars.length; i++)
        {
            problem = problem.replace("uncertainty"+ vars[i] +"",uncertainty[i]);
        }
        System.out.println(problem);
        return problem;
    }

    private static String buildExpProblem(String s, String[] vars, String[] uncertainty, String[] exp, String self)
    {
        String problem = "(" + self + ") * ((";
        for (int i = 0; i < vars.length;i++)
        {
            problem += "((("+ exp[i] +")^2) * ((uncertainty"+ vars[i] +"/" + vars[i]+")^2))";
            if (i != vars.length-1)
                problem += "+";
        }
        problem += ")^(1/2))";
        for (int i = 0; i < vars.length; i++)
        {
            problem = problem.replace("uncertainty"+ vars[i] +"",uncertainty[i]);
        }
        System.out.println(problem);
        return problem;
    }

    public static String propagateFancy(String s, String[] vars, String[] uncertainty)
    {

        return latex(buildLinearProblem(s, vars, uncertainty));
    }
    public static String propagate(String s, String[] vars, String[] uncertainty)
    {

        return solve(buildLinearProblem(s, vars, uncertainty)).toString();
    }

    public static String propagateExpFancy(String s, String[] vars, String[] uncertainty, String[] exp, String self)
    {

        return latex(buildExpProblem(s, vars, uncertainty, exp, self));
    }
    public static String propagateExp(String s, String[] vars, String[] uncertainty, String[] exp, String self)
    {

        return solve(buildExpProblem(s, vars, uncertainty, exp, self)).toString();
    }
}



