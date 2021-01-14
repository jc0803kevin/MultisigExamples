
### 创建多签地址
```json
{
    "id": "1010790928380338176",
    "jsonrpc": "1.0",
    "method": "createmultisig",
    "params": [
        2,
        [
            "021ae8964b8529dc3e52955f2cabd967e08c52008dbcca8e054143b668f3998f4a",
            "0306be609ef37366ab0f3dd4096ac23a6ee4d561fc469fa60003f799b0121ad107",
            "02199f3d89fa00e6f55dd6ecdd911457d7264415914957db124d53bf0064963f38"
        ]
    ]
}
```


```json
{
    "result": {
        "address": "2Mziu12PxtGoaRmGp6MtCWWshrsxeLYWaDs",
        "redeemScript": "5221021ae8964b8529dc3e52955f2cabd967e08c52008dbcca8e054143b668f3998f4a210306be609ef37366ab0f3dd4096ac23a6ee4d561fc469fa60003f799b0121ad1072102199f3d89fa00e6f55dd6ecdd911457d7264415914957db124d53bf0064963f3853ae",
        "descriptor": "sh(multi(2,021ae8964b8529dc3e52955f2cabd967e08c52008dbcca8e054143b668f3998f4a,0306be609ef37366ab0f3dd4096ac23a6ee4d561fc469fa60003f799b0121ad107,02199f3d89fa00e6f55dd6ecdd911457d7264415914957db124d53bf0064963f38))#43qr2daw"
    },
    "error": null,
    "id": "1010790928380338176"
}
```