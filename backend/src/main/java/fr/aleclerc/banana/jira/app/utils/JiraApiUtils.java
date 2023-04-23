package fr.aleclerc.banana.jira.app.utils;

import fr.aleclerc.banana.entities.Project;
import fr.aleclerc.banana.entities.Sprint;
import fr.aleclerc.banana.jira.api.pojo.Board;
import fr.aleclerc.banana.jira.api.pojo.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class JiraApiUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(JiraApiUtils.class);

    private JiraApiUtils() {

    }

    public static Project convert(Board p) {
        Project project = new Project();
        project.setId(UUID.randomUUID());
        project.setName(p.getName());
        project.setJiraId(p.getId().toString());
        return project;
    }

    public static Sprint convert(fr.aleclerc.banana.jira.api.pojo.Sprint s) {
        Sprint sprint = new Sprint();
        sprint.setName(s.getName());
        sprint.setJiraId(s.getId().toString());
        if (s.getEndDate() != null) {
            sprint.setEnd(s.getEndDate().toInstant());
        }
        if (s.getStartDate() != null) {
            sprint.setStart(s.getStartDate().toInstant());
        }
        sprint.setBoardId(String.valueOf(s.getOriginBoardId()));
        return sprint;
    }


    private static boolean isItemToMatching(Item item, String sprintId) {
        if (item.getField() == null || item.getTo() == null) {
            return false;
        }
        boolean isFieldMatching = "Sprint".equals(item.getField());
        List<String> to = Arrays.asList(item.getTo().split(", "));
        boolean isToMatching = to.contains(sprintId);
        return isFieldMatching && isToMatching;

    }

    private static boolean isItemFromMatching(Item item, String sprintId) {
        if (item.getField() == null || item.getTo() == null) {
            return false;
        }
        boolean isFieldMatching = "Sprint".equals(item.getField());
        List<String> to = Arrays.asList(item.getTo().split(", "));
        boolean isToMatching = to.contains(sprintId);
        return isFieldMatching && isToMatching;

    }
}
