package com.ar.mangerspital.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link SalonSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class SalonSearchRepositoryMockConfiguration {

    @MockBean
    private SalonSearchRepository mockSalonSearchRepository;
}
