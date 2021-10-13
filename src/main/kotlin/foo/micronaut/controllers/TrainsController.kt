package foo.micronaut.controllers

import foo.micronaut.repository.TrainRepo
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import jakarta.inject.Inject
import java.util.concurrent.TimeUnit

@Controller("/trains")
class TrainsController(
    @Inject private val trainRepo: TrainRepo
) {

    @Get(value = "/", produces = [MediaType.TEXT_EVENT_STREAM], single = false)
    fun index(): Flowable<String> {
        return trainRepo.all().concatMap {
            Observable.just(it).delay(5, TimeUnit.MILLISECONDS)
        }.toFlowable(BackpressureStrategy.BUFFER)
    }
}