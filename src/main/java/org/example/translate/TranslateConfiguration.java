package org.example.translate;

import com.google.api.gax.tracing.MetricsTracerFactory;
import com.google.api.gax.tracing.OpenTelemetryMetricsRecorder;
import com.google.cloud.opentelemetry.metric.GoogleCloudMetricExporter;
import com.google.cloud.opentelemetry.metric.MetricConfiguration;
import com.google.cloud.translate.v3.TranslationServiceClient;
import com.google.cloud.translate.v3.stub.TranslationServiceStubSettings;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.MetricExporter;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.sdk.resources.Resource;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TranslateConfiguration {

  @Value("${gcp.project-id}")
  private String projectId;

  @Bean
  public TranslationServiceClient translationServiceClient() throws IOException {
    MetricExporter metricExporter =
        GoogleCloudMetricExporter.createWithConfiguration(
            MetricConfiguration.builder()
                .setProjectId(projectId)
                .setPrefix("custom.googleapis.com")
                .build());

    PeriodicMetricReader metricReader =
        PeriodicMetricReader.builder(metricExporter)
            .setInterval(java.time.Duration.ofSeconds(5))
            .build();
    Resource resource = Resource.builder().build();
    SdkMeterProvider sdkMeterProvider =
        SdkMeterProvider.builder().registerMetricReader(metricReader).setResource(resource).build();

    OpenTelemetry openTelemetry =
        OpenTelemetrySdk.builder().setMeterProvider(sdkMeterProvider).build();
    OpenTelemetryMetricsRecorder recorder =
        new OpenTelemetryMetricsRecorder(openTelemetry, "translate");
    TranslationServiceStubSettings translationServiceStubSettings =
        TranslationServiceStubSettings.newBuilder()
            .setTracerFactory(new MetricsTracerFactory(recorder))
            .build();
    return TranslationServiceClient.create(translationServiceStubSettings.createStub());
  }
}
