package com.coble.core.fuse.model;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Message used for the Audit Service which logs exception and application
 * details.
 */
public class AuditMessage implements Serializable {

	private String id;
	private String message;
	private Map<String, String> tags;
	private DateTime auditDate;
	// Thu May 12 06:17:28 UTC 2016
	static DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss zzz yyyy");

	public AuditMessage(String id, Map<String, String> tags, String message) {
		this.id = id;
		this.message = message;
		this.tags = new HashMap<>(tags);

		this.auditDate = new DateTime(DateTimeZone.UTC);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Map<String, String> getTags() {
		return tags;
	}

	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}

	public void addTag(String tag, String value) {
		tags.put(tag, value);
	}

	public String readTag(String tag) {
		return tags.get(tag);
	}

	public DateTime getAuditDate() {
		return auditDate;
	}

	public void setAuditDate(DateTime auditDate) {
		this.auditDate = auditDate;
	}

	public String getHumanReadableAuditDate() {
		return dateTimeFormatter.print(auditDate);
	}
}
