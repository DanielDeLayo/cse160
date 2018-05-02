package ExpTree;

/**
 * Created by dande_000 on 2/16/2018.
 */

class lRule extends Rule{
    static final lRule[] rules = new lRule[]
            {
                    new lRule("L", "+", "R", "L + R"), //addition
                    new lRule("L", "-", "R", "L - R"), //sub
                    new lRule("L", "^", "0.5", "\\sqrt{L}"), //power special case first
                    new lRule("L", "^", "R", "\\left(L\\right) ^ {R}"), //power
                    //new lRule("-1.0", "*", "R", "- \\left(R\\right)"), //special case Mult
                    new lRule("L", "*", "R", "L \\, R"),//\\left(R\\right)"), //mult
                    new lRule("L", "/", "R", "\\frac{L}{R}"), //division
                    new lRule("L", "_", "R", "\\frac{\\partial}{\\partial L}\\left(R\\right)"), //division

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
                    //new lRule("", "pi", "", "\\pi"),
            };

    @Override
    public String toString()
    {
        return left + " " + base + " " + right + " = " + equals;
    }

    lRule(String left, String base, String right, String equals)
    {
        super(left, base, right, equals);
    }

    boolean tryRule(TreeNode node)
    {
        if (node == null)
            return false;
        if (node.getLeft() == null || !node.getVal().equals(base))
            return false;
        if (node.getRight() != null && (right.equals("R") || right.equals(node.getRight().toString())))
        {
            return true;
        }
        if (left.equals("") && right.equals("") && node.getVal().equals(base))
            return true;
        return false;
    }
    String applyRule(TreeNode node)
    {
        String equals = this.equals;
        if (node.getLeft() != null) {
            equals = equals.replace("L", node.getLeft().getVal()/*.replace("(", "{").replace(")", "}")*/);
        }
        if(node.getRight() != null) {
            equals = equals.replace("R", node.getRight().getVal()/*.replace("(", "{").replace(")", "}")*/);
        }
        return equals;
    }
}
