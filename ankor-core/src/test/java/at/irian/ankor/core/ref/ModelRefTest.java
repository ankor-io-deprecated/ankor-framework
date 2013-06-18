package at.irian.ankor.core.ref;

import at.irian.ankor.core.el.StandardELContext;
import at.irian.ankor.core.model.ModelHolder;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.el.ExpressionFactory;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class ModelRefTest {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelRefTest.class);

    private ModelHolder modelHolder;
    private RefFactory refFactory;

    @Before
    public void setup() {
        this.modelHolder = new ModelHolder(Object.class);
        this.refFactory = new RefFactory(ExpressionFactory.newInstance(),
                                         new StandardELContext(),
                                         null,
                                         null,
                                         modelHolder);
    }

    @Test
    public void test_root_path() throws Exception {
        RootRef ref = refFactory.rootRef();
        Assert.assertEquals("", ref.path());
    }

    @Test
    public void test_prop_path() throws Exception {
        ModelRef ref = refFactory.ref("test.person.name");
        Assert.assertEquals("test.person.name", ref.path());
    }

    @Test
    public void test_descendant() throws Exception {
        ModelRef ref = refFactory.ref("test.person.name");
        Assert.assertTrue(ref.isDescendantOf(refFactory.rootRef()));
        Assert.assertTrue(ref.isDescendantOf(refFactory.ref("test.person")));
        Assert.assertTrue(ref.isDescendantOf(refFactory.ref("test")));
        Assert.assertTrue(ref.isDescendantOf(refFactory.ref("")));
        Assert.assertFalse(ref.isDescendantOf(refFactory.ref("foo.bar")));
        Assert.assertFalse(ref.isDescendantOf(refFactory.ref("foo")));
    }
}
