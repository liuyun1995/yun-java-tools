package com.liuyun.github.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class GeoUtils {

    private static double EARTH_RADIUS = 6378.137;

    /**
     * 计算经纬度的距离
     * @param lng1
     * @param lat1
     * @param lng2
     * @param lat2
     * @return
     */
    public static double getDistance(double lng1, double lat1, double lng2, double lat2) {
        double radLat1 = getRadian(lat1);
        double radLat2 = getRadian(lat2);
        double a = radLat1 - radLat2;
        double b = getRadian(lng1) - getRadian(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1)
                * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        return s * 1000;
    }

    /**
     * 获取经纬度的范围
     * @param lng
     * @param lat
     * @param distance
     * @return
     */
    public static SquareScope squareScope(double lng, double lat, double distance){
        double dlng = 2 * Math.asin(Math.sin(distance / (2 * EARTH_RADIUS)) / Math.cos(lat * Math.PI / 180));
        dlng = dlng * 180 / Math.PI;
        double dlat = distance / EARTH_RADIUS;
        dlat = dlat * 180 / Math.PI;
        double minlng = lng - dlng;
        double maxlng = lng + dlng;
        double minlat = lat - dlat;
        double maxlat = lat + dlat;
        return new SquareScope(minlng, maxlng, minlat, maxlat);
    }

    private static double getRadian(double degree) {
        return degree * Math.PI / 180.0;
    }

    @Data
    @AllArgsConstructor
    public static class SquareScope{
        /** 最小经度 */
        private double minLng;
        /** 最大经度 */
        private double maxLng;
        /** 最小纬度 */
        private double minLat;
        /** 最大纬度 */
        private double maxLat;
    }

}