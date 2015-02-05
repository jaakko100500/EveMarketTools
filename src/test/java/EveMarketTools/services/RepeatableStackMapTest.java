package EveMarketTools.services;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class RepeatableStackMapTest {
    @Test
    public void addingToEmptyShouldWork() {
        RepeatableStackMap<String, Long> queue = new RepeatableStackMap<>();
        queue.put("entries", 100L, 2L);
        List<QueueEntry<Long>> entries = queue.take("entries", 1L);

        Assert.assertTrue(entries.size() == 1);

    }

    @Test
    public void addingOnceTakingMultipleShouldWork() {
        RepeatableStackMap<String, Long> queue = new RepeatableStackMap<>();
        queue.put("entries", 6L, 2L);

        Assert.assertTrue(queue.take("entries", 1L).size() == 1);
        Assert.assertTrue(queue.take("entries", 1L).size() == 1);
        Assert.assertTrue(queue.take("entries", 1L).size() == 1);
        Assert.assertTrue(queue.take("entries", 1L).size() == 1);
        Assert.assertTrue(queue.take("entries", 1L).size() == 1);
    }

    @Test
    public void addingTwiceTakingMultipleShouldWork() {
        RepeatableStackMap<String, Long> queue = new RepeatableStackMap<>();
        queue.put("entries", 5L, 2L);
        queue.put("entries", 4L, 2L);

        Assert.assertTrue(queue.take("entries", 2L).size() == 1);
        Assert.assertTrue(queue.take("entries", 2L).size() == 1);
        Assert.assertTrue(queue.take("entries", 2L).size() == 2);
        Assert.assertTrue(queue.take("entries", 2L).size() == 1);
        Assert.assertTrue(queue.take("entries", 1L).size() == 1);
    }

    @Test
    public void addingTwiceTakingMultipleBigTakesShouldWork() {
        RepeatableStackMap<String, Long> queue = new RepeatableStackMap<>();
        queue.put("key", 5L, 1L);
        queue.put("key", 4L, 2L);

        List<QueueEntry<Long>> firstTake = queue.take("key", 6L);
        Assert.assertTrue(firstTake.size() == 2);
        Assert.assertTrue(firstTake.get(0).amount == 5L);
        Assert.assertTrue(firstTake.get(0).object.equals(1L));
        Assert.assertTrue(firstTake.get(1).amount == 1L);
        Assert.assertTrue(firstTake.get(1).object.equals(2L));
        Assert.assertTrue(queue.take("key", 2L).size() == 1);
        Assert.assertTrue(queue.take("key", 1L).size() == 1);
        Assert.assertTrue(queue.take("key", 1L).size() == 0);
    }

    @Test
    public void addingTwiceTakingTooMuchShouldWork() {
        RepeatableStackMap<String, Long> queue = new RepeatableStackMap<>();
        queue.put("key", 10L, 1L);
        queue.put("key", 20L, 2L);

        List<QueueEntry<Long>> firstTake = queue.take("key", 40L);
        Assert.assertTrue(firstTake.size() == 2);
        Assert.assertTrue(firstTake.get(0).amount == 10L);
        Assert.assertTrue(firstTake.get(1).amount == 20L);

        List<QueueEntry<Long>> secondTake = queue.take("key", 40L);
        Assert.assertTrue(secondTake.size() == 0);
    }
}
