package fi.local.social.network.bttest;

public interface Filter<T> {
	boolean filtersOut(T m);
}
