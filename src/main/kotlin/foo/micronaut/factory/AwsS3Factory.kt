package foo.micronaut.factory

import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton

@Factory
class AwsS3Factory {

    @Singleton
    fun createAmazonS3Client(): AmazonS3 {
        return AmazonS3ClientBuilder.standard().withRegion(Regions.EU_WEST_1).build()
    }
}
