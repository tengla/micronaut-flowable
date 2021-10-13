package foo.micronaut.controllers

import com.amazonaws.services.s3.AmazonS3
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
    private val log = LoggerFactory.getLogger(javaClass)
    private val filename = "/tmp/trainstate.json"

    private fun putFileToS3(bucketName: String, key: String) {
        try {
            val client = s3Factory.createAmazonS3Client()
            client.putObject(bucketName, key, File(filename))
        } catch (ex: Exception) {
            log.error(ex.message)
        }
    }

    @Get(value = "/", produces = [MediaType.TEXT_EVENT_STREAM], single = false)
    fun index(): Flowable<String> {
        val trainStateFile = FileWriter(filename)
        return trainRepo.all().concatMap {
            Observable.just(it).delay(5, TimeUnit.MILLISECONDS)
        }.toFlowable(BackpressureStrategy.BUFFER).doAfterNext {
            trainStateFile.write(it + "\r\n")
        }.doOnTerminate {
            trainStateFile.close()
            // bucket must exist! as it does in 'dev' right now
            putFileToS3("trainstate-test", "full-state.json")
        }
    }
}