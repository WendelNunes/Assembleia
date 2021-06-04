package com.wendelnunes.assembleia.utils;

import java.time.OffsetDateTime;

import org.springframework.stereotype.Component;

@Component
public class DateTimeUtil {

	public OffsetDateTime currentDateTime() {
		return OffsetDateTime.now();
	}
}