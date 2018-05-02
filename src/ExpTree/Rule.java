package ExpTree;

/**
 * Created by dande_000 on 9/10/2016.
 */
abstract class Rule
{
    protected final String left;
    protected final String base;
    protected final String right;
    protected final String equals;

    Rule (String left, String base, String right, String equals)
    {
        if (!left.contains("L") && !left.contains("R") && !left.contains("(") && !left.contains(")"))
        {
            this.left = "(" + left + ")";
        }
        else {this.left = left;}
        this.base = base;

        if (!right.contains("L") && !right.contains("R") && !right.contains("(") && !right.contains(")"))
        {
            this.right = "(" + right + ")";
        }else {this.right = right;}
        this.equals = equals;
    }
}
