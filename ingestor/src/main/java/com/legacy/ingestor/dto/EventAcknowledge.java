package com.legacy.ingestor.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class EventAcknowledge {

    Long EventId;
    Long id;
    Long legacyId;
}
