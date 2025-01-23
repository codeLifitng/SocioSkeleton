package com.SocioSkeleton.posts_service.clients;

import com.SocioSkeleton.posts_service.dto.PersonDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "connections-service", path = "/connections")
public interface ConnectionsClient {

    @GetMapping(path = "/core/first-degree")
    public List<PersonDto> getFirstConnections();
}
