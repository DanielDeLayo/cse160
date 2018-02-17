package ExpTree;

/**
 * Created by dande_000 on 2/16/2018.
 */
class lRule {
    private String left;
    private String base;
    private String right;
    private String equals;
    @Override
    public String toString()
    {
        return left + " " + base + " " + right + " = " + equals;
    }

    lRule(String left, String base, String right, String equals)
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

    boolean tryRule(TreeNode node)
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
