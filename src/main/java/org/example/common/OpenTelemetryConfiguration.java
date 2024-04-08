package org.example.common;

import com.google.cloud.opentelemetry.metric.GoogleCloudMetricExporter;
import com.google.cloud.opentelemetry.metric.MetricConfiguration;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.MetricExporter;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.sdk.resources.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenTelemetryConfiguration {

  @Value("${gcp.project-id}")
  private String projectId;

  @Bean
  public OpenTelemetry openTelemetry() {
    MetricExporter metricExporter =
            GoogleCloudMetricExporter.createWithConfiguration(
                    MetricConfiguration.builder()
                            .setProjectId(projectId)
                            .setPrefix("custom.googleapis.com")
                            .build());

    PeriodicMetricReader metricReader =
            PeriodicMetricReader.builder(metricExporter)
                    .setInterval(java.time.Duration.ofSeconds(20))
                    .build();
    Resource resource = Resource.builder().build();
    SdkMeterProvider sdkMeterProvider =
            SdkMeterProvider.builder().registerMetricReader(metricReader).setResource(resource).build();

    return OpenTelemetrySdk.builder().setMeterProvider(sdkMeterProvider).build();
  }
}
