package protos;

import common.CommonData;

import java.util.UUID;

class Common {

    public static CommonData generateCommonData() {
        final UUID clientExchangeUUID = UUID.randomUUID();
        final UUID serverExchangeUUID = UUID.randomUUID();
        final int seqNum = 1; // must be > 0
        return new CommonData(seqNum, clientExchangeUUID, serverExchangeUUID);
    }

}
