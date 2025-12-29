import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import memory.SharedVector;
import memory.VectorOrientation;

public class SharedVectorTest {

public void testVectorAddition() {

    SharedVector v1 = new SharedVector(new double[]{1.0, 2.0}, VectorOrientation.ROW_MAJOR);
    SharedVector v2 = new SharedVector(new double[]{3.0, 4.0}, VectorOrientation.ROW_MAJOR);
    v1.add(v2);
    assertEquals(4.0, v1.get(0), 0.00001);
    assertEquals(6.0, v1.get(1), 0.00001);

    SharedVector v3 = new SharedVector(new double[]{0.0, -5.0}, VectorOrientation.ROW_MAJOR);
    SharedVector v4 = new SharedVector(new double[]{10.0, 5.0}, VectorOrientation.ROW_MAJOR);
    v3.add(v4);
    assertEquals(10.0, v3.get(0), 0.00001);
    assertEquals(0.0, v3.get(1), 0.00001);
   
}

 public void testVectorNegation() {
    
        double[] data1 = {1.0, -2.0};
        SharedVector v1 = new SharedVector(data1, VectorOrientation.ROW_MAJOR);
        v1.negate();
        assertEquals(-1.0, v1.get(0), 0.00001, "negation failed at index" + 0);
        assertEquals(2.0, v1.get(1), 0.00001, "negation failed at index" + 1);
    
       double[] data2 = {0.0, 0.0};
        SharedVector v2 = new SharedVector(data2, VectorOrientation.ROW_MAJOR);
        v2.negate();
        assertEquals(0.0, v2.get(0), 0.00001, "negation failed at index" + 0);
        assertEquals(0.0, v2.get(1), 0.00001, "negation failed at index" + 1);
    

    }

    
}
