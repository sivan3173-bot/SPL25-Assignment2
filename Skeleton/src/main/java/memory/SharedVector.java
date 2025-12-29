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
        this.readLock() ;
        double toReturn = vector[index] ;
       this.readUnlock();
        return toReturn ;
    }

    public int length() {
        // TODO: return vector length
        this.readLock() ;
        int toReturn = vector.length ;
        this.readUnlock();
        return toReturn ;
    }

    public VectorOrientation getOrientation() {
        // TODO: return vector orientation
        this.readLock() ;
        VectorOrientation toReturn = this.orientation ;
        this.readUnlock() ;
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
        this.writeLock();
        if (orientation == VectorOrientation.ROW_MAJOR) {
            orientation = VectorOrientation.COLUMN_MAJOR ;
        } else {
            orientation = VectorOrientation.ROW_MAJOR ;
        }
        this.writeUnlock();
    } 

    public void add(SharedVector other) {
        // TODO: add two vectors
        this.writeLock() ;
        other.readLock();
        for(int i = 0 ; i < vector.length ; i++ ) {
            this.vector[i] = this.vector[i] + other.vector[i] ;
        }
        this.writeUnlock() ;
        other.readUnlock() ;
    }

    public void negate() {
        // TODO: negate vector
        this.writeLock() ;
        for( int i = 0 ; i < vector.length ; i++) {
            vector[i] = (-1) * vector[i] ;
        }
        this.writeUnlock() ;
    }

    public double dot(SharedVector other) {
        // TODO: compute dot product (row · column)
        this.readLock() ;
        other.readLock() ;
        double product = 0 ;

        for( int i = 0 ; i < vector.length ; i++ ) {
            product = product + vector[i]*other.vector[i] ;
        }
        return product ;
    }

    public void vecMatMul(SharedMatrix matrix) {
        // TODO: compute row-vector × matrix
        this.readLock() ;
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
        this.readUnlock() ;
        this.writeLock() ;
        this.vector = result ;
        this.orientation = VectorOrientation.ROW_MAJOR ;
        this.writeUnlock() ;
    }
}
