package org.example.vibecodingmaster.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePortfolioRequest {
    private String name;
    private String userId;
}
