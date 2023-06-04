package net.auzy.service.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.function.Consumer;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public class WebClientService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    public <T> Mono<T> makeRestCall(String url,
                                    MultiValueMap<String, String> headers,
                                    HttpMethod httpMethod,
                                    String body,
                                    Class<T> clazz) {

        return webClientBuilder.build()
                .method(httpMethod)
                .uri(url)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromValue(body))
                .retrieve()
                .bodyToMono(clazz);
    }

    public <T> Mono<T> makePostRestCall(String url,
                                        Consumer<HttpHeaders> headersConsumer,
                                        MultiValueMap<String, String> formData,
                                        Class<T> clazz) {

        return webClientBuilder
                .build()
                .post()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .headers(headersConsumer)
                .body(BodyInserters.fromFormData(Optional.ofNullable(formData).orElseGet(LinkedMultiValueMap::new)))
                .retrieve()
                .bodyToMono(clazz);
    }

    public <T> Mono<T> makeGetRestCall(String url,
                                        Consumer<HttpHeaders> headersConsumer,
                                        Class<T> clazz) {

        return webClientBuilder
                .build()
                .get()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .headers(headersConsumer)
                .retrieve()
                .bodyToMono(clazz);
    }

}
