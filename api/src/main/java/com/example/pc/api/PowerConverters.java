package com.example.pc.api;

import java.util.concurrent.atomic.AtomicInteger;

public class PowerConverters {
	private String name;
	private AtomicInteger pc = new AtomicInteger(0);

	public PowerConverters(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Integer getPc() {
		return pc.get();
	}

	PowerConverters up() {
		this.pc.incrementAndGet();
		return this;
	}

	PowerConverters down() {
		this.pc.decrementAndGet();
		return this;
	}
}
