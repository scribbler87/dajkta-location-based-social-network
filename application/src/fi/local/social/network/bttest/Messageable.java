package fi.local.social.network.bttest;

import java.io.Serializable;

public interface Messageable extends Serializable {

	public String getMessage();
	@Override
	public String toString();
}
