package com.example;

import java.time.LocalTime;

public class QueryDBModel {

    StationModel startStation;

    LocalTime startTime;

    StationModel endStation;

    LocalTime endTime;

    @Override
    public String toString() {
        return "QueryDBModel{" +
                "startStation=" + startStation +
                ", startTime=" + startTime +
                ", endStation=" + endStation +
                ", endTime=" + endTime +
                '}';
    }
}
