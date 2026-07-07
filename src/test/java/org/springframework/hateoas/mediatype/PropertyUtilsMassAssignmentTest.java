/*
 * Copyright 2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.hateoas.mediatype;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Regression test making sure {@link PropertyUtils#createObjectFromProperties} does not bind properties that Jackson
 * would ignore, preventing mass-assignment to {@link JsonIgnore}'d / read-only fields from untrusted payloads.
 *
 * @author Jonathan Schneider
 */
class PropertyUtilsMassAssignmentTest {

	@Test // mass assignment: a payload must not be able to set a @JsonIgnore'd property
	void doesNotBindJsonIgnoredProperties() {

		Map<String, Object> payload = new HashMap<>();
		payload.put("name", "Dave");
		payload.put("admin", true);

		Sample result = PropertyUtils.createObjectFromProperties(Sample.class, payload);

		assertThat(result.getName()).isEqualTo("Dave");
		assertThat(result.isAdmin()).isFalse();
	}

	static class Sample {

		private String name;
		@JsonIgnore private boolean admin;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public boolean isAdmin() {
			return admin;
		}

		public void setAdmin(boolean admin) {
			this.admin = admin;
		}
	}
}
