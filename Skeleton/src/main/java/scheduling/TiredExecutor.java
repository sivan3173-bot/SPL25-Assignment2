package scheduling;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TiredExecutor {

    private final TiredThread[] workers;
    private final PriorityBlockingQueue<TiredThread> idleMinHeap = new PriorityBlockingQueue<>();
    private final AtomicInteger inFlight = new AtomicInteger(0);

    public TiredExecutor(int numThreads) {
        // TODO
        workers = new TiredThread[numThreads] ;
        for (int i = 0 ; i < numThreads ; i++) {
            double fatigueFactor = 0.5 + Math.random() ;
            TiredThread workTiredThread = new TiredThread(i, fatigueFactor) ;
            workers[i] = workTiredThread ;
            workers[i].start() ;
            idleMinHeap.add(workTiredThread) ;
        }
    }

    public void submit(Runnable task) {
        // TODO
        try {
            TiredThread worker = idleMinHeap.take() ;
            inFlight.incrementAndGet() ;
            Runnable r = ()->{try{
                                task.run();
                              } finally {
                                inFlight.decrementAndGet() ; 
                                idleMinHeap.put(worker) ;
                              } 
                            } ;
            worker.newTask(r) ;
        } catch (InterruptedException e ) {
            return ;
        }
    }

    public void submitAll(Iterable<Runnable> tasks) {
        // TODO: submit tasks one by one and wait until all finish
        for (Runnable runnable : tasks) {
            boolean done = false ; 
            while(!done){
                try {
                    submit(runnable) ; //try assigning to a free worker
                    done = true ;
                } catch (IllegalStateException e) {
                    try { 
                        Thread.sleep(1) ;
                    } catch (InterruptedException ignored) {}
                        //sleep might throw exception
                }
            }

        }
        while (inFlight.get() > 0) {
            try {
                Thread.sleep(1) ; // gives time to finish working
            } catch (InterruptedException igneored) {
                //continue waiting.
            }
        }    
    }

    public void shutdown() throws InterruptedException {
        // TODO
        while( inFlight.get() > 0 ) {
            Thread.sleep(100) ;
        }
        for (TiredThread worker : workers) {
            worker.shutdown() ;
        }
        for (TiredThread worker : workers) {
            worker.join(); ;
        }
    }

    public synchronized String getWorkerReport() {
        // TODO: return readable statistics for each worker

        StringBuilder sb = new StringBuilder() ;

        for ( TiredThread worker : workers) {
            sb.append("Worker ").append(worker.getId()).append(": ") ;
            sb.append("fatigue=").append(worker.getFatigue()).append(", ") ;
            sb.append("timeUsed=").append(worker.getTimeUsed()).append(", ") ;
            sb.append("timeIdle=").append(worker.getTimeIdle()).append(", ") ;
            sb.append("busy=").append(worker.isBusy()) ;
            sb.append("\n") ;
        }
        return sb.toString();
    }
}
