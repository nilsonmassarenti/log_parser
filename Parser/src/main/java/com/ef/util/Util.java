package com.ef.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class Util {

	public Timestamp convertLocalDateTimeToSqlDate(LocalDateTime dateTime) {
		ZonedDateTime zdt = dateTime.atZone(ZoneId.systemDefault());
		Date date = Date.from(zdt.toInstant());
		Timestamp ts = new Timestamp(date.getTime());
		return ts;

	}

}
