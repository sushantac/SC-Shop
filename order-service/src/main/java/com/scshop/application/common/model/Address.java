package com.scshop.application.common.model;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import com.scshop.application.common.enums.Country;

@Embeddable
public class Address {

	@NotNull
	private String name;

	@NotNull
	private String addressLine1;

	private String addressLine2;

	@NotNull
	private String city;

	@NotNull
	private Integer pin;
	
	@NotNull
	private String state;

	@NotNull
	@Enumerated(EnumType.STRING)
	private Country country;

	public Address() {}
	
	public Address(@NotNull String name, @NotNull String addressLine1, String addressLine2, @NotNull String city,
			@NotNull Integer pin, @NotNull String state, @NotNull Country country) {
		super();
		this.name = name;
		this.addressLine1 = addressLine1;
		this.addressLine2 = addressLine2;
		this.city = city;
		this.pin = pin;
		this.state = state;
		this.country = country;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((addressLine1 == null) ? 0 : addressLine1.hashCode());
		result = prime * result + ((addressLine2 == null) ? 0 : addressLine2.hashCode());
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((pin == null) ? 0 : pin.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Address other = (Address) obj;
		if (addressLine1 == null) {
			if (other.addressLine1 != null)
				return false;
		} else if (!addressLine1.equals(other.addressLine1))
			return false;
		if (addressLine2 == null) {
			if (other.addressLine2 != null)
				return false;
		} else if (!addressLine2.equals(other.addressLine2))
			return false;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (country != other.country)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (pin == null) {
			if (other.pin != null)
				return false;
		} else if (!pin.equals(other.pin))
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Address [name=" + name + ", addressLine1=" + addressLine1 + ", addressLine2=" + addressLine2 + ", city="
				+ city + ", pin=" + pin + ", state=" + state + ", country=" + country + "]";
	}

	public Integer getPin() {
		return pin;
	}

	public void setPin(Integer pin) {
		this.pin = pin;
	}

	

}
