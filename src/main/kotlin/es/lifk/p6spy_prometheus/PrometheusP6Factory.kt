package es.lifk.p6spy_prometheus

import com.p6spy.engine.event.JdbcEventListener
import com.p6spy.engine.spy.P6Factory
import com.p6spy.engine.spy.option.P6OptionsRepository
import com.p6spy.engine.spy.P6SpyOptions

class PrometheusP6Factory: P6Factory {
    lateinit var options: PrometheusP6SpyOptions
    override fun getOptions(optionsRepository: P6OptionsRepository): PrometheusP6SpyOptions {
        options = PrometheusP6SpyOptions(optionsRepository)

        return options
    }

    override fun getJdbcEventListener(): JdbcEventListener {
        return PrometheusJdbcEventListener(options.getPrefix())
    }

}

class PrometheusP6SpyOptions(private val optionsRepository: P6OptionsRepository) : P6SpyOptions(optionsRepository) {
    override fun load(options: Map<String, String>) {
        super.load(options)
        optionsRepository.set(String::class.java, PREFIX, options[PREFIX])
    }

    fun getPrefix(): String {
        return optionsRepository.get(String::class.java, PREFIX)
    }

    companion object {
        const val PREFIX = "prefix"
    }
}