package uj.wmii.pwj.anns;

public class MyBeautifulTestSuite {

    @MyTest
    public void testSoemthing() {
        System.out.println("I'm testing something!");
    }

    @MyTest(params = {"a param", "b param", "c param. Long, long C param."})
    public void testWithParam(String param) {
        System.out.printf("I was invoked with parameter: %s\n", param);
    }

    public void notATest() {
        System.out.println("I'm not a test.");
    }

    @MyTest
    public void imFailue() {
        System.out.println("I AM EVIL.");
        throw new NullPointerException();
    }

    @MyTest(expectedResult = "expectedValue")
    public String testExpectedFail() {
        return "wrongValue";
    }

    @MyTest(params = {"A", "B"}, expectedResult = "OK")
    public String testParamWithExpected(String param) {
        return "OK";
    }

    @MyTest
    public void testError() {
        System.out.println("Division by 0");
        int x = 5 / 0;
    }

    @MyTest(expectedResult = "67")
    public int testReturnIntPass() {
        return 67;
    }

    @MyTest(expectedResult = "anything")
    public String testReturnStringFail() {
        return "something";
    }

    @MyTest
    public void testThrowsException() {
        throw new IllegalStateException("This is an exception.");
    }

    @MyTest(expectedResult = "true")
    public boolean testBooleanPass() {
        return true;
    }
}