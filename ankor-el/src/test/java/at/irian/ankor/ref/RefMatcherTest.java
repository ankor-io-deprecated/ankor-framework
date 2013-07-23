package at.irian.ankor.ref;

import at.irian.ankor.path.PathSyntax;
import at.irian.ankor.path.el.ELPathSyntax;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import static at.irian.ankor.ref.el.MockELRef.createRef;
import static at.irian.ankor.ref.el.MockELRef.createRootRef;

/**
 * @author Manfred Geiler
 */
@SuppressWarnings("SpellCheckingInspection")
public class RefMatcherTest {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RefMatcherTest.class);

    private PathSyntax pathSyntax;
    private Ref r1;
    private Ref r2;
    private Ref r11;
    private Ref r12;
    private Ref r21;
    private Ref r22;
    private Ref r111;
    private Ref r112;
    private Ref r121;
    private Ref r122;
    private Ref r211;
    private Ref r212;
    private Ref r221;
    private Ref r222;

    @Before
    public void init() {
        pathSyntax = ELPathSyntax.getInstance();

        RefMatcherTest root1Val = new RefMatcherTest();
        RefMatcherTest root2Val = new RefMatcherTest();
        String test1Val = "1";
        String test2Val = "2";
        Integer sub1Val = 1;
        Integer sub2Val = 2;

        r1 = createRootRef("root1", root1Val);
        r2 = createRootRef("root2", root2Val);
        r11 = createRef(r1, "test1", test1Val);
        r12 = createRef(r1, "test2", test2Val);
        r21 = createRef(r2, "test1", test1Val);
        r22 = createRef(r2, "test2", test2Val);
        r111 = createRef(r11, "sub1", sub1Val);
        r112 = createRef(r11, "sub2", sub2Val);
        r121 = createRef(r12, "sub1", sub1Val);
        r122 = createRef(r12, "sub2", sub2Val);
        r211 = createRef(r21, "sub1", sub1Val);
        r212 = createRef(r21, "sub2", sub2Val);
        r221 = createRef(r22, "sub1", sub1Val);
        r222 = createRef(r22, "sub2", sub2Val);
    }

    private RefMatcher createMatcher(String pattern) {
        return new RefMatcher(pathSyntax, pattern);
    }

    @Test
    public void test_1() throws Exception {
        RefMatcher m = createMatcher("root1");

        Assert.assertTrue (m.matches(r1));
        Assert.assertFalse(m.matches(r2));
        Assert.assertFalse(m.matches(r11));
        Assert.assertFalse(m.matches(r12));
        Assert.assertFalse(m.matches(r21));
        Assert.assertFalse(m.matches(r22));
        Assert.assertFalse(m.matches(r111));
        Assert.assertFalse(m.matches(r112));
        Assert.assertFalse(m.matches(r121));
        Assert.assertFalse(m.matches(r122));
        Assert.assertFalse(m.matches(r211));
        Assert.assertFalse(m.matches(r212));
        Assert.assertFalse(m.matches(r221));
        Assert.assertFalse(m.matches(r222));
    }

    @Test
    public void test_2() throws Exception {
        RefMatcher m = createMatcher("root1.test1");

        Assert.assertFalse(m.matches(r1));
        Assert.assertFalse(m.matches(r2));
        Assert.assertTrue(m.matches(r11));
        Assert.assertFalse(m.matches(r12));
        Assert.assertFalse(m.matches(r21));
        Assert.assertFalse(m.matches(r22));
        Assert.assertFalse(m.matches(r111));
        Assert.assertFalse(m.matches(r112));
        Assert.assertFalse(m.matches(r121));
        Assert.assertFalse(m.matches(r122));
        Assert.assertFalse(m.matches(r211));
        Assert.assertFalse(m.matches(r212));
        Assert.assertFalse(m.matches(r221));
        Assert.assertFalse(m.matches(r222));
    }

    @Test
    public void test_3() throws Exception {
        RefMatcher m = createMatcher("root1.test1.sub1");

        Assert.assertFalse(m.matches(r1));
        Assert.assertFalse(m.matches(r2));
        Assert.assertFalse(m.matches(r11));
        Assert.assertFalse(m.matches(r12));
        Assert.assertFalse(m.matches(r21));
        Assert.assertFalse(m.matches(r22));
        Assert.assertTrue(m.matches(r111));
        Assert.assertFalse(m.matches(r112));
        Assert.assertFalse(m.matches(r121));
        Assert.assertFalse(m.matches(r122));
        Assert.assertFalse(m.matches(r211));
        Assert.assertFalse(m.matches(r212));
        Assert.assertFalse(m.matches(r221));
        Assert.assertFalse(m.matches(r222));
    }

    @Test
    public void test_wc1() throws Exception {
        RefMatcher m = createMatcher("root1.test1.*");

        Assert.assertFalse(m.matches(r1));
        Assert.assertFalse(m.matches(r2));
        Assert.assertFalse(m.matches(r11));
        Assert.assertFalse(m.matches(r12));
        Assert.assertFalse(m.matches(r21));
        Assert.assertFalse(m.matches(r22));
        Assert.assertTrue (m.matches(r111));
        Assert.assertTrue (m.matches(r112));
        Assert.assertFalse(m.matches(r121));
        Assert.assertFalse(m.matches(r122));
        Assert.assertFalse(m.matches(r211));
        Assert.assertFalse(m.matches(r212));
        Assert.assertFalse(m.matches(r221));
        Assert.assertFalse(m.matches(r222));
    }

    @Test
    public void test_wc2() throws Exception {
        RefMatcher m = createMatcher("root1.*.sub1");

        Assert.assertFalse(m.matches(r1));
        Assert.assertFalse(m.matches(r2));
        Assert.assertFalse(m.matches(r11));
        Assert.assertFalse(m.matches(r12));
        Assert.assertFalse(m.matches(r21));
        Assert.assertFalse(m.matches(r22));
        Assert.assertTrue (m.matches(r111));
        Assert.assertFalse(m.matches(r112));
        Assert.assertTrue (m.matches(r121));
        Assert.assertFalse(m.matches(r122));
        Assert.assertFalse(m.matches(r211));
        Assert.assertFalse(m.matches(r212));
        Assert.assertFalse(m.matches(r221));
        Assert.assertFalse(m.matches(r222));
    }

    @Test
    public void test_wc3() throws Exception {
        RefMatcher m = createMatcher("*.test1.sub1");

        Assert.assertFalse(m.matches(r1));
        Assert.assertFalse(m.matches(r2));
        Assert.assertFalse(m.matches(r11));
        Assert.assertFalse(m.matches(r12));
        Assert.assertFalse(m.matches(r21));
        Assert.assertFalse(m.matches(r22));
        Assert.assertTrue (m.matches(r111));
        Assert.assertFalse(m.matches(r112));
        Assert.assertFalse(m.matches(r121));
        Assert.assertFalse(m.matches(r122));
        Assert.assertTrue (m.matches(r211));
        Assert.assertFalse(m.matches(r212));
        Assert.assertFalse(m.matches(r221));
        Assert.assertFalse(m.matches(r222));
    }

    @Test
    public void test_wc4() throws Exception {
        RefMatcher m = createMatcher("*.test1.*");

        Assert.assertFalse(m.matches(r1));
        Assert.assertFalse(m.matches(r2));
        Assert.assertFalse(m.matches(r11));
        Assert.assertFalse(m.matches(r12));
        Assert.assertFalse(m.matches(r21));
        Assert.assertFalse(m.matches(r22));
        Assert.assertTrue (m.matches(r111));
        Assert.assertTrue (m.matches(r112));
        Assert.assertFalse(m.matches(r121));
        Assert.assertFalse(m.matches(r122));
        Assert.assertTrue (m.matches(r211));
        Assert.assertTrue (m.matches(r212));
        Assert.assertFalse(m.matches(r221));
        Assert.assertFalse(m.matches(r222));
    }

    @Test
    public void test_wc5() throws Exception {
        RefMatcher m = createMatcher("*.*.*");

        Assert.assertFalse(m.matches(r1));
        Assert.assertFalse(m.matches(r2));
        Assert.assertFalse(m.matches(r11));
        Assert.assertFalse(m.matches(r12));
        Assert.assertFalse(m.matches(r21));
        Assert.assertFalse(m.matches(r22));
        Assert.assertTrue (m.matches(r111));
        Assert.assertTrue (m.matches(r112));
        Assert.assertTrue(m.matches(r121));
        Assert.assertTrue(m.matches(r122));
        Assert.assertTrue (m.matches(r211));
        Assert.assertTrue (m.matches(r212));
        Assert.assertTrue(m.matches(r221));
        Assert.assertTrue(m.matches(r222));
    }



    @Test
    public void test_mwc1() throws Exception {
        RefMatcher m = createMatcher("root1.test1.**");

        Assert.assertFalse(m.matches(r1));
        Assert.assertFalse(m.matches(r2));
        Assert.assertFalse(m.matches(r11));
        Assert.assertFalse(m.matches(r12));
        Assert.assertFalse(m.matches(r21));
        Assert.assertFalse(m.matches(r22));
        Assert.assertTrue (m.matches(r111));
        Assert.assertTrue (m.matches(r112));
        Assert.assertFalse(m.matches(r121));
        Assert.assertFalse(m.matches(r122));
        Assert.assertFalse(m.matches(r211));
        Assert.assertFalse(m.matches(r212));
        Assert.assertFalse(m.matches(r221));
        Assert.assertFalse(m.matches(r222));
    }

    @Test
    public void test_mwc2() throws Exception {
        RefMatcher m = createMatcher("root1.**.sub1");

        Assert.assertFalse(m.matches(r1));
        Assert.assertFalse(m.matches(r2));
        Assert.assertFalse(m.matches(r11));
        Assert.assertFalse(m.matches(r12));
        Assert.assertFalse(m.matches(r21));
        Assert.assertFalse(m.matches(r22));
        Assert.assertTrue (m.matches(r111));
        Assert.assertFalse(m.matches(r112));
        Assert.assertTrue (m.matches(r121));
        Assert.assertFalse(m.matches(r122));
        Assert.assertFalse(m.matches(r211));
        Assert.assertFalse(m.matches(r212));
        Assert.assertFalse(m.matches(r221));
        Assert.assertFalse(m.matches(r222));
    }

    @Test
    public void test_mwc3() throws Exception {
        RefMatcher m = createMatcher("**.test1.sub1");

        Assert.assertFalse(m.matches(r1));
        Assert.assertFalse(m.matches(r2));
        Assert.assertFalse(m.matches(r11));
        Assert.assertFalse(m.matches(r12));
        Assert.assertFalse(m.matches(r21));
        Assert.assertFalse(m.matches(r22));
        Assert.assertTrue (m.matches(r111));
        Assert.assertFalse(m.matches(r112));
        Assert.assertFalse(m.matches(r121));
        Assert.assertFalse(m.matches(r122));
        Assert.assertTrue (m.matches(r211));
        Assert.assertFalse(m.matches(r212));
        Assert.assertFalse(m.matches(r221));
        Assert.assertFalse(m.matches(r222));
    }

    @Test
    public void test_mwc4() throws Exception {
        RefMatcher m = createMatcher("**.test1.**");

        Assert.assertFalse(m.matches(r1));
        Assert.assertFalse(m.matches(r2));
        Assert.assertFalse(m.matches(r11));
        Assert.assertFalse(m.matches(r12));
        Assert.assertFalse(m.matches(r21));
        Assert.assertFalse(m.matches(r22));
        Assert.assertTrue (m.matches(r111));
        Assert.assertTrue (m.matches(r112));
        Assert.assertFalse(m.matches(r121));
        Assert.assertFalse(m.matches(r122));
        Assert.assertTrue (m.matches(r211));
        Assert.assertTrue (m.matches(r212));
        Assert.assertFalse(m.matches(r221));
        Assert.assertFalse(m.matches(r222));
    }

    @Test
    public void test_mwc5() throws Exception {
        RefMatcher m = createMatcher("**.**.**");

        Assert.assertFalse(m.matches(r1));
        Assert.assertFalse(m.matches(r2));
        Assert.assertFalse(m.matches(r11));
        Assert.assertFalse(m.matches(r12));
        Assert.assertFalse(m.matches(r21));
        Assert.assertFalse(m.matches(r22));
        Assert.assertTrue (m.matches(r111));
        Assert.assertTrue (m.matches(r112));
        Assert.assertTrue(m.matches(r121));
        Assert.assertTrue(m.matches(r122));
        Assert.assertTrue (m.matches(r211));
        Assert.assertTrue (m.matches(r212));
        Assert.assertTrue(m.matches(r221));
        Assert.assertTrue(m.matches(r222));
    }

    @Test
    public void test_mwc6() throws Exception {
        RefMatcher m = createMatcher("**.**");

        Assert.assertFalse(m.matches(r1));
        Assert.assertFalse(m.matches(r2));
        Assert.assertTrue(m.matches(r11));
        Assert.assertTrue(m.matches(r12));
        Assert.assertTrue(m.matches(r21));
        Assert.assertTrue(m.matches(r22));
        Assert.assertTrue (m.matches(r111));
        Assert.assertTrue (m.matches(r112));
        Assert.assertTrue (m.matches(r121));
        Assert.assertTrue (m.matches(r122));
        Assert.assertTrue (m.matches(r211));
        Assert.assertTrue (m.matches(r212));
        Assert.assertTrue (m.matches(r221));
        Assert.assertTrue (m.matches(r222));
    }

    @Test
    public void test_mwc7() throws Exception {
        RefMatcher m = createMatcher("**");

        Assert.assertTrue(m.matches(r1));
        Assert.assertTrue(m.matches(r2));
        Assert.assertTrue(m.matches(r11));
        Assert.assertTrue(m.matches(r12));
        Assert.assertTrue(m.matches(r21));
        Assert.assertTrue(m.matches(r22));
        Assert.assertTrue (m.matches(r111));
        Assert.assertTrue (m.matches(r112));
        Assert.assertTrue (m.matches(r121));
        Assert.assertTrue (m.matches(r122));
        Assert.assertTrue (m.matches(r211));
        Assert.assertTrue (m.matches(r212));
        Assert.assertTrue (m.matches(r221));
        Assert.assertTrue (m.matches(r222));
    }


    @Test
    public void test_t1() throws Exception {
        RefMatcher m = createMatcher("root1.test1.<Integer>");

        Assert.assertFalse(m.matches(r1));
        Assert.assertFalse(m.matches(r2));
        Assert.assertFalse(m.matches(r11));
        Assert.assertFalse(m.matches(r12));
        Assert.assertFalse(m.matches(r21));
        Assert.assertFalse(m.matches(r22));
        Assert.assertTrue (m.matches(r111));
        Assert.assertTrue (m.matches(r112));
        Assert.assertFalse(m.matches(r121));
        Assert.assertFalse(m.matches(r122));
        Assert.assertFalse(m.matches(r211));
        Assert.assertFalse(m.matches(r212));
        Assert.assertFalse(m.matches(r221));
        Assert.assertFalse(m.matches(r222));
    }

    @Test
    public void test_tfq1() throws Exception {
        RefMatcher m = createMatcher("root1.test1.<java.lang.Integer>");

        Assert.assertFalse(m.matches(r1));
        Assert.assertFalse(m.matches(r2));
        Assert.assertFalse(m.matches(r11));
        Assert.assertFalse(m.matches(r12));
        Assert.assertFalse(m.matches(r21));
        Assert.assertFalse(m.matches(r22));
        Assert.assertTrue (m.matches(r111));
        Assert.assertTrue (m.matches(r112));
        Assert.assertFalse(m.matches(r121));
        Assert.assertFalse(m.matches(r122));
        Assert.assertFalse(m.matches(r211));
        Assert.assertFalse(m.matches(r212));
        Assert.assertFalse(m.matches(r221));
        Assert.assertFalse(m.matches(r222));
    }

    @Test
    public void test_t2() throws Exception {
        RefMatcher m = createMatcher("root1.test1.<String>");

        Assert.assertFalse(m.matches(r1));
        Assert.assertFalse(m.matches(r2));
        Assert.assertFalse(m.matches(r11));
        Assert.assertFalse(m.matches(r12));
        Assert.assertFalse(m.matches(r21));
        Assert.assertFalse(m.matches(r22));
        Assert.assertFalse(m.matches(r111));
        Assert.assertFalse(m.matches(r112));
        Assert.assertFalse(m.matches(r121));
        Assert.assertFalse(m.matches(r122));
        Assert.assertFalse(m.matches(r211));
        Assert.assertFalse(m.matches(r212));
        Assert.assertFalse(m.matches(r221));
        Assert.assertFalse(m.matches(r222));
    }

    @Test
    public void test_t3() throws Exception {
        RefMatcher m = createMatcher("root1.<String>.*");

        Assert.assertFalse(m.matches(r1));
        Assert.assertFalse(m.matches(r2));
        Assert.assertFalse(m.matches(r11));
        Assert.assertFalse(m.matches(r12));
        Assert.assertFalse(m.matches(r21));
        Assert.assertFalse(m.matches(r22));
        Assert.assertTrue(m.matches(r111));
        Assert.assertTrue(m.matches(r112));
        Assert.assertTrue(m.matches(r121));
        Assert.assertTrue(m.matches(r122));
        Assert.assertFalse(m.matches(r211));
        Assert.assertFalse(m.matches(r212));
        Assert.assertFalse(m.matches(r221));
        Assert.assertFalse(m.matches(r222));
    }

    @Test
    public void test_t4() throws Exception {
        RefMatcher m = createMatcher("**.<String>.sub1");

        Assert.assertFalse(m.matches(r1));
        Assert.assertFalse(m.matches(r2));
        Assert.assertFalse(m.matches(r11));
        Assert.assertFalse(m.matches(r12));
        Assert.assertFalse(m.matches(r21));
        Assert.assertFalse(m.matches(r22));
        Assert.assertTrue (m.matches(r111));
        Assert.assertFalse(m.matches(r112));
        Assert.assertTrue (m.matches(r121));
        Assert.assertFalse(m.matches(r122));
        Assert.assertTrue(m.matches(r211));
        Assert.assertFalse(m.matches(r212));
        Assert.assertTrue(m.matches(r221));
        Assert.assertFalse(m.matches(r222));
    }

    @Test
    public void test_t5() throws Exception {
        RefMatcher m = createMatcher("<RefMatcherTest>.**");

        Assert.assertFalse(m.matches(r1));
        Assert.assertFalse(m.matches(r2));
        Assert.assertTrue (m.matches(r11));
        Assert.assertTrue (m.matches(r12));
        Assert.assertTrue (m.matches(r21));
        Assert.assertTrue (m.matches(r22));
        Assert.assertTrue (m.matches(r111));
        Assert.assertTrue (m.matches(r112));
        Assert.assertTrue (m.matches(r121));
        Assert.assertTrue (m.matches(r122));
        Assert.assertTrue (m.matches(r211));
        Assert.assertTrue (m.matches(r212));
        Assert.assertTrue (m.matches(r221));
        Assert.assertTrue (m.matches(r222));
    }

    @Test
    public void test_t6() throws Exception {
        RefMatcher m = createMatcher("<at.irian.ankor.ref.RefMatcherTest>.**");

        Assert.assertFalse(m.matches(r1));
        Assert.assertFalse(m.matches(r2));
        Assert.assertTrue (m.matches(r11));
        Assert.assertTrue (m.matches(r12));
        Assert.assertTrue (m.matches(r21));
        Assert.assertTrue (m.matches(r22));
        Assert.assertTrue (m.matches(r111));
        Assert.assertTrue (m.matches(r112));
        Assert.assertTrue (m.matches(r121));
        Assert.assertTrue (m.matches(r122));
        Assert.assertTrue (m.matches(r211));
        Assert.assertTrue (m.matches(r212));
        Assert.assertTrue (m.matches(r221));
        Assert.assertTrue (m.matches(r222));
    }

}
