package com.ar.mangerspital.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link InventarSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class InventarSearchRepositoryMockConfiguration {

    @MockBean
    private InventarSearchRepository mockInventarSearchRepository;
}
