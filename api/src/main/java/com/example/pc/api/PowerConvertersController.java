package com.example.pc.api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pc")
public class PowerConvertersController {
	private final Map<String, PowerConverters> inventory = new ConcurrentHashMap<>();

	@GetMapping
	public PowerConverters read(Authentication authentication) {
		return read(authentication.getName());
	}

	@PostMapping("/up")
	public PowerConverters up(Authentication authentication) {
		PowerConverters powerConverters = read(authentication.getName());
		return powerConverters.up();
	}

	@PostMapping("/down")
	public PowerConverters down(Authentication authentication) {
		PowerConverters powerConverters = read(authentication.getName());
		return powerConverters.down();
	}

	private PowerConverters read(String name) {
		return this.inventory.computeIfAbsent(name, (k) -> new PowerConverters(name));
	}
}
