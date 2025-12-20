package com.example.petapp.infrastructure.database.jpa;

import com.example.petapp.application.in.petbreed.PetBreedQueryUseCase;
import com.example.petapp.application.in.petbreed.PetBreedUseCase;
import com.example.petapp.domain.petbreed.model.PetBreed;
import com.example.petapp.domain.role.Role;
import com.example.petapp.domain.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PetBreedQueryUseCase petBreedQueryUseCase;
    private final PetBreedUseCase petBreedUseCase;

    @Override
    @Transactional
    public void run(String... args) {
        addRoleIfMissing();
        seedBreeds();
        log.info("데이터 초기 세팅");
    }

    private void seedBreeds() {
        if (petBreedQueryUseCase.count() == 0) {
            List<String> breeds = List.of("푸들", "불독", "리트리버", "웰시코기", "도베르만", "시바이누", "말티즈", "치와와", "비글", "골든리트리버",
                    "보더콜리", "시츄", "요크셔테리어", "삽살개", "진돗개",
                    "그레이하운드", "허스키", "로트와일러", "달마시안", "페키니즈",
                    "비숑프리제", "샤페이");
            breeds.forEach(this::addBreedIfMissing);
        }
    }

    private void addRoleIfMissing() {
        if (roleRepository.count() == 0) {
            roleRepository.save(Role.builder().name("ROLE_USER").build());
            roleRepository.save(Role.builder().name("ROLE_ADMIN").build());
        }
    }

    private void addBreedIfMissing(String name) {
        petBreedUseCase.save(PetBreed.builder().name(name).build());
    }
}
