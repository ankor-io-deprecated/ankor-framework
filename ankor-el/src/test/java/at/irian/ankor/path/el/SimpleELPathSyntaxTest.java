package at.irian.ankor.path.el;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Manfred Geiler
 */
public class SimpleELPathSyntaxTest {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleELPathSyntaxTest.class);

    private SimpleELPathSyntax pathSyntax;

    @Before
    public void setUp() throws Exception {
        pathSyntax = SimpleELPathSyntax.getInstance();
    }

    @Test
    public void testIsEqual() throws Exception {

        Assert.assertTrue(pathSyntax.isEqual("root", "root"));
        Assert.assertTrue(pathSyntax.isEqual("root.a", "root.a"));
        Assert.assertTrue(pathSyntax.isEqual("root.a.b", "root.a.b"));
        Assert.assertTrue(pathSyntax.isEqual("root['a']", "root.a"));
        Assert.assertTrue(pathSyntax.isEqual("root.a", "root['a']"));
        Assert.assertTrue(pathSyntax.isEqual("root['a'].b", "root.a.b"));
        Assert.assertTrue(pathSyntax.isEqual("root['a']['b']", "root.a.b"));

        Assert.assertFalse(pathSyntax.isEqual("root", "ruth"));
        Assert.assertFalse(pathSyntax.isEqual("root.a", "root.b"));
        Assert.assertFalse(pathSyntax.isEqual("root.a.b", "root.b.a"));
        Assert.assertFalse(pathSyntax.isEqual("root['a']", "root.b"));
        Assert.assertFalse(pathSyntax.isEqual("root.b", "root['a']"));
        Assert.assertFalse(pathSyntax.isEqual("root['1'].b", "root.a.b"));
        Assert.assertFalse(pathSyntax.isEqual("root['a']['b']", "root.a.c"));

    }



}
