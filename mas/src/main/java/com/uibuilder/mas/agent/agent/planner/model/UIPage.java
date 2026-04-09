package com.uibuilder.mas.agent.agent.planner.model;


import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class UIPage {
    String name;
    String route;
    List<PlanStep> steps;
}
