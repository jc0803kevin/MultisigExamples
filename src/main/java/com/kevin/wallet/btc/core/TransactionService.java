package com.kevin.wallet.btc.core;

import com.kevin.wallet.btc.model.BasicTxOutput;
import com.kevin.wallet.btc.model.MultiSignTxReq;
import com.kevin.wallet.btc.model.UnSpentUtxo;
import com.kevin.wallet.btc.util.HexUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.script.ScriptChunk;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class TransactionService {

    static final NetworkParameters params = TestNet3Params.get();

    public  byte[] createTransaction(List<UnSpentUtxo> unSpentUtxos, List<BasicTxOutput> outputs){
        Transaction spendTx = new Transaction(params);

        for (UnSpentUtxo unSpentUtxo : unSpentUtxos) {
            Sha256Hash hash = Sha256Hash.wrap(unSpentUtxo.getHash());
            Address address = Address.fromBase58(params, unSpentUtxo.getAddress());

            Script script = new ScriptBuilder().createOutputScript(address);
            spendTx.addInput(hash, unSpentUtxo.getTxN(), script);
        }

        for (BasicTxOutput output : outputs) {
            Address receiverAddress = new Address(params, output.getAddress());
            Coin charge = Coin.parseCoin(output.getAmount().toPlainString());
            Script outputScript = ScriptBuilder.createOutputScript(receiverAddress);
            spendTx.addOutput(charge, outputScript);
        }

        return spendTx.bitcoinSerialize();
    }

    /**
    * @date 2021/1/14 17:37
    * @author kevin
    * @description 第一次签名
    */
    public String signFirstTime(MultiSignTxReq req){
        byte[] rawTx = req.getRawTransaction();
        ECKey eckey = ECKey.fromPrivate(new BigInteger("64102401986961187973900162212679081334328198710146539384491794427145725009072"));

        Transaction transaction = new Transaction(params, rawTx);
        List<TransactionInput> inputs = transaction.getInputs();
        int i = 0;

        // Use the redeem script we have saved somewhere to start building the transaction
        Script redeemScript = new Script(HexUtils.hexStringToByteArray(req.getRedeemScript()));

        for(TransactionInput in : inputs) {
            Sha256Hash hash = transaction.hashForSignature(i++, redeemScript, Transaction.SigHash.ALL, false);
            ECKey.ECDSASignature ecSig = eckey.sign(hash);
            TransactionSignature txSig = new TransactionSignature(ecSig, Transaction.SigHash.ALL, false);

            // Create p2sh multisig input script
            Script script = ScriptBuilder.createP2SHMultiSigInputScript(Arrays.asList(txSig), redeemScript);
            in.setScriptSig(script);

        }
        return HexUtils.byteArrayToHex( transaction.bitcoinSerialize());
    }


    /**
     * @date 2021/1/14 17:37
     * @author kevin
     * @description 第二次签名
     */
    public String signSecondTime(String transactionString){
// Take the hex string we got from the other part and convert it into a Transaction object
        Transaction spendTx = new Transaction(params, HexUtils.hexStringToByteArray(transactionString));

        // Get the input chunks
        Script inputScript = spendTx.getInput(0).getScriptSig();
        List<ScriptChunk> scriptChunks = inputScript.getChunks();

        // Create a list of all signatures. Start by extracting the existing ones from the list of script schunks.
        // The last signature in the script chunk list is the redeemScript
        List<TransactionSignature> signatureList = new ArrayList<TransactionSignature>();
        Iterator<ScriptChunk> iterator = scriptChunks.iterator();
        Script redeemScript = null;

        while (iterator.hasNext())
        {
            ScriptChunk chunk = iterator.next();

            if (iterator.hasNext() && chunk.opcode != 0)
            {
                TransactionSignature transactionSignarture = TransactionSignature.decodeFromBitcoin(chunk.data, false);
                signatureList.add(transactionSignarture);
            } else
            {
                redeemScript = new Script(chunk.data);
            }
        }

        // Create the sighash using the redeem script
        Sha256Hash sighash = spendTx.hashForSignature(0, redeemScript, Transaction.SigHash.ALL, false);
        ECKey.ECDSASignature secondSignature;

        // Take out the key and sign the signhash
        //ECKey key2 = createKeyFromSha256Passphrase("Super secret key 2");
//        ECKey key2 = ECKey.fromPrivate(new BigInteger("68123968917867656952640885027449260190826636504009580537802764798766700329220"));
        ECKey key2 = ECKey.fromPrivate(new BigInteger("64102401986961187973900162212679081334328198710146539384491794427145725009072"));
        secondSignature = key2.sign(sighash);

        // Add the second signature to the signature list
        TransactionSignature transactionSignarture = new TransactionSignature(secondSignature, Transaction.SigHash.ALL, false);
        signatureList.add(transactionSignarture);

        // Rebuild p2sh multisig input script
        inputScript = ScriptBuilder.createP2SHMultiSigInputScript(signatureList, redeemScript);
        spendTx.getInput(0).setScriptSig(inputScript);

        return HexUtils.byteArrayToHex(spendTx.bitcoinSerialize());
    }

}
