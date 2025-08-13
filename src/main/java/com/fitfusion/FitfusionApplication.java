package com.fitfusion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
public class FitfusionApplication {

	public static void main(String[] args) {

		SpringApplication.run(FitfusionApplication.class, args);
		Scanner sc = new Scanner(System.in);

		int x = 4;
		for (int i = 1; i <= x; i++) {
			if (i % 15 == 0) {
				System.out.println("PIZZ" + "BUZZ");
			} else if (i % 5 == 0) {
				System.out.println("PIZZ");
			} else if (i % 3 == 0) {
				System.out.println("BUZZ");
			} else {
				System.out.println(i);
			}

		}
	}
}
