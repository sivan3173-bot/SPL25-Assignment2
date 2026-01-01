import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import memory.SharedMatrix;
import memory.SharedVector;
import memory.VectorOrientation;

public class SharedMatrixTest {

    @Test
    public void testConstructFromRowMajor() {
        double[][] data = {
            {1, 2},
            {3, 4}
        };

        SharedMatrix m = new SharedMatrix(data);

        assertEquals(2, m.length());
        assertEquals(VectorOrientation.ROW_MAJOR, m.getOrientation());

        double[][] out = m.readRowMajor();
        assertArrayEquals(new double[]{1,2}, out[0]);
        assertArrayEquals(new double[]{3,4}, out[1]);
    }

    @Test
    public void testLoadRowMajorReplacesContent() {
        SharedMatrix m = new SharedMatrix();
        m.loadRowMajor(new double[][]{{1,1}});
        m.loadRowMajor(new double[][]{{9,9},{8,8}});

        assertEquals(2, m.length());
        assertArrayEquals(new double[]{9,9}, m.readRowMajor()[0]);
    }

    @Test
    public void testLoadColumnMajorStoresColumnVectors() {
        double[][] data = {
            {1, 2},
            {3, 4}
        };

        SharedMatrix m = new SharedMatrix();
        m.loadColumnMajor(data);

        assertEquals(2, m.length());
        assertEquals(VectorOrientation.COLUMN_MAJOR, m.getOrientation());

        // readRowMajor should reconstruct row form
        double[][] out = m.readRowMajor();
        assertArrayEquals(new double[]{1,2}, out[0]);
        assertArrayEquals(new double[]{3,4}, out[1]);
    }

    @Test
    public void testGetReturnsUnderlyingSharedVector() {
        SharedMatrix m = new SharedMatrix(new double[][]{
            {5, 6},
            {7, 8}
        });

        SharedVector row = m.get(1);
        assertEquals(2, row.length());
        assertEquals(7.0, row.get(0));
        assertEquals(8.0, row.get(1));
    }

    @Test
    public void testReadRowMajorUsesLocksSafely() {
        SharedMatrix m = new SharedMatrix(new double[][]{
            {1, 2},
            {3, 4}
        });

        // Should not throw and should return a deep copy
        double[][] out = m.readRowMajor();

        out[0][0] = 99;   // modify copy
        assertEquals(1.0, m.get(0).get(0)); // original unchanged
    }

    @Test
    public void testOrientationNullForEmptyMatrix() {
        SharedMatrix m = new SharedMatrix();
        assertNull(m.getOrientation());
        assertEquals(0, m.length());
    }
}
