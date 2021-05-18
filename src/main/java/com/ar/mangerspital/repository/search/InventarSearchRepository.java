package com.ar.mangerspital.repository.search;

import com.ar.mangerspital.domain.Inventar;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Inventar} entity.
 */
public interface InventarSearchRepository extends ElasticsearchRepository<Inventar, Long> {}
