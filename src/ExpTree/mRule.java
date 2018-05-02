package ExpTree;

import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;
import sun.reflect.generics.tree.DoubleSignature;
import sun.reflect.generics.tree.Tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by dande_000 on 4/14/2018.
 */
public class mRule
{
    static protected final mRule[] reduceRules = new mRule[]{
            new mRule("M + 0", "M"), //anything plus 0 is itself
            new mRule("0 + M", "M"), //anything plus 0 is itself

            new mRule("M - 0", "M"), //anything minus 0 is itself

            new mRule("M * 1", "M"), //anything times 1 is itself
            new mRule("1 * M", "M"), //anything times 1 is itself
            new mRule("M * 0", "0"), //anything times 0 is 0
            new mRule("0 * M", "0"), //anything times 0 is 0

            new mRule("M / 1", "M"), //anything divided by 1 is itself
            new mRule("0 / M", "0"), //anything divided by 1 is itself
            new mRule("M ^ 0", "1"), //anything to the 0th power is 1
    };

    static protected final mRule[] sRules = new mRule[]{
            new mRule("M + 0", "M"), //anything plus 0 is itself
            new mRule("M + M", "2 * M"), //anything plus itself is 2 * itself
            new mRule("M + N", "N + M"), //commute
            new mRule("(M + N) + X", "M + (N + X)"), //associate

            new mRule("M - M", "0"), //anything minus itself is 0
            new mRule("M - 0", "M"), //anything minus 0 is itself
            new mRule("0 - M", "-1 * M"), //anything minus 0 is itself

            new mRule("M * 1", "M"), //anything times 1 is itself
            new mRule("M * 0", "0"), //anything times 0 is 0
            new mRule("M * N", "N * M"), //commute
            new mRule("(M * N) * X", "M * (N * X)"), //associate
            new mRule("X * (M + N)", "(X * M) + (X * N))"), //distribute
            new mRule("(X * M) + (X * N))", "X * (M + N)"), //reverse distribute
            new mRule("(M * N) + N", "(M + 1) * N"), //collect
            new mRule("(M * M)", "M ^ 2"), //advance

            new mRule("M / 1", "M"), //anything divided by 1 is itself
            new mRule("M / M", "1"), //anything divided by itself is 1
            new mRule("0 / 0", "NaN"), //zero divided by (almost) anything is 0
            new mRule("0 / M", "0"), //zero divided by (almost) anything is 0

            new mRule("M ^ 0", "1"), //anything to the 0th power is 1
            new mRule("M ^ 1.0", "M"), //anything to the 1st power is itself
            new mRule("(M ^ N) ^ X", "M ^ (N * X)"), //power of power rule
            //new mRule("M ^ N", "1/ (M ^ (-1 * N))"), //negative exponent rule FIXME child always follows rule
            new mRule("1/ (M ^ N))", "M ^ (-1 * N)"), //negative exponent rule
            new mRule("1/ (M))", "M ^ (-1)"), //negative exponent rule
            new mRule("Y/(X/Z)", "Z * (Y/X)"), //redundancy reducer rule
            new mRule("(M ^ X) * (M ^ Y)", "M ^ (X + Y)"), //power rule
            new mRule("(M) * (M ^ Y)", "M ^ (1 + Y)"), //power rule special case
            new mRule("(M ^ X) / (M ^ Y)", "M ^ (X - Y)"), //quotient rule
            new mRule("(M) / (M ^ Y)", "M ^ (1 - Y)"), //quotient rule special case
            new mRule("(M ^ X) / (M)", "M ^ (X - 1)"), //quotient rule special case
            new mRule("1 ^ M", "1"), //1 to any power is 1
            new mRule("(M ^ N) * M", "M ^ (N + 1)"), //converge powers
            new mRule("(M ^ X) * (N ^ X)", "(M * N) ^ X"), //converge powers
            new mRule("(M) / (N * X)", "(M / N) * (1 / X)"), //converge powers
            new mRule("(M / N) ^ Y", "(M ^ Y) / (N ^ Y)"), //split exp fractions

            new mRule("log(M, M)", "1"), //Log simplification
            new mRule("log(M, X * Y)", "log(M, X) + log(M,Y)"), //Log product
            new mRule("log(M, X / Y)", "log(M, X) - log(M,Y)"), //Log quotient
            new mRule("log(M, X ^ Y)", "Y * log(M, X)"), //Log power
            //new mRule("log(M, X)", "log(10, X) / log(10, M)") //base change //FIXME child always follows rule
    };

