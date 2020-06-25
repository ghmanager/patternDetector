# Pattern Detector
This is a design pattern detection tool to detect patterns in a container-based architecture. You can install it in different modes. This is described in german. The following application is also available at docker hub. This tool should only be executed with a Kubernetes cluster.

Empfohlene Entwicklungsumgebung zur Codebearbeitung ohne Ausführung
- Installation von java 10 (Verwendung von oracle jdk 10.0.2)
- Installation von Eclipse (Version 2019-09 R (4.13.0) – neuere/ ältere Versionen sind auch möglich)
- Installation von folgenden eclipse plugins: e(fx)clipse 3.6. & Eclipse C/C++ IDE CDT (Version 9.9)

Ausführung ohne Code ohne Cluster (kann mit der Codebearbeitung kombiniert werden)
- Herunterladen der detector.jar Datei
- Datei ausführen (benötigt eine java version – opt. java jdk 10.2.)

Anleitung zum Aufsetzen eines minikube-kubernetes clusters:
- Installation von docker unter https://docs.docker.com/get-docker/
- Installation eines lokalen minikube clusters nach der Anleitung unter https://kubernetes.io/docs/tasks/tools/install-minikube/
- Aufsetzen einer Beispielinstanz
- Starten Sie das Cluster mit minikube start
- Führen Sie die folgende Befehlskette für jeden Ihrer pods aus, die installiert werden sollen
-- Betriebssystem(OS): Ubuntu Linux, Änderungen der Befehle bei anderen OS möglich
cd path-to-folder-app1
eval $(minikube docker-env)
docker build -t app1:latest .
kubectl apply -f deployment1.yaml
