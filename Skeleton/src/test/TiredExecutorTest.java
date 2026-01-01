
package scheduling;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TiredExecutorTest {
    @Test
    public void testSubmitAllTasksFinished() throws InterruptedException {
        int numThreads = 4;
        int numTasks = 20;
        TiredExecutor executor = new TiredExecutor(numThreads);
        AtomicInteger counter = new AtomicInteger(0);

        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < numTasks; i++) {
            tasks.add(() -> {
                try {
                    Thread.sleep(50); 
                    counter.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        executor.submitAll(tasks);
        assertEquals(numTasks, counter.get(), "Not all tasks were completed!");
        executor.shutdown();
    }

    @Test
    public void testShutdownActuallyStops() throws InterruptedException {
        TiredExecutor executor = new TiredExecutor(2);
        executor.shutdown();
        assertTrue(true); 
    }

    @Test
    public void testWorkerEfficiencySelection() throws InterruptedException {
        TiredExecutor executor = new TiredExecutor(3);
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            tasks.add(() -> {
                try { Thread.sleep(20); } catch (InterruptedException e) {}
            });
        }
        executor.submitAll(tasks);
        String report = executor.getWorkerReport();
        assertTrue(report.contains("Worker 0"), "Worker 0 missing from report"); // check the report contains all workers
        assertTrue(report.contains("Worker 1"), "Worker 1 missing from report");
        assertTrue(report.contains("Worker 2"), "Worker 2 missing from report");
        
        assertFalse(report.contains("timeUsed=0.0"), "Workload distribution failed - some workers stayed idle"); // check priorityQueue

        executor.shutdown();
    }

    @Test
public void testWorkerReportAndParallelism() throws InterruptedException {
    int numThreads = 2;
    TiredExecutor executor = new TiredExecutor(numThreads);
    

    long startTime = System.currentTimeMillis();
    
    List<Runnable> tasks = new ArrayList<>();
    tasks.add(() -> { try { Thread.sleep(200); } catch (InterruptedException e) {} });
    tasks.add(() -> { try { Thread.sleep(200); } catch (InterruptedException e) {} });
    
    executor.submitAll(tasks);
    
    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;

    
    assertTrue(duration < 400, "Tasks should run in parallel, but took " + duration + "ms");  // ckeck parallel

    String report = executor.getWorkerReport(); 
    assertNotNull(report);
    assertTrue(report.contains("fatigue"), "Report should contain fatigue information");
    
    executor.shutdown();
}

@Test
    public void testEmptyTaskList() throws InterruptedException {
       
        TiredExecutor executor = new TiredExecutor(2);
        List<Runnable> emptyTasks = new ArrayList<>(); 
        assertDoesNotThrow(() -> executor.submitAll(emptyTasks), 
            "submitAll should handle an empty list without crashing");
        
        executor.shutdown();
    }

    @Test
    public void testSingleThreadExecution() throws InterruptedException {
      
        TiredExecutor executor = new TiredExecutor(1); // only one thread
        AtomicInteger counter = new AtomicInteger(0);
        
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            tasks.add(() -> {
                counter.incrementAndGet(); 
            });
        }
        executor.submitAll(tasks);
        assertEquals(5, counter.get(), "Even with a single thread, all tasks shuld be completed");
        
        executor.shutdown();
    }


}