package FormObserver;

import tungpham.com.dev.HandleStart;
import tungpham.com.dev.HandleEnd;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.Getter;
import tungpham.com.dev.HandleConditionBreak;
import tungpham.com.dev.HandleEventLoop;

@Getter
public class ObserverAsynTask {

    private Map<String, FormatObserver> mapObserverAsynTask = new HashMap<>();

    int totalTask = Runtime.getRuntime().availableProcessors() * 2;
    int timeWait = 400;
    private ExecutorService executorSingle = Executors.newSingleThreadExecutor();

    static class Helper {

        final static ObserverAsynTask instance = new ObserverAsynTask();
    }

    public static ObserverAsynTask getInstance() {
        return Helper.instance;
    }

    private ObserverAsynTask() {
    }

    HandleStart HS = (formObserver) -> {
        return formObserver;
    };

    HandleEnd HE = (formObserver, u) -> {
        return formObserver;
    };

    HandleConditionBreak conditionBreakLoop = () -> {
        return false;
    };

    public ObserverAsynTask setMapObserverAsynTask(Map<String, FormatObserver> mapObserverAsynTask) {
        this.mapObserverAsynTask = mapObserverAsynTask;
        return this;
    }

    public ObserverAsynTask setHandleStart(HandleStart hs) {
        this.HS = hs;
        return this;
    }

    public ObserverAsynTask setHandleEnd(HandleEnd he) {
        this.HE = he;
        return this;
    }

    public ObserverAsynTask setConditionBreakLoop(HandleConditionBreak conditonBreak) {
        this.conditionBreakLoop = conditonBreak;
        return this;
    }

    public ObserverAsynTask setTimeAwait(int time) {
        this.timeWait = time;
        return this;
    }

    /*
    ***
     */
    public ObserverAsynTask createTask(int totalTask) {
        this.totalTask = totalTask;

        for (int i = 0; i < totalTask; i++) {
            String uid = UUID.randomUUID().toString();

            var formObserver = new FormatObserver();
            formObserver.setIdTask(uid).setTaskFree();

            mapObserverAsynTask.put(uid, formObserver);
        }

        return this;
    }

    public ObserverAsynTask eventLoop(HandleEventLoop handleEventLoop) {
        for (;;) {

            int total = handleTotalTask();
            for (int i = 0; i < total; i++) {
                CompletableFuture.supplyAsync(this::getFormObserver, executorSingle)
                        .thenApplyAsync(this::handleProcesStartTask, executorSingle)
                        .thenApplyAsync(t -> handleTaskWorking(t, handleEventLoop))
                        .whenCompleteAsync(this::handleProcesEndTask, executorSingle);
            }

            /////
            synchronized (this) {
                await();
                if (conditionBreakLoop.handle()) {
                    break;
                }
            }

        }
        return this;
    }

    private FormatObserver handleProcesStartTask(FormatObserver fo) {
        return HS.handle(fo);
    }

    private FormatObserver handleTaskWorking(FormatObserver fo, HandleEventLoop handleEventLoop) {
        if (fo.isTaskSkip()) {
            return fo;
        }

        fo.setTaskWorking();
        return handleEventLoop.handle(fo);
    }

    private void handleProcesEndTask(FormatObserver t, Throwable u) {
        t.setTaskFree();
        if (u != null) {
            u.printStackTrace();
        }
        HE.handle(t, u);
    }

    /*
    ***
     */
    private FormatObserver getFormObserver() {
        FormatObserver formObs = null;

        formObs = mapObserverAsynTask.entrySet().stream().filter((t) -> {
            var fo = t.getValue();
            if (fo.isTaskFree()) {
                return true;
            }
            return false;
        }).findFirst().get().getValue();

        formObs.setTaskStart();
//        System.out.println("<< getFormObserver " + formObs.toString());
        return formObs;
    }

    synchronized int handleTotalTask() {

        // all free = totalTask , anount for =0 ; with anount for !=0 totalTask = total Task Free
        if (checkAllTaskFree()) {
            return totalTask;
        }

        totalTask = (int) mapObserverAsynTask.entrySet().stream().filter((t) -> {
            var fo = t.getValue();

            if (fo.isTaskFree()) {
                return true;
            }
            return false;
        }).count();
        return totalTask;
    }

    public boolean checkAllTaskFree() {

        return mapObserverAsynTask.entrySet().stream().allMatch((t) -> {
            var fo = t.getValue();

            if (!fo.isTaskWorking()) {
                return true;
            }
            return false;
        });
    }

    void await() {
        try {
            wait(timeWait);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
