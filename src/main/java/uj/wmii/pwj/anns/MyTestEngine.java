package uj.wmii.pwj.anns;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyTestEngine {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Error: No test class specified provided.");
            System.exit(-1);
        }

        printSimpleBanner();

        String targetClass = args[0].trim();
        System.out.println("Analyzing class: " + targetClass + "...\n");

        MyTestEngine engine = new MyTestEngine();
        engine.execute(targetClass);
    }

    private static void printSimpleBanner() {
        System.out.println("========================================");
        System.out.println("|        T E S T   E N G I N E         |");
        System.out.println("========================================");
    }

    public void execute(String className) {
        Object testInstance = createInstance(className);
        if (testInstance == null) return;

        Method[] allMethods = testInstance.getClass().getDeclaredMethods();
        List<Method> testMethods = new ArrayList<>();
        
        for (Method m : allMethods) {
            if (m.isAnnotationPresent(MyTest.class)) {
                testMethods.add(m);
            }
        }

        int totalPass = 0;
        int totalFail = 0;
        int totalError = 0;
        int totalTests = 0;

        for (Method method : testMethods) {
            System.out.println(">>> Method: " + method.getName());
            
            MyTest ann = method.getAnnotation(MyTest.class);
            String[] parameters = ann.params();
            String expected = ann.expectedResult();

            if (parameters.length == 0) {
                TestResult status = invokeAndCheck(method, testInstance, null, expected);
                if (status == TestResult.PASS) totalPass++;
                else if (status == TestResult.FAIL) totalFail++;
                else totalError++;
                totalTests++;
            } else {
                for (String param : parameters) {
                    TestResult status = invokeAndCheck(method, testInstance, param, expected);
                    if (status == TestResult.PASS) totalPass++;
                    else if (status == TestResult.FAIL) totalFail++;
                    else totalError++;
                    totalTests++;
                }
            }
            System.out.println();
        }

        printSummary(totalTests, totalPass, totalFail, totalError);
    }

    private TestResult invokeAndCheck(Method method, Object instance, String arg, String expected) {
        String argDisplay = (arg == null) ? "no-args" : "\"" + arg + "\"";
        
        try {
            Object resultObj;
            if (arg == null) {
                resultObj = method.invoke(instance);
            } else {
                resultObj = method.invoke(instance, arg);
            }

            String actual = String.valueOf(resultObj);

            if (expected != null && !expected.isEmpty()) {
                if (actual.equals(expected)) {
                    System.out.printf("   [PASS] Arg: %-10s | Expected: %-10s | Actual: %s%n", argDisplay, expected, actual);
                    return TestResult.PASS;
                } else {
                    System.out.printf("   [FAIL] Arg: %-10s | Expected: %-10s | Actual: %s%n", argDisplay, expected, actual);
                    return TestResult.FAIL;
                }
            } else {
                System.out.printf("   [PASS] Arg: %-10s | Result: %s (No expectation set)%n", argDisplay, actual);
                return TestResult.PASS;
            }

        } catch (InvocationTargetException e) {
            System.out.printf("   [ERROR] Arg: %-10s | Exception: %s%n", argDisplay, e.getCause().getClass().getSimpleName());
            return TestResult.ERROR;
        } catch (Exception e) {
            e.printStackTrace();
            return TestResult.ERROR;
        }
    }

    private void printSummary(int total, int pass, int fail, int error) {
        System.out.println("----------------------------------------");
        System.out.println("TEST SUMMARY");
        System.out.printf("Total: %d | Pass: %d | Fail: %d | Error: %d%n", total, pass, fail, error);
        System.out.println("----------------------------------------");
    }

    private Object createInstance(String className) {
        try {
            return Class.forName(className).getConstructor().newInstance();
        } catch (Exception e) {
            System.out.println("Critical Error: Could not create instance of " + className);
            return null;
        }
    }
}