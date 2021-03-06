package com.scshop.products.productservice.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.scshop.application.common.model.Product;
import com.scshop.products.productservice.exception.ProductNotFoundException;
import com.scshop.products.productservice.repository.ProductRepository;

import io.swagger.annotations.Api;

@Api(value = "/api/v1/orders", produces = "application/json")
@RestController
@RequestMapping(path = "/api/v1/products")
public class ProductController {

	
	private static Logger logger = Logger.getLogger(ProductController.class.toString());
	
	@Autowired
	ProductRepository productRepository;

	/**
	 * 
	 * @return
	 */
	@PreAuthorize("hasAuthority('SCOPE_product')")  
	@RequestMapping(path = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Product> getProducts(final @AuthenticationPrincipal Jwt jwt) {
		List<Product> products = productRepository.findAll();
		
		logger.info("######### Products loaded from here....");

		return products;
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	@PreAuthorize("hasAuthority('SCOPE_product')")  
	@RequestMapping(path = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Product getProduct(@PathVariable UUID id) {

		Optional<Product> optional = productRepository.findById(id);

		if (!optional.isPresent()) {
			throw new ProductNotFoundException("Product not found for id: " + id);
		}
		Product product = optional.get();

		return product;
	}

	/**
	 * //Disabling it for now: Not required - TODO 
	 * 
	 * Create product 
	 * 
	 * @param {@link Product}
	 */
	//@RequestMapping(path = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> createProduct(@RequestBody Product product) {

		Product savedProduct = productRepository.save(product);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(savedProduct.getId()).toUri();

		return ResponseEntity.created(location).build();
	}

	/**
	 * 
	 * @param id
	 * @param product
	 */
	@PreAuthorize("hasAuthority('SCOPE_product')")  
	@RequestMapping(path = "{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateProduct(@PathVariable UUID id, @RequestBody Product product) {
		boolean is_present = productRepository.existsById(id);

		if (!is_present) {
			throw new ProductNotFoundException("Product not found for id: " + id);
		}

		product.setId(id);
		productRepository.save(product);
	}

	/**
	 * //Disabling it for now: Not required - TODO 
	 * @param id
	 */
	//@RequestMapping(path = "{id}", method = RequestMethod.DELETE)
	public void deleteProduct(@PathVariable UUID id) {

		boolean is_present = productRepository.existsById(id);

		if (!is_present) {
			throw new ProductNotFoundException("Product not found for id: " + id);
		}

		productRepository.deleteById(id);

	}

}
