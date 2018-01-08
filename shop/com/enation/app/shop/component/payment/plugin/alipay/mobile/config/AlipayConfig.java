package com.enation.app.shop.component.payment.plugin.alipay.mobile.config;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *版本：3.3
 *日期：2012-08-10
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
	
 *提示：如何获取安全校验码和合作身份者ID
 *1.用您的签约支付宝账号登录支付宝网站(www.alipay.com)
 *2.点击“商家服务”(https://b.alipay.com/order/myOrder.htm)
 *3.点击“查询合作者身份(PID)”、“查询安全校验码(Key)”

 *安全校验码查看时，输入支付密码后，页面呈灰色的现象，怎么办？
 *解决方法：
 *1、检查浏览器配置，不让浏览器做弹框屏蔽设置
 *2、更换浏览器或电脑，重新登录查询。
 */

public class AlipayConfig {
	
	//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	// 合作身份者ID，以2088开头由16位纯数字组成的字符串
	public static String partner = "2088521246745277";//sunny : 2088811103230900  quwei : 2088021233660663 wbl:2088521246745277 cxcar:2088121484601775
	
	// 收款支付宝账号，以2088开头由16位纯数字组成的字符串
	public static String seller_id = partner;
	
	// 交易安全检验码，由数字和字母组成的32位字符串
	// 如果签名方式设置为“MD5”时，请设置该参数
	public static String key = "sxck992teln3bhrfaki3lddfjkpvncdk";//sunny: q00g0n0y0xzxcuht56fuogvnb8dbw8mu  quwei : ydh740blf0ssc8urwxa5j1h1yczy8vlx wbl : sxck992teln3bhrfaki3lddfjkpvncdk cxcar:4reqv0w2e7p57x1uwzwnrpr2kcbk1f7z
	
    // 商户的私钥
    // 如果签名方式设置为“0001”时，请设置该参数
	public static String private_key = /*"MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKvxn24DKEi1c3E4" +
			"V1nql02Il7ib/ytRbq/DMQNeX+4lORZQXKkL7McfsGqK/TYBiyLFwiNMcDuBeXuA" +
			"nPLgpYEpxZIleTuwPjmQ44XIRt9qzQHLXI71E8T8EjSnERBsSdosz5v13H6trqfm" +
			"q/EwfcUYeBw9Qchu0gbJkhVQrCydAgMBAAECgYB9R7z5RFWqvILToNCMlFAPzxF1" +
			"EjyWzGuQpvDkWnQdKYPxzTXmeejYoS9CQekC25cMC/lLDNvNj6X7JZe1o5cSVqbx" +
			"JPmdROtBg6vhF0stXZasB/30ysA/Hwou9FlKqaXBFLjkPuYpoi2w5E2Jm/eZ/sdu" +
			"hZMn1P6SLzYc4J/6RQJBANLf8LA8mhxGs8e7K0DIRTuLqPj2ALO9YoNP2jvljkxZ" +
			"IjyCHrZB8Ek+EzXVfrSnuAfWsW8mFszN6qHjpgjLoTcCQQDQvPvMjhpo85ysQkrI" +
			"XOoodQZq40BvNmlWeniT3BaXXOhpXkJTQsfcqsPTc8laXukYUk0atb6CcS526CLe" +
			"qlrLAkBHcc/eJeogwmOOu/8/h5rNPdgV4WMDgpqp2pgZywCXJWvQDFXTciuy5PEl" +
			"QBwZoOlc3oRnIASBhxKDntlWCYepAkAqGZOnhBmrhUbaL1yp4KORmVi8Ai2iwJSq" +
			"TnU3TTJ5QNHXPp2d8WAiD4cDhNzdjzrwHtlSQ8mywWUfsl4mDrHVAkEAj1XF7f2q" +
			"wLLB9qsUJ7kN0oWHZlY/C7PYMmaKS57LFwzQMX47PxYSEvc1RsHBMPpT/a8IUn0e" +
			"tMz+gxu/lirL2A==";*/
			//"MIICXAIBAAKBgQCr8Z9uAyhItXNxOFdZ6pdNiJe4m/8rUW6vwzEDXl/uJTkWUFypC+zHH7Bqiv02AYsixcIjTHA7gXl7gJzy4KWBKcWSJXk7sD45kOOFyEbfas0By1yO9RPE/BI0pxEQbEnaLM+b9dx+ra6n5qvxMH3FGHgcPUHIbtIGyZIVUKwsnQIDAQABAoGAfUe8+URVqryC06DQjJRQD88RdRI8lsxrkKbw5Fp0HSmD8c015nno2KEvQkHpAtuXDAv5SwzbzY+l+yWXtaOXElam8ST5nUTrQYOr4RdLLV2WrAf99MrAPx8KLvRZSqmlwRS45D7mKaItsORNiZv3mf7HboWTJ9T+ki82HOCf+kUCQQDS3/CwPJocRrPHuytAyEU7i6j49gCzvWKDT9o75Y5MWSI8gh62QfBJPhM11X60p7gH1rFvJhbMzeqh46YIy6E3AkEA0Lz7zI4aaPOcrEJKyFzqKHUGauNAbzZpVnp4k9wWl1zoaV5CU0LH3KrD03PJWl7pGFJNGrW+gnEudugi3qpaywJAR3HP3iXqIMJjjrv/P4eazT3YFeFjA4KaqdqYGcsAlyVr0AxV03IrsuTxJUAcGaDpXN6EZyAEgYcSg57ZVgmHqQJAKhmTp4QZq4VG2i9cqeCjkZlYvAItosCUqk51N00yeUDR1z6dnfFgIg+HA4Tc3Y868B7ZUkPJssFlH7JeJg6x1QJBAI9Vxe39qsCywfarFCe5DdKFh2ZWPwuz2DJmikueyxcM0DF+Oz8WEhL3NUbBwTD6U/2vCFJ9HrTM/oMbv5Yqy9g=";
			/*"MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAOZR9XhtzAr4fO3i" +
			"r1k7wXcY3vusDgq8GuQOWhj3iHQYcTV2m35lKpu3LbXkFZHukVQNWB7KFkc+RtRJ" +
			"NuOlBbh8WybGfXw5v9XZrOvMb64POxAM2rQsRj+1VX5zpDFRK6LUJ0JXugqSGSiX" +
			"FPy0jYEXiA1YLw8shryEJhjmpU0jAgMBAAECgYAxpMzSLmhinPjglZHSHA0xTI0T" +
			"lhxYt/7b1TQaWZBx5arRKUWO0uOCONODdywnDlMI2O97g3eV15iQvU81diZrGsMA" +
			"qrj2X7UaiKP3vNK96AnzBbccyEQM2TN7gJTff1sR3lLUBeoINa10EVsb6jUxVWQo" +
			"EQBNcLU92t8kUl5TEQJBAPX/uwbK6eoUzwSMJZvvs2tio+NW/ZoWrkHMf9xQ2m2v" +
			"czIvarXO4Ls9xPt3GyAcW8cDBnMLLgULEzMfipbGjJUCQQDvrwyJoFtHYJxijDwC" +
			"IjMPR2LabMwTYouFSg9fGjcrfnvLfhac/P5sOsqHlolfEaXdoaulFpHLjYUkR+uw" +
			"RUzXAkEA4Dh66UMUmHblBpkTQqHmINAk33eP6d4YcQML2+2aWnWcAzczBYoOTryL" +
			"ikVS5R7UDH5WZ4/eH1yr4GI0eR96TQJBAL11hVeUphJ5z6QmbZjSJ8JAD2fQ8E+y" +
			"ac7mh9gJBOsghAKKnJ1jbklm/4jF+bhoXtAeZ2uLt5tLNp9DKo6feOkCQFYZ/cQD" +
			"cfjqPrmENgz1+0wXcqw9/eK8FPVv2LLkvMijYo7TWIf/Ae9mJBggi9+XdpiK6ry8" +
			"LsFCW3Lj4H058C8=";*/
			"MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBANYv5sT5HLxdaoN4" +
			"WiugvrCRDT+1XxNVqX92gS0ljSy8mRMGTWwFKi+a+BSgO4T7s3zAOmuZDdtH6RH8" +
			"ImPZoIL4ZkJjK2GoBUZDDi9zEzGBeuuoBCZQJ7pjt/FzY4hhrFqU8wctzHF/p8E8" +
			"eARsZpRnQtNR0BebN37EVJ2gd+ghAgMBAAECgYAe7hvtJew4Z2USRzb+ReBDry6y" +
			"nIZ2TFCFz58n3dxdMX8JeEeF6dkFtuNMVzdBJPfuc/P7xcZyfBf0OOzebnb3Lwsb" +
			"VZTs0kFJU4RTCq0mHumVMVUSy6cCps+IusZrSc4F/hI/Nxe6z9a0tvU0+l66dVnq" +
			"kycSfvv6xtr+tW0igQJBAPtVDqAaTqR9zrVcchVZC92X+iwSMUHSY36O/9MxQRjh" +
			"u9S3QkRbcjA1NUL0mq3UGI5Ds9ciEsvVjqLHMUPLugUCQQDaKj17NxhyKQUqNt00" +
			"LGJWo9wyvbGFFmnH4w81wEngBNZBM2csxR//jh9n0yhnQ14XM35XXLdkTmoaeLcC" +
			"HSRtAkB/jgqPLOrOxt7a4qEvvnKsMw6PwWfoXQuL1q11lODR7Pa0HZHvOW35k3eF" +
			"lLjQEf2obinevHdHpgnvCSMyojm1AkAKyfX6ZvIdiyvHN6le0CcY08eq9riVtVML" +
			"4I27AcRlgLe7x7f9fc4kTDn/X3DrEmjPyusflOU436vdnUSnpRtNAkBraaDRDNZV" +
			"q4snvKqM4uCIxjm/y6VCd0HuEWDoWXNb+KcmVL2y/97dF4CmHPHg9SYpDgyj7y7+" +
			"NXyr4Jibk+IQ";
			/*"MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJ8kJFI10+sxOiUZ" +
			"zgcutlbcN/BKtRWTQQ+e58vFzrvKFDKQUyX0wHsyykooSHmzL9d5Y6YxtthzfTbK" +
			"xFhqUah3h6qcLfTmdgiVl060kWL/g6srg/43+sx8Od3ad6buyD40TamSuULVLpEZ" +
			"iukEcArX2Rc0W30HZB1WDDsLGjxXAgMBAAECgYAcS0y54SAgfMwdtIzQ8hkxME4x" +
			"xtaTU28J2/LSZATxlmgo5UgjQ3TFcq5MqQTZvmYPJWH2NniP21iz2TE3lw+VYHev" +
			"Vz6430iQbu7EfA4OB4o7p6iuCp40Qq0GIbookJuQdI15pNCEJqslUkNOKMUd/rOi" +
			"zwWvmABBjIlukUdTwQJBANG6pQE5GiW+eVBFiwDaX+5AJRc/tq+TGqW/nxo/lKfA" +
			"+EH8GwRZkoH6pbl6KjU0gtmArTkxa5JI38gHI5jHsAkCQQDCQFPBfcWd+Ny0ru6d" +
			"P8xerpTjv6DWFlc2VBYtRiuG0+1FaZaXSk/vvGZ3hCABV1Q8LauSC/SB2kL8bAVf" +
			"COFfAkAdgjpXKragGuWswqwlKZSXzBSr6b4FhBgavBRhuBJta/xCJHuUqBmyChcp" +
			"OqQXqlTCz5UP2AE/qts5pK+AfpApAkEAwCQS9MxsgbKc1RhHrEFj7K+xVInDFKCq" +
			"iXGpMgU7nrTX+JXVwDZgVVk83bFSWyE/j/r4v5CVvS/gnIBY28UT7wJAeCxIkbvz" +
			"Cr4hA4/2bS6O60a0usLe4ourNpb2pM3SK8MAB4eEIHt8EmWM74s+XhcUaLLUFccb" +
			"9QeY7iBuHRW5vA==";*/

			
    // 支付宝的公钥
    // 如果签名方式设置为“0001”时，请设置该参数
	public static String ali_public_key = //"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCfJCRSNdPrMTolGc4HLrZW3DfwSrUVk0EPnufLxc67yhQykFMl9MB7MspKKEh5sy/XeWOmMbbYc302ysRYalGod4eqnC305nYIlZdOtJFi/4OrK4P+N/rMfDnd2nem7sg+NE2pkrlC1S6RGYrpBHAK19kXNFt9B2QdVgw7Cxo8VwIDAQAB"; 
			"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
	                                       

	//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
	

	// 调试用，创建TXT日志文件夹路径
	public static String log_path = "D:\\";

	// 字符编码格式 目前支持  utf-8
	public static String input_charset = "utf-8";
	
	// 签名方式，选择项：0001(RSA)、MD5
	public static String sign_type = "RSA";
	// 无线的产品中，签名方式为rsa时，sign_type需赋值为0001而不是RSA

}
