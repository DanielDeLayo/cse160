package ExpTree;

/**
 * Created by dande_000 on 2/15/2018.
 */
@Deprecated
class dRule extends Rule{
    static final dRule[] rules = new dRule[]{
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

    @Override
    public String toString()
    {
        return left + " " + base + " " + right + " = " + equals;
    }

    dRule(String left, String base, String right, String equals)
    {
        super(left, base, right, equals);
    }

    boolean tryRule(TreeNode node, String respectTo)
    {
        if (node.getVal().equals(base) && node.getLeft() != null && node.getRight() != null)
        {
            return true;
        }
        else if (node.getVal().equals(base) && node.getLeft() != null)
        {
            return true;
        }
        else
            return false;
    }
    TreeNode applyRule(TreeNode node, String respectTo)
    {
        String equals = this.equals.replaceAll("_", respectTo+"_");
        if (node.getLeft() != null) {
            equals = equals.replaceAll("L", node.getLeft().toString());
        }
        if(node.getRight() != null) {
            equals = equals.replaceAll("R", node.getRight().toString());
        }
        System.out.println(equals);
        return TreeNode.solve(equals);
    }
}
