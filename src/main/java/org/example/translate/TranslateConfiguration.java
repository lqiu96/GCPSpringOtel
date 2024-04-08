package org.example.translate;

import com.google.api.gax.tracing.MetricsTracerFactory;
import com.google.api.gax.tracing.OpenTelemetryMetricsRecorder;
import com.google.cloud.translate.v3.TranslationServiceClient;
import com.google.cloud.translate.v3.stub.TranslationServiceStubSettings;
import io.opentelemetry.api.OpenTelemetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class TranslateConfiguration {

  @Value("${gcp.project-id}")
  private String projectId;

  private final OpenTelemetry openTelemetry;

  public TranslateConfiguration(OpenTelemetry openTelemetry) {
    this.openTelemetry = openTelemetry;
  }

  @Bean
  public TranslationServiceClient translationServiceClient() throws IOException {
    OpenTelemetryMetricsRecorder recorder =
        new OpenTelemetryMetricsRecorder(openTelemetry, "translate");
    TranslationServiceStubSettings translationServiceStubSettings =
        TranslationServiceStubSettings.newBuilder()
            .setTracerFactory(new MetricsTracerFactory(recorder))
            .build();
    return TranslationServiceClient.create(translationServiceStubSettings.createStub());
  }
}
