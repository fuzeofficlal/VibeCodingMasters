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
        // Run in a new thread so the response can be returned before self-destruction
        new Thread(() -> {
            try {
                // Wait for the HTTP response to be fully transmitted
                Thread.sleep(1000);
                
                // Execute a decoupled batch script that hunts precisely by TCP ports
                // This defeats Windows Terminal Tab abstraction layers effortlessly.
                String command = "start kill-all.bat";
                
                // If it fails to find the window, safety fallback to kill self
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
