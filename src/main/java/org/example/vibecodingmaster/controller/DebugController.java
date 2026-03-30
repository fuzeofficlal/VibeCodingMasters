package org.example.vibecodingmaster.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/debug")
public class DebugController {

    private final JdbcTemplate jdbcTemplate;

    public DebugController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    
    @GetMapping("/table/{tableName}")
    public ResponseEntity<?> getTableData(@PathVariable String tableName) {
        
        if (!tableName.matches("^[a-zA-Z0-9_]+$")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid table name format"));
        }

        try {
            String sql = "SELECT * FROM " + tableName + " LIMIT 100";
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
            return ResponseEntity.ok(Map.of(
                    "tableName", tableName,
                    "rowCount", rows.size(),
                    "data", rows
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
