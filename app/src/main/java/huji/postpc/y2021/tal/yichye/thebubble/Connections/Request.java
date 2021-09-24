package huji.postpc.y2021.tal.yichye.thebubble.Connections;

import java.io.Serializable;

public class Request implements Comparable<Request>, Serializable {

    boolean inRequest;
    long timeStamp;
    String reqUserId;
    public Request(){}


    public Request(String id, boolean requestIsIncoming){
        inRequest = requestIsIncoming;
        timeStamp = System.currentTimeMillis();
        reqUserId = id;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getReqUserId() {
        return reqUserId;
    }

    public boolean isInRequest() {
        return inRequest;
    }


    @Override
    public int compareTo(Request o) {
        if (inRequest == o.inRequest)
        {
            return timeStamp < o.timeStamp? 1:-1;
        }
        return inRequest ? -1: 1;
    }
}

