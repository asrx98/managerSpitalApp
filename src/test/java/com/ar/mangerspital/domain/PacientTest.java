package com.ar.mangerspital.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ar.mangerspital.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PacientTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Pacient.class);
        Pacient pacient1 = new Pacient();
        pacient1.setId(1L);
        Pacient pacient2 = new Pacient();
        pacient2.setId(pacient1.getId());
        assertThat(pacient1).isEqualTo(pacient2);
        pacient2.setId(2L);
        assertThat(pacient1).isNotEqualTo(pacient2);
        pacient1.setId(null);
        assertThat(pacient1).isNotEqualTo(pacient2);
    }
}
