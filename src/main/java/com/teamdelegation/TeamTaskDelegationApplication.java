package com.teamdelegation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.teamdelegation.engine.AssignmentEngine;

@SpringBootApplication
public class TeamTaskDelegationApplication {

    public static void main(String[] args) {
        SpringApplication.run(TeamTaskDelegationApplication.class, args);
    }

    @Bean
    public AssignmentEngine assignmentEngine() {
        return new AssignmentEngine(12.0, AssignmentEngine.Weights.balanced());
    }
}

