package com.example.dao;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Deneme {
	
	@Id
	@GeneratedValue
	private Long iddeneme;

	private String kolon1;

	Deneme() {
	}

	Deneme(Long iddeneme, String kolon1) {

		this.iddeneme = iddeneme;
		this.kolon1 = kolon1;
	}

	public Long getIddeneme() {
		return this.iddeneme;
	}

	public String getKolon1() {
		return this.kolon1;
	}

	public void setIddeneme(Long iddeneme) {
		this.iddeneme = iddeneme;
	}

	public void setKolon1(String kolon1) {
		this.kolon1 = kolon1;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o)
			return true;
		if (!(o instanceof Deneme))
			return false;
		Deneme deneme = (Deneme) o;
		return Objects.equals(this.iddeneme, deneme.iddeneme) && Objects.equals(this.kolon1, deneme.kolon1);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.iddeneme, this.kolon1);
	}

	@Override
	public String toString() {
		return "Deneme{" + "iddeneme=" + this.iddeneme + ", kolon1='" + this.kolon1;
	}
}
