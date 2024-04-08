package org.example.spanner;

import com.google.api.gax.tracing.MetricsTracerFactory;
import com.google.api.gax.tracing.OpenTelemetryMetricsRecorder;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import io.opentelemetry.api.OpenTelemetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpannerConfiguration {

  @Value("${gcp.project-id}")
  private String projectId;

  private final OpenTelemetry openTelemetry;

  public SpannerConfiguration(OpenTelemetry openTelemetry) {
    this.openTelemetry = openTelemetry;
  }

  @Bean
  public Spanner spanner() {
    OpenTelemetryMetricsRecorder recorder =
        new OpenTelemetryMetricsRecorder(openTelemetry, "spanner");
    SpannerOptions options =
        SpannerOptions.newBuilder()
            .setApiTracerFactory(new MetricsTracerFactory(recorder))
            .setProjectId(projectId)
            .build();
    return options.getService();
  }
}
