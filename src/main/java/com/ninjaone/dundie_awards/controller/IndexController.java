package com.ninjaone.dundie_awards.controller;

import com.ninjaone.dundie_awards.cache.AwardsCache;
import com.ninjaone.dundie_awards.messages.MessageBroker;
import com.ninjaone.dundie_awards.service.ActivityService;
import com.ninjaone.dundie_awards.service.EmployeeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/")
public class IndexController {

    private final EmployeeService employeeService;
    private final ActivityService activityService;
    private final MessageBroker messageBroker;
    private final AwardsCache awardsCache;

    public IndexController(EmployeeService employeeService, ActivityService activityService,
                           MessageBroker messageBroker, AwardsCache awardsCache) {
        this.employeeService = employeeService;
        this.activityService = activityService;
        this.messageBroker = messageBroker;
        this.awardsCache = awardsCache;
    }

    @GetMapping()
    public String getIndex(Model model) {
        model.addAttribute("employees", employeeService.getAllEmployees());
        model.addAttribute("activities", activityService.getAllActivities());
        model.addAttribute("queueMessages", messageBroker.getMessages());
        model.addAttribute("totalDundieAwards", awardsCache.getTotalAwards());
        return "index";
    }
}