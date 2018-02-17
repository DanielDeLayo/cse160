package ExpTree;

        import sun.reflect.generics.tree.Tree;

/**
 * Created by dande_000 on 9/10/2016.
 */
class Rule
{
    private String left;
    private String base;
    private String right;
    private String equals;
    private boolean commutative;

    private String commute(String s)
    {
        return s.replaceAll("L", "_TEMP").replaceAll("R", "L").replaceAll("_TEMP", "R");
    }

    Rule (String left, String base, String right, String equals, boolean commutative)
    {
        this.left = left;
        if (!left.contains("L") && !left.contains("R") && !left.contains("(") && !left.contains(")"))
        {
            this.left = "(" + this.left + ")";
        }
        this.base = base;
        this.right = right;
        if (!right.contains("L") && !right.contains("R") && !right.contains("(") && !right.contains(")"))
        {
            this.right = "(" + this.right + ")";
        }
        this.equals = equals;
        this.commutative = commutative;
    }

    TreeNode tryRule(TreeNode node)
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
                return TreeNode.solve(equals.replaceAll("L", node.getLeft().toString()).replaceAll("R", node.getRight().toString()));
            }
            else if (commutative)
            {
                return new Rule(commute(this.right), this.base, commute(this.left), commute(this.equals), false).tryRule(node);
            }
        }
        return node;
    }
}
