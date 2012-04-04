package fi.local.social.network.bttest;


public class BTMessageImpl implements BTMessage {

	private BTId id;
	private BTContent content;

	public BTMessageImpl(BTContent content) {
		this.id = new BTIdImpl();
		this.content = content;
	}

	public BTMessageImpl(String id, String content) {
		this.id = new BTIdImpl(id);
		this.content = new BTContentImpl(content);
	}

	public BTMessageImpl(BTId id, String content) {
		this.id = id;
		this.content = new BTContentImpl(content);
	}

	public BTMessageImpl(BTId key, BTContent content) {
		this.id = key;
		this.content = content;
	}

	@Override
	public BTId getId() {
		return id;
	}

	@Override
	public String getContent() {
		return content.getMessage();
	}

	@Override
	public String getMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append(id.getMessage());
		sb.append(BTActivity.KEY_VALUE_SEPARATOR);
		sb.append(content.getMessage());
		sb.append(BTActivity.ID_SEPARATOR);
		return sb.toString();
	}

}
