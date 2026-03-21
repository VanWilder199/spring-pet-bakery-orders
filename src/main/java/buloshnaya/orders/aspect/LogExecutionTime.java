package buloshnaya.orders.aspect;

import buloshnaya.orders.annotation.MeasureExecution;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogExecutionTime {
    private static final Logger logger = LoggerFactory.getLogger(LogExecutionTime.class);


    @Around("@annotation(measureExecution)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint, MeasureExecution measureExecution) throws Throwable {
        long start = System.nanoTime();

        Object proceed = joinPoint.proceed();

        long executionTime = System.nanoTime() - start;


        if (measureExecution.logResult()) {
            String unit = measureExecution.unit();
            long result = switch (unit) {
                case "ms" -> executionTime / 1_000_000;
                case "us", "μs" -> executionTime / 1_000;
                case "ns" -> executionTime;
                case "s" -> executionTime / 1_000_000_000;
                default -> throw new IllegalArgumentException("Unknown unit: " + unit);
            };

            logger.info("{} executed in {} {}", joinPoint.getSignature(), result, unit);
        }


        return proceed;
    }
}
