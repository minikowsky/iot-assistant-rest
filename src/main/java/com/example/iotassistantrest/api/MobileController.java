package com.example.iotassistantrest.api;

import com.example.iotassistantrest.iot.controller.SwitchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mobile")
@RequiredArgsConstructor
public class MobileController {
    private final SwitchService service;

    @PostMapping(value = "/light/{id}/{mode}")
    public HttpStatus switchLight(@PathVariable("id") String id, @PathVariable("mode") String mode) {
        try {
            service.switchLight(id, mode.toLowerCase());
            return HttpStatus.OK;
        } catch (Exception e) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }


    @PostMapping(value = "/alarm/{mode}")
    public HttpStatus switchAlarm(@PathVariable("mode") String mode) {
        service.switchAlarm(mode.toLowerCase());
        return HttpStatus.OK;
    }

    @GetMapping(value = "/alarm")
    public boolean getAlarm() {
        return service.getAlarm();
    }
}
