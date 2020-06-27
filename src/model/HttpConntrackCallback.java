package model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.kubernetes.client.openapi.ApiCallback;
import io.kubernetes.client.openapi.ApiException;

/**
 * This class takes care of the http response of the call to the server to get the conntrack connection data
 * @author Alexis T. Bernhard
 *
 */
public class HttpConntrackCallback implements ApiCallback<String> {
	
	/**
	 * Contains all connections as a map from node names to binded connections in a string.
	 */
	private Map<String, String> connections = new HashMap<>();

	/**
	 * Gets all connections as a map from node names to binded connections in a string.
	 * @return a map of connections
	 */
	public Map<String, String> getConnections() {
		return connections;
	}

	@Override
	public void onDownloadProgress(long arg0, long arg1, boolean arg2) {
		// no implementation required
	}

	@Override
	public void onFailure(ApiException e, int status, Map<String, List<String>> headers) {
		e.printStackTrace();
	}

	@Override
	public void onSuccess(String responseBody, int status, Map<String, List<String>> responseHeaders) {
		// status check already done, no check for status > 300 || status < 200 required
		
		if (responseBody == null) {
			System.out.println("Warning: Response is empty! " + status);
			return;
		}
		String[] nodeConnections = responseBody.split(";");
		for (int i = 0; i < nodeConnections.length; i++) {
			String[] nodeMap = nodeConnections[i].split("\\+");
			if (nodeMap.length < 2) {
				// TODO throw error
			} else {
				String nodeIP = nodeMap[0];
				System.out.println(nodeIP);
				StringBuilder destinationsBuilder = new StringBuilder();
				destinationsBuilder.append(nodeMap[1]);
				if (nodeMap.length > 2) {
					for (int k = 2; k < nodeMap.length; k++) {
						destinationsBuilder.append(nodeMap[k]);
					}
				}
				System.out.println(destinationsBuilder.toString());
				connections.put(nodeIP, destinationsBuilder.toString());
			}
		}
	}

	@Override
	public void onUploadProgress(long arg0, long arg1, boolean arg2) {
		// no implementation required
	}
}
