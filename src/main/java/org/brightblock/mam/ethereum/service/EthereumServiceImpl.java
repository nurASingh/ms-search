package org.brightblock.mam.ethereum.service;

import java.io.IOException;
import java.math.BigInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.brightblock.mam.conf.settings.EthereumSettings;
import org.brightblock.mam.ethereum.rest.ArtMarket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;

@Service
public class EthereumServiceImpl implements EthereumService {

	private static final Logger logger = LogManager.getLogger(EthereumServiceImpl.class);
	@Autowired private EthereumSettings ethereumSettings;
	@Autowired private Web3j web3;
	@Autowired private Credentials credentials;
	private ArtMarket contract;

	@Override
	public String getWeb3ClientVersion() throws IOException {
		Web3ClientVersion web3ClientVersion = web3.web3ClientVersion().send();
		return web3ClientVersion.getWeb3ClientVersion();
	}

	@Override
	public ArtMarket loadContract(Long gasLimit, Long gas) {
		BigInteger bgGas = BigInteger.valueOf(gas);
		BigInteger bgGasLimit = BigInteger.valueOf(gasLimit);
		contract = ArtMarket.load(ethereumSettings.getContractAddress(), web3, credentials, bgGas, bgGasLimit);
		logger.info("Loaded Contract: gas=" + gas + " gasLimit=" + gasLimit);
		return contract;
	}

	@Override
	public ArtMarket deployContract(Long gasLimit, Long gas) throws Exception {
		BigInteger bgGas = BigInteger.valueOf(gas);
		BigInteger bgGasLimit = BigInteger.valueOf(gasLimit);
		contract = ArtMarket.deploy(web3, credentials, bgGas, bgGasLimit).send();
		logger.info("Deployed Contract: gas=" + gas + " gasLimit=" + gasLimit);
		return contract;
	}

	@Override
	public BigInteger numbItems() {
		try {
			BigInteger result;
			RemoteCall<BigInteger> numbItems = contract.itemIndex();
			result = numbItems.send();
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
