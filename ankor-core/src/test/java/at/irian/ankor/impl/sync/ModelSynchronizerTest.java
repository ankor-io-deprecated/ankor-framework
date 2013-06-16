package at.irian.ankor.impl.sync;

import at.irian.ankor.api.application.Application;
import at.irian.ankor.impl.application.DefaultApplication;
import at.irian.ankor.impl.ref.ModelPropertyRef;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Manfred Geiler
 */
public class ModelSynchronizerTest {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelSynchronizerTest.class);

    private Application application;

    @Before
    public void setUp() throws Exception {
        application = new DefaultApplication();
    }

    @Test
    public void test1() {

        ModelSynchronizer modelSynchronizer = new ModelSynchronizer(application);

        TestModel testModel = new TestModel("pre");

        ModelPropertyRef ref = new ModelPropertyRef("root.name");
        ModelChange change = new ModelChange(ref, "post");
        modelSynchronizer.apply(testModel, change);

        Assert.assertEquals("name", "post", testModel.getName());
    }




    public static class TestModel {
        private String name;

        public TestModel(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
