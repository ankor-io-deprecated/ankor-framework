package at.irian.ankor.ref;

import at.irian.ankor.path.PathSyntax;
import at.irian.ankor.path.el.ELPathSyntax;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

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

    private boolean matches(RefMatcher matcher, Ref ref) {
        RefMatcher.Result result = matcher.match(ref);
        return result.isMatch();
    }
    
    @Test
    public void test_1() throws Exception {
        RefMatcher m = createMatcher("root1");

        Assert.assertTrue (matches(m, r1));
        Assert.assertFalse(matches(m, r2));
        Assert.assertFalse(matches(m, r11));
        Assert.assertFalse(matches(m, r12));
        Assert.assertFalse(matches(m, r21));
        Assert.assertFalse(matches(m, r22));
        Assert.assertFalse(matches(m, r111));
        Assert.assertFalse(matches(m, r112));
        Assert.assertFalse(matches(m, r121));
        Assert.assertFalse(matches(m, r122));
        Assert.assertFalse(matches(m, r211));
        Assert.assertFalse(matches(m, r212));
        Assert.assertFalse(matches(m, r221));
        Assert.assertFalse(matches(m, r222));
    }

    @Test
    public void test_2() throws Exception {
        RefMatcher m = createMatcher("root1.test1");

        Assert.assertFalse(matches(m, r1));
        Assert.assertFalse(matches(m, r2));
        Assert.assertTrue(matches(m, r11));
        Assert.assertFalse(matches(m, r12));
        Assert.assertFalse(matches(m, r21));
        Assert.assertFalse(matches(m, r22));
        Assert.assertFalse(matches(m, r111));
        Assert.assertFalse(matches(m, r112));
        Assert.assertFalse(matches(m, r121));
        Assert.assertFalse(matches(m, r122));
        Assert.assertFalse(matches(m, r211));
        Assert.assertFalse(matches(m, r212));
        Assert.assertFalse(matches(m, r221));
        Assert.assertFalse(matches(m, r222));
    }

    @Test
    public void test_3() throws Exception {
        RefMatcher m = createMatcher("root1.test1.sub1");

        Assert.assertFalse(matches(m, r1));
        Assert.assertFalse(matches(m, r2));
        Assert.assertFalse(matches(m, r11));
        Assert.assertFalse(matches(m, r12));
        Assert.assertFalse(matches(m, r21));
        Assert.assertFalse(matches(m, r22));
        Assert.assertTrue(matches(m, r111));
        Assert.assertFalse(matches(m, r112));
        Assert.assertFalse(matches(m, r121));
        Assert.assertFalse(matches(m, r122));
        Assert.assertFalse(matches(m, r211));
        Assert.assertFalse(matches(m, r212));
        Assert.assertFalse(matches(m, r221));
        Assert.assertFalse(matches(m, r222));
    }

    @Test
    public void test_wc1() throws Exception {
        RefMatcher m = createMatcher("root1.test1.*");

        Assert.assertFalse(matches(m, r1));
        Assert.assertFalse(matches(m, r2));
        Assert.assertFalse(matches(m, r11));
        Assert.assertFalse(matches(m, r12));
        Assert.assertFalse(matches(m, r21));
        Assert.assertFalse(matches(m, r22));
        Assert.assertTrue (matches(m, r111));
        Assert.assertTrue (matches(m, r112));
        Assert.assertFalse(matches(m, r121));
        Assert.assertFalse(matches(m, r122));
        Assert.assertFalse(matches(m, r211));
        Assert.assertFalse(matches(m, r212));
        Assert.assertFalse(matches(m, r221));
        Assert.assertFalse(matches(m, r222));
    }

    @Test
    public void test_wc2() throws Exception {
        RefMatcher m = createMatcher("root1.*.sub1");

        Assert.assertFalse(matches(m, r1));
        Assert.assertFalse(matches(m, r2));
        Assert.assertFalse(matches(m, r11));
        Assert.assertFalse(matches(m, r12));
        Assert.assertFalse(matches(m, r21));
        Assert.assertFalse(matches(m, r22));
        Assert.assertTrue (matches(m, r111));
        Assert.assertFalse(matches(m, r112));
        Assert.assertTrue (matches(m, r121));
        Assert.assertFalse(matches(m, r122));
        Assert.assertFalse(matches(m, r211));
        Assert.assertFalse(matches(m, r212));
        Assert.assertFalse(matches(m, r221));
        Assert.assertFalse(matches(m, r222));
    }

    @Test
    public void test_wc3() throws Exception {
        RefMatcher m = createMatcher("*.test1.sub1");

        Assert.assertFalse(matches(m, r1));
        Assert.assertFalse(matches(m, r2));
        Assert.assertFalse(matches(m, r11));
        Assert.assertFalse(matches(m, r12));
        Assert.assertFalse(matches(m, r21));
        Assert.assertFalse(matches(m, r22));
        Assert.assertTrue (matches(m, r111));
        Assert.assertFalse(matches(m, r112));
        Assert.assertFalse(matches(m, r121));
        Assert.assertFalse(matches(m, r122));
        Assert.assertTrue (matches(m, r211));
        Assert.assertFalse(matches(m, r212));
        Assert.assertFalse(matches(m, r221));
        Assert.assertFalse(matches(m, r222));
    }

    @Test
    public void test_wc4() throws Exception {
        RefMatcher m = createMatcher("*.test1.*");

        Assert.assertFalse(matches(m, r1));
        Assert.assertFalse(matches(m, r2));
        Assert.assertFalse(matches(m, r11));
        Assert.assertFalse(matches(m, r12));
        Assert.assertFalse(matches(m, r21));
        Assert.assertFalse(matches(m, r22));
        Assert.assertTrue (matches(m, r111));
        Assert.assertTrue (matches(m, r112));
        Assert.assertFalse(matches(m, r121));
        Assert.assertFalse(matches(m, r122));
        Assert.assertTrue (matches(m, r211));
        Assert.assertTrue (matches(m, r212));
        Assert.assertFalse(matches(m, r221));
        Assert.assertFalse(matches(m, r222));
    }

    @Test
    public void test_wc5() throws Exception {
        RefMatcher m = createMatcher("*.*.*");

        Assert.assertFalse(matches(m, r1));
        Assert.assertFalse(matches(m, r2));
        Assert.assertFalse(matches(m, r11));
        Assert.assertFalse(matches(m, r12));
        Assert.assertFalse(matches(m, r21));
        Assert.assertFalse(matches(m, r22));
        Assert.assertTrue (matches(m, r111));
        Assert.assertTrue (matches(m, r112));
        Assert.assertTrue(matches(m, r121));
        Assert.assertTrue(matches(m, r122));
        Assert.assertTrue (matches(m, r211));
        Assert.assertTrue (matches(m, r212));
        Assert.assertTrue(matches(m, r221));
        Assert.assertTrue(matches(m, r222));
    }



    @Test
    public void test_mwc1() throws Exception {
        RefMatcher m = createMatcher("root1.test1.**");

        Assert.assertFalse(matches(m, r1));
        Assert.assertFalse(matches(m, r2));
        Assert.assertFalse(matches(m, r11));
        Assert.assertFalse(matches(m, r12));
        Assert.assertFalse(matches(m, r21));
        Assert.assertFalse(matches(m, r22));
        Assert.assertTrue (matches(m, r111));
        Assert.assertTrue (matches(m, r112));
        Assert.assertFalse(matches(m, r121));
        Assert.assertFalse(matches(m, r122));
        Assert.assertFalse(matches(m, r211));
        Assert.assertFalse(matches(m, r212));
        Assert.assertFalse(matches(m, r221));
        Assert.assertFalse(matches(m, r222));
    }

    @Test
    public void test_mwc2() throws Exception {
        RefMatcher m = createMatcher("root1.**.sub1");

        Assert.assertFalse(matches(m, r1));
        Assert.assertFalse(matches(m, r2));
        Assert.assertFalse(matches(m, r11));
        Assert.assertFalse(matches(m, r12));
        Assert.assertFalse(matches(m, r21));
        Assert.assertFalse(matches(m, r22));
        Assert.assertTrue (matches(m, r111));
        Assert.assertFalse(matches(m, r112));
        Assert.assertTrue (matches(m, r121));
        Assert.assertFalse(matches(m, r122));
        Assert.assertFalse(matches(m, r211));
        Assert.assertFalse(matches(m, r212));
        Assert.assertFalse(matches(m, r221));
        Assert.assertFalse(matches(m, r222));
    }

    @Test
    public void test_mwc3() throws Exception {
        RefMatcher m = createMatcher("**.test1.sub1");

        Assert.assertFalse(matches(m, r1));
        Assert.assertFalse(matches(m, r2));
        Assert.assertFalse(matches(m, r11));
        Assert.assertFalse(matches(m, r12));
        Assert.assertFalse(matches(m, r21));
        Assert.assertFalse(matches(m, r22));
        Assert.assertTrue (matches(m, r111));
        Assert.assertFalse(matches(m, r112));
        Assert.assertFalse(matches(m, r121));
        Assert.assertFalse(matches(m, r122));
        Assert.assertTrue (matches(m, r211));
        Assert.assertFalse(matches(m, r212));
        Assert.assertFalse(matches(m, r221));
        Assert.assertFalse(matches(m, r222));
    }

    @Test
    public void test_mwc4() throws Exception {
        RefMatcher m = createMatcher("**.test1.**");

        Assert.assertFalse(matches(m, r1));
        Assert.assertFalse(matches(m, r2));
        Assert.assertFalse(matches(m, r11));
        Assert.assertFalse(matches(m, r12));
        Assert.assertFalse(matches(m, r21));
        Assert.assertFalse(matches(m, r22));
        Assert.assertTrue (matches(m, r111));
        Assert.assertTrue (matches(m, r112));
        Assert.assertFalse(matches(m, r121));
        Assert.assertFalse(matches(m, r122));
        Assert.assertTrue (matches(m, r211));
        Assert.assertTrue (matches(m, r212));
        Assert.assertFalse(matches(m, r221));
        Assert.assertFalse(matches(m, r222));
    }

    @Test
    public void test_mwc5() throws Exception {
        RefMatcher m = createMatcher("**.**.**");

        Assert.assertFalse(matches(m, r1));
        Assert.assertFalse(matches(m, r2));
        Assert.assertFalse(matches(m, r11));
        Assert.assertFalse(matches(m, r12));
        Assert.assertFalse(matches(m, r21));
        Assert.assertFalse(matches(m, r22));
        Assert.assertTrue (matches(m, r111));
        Assert.assertTrue (matches(m, r112));
        Assert.assertTrue(matches(m, r121));
        Assert.assertTrue(matches(m, r122));
        Assert.assertTrue (matches(m, r211));
        Assert.assertTrue (matches(m, r212));
        Assert.assertTrue(matches(m, r221));
        Assert.assertTrue(matches(m, r222));
    }

    @Test
    public void test_mwc6() throws Exception {
        RefMatcher m = createMatcher("**.**");

        Assert.assertFalse(matches(m, r1));
        Assert.assertFalse(matches(m, r2));
        Assert.assertTrue(matches(m, r11));
        Assert.assertTrue(matches(m, r12));
        Assert.assertTrue(matches(m, r21));
        Assert.assertTrue(matches(m, r22));
        Assert.assertTrue (matches(m, r111));
        Assert.assertTrue (matches(m, r112));
        Assert.assertTrue (matches(m, r121));
        Assert.assertTrue (matches(m, r122));
        Assert.assertTrue (matches(m, r211));
        Assert.assertTrue (matches(m, r212));
        Assert.assertTrue (matches(m, r221));
        Assert.assertTrue (matches(m, r222));
    }

    @Test
    public void test_mwc7() throws Exception {
        RefMatcher m = createMatcher("**");

        Assert.assertTrue(matches(m, r1));
        Assert.assertTrue(matches(m, r2));
        Assert.assertTrue(matches(m, r11));
        Assert.assertTrue(matches(m, r12));
        Assert.assertTrue(matches(m, r21));
        Assert.assertTrue(matches(m, r22));
        Assert.assertTrue (matches(m, r111));
        Assert.assertTrue (matches(m, r112));
        Assert.assertTrue (matches(m, r121));
        Assert.assertTrue (matches(m, r122));
        Assert.assertTrue (matches(m, r211));
        Assert.assertTrue (matches(m, r212));
        Assert.assertTrue (matches(m, r221));
        Assert.assertTrue (matches(m, r222));
    }


    @Test
    public void test_t1() throws Exception {
        RefMatcher m = createMatcher("root1.test1.<Integer>");

        Assert.assertFalse(matches(m, r1));
        Assert.assertFalse(matches(m, r2));
        Assert.assertFalse(matches(m, r11));
        Assert.assertFalse(matches(m, r12));
        Assert.assertFalse(matches(m, r21));
        Assert.assertFalse(matches(m, r22));
        Assert.assertTrue (matches(m, r111));
        Assert.assertTrue (matches(m, r112));
        Assert.assertFalse(matches(m, r121));
        Assert.assertFalse(matches(m, r122));
        Assert.assertFalse(matches(m, r211));
        Assert.assertFalse(matches(m, r212));
        Assert.assertFalse(matches(m, r221));
        Assert.assertFalse(matches(m, r222));
    }

    @Test
    public void test_tfq1() throws Exception {
        RefMatcher m = createMatcher("root1.test1.<java.lang.Integer>");

        Assert.assertFalse(matches(m, r1));
        Assert.assertFalse(matches(m, r2));
        Assert.assertFalse(matches(m, r11));
        Assert.assertFalse(matches(m, r12));
        Assert.assertFalse(matches(m, r21));
        Assert.assertFalse(matches(m, r22));
        Assert.assertTrue (matches(m, r111));
        Assert.assertTrue (matches(m, r112));
        Assert.assertFalse(matches(m, r121));
        Assert.assertFalse(matches(m, r122));
        Assert.assertFalse(matches(m, r211));
        Assert.assertFalse(matches(m, r212));
        Assert.assertFalse(matches(m, r221));
        Assert.assertFalse(matches(m, r222));
    }

    @Test
    public void test_t2() throws Exception {
        RefMatcher m = createMatcher("root1.test1.<String>");

        Assert.assertFalse(matches(m, r1));
        Assert.assertFalse(matches(m, r2));
        Assert.assertFalse(matches(m, r11));
        Assert.assertFalse(matches(m, r12));
        Assert.assertFalse(matches(m, r21));
        Assert.assertFalse(matches(m, r22));
        Assert.assertFalse(matches(m, r111));
        Assert.assertFalse(matches(m, r112));
        Assert.assertFalse(matches(m, r121));
        Assert.assertFalse(matches(m, r122));
        Assert.assertFalse(matches(m, r211));
        Assert.assertFalse(matches(m, r212));
        Assert.assertFalse(matches(m, r221));
        Assert.assertFalse(matches(m, r222));
    }

    @Test
    public void test_t3() throws Exception {
        RefMatcher m = createMatcher("root1.<String>.*");

        Assert.assertFalse(matches(m, r1));
        Assert.assertFalse(matches(m, r2));
        Assert.assertFalse(matches(m, r11));
        Assert.assertFalse(matches(m, r12));
        Assert.assertFalse(matches(m, r21));
        Assert.assertFalse(matches(m, r22));
        Assert.assertTrue(matches(m, r111));
        Assert.assertTrue(matches(m, r112));
        Assert.assertTrue(matches(m, r121));
        Assert.assertTrue(matches(m, r122));
        Assert.assertFalse(matches(m, r211));
        Assert.assertFalse(matches(m, r212));
        Assert.assertFalse(matches(m, r221));
        Assert.assertFalse(matches(m, r222));
    }

    @Test
    public void test_t4() throws Exception {
        RefMatcher m = createMatcher("**.<String>.sub1");

        Assert.assertFalse(matches(m, r1));
        Assert.assertFalse(matches(m, r2));
        Assert.assertFalse(matches(m, r11));
        Assert.assertFalse(matches(m, r12));
        Assert.assertFalse(matches(m, r21));
        Assert.assertFalse(matches(m, r22));
        Assert.assertTrue(matches(m, r111));
        Assert.assertFalse(matches(m, r112));
        Assert.assertTrue(matches(m, r121));
        Assert.assertFalse(matches(m, r122));
        Assert.assertTrue(matches(m, r211));
        Assert.assertFalse(matches(m, r212));
        Assert.assertTrue(matches(m, r221));
        Assert.assertFalse(matches(m, r222));
    }

    @Test
    public void test_t5() throws Exception {
        RefMatcher m = createMatcher("<RefMatcherTest>.**");

        Assert.assertFalse(matches(m, r1));
        Assert.assertFalse(matches(m, r2));
        Assert.assertTrue(matches(m, r11));
        Assert.assertTrue(matches(m, r12));
        Assert.assertTrue(matches(m, r21));
        Assert.assertTrue(matches(m, r22));
        Assert.assertTrue (matches(m, r111));
        Assert.assertTrue(matches(m, r112));
        Assert.assertTrue (matches(m, r121));
        Assert.assertTrue(matches(m, r122));
        Assert.assertTrue(matches(m, r211));
        Assert.assertTrue(matches(m, r212));
        Assert.assertTrue(matches(m, r221));
        Assert.assertTrue(matches(m, r222));
    }

    @Test
    public void test_t6() throws Exception {
        RefMatcher m = createMatcher("<at.irian.ankor.ref.RefMatcherTest>.**");

        Assert.assertFalse(matches(m, r1));
        Assert.assertFalse(matches(m, r2));
        Assert.assertTrue(matches(m, r11));
        Assert.assertTrue(matches(m, r12));
        Assert.assertTrue(matches(m, r21));
        Assert.assertTrue(matches(m, r22));
        Assert.assertTrue (matches(m, r111));
        Assert.assertTrue(matches(m, r112));
        Assert.assertTrue (matches(m, r121));
        Assert.assertTrue(matches(m, r122));
        Assert.assertTrue (matches(m, r211));
        Assert.assertTrue(matches(m, r212));
        Assert.assertTrue (matches(m, r221));
        Assert.assertTrue(matches(m, r222));
    }

    @Test
    public void test_w1() throws Exception {
        RefMatcher m = createMatcher("**.<String>.(sub1)");

        Assert.assertFalse(matches(m, r1));
        Assert.assertFalse(matches(m, r2));
        Assert.assertFalse(matches(m, r11));
        Assert.assertFalse(matches(m, r12));
        Assert.assertFalse(matches(m, r21));
        Assert.assertFalse(matches(m, r22));
        Assert.assertTrue (matches(m, r111));
        Assert.assertFalse(matches(m, r112));
        Assert.assertTrue (matches(m, r121));
        Assert.assertFalse(matches(m, r122));
        Assert.assertTrue (matches(m, r211));
        Assert.assertFalse(matches(m, r212));
        Assert.assertTrue (matches(m, r221));
        Assert.assertFalse(matches(m, r222));

        List<Ref> watchedRefs = m.match(r111).getWatchedRefs();
        Assert.assertTrue(watchedRefs.size() == 1);
        Assert.assertTrue(watchedRefs.get(0).equals(r111));
    }

    @Test
    public void test_w3() throws Exception {
        RefMatcher m = createMatcher("(root1).(test1).(sub1)");

        Assert.assertFalse(matches(m, r1));
        Assert.assertFalse(matches(m, r2));
        Assert.assertFalse(matches(m, r11));
        Assert.assertFalse(matches(m, r12));
        Assert.assertFalse(matches(m, r21));
        Assert.assertFalse(matches(m, r22));
        Assert.assertTrue (matches(m, r111));
        Assert.assertFalse(matches(m, r112));
        Assert.assertFalse(matches(m, r121));
        Assert.assertFalse(matches(m, r122));
        Assert.assertFalse(matches(m, r211));
        Assert.assertFalse(matches(m, r212));
        Assert.assertFalse(matches(m, r221));
        Assert.assertFalse(matches(m, r222));

        List<Ref> watchedRefs = m.match(r111).getWatchedRefs();
        Assert.assertTrue(watchedRefs.size() == 3);
        Assert.assertTrue(watchedRefs.get(0).equals(r1));
        Assert.assertTrue(watchedRefs.get(1).equals(r11));
        Assert.assertTrue(watchedRefs.get(2).equals(r111));
    }


}
