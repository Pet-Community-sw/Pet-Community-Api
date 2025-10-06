package com.example.PetApp.common.base.init;

import com.example.PetApp.domain.member.RoleRepository;
import com.example.PetApp.domain.member.model.entity.Role;
import com.example.PetApp.domain.petbreed.PetBreedRepository;
import com.example.PetApp.domain.petbreed.model.entity.PetBreed;
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
    private final PetBreedRepository petBreedRepository;

    @Override
    @Transactional
    public void run(String... args) {
        addRoleIfMissing();
        seedBreeds();
        log.info("데이터 초기 세팅");
    }

    private void seedBreeds() {
        if (petBreedRepository.count()==0) {
            List<String> breeds = List.of("푸들", "불독", "리트리버", "웰시코기", "도베르만");
            breeds.forEach(this::addBreedIfMissing);
        }
    }

    private void addRoleIfMissing() {
        if (roleRepository.count()==0) {
            roleRepository.save(Role.builder().name("ROLE_USER").build());
            roleRepository.save(Role.builder().name("ROLE_ADMIN").build());
        }
    }

    private void addBreedIfMissing(String name) {
        petBreedRepository.save(PetBreed.builder().name(name).build());
    }
}
