package org.cloudifysource.rest.events.cache;

import static com.gigaspaces.log.LogEntryMatchers.regex;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.cloudifysource.dsl.internal.CloudifyConstants;
import org.cloudifysource.dsl.rest.response.ServiceDeploymentEvent;
import org.cloudifysource.dsl.rest.response.ServiceDeploymentEvents;
import org.openspaces.admin.Admin;
import org.openspaces.admin.gsc.GridServiceContainer;
import org.openspaces.admin.gsc.GridServiceContainers;
import org.openspaces.admin.pu.ProcessingUnit;
import org.openspaces.admin.pu.ProcessingUnitInstance;
import org.openspaces.admin.zone.Zone;

import com.gigaspaces.log.LogEntry;
import com.gigaspaces.log.LogEntryMatcher;

/**
 * Created with IntelliJ IDEA.
 * User: elip
 * Date: 5/16/13
 * Time: 10:47 AM
 * To change this template use File | Settings | File Templates.
 */
public class EventsUtils {

    private static final String USM_EVENT_LOGGER_NAME = ".*.USMEventLogger.*";

    public static ServiceDeploymentEvent logToEvent(final LogEntry logEntry,
                                                     final String hostName,
                                                     final String hostAddress) {
        String text = logEntry.getText();
        String textWithoutLogger = text.split(" - ")[1];
        String actualEvent = textWithoutLogger.substring(textWithoutLogger.indexOf(".") + 1);
        ServiceDeploymentEvent event = new ServiceDeploymentEvent();
        event.setDescription("[" + hostName + "/" + hostAddress + "] - " + actualEvent);
        return event;
    }

    public static LogEntryMatcher createMatcher(){
        final String regex = MessageFormat.format(USM_EVENT_LOGGER_NAME, new Object() {
        });
        return regex(regex);
    }

    public static GridServiceContainers getContainersForDeployment(final ProcessingUnit pu) {
        Zone zone = pu.getAdmin().getZones().getByName(pu.getName());
        if (zone == null) {
            return null;
        } else {
            return zone.getGridServiceContainers();
        }
    }

    public static GridServiceContainers getContainersForDeployment(final String deploymentId, final Admin admin) {

        for (ProcessingUnit pu : admin.getProcessingUnits()) {
            String puDeploymentId = (String) pu.getBeanLevelProperties().getContextProperties().get(CloudifyConstants.CONTEXT_PROPERTY_DEPLOYMENT_ID);
            if (puDeploymentId == null) {
                throw new IllegalStateException("Service " + pu.getName() + " does not have a deployment id context property");
            }
            if (deploymentId.equals(puDeploymentId)) {
                return getContainersForDeployment(pu);
            }
        }
        return null;
    }

    public static ServiceDeploymentEvents extractDesiredEvents(final ServiceDeploymentEvents events,
                                                         final int from,
                                                         final int to) {

        ServiceDeploymentEvents desiredEvents = new ServiceDeploymentEvents();
        for (int i = from; i <= to; i++) {
            ServiceDeploymentEvent serviceDeploymentEvent = events.getEvents().get(i);
            if (serviceDeploymentEvent != null) {
                desiredEvents.getEvents().put(i, serviceDeploymentEvent);
            }
        }
        return desiredEvents;
    }

    public static boolean eventsPresent(final ServiceDeploymentEvents events,
                                  final int from,
                                  final int to) {

        for (int i = from; i <= to; i++) {
            if (events.getEvents().get(i) == null) {
                return false;
            }
        }

        return true;
    }

    public static String getThreadId() {
        return "[" + Thread.currentThread().getName() + "][" + Thread.currentThread().getId() + "] - ";
    }

}
