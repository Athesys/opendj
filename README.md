# OpenDJ

## Build Environment

### Linux
* **maven 3.3.9**
* **OpenJDK 1.8.0_162**

## Build Instruction

* **git clone --recursive git@github.com:Athesys/forgerock-parent.git -b release/2.0.6**
* **git clone git@github.com:Athesys/opendj.git -b release/4.0.0-SNAPSHOT**
* **cd opendj**
* **mvn clean install -f ../forgerock-parent/pom.xml**
* **mvn clean install**


