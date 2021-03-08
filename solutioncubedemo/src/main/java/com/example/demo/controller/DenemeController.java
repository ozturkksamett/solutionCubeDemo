package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dao.Deneme;
import com.example.demo.dao.DenemeRepository;
import com.example.demo.exception.DenemeNotFoundException;

@RestController
public class DenemeController {

	@Autowired
	private final DenemeRepository repository;

	DenemeController(DenemeRepository repository) {
		this.repository = repository;
	}

	@RequestMapping("/")
	public String home() {
		return "Hello World!";
	}

	@GetMapping("/denemeler")
	List<Deneme> all() {
		return repository.findAll();
	}

	@PostMapping("/denemeler")
	Deneme newDeneme(@RequestBody Deneme newDeneme) {
		return repository.save(newDeneme);
	}

	@GetMapping("/denemeler/{id}")
	Deneme one(@PathVariable Long id) {

		return repository.findById(id).orElseThrow(() -> new DenemeNotFoundException(id));
	}

	@PutMapping("/denemeler/{id}")
	Deneme replaceDeneme(@RequestBody Deneme newDeneme, @PathVariable Long id) {

		return repository.findById(id).map(deneme -> {
			deneme.setIddeneme(newDeneme.getIddeneme());
			deneme.setKolon1(newDeneme.getKolon1());
			return repository.save(deneme);
		}).orElseGet(() -> {
			newDeneme.setIddeneme(id);
			return repository.save(newDeneme);
		});
	}

	@DeleteMapping("/denemeler/{id}")
	void deleteDeneme(@PathVariable Long id) {
		repository.deleteById(id);
	}
}
