import ExpTree.TreeNode;
import com.sun.javafx.collections.ImmutableObservableList;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import sun.java2d.pipe.TextPipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by dande_000 on 4/12/2018.
 */
public class PropOptionBox extends VBox
{
	String[] vars = new String[0];
	String[] uncertainty = new String[0];
	public PropOptionBox(String postFix)
	{
		setDisable(true);
		updateEquation(postFix);
	}
	private int isInArr(String s, String[] arr)
	{
		for (int i= 0; i < arr.length; i++)
		{
			if(s.equals(arr[i]))
				return i;
		}
		return -1;
	}

	public void updateEquation(String eq)
	{
		System.out.println("from " + vars.length);
		String[] nVars;
		try
		{
			nVars= TreeNode.getVarsInString(eq);
		}
		catch (Exception e)
		{
			return;
		}
        Collection<Node> newChildren = new ArrayList<>();
		System.out.println("to " + nVars.length);
		String[] nUncertainty = new String[nVars.length];
		for (int i = 0; i < nVars.length; i++)
		{
			CheckBox isVar = new CheckBox();
			TextField uName = new TextField();

			uName.setId(""+i);
			uName.setMinSize(80, 25);
			uName.setMaxSize(80, 25);
			int index = isInArr(nVars[i], vars);

			if (index >= 0) //preserve info
			{
			    System.out.println("preserve");
				isVar.setText(vars[index]);
				uName.setText(uncertainty[index]);
                FlowPane flow = (FlowPane)this.getChildren().get(index);
                CheckBox c = (CheckBox) flow.getChildren().get(0);
                isVar.setSelected(c.isSelected());
			}
			else //new variable
			{
                System.out.println("no preserve");
				isVar.setText(nVars[i]);
				uName.setText("u" + nVars[i]);
                isVar.setSelected(true);
			}

			nUncertainty[i] = uName.getText();
			uName.addEventHandler(KeyEvent.KEY_PRESSED, e ->
            {
				nUncertainty[Integer.parseInt(uName.getId())] = uName.getText();
			});
			isVar.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(ActionEvent event)
				{
					uName.setEditable(isVar.isSelected());
					uName.setDisable(!isVar.isSelected());
				}
			});

            isVar.fireEvent(new ActionEvent());
			FlowPane entry = new FlowPane();
			entry.getChildren().addAll(isVar, uName);
			newChildren.add(entry);
		}
		vars = nVars;
		uncertainty = nUncertainty;
        this.getChildren().clear();
        this.getChildren().addAll(newChildren);
	}

	String[] getVars()
	{
		if (this.isDisable())
			return new String[0];
		ArrayList<String> toRet = new ArrayList<>();
		for(int i = 0; i < vars.length; i++)
		{
			FlowPane flow = (FlowPane)this.getChildren().get(i);
            CheckBox c = (CheckBox) flow.getChildren().get(0);
			if (!c.isSelected())
				continue;
			toRet.add(((CheckBox)flow.getChildren().get(0)).getText());
		}
		String[] s= new String[toRet.size()];
		toRet.toArray(s);
		return s;
	}
	String[] getUncertainties()
	{

		if (this.isDisable())
			return new String[0];
		ArrayList<String> toRet = new ArrayList<>();
		for(int i = 0; i < vars.length; i++)
		{
			FlowPane flow = (FlowPane)this.getChildren().get(i);
            CheckBox c = (CheckBox) flow.getChildren().get(0);
            if (!c.isSelected())
                continue;
			toRet.add(((TextField)flow.getChildren().get(1)).getText());
		}
		String[] s= new String[toRet.size()];
		toRet.toArray(s);
		return s;
	}


}
