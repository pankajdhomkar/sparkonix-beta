package com.sparkonix.entity.dto;

import com.sparkonix.entity.Customer;

public class CustomerDetailDTO {
	private Customer customer;
	private long factoryLocationsCount;
	private long machinesCount;
	private long operatorsCount;

	public Customer getCustomerDetail() {
		return customer;
	}

	public void setCustomerDetail(Customer customer) {
		this.customer = customer;
	}

	public long getFactoryLocationsCount() {
		return factoryLocationsCount;
	}

	public void setFactoryLocationsCount(long factoryLocationsCount) {
		this.factoryLocationsCount = factoryLocationsCount;
	}

	public long getMachinesCount() {
		return machinesCount;
	}

	public void setMachinesCount(long machinesCount) {
		this.machinesCount = machinesCount;
	}

	public long getOperatorsCount() {
		return operatorsCount;
	}

	public void setOperatorsCount(long operatorsCount) {
		this.operatorsCount = operatorsCount;
	}
}
