package at.irian.ankor.viewmodel.diff;

import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Manfred Geiler
 */
public class ListDiffTest {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ListDiffTest.class);

    @Test
    public void testCalcDiffChanges_0() throws Exception {
        List<Integer> list1 = Arrays.asList(1, 2, 3);
        List<Integer> list2 = Arrays.asList(1, 2, 3);
        List<DiffChange<Integer>> diffChanges = new ListDiff<Integer>(null, list1, list2).calcDiffChanges();
        Assert.assertEquals(list2, apply(list1, diffChanges));
        Assert.assertEquals(0, diffChanges.size());
    }

    @Test
    public void testCalcDiffChanges_1() throws Exception {
        List<Integer> list1 = Arrays.asList(1, 2, 3);
        List<Integer> list2 = Arrays.asList();
        List<DiffChange<Integer>> diffChanges = new ListDiff<Integer>(null, list1, list2).calcDiffChanges();
        Assert.assertEquals(list2, apply(list1, diffChanges));
        Assert.assertEquals(3, diffChanges.size());
    }

    @Test
    public void testCalcDiffChanges_2() throws Exception {
        List<Integer> list1 = Arrays.asList();
        List<Integer> list2 = Arrays.asList(1, 2, 3);
        List<DiffChange<Integer>> diffChanges = new ListDiff<Integer>(null, list1, list2).calcDiffChanges();
        Assert.assertEquals(list2, apply(list1, diffChanges));
        Assert.assertEquals(3, diffChanges.size());
    }

    @Test
    public void testCalcDiffChanges_3() throws Exception {
        List<Integer> list1 = Arrays.asList(1, 2, 3);
        List<Integer> list2 = Arrays.asList(2, 3);
        List<DiffChange<Integer>> diffChanges = new ListDiff<Integer>(null, list1, list2).calcDiffChanges();
        Assert.assertEquals(list2, apply(list1, diffChanges));
        Assert.assertEquals(1, diffChanges.size());
    }

    @Test
    public void testCalcDiffChanges_4() throws Exception {
        List<Integer> list1 = Arrays.asList(1, 2, 3);
        List<Integer> list2 = Arrays.asList(1, 3);
        List<DiffChange<Integer>> diffChanges = new ListDiff<Integer>(null, list1, list2).calcDiffChanges();
        Assert.assertEquals(list2, apply(list1, diffChanges));
        Assert.assertEquals(1, diffChanges.size());
    }

    @Test
    public void testCalcDiffChanges_5() throws Exception {
        List<Integer> list1 = Arrays.asList(1, 2, 3);
        List<Integer> list2 = Arrays.asList(1, 2);
        List<DiffChange<Integer>> diffChanges = new ListDiff<Integer>(null, list1, list2).calcDiffChanges();
        Assert.assertEquals(list2, apply(list1, diffChanges));
        Assert.assertEquals(1, diffChanges.size());
    }

    @Test
    public void testCalcDiffChanges_6() throws Exception {
        List<Integer> list1 = Arrays.asList(1, 2, 3);
        List<Integer> list2 = Arrays.asList(1, 3, 4);
        List<DiffChange<Integer>> diffChanges = new ListDiff<Integer>(null, list1, list2).calcDiffChanges();
        Assert.assertEquals(list2, apply(list1, diffChanges));
        Assert.assertEquals(2, diffChanges.size());
    }

    @Test
    public void testCalcDiffChanges_7() throws Exception {
        List<Integer> list1 = Arrays.asList(1, 2, 3);
        List<Integer> list2 = Arrays.asList(3, 2, 1);
        List<DiffChange<Integer>> diffChanges = new ListDiff<Integer>(null, list1, list2).calcDiffChanges();
        Assert.assertEquals(list2, apply(list1, diffChanges));
        Assert.assertEquals(2, diffChanges.size());
    }

    @Test
    public void testCalcDiffChanges_8() throws Exception {
        List<Integer> list1 = Arrays.asList(1, 2, 3, 4, 5, 6);
        List<Integer> list2 = Arrays.asList(2, 3, 4, 6, 7, 8);
        List<DiffChange<Integer>> diffChanges = new ListDiff<Integer>(null, list1, list2).calcDiffChanges();
        Assert.assertEquals(list2, apply(list1, diffChanges));
        Assert.assertEquals(4, diffChanges.size());
    }

    @Test
    public void testCalcDiffChanges_9() throws Exception {
        List<Integer> list1 = Arrays.asList(1, 1, 1, 1);
        List<Integer> list2 = Arrays.asList(2, 2, 2, 2);
        List<DiffChange<Integer>> diffChanges = new ListDiff<Integer>(null, list1, list2).calcDiffChanges();
        Assert.assertEquals(list2, apply(list1, diffChanges));
        Assert.assertEquals(4, diffChanges.size());
    }

    @Test
    public void testCalcDiffChanges_10() throws Exception {
        List<Integer> list1 = Arrays.asList(1, 1, 1, 1);
        List<Integer> list2 = Arrays.asList(2, 2, 2, 1);
        List<DiffChange<Integer>> diffChanges = new ListDiff<Integer>(null, list1, list2).calcDiffChanges();
        Assert.assertEquals(list2, apply(list1, diffChanges));
        Assert.assertEquals(6, diffChanges.size());
    }

    @Test
    public void testCalcDiffChanges_11() throws Exception {
        List<Integer> list1 = Arrays.asList(1, 1, 1, 1, 1, 1);
        List<Integer> list2 = Arrays.asList(2, 2, 2, 1, 1, 1);
        List<DiffChange<Integer>> diffChanges = new ListDiff<Integer>(null, list1, list2).calcDiffChanges();
        Assert.assertEquals(list2, apply(list1, diffChanges));
        Assert.assertEquals(6, diffChanges.size());
    }

    private static <E> List<E> apply(List<E> list1, List<DiffChange<E>> diffChanges) {
        List<E> result = new ArrayList<E>(list1);
        for (DiffChange<E> diffChange : diffChanges) {
            int index = diffChange.getIndex();
            switch (diffChange.getType()) {
                case delete:
                    result.remove(index);
                    LOG.info("removed elem at #{}", index);
                    break;
                case insert:
                    result.add(index, diffChange.getElement());
                    LOG.info("added '{}' at #{}", diffChange.getElement(), index);
                    break;
                case replace:
                    result.set(index, diffChange.getElement());
                    LOG.info("replaced '{}' at #{}", diffChange.getElement(), index);
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
        return result;
    }

}
