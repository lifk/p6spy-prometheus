package es.lifk.p6spy_prometheus

import com.p6spy.engine.common.StatementInformation
import com.p6spy.engine.event.SimpleJdbcEventListener
import io.prometheus.client.Counter
import java.sql.SQLException
import io.prometheus.client.Gauge
import io.prometheus.client.Histogram
import java.util.concurrent.TimeUnit

class PrometheusJdbcEventListener: SimpleJdbcEventListener() {
    private val queries = Counter.build().name("queries_total").labelNames("status").help("Total queries.").register()
    private val inProgressQueries = Gauge.build().name("in_progress_queries").help("In progress queries.").register()
    private val queryLatency = Histogram.build()
        .buckets(0.1, 1.0, 5.0, 10.0, 20.0, 40.0, 80.0, 160.0, 320.0, 640.0, 1280.0, 2560.0, 5120.0, 10240.0, 20580.0, 41060.0)
        .name("query_latency_milis")
        .help("query latency in milis")
        .register()

    override fun onBeforeAnyExecute(statementInformation: StatementInformation) {
        inProgressQueries.inc()
    }

    override fun onAfterAnyExecute(
        statementInformation: StatementInformation?,
        timeElapsedNanos: Long,
        e: SQLException?
    ) {
        inProgressQueries.dec()
        if (e == null) {
            queries.labels("success").inc()
        } else {
            queries.labels("error").inc()
        }

        queryLatency.observe(TimeUnit.NANOSECONDS.toMillis(timeElapsedNanos).toDouble())
    }
}