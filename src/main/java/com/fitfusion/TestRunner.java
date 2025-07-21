package com.fitfusion;

import com.fitfusion.service.FoodApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// API DB저장 테스트용
// PUSH 전에 주석 처리 하거나 지워!!!
// 이거 쓰면 POSTMAN 안 써도 DB에 저장돼. 개꿀
@Component
@RequiredArgsConstructor
public class TestRunner implements CommandLineRunner {

    private final FoodApiService foodApiService;

    @Override
    public void run(String... args) throws Exception {
        int inserted = foodApiService.importFoodsAndSave("샐러드");
        System.out.println("저장된 레코드 수: " + inserted);
    }
}
