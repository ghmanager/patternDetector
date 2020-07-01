# Pattern Detector for a Container-based Architecture
This is a design pattern detection tool to detect patterns in a container-based architecture. You can install it in different modes. The following application is also available at docker hub.

This tool can be executed in two modes. The first mode is the mock mode. This mode is the test and default mode and does not require a Kubernetes cluster. You can test the pattern detection algorithm by defining your graph to perform a pattern detection in the TestGraph.java class. If you want to test your principle connection to the Kubernetes API, you can click on rerun. Then, all services with their connected pods are displayed.

The second mode is the full mode. You can enable it by setting the MOCK variable in the Model.class to false. Then you require a working Kubernetes cluster. You can provide the config file through a config file, a url to the config file or get access with a token or a username/ password. The underlying technology is the official java client https://github.com/kubernetes-client/java.

Recommended installation of the code without running a cluster:
- Clone the repository 
- Install java 10 (Verwendung von oracle jdk 10.0.2)
- Install Eclipse (Version 2019-09 R (4.13.0) – newer/ older versions are possible too)
- Install the following eclipse plugins: e(fx)clipse 3.6. & Eclipse C/C++ IDE CDT (Version 9.9)

To run the tool without running/ adapting the code
- go to the target folder and run the detector-0.0.1-SNAPSHOT.jar (requires a java version – opt. java jdk 10.x.)

You can setup a minikube cluster to run the tool (warning: this can take a while and can sometimes cause problems with the tool)
- Install docker from https://docs.docker.com/get-docker/
- Install a local minikube cluster from https://kubernetes.io/docs/tasks/tools/install-minikube/
- Build your own examplary instance (add pods, services, ...)
- Start the cluster with minikube start
- Run the following commands for each pod to install:
-- operating system: Ubuntu Linux (other operating systems may require other commands)
cd path-to-folder-app1
eval $(minikube docker-env)
docker build -t app1:latest .
kubectl apply -f deployment1.yaml
