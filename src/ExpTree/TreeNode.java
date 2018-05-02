package ExpTree;

import sun.reflect.generics.tree.Tree;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.*;

/**
 * Created by dande_000 on 8/11/2016.
 */
public class TreeNode implements Comparable<TreeNode>
{
    static final private String[] ops = new String[]{"^", "*", "/", "+", "-"};
    static final private String[] oneOps = new String[]{"round", "floor", "ceil", "abs", "asin", "acos", "atan", "acsc", "asec", "acot", "sinh", "cosh", "tanh", "csch", "sech", "coth", "sin", "cos", "tan", "csc", "sec", "cot"};
    static final private String[] twoOps = new String[]{"_", "log", "max", "min"};
    static final private int THREAD_LIMIT = 10;
    static public boolean hasOPs(TreeNode tree)
    {
        return (containsAny(tree.getVal(), ops) || containsAny(tree.getVal(), twoOps) || containsAny(tree.getVal(), oneOps));
    }
    static public boolean hasConsts(TreeNode tree)
    {
        try
        {
            Double.parseDouble(tree.getVal());
        } catch (Exception e)
        {
            return false;
        }
        return true;
    }

    private TreeNode right = null;
    private TreeNode left = null;
    private String val;

    public boolean hasLeft()
    {
        return left != null;
    }
    public boolean hasRight()
    {
        return right != null;
    }
    String getVal() {
        return val;
    }

    private void setVal(String val) {
        this.val = val;
    }

    TreeNode getRight() {
        return right;
    }

    protected void setRight(TreeNode right) {
        this.right = new TreeNode(right);
    }

    TreeNode getLeft() {
        return left;
    }

    protected void setLeft(TreeNode left) {
        this.left = new TreeNode(left);
    }

    public TreeNode(String val) {
        this.val = val;
    }


