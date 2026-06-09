package com.seetha.taskqueue.worker.identity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.UUID;

@Component
public class WorkerIdentity {

    private static final Logger log = LoggerFactory.getLogger(WorkerIdentity.class);
    private static final String BASE62_ALPHABET= "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private final String workerId;

    public WorkerIdentity(){
        this.workerId = this.generateWorkerId();
    }

    public String getWorkerId(){
        return workerId;
    }

    private String generateWorkerId(){
        String hostName = "unknown-host";
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            hostName = localHost.getHostName();
        } catch (UnknownHostException e) {
            log.warn(e.getMessage());
        }
        return hostName + "-" + getShortUuid();
    }

    private static String getShortUuid(){
        UUID uuid = UUID.randomUUID();

        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());

        BigInteger number = new BigInteger(1, byteBuffer.array());
        StringBuilder sb = new StringBuilder();
        BigInteger base = BigInteger.valueOf(62);
        while (number.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] quotientAndRemainder = number.divideAndRemainder(base);
            sb.append(BASE62_ALPHABET.charAt(quotientAndRemainder[0].intValue()));
            number = quotientAndRemainder[0];
        }
        return sb.reverse().toString();
    }
}
