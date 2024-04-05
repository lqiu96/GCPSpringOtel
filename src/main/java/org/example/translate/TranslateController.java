package org.example.translate;

import com.google.cloud.translate.v3.LocationName;
import com.google.cloud.translate.v3.TranslateTextRequest;
import com.google.cloud.translate.v3.TranslateTextResponse;
import com.google.cloud.translate.v3.TranslationServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/translate")
public class TranslateController {
  @Value("${gcp.project-id}")
  private String projectId;

  private final TranslationServiceClient translationServiceClient;

  public TranslateController(TranslationServiceClient translationServiceClient) {
    this.translationServiceClient = translationServiceClient;
  }

  @GetMapping
  public String translate() {
    return "hello-translate";
  }

  @GetMapping(path = "/{text}", produces = "application/json")
  public String translateText(@PathVariable String text) {
    LocationName locationName = LocationName.of(projectId, "global");
    TranslateTextRequest translateTextRequest =
        TranslateTextRequest.newBuilder()
            .setParent(locationName.toString())
            .setTargetLanguageCode("es")
            .addContents(text)
            .build();
    TranslateTextResponse response = translationServiceClient.translateText(translateTextRequest);
    return response.getTranslations(0).getTranslatedText();
  }
}
