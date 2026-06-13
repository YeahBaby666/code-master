package com.codemaster.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executors;

@Configuration
@EnableAsync // Obligatorio para que @Async funcione
public class AsyncConfig {

    @Bean
    public AsyncTaskExecutor applicationTaskExecutor() {
        // Forzamos a Spring a usar Virtual Threads (Java 21) para @Async
        // Esto permite manejar miles de llamadas a la IA concurrentemente
        // usando muy poca memoria RAM, ya que delegan la espera de red a la JVM.
        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
    }
}