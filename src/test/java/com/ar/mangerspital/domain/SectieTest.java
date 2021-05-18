package com.ar.mangerspital.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ar.mangerspital.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SectieTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Sectie.class);
        Sectie sectie1 = new Sectie();
        sectie1.setId(1L);
        Sectie sectie2 = new Sectie();
        sectie2.setId(sectie1.getId());
        assertThat(sectie1).isEqualTo(sectie2);
        sectie2.setId(2L);
        assertThat(sectie1).isNotEqualTo(sectie2);
        sectie1.setId(null);
        assertThat(sectie1).isNotEqualTo(sectie2);
    }
}
