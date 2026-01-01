import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import memory.SharedMatrix;
import memory.SharedVector;
import memory.VectorOrientation;

public class SharedVectorTest {

    @Test
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

        SharedVector v5 = new SharedVector(new double[]{1.0, 2.0}, VectorOrientation.ROW_MAJOR);
        SharedVector v6 = new SharedVector(new double[]{1.0, 2.0, 3.0}, VectorOrientation.ROW_MAJOR);

    
        assertThrows(Exception.class, () -> {   // test invalid addition dimensions
         v5.add(v6);
        }, "Should throw an exception when adding vectors of different lengths");

        double[] initialData = {5.0, -3.0};  // adding zero test
        double[] zeroData = {0.0, 0.0};
        SharedVector v7 = new SharedVector(initialData, VectorOrientation.ROW_MAJOR);
        SharedVector zeroVec = new SharedVector(zeroData, VectorOrientation.ROW_MAJOR);
        v7.add(zeroVec);
        
        for (int i = 0; i < initialData.length; i++) {
            assertEquals(initialData[i], v7.get(i), 0.00001, "Value at index " + i + " should remain the same");
   
        }
    }

    @Test
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

    @Test
public void testTranspose() {
    SharedVector v = new SharedVector(new double[]{1.0, 2.0}, VectorOrientation.ROW_MAJOR);
    
     v.transpose();
    assertEquals(VectorOrientation.COLUMN_MAJOR, v.getOrientation(), "Transpose failed to change to COLUMN_MAJOR");
    v.transpose();
    assertEquals(VectorOrientation.ROW_MAJOR, v.getOrientation(), "Transpose failed to change back to ROW_MAJOR");
}

@Test
public void testDotProduct() {
    SharedVector v1 = new SharedVector(new double[]{1.0, 3.0, -5.0}, VectorOrientation.ROW_MAJOR);
    SharedVector v2 = new SharedVector(new double[]{4.0, -2.0, -1.0}, VectorOrientation.ROW_MAJOR);
    SharedVector zeroVec = new SharedVector(new double[] {0.0, 0.0, 0.0},VectorOrientation.ROW_MAJOR);
    
    double expected = 3.0;
    double actual = v1.dot(v2);
    
    assertEquals(expected, actual, 0.00001, "Dot product calculation is incorrect");


    SharedVector v3 = new SharedVector(new double[]{1.0}, VectorOrientation.ROW_MAJOR);
    SharedVector v4 = new SharedVector(new double[]{1.0, 2.0}, VectorOrientation.ROW_MAJOR);

    assertThrows(Exception.class, () -> {  //  test invalid dot dimensions
        v3.dot(v4);
    }, "Dot product should fail for mismatched dimensions");

    double multiplyBy0 = v1.dot(zeroVec); // test multiply by zero
    assertEquals(0.0, multiplyBy0, 0.00001, "Dot product with zero vector must be zero");
}


@Test
public void testVectorMatrixMultiplication() {
    
    SharedVector v = new SharedVector(new double[]{1.0, 2.0}, VectorOrientation.ROW_MAJOR);
        
    double [] col1 = new double[]{3.0, 5.0};
    double [] col2 = new double[]{4.0, 6.0}; 

    double [][] columns = {col1, col2};
    SharedMatrix matrix = new SharedMatrix(columns);

    double[] expected = {13.0, 16.0};
    v.vecMatMul(matrix);
    
    for (int i = 0; i < expected.length; i++) {
        assertEquals(expected[i], v.get(i), 0.00001, "Vector-Matrix multiplication failed at index " + i);
    }

    SharedVector v1 = new SharedVector(new double[]{1, 2, 3}, VectorOrientation.ROW_MAJOR);
    assertThrows(Exception.class, () -> {  // test invalid - vector length diffrent than row number in matrix
        v1.vecMatMul(matrix);
    }, "Vector length (3) must match matrix row count (2)");
}
    
}
