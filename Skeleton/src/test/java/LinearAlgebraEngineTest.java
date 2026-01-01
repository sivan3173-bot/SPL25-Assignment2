package spl.lae;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import parser.*; 
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List; 

public class LinearAlgebraEngineTest {

    @Test
    public void testSimpleAddition() {
        // הכנת נתונים - יצירת צמתים מסוג MATRIX
        ComputationNode m1 = new ComputationNode(new double[][]{{1, 2}, {3, 4}});
        ComputationNode m2 = new ComputationNode(new double[][]{{10, 20}, {30, 40}});
        
        // יצירת צומת חיבור (ADD) עם הילדים
        ComputationNode root = new ComputationNode(ComputationNodeType.ADD, Arrays.asList(m1, m2));

        LinearAlgebraEngine engine = new LinearAlgebraEngine(2);
        engine.run(root);

        // בדיקה שהשורש הפך למטריצה אחרי ה-resolve
        assertEquals(ComputationNodeType.MATRIX, root.getNodeType());
        double[][] result = root.getMatrix();
        assertEquals(11.0, result[0][0]);
        assertEquals(44.0, result[1][1]);
    }

    @Test
    public void testAssociativeNestingEdgeCase() {
        // מקרה קצה: חיבור של 3 מטריצות בבת אחת (A + B + C)
        // ה-LAE צריך להפוך את זה ל: (A + B) + C בעזרת associativeNesting
        
        ComputationNode a = new ComputationNode(new double[][]{{1}});
        ComputationNode b = new ComputationNode(new double[][]{{2}});
        ComputationNode c = new ComputationNode(new double[][]{{3}});
        
        // יצירת רשימה ניתנת לשינוי (ArrayList) כי associativeNesting קוראת ל-remove
        ArrayList<ComputationNode> children = new ArrayList<>(Arrays.asList(a, b, c));
        ComputationNode root = new ComputationNode(ComputationNodeType.ADD, children);

        LinearAlgebraEngine engine = new LinearAlgebraEngine(2);
        engine.run(root); // כאן יופעל ה-associativeNesting

        assertEquals(6.0, root.getMatrix()[0][0], "A + B + C should equal 6");
    }

    @Test
    public void testDeepTreeResolution() {
        // מקרה קצה: עץ עמוק שדורש מספר סבבים של findResolvable
        // חישוב: (1 + 2) * 3
        
        ComputationNode n1 = new ComputationNode(new double[][]{{1}});
        ComputationNode n2 = new ComputationNode(new double[][]{{2}});
        ComputationNode n3 = new ComputationNode(new double[][]{{3}});
        
        ComputationNode innerAdd = new ComputationNode(ComputationNodeType.ADD, Arrays.asList(n1, n2));
        ComputationNode root = new ComputationNode(ComputationNodeType.MULTIPLY, Arrays.asList(innerAdd, n3));

        LinearAlgebraEngine engine = new LinearAlgebraEngine(4);
        engine.run(root);

        // הסבר: קודם findResolvable ימצא את ה-ADD, יפתור אותו ל-3, 
        // ואז בסבב הבא יפתור את ה-MULTIPLY (3 * 3)
        assertEquals(9.0, root.getMatrix()[0][0], "(1+2)*3 should be 9");
    }

    @Test
    public void testTransposeAndNegate() {
        // בדיקת פעולות אונאריות (על מטריצה אחת)
        double[][] data = {{1, 2}, {3, 4}};
        ComputationNode m = new ComputationNode(data);
        
        // יצירת עץ: T(-M)
        ComputationNode negate = new ComputationNode(ComputationNodeType.NEGATE, Arrays.asList(m));
        ComputationNode root = new ComputationNode(ComputationNodeType.TRANSPOSE, Arrays.asList(negate));

        LinearAlgebraEngine engine = new LinearAlgebraEngine(2);
        engine.run(root);

        double[][] res = root.getMatrix();
        // צפוי: מטריצה משוקפת עם מינוסים
        // {{ -1, -3 }, { -2, -4 }}
        assertEquals(-1.0, res[0][0]);
        assertEquals(-3.0, res[0][1]);
        assertEquals(-2.0, res[1][0]);
        assertEquals(-4.0, res[1][1]);
    }
}