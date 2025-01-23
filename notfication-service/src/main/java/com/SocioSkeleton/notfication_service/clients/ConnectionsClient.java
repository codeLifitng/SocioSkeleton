package com.SocioSkeleton.notfication_service.clients;

import com.SocioSkeleton.notfication_service.dto.PersonDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "connections-service", path = "/connections")
public interface ConnectionsClient {

    @GetMapping(path = "/core/first-degree")
    public List<PersonDto> getFirstConnections(@RequestHeader("x-User-Id") Long userId);
}
