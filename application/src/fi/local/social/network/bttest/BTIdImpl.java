package fi.local.social.network.bttest;

import java.util.Random;

public class BTIdImpl implements BTId {

	private String s;
	private Random random = new Random();

	public BTIdImpl(String s) {
		this.s = s;
	}

	public BTIdImpl() {
		this.s = "" + random.nextInt();
	}

	@Override
	public String getMessage() {
		return s;
	}

}
