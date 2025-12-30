package spl.lae;

import parser.*;
import memory.*;
import scheduling.*;

import java.util.ArrayList;
import java.util.List;

public class LinearAlgebraEngine {

    private SharedMatrix leftMatrix = new SharedMatrix();
    private SharedMatrix rightMatrix = new SharedMatrix();
    private TiredExecutor executor;

    public LinearAlgebraEngine(int numThreads) {
        // TODO: create executor with given thread count
        this.executor = new TiredExecutor(numThreads);
    }


    public ComputationNode run(ComputationNode computationRoot) {
        // TODO: resolve computation tree step by step until final matrix is produced
        computationRoot.associativeNesting(); // preperation
        ComputationNode nextNode;
        while ((nextNode = computationRoot.findResolvable()) != null) { // still have operator with matrix children to calculate
            loadAndCompute(nextNode);
        } 
        return computationRoot; // final result
    }



    public void loadAndCompute(ComputationNode node) {
        // TODO: load operand matrices
        // TODO: create compute tasks & submit tasks to executor
        List<ComputationNode> children = node.getChildren(); 
        leftMatrix.loadRowMajor(children.get(0).getMatrix()); // M1
    
        if (children.size() > 1) {
            rightMatrix.loadRowMajor(children.get(1).getMatrix()); // M2    
        }

        List<Runnable> tasks = null;
        ComputationNodeType type = node.getNodeType(); 

        if (type == ComputationNodeType.ADD) { // operator check
            tasks = createAddTasks(); 
        } else if (type == ComputationNodeType.MULTIPLY) {
            tasks = createMultiplyTasks();
        } else if (type == ComputationNodeType.TRANSPOSE) {
            tasks = createTransposeTasks(); 
        } else if (type == ComputationNodeType.NEGATE) {
            tasks = createNegateTasks();
        }

        if (tasks != null) {
            executor.submitAll(tasks); // sends tasks
        }
        double[][] result = leftMatrix.readRowMajor(); 
        node.resolve(result); // this node is now ready after solving

    }

    public List<Runnable> createAddTasks() {
        // TODO: return tasks that perform row-wise addition
        List<Runnable> tasks = new ArrayList<>();
        int numRows = leftMatrix.length(); 

        for (int i = 0; i < numRows; i++) {
            final int rowIndex = i;   
            Runnable task = () -> {
                SharedVector v1 = leftMatrix.get(rowIndex);
                SharedVector v2 = rightMatrix.get(rowIndex);
                v1.add(v2); 
            };
            tasks.add(task);
        }
        return tasks;
    }

    public List<Runnable> createMultiplyTasks() {
        // TODO: return tasks that perform row × matrix multiplication
        List<Runnable> tasks = new ArrayList<>();
        int numRowsM1 = leftMatrix.length(); // M1 

        for (int i = 0; i < numRowsM1; i++) {
        final int rowIndex = i;
            tasks.add(() -> {
                SharedVector row = leftMatrix.get(rowIndex);
                row.writeLock(); 
                try {
                    row.vecMatMul(rightMatrix);
                } finally {
                    row.writeUnlock();
                }
            });
        }
        return tasks;    
    }

    public List<Runnable> createNegateTasks() {
        // TODO: return tasks that negate rows
        List<Runnable> tasks = new ArrayList<>();
        int numRows = leftMatrix.length();

        for (int i = 0; i < numRows; i++) {
            final int rowIndex = i;
            Runnable task = () -> {
                SharedVector v = leftMatrix.get(rowIndex);
                v.negate();
            };
            tasks.add(task);
        }
        return tasks;
    }

    public List<Runnable> createTransposeTasks() {  
        // TODO: return tasks that transpose rows
        List<Runnable> tasks = new ArrayList<>();
        int numRows = leftMatrix.length();

        for (int i = 0; i < numRows; i++) {
            final int rowIndex = i;
            tasks.add(() -> {
                leftMatrix.get(rowIndex).transpose();
            });
        }
        return tasks;
    }

    public String getWorkerReport() {
        // TODO: return summary of worker activity
        return executor.getWorkerReport();
    }
}



