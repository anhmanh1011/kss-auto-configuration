package com.kss.autoconfigure.interceptor;

import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.kafka.listener.RecordInterceptor;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class AuditRecordInterceptor<K, V> implements RecordInterceptor<K, V> {

    @Override
    public ConsumerRecord<K, V> intercept(ConsumerRecord<K, V> consumerRecord) {
        return consumerRecord;
    }

    @Override
    public ConsumerRecord<K, V> intercept(ConsumerRecord<K, V> record, Consumer<K, V> consumer) {
        for (Header header : record.headers()) {
            if (header.key().equals("trace_id")) {
                ThreadContext.put("trace_id", new String(header.value()));
            }
        }
        log.trace("received record "+record.value());
        return RecordInterceptor.super.intercept(record, consumer);
    }

    @Override
    public void success(ConsumerRecord<K, V> record, Consumer<K, V> consumer) {
        log.trace(" success " + record.value());
        ThreadContext.clearAll();
        RecordInterceptor.super.success(record, consumer);
    }

    @Override
    public void failure(ConsumerRecord<K, V> record, Exception exception, Consumer<K, V> consumer) {
        log.error("failure " + record.value(),exception);
        ThreadContext.clearAll();
        RecordInterceptor.super.failure(record, exception, consumer);
    }

    @Override
    public void afterRecord(ConsumerRecord<K, V> record, Consumer<K, V> consumer) {
        RecordInterceptor.super.afterRecord(record, consumer);
    }
}
