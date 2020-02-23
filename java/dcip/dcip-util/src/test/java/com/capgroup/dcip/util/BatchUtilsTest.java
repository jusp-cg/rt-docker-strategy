package com.capgroup.dcip.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class BatchUtilsTest {

    @Test
    public void chunkLessThan10Test() {
        List<List<Long>> result =
                BatchUtils.chunk(LongStream.range(0, 5).boxed().collect(Collectors.toList()), 10).collect(Collectors.toList());

        Assert.assertEquals(1, result.size());
        Assert.assertEquals(5, result.get(0).size());
    }

    @Test
    public void chunkGreaterThan10Test() {
        List<List<Long>> result =
                BatchUtils.chunk(LongStream.range(0, 15).boxed().collect(Collectors.toList()), 10).collect(Collectors.toList());

        Assert.assertEquals(2, result.size());
        Assert.assertEquals(10, result.get(0).size());
        Assert.assertEquals(5, result.get(1).size());
    }

    @Test
    public void chunkEquals10Test() {
        List<List<Long>> result =
                BatchUtils.chunk(LongStream.range(0, 10).boxed().collect(Collectors.toList()), 10).collect(Collectors.toList());

        Assert.assertEquals(1, result.size());
        Assert.assertEquals(10, result.get(0).size());
    }

    @Test
    public void chunkEquals20Test() {
        List<List<Long>> result =
                BatchUtils.chunk(LongStream.range(0, 20).boxed().collect(Collectors.toList()), 10).collect(Collectors.toList());

        Assert.assertEquals(2, result.size());
        Assert.assertEquals(10, result.get(0).size());
        Assert.assertEquals(10, result.get(1).size());
    }

}
