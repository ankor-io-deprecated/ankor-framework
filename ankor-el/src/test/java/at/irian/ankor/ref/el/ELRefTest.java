package at.irian.ankor.ref.el;

import at.irian.ankor.application.ModelHolder;
import at.irian.ankor.el.ModelELContext;
import at.irian.ankor.el.StandardELContext;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefFactory;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.el.ExpressionFactory;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class ELRefTest {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ELRefTest.class);

    private ModelHolder modelHolder;
    private RefFactory refFactory;

    @Before
    public void setup() {
        this.modelHolder = new ModelHolder(Object.class);
        ExpressionFactory expressionFactory = ExpressionFactory.newInstance();
        Config config = ConfigFactory.load();
        ModelELContext modelELContext = new ModelELContext(new StandardELContext(), modelHolder, config);
        ELRefContext refContext = ELRefContext.create(expressionFactory,
                                                      modelELContext,
                                                      null, null,
                                                      config);
        this.refFactory = new ELRefFactory(refContext);
    }

    @Test
    public void test_root_path() throws Exception {
        Ref ref = refFactory.rootRef();
        Assert.assertEquals("root", ref.path());
    }

    @Test
    public void test_prop_path() throws Exception {
        Ref ref = refFactory.ref("root.test.person.name");
        Assert.assertEquals("root.test.person.name", ref.path());
    }

    @Test
    public void test_prop_path_auto() throws Exception {
        Ref ref = refFactory.ref("root.test.person.name");
        Assert.assertEquals("root.test.person.name", ref.path());
    }

    @Test
    public void test_parent_path() throws Exception {
        Ref ref = refFactory.ref("root.test.person.name");
        Assert.assertEquals("root.test.person", ref.parent().path());
    }

    @Test
    public void test_descendant() throws Exception {
        Ref ref = refFactory.ref("root.test.person.name");
        Assert.assertTrue(ref.isDescendantOf(refFactory.rootRef()));
        Assert.assertTrue(ref.isDescendantOf(refFactory.ref("root.test.person")));
        Assert.assertTrue(ref.isDescendantOf(refFactory.ref("root.test")));
        Assert.assertTrue(ref.isDescendantOf(refFactory.ref("root")));
        Assert.assertTrue(ref.isDescendantOf(refFactory.ref("")));
        Assert.assertFalse(ref.isDescendantOf(refFactory.ref("root.foo.bar")));
        Assert.assertFalse(ref.isDescendantOf(refFactory.ref("root.foo")));
    }
}
