package org.myplaylist.myplaylist.utils.impl;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Duration;

@Converter(autoApply = true)
public class DurationConverter implements AttributeConverter<Duration, Long> {
    @Override
    public Long convertToDatabaseColumn(Duration duration) {
        return (duration == null ? null : duration.toSeconds());  // convert time to seconds
    }

    @Override
    public Duration convertToEntityAttribute(Long dbData) {
        return (dbData == null ? null : Duration.ofSeconds(dbData));
    }
}
