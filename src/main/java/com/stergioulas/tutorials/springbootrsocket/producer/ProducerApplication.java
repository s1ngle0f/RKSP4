package com.stergioulas.tutorials.springbootrsocket.producer;

import io.rsocket.util.DefaultPayload;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Flow;
import java.util.stream.Stream;

@SpringBootApplication
public class ProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProducerApplication.class, args);
    }

}


@Data
@NoArgsConstructor
@AllArgsConstructor
class GreetingsRequest {
    private String name;
}


@Data
@NoArgsConstructor
@AllArgsConstructor
class GreetingsResponse {
    private String greeting;
}

@Controller
class GreetingsController {
    @MessageMapping("fire-and-forget")
    Mono<Void> fireAndForget(GreetingsRequest request) {
        return Mono.empty();
    }

    @MessageMapping("request-stream")
    Flux<GreetingsResponse> requestStream(GreetingsRequest request) {
        return Flux.fromStream(Stream.generate(
                () -> new GreetingsResponse("request-stream " + request.getName())
        )).delayElements(Duration.ofSeconds(1));
    }

    @MessageMapping("request-channel")
    Flux<GreetingsResponse> requestChannel(Publisher<GreetingsRequest> requests) {
        return Flux.from(requests)
                .flatMap(request -> Flux.fromStream(
                Stream.generate(
                        () -> new GreetingsResponse("request-channel " + request.getName())
                )
        )).doOnNext(System.out::println);
    }

    @MessageMapping("request-response")
    Mono<GreetingsResponse> requestResponse(GreetingsRequest request) {
        return Mono.just(new GreetingsResponse("request-response " + request.getName()));
    }

}


