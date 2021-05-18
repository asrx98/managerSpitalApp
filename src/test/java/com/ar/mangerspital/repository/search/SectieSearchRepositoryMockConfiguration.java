package com.ar.mangerspital.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link SectieSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class SectieSearchRepositoryMockConfiguration {

    @MockBean
    private SectieSearchRepository mockSectieSearchRepository;
}
