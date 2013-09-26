package at.irian.ankorsamples.todosample.servlet;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.servlet.websocket.AnkorEndpoint;
import at.irian.ankorsamples.todosample.domain.task.TaskRepository;
import at.irian.ankorsamples.todosample.viewmodel.ModelRoot;

public class TodoEndpoint extends AnkorEndpoint {
    @Override
    protected Object getModelRoot(Ref rootRef) {
        return new ModelRoot(rootRef, new TaskRepository());
    }
}
