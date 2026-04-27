package com.bibliotheque.graphql.metrics;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RepositoryCallCountingAspect {

    @Before("execution(* com.bibliotheque.graphql..repositories..*(..))")
    public void countRepositoryCall() {
        RequestMetrics.incInternalCall();
    }
}

