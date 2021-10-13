package foo.micronaut.controllers

import foo.micronaut.factory.AwsS3Factory
import foo.micronaut.repository.TrainRepo
import io.micronaut.context.ApplicationContext
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import jakarta.inject.Inject
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileWriter
import java.util.concurrent.TimeUnit

@Controller("/trains")
class TrainsController(
    @Inject private val trainRepo: TrainRepo,
    @Inject private val s3Factory: AwsS3Factory
) {

    private val client = s3Factory.createAmazonS3Client()

    @Get(value = "/", produces = [MediaType.TEXT_EVENT_STREAM], single = false)
    fun index(): Flowable<String> {
        val trainStateFile = FileWriter("/tmp/trainstate.json")
        return trainRepo.all().concatMap {
            Observable.just(it).delay(5, TimeUnit.MILLISECONDS)
        }.toFlowable(BackpressureStrategy.BUFFER).doAfterNext {
            trainStateFile.write(it + "\r\n")
        }.doOnTerminate {
            trainStateFile.close()
            client.putObject("trainstate-test", "full-state.json", File("/tmp/trainstate.json"))
        }
    }
}