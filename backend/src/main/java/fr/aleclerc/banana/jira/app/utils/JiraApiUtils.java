package fr.aleclerc.banana.jira.app.utils;

import fr.aleclerc.banana.entities.Project;
import fr.aleclerc.banana.jira.api.pojo.Board;

import java.util.UUID;

public class JiraApiUtils {

    private JiraApiUtils() {

    }

    public static Project convert(Board p) {
        Project project = new Project();
        project.setId(UUID.randomUUID());
        project.setName(p.getName());
        project.setJiraId(p.getId().toString());
        return project;
    }
}
