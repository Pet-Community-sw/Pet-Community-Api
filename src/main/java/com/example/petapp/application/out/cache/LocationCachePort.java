package com.example.petapp.application.out.cache;

import java.util.List;

public interface LocationCachePort {
    void createLocation(Long walkRecordId, String location);

    String findLatestLocation(Long walkRecordId);

    List<String> findPath(Long walkRecordId);

    void deletePath(Long walkRecordId);
}
