package com.kevin.wallet.btc;

import com.kevin.wallet.btc.core.TransactionService;
import com.kevin.wallet.btc.model.BasicTxOutput;
import com.kevin.wallet.btc.model.MultiSignTxReq;
import com.kevin.wallet.btc.model.UnSpentUtxo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MainTest {

    public static void main(String[] args) {
        TransactionService transactionService = new TransactionService();

        List<UnSpentUtxo> unSpentUtxos = new ArrayList<UnSpentUtxo>();
        UnSpentUtxo unSpentUtxo = new UnSpentUtxo();
        unSpentUtxo.setAddress("2Mziu12PxtGoaRmGp6MtCWWshrsxeLYWaDs");
        unSpentUtxo.setHash("fed695bf5e2c15286956a7bd3464c5beb97ef064e1f9406eba189ea844733e7c");
        unSpentUtxo.setValue(new BigDecimal("0.01220507").longValue());
        unSpentUtxo.setTxN(1);
        unSpentUtxo.setScript("a9145204ad7c5fa5a2491cd91c332e28c87221194ca087");
        unSpentUtxos.add(unSpentUtxo);

        List<BasicTxOutput > outputs = new ArrayList<BasicTxOutput>();
        BasicTxOutput basicTxOutput = new BasicTxOutput();
        basicTxOutput.setAddress("n2cWhs5sbWFCwzuuWWsVM9ubPwykGtX75T");
        basicTxOutput.setAmount(new BigDecimal("0.01"));
        outputs.add(basicTxOutput);


        ;

        MultiSignTxReq req = new MultiSignTxReq();
        req.setRawTransaction(transactionService.createTransaction(unSpentUtxos, outputs));
        req.setRedeemScript("5221021ae8964b8529dc3e52955f2cabd967e08c52008dbcca8e054143b668f3998f4a210306be609ef37366ab0f3dd4096ac23a6ee4d561fc469fa60003f799b0121ad1072102199f3d89fa00e6f55dd6ecdd911457d7264415914957db124d53bf0064963f3853ae");
        String firstTx = transactionService.signFirstTime(req);
        System.out.println("firstTx -> \n" + firstTx);

        String rawTx = transactionService.signSecondTime(firstTx);
        System.out.println("rawTx -> \n" + rawTx);

    }


}