    public TreeNode(TreeNode old)
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
        System.out.println("s" + s);
        return toLatexR(TreeNode.reduceCalc(TreeNode.softSolve(s))).getVal();
    }

    private static TreeNode toLatexR(TreeNode working)
    {
        TreeNode right;
        TreeNode left;
        try {   //recursively simplify if right/left is an operator with 2 parameters
            right = working.hasRight() && (containsAny(working.getRight().getVal(), ops) || containsAny(working.getRight().getVal(), oneOps) || containsAny(working.getRight().getVal(), twoOps)) ? toLatexR(working.getRight()) : working.getRight();
            left = working.hasLeft() && (containsAny(working.getLeft().getVal(), ops)  || containsAny(working.getLeft().getVal(), oneOps) || containsAny(working.getLeft().getVal(), twoOps)) ? toLatexR(working.getLeft()) : working.getLeft();

            working.setRight(right);
            working.setLeft(left);
        } catch (Exception e) {
            System.out.println(":( Nothing to simplify");
            return working;
        }

        for (lRule l : lRule.rules) {
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

        //System.out.println(split);
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

    private static TreeNode reduceCalc(TreeNode start)
    {
        return simplifyTree(doStepSimplify(simplifyTree(start), 5, mRule.reduceRules));
    }

    private static TreeNode simplifyTree(TreeNode start) { //FIXME simplify
        System.out.println(start + ", " + start.getLeft() + ", "+ start.getRight());
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
            //System.out.println( right.getVal() + "right Null");
        }
        try {
            leftVal = Double.parseDouble(left.getVal());
        } catch (Exception e) {
            //System.out.println( left.getVal() + "left Null");
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
                /*case "_":
                    result = 0;
                    break;*/
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
                //case "_":
                  //  return TreeNode.softSolve("0");
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
                /*case "log":
                    if (working.getRight().getVal().equals("^"))
                    {
                        return TreeNode.softSolve(working.getRight().getRight() + "* log("+ working.getLeft().getVal() +"," + working.getRight().getLeft() +") ");
                    }
                    break;
                case "^":
                    if (working.getLeft().getVal().equals("^"))
                    {
                        return TreeNode.softSolve(working.getLeft().getLeft() + " ^ " + working.getLeft().getRight() + " * " + working.getRight() );
                    }
                    break;*/
            }


            /*for (sRule s : sRule.rules) {
                if (s.tryRule(working))
                {
                    return s.applyRule(working);
                }
            }*/
            return working;
        } else if (rightVal != null) //left is only variable? try rules + single params
        {
            /*switch (working.getVal()) {
                case "_":
                    return deriveTree(working.getRight(), working.getLeft().getVal());
            }*/
            /*for (sRule s : sRule.rules) {
                if (s.tryRule(working))
                {
                    return s.applyRule(working);
                }
            }*/
            return working;
        } else //both null
        {
            /*switch (working.getVal()) {
                case "_":
                    return deriveTree(working.getRight(), working.getLeft().getVal());
                case "log":
                    if (working.getRight().getVal().equals("^"))
                    {
                        return TreeNode.softSolve(working.getRight().getRight() + "* log("+ working.getLeft().getVal() +"," + working.getRight().getLeft() +") ");
                    }
                    break;
            }*/
            /*if (working.getLeft() != null && working.getRight() != null) //no operators to simplify.
            {
                for (sRule s : sRule.rules) {
                    if (s.tryRule(working))
                    {
                        return  s.applyRule(working);
                    }
                }
                return working;
            }*/
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

    private static TreeNode deriveTree(TreeNode start, String respectTo) { //FIXME
        TreeNode working = new TreeNode(start);
        System.out.println(start.toString() + " with respect to " + respectTo);
        if (working.getVal().equals("e")) return TreeNode.softSolve("0");
        try {
            Double.parseDouble(working.getVal());
            return TreeNode.softSolve("0");
        } catch (Exception e) {
            if (working.getVal().equals(respectTo)) {
                return TreeNode.softSolve("1");}
        }
        for (dRule r : dRule.rules) {
            if (r.tryRule(working, respectTo))
            {
                return r.applyRule(working, respectTo);
            }
        }
        return TreeNode.softSolve("0");//TreeNode.softSolve("d" + working.getVal() + "/ d" + respectTo);
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

    public static String substituteSolve(String s, String[] vars, double[] nums)
    {
        return simplifyTree(substituteVarsInTree(solve(s), vars, nums)).toString();
    }
    public static String substituteSolveFancy(String s, String[] vars, double[] nums)
    {
        return toLatexR(simplifyTree(substituteVarsInTree(solve(s), vars, nums))).toString();
    }

    /*public static TreeNode limit(String var, String approaches, String s) {
        return simplifyTree(limitTree(solve(s), var, approaches));
    }*/

    public static TreeNode solve(String s) {System.out.println("solving" + s);return stepSimplify(convertToTree(convertToPost(s)), 5000);} //FIXME

    public static TreeNode softSolve(String s) {return convertToTree(convertToPost(s));}

    public static TreeNode derive(String s, String respect) {
        return simplifyTree(deriveTree(softSolve(s), respect));
    }

    private static String buildLinearProblem(String s, String[] vars, String[] uncertainty)
    {
        String problem = "(";
        for (int i = 0; i < vars.length;i++)
        {
            System.out.println("Starting!" + vars[i]);
            problem += solve("(( (("+vars[i]+")_("+ s +"))^2) * (("+ uncertainty[i] +")^2))").toString();;
            if (i != vars.length-1)
                problem += "+";
        }
        problem += ")^(1/2)";
        System.out.println("problem:"+problem);
        return problem;
    }
/*
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
*/
    private static TreeSet<String> getVarsInTree(TreeNode tree)
    {
        TreeSet<String> list = new TreeSet<>();
        if (!hasOPs(tree))
        {
            try
            {
                Double.parseDouble(tree.getVal());
            } catch (Exception e)
            {
                list.add(tree.getVal());
            }
        }
        if (tree.getLeft() != null)
            list.addAll(getVarsInTree(tree.getLeft()));
        if (tree.getRight() != null)
            list.addAll(getVarsInTree(tree.getRight()));
        return list;
    }

    public static String[] getVarsInString(String s)
    {
        TreeSet<String> set = getVarsInTree(convertToTree(convertToPost(s)));
        String[] arr = new String[set.size()];
        return set.toArray(arr);
    }

    public static String propagateFancy(String s, String[] vars, String[] uncertainty)
    {
        return latex(propagate(s, vars, uncertainty));
    }
    public static String propagate(String s, String[] vars, String[] uncertainty)
    {
        return (buildLinearProblem(s, vars, uncertainty)).toString();
    }
/*
    public static String propagateExpFancy(String s, String[] vars, String[] uncertainty, String[] exp, String self)
    {

        return latex(buildExpProblem(s, vars, uncertainty, exp, self));
    }
    public static String propagateExp(String s, String[] vars, String[] uncertainty, String[] exp, String self)
    {

        return solve(buildExpProblem(s, vars, uncertainty, exp, self)).toString();
    }
*/

    static public TreeNode stepSimplify(TreeNode parent, int maxSteps)
    {
        System.out.println
                ("p:" + parent);
        TreeNode simple = doStepSimplify(parent, maxSteps, mRule.sRules);
        System.out.println(simple);
        if (simple.toString().contains("_")) {
            simple = doStepDerivative(simple, maxSteps);
            System.out.println(simple);
            simple = doStepSimplify(simple, maxSteps, mRule.sRules);
            System.out.println(simple);
        }
        System.out.println(parent + " to " + simple);
        return simple;
    }

    static private TreeNode doStepDerivative(TreeNode parent, int maxSteps)
    {
        if (!parent.toString().contains("_"))
            return parent;
        int steps = 0;
        String old = "";
        TreeNode temp = new TreeNode(parent);
        while (steps <= maxSteps && !old.equals(temp.toString()))
        {
            old = temp.toString();
            for (mRule rule : mRule.dRules) {
                HashMap<String, TreeNode> varMap = rule.buildAndTryRule(temp);
                if (varMap != null) {
                    temp = TreeNode.reduceCalc(rule.applyRule(varMap));
                    //System.out.println("Applied Rule: ");
                    //System.out.println("result: " + rule.applyRule());
                } else {
                    //System.out.println("Rule Not Applied");
                }
            }

            if (temp.hasLeft()) {
                temp.setLeft(doStepDerivative(temp.getLeft(), maxSteps - steps));
                steps++;
            }

            if (temp.hasRight()) {
                temp.setRight(doStepDerivative(temp.getRight(), maxSteps - steps));
                steps++;
            }
            steps++;
        }
        return temp;
    }

    static private TreeNode doStepSimplify(TreeNode parent, int maxSteps, mRule[] rules)
    {
        int steps = 0;
        Set<TreeNode> trees = new HashSet<TreeNode>();
        Set<TreeNode> toCheckTrees = new HashSet<TreeNode>();
        boolean done = false;
        int numTrees;
        toCheckTrees.add(parent);
        trees.add(parent);
        final int complexityLimit = getComplexityLimit(parent);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_LIMIT);

        while (!done)
        {
            final Set<TreeNode> temp = ConcurrentHashMap.newKeySet();
            steps++;
            numTrees = trees.size();
            final TreeNode toCheckArr[] = new TreeNode[toCheckTrees.size()];
            toCheckTrees.toArray(toCheckArr);

            Collection<Future<?>> tasks = new LinkedList<Future<?>>();
            for (int i = 0; i < toCheckArr.length;i++)
            {
                TreeNode toCheck = toCheckArr[i];
                tasks.add(executor.submit(new Thread(){
                    public void run(){
                            temp.addAll(permutation(toCheck, complexityLimit, rules));
                    }
                }));
            }
            for (Future<?> currTask : tasks) {
                try {
                    currTask.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println("temp:" + temp.size());
            System.out.println("temp:" + temp);
            System.out.println("trees:" + trees.size());
            System.out.println("trees:" + trees);
            System.out.println("best:" + getBestNode(trees));
            System.out.println("steps:" + steps);

            toCheckTrees.clear();
            toCheckTrees.addAll(temp);
            toCheckTrees.removeAll(trees);
            trees.addAll(temp);
            done = (numTrees == trees.size() || steps >= maxSteps);
        }
        System.out.println(trees.size());
        System.out.println(trees);
        executor.shutdownNow();
        return getBestNode(trees);
    }

    static private TreeNode getBestNode(Set<TreeNode> trees)
    {
        TreeNode[] sortMe = new TreeNode[trees.size()];
        trees.toArray(sortMe);
        Arrays.sort(sortMe);
        /*for (TreeNode t : sortMe)
            System.out.print(t.toString() + ",");
        System.out.println();*/
        return sortMe[0];

    }
    static private Set<TreeNode> permutation(TreeNode parent, int complexityLimit, mRule[] ruleset)
    {
        Set<TreeNode> set = new HashSet<>();//new HashSet<TreeNode>();
        TreeNode temp;
        for (mRule rule: ruleset)
        {
            //System.out.println("Trying rule");
            //System.out.println(rule.toString());
            HashMap<String, TreeNode> varMap = rule.buildAndTryRule(parent);
            if (varMap != null)
            {
                temp = TreeNode.reduceCalc(rule.applyRule(varMap));
                if (temp.getComplexity() <= complexityLimit)
                    set.add(temp);
                //System.out.println("Applied Rule: ");
                //System.out.println("result: " + rule.applyRule());
            }
            else {
                //System.out.println("Rule Not Applied");
            }
        }

        if (parent.hasLeft())
        {
            Set<TreeNode> s = permutation(parent.getLeft(), complexityLimit-(parent.getComplexity()-parent.getLeft().getComplexity()), ruleset);
            for (TreeNode n : s)
            {
                //System.out.println(n);
                temp = new TreeNode(parent);
                temp.setLeft(n);
                temp = TreeNode.reduceCalc(temp);
                if (temp.getComplexity() <= complexityLimit)
                    set.add(temp);
            }
        }

        if (parent.hasRight())
        {
            Set<TreeNode> s = permutation(parent.getRight(), complexityLimit-(parent.getComplexity()-parent.getLeft().getComplexity()), ruleset);
            for (TreeNode n : s)
            {
                temp = new TreeNode(parent);
                temp.setRight(n);
                temp = TreeNode.reduceCalc(temp);
                if (temp.getComplexity() <= complexityLimit)
                    set.add(temp);
            }
        }
        //System.out.println("Nope");
        return set;
    }

    //FIXME Derivatives take forever due to large complexity increase
    private static int getComplexityLimit(TreeNode tree)
    {
        return Math.max(tree.getComplexity()*2, 1000 );
    }

    private int getComplexity()
    {
        int complexity = (hasRight()?getRight().getComplexity():0) + (hasLeft()?getLeft().getComplexity():0);
        if (hasOPs(this))
        {
            if (containsAny(getVal(), ops))
            {
                return complexity + 2;
            }
            else if (containsAny(getVal(), twoOps))
            {
                return complexity + 3;
            }
            else
            {
                return complexity + 5;
            }

        }
        try{
            Integer.parseInt(getVal().replace(".0",""));
            return complexity + 1;
        }
        catch (Exception e) {}
        try{
            Double.parseDouble(getVal());
            return complexity + 2;
        }
        catch (Exception e) {}
        return complexity + 2;
        //return toString().length();
    }
    //FIXME build a weighting function
    @Override
    public int compareTo(TreeNode t)
    {
        return getComplexity() + ((this.toString().contains("_"))? 10000 : 0) - (t.getComplexity() + ((t.toString().contains("_"))? 10000 : 0));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TreeNode treeNode = (TreeNode) o;

        if (right != null ? !right.equals(treeNode.right) : treeNode.right != null) return false;
        if (left != null ? !left.equals(treeNode.left) : treeNode.left != null) return false;
        return val.equals(treeNode.val);

    }

    @Override
    public int hashCode(){
        return getVal().hashCode()*31*31 + (hasRight()?getRight().hashCode()*31:0) + (hasLeft()?getLeft().hashCode():0);
    }

}



