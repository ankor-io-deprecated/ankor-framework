package at.irian.ankorsamples.todo.servlet;

import at.irian.ankor.application.Application;
import at.irian.ankor.application.SimpleSingleRootApplication;
import at.irian.ankor.base.BeanResolver;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.servlet.polling.AnkorServletContextListener;
import at.irian.ankorsamples.todosample.domain.task.Task;
import at.irian.ankorsamples.todosample.domain.task.TaskRepository;
import at.irian.ankorsamples.todosample.viewmodel.ModelRoot;

import java.util.Collection;
import java.util.Collections;

/**
 * @author Thomas Spiegl
 */
public class TodoSampleServletContextListener extends AnkorServletContextListener {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalsSampleServletContextListener.class);

    @Override
    protected String getName() {
        return "todo-sample-servlet-server";
    }

    @Override
    protected BeanResolver getBeanResolver() {
        return new BeanResolver() {
            @Override
            public Object resolveByName(String beanName) {
                return null;
            }

            @Override
            public Collection<String> getKnownBeanNames() {
                return Collections.emptyList();
            }
        };
    }

    @Override
    protected Application getApplication() {
        return new SimpleSingleRootApplication("Todo", "root") {
            @Override
            public Object createRoot(Ref rootRef) {
                TaskRepository taskRepository = new TaskRepository();
                taskRepository.saveTask(new Task("Test task 1"));
                taskRepository.saveTask(new Task("Test task 2"));
                return new ModelRoot(rootRef, taskRepository);
            }
        };
    }
}
