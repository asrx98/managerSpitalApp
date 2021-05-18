package com.ar.mangerspital.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ar.mangerspital.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PersonalTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Personal.class);
        Personal personal1 = new Personal();
        personal1.setId(1L);
        Personal personal2 = new Personal();
        personal2.setId(personal1.getId());
        assertThat(personal1).isEqualTo(personal2);
        personal2.setId(2L);
        assertThat(personal1).isNotEqualTo(personal2);
        personal1.setId(null);
        assertThat(personal1).isNotEqualTo(personal2);
    }
}
