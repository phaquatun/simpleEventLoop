package dynamic.proxy;

import FormObserver.FormatObserver;
import FormObserver.FormatWebProxy;
import FormObserver.ObserverAsynTask;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import tungpham.com.dev.HandleConditionWebProxy;

@Getter
public class ScripsWebProxy {

    private ObserverAsynTask observerAsynTask = ObserverAsynTask.getInstance();
    private FormatObserver formatObserver;

    private List<String> listKeyApi = new ArrayList<>();
    private boolean firstGetProxy;

    int countKeyApi;

    HandleChangeProxy handleChangeProxy = (fo, allTaskPass) -> {
        return formatObserver.getFormWebProxy();
    };

    static class Helper {

        final static ScripsWebProxy inst = new ScripsWebProxy();
    }

    public static ScripsWebProxy getInstance() {
        return Helper.inst;
    }

    public ScripsWebProxy setListKeyApi(List<String> listKeyApi) {
        this.listKeyApi = listKeyApi;
        return this;
    }

    public ScripsWebProxy setFormatObserver(FormatObserver formatObserver) {
        this.formatObserver = formatObserver;
        return this;
    }

    public FormatObserver toFormObserver() {
        if (formatObserver.getFormWebProxy().isGetProxyErr()) {
            formatObserver.setTaskSkip();
        }
        return formatObserver;
    }

    public ScripsWebProxy handleChangeProxyNext(HandleChangeProxy handleChangeProxy) {
        if (allTaskFreeUseKey()) {
            this.handleChangeProxy = handleChangeProxy;
        }
        return this;
    }

    public ScripsWebProxy getProxy() {
        boolean nextGetProxy = allTaskFreeUseKey();
        System.out.println(">> check boolean nextGetProxy " + nextGetProxy + " check firstGetProxy " + firstGetProxy);

        if (firstGetProxy) {
            handleGetFirstProxy();
            firstGetProxy = false;
            return this;
        }
        if (nextGetProxy) {

            var fwp = handleChangeProxy.handle(formatObserver.getFormWebProxy().getKeyApi(), allTaskFreeUseKey());
            var map = observerAsynTask.getMapObserverAsynTask();
            map.forEach((t, u) -> {
                if (fwp.isGetProxyErr() & t.contains(fwp.getKeyApi())) {
                    u.setTaskSkip();
                } else if (u.getFormWebProxy().getKeyApi().contains(fwp.getKeyApi())) {
                    u.setFormWebProxy(fwp);
                }
            });
            observerAsynTask.setMapObserverAsynTask(map);
            System.out.println(">> check mapObserverAsynTask in getProxy " + observerAsynTask.getMapObserverAsynTask().toString());
        }
        return this;
    }

    /*
    ****
     */
    private void handleGetFirstProxy() {
        System.out.println(">> check list key api " + listKeyApi.toString());
        List<CompletableFuture<FormatWebProxy>> listFutFormWebProxy = listKeyApi.stream().map(this::futFormWebProxy).collect(Collectors.toList());
        CompletableFuture<Void> futAll = CompletableFuture.allOf(listFutFormWebProxy.toArray(new CompletableFuture[listFutFormWebProxy.size()]));

        CompletableFuture<List<FormatWebProxy>> futListFormWebProxy = futAll.thenApply((unuse) -> {
            return listFutFormWebProxy.stream().map((f) -> {
                return f.join();
            }).collect(Collectors.toList());
        });

        List<FormatWebProxy> listFwp = futListFormWebProxy.join();
        setFormWebProxyTask(listFwp);
    }

    private CompletableFuture<FormatWebProxy> futFormWebProxy(String keyApi) {
        return CompletableFuture.supplyAsync(() -> {
            return handleChangeProxy.handle(keyApi, allTaskFreeUseKey());
        });
    }

    void setFormWebProxyTask(List<FormatWebProxy> listFormatWebProxy) {
        var map = observerAsynTask.getMapObserverAsynTask();
        map.forEach((t, u) -> {
            if (countKeyApi == listKeyApi.size()) {
                countKeyApi = 0;
            }
            var formWebproxy = listFormatWebProxy.get(countKeyApi++);
            if (formWebproxy.isGetProxyErr()) {
                u.setTaskSkip();
            }
            u.setFormWebProxy(formWebproxy);
            map.replace(t, u);
        });
        observerAsynTask.setMapObserverAsynTask(map);

        System.out.println(">> check mapObserverAsynTask in setFormWebProxyTask " + observerAsynTask.getMapObserverAsynTask().toString());
    }

    /*
    ***
     */
    boolean allTaskFreeUseKey() {

        // first get proxy  
        firstGetProxy = observerAsynTask.getMapObserverAsynTask().entrySet().stream().allMatch(this::firstGetProxy);
        if (firstGetProxy) {
            return true;
        }

        // it's not time to change ip
        if (!formatObserver.getFormWebProxy().isChangeNextIp()) {
            return false;
        }

        // it's time to change ip next get proxy
        formatObserver.setTaskSkip();
        String keyApi = formatObserver.getFormWebProxy().getKeyApi();

        return observerAsynTask.getMapObserverAsynTask().entrySet().stream()
                .filter((t) -> isTaskUseKeyApi(t, keyApi))
                .allMatch(this::checkTaskUseKey);
    }

    boolean firstGetProxy(Entry<String, FormatObserver> t) {
        var keyApi = t.getValue().getFormWebProxy().getKeyApi();
        if (keyApi == null) {
            return true;
        }
        return false;
    }

    boolean isTaskUseKeyApi(Entry<String, FormatObserver> t, String keyApi) {
        return t.getValue().getFormWebProxy().getKeyApi().contains(keyApi);
    }

    boolean checkTaskUseKey(Entry<String, FormatObserver> t) {
        var fo = t.getValue();
        boolean allPass = !fo.isTaskWorking() & fo.getFormWebProxy().isChangeNextIp();

        return allPass;
    }

}
