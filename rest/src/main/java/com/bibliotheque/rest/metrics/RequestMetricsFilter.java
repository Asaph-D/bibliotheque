package com.bibliotheque.rest.metrics;

import java.io.IOException;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestMetricsFilter extends OncePerRequestFilter {

    private final EntityManagerFactory entityManagerFactory;

    public RequestMetricsFilter(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // On ne mesure que l'API (pas les fichiers statiques)
        return path == null || !path.startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        RequestMetrics.reset();

        Statistics stats = null;
        try {
            SessionFactory sf = entityManagerFactory.unwrap(SessionFactory.class);
            if (sf != null) {
                stats = sf.getStatistics();
                stats.setStatisticsEnabled(true);
                stats.clear();
            }
        } catch (Exception ignored) {
            // Si Hibernate n'est pas accessible, on continue sans métriques DB.
        }

        ContentCachingResponseWrapper wrapper = new ContentCachingResponseWrapper(response);
        long t0 = System.nanoTime();
        try {
            filterChain.doFilter(request, wrapper);
        } finally {
            long elapsedMs = Math.round((System.nanoTime() - t0) / 1_000_000.0);
            long dbStatements = stats != null ? stats.getPrepareStatementCount() : -1;

            wrapper.setHeader("X-Internal-Calls", Long.toString(RequestMetrics.internalCalls()));
            wrapper.setHeader("X-Db-Statements", Long.toString(dbStatements));
            wrapper.setHeader("X-Elapsed-Ms", Long.toString(elapsedMs));

            RequestMetrics.clear();
            wrapper.copyBodyToResponse();
        }
    }
}

