package io.github.alexmofer.projectx.business.others.retrofithelper;

import java.util.List;

/**
 * 测试数据
 * {"tips":"注意：当前API已经停用，新API没有次数限制，详情查看===>https://www.sojson.com/blog/305.html。","date":"20190129","message":"Success !","status":200,"city":"北京当前API已经停用，新API没有次数限制，详情查看：https://www.sojson.com/blog/305.html","data":{"shidu":"29%","pm25":86.0,"pm10":112.0,"quality":"轻度污染","wendu":"-5","ganmao":"儿童、老年人及心脏、呼吸系统疾病患者人群应减少长时间或高强度户外锻炼","yesterday":{"date":"28日星期一","sunrise":"07:28","high":"高温 6.0℃","low":"低温 -4.0℃","sunset":"17:27","aqi":33.0,"fx":"西南风","fl":"<3级","type":"晴","notice":"愿你拥有比阳光明媚的心情"},"forecast":[{"date":"29日星期二","sunrise":"07:27","high":"高温 7.0℃","low":"低温 -4.0℃","sunset":"17:28","aqi":114.0,"fx":"西南风","fl":"<3级","type":"多云","notice":"阴晴之间，谨防紫外线侵扰"},{"date":"30日星期三","sunrise":"07:26","high":"高温 6.0℃","low":"低温 -6.0℃","sunset":"17:30","aqi":56.0,"fx":"北风","fl":"3-4级","type":"多云","notice":"阴晴之间，谨防紫外线侵扰"},{"date":"31日星期四","sunrise":"07:25","high":"高温 2.0℃","low":"低温 -7.0℃","sunset":"17:31","aqi":56.0,"fx":"西南风","fl":"<3级","type":"多云","notice":"阴晴之间，谨防紫外线侵扰"},{"date":"01日星期五","sunrise":"07:25","high":"高温 6.0℃","low":"低温 -5.0℃","sunset":"17:32","aqi":162.0,"fx":"南风","fl":"<3级","type":"晴","notice":"愿你拥有比阳光明媚的心情"},{"date":"02日星期六","sunrise":"07:24","high":"高温 4.0℃","low":"低温 -5.0℃","sunset":"17:33","aqi":171.0,"fx":"东北风","fl":"<3级","type":"多云","notice":"阴晴之间，谨防紫外线侵扰"}]},"count":4}
 * Created by Alex on 2019/1/29.
 */
@SuppressWarnings("unused")
class TestBean {

    /**
     * tips : 注意：当前API已经停用，新API没有次数限制，详情查看===>https://www.sojson.com/blog/305.html。
     * date : 20190129
     * message : Success !
     * status : 200
     * city : 北京当前API已经停用，新API没有次数限制，详情查看：https://www.sojson.com/blog/305.html
     * data : {"shidu":"29%","pm25":86,"pm10":112,"quality":"轻度污染","wendu":"-5","ganmao":"儿童、老年人及心脏、呼吸系统疾病患者人群应减少长时间或高强度户外锻炼","yesterday":{"date":"28日星期一","sunrise":"07:28","high":"高温 6.0℃","low":"低温 -4.0℃","sunset":"17:27","aqi":33,"fx":"西南风","fl":"<3级","type":"晴","notice":"愿你拥有比阳光明媚的心情"},"forecast":[{"date":"29日星期二","sunrise":"07:27","high":"高温 7.0℃","low":"低温 -4.0℃","sunset":"17:28","aqi":114,"fx":"西南风","fl":"<3级","type":"多云","notice":"阴晴之间，谨防紫外线侵扰"},{"date":"30日星期三","sunrise":"07:26","high":"高温 6.0℃","low":"低温 -6.0℃","sunset":"17:30","aqi":56,"fx":"北风","fl":"3-4级","type":"多云","notice":"阴晴之间，谨防紫外线侵扰"},{"date":"31日星期四","sunrise":"07:25","high":"高温 2.0℃","low":"低温 -7.0℃","sunset":"17:31","aqi":56,"fx":"西南风","fl":"<3级","type":"多云","notice":"阴晴之间，谨防紫外线侵扰"},{"date":"01日星期五","sunrise":"07:25","high":"高温 6.0℃","low":"低温 -5.0℃","sunset":"17:32","aqi":162,"fx":"南风","fl":"<3级","type":"晴","notice":"愿你拥有比阳光明媚的心情"},{"date":"02日星期六","sunrise":"07:24","high":"高温 4.0℃","low":"低温 -5.0℃","sunset":"17:33","aqi":171,"fx":"东北风","fl":"<3级","type":"多云","notice":"阴晴之间，谨防紫外线侵扰"}]}
     * count : 4
     */

