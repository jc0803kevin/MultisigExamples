package com.kevin.wallet;

import com.kevin.wallet.btc.model.BasicTxOutput;
import com.kevin.wallet.btc.model.UnSpentUtxo;
import com.kevin.wallet.btc.util.HexUtils;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;

public class TestMultiSign2 {

    private TestNet3Params params = TestNet3Params.get();
    private TransactionOutput multisigOutput;
    private Transaction redeemingMultisigTx1;
    private Transaction redeemingMultisigTx2;
    private TransactionInput redeemMultisigTxInput;
    private ECKey.ECDSASignature partyASignature;
    private ECKey.ECDSASignature partyBSignature;

    ECKey key1 = ECKey.fromPrivate(new BigInteger("64102401986961187973900162212679081334328198710146539384491794427145725009072"));
    ECKey key2 = ECKey.fromPrivate(new BigInteger("68123968917867656952640885027449260190826636504009580537802764798766700329220"));


    public static void main(String[] args) {
        TestMultiSign2 testMultiSign2 = new TestMultiSign2();

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


        testMultiSign2.createRawTransactionForMultisigRedeemingByA(unSpentUtxos, outputs);
        testMultiSign2.createRawTransactionForMultisigRedeemingByB(unSpentUtxos, outputs);


        testMultiSign2.createAndBroadcastMultisigRedeemingTx();

    }

    private void createAndBroadcastMultisigRedeemingTx(){
        TransactionSignature signatureA = new TransactionSignature(partyASignature, Transaction.SigHash.ALL, false);
        TransactionSignature signatureB = new TransactionSignature(partyBSignature, Transaction.SigHash.ALL, false);
        Script inputScript = ScriptBuilder.createMultiSigInputScript(signatureA, signatureB);
        System.out.println("redeeming Tx input script: " + inputScript);
        redeemMultisigTxInput.setScriptSig(inputScript);

        System.out.println("rawtx :   "+ HexUtils.byteArrayToHex(redeemingMultisigTx2.bitcoinSerialize()));
    }

    private void createRawTransactionForMultisigRedeemingByA(List<UnSpentUtxo> inputs, List<BasicTxOutput> outputs){
        redeemingMultisigTx1 = new Transaction(params);

        for (UnSpentUtxo unSpentUtxo : inputs) {
            Sha256Hash hash = Sha256Hash.wrap(unSpentUtxo.getHash());
            Address address = Address.fromBase58(params, unSpentUtxo.getAddress());
            Script script = ScriptBuilder.createOutputScript(address);

            redeemingMultisigTx1.addInput(hash, unSpentUtxo.getTxN(), script);
        }



        //接收
        for (BasicTxOutput output : outputs) {
            Address receiverAddress = new Address(params, output.getAddress());
            Coin charge = Coin.parseCoin(output.getAmount().toPlainString());
            Script outputScript = ScriptBuilder.createOutputScript(receiverAddress);
            redeemingMultisigTx1.addOutput(charge, outputScript);
        }

        multisigOutput = redeemingMultisigTx1.getOutput(0);

        //签名
        Script payingToMultisigTxoutScriptPubKey = multisigOutput.getScriptPubKey();
        System.out.println("payingToMultisigTxoutScriptPubKey: " + payingToMultisigTxoutScriptPubKey);
        //checkState(payingToMultisigTxoutScriptPubKey.isSentToMultiSig());
        Sha256Hash sighash = redeemingMultisigTx1.hashForSignature(0, payingToMultisigTxoutScriptPubKey, Transaction.SigHash.ALL, false);
        partyASignature = key1.sign(sighash);

    }

    public void createRawTransactionForMultisigRedeemingByB(List<UnSpentUtxo> inputs, List<BasicTxOutput> outputs){
        redeemingMultisigTx2 = new Transaction(params);
        for (UnSpentUtxo unSpentUtxo : inputs) {
            Sha256Hash hash = Sha256Hash.wrap(unSpentUtxo.getHash());
            Address address = Address.fromBase58(params, unSpentUtxo.getAddress());
            Script script = ScriptBuilder.createOutputScript(address);

            redeemingMultisigTx2.addInput(hash, unSpentUtxo.getTxN(), script);
        }

        //接收
        for (BasicTxOutput output : outputs) {
            Address receiverAddress = new Address(params, output.getAddress());
            Coin charge = Coin.parseCoin(output.getAmount().toPlainString());
            Script outputScript = ScriptBuilder.createOutputScript(receiverAddress);
            redeemingMultisigTx2.addOutput(charge, outputScript);
        }

        redeemMultisigTxInput = redeemingMultisigTx2.getInput(0);

        Script payingToMultisigTxoutScriptPubKey = multisigOutput.getScriptPubKey();
        Sha256Hash sighash = redeemingMultisigTx2.hashForSignature(0, payingToMultisigTxoutScriptPubKey, Transaction.SigHash.ALL, false);
        partyBSignature = key2.sign(sighash);
    }

}
