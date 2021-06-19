package com.wendelnunes.assembleia.utils;

import static java.util.Arrays.asList;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

public class SortUtil {

	public static List<Order> createOrders(String... sort) {
		if (sort != null) {
			return asList(sort).stream().map(i -> new Order( //
					Direction.fromOptionalString(i.split("\\.").length > 1 ? i.split("\\.")[1].toUpperCase() : "ASC")
							.orElse(Direction.ASC),
					i.split("\\.")[0])) //
					.collect(Collectors.toList());
		}
		return Collections.singletonList(new Order(Direction.ASC, "id"));
	}
}