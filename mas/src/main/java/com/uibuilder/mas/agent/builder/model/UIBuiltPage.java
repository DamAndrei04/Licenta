package com.uibuilder.mas.agent.builder.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class UIBuiltPage {
    String name;
    String route;
    List<UIComponentNode> components;
}