/**
 * Created by dande_000 on 2/17/2018.
 **/
import ExpTree.TreeNode;

public class Parser {
    /*.
1.  Print operands as they arrive.
2.	If the stack is empty or contains a left parenthesis on top, push the incoming operator onto the stack.
3.	If the incoming symbol is a left parenthesis, push it on the stack.
4.	If the incoming symbol is a right parenthesis, pop the stack and print the operators until you see a left parenthesis.
    Discard the pair of parentheses.
5.	If the incoming symbol has higher precedence than the top of the stack, push it on the stack.
6.	If the incoming symbol has equal precedence with the top of the stack, use association. If the association is left to right, pop
    and print the top of the stack and then push the incoming operator. If the association is right to left, push the incoming operator.
7.	If the incoming symbol has lower precedence than the symbol on the top of the stack, pop the stack and print the top operator.
    Then test the incoming operator against the new top of stack.
8.	At the end of the expression, pop and print all operators on the stack. (No parentheses should remain.)


Algorithm: Have a stack to store intermediate values (which are trees), and examine each token from left to right:

If it is a number, turn it into a leaf node and push it on the stack.
If it is an operator, pop two items from the stack, construct an operator node with those children, and push the new node on the stack.
 */
    public static void main(String[] args) {
        //System.out.println(TreeNode.limit("h", "0", "(((x+h)^(x+h)) - x^x)/h"));
        //System.out.println(TreeNode.latex("tan x"));
        //System.out.println(TreeNode.latex("x _(x^(atan(log(e, cosh(x)))))"));
        //System.out.println(TreeNode.latex("x _(x^(atan(log(e, x))))"));
        //System.out.println(TreeNode.latex("x _(x^(log(e,x)))"));
        //System.out.println(TreeNode.latex("x _ (log(10, x))"));
        //System.out.println(TreeNode.latex("(x_x)/x)"));
        //System.out.println(TreeNode.latex("x _ (log (e, x^2))"));
        System.out.println(TreeNode.latex("x _ (cosh (x))"));
        //System.out.println(TreeNode.latex("m _ ((4 * m * M * g) / (E * l))"));
        //System.out.println(TreeNode.propagateFancy("m * x + b", new String[]{"m", "x", "b"}, new String[]{"apples", "z", TreeNode.propagate("((ba+bb)/2)", new String[]{"ba","bb"}, new String[]{"b", "a"})}));
        //System.out.println(TreeNode.propagateFancy("3/(1 + (C/5))", new String[]{"C"}, new String[]{"deltac"}));
        //System.out.println(TreeNode.propagateFancy("((4 * m * M * g) / (E * l))", new String[]{"E", "m"}, new String[]{"deltalength", "deltaslope"}));
        //System.out.println(TreeNode.propagateFancy("((1) / (L))", new String[]{"L"}, new String[]{"deltaL"}));
        //System.out.println(TreeNode.propagateExpFancy("(2*d)/(t^2)", new String[]{"d", "t"}, new String[]{"ud", "ut"}, new String[]{"1", "2"}, "g"));
        //System.out.println(TreeNode.latex("x_(3/x)"));
    }
}