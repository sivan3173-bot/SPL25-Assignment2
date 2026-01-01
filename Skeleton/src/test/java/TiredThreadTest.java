import scheduling.TiredThread;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicInteger;

public class TiredThreadTest {


    @Test
    public void testNewTaskAndRun() throws InterruptedException {

        TiredThread worker = new TiredThread(1, 1.0);
        worker.start();
        AtomicInteger counter = new AtomicInteger(0);
        Runnable task = () -> counter.incrementAndGet();
        worker.newTask(task);
        Thread.sleep(100); 

        assertEquals(1, counter.get(), "the task was supposed to be completed and to raise counter");
        worker.shutdown();
        worker.join(500);
    }

    @Test
    public void testFatigueCalculation() throws InterruptedException { // check get fatigue
        double factor = 2.5;
        TiredThread worker = new TiredThread(1, factor);
        worker.start();

        worker.newTask(() -> {
            try { Thread.sleep(50); } catch (InterruptedException e) {}
        });
        
        Thread.sleep(100);
        long timeUsed = worker.getTimeUsed();
        assertTrue(timeUsed > 0);
        assertEquals(timeUsed * factor, worker.getFatigue(), 0.001, "fatigue calculation must be time * factor");

        worker.shutdown();
        worker.join(500);
    }

    @Test
    public void testIsBusyAndException() throws InterruptedException {

        TiredThread worker = new TiredThread(2, 1.0);
        worker.start();
        worker.newTask(() -> {
            try { Thread.sleep(200); } catch (InterruptedException e) {}
        });

        assertTrue(worker.isBusy(), "the worker must be in a busy state when running a task");
        assertThrows(IllegalStateException.class, () -> { // giving task while is busy check
            worker.newTask(() -> {});
        }, "an error must be thrown when the worker is busy");

        worker.shutdown();
        worker.join(500);
    }

   
    @Test
    public void testTimeUsedAccumulation() throws InterruptedException {

        TiredThread worker = new TiredThread(3, 1.0);
        worker.start();

        Runnable shortTask = () -> {
             try { Thread.sleep(30); 
             } catch (InterruptedException e) {} };

        worker.newTask(shortTask);
        Thread.sleep(100);
        long firstTaskTime = worker.getTimeUsed();

        worker.newTask(shortTask);
        Thread.sleep(100);
        
        assertTrue(worker.getTimeUsed() > firstTaskTime, "total working time must be accumulated");

        worker.shutdown();
        worker.join(500);
    }

    @Test
    public void testIdleTimeMeasurement() throws InterruptedException {

        TiredThread worker = new TiredThread(4, 1.0);
        worker.start();
        Thread.sleep(100);
        worker.newTask(() -> {}); 
        Thread.sleep(50);

        assertTrue(worker.getTimeIdle() > 0, "idle time must be recorded");

        worker.shutdown();
        worker.join(500);
    }

   
    @Test
    public void testCompareTo() throws InterruptedException {

        TiredThread freshWorker = new TiredThread(1, 1.0);
        TiredThread tiredWorker = new TiredThread(2, 10.0); // high fatigue factor
        freshWorker.start();
        tiredWorker.start();

        Runnable work = () -> { try { Thread.sleep(50); } catch (InterruptedException e) {} };
        freshWorker.newTask(work);
        tiredWorker.newTask(work);
        Thread.sleep(150);

        assertTrue(freshWorker.compareTo(tiredWorker) < 0, "the fresh worker should be smaller than the tired");
        assertTrue(tiredWorker.compareTo(freshWorker) > 0, "the tired worker should be bigger than the fresh one");

        freshWorker.shutdown();
        tiredWorker.shutdown();
        freshWorker.join(500);
        tiredWorker.join(500);
    }

   
    @Test
    public void testShutdownProcess() throws InterruptedException { // including poisonPill check

        TiredThread worker = new TiredThread(5, 1.0);
        worker.start();
        
        worker.shutdown();
        worker.join(1000); 
        assertFalse(worker.isAlive(), "the thread should be dead after shutdown");
    }



    @Test
    public void testInterrupt() throws InterruptedException {

        TiredThread worker = new TiredThread(6, 1.0);
        worker.start();
        worker.interrupt(); // interrupt while waiting
        
        worker.join(500);
        assertFalse(worker.isAlive(), "the thread should be dead when gets interrupt while waiting");
    }


}