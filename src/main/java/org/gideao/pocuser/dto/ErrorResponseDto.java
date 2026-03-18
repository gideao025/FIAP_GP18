package org.gideao.pocuser.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record ErrorResponseDto(
        OffsetDateTime timestamp,
        int status,
        String error,
        List<String> details
) {
}
