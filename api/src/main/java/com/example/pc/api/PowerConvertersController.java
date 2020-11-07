package com.example.pc.api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pc")
public class PowerConvertersController {
	private final Map<String, PowerConverters> inventory = new ConcurrentHashMap<>();
	private final String name = "luke";

	@GetMapping
	public PowerConverters read() {
		return read(this.name);
	}

	@PostMapping("/up")
	public PowerConverters up() {
		PowerConverters powerConverters = read(this.name);
		return powerConverters.up();
	}

	@PostMapping("/down")
	public PowerConverters down() {
		PowerConverters powerConverters = read(this.name);
		return powerConverters.down();
	}

	private PowerConverters read(String name) {
		return this.inventory.computeIfAbsent(name, (k) -> new PowerConverters(name));
	}
}