    static protected final mRule[] dRules = new mRule[]{

            //derivative rules
            new mRule("D_(M + N)", "(D_(M)) + (D_(N))"), //additive rule
            new mRule("D_(M - N)", "(D_(M)) - (D_(N))"), //subtractive rule
            new mRule("D_(X ^ c)", "(c * (X ^ (c-1))) * (D_X)"), //power rule
            new mRule("D_(c ^ X)", "(c^X) * log(" + /*Math.E*/ "e"+ ", c)"), //other power rule
            new mRule("D_(x ^ y)", "(x ^ y) * ( ( (y) * ((D_(x))/x) ) + ( ( (D_(y)) * log(" + /*Math.E*/ "e"+ ", x)) ))"), // :( power rule
            new mRule("D_(L * R)", "( (D_(L)) * R ) + ( L * (D_(R)) ) "), //product rule
            new mRule("D_(L / R)", "( ((D_(L))*R) - (L*(D_(R))) ) / (R ^ 2)"), //quotient rule

            new mRule("D_(sin (M))", "cos(M) * (D_(M))"),
            new mRule("D_(cos (M))", "-1 * sin(M) * (D_(M))"),
            new mRule("D_(tan (L))", "(sec(L) ^ 2) * (D_(L))"),
            new mRule("D_(sec (L))", "(sec(L) * tan(L)) * ((D_)L)"),
            new mRule("D_(csc (L))", "-1 * (csc(L) * cot(L)) * (D_(L))"),
            new mRule("D_(cot (L))", "-1 * (1 + (cot(L) ^ 2) ) * (D_(L))"),
            new mRule("D_(cot (L))", "-1 * (1 + (cot(L) ^ 2) ) * (D_(L))"),

            new mRule("D_(asin (L))", "(D_(L)) / ((1-(L^2))^(1/2))"),
            new mRule("D_(acos (L))", "(-1 * (D_(L)))/ ((1-(L^2))^(1/2))"),
            new mRule("D_(atan (L))", "(D_(L))/ (1+(L^2))"),
            new mRule("D_(asec (L))", "(D_(L))/ (abs(L) * ((L^2) - 1)^(1/2))"),
            new mRule("D_(acsc (L))", "(-1*(D_(L)))/ (abs(L) * ((L^2) - 1)^(1/2))"),
            new mRule("D_(acot (L))", "(-1*(D_(L)))/ (1+(L^2))"),

            new mRule("D_(sinh (L))", "cosh(L) * (D_(L))"),
            new mRule("D_(cosh (L))", "sinh(L) * (D_(L))"),
            new mRule("D_(tanh (L))", "(1 - (tanh(L) ^ 2)) * (D_(L))"),
            new mRule("D_(sech (L))", "(-1) * (sech(L) * tanh(L)) * (D_(L))"),
            new mRule("D_(csch (L))", "(-1) * (csch(L) * coth(L)) * (D_(L))"),
            new mRule("D_(coth (L))", "(1 - (coth(L) ^ 2) ) * (D_(L))"),

            new mRule("D_(log (B, M))", "(D_(M))/(M * log(e,B))"),
            //derivative base rules
            new mRule("D_D", "1"),
            new mRule("D_c", "0"),
            new mRule("D_u", "0"),


            //latex can use the old system

    };

    protected final TreeNode equation;
    protected final TreeNode equalTo;

    //c is for constant
    //u is for unique
    //other lower case forces variable

    private mRule(String equation, String equalTo)
    {
        this.equation = TreeNode.softSolve(equation);
        this.equalTo = TreeNode.softSolve(equalTo);
    }

    @Override
    public String toString()
    {
        return equation.toString();
    }

