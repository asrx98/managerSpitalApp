package com.ar.mangerspital.repository.search;

import com.ar.mangerspital.domain.Pacient;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Pacient} entity.
 */
public interface PacientSearchRepository extends ElasticsearchRepository<Pacient, Long> {}
