package nl.ekholabs.reactive.controller;

import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import static java.util.logging.Logger.getLogger;
import static reactor.core.publisher.Flux.range;
import static reactor.core.publisher.Mono.fromCallable;
import static reactor.core.scheduler.Schedulers.elastic;
import static reactor.core.scheduler.Schedulers.parallel;

@RestController
public class ReactorController {

  private static final Logger log = getLogger(ReactorController.class.getName());

  @Value("${app.url:http://example.com}")
  private String url;

  private RestTemplate restTemplate = new RestTemplate();

  @RequestMapping("/parallelism/{max}")
  public Mono<Result> parallelism(@PathVariable final int max) {
    log.info("Handling /parallelism");

    // Concurrency hint in flatMap. It defines the # of
    // in-flight elements in the flatMap. Good for fine-tuning.
    final int concurrency = 10;

    return range(1, max) // <1>
      .log() //
      .flatMap( // <2>
        value -> fromCallable(() -> status(value)) // <3>
          .subscribeOn(elastic()), // <4>
        concurrency) // <5>
      .collect(() -> new Result(), (result, status) -> result.add(status)) // <6>
      .doOnSuccess((newResult) -> newResult.stop()); // <7>

    // <1> make 10 calls
    // <2> drop down to a new publisher to process in parallel
    // <3> blocking code here inside a Callable to defer execution
    // <4> subscribe to the slow publisher on a background thread
    // <5> concurrency hint in flatMap
    // <6> collect results and aggregate into a single object
    // <7> at the end stop the clock
  }

  @RequestMapping("/serial/{max}")
  public Mono<Result> serial(@PathVariable final int max) {
    log.info("Handling /serial");
    return range(1, max) // <1>
      .log() //
      .map( // <2>
        (value) -> status(value)) // <3>
      .collect(() -> new Result(), (result, status) -> result.add(status)) // <4>
      .doOnSuccess((newResult) -> newResult.stop()) // <5>
      .subscribeOn(parallel()); // <6>

    // <1> make 10 calls
    // <2> stay in the same publisher chain
    // <3> blocking call not deferred (no point in this case)
    // <4> collect results and aggregate into a single object
    // <5> at the end stop the clock
    // <6> subscribe on a background thread
  }

  private HttpStatus status(int value) {
    return this.restTemplate.getForEntity(url, String.class, value).getStatusCode();
  }
}