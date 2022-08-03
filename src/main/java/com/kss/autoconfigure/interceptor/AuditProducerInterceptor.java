package com.kss.autoconfigure.interceptor;

import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Header;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;

import java.util.Map;

@Log4j2
public class AuditProducerInterceptor<K, V> implements ProducerInterceptor<String, String> {

    @Override
    public ProducerRecord onSend(ProducerRecord producerRecord) {
        log.info(producerRecord);
        producerRecord.headers().add(new Header() {
            @Override
            public String key() {
                return "trace_id";
            }

            @Override
            public byte[] value() {
                return TraceContext.traceId().getBytes();
            }
        });
        return producerRecord;
    }

    @Override
    public void onAcknowledgement(RecordMetadata recordMetadata, Exception e) {
    }

    @Override
    public void close() {
    }

    @Override
    public void configure(Map<String, ?> map) {
    }
}

