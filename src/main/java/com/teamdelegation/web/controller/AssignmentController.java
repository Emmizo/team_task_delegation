package com.teamdelegation.web.controller;

import com.teamdelegation.engine.AssignmentEngine;
import com.teamdelegation.model.AssignmentDecision;
import com.teamdelegation.model.ProjectDemand;
import com.teamdelegation.model.SkillProfile;
import com.teamdelegation.web.ScenarioRepository;
import com.teamdelegation.web.form.ProjectForm;
import com.teamdelegation.web.util.TextParser;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping({"/", "/assignment"})
public class AssignmentController {

    private final ScenarioRepository repository;
    private final AssignmentEngine engine;

    public AssignmentController(ScenarioRepository repository, AssignmentEngine engine) {
        this.repository = repository;
        this.engine = engine;
    }

    @ModelAttribute("projectForm")
    public ProjectForm projectForm() {
        return new ProjectForm();
    }

    @GetMapping
    public String showForm(Model model) {
        model.addAttribute("members", repository.getMembers());
        model.addAttribute("lastDecision", repository.getLastDecision());
        return "assignment";
    }

    @PostMapping
    public String evaluate(@Valid @ModelAttribute("projectForm") ProjectForm form,
                           BindingResult result,
                           Model model) {
        if (repository.getMembers().isEmpty()) {
            result.reject("members.empty", "Please add at least one member before assigning work.");
        }
        if (result.hasErrors()) {
            model.addAttribute("members", repository.getMembers());
            model.addAttribute("lastDecision", repository.getLastDecision());
            return "assignment";
        }

        SkillProfile requiredSkills = TextParser.parseSkills(form.getRequiredSkillsRaw());
        ProjectDemand demand = new ProjectDemand(
                form.getProjectName(),
                requiredSkills,
                form.getDurationWeeks(),
                TextParser.parseObjectives(form.getObjectivesRaw())
        );

        AssignmentDecision decision = engine.evaluate(demand, repository.getMembers());
        repository.setLastDecision(decision);

        model.addAttribute("decision", decision);
        return "result";
    }
}

