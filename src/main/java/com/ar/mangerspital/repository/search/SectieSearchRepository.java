package com.ar.mangerspital.repository.search;

import com.ar.mangerspital.domain.Sectie;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Sectie} entity.
 */
public interface SectieSearchRepository extends ElasticsearchRepository<Sectie, Long> {}
