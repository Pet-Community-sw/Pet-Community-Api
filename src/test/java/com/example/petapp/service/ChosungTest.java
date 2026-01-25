package com.example.petapp.service;

import com.example.petapp.application.common.NameChosungUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class ChosungTest {

    @Test
    public void test() {
        Assertions.assertThat(NameChosungUtil.getChosung("최선재")).isEqualTo("ㅊㅅㅈ");
    }
}
