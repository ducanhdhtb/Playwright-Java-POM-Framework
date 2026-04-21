package utils;

import io.qameta.allure.Step;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.StringJoiner;

@Aspect
public class StepLoggerAspect {

    private static final Logger logger = LogManager.getLogger(StepLoggerAspect.class);

    /**
     * Defines a pointcut that matches the execution of any method annotated with @Step.
     */
    @Pointcut("@annotation(io.qameta.allure.Step)")
    public void stepAnnotation() {
    }

    /**
     * This advice runs "around" the method execution matched by the pointcut.
     * It logs the step description before the method is executed.
     *
     * @param joinPoint The join point that gives access to method metadata.
     * @return The result of the original method call.
     * @throws Throwable If the original method throws an exception.
     */
    @Around("stepAnnotation()")
    public Object logStep(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Step stepAnnotation = method.getAnnotation(Step.class);

        // Get the step description from the annotation
        String stepDescription = stepAnnotation.value();

        // Replace placeholders like {0}, {1} with actual argument values
        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                String placeholder = "{" + i + "}";
                if (stepDescription.contains(placeholder)) {
                    // Be careful with sensitive data like passwords
                    String argValue = (signature.getParameterNames()[i].toLowerCase().contains("password"))
                            ? "[PROTECTED]"
                            : String.valueOf(args[i]);
                    stepDescription = stepDescription.replace(placeholder, "'" + argValue + "'");
                }
            }
        }
        
        logger.info("STEP: " + stepDescription);

        // Create a real Allure step without relying on Allure's AspectJ aspects.
        return Allure.step(stepDescription, (Allure.ThrowableRunnable<Object>) joinPoint::proceed);
    }
}
