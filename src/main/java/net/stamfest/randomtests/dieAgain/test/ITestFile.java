package net.stamfest.randomtests.dieAgain.test;


import net.stamfest.randomtests.dieAgain.util.StandardTest;

import java.io.IOException;
import java.util.Random;
/**
 *
 * @author Christian Schürhoff
 * @see FunctionalInterface
 *
 */
@FunctionalInterface
public interface ITestFile {
    /**
     *
     * @param rng
     * The Source of randomness to test.
     * @param parameters
     * Each complete run of a single Test uses one of the given parameters.
     */
    void runTestOn(Random rng, String path, StandardTest...parameters) throws IOException;
}