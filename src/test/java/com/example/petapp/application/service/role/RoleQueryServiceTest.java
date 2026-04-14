package com.example.petapp.application.service.role;

import com.example.petapp.domain.role.Role;
import com.example.petapp.domain.role.RoleRepository;
import com.example.petapp.interfaces.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleQueryServiceTest {

    @Mock
    private RoleRepository repository;

    @InjectMocks
    private RoleQueryService roleQueryService;

    @Test
    void 임시역할이_존재하면_조회에_성공한다() {
        Role role = Role.builder().name("ROLE_TEMPORARY").build();
        when(repository.find("ROLE_TEMPORARY")).thenReturn(Optional.of(role));

        Role result = roleQueryService.findTemporaryRole();

        assertThat(result).isSameAs(role);
        verify(repository).find("ROLE_TEMPORARY");
    }

    @Test
    void 임시역할이_없으면_예외가_발생한다() {
        when(repository.find("ROLE_TEMPORARY")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleQueryService.findTemporaryRole())
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 role은 없습니다.");
    }

    @Test
    void 사용자역할이_존재하면_조회에_성공한다() {
        Role role = Role.builder().name("ROLE_USER").build();
        when(repository.find("ROLE_USER")).thenReturn(Optional.of(role));

        Role result = roleQueryService.findUserRole();

        assertThat(result).isSameAs(role);
        verify(repository).find("ROLE_USER");
    }

    @Test
    void 사용자역할이_없으면_예외가_발생한다() {
        when(repository.find("ROLE_USER")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleQueryService.findUserRole())
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 role은 없습니다.");
    }
}