    private boolean isIndependentVariable(String toTest, HashMap<String, TreeNode> varMap)
    {
        String var = varMap.get(toTest).toString();
        for (String key : varMap.keySet()) {
            if (key.equals(toTest))
                continue;
            String val = varMap.get(key).toString();
            if (var.contains(val) || val.contains(var))
                return false;
        }
        //System.out.println(varMap);
        return true;
    }

    HashMap<String, TreeNode> buildAndTryRule(TreeNode fit)
    {
        HashMap<String, TreeNode> varMap = new HashMap<String, TreeNode>();
        if (fitsSubRule(this.equation, fit, varMap) &&
                (!varMap.containsKey("u")||(isIndependentVariable("u", varMap))))
            return varMap;
        return null;
    }

    private boolean fitsSubRule(TreeNode subEq, TreeNode subFit, HashMap<String, TreeNode> varMap)
    {
        System.out.println("Fitting " + subEq);
        System.out.println("To " + subFit);
        if (!subEq.hasLeft() && !subEq.hasRight()) //bottom, match constants and store the node
        {
            //System.out.println("Found Primitive:\n" + subEq.getVal());

            if (!TreeNode.hasConsts(subEq)) //no literal constants
            {
                //arbitrary constants
                if (subEq.getVal().equals("c") && !TreeNode.hasConsts(subFit))
                {
                    System.out.println(subEq);
                    System.out.println("fail1");
                    return false;
                }
                if (!(subEq.getVal().equals("u") || subEq.getVal().equals("c")) && subEq.getVal().toLowerCase().equals(subEq.getVal()) && TreeNode.hasConsts(subFit))
                {
                    System.out.println(subEq);
                    System.out.println("fail2");
                    return false;
                }
                //variables
                if (varMap.containsKey(subEq.getVal()) && !varMap.get(subEq.getVal()).equals(subFit)) //variables aren't consistent
                {
                    //System.out.println("Contradiction");
                    return false;
                }
                //System.out.println("Mapping:\n" + subEq.getVal() +"->" + subFit.toString());
                varMap.put(subEq.getVal(), new TreeNode(subFit));
                return true;
            }
            else if (TreeNode.hasConsts(subFit))
            {
                //System.out.println("Const Check");
                return Double.parseDouble(subEq.getVal()) == Double.parseDouble(subFit.getVal());
            }
            else
            {
                return false;
            }
        }

        if (subEq.hasLeft() && subFit.hasLeft()) //left recursion
        {
            if (!fitsSubRule(subEq.getLeft(), subFit.getLeft(), varMap))
                return false;
        }
        else if (subEq.hasLeft() && !subFit.hasLeft())
        {
            return false;
        }

        if (subEq.getRight() != null && subFit.getRight() != null) //right recursion
        {
            if (!fitsSubRule(subEq.getRight(), subFit.getRight(), varMap))
                return false;
        }
        else if (subEq.getRight() != null && subFit.getRight() == null)
        {
            return false;
        }

        //Make sure the conditions apply correctly at non-bottom nodes
        if (TreeNode.hasOPs(subEq)) //force match ops
        {
            if (!subEq.getVal().equals(subFit.getVal()))
                return false;
        }
        return true;
    }
    TreeNode applyRule(HashMap<String, TreeNode> varMap)
    {
        //System.out.println("Applying Map\n" + varMap +"\nto equation\n" + equalTo);
        return applySubRule(new TreeNode(this.equalTo), varMap);
    }

    private TreeNode applySubRule(TreeNode subResult, HashMap<String, TreeNode> varMap)
    { //FIXME literally just plain wrong
        //System.out.println("sNode: "+ subResult);
        if (varMap.containsKey(subResult.getVal()))
        {
            subResult = new TreeNode(varMap.get(subResult.getVal()));
        }
        else {
            if (subResult.hasLeft()) {
                subResult.setLeft(applySubRule(subResult.getLeft(), varMap));
            }
            if (subResult.hasRight()) {
                subResult.setRight(applySubRule(subResult.getRight(), varMap));
            }
        }
        //System.out.println("\teNode: "+ subResult);

        return subResult;

    }
}