package com.teamdelegation.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.teamdelegation.model.Member;
import com.teamdelegation.model.SkillProfile;
import com.teamdelegation.web.ScenarioRepository;
import com.teamdelegation.web.form.MemberForm;
import com.teamdelegation.web.util.TextParser;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/members")
public class MemberController {

    private final ScenarioRepository repository;

    public MemberController(ScenarioRepository repository) {
        this.repository = repository;
    }

    @ModelAttribute("memberForm")
    public MemberForm memberForm() {
        return new MemberForm();
    }

    @GetMapping
    public String show(Model model) {
        model.addAttribute("members", repository.getMembers());
        return "members";
    }

    @PostMapping
    public String createMember(@Valid @ModelAttribute("memberForm") MemberForm form,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("members", repository.getMembers());
            return "members";
        }

        SkillProfile skills = TextParser.parseSkills(form.getSkillsRaw());
        double performance = form.getPerformance();
        double growth = form.getGrowth();
        Member member = new Member(form.getName(), skills, performance, growth);
        TextParser.applyProjects(form.getProjectsRaw(), member::assignProject);
        repository.addMember(member);

        redirectAttributes.addFlashAttribute("message", "Member added to the pool.");
        return "redirect:/members";
    }
}

