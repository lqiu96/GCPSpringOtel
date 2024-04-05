package org.example.spanner;

import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.admin.instance.v1.InstanceAdminClient;
import com.google.spanner.admin.instance.v1.Instance;
import com.google.spanner.admin.instance.v1.ProjectName;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/spanner")
public class SpannerController {
  @Value("${gcp.project-id}")
  private String projectId;

  private final Spanner spanner;

  public SpannerController(Spanner spanner) {
    this.spanner = spanner;
  }

  @GetMapping
  public String spanner() {
    return "hello";
  }

  @GetMapping(path = "/listInstances", produces = "application/json")
  public String listInstances() {
    try (InstanceAdminClient instanceAdminClient = spanner.createInstanceAdminClient()) {
      InstanceAdminClient.ListInstancesPagedResponse listInstancesPagedResponse =
          instanceAdminClient.listInstances(ProjectName.of(projectId));
      String instanceName = "";
      for (Instance instance : listInstancesPagedResponse.iterateAll()) {
        instanceName = instance.getName();
      }
      return instanceName;
    }
  }
}
