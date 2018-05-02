/**
 * Created by dande_000 on 2/17/2018.
 **/
import ExpTree.TreeNode;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.geometry.Insets;

import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;

public class Parser extends Application{
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
    public static void main(String[] args)
    {
        //System.out.println(TreeNode.propagateFancy("log (10, V / O)", new String[]{"V"}, new String[]{"U"}));
	    //System.out.println(TreeNode.propagateFancy("1/( (((4)/(E^2)) * ( ((1)/(I*C))-((4 * P^2)/(T^2)) ) )^(1/2) )", new String[]{"T"}, new String[]{"U"}));
        //String s = TreeNode.propagate("1/( (((4)/(E^2)) * ( ((1)/(I*C))-((4 * (pi)^2)/(T^2)) ) )^(1/2) )", new String[]{"T"}, new String[]{"U"})
        ///*.replace("I","L").replace("E", "R").replace("pi", "\\pi").replace("deltaT", "\\delta T")*/;
        //String s2 = TreeNode.substituteSolveFancy(s, new String[]{"I", "E", "T", "U", "C", "pi"}, new double[]{0.0036, 2, 0.000125, 0.00002, 0.000000004, Math.PI});
        //System.out.println(s2);
        //System.out.println(TreeNode.propagateFancy("C / T)", new String[]{"T"}, new String[]{"U"})
        //.replace("U", "\\delta T").replace("C", "2 \\pi"));
        /*String eq = "x * x * x";
        long start;
        start = System.currentTimeMillis();
        System.out.println(TreeNode.solve(eq));
        System.out.println(System.currentTimeMillis()-start);
*/
        //System.out.println(TreeNode.stepSimplify(TreeNode.softSolve("(x)_(x * x * x * x * x)"), 15000));
        //System.out.println(TreeNode.latex("(0 * (x + y)) / (x)")); //FIXME
        //FIXME TEST GetVars
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
	    String defaultEquation = "x + y";
	    CheckBox beautify = new CheckBox();
		CheckBox propagate = new CheckBox();
		PropOptionBox propOptions = new PropOptionBox(defaultEquation);


	    TextField latexCopy = new TextField();
        WebView latexView = new WebView();
        updateLatex(latexView, defaultEquation);
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(5, 10, 5, 10));
		//FIXME USE SPLITPANE?
        FlowPane top = new FlowPane();
        top.getChildren().add(new Label("Equation:"));

        TextField equation = new TextField();
        equation.setMinSize(100, 25);
        equation.setText(defaultEquation);
        equation.addEventHandler(KeyEvent.KEY_RELEASED, e ->
        {
	        char pressed = (e.getText() + " ").charAt(0);
	        System.out.println("pressed: "+ pressed);
	        if (e.getCode() == KeyCode.ENTER) {
                updateLoading(latexView);
                Thread myThread = new Thread(() -> {
                    try {
                        String s = "Something Went Wrong";
                        if (!propagate.isSelected()) {
                            s = TreeNode.latex(TreeNode.solve(equation.getText()).toString());
                        } else {
				        /*System.out.print("V: ");
                        for (String t : propOptions.getVars())
                            System.out.print(t + ", ");
				        System.out.print("\nU: ");
                        for (String t : propOptions.getUncertainties())
                            System.out.print(t + ", ");
                        System.out.println();
                        */
                            s = TreeNode.propagateFancy(equation.getText(), propOptions.getVars(), propOptions.getUncertainties());
                            System.out.println("No really");
                            System.out.println(s);
                        }
                        if (beautify.isSelected()) {
                            s = s.replace(".0", "");
                        }
                        updateLatex(latexView, s);
                        latexCopy.setText(s);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        updateError(latexView, "Unable to read and/or display Equation!");
                    }
                });
                myThread.setDaemon(true);
                myThread.start();
            }
	        else
                if (e.getCode() == KeyCode.BACK_SPACE || e.getCode() == KeyCode.SPACE || ((pressed >= 'a' && pressed <= 'z') || (pressed >= 'A' && pressed <= 'Z'))) {
                    propOptions.updateEquation(equation.getText());
                }
        });
	    equation.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ENTER, false, false, false, false));
        top.getChildren().add(equation);

        pane.setTop(top);
        pane.setCenter(latexView);
        BorderPane.setMargin(latexView, new Insets(10));

	    FlowPane bot = new FlowPane();
	    bot.getChildren().add(new Label("Latex version"));

	    latexCopy.setMinSize(100, 25);
	    latexCopy.setEditable(false);
	    bot.getChildren().add(latexCopy);

	    Button copyButton = new Button();
	    copyButton.setOnAction(new EventHandler<ActionEvent>()
	    {
		    @Override
		    public void handle(ActionEvent event)
		    {
			    StringSelection stringSelection = new StringSelection(latexCopy.getText());
			    java.awt.datatransfer.Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
			    clpbrd.setContents(stringSelection, null);
		    }
	    });
	    copyButton.setText("Copy Latex");
	    bot.getChildren().add(copyButton);
	    pane.setBottom(bot);

	    VBox left = new VBox();
        BorderPane.setMargin(top, new Insets(10));

	    beautify.setText("Beautify");
	    propagate.setText("Propagate Uncertainty");

	    propagate.setOnAction(new EventHandler<ActionEvent>()
	    {
		    @Override
		    public void handle(ActionEvent event)
		    {
			    propOptions.setDisable(!propagate.isSelected());
		    }
	    });
	    propOptions.setMaxWidth(100);

	    left.getChildren().addAll(beautify, propagate, propOptions);
        left.setSpacing(6);
	    pane.setLeft(left);

        Scene s = new Scene(pane, 400, 400);
        primaryStage.setScene(s);
        primaryStage.setTitle("Equation Parser with Latex");
        primaryStage.show();

        //String latexString = TreeNode.propagateFancy("m * x + b", new String[]{"m", "x", "b"}, new String[]{"apples", "z", TreeNode.propagate("((ba+bb)/2)", new String[]{"ba","bb"}, new String[]{"b", "a"})});
    }

    public void updateLatex(WebView view, String latexString)
    {
        Platform.runLater(() -> {
            WebEngine latexEngine = view.getEngine();
            latexEngine.loadContent("\\[" + latexString + "\\]\n" +
                    "<script type=\"text/javascript\" src=\"http://www.hostmath.com/Math/MathJax.js?config=OK\"></script>");
        });
    }

	public void updateError(WebView view, String errorString)
	{
        Platform.runLater(() -> {
            WebEngine latexEngine = view.getEngine();
            latexEngine.loadContent("<marquee behavior=\"alternate\">\n" +
                    errorString + "\n" +
                    "</marquee>");
        });
	}

	public void updateLoading(WebView view)
    {
        Platform.runLater(() -> {
            WebEngine latexEngine = view.getEngine();
            latexEngine.loadContent("<marquee behavior=\"alternate\">\n" +
                    "loading..." + "\n" +
                    "</marquee>");
        });
    }
}