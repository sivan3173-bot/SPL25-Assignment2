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
        double toReturn ;
        this.readLock() ;
        try {
            toReturn = vector[index] ;
            return toReturn ;
        } finally {
            this.readUnlock() ;
        }
    }

    public int length() {
        // TODO: return vector length
        this.readLock() ;
        int toReturn ;
        try {
            toReturn = vector.length ;
            return toReturn ;
        } finally{
            this.readUnlock() ;
        }
    }

    public VectorOrientation getOrientation() {
        // TODO: return vector orientation
        this.readLock() ;
        VectorOrientation toReturn ;
        try{
            toReturn = this.orientation ;
            return toReturn ;
        } finally {
            this.readUnlock() ;
        }
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
        this.writeLock() ;
        try {
             if (orientation == VectorOrientation.ROW_MAJOR) {
                orientation = VectorOrientation.COLUMN_MAJOR ;
            } else {
                orientation = VectorOrientation.ROW_MAJOR ;
            }
        } finally {
            this.writeUnlock() ;
        }
    } 

    public void add(SharedVector other) {
        // TODO: add two vectors
        if (this.vector.length != other.vector.length) {
            throw new IllegalArgumentException("Vector length mismatch");
        }

        this.writeLock() ;
        other.readLock() ;
        try {
            for(int i = 0 ; i < vector.length ; i++ ) {
                this.vector[i] = this.vector[i] + other.vector[i] ;
            }
        } finally {
            other.readUnlock() ;
            this.writeUnlock() ;
        }
    }

    public void negate() {
        // TODO: negate vector
        this.writeLock() ; 
        try {
            for(int i = 0 ; i < vector.length ; i++ ){
                vector[i] = (-1) * vector[i] ;
            }
        } finally {
            this.writeUnlock() ;
        }
    }

    public double dot(SharedVector other) {
        // TODO: compute dot product (row · column)
        if (this.vector.length != other.vector.length) {
            throw new IllegalArgumentException("Vector length mismatch");
        }
        
        this.readLock() ;
        other.readLock() ;
        
        try {
            double product = 0 ;
            for( int i = 0 ; i < vector.length ; i++ ) {
                product = product + vector[i]*other.vector[i] ;
            }
            return product ;
        } finally {    
            other.readUnlock() ;
            this.readUnlock() ;
        }  
    }

    public void vecMatMul(SharedMatrix matrix) {
        // TODO: compute row-vector × matrix
        double[] result ;
        this.readLock() ;
        try {
            double[] original = this.vector ;
            int cols = matrix.length() ;
            result = new double[cols] ;
            for( int j = 0 ; j < cols ; j++){
                SharedVector column = matrix.get(j) ;
                column.readLock() ;
                try {
                    double sum = 0 ;
                    for (int i = 0 ; i< original.length ; i++) {
                        sum = sum + original[i] * column.vector[i] ;
                    }
                    result[j] = sum ;
                } finally {
                    column.readUnlock() ;
                }  
            }
        } finally {
            this.readUnlock() ;
        }          
        
        this.writeLock() ;
        try {
            this.vector = result ;
            this.orientation = VectorOrientation.ROW_MAJOR ;
        } finally {
            this.writeUnlock() ;
        }
    }
}
