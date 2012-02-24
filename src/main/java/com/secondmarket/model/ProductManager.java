package com.secondmarket.model;

import java.util.List;

public interface ProductManager {
	void increasePrice(int percentage);

	List<Product> getProducts();
}
