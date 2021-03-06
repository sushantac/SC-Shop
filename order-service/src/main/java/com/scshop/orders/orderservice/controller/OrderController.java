package com.scshop.orders.orderservice.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.scshop.application.common.enums.InventoryStatus;
import com.scshop.application.common.enums.OrderStatus;
import com.scshop.application.common.enums.PaymentStatus;
import com.scshop.application.common.model.FinalOrder;
import com.scshop.orders.orderservice.entity.OrderRepository;
import com.scshop.orders.orderservice.exception.OrderDetailsInvalidException;
import com.scshop.orders.orderservice.services.OrderService;
import com.scshop.orders.orderservice.validation.OrderValidation;
import com.scshop.orders.orderservice.validation.OrderValidationStatus;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "/api/v1/orders", produces = "application/json")
@RestController
@RequestMapping(path = "/api/v1/orders")
public class OrderController {

	final Logger logger = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	OrderService orderService;
	
	@RequestMapping(path = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<FinalOrder> getOrders() {
		
		logger.info("Fetching orders......");
		
		return orderRepository.findAll();

	}

	@ApiOperation(value = "getOrder", response = FinalOrder.class)
	@ApiResponses(value = 
			{ 
			  @ApiResponse(code = 200, message = "Order Details", response = FinalOrder.class),
			  @ApiResponse(code = 500, message = "Internal Server Error"),
			  @ApiResponse(code = 404, message = "Order not found") 
			}
	)
	@RequestMapping(path = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public FinalOrder getOrder(@PathVariable UUID id) {

		logger.info("Fetching order for : " + id);
		
		Optional<FinalOrder> optional = orderRepository.findById(id);

		if (!optional.isPresent()) {
			throw new RuntimeException("Order not found");
		}

		return optional.get();
	}

	@RequestMapping(path = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public UUID generateOrder(@RequestBody FinalOrder order) {

		logger.info("Generating order : " + order);
		
		// # // Validate the incoming order data before committing it
		// - validate if the totalPrice is correct by re-calculating it by fetching product item prices from product-service
		// - validate if customer has enough balance in credits
		// - validate if product inventory is available
		
		OrderValidation orderValidation = orderService.validate(order);
		
		if(!OrderValidationStatus.ORDER_IS_VALID.equals(orderValidation.getStatus())) {
			throw new OrderDetailsInvalidException(orderValidation.getStatus().getDetails());
		}
		
		order.setStatus(OrderStatus.CREATED);
		order.setInventoryStatus(InventoryStatus.REDUCE_STOCK_UPDATE_INITIATED);
		order.setPaymentStatus(PaymentStatus.INITIATED);
		FinalOrder savedOrder = orderRepository.save(order);

		orderService.processOrder(savedOrder);
		
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(savedOrder.getId())
				.toUri();

		//TODO Change it later 
		//return ResponseEntity.created(location).build();
		
		return savedOrder.getId();
	}

	@RequestMapping(path = "{id}", method = RequestMethod.DELETE)
	public void cancelOrder(@PathVariable UUID id) {
		logger.info("Cancelling order for : " + id);
		
		Optional<FinalOrder> optional = orderRepository.findById(id);

		if (!optional.isPresent()) {
			throw new RuntimeException("Order not found");
		}

		FinalOrder order = optional.get();
		order.setStatus(OrderStatus.CANCELLED);
		
		orderRepository.save(order);
		
	}

}
