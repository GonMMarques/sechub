// SPDX-License-Identifier: MIT
package com.daimler.sechub.domain.schedule.config;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * Configuration entry for scheduler inside database. Contains only ONE row! see {@link #ID}
 * @author Albert Tregnaghi
 *
 */
@Entity
@Table(name = SchedulerConfig.TABLE_NAME)
public class SchedulerConfig {

	/**
	 * We got only ONE schedule configuration entry inside table. So we use always only the first one here!
	 */
	public static final Integer ID = Integer.valueOf(0);

	/* +-----------------------------------------------------------------------+ */
	/* +............................ SQL ......................................+ */
	/* +-----------------------------------------------------------------------+ */
	public static final String TABLE_NAME = "SCHEDULE_CONFIG";

	public static final String COLUMN_ID = "CONFIG_ID";

	public static final String COLUMN_JOB_PROCESSING_ENABLED = "CONFIG_JOB_PROCESSING_ENABLED";

	/* +-----------------------------------------------------------------------+ */
	/* +............................ JPQL .....................................+ */
	/* +-----------------------------------------------------------------------+ */
	public static final String CLASS_NAME = SchedulerConfig.class.getSimpleName();

	@Id
	@Column(name = COLUMN_ID, unique = true, nullable = false)
	Integer id = ID;

	@Column(name = COLUMN_JOB_PROCESSING_ENABLED, nullable = false)
	boolean jobProcessingEnabled = true;

    @Version
	@Column(name = "VERSION")
	Integer version;

    public void setJobProcessingEnabled(boolean jobProcessingEnabled) {
		this.jobProcessingEnabled = jobProcessingEnabled;
	}

    public boolean isJobProcessingEnabled() {
		return jobProcessingEnabled;
	}

	public Integer getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SchedulerConfig other = (SchedulerConfig) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}



}