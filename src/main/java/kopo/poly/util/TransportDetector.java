package kopo.poly.util;

import java.util.*;

/**
 * 교통수단 판별 엔진
 * - FeatureWindow: GPS 포인트 누적 후 특징 추출
 * - ModeClassifier: 걷기, 자전거, 승용차, 버스, 지하철 판별
 * - ModeSmoother: 튀는 전환 보정
 */
public class TransportDetector {

    public static class Result {
        public final String modeRaw;
        public final String mode;
        public final double confidence;
        public final Map<String, Double> features;

        public Result(String modeRaw, String mode, double conf, Map<String, Double> feats) {
            this.modeRaw = modeRaw;
            this.mode = mode;
            this.confidence = conf;
            this.features = feats;
        }
    }

    /* -------------------- 유틸 -------------------- */
    static double toRad(double d) { return d * Math.PI / 180.0; }
    static double haversineMeters(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371000.0;
        double dlat = toRad(lat2 - lat1);
        double dlon = toRad(lon2 - lon1);
        double a = Math.sin(dlat/2)*Math.sin(dlat/2)
                + Math.cos(toRad(lat1))*Math.cos(toRad(lat2))*Math.sin(dlon/2)*Math.sin(dlon/2);
        return 2*R*Math.asin(Math.sqrt(a));
    }
    static double angDiffDeg(double a, double b) {
        double d = (b - a + 180.0) % 360.0 - 180.0;
        return Math.abs(d);
    }
    static class Ewma {
        final double alpha; Double v = null;
        Ewma(double a) { this.alpha = a; }
        double update(double x) { v = (v==null)? x : (alpha * x + (1-alpha) * v); return v; }
    }

    /* -------------------- Feature Window -------------------- */
    static class WindowPoint {
        final double t, lat, lon, speed, bearing;
        final Double acc;
        final boolean valid;
        WindowPoint(double t, double lat, double lon, double speed, double bearing, Double acc, boolean valid) {
            this.t=t; this.lat=lat; this.lon=lon; this.speed=speed; this.bearing=bearing; this.acc=acc; this.valid=valid;
        }
    }

    static class FeatureWindow {
        final double winSec, strideSec;
        final Deque<WindowPoint> win = new ArrayDeque<>();
        Double lastEmit = null;
        Double dropStart = null;
        final Deque<Double> dropDurations = new ArrayDeque<>();
        final Ewma spdEwma = new Ewma(0.3);
        Double prevSpeed = null; Double prevBearing = null;
        double prevT, prevLat, prevLon;

        FeatureWindow(double winSec, double strideSec) {
            this.winSec = winSec; this.strideSec = strideSec;
        }

        boolean addPoint(double t, double lat, double lon, Double acc, Double speedIn, Double bearingIn) {
            boolean valid = (acc == null || acc <= 50.0);
            double speedEst, bearingEst;

            if(!win.isEmpty()){
                double dt = Math.max(1e-3, t - prevT);
                double dist = haversineMeters(prevLat, prevLon, lat, lon);
                double speedGeo = dist / dt;
                double speedRaw = (speedIn!=null && speedIn>=0 && speedIn<=80) ? speedIn : speedGeo;
                speedEst = 0.5*speedRaw + 0.5*speedGeo;

                double y = Math.sin(toRad(lon - prevLon)) * Math.cos(toRad(lat));
                double x = Math.cos(toRad(prevLat)) * Math.sin(toRad(lat)) -
                           Math.sin(toRad(prevLat)) * Math.cos(toRad(lat)) * Math.cos(toRad(lon - prevLon));
                double br = (Math.toDegrees(Math.atan2(y, x)) + 360.0) % 360.0;
                bearingEst = (prevBearing!=null)? (prevBearing + ((br - prevBearing + 180)%360 -180))%360 : br;

                if(speedEst > 90) valid=false;
            } else {
                speedEst = (speedIn!=null? speedIn:0.0);
                bearingEst = (bearingIn!=null? bearingIn:0.0);
            }

            double spdSm = spdEwma.update(speedEst);
            Double accEst = (prevSpeed!=null)? (spdSm - prevSpeed)/Math.max(1e-3, (t-prevT)) : null;

            win.addLast(new WindowPoint(t, lat, lon, spdSm, bearingEst, accEst, valid));
            while(!win.isEmpty() && (t - win.peekFirst().t > winSec)) win.removeFirst();

            if(!valid) { if(dropStart==null) dropStart=t; }
            else { if(dropStart!=null){ dropDurations.addLast(t-dropStart); if(dropDurations.size()>20) dropDurations.removeFirst(); dropStart=null; } }

            prevT=t; prevLat=lat; prevLon=lon; prevSpeed=spdSm; prevBearing=bearingEst;

            if(lastEmit==null || (t-lastEmit>=strideSec)){ lastEmit=t; return true; }
            return false;
        }

