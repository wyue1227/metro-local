package com.example;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mapper.StationMapper;
import com.example.mapper.TimeMapper;
import com.example.util.MybatisUtils;
import org.apache.ibatis.session.SqlSession;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MetroService {

    List<QueryDBModel> models = new ArrayList<>();

    public void search(QueryModel start, QueryModel end, LocalTime startTime) throws Exception {

        if (Objects.equals(start.line, end.line)) {
            models.add(querySameRoute(start, end, startTime));
        } else {
            QueryDBModel tmp = queryToRelatedStation(start, startTime);

            StationModel relatedStation = getRelatedStation(tmp.endStation.getName(), tmp.endStation.getLine());
            search(new QueryModel(relatedStation.getName(), relatedStation.getLine()), end, tmp.endTime);
        }
    }

    public void printResult() {
        System.out.println(models);
    }

    /**
     * 查询同一线路
     * @param start 开始站点
     * @param end 结束站点
     * @param startTime 开始时间
     * @return 同一线路的返回集
     */
    private QueryDBModel querySameRoute(QueryModel start, QueryModel end, LocalTime startTime) throws Exception {

        QueryDBModel result = new QueryDBModel();
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StationMapper stationMapper = sqlSession.getMapper(StationMapper.class);
        QueryWrapper<StationModel> startWrapper = new QueryWrapper<>();
        startWrapper.eq("line", start.line);
        startWrapper.eq("name", start.name);
        StationModel startModel = stationMapper.selectOne(startWrapper);
        System.out.println(startModel);
        result.startStation = startModel;

        QueryWrapper<StationModel> endWrapper = new QueryWrapper<>();
        endWrapper.eq("line", end.line);
        endWrapper.eq("name", end.name);
        StationModel endModel = stationMapper.selectOne(endWrapper);
        System.out.println(endModel);
        result.endStation = endModel;


        TimeMapper timeMapper = sqlSession.getMapper(TimeMapper.class);
        QueryWrapper<TimeModel> startTimeWrapper = new QueryWrapper<>();
        startTimeWrapper.ge("turn_time", startTime);
        startTimeWrapper.eq("station", startModel.getId());
        if (endModel.getPosition() > startModel.getPosition()) {
            // 查询顺序时刻表 1a
            startTimeWrapper.eq("station_order", start.line.toString() + "a");
        } else {
            // 查询倒序时刻表 1b
            startTimeWrapper.eq("station_order", start.line.toString() + "b");
        }
        TimeModel startTimeModel =timeMapper.selectOne(startTimeWrapper);
        if (startTimeModel == null) {
            throw new Exception("赶不上车了，凉凉");
        }
        result.startTime = startTimeModel.getTurnTime();

        QueryWrapper<TimeModel> endTimeWrapper = new QueryWrapper<>();
        endTimeWrapper.eq("station_order", startTimeModel.getstationOrder());
        endTimeWrapper.eq("turn", startTimeModel.getTurn());
        endTimeWrapper.eq("station", endModel.getId());
        TimeModel endTimeModel = timeMapper.selectOne(endTimeWrapper);
        result.endTime = endTimeModel.getTurnTime();

        return result;
    }

    public QueryDBModel queryToRelatedStation(QueryModel start, LocalTime startTime) throws Exception {

        QueryDBModel result = new QueryDBModel();
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StationMapper stationMapper = sqlSession.getMapper(StationMapper.class);
        QueryWrapper<StationModel> startWrapper = new QueryWrapper<>();
        startWrapper.eq("line", start.line);
        startWrapper.eq("name", start.name);
        StationModel startModel = stationMapper.selectOne(startWrapper);
        System.out.println(startModel);
        result.startStation = startModel;

        QueryWrapper<StationModel> endWrapper = new QueryWrapper<>();
        endWrapper.eq("line", start.line);
        endWrapper.isNotNull("related");
        endWrapper.ne("name", start.name);
        StationModel endModel = stationMapper.selectOne(endWrapper);
        System.out.println(endModel);
        result.endStation = endModel;

        TimeMapper timeMapper = sqlSession.getMapper(TimeMapper.class);
        QueryWrapper<TimeModel> startTimeWrapper = new QueryWrapper<>();
        startTimeWrapper.ge("turn_time", startTime);
        startTimeWrapper.eq("station", startModel.getId());
        if (endModel.getPosition() > startModel.getPosition()) {
            // 查询顺序时刻表 1a
            startTimeWrapper.eq("station_order", start.line.toString() + "a");
        } else {
            // 查询倒序时刻表 1b
            startTimeWrapper.eq("station_order", start.line.toString() + "b");
        }
        TimeModel startTimeModel =timeMapper.selectOne(startTimeWrapper);
        if (startTimeModel == null) {
            throw new Exception("赶不上车了，凉凉");
        }
        result.startTime = startTimeModel.getTurnTime();

        QueryWrapper<TimeModel> endTimeWrapper = new QueryWrapper<>();
        endTimeWrapper.eq("station_order", startTimeModel.getstationOrder());
        endTimeWrapper.eq("turn", startTimeModel.getTurn());
        endTimeWrapper.eq("station", endModel.getId());
        TimeModel endTimeModel = timeMapper.selectOne(endTimeWrapper);
        result.endTime = endTimeModel.getTurnTime();

        return result;
    }

    private StationModel getRelatedStation(String name, Integer line) {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StationMapper stationMapper = sqlSession.getMapper(StationMapper.class);
        QueryWrapper<StationModel> startWrapper = new QueryWrapper<>();
        startWrapper.ne("line", line);
        startWrapper.eq("name", name);
        StationModel relatedStation = stationMapper.selectOne(startWrapper);
        return relatedStation;
    }

    public static void main(String[] args) {

        try {

            // 单程
//            test1();//成功
//            test4();//失败

            // 换乘一次
            test3();//成功
            test5();//失败

            // ------以下未测试，数据还没造

            // 换乘两次
//            test2();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void test1() throws Exception {
        QueryModel start = new QueryModel();
        start.name = "河口";
        start.line = 1;
        QueryModel end = new QueryModel();
        end.name = "姚家";
        end.line = 1;
        MetroService test1 = new MetroService();
        test1.search(start, end, LocalTime.of(5,0,0));
        test1.printResult();
    }

    public static void test2() throws Exception {
        QueryModel start = new QueryModel();
        start.name = "旅顺新港";
        start.line = 12;
        QueryModel end = new QueryModel();
        end.name = "海之韵";
        end.line = 2;
        MetroService test2 = new MetroService();
        test2.search(start, end, LocalTime.now());
        test2.printResult();
    }

    public static void test3() throws Exception {
        QueryModel start = new QueryModel();
        start.name = "机场";
        start.line = 2;
        QueryModel end = new QueryModel();
        end.name = "春柳";
        end.line = 1;
        MetroService test3 = new MetroService();
        test3.search(start, end, LocalTime.now());
        test3.printResult();
    }

    public static void test4() throws Exception {
        QueryModel start = new QueryModel();
        start.name = "河口";
        start.line = 1;
        QueryModel end = new QueryModel();
        end.name = "姚家";
        end.line = 1;
        MetroService test1 = new MetroService();
        test1.search(start, end, LocalTime.of(7,0,0));
        test1.printResult();
    }

    public static void test5() throws Exception {
        QueryModel start = new QueryModel();
        start.name = "机场";
        start.line = 2;
        QueryModel end = new QueryModel();
        end.name = "旅顺新港";
        end.line = 12;
        MetroService test1 = new MetroService();
        test1.search(start, end, LocalTime.of(7,0,0));
        test1.printResult();
    }
}
