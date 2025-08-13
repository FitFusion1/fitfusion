package com.fitfusion.service;

import com.fitfusion.enums.BodyPart;
import com.fitfusion.mapper.TargetRoutineMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TargetPartRoutineService {

    private final TargetRoutineMapper targetRoutineMapper;

    public List<BodyPart> findLackParts(int userId) {
        return targetRoutineMapper.getLeastUsedParts(userId).stream()
                .map(kor -> Arrays.stream(BodyPart.values())
                        .filter(bp ->  bp.getBodyName().equalsIgnoreCase(kor))
                        .findFirst()
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

}
