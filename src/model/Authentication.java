package model;

import java.io.IOException;

import controller.InputType;
import io.kubernetes.client.openapi.ApiException;

/**
 * This class authenticates a user to the cluster
 * @author Alexis T. Bernhard
 *
 */
class Authentication {

	/**
	 * Authenticates the user to the cluster
	 * @param client the kubernetes client to get access to kubernetes
	 * @param authmethod the desired method to authenticate the client to the kubernetes cluster
	 * @param authdata the inserted data for the authentication process
	 * @throws IOException thrown if no authentication method and data is inserted.
	 * @throws ApiException thrown if the list call to the kubernetes client fails.
	 */
	void authenticate(KubClient client, InputType authmethod, String[] authdata) throws IOException, ApiException {

		client.setAuthenticationData(authdata);
		client.setAuthmethod(authmethod);
		client.start();
	}
}