    private String tips;
    private String date;
    private String message;
    private int status;
    private String city;
    private DataBean data;
    private int count;

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public static class DataBean {
        /**
         * shidu : 29%
         * pm25 : 86.0
         * pm10 : 112.0
         * quality : 轻度污染
         * wendu : -5
         * ganmao : 儿童、老年人及心脏、呼吸系统疾病患者人群应减少长时间或高强度户外锻炼
         * yesterday : {"date":"28日星期一","sunrise":"07:28","high":"高温 6.0℃","low":"低温 -4.0℃","sunset":"17:27","aqi":33,"fx":"西南风","fl":"<3级","type":"晴","notice":"愿你拥有比阳光明媚的心情"}
         * forecast : [{"date":"29日星期二","sunrise":"07:27","high":"高温 7.0℃","low":"低温 -4.0℃","sunset":"17:28","aqi":114,"fx":"西南风","fl":"<3级","type":"多云","notice":"阴晴之间，谨防紫外线侵扰"},{"date":"30日星期三","sunrise":"07:26","high":"高温 6.0℃","low":"低温 -6.0℃","sunset":"17:30","aqi":56,"fx":"北风","fl":"3-4级","type":"多云","notice":"阴晴之间，谨防紫外线侵扰"},{"date":"31日星期四","sunrise":"07:25","high":"高温 2.0℃","low":"低温 -7.0℃","sunset":"17:31","aqi":56,"fx":"西南风","fl":"<3级","type":"多云","notice":"阴晴之间，谨防紫外线侵扰"},{"date":"01日星期五","sunrise":"07:25","high":"高温 6.0℃","low":"低温 -5.0℃","sunset":"17:32","aqi":162,"fx":"南风","fl":"<3级","type":"晴","notice":"愿你拥有比阳光明媚的心情"},{"date":"02日星期六","sunrise":"07:24","high":"高温 4.0℃","low":"低温 -5.0℃","sunset":"17:33","aqi":171,"fx":"东北风","fl":"<3级","type":"多云","notice":"阴晴之间，谨防紫外线侵扰"}]
         */

        private String shidu;
        private double pm25;
        private double pm10;
        private String quality;
        private String wendu;
        private String ganmao;
        private YesterdayBean yesterday;
        private List<ForecastBean> forecast;

        public String getShidu() {
            return shidu;
        }

        public void setShidu(String shidu) {
            this.shidu = shidu;
        }

        public double getPm25() {
            return pm25;
        }

        public void setPm25(double pm25) {
            this.pm25 = pm25;
        }

        public double getPm10() {
            return pm10;
        }

        public void setPm10(double pm10) {
            this.pm10 = pm10;
        }

        public String getQuality() {
            return quality;
        }

        public void setQuality(String quality) {
            this.quality = quality;
        }

        public String getWendu() {
            return wendu;
        }

        public void setWendu(String wendu) {
            this.wendu = wendu;
        }

        public String getGanmao() {
            return ganmao;
        }

        public void setGanmao(String ganmao) {
            this.ganmao = ganmao;
        }

        public YesterdayBean getYesterday() {
            return yesterday;
        }

        public void setYesterday(YesterdayBean yesterday) {
            this.yesterday = yesterday;
        }

        public List<ForecastBean> getForecast() {
            return forecast;
        }

        public void setForecast(List<ForecastBean> forecast) {
            this.forecast = forecast;
        }

        public static class YesterdayBean {
            /**
             * date : 28日星期一
             * sunrise : 07:28
             * high : 高温 6.0℃
             * low : 低温 -4.0℃
             * sunset : 17:27
             * aqi : 33.0
             * fx : 西南风
             * fl : <3级
             * type : 晴
             * notice : 愿你拥有比阳光明媚的心情
             */

            private String date;
            private String sunrise;
            private String high;
            private String low;
            private String sunset;
            private double aqi;
            private String fx;
            private String fl;
            private String type;
            private String notice;

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public String getSunrise() {
                return sunrise;
            }

            public void setSunrise(String sunrise) {
                this.sunrise = sunrise;
            }

            public String getHigh() {
                return high;
            }

            public void setHigh(String high) {
                this.high = high;
            }

            public String getLow() {
                return low;
            }

            public void setLow(String low) {
                this.low = low;
            }

            public String getSunset() {
                return sunset;
            }

            public void setSunset(String sunset) {
                this.sunset = sunset;
            }

            public double getAqi() {
                return aqi;
            }

            public void setAqi(double aqi) {
                this.aqi = aqi;
            }

            public String getFx() {
                return fx;
            }

            public void setFx(String fx) {
                this.fx = fx;
            }

            public String getFl() {
                return fl;
            }

            public void setFl(String fl) {
                this.fl = fl;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getNotice() {
                return notice;
            }

            public void setNotice(String notice) {
                this.notice = notice;
            }
        }

        public static class ForecastBean {
            /**
             * date : 29日星期二
             * sunrise : 07:27
             * high : 高温 7.0℃
             * low : 低温 -4.0℃
             * sunset : 17:28
             * aqi : 114.0
             * fx : 西南风
             * fl : <3级
             * type : 多云
             * notice : 阴晴之间，谨防紫外线侵扰
             */

            private String date;
            private String sunrise;
            private String high;
            private String low;
            private String sunset;
            private double aqi;
            private String fx;
            private String fl;
            private String type;
            private String notice;

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public String getSunrise() {
                return sunrise;
            }

            public void setSunrise(String sunrise) {
                this.sunrise = sunrise;
            }

            public String getHigh() {
                return high;
            }

            public void setHigh(String high) {
                this.high = high;
            }

            public String getLow() {
                return low;
            }

            public void setLow(String low) {
                this.low = low;
            }

            public String getSunset() {
                return sunset;
            }

            public void setSunset(String sunset) {
                this.sunset = sunset;
            }

            public double getAqi() {
                return aqi;
            }

            public void setAqi(double aqi) {
                this.aqi = aqi;
            }

            public String getFx() {
                return fx;
            }

            public void setFx(String fx) {
                this.fx = fx;
            }

            public String getFl() {
                return fl;
            }

            public void setFl(String fl) {
                this.fl = fl;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getNotice() {
                return notice;
            }

            public void setNotice(String notice) {
                this.notice = notice;
            }
        }
    }
}
