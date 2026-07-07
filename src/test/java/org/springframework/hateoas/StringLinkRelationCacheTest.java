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
package org.springframework.hateoas;

import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;
import org.springframework.util.ConcurrentLruCache;

/**
 * Regression test for the {@link StringLinkRelation} cache being bounded so that a stream of distinct link relations
 * derived from untrusted input cannot exhaust the heap.
 *
 * @author Jonathan Schneider
 */
class StringLinkRelationCacheTest {

	@Test // heap exhaustion: the interned-relation cache must not grow without bound
	void relationCacheIsBounded() throws Exception {

		for (int i = 0; i < 1000; i++) {
			LinkRelation.of("attacker-relation-" + i);
		}

		Field field = StringLinkRelation.class.getDeclaredField("CACHE");
		field.setAccessible(true);
		Object cache = field.get(null);

		assertThat(cache).isInstanceOf(ConcurrentLruCache.class);

		ConcurrentLruCache<?, ?> lru = (ConcurrentLruCache<?, ?>) cache;

		assertThat(lru.sizeLimit()).isEqualTo(256);
		assertThat(lru.size()).isLessThanOrEqualTo(256);
	}
}
