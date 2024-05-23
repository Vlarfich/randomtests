package net.stamfest.randomtests.dieAgain.util;

public class TestObserver implements Runnable {
	private StandardTest[] tests;

	public TestObserver() {
		super();
	}

	public StandardTest[] getTests() {
		return tests;
	}

	public void setTests(StandardTest... tests) {
		this.tests = tests;
		for (StandardTest test : tests) {
			for (int i = 0; i < test.getpValues().length; i++) {
				test.getpValues()[i] = -1;
			}
		}
	}

	@Override
	public void run() {
		long time = 0;
		for (StandardTest test : tests) {
			for (int i = 0; i < test.getpValues().length; i++) {
				if (test.getpValues()[i] == -1) {
					time = System.currentTimeMillis();
				}
				while (test.getpValues()[i] == -1) {
					Thread.onSpinWait();
				}
				System.out.println("P-Value generated in " + (System.currentTimeMillis() - time) + "ms.");
			}
		}
	}

}