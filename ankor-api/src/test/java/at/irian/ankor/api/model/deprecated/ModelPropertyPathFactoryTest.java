package at.irian.ankor.api.model.deprecated;

import at.irian.ankor.api.model.deprecated.ModelPropertyPath;
import at.irian.ankor.api.model.deprecated.ModelPropertyPathFactory;
import junit.framework.Assert;
import org.junit.Test;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class ModelPropertyPathFactoryTest {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelPropertyPathFactoryTest.class);

    private final ModelPropertyPathFactory factory = new ModelPropertyPathFactory();

    @Test
    public void testGetName1() throws Exception {
        ModelPropertyPath p = factory.getRootPath();
        Assert.assertEquals("name", "/", p.getName());
    }

    @Test
    public void testGetName2() throws Exception {
        ModelPropertyPath p = factory.getRootPath().withChild("test");
        Assert.assertEquals("name", "/test", p.getName());
    }

    @Test
    public void testGetName3() throws Exception {
        ModelPropertyPath p = factory.getRootPath().withChild("test").withChild("x").withChild("y");
        Assert.assertEquals("name", "/test/x/y", p.getName());
    }

    @Test
    public void testGetParent1() throws Exception {
        ModelPropertyPath p = factory.getRootPath();
        Assert.assertEquals("root parent is null", null, p.getParent());
    }

    @Test
    public void testGetParent2() throws Exception {
        ModelPropertyPath p = factory.getRootPath().withChild("test").withChild("x").withChild("y");
        Assert.assertEquals("name", "/test/x", p.getParent().getName());
    }
}
