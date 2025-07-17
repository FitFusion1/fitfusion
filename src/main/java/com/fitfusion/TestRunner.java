package com.fitfusion;

import com.fitfusion.service.FoodApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestRunner implements CommandLineRunner {

    private final FoodApiService foodApiService;

    @Override
    public void run(String... args) throws Exception {
        int inserted = foodApiService.importFoodsAndSave("고구마");
        System.out.println("저장된 레코드 수: " + inserted);
    }
}
