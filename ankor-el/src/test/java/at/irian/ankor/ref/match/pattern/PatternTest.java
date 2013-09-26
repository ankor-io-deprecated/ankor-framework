package at.irian.ankor.ref.match.pattern;

import junit.framework.Assert;
import org.junit.Test;

/**
 * @author Manfred Geiler
 */
public class PatternTest {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(PatternTest.class);

    private Pattern createPattern(String pattern) {
        return new AntlrRefMatcherFactory().createPatternFrom(pattern);
    }

    @Test
    public void test_root() throws Exception {
        String pattern = "root";
        Pattern root = createPattern(pattern);
        Assert.assertEquals("root", root.property().getPropertyName());
        Assert.assertFalse(root.property().isWildcard());
        Assert.assertFalse(root.property().isBackref());

        Assert.assertNull(root.parent());
    }

    @Test
    public void test_straight() throws Exception {
        Pattern sub = createPattern("root.test.sub");
        Assert.assertEquals("sub", sub.property().getPropertyName());
        Assert.assertFalse(sub.property().isWildcard());
        Assert.assertFalse(sub.property().isBackref());

        Pattern test = sub.parent();
        Assert.assertEquals("test", test.property().getPropertyName());
        Assert.assertFalse(test.property().isWildcard());
        Assert.assertFalse(test.property().isBackref());

        Pattern root = test.parent();
        Assert.assertEquals("root", root.property().getPropertyName());
        Assert.assertFalse(root.property().isWildcard());
        Assert.assertFalse(root.property().isBackref());

        Assert.assertNull(root.parent());
    }

    @Test
    public void test_backrefs() throws Exception {
        Pattern sub = createPattern("(root).test.(sub)");
        Assert.assertEquals("sub", sub.property().getPropertyName());
        Assert.assertFalse(sub.property().isWildcard());
        Assert.assertTrue(sub.property().isBackref());

        Pattern test = sub.parent();
        Assert.assertEquals("test", test.property().getPropertyName());
        Assert.assertFalse(test.property().isWildcard());
        Assert.assertFalse(test.property().isBackref());

        Pattern root = test.parent();
        Assert.assertEquals("root", root.property().getPropertyName());
        Assert.assertFalse(root.property().isWildcard());
        Assert.assertTrue(root.property().isBackref());

        Assert.assertNull(root.parent());
    }

    @Test
    public void test_relative() throws Exception {
        Pattern sub = createPattern(".test.(sub)");
        Assert.assertEquals("sub", sub.property().getPropertyName());
        Assert.assertFalse(sub.property().isWildcard());
        Assert.assertTrue(sub.property().isBackref());

        Pattern test = sub.parent();
        Assert.assertEquals("test", test.property().getPropertyName());
        Assert.assertFalse(test.property().isWildcard());
        Assert.assertFalse(test.property().isBackref());

        Pattern root = test.parent();
        Assert.assertTrue (root.property().isWildcard());
        Assert.assertEquals(WildcardType.context, root.property().getWildcardType());
        Assert.assertFalse(root.property().isBackref());

        Assert.assertNull(root.parent());
    }

    @Test
    public void test_wildcard() throws Exception {
        Pattern sub = createPattern("**.test.*");
        Assert.assertTrue(sub.property().isWildcard());
        Assert.assertEquals(WildcardType.singleNode, sub.property().getWildcardType());
        Assert.assertFalse(sub.property().isBackref());

        Pattern test = sub.parent();
        Assert.assertEquals("test", test.property().getPropertyName());
        Assert.assertFalse(test.property().isWildcard());
        Assert.assertFalse(test.property().isBackref());

        Pattern root = test.parent();
        Assert.assertTrue (root.property().isWildcard());
        Assert.assertEquals(WildcardType.multiNode, root.property().getWildcardType());
        Assert.assertFalse(root.property().isBackref());

        Assert.assertNull(root.parent());
    }

    @Test
    public void test_square_brackets() throws Exception {
        Pattern sub = createPattern("**.test[*].sub");
        Assert.assertEquals("sub", sub.property().getPropertyName());
        Assert.assertFalse(sub.property().isWildcard());
        Assert.assertFalse(sub.property().isBackref());

        Pattern key = sub.parent();
        Assert.assertTrue (key.property().isWildcard());
        Assert.assertEquals(WildcardType.singleNode, key.property().getWildcardType());
        Assert.assertFalse(key.property().isBackref());

        Pattern test = key.parent();
        Assert.assertEquals("test", test.property().getPropertyName());
        Assert.assertFalse(test.property().isWildcard());
        Assert.assertFalse(test.property().isBackref());

        Pattern root = test.parent();
        Assert.assertTrue (root.property().isWildcard());
        Assert.assertEquals(WildcardType.multiNode, root.property().getWildcardType());
        Assert.assertFalse(root.property().isBackref());

        Assert.assertNull(root.parent());
    }

}
