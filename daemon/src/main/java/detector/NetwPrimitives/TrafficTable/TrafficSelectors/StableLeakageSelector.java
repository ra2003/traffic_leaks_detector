package detector.NetwPrimitives.TrafficTable.TrafficSelectors;

import detector.NetwPrimitives.IPv4Address;
import detector.NetwPrimitives.Port;
import detector.NetwPrimitives.TrafficFlow;
import detector.OsProcessesPrimitives.NetProcess;

/**
 * This class describes the next selection rule:
 * "more than 32 kB and in more than 8 seconds"
 */
public class StableLeakageSelector implements TrafficSelector
{
    private int allowedLeakSize;
    private int allowedActivitySec;
    private int allowedInactivitySec;


    public StableLeakageSelector(int maxTrafficBytes, int minActivitySec, int maxInactivitySec)
    {
        this.allowedLeakSize = maxTrafficBytes;
        this.allowedActivitySec = minActivitySec;
        this.allowedInactivitySec = maxInactivitySec;
    }


    @Override
    public boolean select(IPv4Address dstIp, TrafficFlow trafficFlow)
    {
        return isTrafficActual(trafficFlow) && isOverflowed(trafficFlow);
    }


    @Override
    public boolean select(Port srcPort, TrafficFlow trafficFlow)
    {
        return isTrafficActual(trafficFlow) && isOverflowed(trafficFlow);
    }


    @Override
    public boolean select(NetProcess process, TrafficFlow trafficFlow)
    {
        boolean isDominantDetected =
                trafficFlow.getDominantDstAddr() != null ||
                        trafficFlow.getDominantSrcPort() != null;

        return isTrafficActual(trafficFlow) &&
                isOverflowed(trafficFlow) &&
                isDominantDetected;
    }


    private boolean isOverflowed(TrafficFlow trafficFlow)
    {
        boolean isSuspiciousTrafficSize =
                trafficFlow.getBytes() >= allowedLeakSize;

        return  isSuspiciousTrafficSize;
    }


    private boolean isTrafficActual(TrafficFlow trafficFlow)
    {
        boolean lifeTimeAppropriate =
                trafficFlow.getActivityTimeSec() >= allowedActivitySec;
        boolean wasActiveNotLongAgo =
                trafficFlow.getInactivityTimeSec() < allowedInactivitySec;

        return lifeTimeAppropriate && wasActiveNotLongAgo;
    }

}
