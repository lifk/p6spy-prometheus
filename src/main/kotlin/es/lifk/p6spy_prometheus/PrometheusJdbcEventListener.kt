package es.lifk.p6spy_prometheus

import com.p6spy.engine.common.StatementInformation
import com.p6spy.engine.event.SimpleJdbcEventListener
import io.prometheus.client.Counter
import java.sql.SQLException
import io.prometheus.client.Gauge
import io.prometheus.client.Histogram
import java.util.concurrent.TimeUnit

class PrometheusJdbcEventListener(prefix: String): SimpleJdbcEventListener() {
    private val queries = Counter.build().name("${prefix}queries_total").help("Total queries.").register()
    private val inProgressQueries = Gauge.build().name("${prefix}in_progress_queries").help("In progress queries.").register()
    private val queryLatency = Histogram.build().name("${prefix}query_latency_milis").help("query latency in milis").register()

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