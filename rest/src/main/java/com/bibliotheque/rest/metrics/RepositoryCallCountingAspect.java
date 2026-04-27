package com.bibliotheque.rest.metrics;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RepositoryCallCountingAspect {

    @Before("execution(* com.bibliotheque.rest..repositories..*(..))")
    public void countRepositoryCall() {
        RequestMetrics.incInternalCall();
    }
}

