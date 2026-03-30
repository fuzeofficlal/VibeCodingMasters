package org.example.vibecodingmaster.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/system")
public class SystemController {

    @PostMapping("/shutdown")
    @Operation(summary = "Shut down all active microservices")
    public ResponseEntity<String> shutdownAll() {
        
        new Thread(() -> {
            try {
                
                Thread.sleep(1000);
                
                
                
                String command = "start kill-all.bat";
                
                
                Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", command});
                Thread.sleep(2000);
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        return ResponseEntity.ok("{\"status\": \"shutting_down\"}");
    }
}
