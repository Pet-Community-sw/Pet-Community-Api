package com.example.PetApp.domain.petbreed;

import com.example.PetApp.domain.petbreed.model.dto.PetBreedGetListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pet-breeds")
@RequiredArgsConstructor
public class PetBreedController {

    private final PetBreedService petBreedService;

    @GetMapping()
    public List<PetBreedGetListDto> getList() {
        return petBreedService.getList();
    }
}
