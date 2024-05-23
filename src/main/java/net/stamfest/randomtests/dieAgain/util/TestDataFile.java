package net.stamfest.randomtests.dieAgain.util;

import net.stamfest.randomtests.dieAgain.test.ITest;
import net.stamfest.randomtests.dieAgain.test.ITestFile;

public class TestDataFile {
    /**
     * The name of the test.
     */
    private String name;
    /**
     * The short name of the test, also its call name.
     */
    private String shortName = "runTestOn";
    /**
     * The test description;
     */
    private String description;
    /**
     * Tests default value for {@link StandardTest#pSamples}.
     */
    private int pSamplesStandard;
    /**
     * Tests default value for {@link StandardTest#tSamples}.
     */
    private int tSamplesStandard;
    /**
     * Number of independent statistics generated per run.
     */
    private int nkps;
    /**
     * An instance of the class implementing the test.
     */
    private ITestFile testMethod;
    /**
     * Extra information for the test.
     */
    private double[] extra;

    public TestDataFile() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getpSamplesStandard() {
        return pSamplesStandard;
    }

    public void setpSamplesStandard(int pSamplesStandard) {
        this.pSamplesStandard = pSamplesStandard;
    }

    public int gettSamplesStandard() {
        return tSamplesStandard;
    }

    public void settSamplesStandard(int tSamplesStandard) {
        this.tSamplesStandard = tSamplesStandard;
    }

    public int getNkps() {
        return nkps;
    }

    public void setNkps(int nkps) {
        this.nkps = nkps;
    }

    public ITestFile getTestMethod() {
        return testMethod;
    }

    public void setTestMethod(ITestFile testMethod) {
        this.testMethod = testMethod;
    }

    public double[] getExtra() {
        return extra;
    }

    public void setExtra(double... extra) {
        this.extra = extra;
    }

    public StandardTest createTest(int pSamples, int tSamples) {
        StandardTest ret = new StandardTest();
        ret.setpSamples(pSamples);
        ret.settSamples(tSamples);
        ret.setNkps(nkps);
        ret.setXyz(extra);
        return ret;
    }

    public StandardTest createTest(int pSamples) {
        return createTest(pSamples, tSamplesStandard);
    }

    public StandardTest createTest() {
        return createTest(pSamplesStandard, tSamplesStandard);
    }

    public StandardTest[] createTests(int[] pSamples, int[] tSamples) {
        int count=Math.min(pSamples.length, tSamples.length);
        StandardTest[] ret=new StandardTest[count];
        for (int i=0;i<count;i++) {
            ret[i]=createTest(pSamples[i], tSamples[i]);
        }
        return ret;
    }
}