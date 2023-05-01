package FormObserver;

import tungpham.com.dev.HandleStart;
import tungpham.com.dev.HandleEnd;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import tungpham.com.dev.HandleConditionBreak;
import tungpham.com.dev.HandleEventLoop;
import tungpham.com.dev.HandleSkipTask;

@Getter
public class ObserverAsynTask {

    private Map<String, FormatObserver> mapObserverAsynTask = new HashMap<>();

    int totalTask = Runtime.getRuntime().availableProcessors() * 2;
    int timeWait = 400;
    private ExecutorService executorSingle = Executors.newSingleThreadExecutor();
    private Map<String, Object> mapSubTranform = new HashMap<>();
    boolean check;

    static class Helper {

        final static ObserverAsynTask instance = new ObserverAsynTask();
    }

    public static ObserverAsynTask getInstance() {
        return Helper.instance;
    }

    private ObserverAsynTask() {
    }

    HandleSkipTask HSkipTask = (formObserver) -> {
        return formObserver;
    };

    HandleStart HS = (formObserver) -> {
        return formObserver;
    };

    HandleEnd HE = (formObserver, u) -> {
        return formObserver;
    };

    HandleConditionBreak conditionBreakLoop = () -> {
        return false;
    };

    HandleEventLoop handleEventLoop = (form) -> {
        return null; //To change body of generated lambdas, choose Tools | Templates.
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

    public ObserverAsynTask setHandleSkipTask(HandleSkipTask hst) {
        this.HSkipTask = hst;
        return this;
    }

    public ObserverAsynTask setConditionBreakLoop(HandleConditionBreak conditonBreak) {
        this.conditionBreakLoop = conditonBreak;
        return this;
    }

    public ObserverAsynTask handleEventLoop(HandleEventLoop handleEventLoop) {
        this.handleEventLoop = handleEventLoop;
        return this;
    }

    public ObserverAsynTask setTimeAwait(int time) {
        this.timeWait = time;
        return this;
    }

    public ObserverAsynTask addSubTransform(String key, Object obj) {
        mapSubTranform.put(key, obj);
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
        this.handleEventLoop = handleEventLoop;
        eventLoop();
        return this;
    }

    public ObserverAsynTask eventLoop() {
        for (;;) {

            int total = handleTotalTask();
            for (int i = 0; i < total; i++) {
                futRunEventLoop();
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

    private CompletableFuture<FormatObserver> futRunEventLoop() {
        return CompletableFuture.supplyAsync(this::getFormObserver, executorSingle)
                .thenApplyAsync(this::handleProcesStartTask, executorSingle)
                .thenApplyAsync(t -> handleTaskWorking(t, handleEventLoop))
                .whenCompleteAsync(this::handleProcesEndTask, executorSingle);
    }

    private FormatObserver handleProcesStartTask(FormatObserver fo) {
        if (fo.isTaskSkip()) {
            return fo;
        }
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
        HSkipTask.handle(formObs);
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

    /*
    *** beta test eventLoopGroup
     */
    ExecutorService excutorSingleStart = Executors.newSingleThreadScheduledExecutor();
    ExecutorService excutorSingleEnd = Executors.newSingleThreadScheduledExecutor();

    public ObserverAsynTask eventLoopGroup() {
        handleEVG(10);
        return this;
    }

    public ObserverAsynTask eventLoopGroup(int timeWaite) {
        handleEVG(timeWaite);
        return this;
    }

    void handleEVG(int time) {
        setTimeAwait(time);
        int totalThread = Runtime.getRuntime().availableProcessors();
        for (int i = 0; i < totalThread; i++) {
            new Thread(() -> {
                eventLoopInGroup();
            }).start();
        }
    }

    ObserverAsynTask eventLoopInGroup() {

        for (;;) {

            int total = handleTotalTask();
            for (int i = 0; i < total; i++) {
                CompletableFuture.supplyAsync(this::getFormObserver, executorSingle)
                        .thenApplyAsync(this::handleProcesStartTask, executorSingle)
                        .thenApplyAsync(t -> handleTaskWorking(t, handleEventLoop))
                        .whenCompleteAsync(this::handleProcesEndTask, excutorSingleEnd);
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
}
