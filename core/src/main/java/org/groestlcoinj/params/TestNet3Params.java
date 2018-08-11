/*
 * Copyright 2013 Google Inc.
 * Copyright 2014 Andreas Schildbach
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.groestlcoinj.params;

import org.groestlcoinj.core.CoinDefinition;
import java.math.BigInteger;
import java.util.Date;

import org.groestlcoinj.core.Block;
import org.groestlcoinj.core.NetworkParameters;
import org.groestlcoinj.core.StoredBlock;
import org.groestlcoinj.core.Utils;
import org.groestlcoinj.core.VerificationException;
import org.groestlcoinj.store.BlockStore;
import org.groestlcoinj.store.BlockStoreException;

import static com.google.common.base.Preconditions.checkState;

/**
 * Parameters for the testnet, a separate public instance of Bitcoin that has relaxed rules suitable for development
 * and testing of applications and new Bitcoin versions.
 */
public class TestNet3Params extends AbstractBitcoinNetParams {
    public TestNet3Params() {
        super();
        id = ID_TESTNET;

        // Genesis hash is 000000000933ea01ad0ee984209779baaec3ced90fa3f408719526f8d77f4943

        packetMagic = 0x0b110907;
        interval = INTERVAL;
        targetTimespan = TARGET_TIMESPAN;
        maxTarget = Utils.decodeCompactBits(0x1E00FFFF);
        port = 17777;
        addressHeader = CoinDefinition.testnetAddressHeader;
        p2shHeader = CoinDefinition.testnetp2shHeader;
        acceptableAddressCodes = new int[] { addressHeader, p2shHeader };
        dumpedPrivateKeyHeader = 128 + CoinDefinition.testnetAddressHeader;
        genesisBlock.setTime(CoinDefinition.testnetGenesisBlockTime);
        genesisBlock.setDifficultyTarget(CoinDefinition.testnetGenesisBlockDifficultyTarget);
        genesisBlock.setNonce(CoinDefinition.testnetGenesisBlockNonce);
        genesisBlock.setVersion(3);
        spendableCoinbaseDepth = 100;

        subsidyDecreaseBlockCount = CoinDefinition.subsidyDecreaseBlockCount;
         String genesisHash = genesisBlock.getHashAsString();

        if(CoinDefinition.supportsTestNet)
            checkState(genesisHash.equals(CoinDefinition.testnetGenesisHash));
        alertSigningKey = Utils.HEX.decode(CoinDefinition.TESTNET_SATOSHI_KEY);

        dnsSeeds = CoinDefinition.testnetDnsSeeds;


        bip32HeaderPub = 0x043587CF;
        bip32HeaderPriv = 0x04358394;

        majorityEnforceBlockUpgrade = TestNet2Params.TESTNET_MAJORITY_ENFORCE_BLOCK_UPGRADE;
        majorityRejectBlockOutdated = TestNet2Params.TESTNET_MAJORITY_REJECT_BLOCK_OUTDATED;
        majorityWindow = TestNet2Params.TESTNET_MAJORITY_WINDOW;
    }

    private static TestNet3Params instance;
    public static synchronized TestNet3Params get() {
        if (instance == null) {
            instance = new TestNet3Params();
        }
        return instance;
    }

    @Override
    public String getPaymentProtocolId() {
        return PAYMENT_PROTOCOL_ID_TESTNET;
    }

    @Override
    public void checkDifficultyTransitions(final StoredBlock storedPrev, final Block nextBlock,
        final BlockStore blockStore) throws VerificationException, BlockStoreException {

        if (nextBlock.getTimeSeconds() > (storedPrev.getHeader().getTimeSeconds() + NetworkParameters.TARGET_SPACING*2)) {
            verifyDifficulty(nextBlock.getDifficultyTargetAsInteger(), storedPrev, nextBlock);
            return;
        }

        else if(storedPrev.getHeight() >= 99999)
            super.checkDifficultyTransitions(storedPrev, nextBlock, blockStore);
    }
}
