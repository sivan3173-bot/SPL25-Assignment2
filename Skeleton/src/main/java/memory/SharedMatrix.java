package memory;

public class SharedMatrix {

    private volatile SharedVector[] vectors = {}; // underlying vectors

    public SharedMatrix() {
        // TODO: initialize empty matrix
        this.vectors = new SharedVector[0];

    }

    public SharedMatrix(double[][] matrix) {
        // TODO: construct matrix as row-major SharedVectors
       loadRowMajor(matrix);
    }

    public void loadRowMajor(double[][] matrix) {
        this.vectors = new SharedVector[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            this.vectors[i] = new SharedVector(matrix[i], VectorOrientation.ROW_MAJOR);
        }
    }

    public void loadColumnMajor(double[][] matrix) {
        // TODO: replace internal data with new column-major matrix
        if (matrix.length > 0) {

            int numRows = matrix.length;
            int numCols = matrix[0].length;
            this.vectors = new SharedVector[numCols];

            for (int j = 0; j < numCols; j++) { // colomns of original
                double[] colData = new double[numRows];
                for (int i = 0; i < numRows; i++) { // rows of original
                    colData[i] = matrix[i][j] ;
                }
                this.vectors[j] = new SharedVector(colData, VectorOrientation.COLUMN_MAJOR); // connect 
            }
        }
        
    }

    public double[][] readRowMajor() {
        // TODO: return matrix contents as a row-major double[][]
        double[][] toReturn = new double[vectors.length][0];

        for (int i = 0; i < vectors.length; i++) {
            SharedVector current = vectors[i];
            current.readLock(); 
            double[] doubleVector = new double[current.length()]; // create the double vector;
            for (int j = 0; j < current.length(); j++) {
                doubleVector[j] = current.get(j);
            }
            toReturn[i] = doubleVector;
            current.readUnlock(); 
        }

        return toReturn;
    }

    public SharedVector get(int index) {
        // TODO: return vector at index
        return vectors[index];
    }

    public int length() {
        // TODO: return number of stored vectors
        return vectors.length;
    }

    public VectorOrientation getOrientation() {
        // TODO: return orientation
       if (vectors == null || vectors.length == 0) return null;
       return vectors[0].getOrientation();

    }

    private void acquireAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: acquire read lock for each 
        for (SharedVector v : vecs) 
            v.readLock();
        
    }

    private void releaseAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: release read locks
        for (SharedVector v : vecs) 
            v.readUnlock();
        
    }

    private void acquireAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: acquire write lock for each vector
        for (SharedVector v : vecs) 
            v.writeLock();
        
       
    }

    private void releaseAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: release write locks
        for (SharedVector v : vecs) 
            v.writeUnlock();
        
    }
}
