package com.ar.mangerspital.repository.search;

import com.ar.mangerspital.domain.Personal;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Personal} entity.
 */
public interface PersonalSearchRepository extends ElasticsearchRepository<Personal, Long> {}
