package foo.micronaut.factory

import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Factory
class AwsS3Factory {

    private val log = LoggerFactory.getLogger(this::class.simpleName)

    @Singleton
    fun createAmazonS3Client(): AmazonS3 {
        log.info("Hello from AwsS3Factory")
        return AmazonS3ClientBuilder.standard().withRegion(Regions.EU_WEST_1).build()
    }
}
