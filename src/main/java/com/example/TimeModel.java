package com.example;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalTime;

@TableName("metro_time")
public class TimeModel {

    @TableId
    private Integer id;

    private Integer turn;

    private Integer station;

    @TableField("turn_time")
    private LocalTime turnTime;

    @TableField("station_order")
    private String stationOrder;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTurn() {
        return turn;
    }

    public void setTurn(Integer turn) {
        this.turn = turn;
    }

    public Integer getStation() {
        return station;
    }

    public void setStation(Integer station) {
        this.station = station;
    }

    public LocalTime getTurnTime() {
        return turnTime;
    }

    public void setTime(LocalTime time) {
        this.turnTime = time;
    }

    public String getstationOrder() {
        return stationOrder;
    }

    public void setOrder(String order) {
        this.stationOrder = order;
    }
}
