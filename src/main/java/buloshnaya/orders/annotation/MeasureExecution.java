package buloshnaya.orders.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(java.lang.annotation.ElementType.METHOD)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface MeasureExecution {
    String unit() default "ms";
    boolean logResult() default true;
}
