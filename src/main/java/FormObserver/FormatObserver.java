package FormObserver;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

@Getter
public class FormatObserver {

    String idTask;
    boolean taskStart, taskFree, taskSkip, taskWorking;
    int totalTaskFree, totalTask;
    FormatWebProxy formWebProxy = new FormatWebProxy();

    public FormatObserver setTotalTaskFree(int totalTaskFree) {
        this.totalTaskFree = totalTaskFree;
        return this;
    }

    public FormatObserver setTotalTask(int totalTask) {
        this.totalTask = totalTask;
        return this;
    }

    public FormatObserver setIdTask(String idTask) {
        this.idTask = idTask;
        return this;
    }

    public FormatObserver setTaskStart() {
        this.taskStart = true;
        this.taskFree = this.taskSkip = this.taskWorking = false;
        return this;
    }

    public FormatObserver setTaskFree() {
        this.taskFree = true;
        this.taskStart = this.taskSkip = this.taskWorking = false;
        return this;
    }

    public FormatObserver setTaskSkip() {
        this.taskSkip = true;
        this.taskFree = this.taskStart = this.taskWorking = false;
        return this;
    }

    public FormatObserver setTaskWorking() {
        this.taskWorking = true;
        this.taskFree = this.taskStart = this.taskSkip = false;
        return this;
    }

    public FormatObserver setFormWebProxy(FormatWebProxy formWebProxy) {
        this.formWebProxy = formWebProxy;
        return this;
    }

    @Override
    public String toString() {
        return new JSONObject().put("idTask", idTask)
                .put("taskStart", taskStart)
                .put("taskFree", taskFree)
                .put("taskSkip", taskSkip)
                .put("taskWorking", taskWorking)
                .put("totalTaskFree", totalTaskFree).put("totalTask", totalTask)
                .put("formWebProxy", formWebProxy.toString())
                .toString();
    }

}
