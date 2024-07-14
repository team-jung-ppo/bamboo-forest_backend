package org.jungppo.bambooforest.controller.battery;

import static org.jungppo.bambooforest.response.ResponseUtil.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jungppo.bambooforest.dto.battery.BatteryItemDto;
import org.jungppo.bambooforest.entity.battery.BatteryItem;
import org.jungppo.bambooforest.response.ResponseBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/batteries")
public class BatteryController {

	@GetMapping
	public ResponseEntity<ResponseBody<List<BatteryItemDto>>> getBatteryInfo() {
		List<BatteryItemDto> batteryItemDtos = Stream.of(BatteryItem.values())
			.map(BatteryItemDto::new)
			.collect(Collectors.toList());
		return ResponseEntity.status(HttpStatus.OK).body(createSuccessResponse(batteryItemDtos));
	}
}
