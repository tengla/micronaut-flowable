package foo.micronaut.repository

import com.github.javafaker.Faker
import foo.micronaut.domain.Train
import io.reactivex.rxjava3.core.Observable
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import javax.sql.DataSource

@Singleton
class TrainRepo(@Inject val dataSource: DataSource) {

    private val log = LoggerFactory.getLogger(TrainRepo::class.java)

    init {
        log.info("TrainRepo initialized.")
        val faker = Faker.instance()
        log.info("Generating 10000 train instances.")
        val trains = mutableListOf<Train>().apply {
            repeat(10_000) {
                this.add(
                    Train(
                        name = faker.funnyName().name()
                    )
                )
            }
        }
        saveAll(trains)
        log.info("TrainRepo done inserting train records")
    }

    fun all(): Observable<String> {
        return Observable.create {
            dataSource.connection.use { conn ->
                val stmt = conn.prepareStatement(
                    "SELECT * FROM trains ORDER BY id"
                )
                stmt.execute()
                while(stmt.resultSet.next()) {
                    val id = stmt.resultSet.getInt(1)
                    val name = stmt.resultSet.getString(2)
                    it.onNext(
                        Train(
                            id = id,
                            name = name
                        ).toJSON()
                    )
                }
            }
            it.onComplete()
        }
    }

    private fun saveAll(trains: List<Train>) {
        dataSource.connection.use { conn ->
            val stmt = conn.prepareStatement(
                "INSERT INTO trains(name) VALUES(?);"
            )
            trains.forEach {
                stmt.setString(1, it.name)
                stmt.addBatch()
            }
            stmt.executeBatch()
            conn.commit()
        }
    }
}