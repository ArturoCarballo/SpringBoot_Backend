package com.springboot.connectmate.controllers;

import com.amazonaws.services.connect.model.*;
import com.amazonaws.services.connect.model.Queue;
import com.springboot.connectmate.services.AmazonConnectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/amazon-connect")
@Tag(
        name = "Amazon Connect Queues REST API",
        description = "CRUD REST API for Amazon Connect Queues"
)
public class ConnectQueueController {

    private final AmazonConnectService amazonConnectService;

    @Autowired
    public ConnectQueueController(AmazonConnectService amazonConnectService) {
        this.amazonConnectService = amazonConnectService;
    }


    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = QueueSummary.class))),
            description = "List of queues for a given instance fetched successfully."
    )
    @Operation(
            summary = "Get all queues",
            description = "Get Amazon Connect queues by instance ID"
    )
    @GetMapping("/instances/{instanceId}/queues")
    public ResponseEntity<List<QueueSummary>> listQueues(@PathVariable(name = "instanceId") String instanceId) {
        return ResponseEntity.ok(amazonConnectService.listQueues(instanceId));
    }

    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = Queue.class))),
            description = "Get a queue's info."
    )
    @Operation(
            summary = "Gets the data of a particular queue.",
            description = "Gets the data of a particular queue by instance ID and queue ID."
    )
    @GetMapping("/instances/{instanceId}/queues/{queueId}/description")
    public ResponseEntity<Queue> describeQueue(@PathVariable(name = "instanceId") String instanceId, @PathVariable(name = "queueId") String queueId) {
        return ResponseEntity.ok(amazonConnectService.describeQueue(instanceId, queueId));
    }

    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = UserData.class))),
            description = "List of all current data of agents, queues, and contacts."
    )
    @Operation(
            summary = "Get the current data of agents, queues, and contacts.",
            description = "Get the current data of agents, queues, and contacts by instance ID"
    )
    @GetMapping("/instances/{instanceId}/current-user-data")
    public ResponseEntity<List<UserData>> getCurrentUserData(@PathVariable(name = "instanceId") String instanceId) {
        return ResponseEntity.ok(amazonConnectService.getCurrentUserData(instanceId));
    }


    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = Map.class))),
            description = "Get all the users and contact info for all the queues."
    )
    @Operation(
            summary = "Gets the user and contact data of all queues.",
            description = "Gets the users and contacts of all queues."
    )
    @GetMapping("/instances/{instanceId}/queue-users")
    public Map<String, Map<String, Object>> queueUserCounts(@PathVariable(name = "instanceId") String instanceId) {
        // Move to a Service
        ResponseEntity<List<UserData>> response = getCurrentUserData(instanceId);
        List<UserData> userDataList = response.getBody();
        Map<String, Map<String, Object>> queueInfo = new HashMap<>();

        if (userDataList != null) {
            for (UserData userData : userDataList) {
                String userId = userData.getUser().getId();
                for (AgentContactReference contact : userData.getContacts()) {
                    String queueId = contact.getQueue().getId();
                    queueInfo.putIfAbsent(queueId, new HashMap<>());
                    queueInfo.get(queueId).putIfAbsent("users", new HashSet<>());
                    queueInfo.get(queueId).putIfAbsent("contactCount", 0);

                    Set<String> users = (Set<String>) queueInfo.get(queueId).get("users");
                    users.add(userId);

                    int count = (int) queueInfo.get(queueId).get("contactCount");
                    queueInfo.get(queueId).put("contactCount", count + 1);
                }
            }
        }

        return queueInfo;
    }
}
