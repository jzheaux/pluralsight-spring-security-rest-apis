package com.example.pc.api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.hateoas.EntityModel;
import org.springframework.security.access.prepost.PreAuthorize;
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
	@PreAuthorize("hasAuthority('SCOPE_pc.read')")
	public EntityModel<PowerConverters> read(Authentication authentication) {
		return EntityModel.of(read(authentication.getName()));
	}

	@PostMapping("/up")
	@PreAuthorize("hasAuthority('SCOPE_pc.write')")
	public EntityModel<PowerConverters> up(Authentication authentication) {
		PowerConverters powerConverters = read(authentication.getName());
		return EntityModel.of(powerConverters.up());
	}

	@PostMapping("/down")
	@PreAuthorize("hasAuthority('SCOPE_pc.write')")
	public EntityModel<PowerConverters> down(Authentication authentication) {
		PowerConverters powerConverters = read(authentication.getName());
		return EntityModel.of(powerConverters.down());
	}

	private PowerConverters read(String name) {
		return this.inventory.computeIfAbsent(name, (k) -> new PowerConverters(name));
	}
}
