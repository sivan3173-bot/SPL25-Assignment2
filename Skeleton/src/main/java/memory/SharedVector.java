package memory;

import java.util.concurrent.locks.ReadWriteLock;

public class SharedVector {

    private double[] vector;
    private VectorOrientation orientation;
    private ReadWriteLock lock = new java.util.concurrent.locks.ReentrantReadWriteLock();

    public SharedVector(double[] vector, VectorOrientation orientation) {
        // TODO: store vector data and its orientation
        this.vector = vector ;
        this.orientation = orientation ;
    }

    public double get(int index) {
        // TODO: return element at index (read-locked)
        lock.readLock().lock() ;
        double toReturn = vector[index] ;
        lock.readLock().unlock();
        return toReturn ;
    }

    public int length() {
        // TODO: return vector length
        lock.readLock().lock();
        int toReturn = vector.length ;
        lock.readLock().unlock();
        return toReturn ;
    }

    public VectorOrientation getOrientation() {
        // TODO: return vector orientation
        lock.readLock().lock();
        VectorOrientation toReturn = this.orientation ;
        lock.readLock().unlock();
        return toReturn ;
    }

    public void writeLock() {
        // TODO: acquire write lock
        lock.writeLock().lock();
    }

    public void writeUnlock() {
        // TODO: release write lock
        lock.writeLock().unlock();
    }

    public void readLock() {
        // TODO: acquire read lock
        lock.readLock().lock();
    }

    public void readUnlock() {
        // TODO: release read lock
        lock.readLock().unlock();
    }

    public void transpose() {
        // TODO: transpose vector
        if (orientation == VectorOrientation.ROW_MAJOR) {
            orientation = VectorOrientation.COLUMN_MAJOR ;
        } else {
            orientation = VectorOrientation.ROW_MAJOR ;
        }
    } // RAZ NOTE : need to make sure that each time this is used in main the vector is bprotected with writelock

    public void add(SharedVector other) {
        // TODO: add two vectors
        for(int i = 0 ; i < vector.length ; i++ ) {
            this.vector[i] = this.vector[i] + other.vector[i] ;
        }
    }

    public void negate() {
        // TODO: negate vector
        for( int i = 0 ; i < vector.length ; i++) {
            vector[i] = (-1) * vector[i] ;
        }
    }

    public double dot(SharedVector other) {
        // TODO: compute dot product (row · column)
        double product = 0 ;

        for( int i = 0 ; i < vector.length ; i++ ) {
            product = product + vector[i]*other.vector[i] ;
        }
        return product ;
    }

    public void vecMatMul(SharedMatrix matrix) {
        // TODO: compute row-vector × matrix
        double[] original = this.vector ;
        int cols = matrix.length() ;
        double[] result = new double[cols] ;
        for( int j = 0 ; j < cols ; j++){
           SharedVector column = matrix.get(j) ;
           double sum = 0 ;

           for (int i = 0 ; i< original.length ; i++) {
            sum = sum + original[i] * column.vector[i] ;
           }
           result[j] = sum ;
        }
        this.vector = result ;
        this.orientation = VectorOrientation.ROW_MAJOR ;
    }
}
