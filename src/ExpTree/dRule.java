package ExpTree;

/**
 * Created by dande_000 on 2/15/2018.
 */
class dRule {
    private String left;
    private String base;
    private String right;
    private String equals;

    @Override
    public String toString()
    {
        return left + " " + base + " " + right + " = " + equals;
    }

    dRule(String left, String base, String right, String equals)
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
