package org.jungppo.bambooforest.battery.presentation;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jungppo.bambooforest.battery.domain.BatteryItem;
import org.jungppo.bambooforest.battery.dto.BatteryItemDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/batteries")
public class BatteryController {

	@GetMapping
	public ResponseEntity<List<BatteryItemDto>> getBatteries() {
		final List<BatteryItemDto> batteryItemDtos = Stream.of(BatteryItem.values())
			.map(BatteryItemDto::new)
			.collect(Collectors.toList());
		return ResponseEntity.ok().body(batteryItemDtos);
	}
}
