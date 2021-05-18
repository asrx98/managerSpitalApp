package com.ar.mangerspital.repository.search;

import com.ar.mangerspital.domain.Salon;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Salon} entity.
 */
public interface SalonSearchRepository extends ElasticsearchRepository<Salon, Long> {}