        Map<String, Double> compute() {
            if(win.size()<3) return null;
            List<Double> speeds=new ArrayList<>(), accs=new ArrayList<>(), bears=new ArrayList<>();
            for(WindowPoint w: win){ if(w.valid){ speeds.add(w.speed); if(w.acc!=null) accs.add(w.acc); bears.add(w.bearing);} }
            if(speeds.isEmpty()) return null;

            double vAvg = speeds.stream().mapToDouble(d->d).average().orElse(0);
            List<Double> sorted=new ArrayList<>(speeds); Collections.sort(sorted);
            double vP85 = sorted.get((int)Math.round((sorted.size()-1)*0.85));
            double stopRatio = speeds.stream().filter(s->s<1.0).count()/(double)speeds.size();

            double aVar=0.0; if(accs.size()>1){ double avg=accs.stream().mapToDouble(d->d).average().orElse(0); for(double a:accs) aVar+=(a-avg)*(a-avg); aVar/=(accs.size()-1); }
            double hRate=0.0; if(bears.size()>1){ for(int i=1;i<bears.size();i++) hRate+=angDiffDeg(bears.get(i-1), bears.get(i)); hRate/=bears.size()-1; }

            double dropMax=0.0; for(double d: dropDurations) if(d>dropMax) dropMax=d;

            Map<String, Double> f=new LinkedHashMap<>();
            f.put("v_avg",vAvg); f.put("v_p85",vP85); f.put("stop_ratio",stopRatio); f.put("a_var",aVar);
            f.put("heading_change_rate",hRate); f.put("gps_drop_max",dropMax);
            return f;
        }
    }

    /* -------------------- Classifier -------------------- */
    static class ModeClassifier {
        final Map<String,Double> C=new HashMap<>();
        ModeClassifier(){ C.put("walk_v_avg_max",2.0); C.put("walk_v_p85_max",2.5); C.put("walk_stop_min",0.2); C.put("walk_heading_min",15.0);
            C.put("bike_v_avg_min",2.0); C.put("bike_v_avg_max",7.0); C.put("bike_v_p85_max",9.0); C.put("bike_stop_min",0.05); C.put("bike_stop_max",0.25);
            C.put("car_v_avg_min",7.0); C.put("car_v_p85_min",10.0); C.put("car_stop_max",0.10); C.put("car_avar_min",1.0);
            C.put("bus_stop_events_min",2.0); C.put("bus_stop_cv_max",0.6); C.put("subway_drop_long",60.0); }

        Pair infer(Map<String,Double> f){
            double vAvg=f.get("v_avg"), vP85=f.get("v_p85"), stopRatio=f.get("stop_ratio"), aVar=f.get("a_var"), hRate=f.get("heading_change_rate"), dropLong=f.get("gps_drop_max");

            if(dropLong>=C.get("subway_drop_long") && vAvg<2.0) return new Pair("subway",0.7);
            if(vAvg<=C.get("walk_v_avg_max")&&vP85<=C.get("walk_v_p85_max")&&stopRatio>=C.get("walk_stop_min")&&hRate>=C.get("walk_heading_min")) return new Pair("walk",0.9);
            if(vAvg>=C.get("bike_v_avg_min")&&vAvg<=C.get("bike_v_avg_max")&&vP85<=C.get("bike_v_p85_max")&&stopRatio>=C.get("bike_stop_min")&&stopRatio<=C.get("bike_stop_max")) return new Pair("bike",0.8);
            if(vAvg>=C.get("car_v_avg_min")&&vP85>=C.get("car_v_p85_min")&&stopRatio<=C.get("car_stop_max")) return new Pair("car",(aVar>=C.get("car_avar_min"))?0.7:0.6);
            return new Pair("unknown",0.3);
        }
        static class Pair{ final String mode; final double conf; Pair(String m,double c){mode=m;conf=c;} }
    }

    /* -------------------- Smoother -------------------- */
    static class ModeSmoother {
        final Deque<String> hist; final int cap; String current=null;
        ModeSmoother(int persist){ cap=persist; hist=new ArrayDeque<>(persist);}
        ModeClassifier.Pair update(ModeClassifier.Pair r){ if(hist.size()>=cap) hist.removeFirst(); hist.addLast(r.mode);
            Map<String,Integer> counts=new HashMap<>(); for(String m:hist) counts.put(m,counts.getOrDefault(m,0)+1);
            String top=r.mode; int best=-1; for(var e:counts.entrySet()){ if(e.getValue()>best){best=e.getValue();top=e.getKey();}}
            if(hist.size()>=cap && (current==null||!current.equals(top))) current=top; return new ModeClassifier.Pair(current!=null?current:r.mode,r.conf);}
    }

    /* -------------------- Detector 본체 -------------------- */
    private final FeatureWindow fw;
    private final ModeClassifier clf = new ModeClassifier();
    private final ModeSmoother sm;

    public TransportDetector(double winSec, double strideSec){
        this.fw=new FeatureWindow(winSec,strideSec);
        this.sm=new ModeSmoother(Math.max(2,(int)(20.0/strideSec)));
    }

    public Result push(double t,double lat,double lon,Double acc,Double speed,Double bearing){
        boolean emit=fw.addPoint(t,lat,lon,acc,speed,bearing);
        if(!emit) return null;
        Map<String,Double> f=fw.compute(); if(f==null) return null;
        ModeClassifier.Pair raw=clf.infer(f); ModeClassifier.Pair stable=sm.update(raw);
        return new Result(raw.mode,stable.mode,stable.conf,f);
    }
}
