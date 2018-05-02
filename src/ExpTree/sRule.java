package ExpTree;

/**
* Created by dande_000 on 4/10/2018.
*/
public class sRule extends Rule{
    static final public sRule[] rules = new sRule[]{
            /*new sRule("Infinity", "+", "Infinity", "Infinity", false), //infinity simplification
            new sRule("-Infinity", "-", "-Infinity", "Infinity", false),
            new sRule("Infinity", "*", "Infinity", "Infinity", false),
            new sRule("-Infinity", "*", "Infinity", "-Infinity", false),
            new sRule("-Infinity", "*", "-Infinity", "Infinity", false),*/

            new sRule("L", "+", "0.0", "L"), //anything plus 0 is itself
            commute(new sRule("L", "+", "0.0", "L")), //anything plus 0 is itself
            //new sRule("L", "+", "L", "2 * L", true), //anything plus itself is 2 * itself

            new sRule("L", "-", "L", "0.0"), //anything minus itself is 0
            new sRule("L", "-", "0.0", "L"), //anything minus 0 is itself
            new sRule("0.0", "-", "R", "-1*R"), //anything minus 0 is itself

            new sRule("L", "*", "1.0", "L"), //anything times 1 is itself
            commute(new sRule("L", "*", "1.0", "L")), //anything times 1 is itself
            new sRule("L", "*", "0.0", "0.0"), //anything times 0 is 0
            commute(new sRule("L", "*", "0.0", "0.0")), //anything times 0 is 0

            new sRule("L", "/", "1.0", "L"), //anything divided by 1 is itself
            new sRule("L", "/", "L", "1.0"), //anything divided by itself is 1
            new sRule("0.0", "/", "R", "0.0"), //zero divided by (almost) anything is 0

            new sRule("L", "^", "0.0", "1.0"), //anything to the 0th power is 1
            new sRule("L", "^", "1.0", "L"), //anything to the 1st power is itself
            new sRule("1.0", "^", "R", "1.0"), //1 to any power is

            new sRule("L", "log", "L", "1.0") //Log simplification. See manual addition
    };
    private static sRule commute(sRule s)
    {
        return new sRule(commute(s.left), s.base, commute(s.right), commute(s.equals));
    }
    private static String commute(String s)
    {
        return s.replaceAll("L", "_TEMP").replaceAll("R", "L").replaceAll("_TEMP", "R");
    }

    sRule (String left, String base, String right, String equals)
    {
        super(left, base, right, equals);
    }

    boolean tryRule(TreeNode node)
    {
        String left = this.left; //temporary variables to allow replacing of wildcards
        String right = this.right;
        //System.out.println(this.left + this.base + this.right + "=" + this.equals);
        if (node.getVal().equals(base) && node.getLeft() != null && node.getRight() != null)
        {
            left = left.replaceAll("L", node.getLeft().toString()); //replace wildcards
            right = right.replaceAll("L", node.getLeft().toString());
            left = left.replaceAll("R", node.getRight().toString()); //replace wildcards
            right = right.replaceAll("R", node.getRight().toString());

            if (left.equals(node.getLeft().toString()) && right.equals(node.getRight().toString()))
            {
                return true;
            }
        }
        return false;
    }

    TreeNode applyRule(TreeNode node)
    {
        return TreeNode.softSolve(equals.replaceAll("L", node.getLeft().toString()).replaceAll("R", node.getRight().toString()));
    }

}
