package idv.const_x.utils;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class RSAUtils {
	public static final String CHARSET = "UTF-8";
	public static final String RSA_ALGORITHM = "RSA";
	public static final String RSA_ALGORITHM_SIGN = "SHA256WithRSA";

	public static Map<String, String> createKeys(int keySize) {
		// 为RSA算法创建一个KeyPairGenerator对象
		KeyPairGenerator kpg;
		try {
			kpg = KeyPairGenerator.getInstance(RSA_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException("No such algorithm-->[" + RSA_ALGORITHM + "]");
		}

		// 初始化KeyPairGenerator对象,不要被initialize()源码表面上欺骗,其实这里声明的size是生效的
		kpg.initialize(keySize);
		// 生成密匙对
		KeyPair keyPair = kpg.generateKeyPair();
		// 得到公钥
		Key publicKey = keyPair.getPublic();
		String publicKeyStr = Base64.getEncoder().encodeToString(publicKey.getEncoded());
		// 得到私钥
		Key privateKey = keyPair.getPrivate();
		String privateKeyStr = Base64.getEncoder().encodeToString(privateKey.getEncoded());
		Map<String, String> keyPairMap = new HashMap<String, String>();
		keyPairMap.put("publicKey", publicKeyStr);
		keyPairMap.put("privateKey", privateKeyStr);

		return keyPairMap;
	}

	public static String publicEncrypt(String data, String publicKey) {
		try {
			Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
			KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
			byte[] keyBytes = Base64.getDecoder().decode(publicKey.getBytes());
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
			RSAPublicKey publicK = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
			cipher.init(Cipher.ENCRYPT_MODE, publicK);
			return Base64.getEncoder().encodeToString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET),
					publicK.getModulus().bitLength()));
		} catch (Exception e) {
			throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
		}
	}

	public static String privateDecrypt(String data, String privateKey) {
		try {
			Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
			KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
			byte[] keyBytes = Base64.getDecoder().decode(privateKey.getBytes());
			PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
			RSAPrivateKey privateK = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
			cipher.init(Cipher.DECRYPT_MODE, privateK);
			return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.getDecoder().decode(data),
					privateK.getModulus().bitLength()), CHARSET);
		} catch (Exception e) {
			throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
		}
	}

	public static String privateEncrypt(String data, String privateKey) {
		try {
			Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
			KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
			byte[] keyBytes = Base64.getDecoder().decode(privateKey.getBytes());
			PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
			RSAPrivateKey privateK = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
			cipher.init(Cipher.ENCRYPT_MODE, privateK);
			return Base64.getEncoder().encodeToString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET),
					privateK.getModulus().bitLength()));
		} catch (Exception e) {
			throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
		}
	}

	public static String publicDecrypt(String data, String publicKey) {
		try {
			Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
			KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
			byte[] keyBytes = Base64.getDecoder().decode(publicKey.getBytes());
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
			RSAPublicKey publicK = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
			cipher.init(Cipher.DECRYPT_MODE, publicK);
			return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.getDecoder().decode(data),
					publicK.getModulus().bitLength()), CHARSET);
		} catch (Exception e) {
			throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
		}
	}

	public static String sign(String data, String privateKey) {
		try {
			// sign
			Signature signature = Signature.getInstance(RSA_ALGORITHM_SIGN);
			KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
			byte[] keyBytes = Base64.getDecoder().decode(privateKey.getBytes());
			PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
			RSAPrivateKey privateK = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
			signature.initSign(privateK);
			signature.update(data.getBytes(CHARSET));
			return Base64.getEncoder().encodeToString(signature.sign());
		} catch (Exception e) {
			throw new RuntimeException("签名字符串[" + data + "]时遇到异常", e);
		}
	}

	public static boolean verify(String data, String sign, String publicKey) {
		try {
			Signature signature = Signature.getInstance(RSA_ALGORITHM_SIGN);
			KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
			byte[] keyBytes = Base64.getDecoder().decode(publicKey.getBytes());
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
			RSAPublicKey publicK = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
			signature.initVerify(publicK);
			signature.update(data.getBytes(CHARSET));
			return signature.verify(Base64.getDecoder().decode(sign));
		} catch (Exception e) {
			throw new RuntimeException("验签字符串[" + data + "]时遇到异常", e);
		}
	}

	private  static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize) {
		int maxBlock = 0;
		if (opmode == Cipher.DECRYPT_MODE) {
			maxBlock = keySize / 8;
		} else {
			maxBlock = keySize / 8 - 11;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] buff;
		int i = 0;
		try {
			while (datas.length > offSet) {
				if (datas.length - offSet > maxBlock) {
					buff = cipher.doFinal(datas, offSet, maxBlock);
				} else {
					buff = cipher.doFinal(datas, offSet, datas.length - offSet);
				}
				out.write(buff, 0, buff.length);
				i++;
				offSet = i * maxBlock;
			}
			byte[] resultDatas = out.toByteArray();
			out.close();
			return resultDatas;
		} catch (Exception e) {
			throw new RuntimeException("加解密阀值为[" + maxBlock + "]的数据时发生异常", e);
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		 Map<String, String> keys = RSAUtils.createKeys(1024);
		 String  publicKey = keys.get("publicKey");
		 String  privateKey= keys.get("privateKey");

		 System.out.println("公钥：" + publicKey);
		 System.out.println("私钥：" + privateKey);
		 
		 String pain = "12323332222";
		 System.out.println("明文：" + pain);
		 
		 System.out.println("私钥签名,公钥验证,---------------------------");
		 String sign = RSAUtils.sign(pain, privateKey);
		 System.out.println("签名：" + sign);
		 System.out.println("验签：" +  RSAUtils.verify(pain, sign, publicKey));
		 
		 System.out.println("私钥加密,公钥解密---------------------------");
		 
		 String encrypted = RSAUtils.privateEncrypt(pain, privateKey);
		 System.out.println("加密： " + encrypted);
		 String decrypted = RSAUtils.publicDecrypt(encrypted, publicKey);
		 System.out.println("解密： " + decrypted);
//		 
//         System.out.println("公钥加密,私钥解密---------------------------");
//		 
//		 encrypted = RSAUtils.publicEncrypt(pain, privateKey);
//		 System.out.println("加密： " + encrypted);
//		 decrypted = RSAUtils.privateDecrypt(encrypted, publicKey);
//		 System.out.println("解密： " + decrypted);
//		 

		 
		 
	}
}
