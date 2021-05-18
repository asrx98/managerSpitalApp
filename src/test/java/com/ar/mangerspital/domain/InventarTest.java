package com.ar.mangerspital.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ar.mangerspital.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InventarTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Inventar.class);
        Inventar inventar1 = new Inventar();
        inventar1.setId(1L);
        Inventar inventar2 = new Inventar();
        inventar2.setId(inventar1.getId());
        assertThat(inventar1).isEqualTo(inventar2);
        inventar2.setId(2L);
        assertThat(inventar1).isNotEqualTo(inventar2);
        inventar1.setId(null);
        assertThat(inventar1).isNotEqualTo(inventar2);
    }
}
