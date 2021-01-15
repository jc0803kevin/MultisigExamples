package com.kevin.wallet;

import com.google.common.collect.ImmutableList;
import com.kevin.wallet.btc.core.TransactionService;
import com.kevin.wallet.btc.model.BasicTxOutput;
import com.kevin.wallet.btc.model.UnSpentUtxo;
import com.kevin.wallet.btc.util.HexUtils;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class TestMultiSig {

    private static final NetworkParameters TESTNET = TestNet3Params.get();


    public static void main(String[] args) {
        Script redeemScript = new Script(HexUtils.hexStringToByteArray("5221021ae8964b8529dc3e52955f2cabd967e08c52008dbcca8e054143b668f3998f4a210306be609ef37366ab0f3dd4096ac23a6ee4d561fc469fa60003f799b0121ad1072102199f3d89fa00e6f55dd6ecdd911457d7264415914957db124d53bf0064963f3853ae"));
        ECKey key1 = ECKey.fromPrivate(new BigInteger("64102401986961187973900162212679081334328198710146539384491794427145725009072"));
        ECKey key2 = ECKey.fromPrivate(new BigInteger("68123968917867656952640885027449260190826636504009580537802764798766700329220"));

        TransactionService transactionService = new TransactionService();

        List<UnSpentUtxo> unSpentUtxos = new ArrayList<UnSpentUtxo>();
        UnSpentUtxo unSpentUtxo = new UnSpentUtxo();
        unSpentUtxo.setAddress("2Mziu12PxtGoaRmGp6MtCWWshrsxeLYWaDs");
        unSpentUtxo.setHash("fed695bf5e2c15286956a7bd3464c5beb97ef064e1f9406eba189ea844733e7c");
        unSpentUtxo.setValue(new BigDecimal("0.01220507").longValue());
        unSpentUtxo.setTxN(1);
        unSpentUtxo.setScript("a9145204ad7c5fa5a2491cd91c332e28c87221194ca087");
        unSpentUtxos.add(unSpentUtxo);

        List<BasicTxOutput> outputs = new ArrayList<BasicTxOutput>();
        BasicTxOutput basicTxOutput = new BasicTxOutput();
        basicTxOutput.setAddress("n2cWhs5sbWFCwzuuWWsVM9ubPwykGtX75T");
        basicTxOutput.setAmount(new BigDecimal("0.01"));
        outputs.add(basicTxOutput);

        byte[] txByte = transactionService.createTransaction(unSpentUtxos, outputs);

        Transaction spendTx = new Transaction(TESTNET, txByte);


        Sha256Hash sighash = spendTx.hashForSignature(0, redeemScript, Transaction.SigHash.ALL, false);
        ECKey.ECDSASignature party1Signature = key1.sign(sighash);
        ECKey.ECDSASignature party2Signature = key2.sign(sighash);
        TransactionSignature party1TransactionSignature = new TransactionSignature(party1Signature, Transaction.SigHash.ALL, false);
        TransactionSignature party2TransactionSignature = new TransactionSignature(party2Signature, Transaction.SigHash.ALL, false);

        Script inputScript = ScriptBuilder.createP2SHMultiSigInputScript(ImmutableList.of(party1TransactionSignature, party2TransactionSignature), redeemScript);
        spendTx.getInput(0).setScriptSig(inputScript);

        System.out.println(HexUtils.byteArrayToHex(spendTx.bitcoinSerialize()));
    }

}
