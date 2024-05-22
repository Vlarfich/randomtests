/*
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests.nist.test;

import java.io.IOException;
import junit.framework.Assert;
import net.stamfest.randomtests.bits.Bits;
import net.stamfest.randomtests.nist.OverlappingTemplateMatching;
import net.stamfest.randomtests.nist.Result;
import net.stamfest.randomtests.utils.IO;
import org.junit.Test;

/**
 *
 * @author Vlad Zhuravlev
 */
public class OverlappingTemplateMatchingTest {

    @Test
    public void example2_8_8() throws IOException {
        Bits bits = IO.readAscii(OverlappingTemplateMatchingTest.class.getResourceAsStream("/data.e"), 1000000);
        OverlappingTemplateMatching o = new OverlappingTemplateMatching(9);

        long startTime = System.currentTimeMillis();
        Result[] results = o.runTest(bits);
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        System.out.println(this.getClass().getSimpleName() + " time for " + bits.getLength() +
                " bits: " +timeElapsed + " ms");
        o.report(System.out, results);

        Assert.assertEquals(0.110434, results[0].getPValue(), 0.000001);
    }
}
