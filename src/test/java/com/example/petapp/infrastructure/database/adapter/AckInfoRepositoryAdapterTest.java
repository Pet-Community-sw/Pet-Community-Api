package com.example.petapp.infrastructure.database.adapter;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AckInfoRepositoryAdapterTest {

    private final AckInfoRepositoryAdapter adapter = new AckInfoRepositoryAdapter();

    @Test
    void save후_find를_호출하면_저장한_유저들이_반환된다() {
        adapter.save("m1", Set.of(1L, 2L));

        Set<Long> result = adapter.find("m1");

        assertThat(result).containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    void deleteUser로_모든_유저가_지워지면_메시지키가_정리된다() {
        adapter.save("m2", Set.of(1L));

        adapter.deleteUser("m2", 1L);

        assertThat(adapter.find("m2")).isEmpty();
    }

    @Test
    void 존재하지_않는_메시지키를_find하면_빈_셋을_반환한다() {
        assertThat(adapter.find("unknown")).isEmpty();
    }
}
